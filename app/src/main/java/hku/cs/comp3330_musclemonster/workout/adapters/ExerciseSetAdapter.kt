package hku.cs.comp3330_musclemonster.workout.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import hku.cs.comp3330_musclemonster.databinding.ItemExerciseSetBinding
import hku.cs.comp3330_musclemonster.workout.model.ExerciseSet

class ExerciseSetAdapter(
    private val items: MutableList<ExerciseSet>,
    private val onRemove: (Int) -> Unit = {},
    private val onChanged: (Int, ExerciseSet) -> Unit = { _, _ -> }
) : RecyclerView.Adapter<ExerciseSetAdapter.Holder>() {

    inner class Holder(binding: ItemExerciseSetBinding) : RecyclerView.ViewHolder(binding.root) {
        val setIndex: TextView = binding.setIndex
        val weightInput: EditText = binding.inputWeight
        val repsInput: EditText = binding.inputReps
        val btnDelete: ImageButton = binding.btnDeleteExecise
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemExerciseSetBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val set = items[position]
        holder.setIndex.text = buildString {
            append("Set ")
            append(position + 1)
        }
        holder.weightInput.setText(set.weightPerRep.toString())
        holder.repsInput.setText(set.repCount.toString())

        holder.weightInput.doAfterTextChanged { s ->
            set.weightPerRep = s?.toString()?.toIntOrNull() ?: 0
            onChanged(position, set)
        }
        holder.repsInput.doAfterTextChanged { s ->
            set.repCount = s?.toString()?.toIntOrNull() ?: 0
            onChanged(position, set)
        }

        holder.btnDelete.setOnClickListener {
            onRemove(position)
        }
    }

    override fun getItemCount(): Int = items.size
}
