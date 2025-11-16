package hku.cs.comp3330_musclemonster.workout

import androidx.lifecycle.ViewModel
import hku.cs.comp3330_musclemonster.workout.model.Exercise
import hku.cs.comp3330_musclemonster.workout.model.ExerciseType

class WorkoutViewModel : ViewModel() {
    val exerciseTypes: List<ExerciseType> = listOf()
    var exercises: MutableList<Exercise> = mutableListOf()

    var datetime: Int = 0
    var name: String = ""
    var totalVolume: Int = 0
    var numExercises: Int = 0
    var notes: String = ""

    // TODO: methods on interacting w/ the ViewModel
}