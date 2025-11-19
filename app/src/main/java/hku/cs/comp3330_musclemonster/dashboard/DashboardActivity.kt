package hku.cs.comp3330_musclemonster.dashboard

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import hku.cs.comp3330_musclemonster.R
import hku.cs.comp3330_musclemonster.dashboard.adapters.DashboardCalendarAdapter
import hku.cs.comp3330_musclemonster.dashboard.adapters.DashboardWorkoutItemAdapter
import hku.cs.comp3330_musclemonster.data.WorkoutRepository
import hku.cs.comp3330_musclemonster.pet.PetActivity
import hku.cs.comp3330_musclemonster.social.PostActivity
import hku.cs.comp3330_musclemonster.utils.Constants
import hku.cs.comp3330_musclemonster.workout.WorkoutTrackerActivity
import kotlinx.coroutines.launch

class DashboardActivity : AppCompatActivity() {

    // UI elements
    private lateinit var calendarRecycler: RecyclerView
    private lateinit var workoutAdapter: DashboardWorkoutItemAdapter
    private lateinit var workoutRecycler: RecyclerView

    private lateinit var btnSocialMedia: Button
    private lateinit var btnWorkoutTracker: Button
    private lateinit var btnPets: Button

    private lateinit var llPRs: LinearLayout

    // TODO This is dummy; will be set dynamically later
    private val workoutDays = mutableSetOf(2, 5, 7, 12, 15, 21)
    private val todayDay = 15 // Dummy: fetch today's date in production

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // ======= IMPORTANT =========
        // SharedPreferences to hold small long-lived data (DataStore is a better api)
        // intents are temporary, data gets destroyed on lifecycle
        var username = intent.getStringExtra(Constants.INTENT_ARG_USERNAME)
        val sharedPreferences = getSharedPreferences(Constants.SP, MODE_PRIVATE)
        if (username != null) {
            sharedPreferences.edit {
                putString(Constants.INTENT_ARG_USERNAME, username)
            }
        }
        // use the stored username
        username = sharedPreferences.getString(Constants.INTENT_ARG_USERNAME, null)


        // View init
        calendarRecycler = findViewById(R.id.recyclerCalendar)
        workoutRecycler = findViewById(R.id.rv_workout_list)
        btnSocialMedia = findViewById(R.id.btnSocialMedia)
        btnWorkoutTracker = findViewById(R.id.btnWorkoutTracker)
        btnPets = findViewById(R.id.btnPets)
        llPRs = findViewById(R.id.llPRs)

        // Navigation Boilerplate
        btnSocialMedia.setOnClickListener {
            val user = getSharedPreferences(Constants.SP, MODE_PRIVATE)
                .getString(Constants.INTENT_ARG_USERNAME, "guest")
            val intent = Intent(this, PostActivity::class.java)
            intent.putExtra(Constants.INTENT_ARG_USERNAME, user.toString())
            startActivity(intent)
        }
        btnWorkoutTracker.setOnClickListener {
            val user = getSharedPreferences(Constants.SP, MODE_PRIVATE)
                .getString(Constants.INTENT_ARG_USERNAME, "guest")
            val intent = Intent(this, WorkoutTrackerActivity::class.java)
            intent.putExtra(Constants.INTENT_ARG_USERNAME, user.toString())
            startActivity(intent)
        }

        // Pets activity
        btnPets.setOnClickListener {
            val user = getSharedPreferences(Constants.SP, MODE_PRIVATE)
                .getString(Constants.INTENT_ARG_USERNAME, "guest")
            val intent = Intent(this, PetActivity::class.java)
            intent.putExtra(Constants.INTENT_ARG_USERNAME, user.toString())
            startActivity(intent)
        }

        // Interactive Calendar
        val calendarAdapter = DashboardCalendarAdapter(workoutDays, todayDay) { day ->
            // TODO On day click, maybe show details, or allow editing (stub)
            Toast.makeText(this, "Selected day $day", Toast.LENGTH_SHORT).show()
        }
        calendarRecycler.adapter = calendarAdapter
        calendarRecycler.layoutManager = GridLayoutManager(this, 7)

        // Workout Listing
        workoutAdapter = DashboardWorkoutItemAdapter(mutableListOf())
        workoutRecycler.adapter = workoutAdapter
        workoutRecycler.layoutManager = LinearLayoutManager(this)

        // Get PRs boilerplate from Firestore
        loadPersonalRecords()
        loadWorkoutRecords(username.toString())
    }


    // Boilerplate for PRs from Firestore
    private fun loadPersonalRecords() {
        llPRs.removeAllViews()
        // Example structure (will change depending on your data model later)
        val db = FirebaseFirestore.getInstance()
        val username = "demoUser" // TODO: Hook up real userId

        // Loading logic
        db.collection("users").document(username).collection("personalRecords")
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (doc in querySnapshot.documents) {
                    val workoutType = doc.getString("workoutName") ?: "Unknown"
                    val prValue = doc.getDouble("maxWeight") ?: 0.0
                    val tvPR = TextView(this)
                    tvPR.text = "PR for $workoutType: ${if (prValue > 0) prValue else "No Record"}"
                    llPRs.addView(tvPR)
                }
            }
            .addOnFailureListener { exception ->
                val tvError = TextView(this)
                tvError.text = "Could not load PRs yet"
                llPRs.addView(tvError)
            }
    }

    private fun loadWorkoutRecords(username: String) {
        lifecycleScope.launch {
            val db = FirebaseFirestore.getInstance()
            val repo = WorkoutRepository(db)
            val res = repo.getWorkoutsByUsername(username)

            workoutAdapter.replaceAll(res)
        }
    }
}