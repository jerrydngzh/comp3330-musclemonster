package hku.cs.comp3330_musclemonster.workout.model

data class Workout(
    val id: String,
    val name: String,
    val datetime: Int,
    var exercises: MutableList<Exercise>
)