package hku.cs.comp3330_musclemonster.dashboard.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hku.cs.comp3330_musclemonster.databinding.ItemWorkoutBinding
import hku.cs.comp3330_musclemonster.utils.Utils
import hku.cs.comp3330_musclemonster.workout.model.Workout

class DashboardWorkoutItemAdapter(
    private var items: MutableList<Workout>
) : RecyclerView.Adapter<DashboardWorkoutItemAdapter.Holder>() {
    inner class Holder(binding: ItemWorkoutBinding) : RecyclerView.ViewHolder(binding.root) {
        val name = binding.tvDashboardWorkoutName
        val date = binding.tvDashboardWorkoutDate
        val numExercises = binding.tvDashboardNumExercises
        val duration = binding.tvDashboardWorkoutDuration
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Holder {
        val binding = ItemWorkoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(
        holder: Holder,
        position: Int
    ) {
        val w = items[position]

        holder.name.text = w.name
        holder.date.text = Utils.formatMillisToDateTimeString(w.datetime) // format to be readable
        holder.numExercises.text = buildString {
            append(w.exercises.size)
            append(" Exercises")
        }
        holder.duration.text = buildString {
            append(w.durationMinutes)
            append(" min")
        }
    }

    override fun getItemCount(): Int = items.size
}