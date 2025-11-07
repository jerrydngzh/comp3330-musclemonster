package hku.cs.comp3330_musclemonster

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class DashboardActivity : AppCompatActivity() {

    private lateinit var tvStreak: TextView
    private lateinit var tvTotalWorkouts: TextView
    private lateinit var tvStreakBadge: TextView
    private lateinit var calendarRecycler: RecyclerView
    private lateinit var btnLogWorkout: Button

    // Example: ToggleButtons for days of the week
    private lateinit var tgMon: ToggleButton
    private lateinit var tgTue: ToggleButton
    private lateinit var tgWed: ToggleButton
    private lateinit var tgThu: ToggleButton
    private lateinit var tgFri: ToggleButton
    private lateinit var tgSat: ToggleButton
    private lateinit var tgSun: ToggleButton

    // Example: Dummy workout data for the calendar
    private val workoutDays = setOf(2, 5, 7, 12, 15, 21) // Example completed workout days (for current month)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // Initialize views
        tvStreak = findViewById(R.id.tvStreak)
        tvTotalWorkouts = findViewById(R.id.tvTotalWorkouts)
        tvStreakBadge = findViewById(R.id.tvStreakBadge)
        calendarRecycler = findViewById(R.id.recyclerCalendar)
        btnLogWorkout = findViewById(R.id.btnLogWorkout)

        tgMon = findViewById(R.id.tgMon)
        tgTue = findViewById(R.id.tgTue)
        tgWed = findViewById(R.id.tgWed)
        tgThu = findViewById(R.id.tgThu)
        tgFri = findViewById(R.id.tgFri)
        tgSat = findViewById(R.id.tgSat)
        tgSun = findViewById(R.id.tgSun)

        // Set up dummy streaks/metrics
        updateMetrics(7, 21)
        tvStreakBadge.text = "🔥 7-Day Streak"

        // Set up the calendar RecyclerView as a 7-column grid
        calendarRecycler.layoutManager = GridLayoutManager(this, 7)
        val calendarAdapter = CalendarAdapter(workoutDays)
        calendarRecycler.adapter = calendarAdapter

        // Set ToggleButton listeners
        val toggles = listOf(tgMon, tgTue, tgWed, tgThu, tgFri, tgSat, tgSun)
        toggles.forEachIndexed { idx, toggle ->
            toggle.setOnCheckedChangeListener { _, isChecked ->
                // Save user's preferred workout days (add persistence as needed)
                Toast.makeText(this, "Workout on ${toggle.text} set to $isChecked", Toast.LENGTH_SHORT).show()
            }
        }

        // Quick log today’s workout (replace with real logic/storage as needed)
        btnLogWorkout.setOnClickListener {
            Toast.makeText(this, "Marked today as workout done!", Toast.LENGTH_SHORT).show()
            // Update metrics, calendar, streaks, etc.
        }
    }

    private fun updateMetrics(streak: Int, totalWorkouts: Int) {
        tvStreak.text = "Streak: $streak🔥"
        tvTotalWorkouts.text = "Total: $totalWorkouts"
        tvStreakBadge.text = "🔥 $streak-Day Streak"
    }

    // Example Adapter for calendar days
    class CalendarAdapter(private val workoutDays: Set<Int>) : RecyclerView.Adapter<CalendarAdapter.DayViewHolder>() {
        // 30-day calendar for simplicity; real implementation should be dynamic
        private val days = (1..30).toList()

        class DayViewHolder(val view: TextView) : RecyclerView.ViewHolder(view)

        override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): DayViewHolder {
            val textView = TextView(parent.context)
            textView.layoutParams = RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT
            )
            textView.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            textView.textSize = 16f
            textView.setPadding(0, 16, 0, 16)
            return DayViewHolder(textView)
        }

        override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
            val day = days[position]
            holder.view.text = day.toString()
            // Highlight if workout was completed
            if (workoutDays.contains(day)) {
                holder.view.setBackgroundColor(0xFFCCFF90.toInt()) // light green
            } else {
                holder.view.setBackgroundColor(0x00000000) // transparent
            }
        }

        override fun getItemCount(): Int = days.size
    }
}
