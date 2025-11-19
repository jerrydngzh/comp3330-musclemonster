package hku.cs.comp3330_musclemonster.data

import com.google.firebase.firestore.FirebaseFirestore
import hku.cs.comp3330_musclemonster.workout.model.Exercise
import hku.cs.comp3330_musclemonster.workout.model.Workout
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class WorkoutRepository(private val db: FirebaseFirestore) {

    private val workoutsCollection = db.collection("workouts")

    suspend fun saveNewWorkout(currentUserId: String, workout: Workout, exercises: List<Exercise>) : String {
        val newWorkoutRef = workoutsCollection.document()
        val workoutId = newWorkoutRef.id

        val batch = db.batch()

        // create the workout doc in the collection
        val workoutToSave = workout.copy(id = workoutId, username = currentUserId)
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

    suspend fun getWorkoutsByUsername(username: String): List<Workout> {
        val workouts = workoutsCollection
            .whereEqualTo("username", username)
            .get()
            .await()
            .toObjects(Workout::class.java)

        return coroutineScope {
            workouts.map { workout ->
                async {
                    // Construct the reference to the subcollection using the workout ID
                    val exercisesSnapshot = db.collection("workouts")
                        .document(workout.id)
                        .collection("exercises")
                        .get()
                        .await()
                    val exercises = exercisesSnapshot.toObjects(Exercise::class.java)
                    return@async workout.copy(exercises = exercises.toMutableList())
                }
            }.awaitAll() // Wait for all parallel fetches to complete
        }
    }

    suspend fun getExercisesByWorkoutId(workoutId: String): List<Exercise> =
        suspendCoroutine { continuation ->
            db.collection("workouts")
                .document(workoutId)
                .collection("exercises")
                .get()
                .addOnSuccessListener { snapshot ->
                    val exercises = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Exercise::class.java)
                    }
                    continuation.resume(exercises)
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }

}