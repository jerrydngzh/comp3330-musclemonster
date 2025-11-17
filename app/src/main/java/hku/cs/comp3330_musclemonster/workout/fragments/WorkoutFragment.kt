package hku.cs.comp3330_musclemonster.workout.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels

import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat

import hku.cs.comp3330_musclemonster.R
import hku.cs.comp3330_musclemonster.databinding.FragmentWorkoutBinding
import hku.cs.comp3330_musclemonster.workout.WorkoutViewModel
import hku.cs.comp3330_musclemonster.workout.adapters.ExerciseAdapter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class WorkoutFragment : Fragment() {

    companion object {
        fun newInstance() = WorkoutFragment()
    }

    private var _binding: FragmentWorkoutBinding? = null
    private val binding get() = _binding!!
    private val workoutViewModel: WorkoutViewModel by activityViewModels()
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
                        ExerciseEditFragment.newInstance(0)) // TODO supply exercise name/type
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
                .replace(R.id.fragment_workout_container, ExerciseSearchFragment.newInstance())
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
            binding.tvTotalExercisesCount.text = it.toString()
        })

        workoutViewModel.totalVolume.observe(viewLifecycleOwner, Observer {
            binding.tvTotalVolume.text = buildString {
                append(it.toString())
                append(" kg")
            }
        })

        // setup the datetime and a date picker dialog
        binding.tvWorkoutDatetime.text = formatMillisToDateTimeString(workoutViewModel.datetime)
        binding.tvWorkoutDatetime.setOnClickListener {
            showDateTimePicker()
        }

        // Finally, update the adapter with the new data
        workoutViewModel.exercises.observe(viewLifecycleOwner) { list ->
            adapter.replaceAll(list.toList())
        }

    }

    // clean up the binding
    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    // ========= Helper Functions =========
    private fun formatMillisToDateTimeString(millis: Long): String {
        val date = Date(millis)
        val formatter = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault())
        return formatter.format(date)
    }

    private fun showDateTimePicker() {
        val initialSelection = workoutViewModel.datetime

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Workout Date")
            .setSelection(initialSelection)
            .build()

        datePicker.addOnPositiveButtonClickListener { selectedDateMillis ->
            showTimePicker(selectedDateMillis)
        }

        datePicker.show(parentFragmentManager, "DATE_PICKER_TAG")
    }

    private fun showTimePicker(dateOnlyMillis: Long) {
        val initialCalendar = Calendar.getInstance().apply {
            timeInMillis = workoutViewModel.datetime
        }

        val timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H) // Use CLOCK_24H for 24-hour format
            .setHour(initialCalendar.get(Calendar.HOUR_OF_DAY))
            .setMinute(initialCalendar.get(Calendar.MINUTE))
            .setTitleText("Select Workout Time")
            .build()

        timePicker.addOnPositiveButtonClickListener {
            val combinedCalendar = Calendar.getInstance(TimeZone.getDefault()).apply {
                timeInMillis = dateOnlyMillis

                set(Calendar.HOUR_OF_DAY, timePicker.hour)
                set(Calendar.MINUTE, timePicker.minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            val finalTimestampMillis = combinedCalendar.timeInMillis

            workoutViewModel.datetime = finalTimestampMillis
            binding.tvWorkoutDatetime.text = formatMillisToDateTimeString(finalTimestampMillis)
        }

        timePicker.show(parentFragmentManager, "TIME_PICKER_TAG")
    }
}