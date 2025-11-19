package hku.cs.comp3330_musclemonster.dashboard.adapters

import android.graphics.Color
import android.util.Log
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hku.cs.comp3330_musclemonster.workout.model.Workout
import java.util.Calendar
import java.util.Date

class DashboardCalendarAdapter(
    private val workoutDays: MutableSet<Int>,
    private var currentMonth: Int,
    private var currentYear: Int,
    private val today: Int,
    private val onMonthChangeListener: (month: Int, year: Int) -> Unit
) : RecyclerView.Adapter<DashboardCalendarAdapter.DayViewHolder>() {

    private var days = mutableListOf<Int>()
    private var startOffset = 0 // Empty cells before first day

    init {
        updateDaysInMonth()
    }

    class DayViewHolder(val view: TextView) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
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
        // Handle empty cells before the first day
        if (position < startOffset) {
            holder.view.text = ""
            holder.view.setBackgroundColor(Color.TRANSPARENT)
            holder.view.setTextColor(Color.TRANSPARENT)
            return
        }

        val dayIndex = position - startOffset
        if (dayIndex >= days.size) {
            holder.view.text = ""
            holder.view.setBackgroundColor(Color.TRANSPARENT)
            holder.view.setTextColor(Color.TRANSPARENT)
            return
        }

        val day = days[dayIndex]
        holder.view.text = day.toString()

        // Visual state: yellow glow when marked
        if (workoutDays.contains(day)) {
            Log.d("CalendarDebug", "Day $day: Setting YELLOW background")
            holder.view.setBackgroundColor(Color.YELLOW)
        } else {
            Log.d("CalendarDebug", "Day $day: Setting TRANSPARENT background")
            holder.view.setBackgroundColor(Color.TRANSPARENT)
        }

        // Highlight current day if we're viewing the current month
        val cal = Calendar.getInstance()
        val isCurrentMonth = currentMonth == cal.get(Calendar.MONTH) &&
                currentYear == cal.get(Calendar.YEAR)

        if (day == today && isCurrentMonth) {
            holder.view.setTextColor(Color.RED)
        } else {
            holder.view.setTextColor(Color.BLACK)
        }
    }


    override fun getItemCount(): Int = days.size + startOffset

    // Update the number of days based on current month/year
    private fun updateDaysInMonth() {
        val cal = Calendar.getInstance()
        cal.set(currentYear, currentMonth, 1)

        // Get day of week for first day (0 = Sunday, 1 = Monday, etc.)
        startOffset = cal.get(Calendar.DAY_OF_WEEK) - 1

        // Get number of days in month
        val maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        days = (1..maxDay).toMutableList()
    }

    // Method to change to next month
    fun nextMonth() {
        currentMonth++
        if (currentMonth > 11) {
            currentMonth = 0
            currentYear++
        }
        updateDaysInMonth()
        onMonthChangeListener(currentMonth, currentYear)
        notifyDataSetChanged()
    }

    // Method to change to previous month
    fun previousMonth() {
        currentMonth--
        if (currentMonth < 0) {
            currentMonth = 11
            currentYear--
        }
        updateDaysInMonth()
        onMonthChangeListener(currentMonth, currentYear)
        notifyDataSetChanged()
    }

    fun replaceAll(newItems: List<Workout>, username: String) {
        workoutDays.clear()

        Log.d("CalendarDebug", "=== Starting replaceAll ===")
        Log.d("CalendarDebug", "Username: $username")
        Log.d("CalendarDebug", "Total workouts: ${newItems.size}")
        Log.d("CalendarDebug", "Current month: $currentMonth, Current year: $currentYear")

        for (newItem in newItems) {
            Log.d("CalendarDebug", "Checking workout - name: ${newItem.name}, datetime: ${newItem.datetime}")

            if (newItem.username == username) {
                val itemCalendar = Calendar.getInstance()
                itemCalendar.timeInMillis = newItem.datetime

                val itemMonth = itemCalendar.get(Calendar.MONTH)
                val itemYear = itemCalendar.get(Calendar.YEAR)
                val dayOfMonth = itemCalendar.get(Calendar.DAY_OF_MONTH)

                Log.d("CalendarDebug", "Workout date: Month=$itemMonth, Year=$itemYear, Day=$dayOfMonth")

                // Check if item is in currently displayed month
                if (itemMonth == currentMonth && itemYear == currentYear) {
                    workoutDays.add(dayOfMonth)
                    Log.d("CalendarDebug", "✅ ADDED day $dayOfMonth to workoutDays")
                } else {
                    Log.d("CalendarDebug", "❌ NOT in current month/year")
                }
            } else {
                Log.d("CalendarDebug", "❌ Username mismatch: '${newItem.name}' != '$username'")
            }
        }

        Log.d("CalendarDebug", "Final workoutDays set: $workoutDays")
        Log.d("CalendarDebug", "=== End replaceAll ===")

        notifyDataSetChanged()
    }
}
