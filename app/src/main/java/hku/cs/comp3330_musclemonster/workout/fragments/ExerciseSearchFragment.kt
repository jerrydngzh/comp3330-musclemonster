package hku.cs.comp3330_musclemonster.workout.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import hku.cs.comp3330_musclemonster.R
import hku.cs.comp3330_musclemonster.workout.WorkoutViewModel
import hku.cs.comp3330_musclemonster.workout.model.ExerciseType


class ExerciseSearchFragment : Fragment() {
    var exerciseTypeList: List<ExerciseType> = listOf()
    private val vm: WorkoutViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exerciseTypeList = vm.exerciseTypes
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_exercise_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO
    }
}