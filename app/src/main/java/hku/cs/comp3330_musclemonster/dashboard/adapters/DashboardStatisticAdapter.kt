package hku.cs.comp3330_musclemonster.dashboard.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hku.cs.comp3330_musclemonster.R
import hku.cs.comp3330_musclemonster.dashboard.model.WorkoutStatistic

class DashboardStatisticsAdapter(
    private val statistics: MutableList<WorkoutStatistic>
) : RecyclerView.Adapter<DashboardStatisticsAdapter.StatisticViewHolder>() {

    class StatisticViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvLabel: TextView = view.findViewById(R.id.tvStatLabel)
        val tvValue: TextView = view.findViewById(R.id.tvStatValue)
        val tvSubtitle: TextView = view.findViewById(R.id.tvStatSubtitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatisticViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_workout_statistic, parent, false)
        return StatisticViewHolder(view)
    }

    override fun onBindViewHolder(holder: StatisticViewHolder, position: Int) {
        val stat = statistics[position]
        holder.tvLabel.text = stat.label
        holder.tvValue.text = stat.value

        if (stat.subtitle.isNotEmpty()) {
            holder.tvSubtitle.text = stat.subtitle
            holder.tvSubtitle.visibility = View.VISIBLE
        } else {
            holder.tvSubtitle.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = statistics.size

    fun replaceAll(newStatistics: List<WorkoutStatistic>) {
        statistics.clear()
        statistics.addAll(newStatistics)
        notifyDataSetChanged()
    }
}
