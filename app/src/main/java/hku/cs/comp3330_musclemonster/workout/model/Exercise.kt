package hku.cs.comp3330_musclemonster.workout.model

data class Exercise(
    val id: String,
    val name: String,
    var exerciseSets: MutableList<ExerciseSet>
)