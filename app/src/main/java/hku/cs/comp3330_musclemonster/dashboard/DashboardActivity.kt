package hku.cs.comp3330_musclemonster.dashboard

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import hku.cs.comp3330_musclemonster.R
import hku.cs.comp3330_musclemonster.dashboard.adapters.DashboardCalendarAdapter
import hku.cs.comp3330_musclemonster.dashboard.adapters.DashboardDayHeaderAdapter
import hku.cs.comp3330_musclemonster.dashboard.adapters.DashboardStatisticsAdapter
import hku.cs.comp3330_musclemonster.dashboard.adapters.DashboardWorkoutItemAdapter
import hku.cs.comp3330_musclemonster.dashboard.utils.DashboardStatisticsCalculator
import hku.cs.comp3330_musclemonster.data.WorkoutRepository
import hku.cs.comp3330_musclemonster.pet.PetActivity
import hku.cs.comp3330_musclemonster.social.PostActivity
import hku.cs.comp3330_musclemonster.utils.Constants
import hku.cs.comp3330_musclemonster.workout.WorkoutTrackerActivity
import hku.cs.comp3330_musclemonster.workout.model.Exercise
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import hku.cs.comp3330_musclemonster.workout.model.Workout

class DashboardActivity : AppCompatActivity() {

    // UI elements
    private lateinit var calendarAdapter: DashboardCalendarAdapter
    private lateinit var calendarRecycler: RecyclerView
    private lateinit var dayHeaderAdapter: DashboardDayHeaderAdapter
    private lateinit var dayHeaderRecycler: RecyclerView
    private lateinit var workoutAdapter: DashboardWorkoutItemAdapter
    private lateinit var workoutRecycler: RecyclerView

    private lateinit var btnSocialMedia: Button
    private lateinit var btnWorkoutTracker: Button
    private lateinit var btnPets: Button
    private lateinit var btnPreviousMonth: ImageButton
    private lateinit var btnNextMonth: ImageButton

    private lateinit var tvMonthYear: TextView
    private lateinit var llPRs: LinearLayout
    private lateinit var statisticsAdapter: DashboardStatisticsAdapter
    private lateinit var statisticsRecycler: RecyclerView

    private var workoutsForCurrentMonth: List<Workout> = listOf()
    private var selectedDay: Int? = null

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
        dayHeaderRecycler = findViewById(R.id.recyclerDayHeaders)
        workoutRecycler = findViewById(R.id.rv_workout_list)
        btnSocialMedia = findViewById(R.id.btnSocialMedia)
        btnWorkoutTracker = findViewById(R.id.btnWorkoutTracker)
        btnPets = findViewById(R.id.btnPets)
        btnPreviousMonth = findViewById(R.id.btnPreviousMonth)
        btnNextMonth = findViewById(R.id.btnNextMonth)
        tvMonthYear = findViewById(R.id.tvMonthYear)
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
        // Get current date info for calendar initialization
        val cal = Calendar.getInstance()
        val currentMonth = cal.get(Calendar.MONTH)
        val currentYear = cal.get(Calendar.YEAR)
        val todayDay = cal.get(Calendar.DAY_OF_MONTH)

        // Update month/year display
        updateMonthYearDisplay(currentMonth, currentYear)

        // Day of Week Headers
        dayHeaderAdapter = DashboardDayHeaderAdapter()
        dayHeaderRecycler.adapter = dayHeaderAdapter
        dayHeaderRecycler.layoutManager = GridLayoutManager(this, 7)

        // Interactive Calendar
        calendarAdapter = DashboardCalendarAdapter(
            workoutDays = mutableSetOf(),
            currentMonth = currentMonth,
            currentYear = currentYear,
            today = todayDay,
            onMonthChangeListener = { month, year ->
                // When the month changes, clear selection and reload
                updateMonthYearDisplay(month, year)
                selectedDay = null
                calendarAdapter.selectedDay = null
                loadWorkoutRecords(username.toString())
            },
            onDateClick = { day ->
                selectedDay = if (selectedDay == day) null else day
                calendarAdapter.selectedDay = selectedDay
                calendarAdapter.notifyDataSetChanged()
                filterAndDisplayWorkouts()
            }
        )
        calendarRecycler.adapter = calendarAdapter
        calendarRecycler.layoutManager = GridLayoutManager(this, 7)

        // Month navigation buttons
        btnPreviousMonth.setOnClickListener {
            calendarAdapter.previousMonth()
        }

        btnNextMonth.setOnClickListener {
            calendarAdapter.nextMonth()
        }

        // Initialize statistics RecyclerView
        statisticsRecycler = findViewById(R.id.recyclerStatistics)
        statisticsAdapter = DashboardStatisticsAdapter(mutableListOf())
        statisticsRecycler.adapter = statisticsAdapter
        statisticsRecycler.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false
        )


        // Workout Listing
        workoutAdapter = DashboardWorkoutItemAdapter(mutableListOf())
        workoutRecycler.adapter = workoutAdapter
        workoutRecycler.layoutManager = LinearLayoutManager(this)

        loadWorkoutRecords(username.toString())
    }

    private fun updateMonthYearDisplay(month: Int, year: Int) {
        val cal = Calendar.getInstance()
        cal.set(year, month, 1)
        val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        tvMonthYear.text = dateFormat.format(cal.time)
    }

    private fun filterAndDisplayWorkouts() {
        val day = selectedDay
        if (day == null) {
            // If no day is selected, show all workouts for the month
            workoutAdapter.replaceAll(workoutsForCurrentMonth)
        } else {
            // If a day is selected, filter the list
            val filteredWorkouts = workoutsForCurrentMonth.filter { workout ->
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = workout.datetime
                calendar.get(Calendar.DAY_OF_MONTH) == day
            }
            workoutAdapter.replaceAll(filteredWorkouts)
        }
    }

    private fun loadWorkoutRecords(username: String) {
        lifecycleScope.launch {
            val db = FirebaseFirestore.getInstance()
            val repo = WorkoutRepository(db)
            val workouts = repo.getWorkoutsByUsername(username)

            workoutsForCurrentMonth = workouts

            // Load exercises for each workout
            val exercisesMap = mutableMapOf<String, List<Exercise>>()
            workouts.forEach { workout ->
                val exercises = repo.getExercisesByWorkoutId(workout.id)
                exercisesMap[workout.id] = exercises
            }

            // Calculate and display statistics
            val statistics = DashboardStatisticsCalculator.calculateStatistics(
                workouts,
                exercisesMap,
                timeframeDays = 30
            )
            statisticsAdapter.replaceAll(statistics)

            // Update calendar and workout list
            calendarAdapter.replaceAll(workoutsForCurrentMonth, username)
            filterAndDisplayWorkouts()
        }
    }
}
