package hku.cs.comp3330_musclemonster.workout.model

data class Exercise(
    val id: String = "",
    val orderIdx: Int,
    val name: String,
    val exerciseType: String,
    val totalVolume: Int = 0,
    var exerciseSets: MutableList<ExerciseSet>
)