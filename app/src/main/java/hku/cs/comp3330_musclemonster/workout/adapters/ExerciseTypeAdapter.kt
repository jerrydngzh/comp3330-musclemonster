package hku.cs.comp3330_musclemonster.workout.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hku.cs.comp3330_musclemonster.databinding.ItemExerciseSearchTypeBinding
import hku.cs.comp3330_musclemonster.workout.model.ExerciseType

class ExerciseTypeAdapter(
    private var items: List<ExerciseType>,
    private val onItemClicked: (ExerciseType) -> Unit
) : RecyclerView.Adapter<ExerciseTypeAdapter.Holder>() {

    // a view reference holder to a list item
    inner class Holder(binding: ItemExerciseSearchTypeBinding) : RecyclerView.ViewHolder(binding.root) {
        val name: TextView = binding.tvExerciseTypeName
        val exerciseType: TextView = binding.tvExerciseTypeLabel
    }

    // boiler
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemExerciseSearchTypeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    // update the list item view with the data
    override fun onBindViewHolder(holder: Holder, position: Int) {
        val ex = items[position]
        holder.name.text = ex.name
        holder.exerciseType.text = ex.type

        holder.itemView.setOnClickListener {
            onItemClicked(ex)
        }
    }

    override fun getItemCount(): Int = items.size
}
