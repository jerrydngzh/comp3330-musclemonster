package hku.cs.comp3330_musclemonster.workout.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment

import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.datepicker.MaterialDatePicker

import hku.cs.comp3330_musclemonster.R
import hku.cs.comp3330_musclemonster.databinding.FragmentWorkoutBinding
import hku.cs.comp3330_musclemonster.workout.WorkoutViewModel
import hku.cs.comp3330_musclemonster.workout.adapters.ExerciseAdapter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WorkoutFragment : Fragment() {

    companion object {
        fun newInstance() = WorkoutFragment()
    }

    private var _binding: FragmentWorkoutBinding? = null
    private val binding get() = _binding!!
    private val workoutViewModel: WorkoutViewModel by viewModels()
    private lateinit var adapter: ExerciseAdapter

    // create a type safe reference to views in the layout
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentWorkoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // setup the adapter for list of exercises
        adapter = ExerciseAdapter(
            items = workoutViewModel.exercises.value ?: mutableListOf(),
            onEdit = { position ->
                parentFragmentManager.beginTransaction()
                    .replace(
                        R.id.fragment_workout_container,
                        ExerciseEditFragment.newInstance("","")) // TODO
                    .addToBackStack(null)
                    .commit()
            },
            onDelete = { position ->
                workoutViewModel.removeExercise(position)
            }
        )

        // setup the recycler view
        binding.rvWorkoutExerciseList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvWorkoutExerciseList.adapter = adapter

        // setup fragment navigation
        // add button
        binding.btnAddExercise.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_workout_container, ExerciseSearchFragment())
                .addToBackStack(null)
                .commit()
        }

        // register the save button for saving the workout
        binding.btnSaveWorkout.setOnClickListener {
            // TODO save workout to db, navigate to dashboard via intent
            // call the firestore repository
        }

        // linking changes on TextViews relative to the viewmodel
        binding.textInputEditText.doOnTextChanged { text, start, before, count ->
            workoutViewModel.name = text.toString()
         }

        workoutViewModel.numExercises.observe(viewLifecycleOwner, Observer {
            view.findViewById<TextView>(R.id.tv_total_exercises_count).text = it.toString()
        })

        workoutViewModel.totalVolume.observe(viewLifecycleOwner, Observer {
            view.findViewById<TextView>(R.id.tv_total_volume).text = it.toString()
        })

        // setup the datetime and a date picker dialog
        binding.tvWorkoutDatetime.text = formatMillisToDateString(workoutViewModel.datetime)
        binding.tvWorkoutDatetime.setOnClickListener {
            showDatePicker()
        }

    }

    // clean up the binding
    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun formatMillisToDateString(millis: Long): String {
        val date = Date(millis)
        // You can customize this pattern (e.g., "MM/dd/yyyy", "EEEE, MMM dd")
        val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        return formatter.format(date)
    }

    private fun showDatePicker() {
        // Use the currently stored date as the pre-selected value
        val initialSelection = workoutViewModel.datetime

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Workout Date")
            .setSelection(initialSelection)
            .build()

        datePicker.addOnPositiveButtonClickListener { selectedDateMillis ->
            workoutViewModel.datetime = selectedDateMillis
            binding.tvWorkoutDatetime.text = formatMillisToDateString(workoutViewModel.datetime)
        }

        datePicker.show(parentFragmentManager, "DATE_PICKER_TAG")
    }


}