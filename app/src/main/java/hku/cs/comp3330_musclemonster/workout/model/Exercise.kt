package hku.cs.comp3330_musclemonster.workout.model

import com.google.firebase.firestore.DocumentId

data class Exercise(
    @DocumentId
    val id: String = "",
    val orderIdx: Int = 0,
    val name: String = "",
    val exerciseType: String = "",
    val totalVolume: Int = 0,
    var exerciseSets: MutableList<ExerciseSet> = mutableListOf()
)