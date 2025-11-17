package hku.cs.comp3330_musclemonster.workout.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import hku.cs.comp3330_musclemonster.databinding.FragmentExerciseEditBinding
import hku.cs.comp3330_musclemonster.workout.WorkoutViewModel
import hku.cs.comp3330_musclemonster.workout.adapters.ExerciseSetAdapter
import hku.cs.comp3330_musclemonster.workout.model.ExerciseSet

private const val ARG_INDEX = "exercise_index"

class ExerciseEditFragment : Fragment() {
    private var _binding: FragmentExerciseEditBinding? = null
    private val binding get() = _binding!!

    private val viewModel: WorkoutViewModel by activityViewModels()
    private var exerciseIndex: Int = 0
    private lateinit var setsAdapter: ExerciseSetAdapter

    companion object {
        fun newInstance(index: Int): ExerciseEditFragment {
            val f = ExerciseEditFragment()
            val b = Bundle()
            b.putInt(ARG_INDEX, index)
            f.arguments = b
            return f
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exerciseIndex = arguments?.getInt(ARG_INDEX) ?: 0
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentExerciseEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val exercise = viewModel.exercises.value?.getOrNull(exerciseIndex)
        if (exercise == null) {
            requireActivity().onBackPressedDispatcher.onBackPressed()
            return
        }

        setsAdapter = ExerciseSetAdapter(exercise.exerciseSets,
            onRemove = { pos ->
                exercise.exerciseSets.removeAt(pos)
                setsAdapter.notifyItemRemoved(pos)
            },
            onChanged = { pos, newSet ->
                exercise.exerciseSets[pos] = newSet
            })

        binding.setsRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.setsRecycler.adapter = setsAdapter

        binding.btnAddSet.setOnClickListener {
            exercise.exerciseSets.add(ExerciseSet(0, 0, 0))
            setsAdapter.notifyItemInserted(exercise.exerciseSets.size - 1)
        }

        binding.btnSaveSets.setOnClickListener {
            viewModel.updateExerciseSets(exerciseIndex, exercise.exerciseSets)
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}