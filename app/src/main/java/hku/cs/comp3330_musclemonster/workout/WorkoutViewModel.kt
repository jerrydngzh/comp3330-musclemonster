package hku.cs.comp3330_musclemonster.workout

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import hku.cs.comp3330_musclemonster.workout.model.Exercise
import hku.cs.comp3330_musclemonster.workout.model.ExerciseSet
import hku.cs.comp3330_musclemonster.workout.model.ExerciseType

class WorkoutViewModel : ViewModel() {
    private val _exercises = MutableLiveData<MutableList<Exercise>>(mutableListOf())
    val exercises: LiveData<MutableList<Exercise>> = _exercises

    // TODO load from db/hard coded list to be used in exercise selection
    val exerciseTypes: List<ExerciseType> = listOf()

    var name: String = ""
    var datetime: Int = 0
    var totalVolume: Int = 0
    var numExercises: Int = 0
    var notes: String = ""
    var duration: Int = 0

    // ====== Workout Model ======
    // TODO updating attributes of a workout to be saved to firestore

    // ====== Exercises List ======
    fun addExercise(type: ExerciseType) {
        val list = _exercises.value ?: mutableListOf()
        list.add(
            Exercise(
                name = type.name,
                exerciseType = type.type,
                orderIdx = list.size,
                exerciseSets = mutableListOf()
            )
        )
        _exercises.value = list
    }

    fun removeExercise(index: Int) {
        val list = _exercises.value ?: return
        if (index in list.indices) {
            list.removeAt(index)
            _exercises.value = list
        }
    }

    // ====== Exercise Sets w/in an Exercise ======
    fun updateExerciseSets(index: Int, sets: MutableList<ExerciseSet>) {
        val list = _exercises.value ?: return
        if (index in list.indices) {
            list[index].exerciseSets = sets
            _exercises.value = list
        }
    }

    // ====== Basic derived stats ======
    fun totalSets(): Int {
        return _exercises.value?.sumOf { it.exerciseSets.size } ?: 0
    }

    fun totalWeightVolume(): Int {
        return _exercises.value?.sumOf { ex ->
            ex.exerciseSets.sumOf { it.weightPerRep * it.repCount }
        } ?: 0
    }
}