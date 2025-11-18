package hku.cs.comp3330_musclemonster.workout.data

import com.google.firebase.firestore.FirebaseFirestore
import hku.cs.comp3330_musclemonster.workout.model.Exercise
import hku.cs.comp3330_musclemonster.workout.model.Workout
import kotlinx.coroutines.tasks.await

class WorkoutRepository(private val db: FirebaseFirestore) {

    private val workoutsCollection = db.collection("workouts")

    suspend fun saveNewWorkout(currentUserId: String, workout: Workout, exercises: List<Exercise>) : String {
        val newWorkoutRef = workoutsCollection.document()
        val workoutId = newWorkoutRef.id

        val batch = db.batch()

        // create the workout doc in the collection
        val workoutToSave = workout.copy(id = workoutId, userId = currentUserId)
        batch.set(newWorkoutRef, workoutToSave)

        // exercises subcollection
        exercises.forEach { exercise ->
            val newExerciseRef = newWorkoutRef.collection("exercises").document()
            val exerciseToSave = exercise.copy(id = newExerciseRef.id)
            batch.set(newExerciseRef, exerciseToSave)
        }

        batch.commit().await()
        return workoutId
    }

    suspend fun deleteWorkout(workoutId: String) {
        val exercisesQuery = workoutsCollection
            .document(workoutId)
            .collection("exercises")
            .limit(50)

        val exerciseDocs = exercisesQuery.get().await()
        val batch = db.batch()

        exerciseDocs.documents.forEach { doc ->
            batch.delete(doc.reference)
        }
        batch.commit().await()

        workoutsCollection.document(workoutId).delete().await()
    }
}