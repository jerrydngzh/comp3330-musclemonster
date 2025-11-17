package hku.cs.comp3330_musclemonster.workout.adapters

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.recyclerview.widget.RecyclerView
import hku.cs.comp3330_musclemonster.databinding.ItemExerciseSearchTypeBinding
import hku.cs.comp3330_musclemonster.workout.model.ExerciseType

class ExerciseTypeAdapter(
    private var items: List<ExerciseType>,
    private val onItemClicked: (ExerciseType) -> Unit
) : RecyclerView.Adapter<ExerciseTypeAdapter.Holder>() {

    // to keep track of items selected
    private var currSelection: Set<ExerciseType> = emptySet()

    // a view reference holder to a list item
    inner class Holder(binding: ItemExerciseSearchTypeBinding) : RecyclerView.ViewHolder(binding.root) {
        val name: TextView = binding.tvExerciseTypeName
        val exerciseType: TextView = binding.tvExerciseTypeLabel
        val container: LinearLayout = binding.exerciseTypeItem
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

        val context = holder.container.context
        if (currSelection.contains(ex)) {
            holder.container.setBackgroundColor(context.getColorFromAttr(android.R.attr.colorPrimary))
            holder.name.setTextColor(Color.WHITE)
            holder.exerciseType.setTextColor(Color.WHITE)
        } else {
            holder.container.setBackgroundColor(Color.TRANSPARENT)
            holder.name.setTextColor(context.getColorFromAttr(android.R.attr.textColorPrimary))
            holder.exerciseType.setTextColor(context.getColorFromAttr(android.R.attr.textColorSecondary))
        }

        holder.container.setOnClickListener { onItemClicked(ex) }
    }

    override fun getItemCount(): Int = items.size

    // ==== Helper Functions ====
    private fun Context.getColorFromAttr(
        @AttrRes attrColor: Int,
        typedValue: TypedValue = TypedValue(),
        resolveRefs: Boolean = true
    ): Int {
        theme.resolveAttribute(attrColor, typedValue, resolveRefs)
        return typedValue.data
    }

    fun updateSelection(newSelection: Set<ExerciseType>) {
        this.currSelection = newSelection
        notifyDataSetChanged() // In a real app, use DiffUtil for better performance
    }
}
