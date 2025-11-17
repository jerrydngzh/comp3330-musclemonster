package hku.cs.comp3330_musclemonster.workout.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import hku.cs.comp3330_musclemonster.databinding.FragmentExerciseSearchBinding
import hku.cs.comp3330_musclemonster.workout.WorkoutViewModel
import hku.cs.comp3330_musclemonster.workout.adapters.ExerciseTypeAdapter
import hku.cs.comp3330_musclemonster.workout.model.ExerciseType


class ExerciseSearchFragment : Fragment() {
    companion object {
        fun newInstance() = ExerciseSearchFragment()
    }
    private var _binding: FragmentExerciseSearchBinding? = null
    private val binding get() = _binding!!
    private val vm: WorkoutViewModel by activityViewModels()
    private lateinit var adapter: ExerciseTypeAdapter
    var exerciseTypeList: List<ExerciseType> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exerciseTypeList = vm.exerciseTypes
    }

    // create a type safe reference to views in the layout
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentExerciseSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ExerciseTypeAdapter(
            items = exerciseTypeList,
            onItemClicked = {
                vm.selectedExerciseTypes
            }
        )
        binding.rvExerciseTypeList.adapter = adapter
        binding.rvExerciseTypeList.layoutManager = LinearLayoutManager(requireContext())

        vm.selectedExerciseTypes.observe(viewLifecycleOwner) {
            adapter.updateSelection(it)
        }

        // fragment navigation
        binding.btAddExSearch.setOnClickListener {
            vm.addExercises()
            vm.clearSelectedExercises()
            parentFragmentManager.popBackStack()
        }

        binding.btCancelExSearch.setOnClickListener {
            vm.clearSelectedExercises()
            parentFragmentManager.popBackStack()
        }
    }
}