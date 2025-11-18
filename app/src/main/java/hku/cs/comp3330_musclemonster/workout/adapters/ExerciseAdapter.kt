package hku.cs.comp3330_musclemonster.workout.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hku.cs.comp3330_musclemonster.databinding.ItemExerciseBinding
import hku.cs.comp3330_musclemonster.workout.model.Exercise

class ExerciseAdapter(
    private var items: MutableList<Exercise>,
    private val onEdit: (Int, String, String) -> Unit,
    private val onDelete: (Int) -> Unit
) : RecyclerView.Adapter<ExerciseAdapter.Holder>() {

    // a view reference holder to a list item
    inner class Holder(binding: ItemExerciseBinding) : RecyclerView.ViewHolder(binding.root) {
        val name: TextView = binding.tvExerciseName
        val summary: TextView = binding.tvExerciseSummary
        val editBtn: LinearLayout = binding.btnExerciseEdit
        val deleteBtn: ImageButton = binding.btnDeleteExercise
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemExerciseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    // update the list item view with the data
    override fun onBindViewHolder(holder: Holder, position: Int) {
        val ex = items[position]
        holder.name.text = ex.name
        holder.summary.text = buildString {
            append(ex.exerciseSets.size)
            append(" sets • ")
            append(ex.exerciseSets.sumOf { it.weightPerRep * it.repCount })
            append(" vol")
        }
        holder.editBtn.setOnClickListener { onEdit(position, ex.name, ex.exerciseType) }
        holder.deleteBtn.setOnClickListener { onDelete(position) }
    }

    override fun getItemCount(): Int = items.size

    fun replaceAll(newItems: List<Exercise>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}