package hku.cs.comp3330_musclemonster

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import hku.cs.comp3330_musclemonster.social.PostActivity
import hku.cs.comp3330_musclemonster.utils.Constants
import hku.cs.comp3330_musclemonster.workout.WorkoutTrackerActivity
import androidx.core.content.edit

class DashboardActivity : AppCompatActivity() {

    // UI elements
    private lateinit var calendarRecycler: RecyclerView

    private lateinit var btnSocialMedia: Button
    private lateinit var btnWorkoutTracker: Button
    private lateinit var btnPets: Button

    private lateinit var llPRs: LinearLayout

    // This is dummy; will be set dynamically later
    private val workoutDays = mutableSetOf(2, 5, 7, 12, 15, 21)
    private val todayDay = 15 // Dummy: fetch today's date in production

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // ======= IMPORTANT =========
        // SharedPreferences to hold small long-lived data (DataStore is a better api)
        // intents are temporary, data gets destroyed on lifecycle
        val username = intent.getStringExtra(Constants.INTENT_ARG_USERNAME)
        val sharedPreferences = getSharedPreferences(Constants.SP, MODE_PRIVATE)
        if (username != null) {
            sharedPreferences.edit {
                putString(Constants.INTENT_ARG_USERNAME, username)
            }
        }

        // View init
        calendarRecycler = findViewById(R.id.recyclerCalendar)
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

        // TODO: for when we add pets activity
//        btnPets.setOnClickListener {
//            val intent = Intent(this, PetsActivity::class.java)
//            startActivity(intent)
//        }

        // Interactive Calendar
        calendarRecycler.layoutManager = GridLayoutManager(this, 7)
        val calendarAdapter = CalendarAdapter(workoutDays, todayDay) { day ->
            // On day click, maybe show details, or allow editing (stub)
            Toast.makeText(this, "Selected day $day", Toast.LENGTH_SHORT).show()
        }
        calendarRecycler.adapter = calendarAdapter

        // Get PRs boilerplate from Firestore
        loadPersonalRecords()
    }

    // Calendar Adapter: interactive + yellow glow
    class CalendarAdapter(private val workoutDays: MutableSet<Int>, private val today: Int, private val dayClickListener: (Int) -> Unit) : RecyclerView.Adapter<CalendarAdapter.DayViewHolder>() {
        private val days = (1..30).toList()

        class DayViewHolder(val view: TextView) : RecyclerView.ViewHolder(view)

        override fun onCreateViewHolder(
            parent: android.view.ViewGroup,
            viewType: Int
        ): DayViewHolder {
            val textView = TextView(parent.context).apply {
                layoutParams = RecyclerView.LayoutParams(
                    RecyclerView.LayoutParams.MATCH_PARENT,
                    RecyclerView.LayoutParams.WRAP_CONTENT
                )
                textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                textSize = 16f
                setPadding(0, 16, 0, 16)
            }
            return DayViewHolder(textView)
        }

        override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
            val day = days[position]
            holder.view.text = day.toString()

            // Visual state: yellow glow when marked
            if (workoutDays.contains(day)) {
                holder.view.setBackgroundColor(Color.YELLOW)
            } else {
                holder.view.setBackgroundColor(Color.TRANSPARENT)
            }

            // Highlight today (optional)
            if (day == today) {
                holder.view.setTextColor(Color.RED)
            } else {
                holder.view.setTextColor(Color.BLACK)
            }

            // Tap-to-toggle
            holder.view.setOnClickListener {
                dayClickListener(day)
            }
        }

        override fun getItemCount(): Int = days.size
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
}