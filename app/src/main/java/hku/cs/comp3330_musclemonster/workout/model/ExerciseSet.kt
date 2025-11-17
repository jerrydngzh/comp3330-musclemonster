package hku.cs.comp3330_musclemonster.workout.model

data class ExerciseSet(
    val setNumber: Int,
    val weightPerRep: Int,
    val repCount: Int
) {
    companion object {
        // for firestore data mapping
        fun fromMap(map: Map<String, Any>): ExerciseSet {
            return ExerciseSet(
                setNumber = (map["setNumber"] as? Long)?.toInt() ?: 0,
                weightPerRep = (map["weight"] as? Long)?.toInt() ?: 0,
                repCount = (map["reps"] as? Long)?.toInt() ?: 0,
            )
        }
    }
}