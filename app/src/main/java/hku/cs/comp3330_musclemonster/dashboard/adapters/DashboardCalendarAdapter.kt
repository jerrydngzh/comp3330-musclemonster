package hku.cs.comp3330_musclemonster.dashboard.adapters

import android.graphics.Color
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// Calendar Adapter: interactive + yellow glow
class DashboardCalendarAdapter(
    private val workoutDays: MutableSet<Int>,
    private val today: Int,
    private val dayClickListener: (Int) -> Unit
) : RecyclerView.Adapter<DashboardCalendarAdapter.DayViewHolder>() {
    private val days = (1..30).toList()

    class DayViewHolder(val view: TextView) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(
        parent: ViewGroup,
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
