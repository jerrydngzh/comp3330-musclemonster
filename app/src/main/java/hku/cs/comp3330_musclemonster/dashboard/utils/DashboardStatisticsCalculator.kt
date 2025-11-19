package hku.cs.comp3330_musclemonster.dashboard.utils

import hku.cs.comp3330_musclemonster.dashboard.model.WorkoutStatistic
import hku.cs.comp3330_musclemonster.workout.model.Exercise
import hku.cs.comp3330_musclemonster.workout.model.Workout
import java.util.Calendar
import kotlin.math.roundToInt

object DashboardStatisticsCalculator {

    fun calculateStatistics(
        workouts: List<Workout>,
        exercises: Map<String, List<Exercise>>, // Map of workoutId to exercises
        timeframeDays: Int = 30
    ): List<WorkoutStatistic> {
        val statistics = mutableListOf<WorkoutStatistic>()

        // Filter workouts to timeframe
        val cutoffTime = System.currentTimeMillis() - (timeframeDays * 24 * 60 * 60 * 1000L)
        val recentWorkouts = workouts.filter { it.datetime >= cutoffTime }

        // Calculate total volume
        var totalVolume = 0.0
        var heaviestWeight = 0.0
        var heaviestExerciseName = ""

        recentWorkouts.forEach { workout ->
            val workoutExercises = exercises[workout.id] ?: emptyList()
            workoutExercises.forEach { exercise ->
                exercise.exerciseSets.forEach { set ->  // Changed: sets → exerciseSets
                    val volume = set.weightPerRep * set.repCount  // Changed: weight → weightPerRep, reps → repCount
                    totalVolume += volume

                    if (set.weightPerRep > heaviestWeight) {  // Changed: weight → weightPerRep
                        heaviestWeight = set.weightPerRep.toDouble()  // Convert Int to Double
                        heaviestExerciseName = exercise.name
                    }
                }
            }
        }

        // Total Volume Stat
        statistics.add(
            WorkoutStatistic(
                label = "Total Volume",
                value = "${totalVolume.roundToInt()} lbs",
                subtitle = "Last $timeframeDays days"
            )
        )

        // Heaviest Weight Stat
        if (heaviestWeight > 0) {
            statistics.add(
                WorkoutStatistic(
                    label = "Heaviest Lift",
                    value = "${heaviestWeight.roundToInt()} lbs",
                    subtitle = heaviestExerciseName
                )
            )
        }

        // Total Workouts Stat
        statistics.add(
            WorkoutStatistic(
                label = "Total Workouts",
                value = "${recentWorkouts.size}",
                subtitle = "Last $timeframeDays days"
            )
        )

        // Average Duration Stat
        if (recentWorkouts.isNotEmpty()) {
            val avgDuration = recentWorkouts.map { it.durationMinutes }.average().roundToInt()
            statistics.add(
                WorkoutStatistic(
                    label = "Avg Duration",
                    value = "$avgDuration min",
                    subtitle = "Per workout"
                )
            )
        }

        return statistics
    }
}
