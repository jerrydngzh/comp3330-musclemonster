package hku.cs.comp3330_musclemonster.dashboard.adapters

import android.graphics.Color
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DashboardDayHeaderAdapter : RecyclerView.Adapter<DashboardDayHeaderAdapter.DayHeaderViewHolder>() {

    private val dayHeaders = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

    class DayHeaderViewHolder(val view: TextView) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayHeaderViewHolder {
        val textView = TextView(parent.context).apply {
            layoutParams = RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT
            )
            textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            textSize = 14f
            setPadding(0, 8, 0, 8)
            setTextColor(Color.GRAY)
            setTypeface(null, android.graphics.Typeface.BOLD)
        }
        return DayHeaderViewHolder(textView)
    }

    override fun onBindViewHolder(holder: DayHeaderViewHolder, position: Int) {
        holder.view.text = dayHeaders[position]
    }

    override fun getItemCount(): Int = dayHeaders.size
}
