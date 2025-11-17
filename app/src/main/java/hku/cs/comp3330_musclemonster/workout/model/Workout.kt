package hku.cs.comp3330_musclemonster.workout.model

data class Workout(
    val id: String,
    val userId: String,
    val name: String,
    val datetime: Int,
    val durationMinutes: Int,
    var note: String,
    var exercises: MutableList<Exercise>
)