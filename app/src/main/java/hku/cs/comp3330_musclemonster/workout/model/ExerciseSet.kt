package hku.cs.comp3330_musclemonster.workout.model

data class ExerciseSet(
    var weightPerRep: Int,
    var repCount: Int,
    var setNumber: Int = 0
)