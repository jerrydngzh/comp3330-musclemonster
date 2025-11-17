package hku.cs.comp3330_musclemonster.workout.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude

data class Workout(
    @DocumentId
    var id: String = "",

    var userId: String = "",

    var name: String = "",
    var note: String = "",
    var durationMinutes: Int = 0,

    var datetime: Long = 0,

    @get:Exclude
    var exercises: MutableList<Exercise> = mutableListOf()
)