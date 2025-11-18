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
private const val ARG_EXERCISE_NAME = "exercise_name"
private const val ARG_EXERCISE_TYPE = "exercise_type"


class ExerciseEditFragment : Fragment() {
    private var _binding: FragmentExerciseEditBinding? = null
    private val binding get() = _binding!!

    private val viewModel: WorkoutViewModel by activityViewModels()
    private var exerciseIndex: Int = 0
    private lateinit var setsAdapter: ExerciseSetAdapter

    companion object {
        fun newInstance(index: Int, exerciseName: String, exerciseType: String): ExerciseEditFragment {
            val f = ExerciseEditFragment()
            val b = Bundle()
            b.putInt(ARG_INDEX, index)
            b.putString(ARG_EXERCISE_NAME, exerciseName)
            b.putString(ARG_EXERCISE_TYPE, exerciseType)
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
        val setsForAdapter: MutableList<ExerciseSet> = exercise?.exerciseSets?.toMutableList() ?: mutableListOf()
        val newlyAddedSets = mutableListOf<ExerciseSet>()


        if (exercise == null) {
            requireActivity().onBackPressedDispatcher.onBackPressed()
            return
        }

        // setup
        setsAdapter = ExerciseSetAdapter(setsForAdapter,
            onRemove = { pos ->
                if (pos in setsForAdapter.indices) {
                    setsForAdapter.removeAt(pos)
                    setsAdapter.notifyItemRemoved(pos)
                    setsAdapter.notifyItemRangeChanged(pos, setsForAdapter.size)
                }
            },
            onChanged = { pos, newSet ->
                if (pos in setsForAdapter.indices) {
                    setsForAdapter[pos] = newSet
                }
            })

        binding.setsRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.setsRecycler.adapter = setsAdapter

        // textviews
        binding.tvExerciseSetTypeName.text = buildString {
            append(exercise.exerciseType)
            append(" • ")
        }
        binding.tvExerciseSetName.text = exercise.name

        // buttons
        binding.btnAddSet.setOnClickListener {
            val e = ExerciseSet(0, 0)
            setsForAdapter.add(e)
            newlyAddedSets.add(e)
            setsAdapter.notifyItemInserted(setsForAdapter.size - 1)
        }
        binding.btnSaveSets.setOnClickListener {
            viewModel.updateExerciseSets(exerciseIndex, setsForAdapter)
            parentFragmentManager.popBackStack()
        }
        binding.btnExerciseSetCancel.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}