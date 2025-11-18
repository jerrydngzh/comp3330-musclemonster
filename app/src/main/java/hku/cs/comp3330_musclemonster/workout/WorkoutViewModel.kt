package hku.cs.comp3330_musclemonster.workout

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map

import hku.cs.comp3330_musclemonster.workout.model.Exercise
import hku.cs.comp3330_musclemonster.workout.model.ExerciseSet
import hku.cs.comp3330_musclemonster.workout.model.ExerciseType
import kotlinx.datetime.Clock


class WorkoutViewModel : ViewModel() {
    // exercises
    private val _exercises = MutableLiveData<MutableList<Exercise>>(mutableListOf())
    val exercises: LiveData<MutableList<Exercise>> = _exercises

    // exercise selection
    private val _selectedExerciseTypes = MutableLiveData<Set<ExerciseType>>(emptySet())
    val selectedExerciseTypes: LiveData<Set<ExerciseType>> = _selectedExerciseTypes
    val exerciseTypes: List<ExerciseType> = _loadExerciseTypes()

    // === other ===
    var name: String = ""
    var datetime: Long = Clock.System.now().toEpochMilliseconds()
    var notes: String = ""
    var duration: Int = 0

    // computed basic stats
    var numExercises: LiveData<Int> = _exercises.map { it.size }
    var totalVolume: LiveData<Int> = _exercises.map { exs ->
        exs.sumOf { ex ->
            ex.exerciseSets.sumOf {
                it.weightPerRep * it.repCount
            }
        }
    }


    // =========== viewmodel methods ==============
    fun removeExercise(index: Int) {
        val list = _exercises.value ?: return
        if (index in list.indices) {
            list.removeAt(index)
            _exercises.value = list
        }
    }

    fun toggleExerciseSelection(type: ExerciseType) {
        val currentSelection = _selectedExerciseTypes.value ?: emptySet()
        val newSelection = if (currentSelection.contains(type)) {
            currentSelection.minus(type)
        } else {
            currentSelection.plus(type)
        }
        _selectedExerciseTypes.value = newSelection
    }

    fun confirmSelections() {
        val mainList = _exercises.value ?: mutableListOf()
        _selectedExerciseTypes.value?.forEach { type ->
            mainList.add(
                Exercise(
                    name = type.name,
                    exerciseType = type.type,
                    orderIdx = mainList.size,
                    exerciseSets = mutableListOf()
                )
            )
        }
        _exercises.value = mainList
        clearSelections()
    }

    fun clearSelections() {
        _selectedExerciseTypes.value = emptySet()
    }

    // TODO check
    fun updateExerciseSets(index: Int, newSets: List<ExerciseSet>) {

        val list = _exercises.value ?: return
        if (index in list.indices) {
            list[index].exerciseSets = newSets.toMutableList()
            _exercises.value = list
        }
    }

    private fun _loadExerciseTypes() : List<ExerciseType> {
        return listOf(// Chest Exercises
            ExerciseType(id = "chest_01", name = "Bench Press", type = "Chest"),
            ExerciseType(id = "chest_02", name = "Dumbbell Flyes", type = "Chest"),
            ExerciseType(id = "chest_03", name = "Incline Dumbbell Press", type = "Chest"),
            ExerciseType(id = "chest_04", name = "Push-ups", type = "Chest"),
            ExerciseType(id = "chest_05", name = "Cable Crossover", type = "Chest"),

            // Back Exercises
            ExerciseType(id = "back_01", name = "Pull-ups", type = "Back"),
            ExerciseType(id = "back_02", name = "Deadlift", type = "Back"),
            ExerciseType(id = "back_03", name = "Bent-Over Rows", type = "Back"),
            ExerciseType(id = "back_04", name = "Lat Pulldown", type = "Back"),
            ExerciseType(id = "back_05", name = "Seated Cable Rows", type = "Back"),

            // Leg Exercises
            ExerciseType(id = "legs_01", name = "Squat", type = "Legs"),
            ExerciseType(id = "legs_02", name = "Leg Press", type = "Legs"),
            ExerciseType(id = "legs_03", name = "Lunges", type = "Legs"),
            ExerciseType(id = "legs_04", name = "Leg Curls", type = "Legs"),
            ExerciseType(id = "legs_05", name = "Calf Raises", type = "Legs"),

            // Shoulder Exercises
            ExerciseType(id = "shld_01", name = "Overhead Press", type = "Shoulders"),
            ExerciseType(id = "shld_02", name = "Lateral Raises", type = "Shoulders"),
            ExerciseType(id = "shld_03", name = "Face Pulls", type = "Shoulders"),
            ExerciseType(id = "shld_04", name = "Arnold Press", type = "Shoulders"),

            // Arms (Biceps & Triceps)
            ExerciseType(id = "arms_01", name = "Bicep Curls", type = "Arms"),
            ExerciseType(id = "arms_02", name = "Tricep Dips", type = "Arms"),
            ExerciseType(id = "arms_03", name = "Hammer Curls", type = "Arms"),
            ExerciseType(id = "arms_04", name = "Tricep Pushdowns", type = "Arms"),

            // Core Exercises
            ExerciseType(id = "core_01", name = "Plank", type = "Core"),
            ExerciseType(id = "core_02", name = "Crunches", type = "Core"),
            ExerciseType(id = "core_03", name = "Leg Raises", type = "Core")
        )
    }
}