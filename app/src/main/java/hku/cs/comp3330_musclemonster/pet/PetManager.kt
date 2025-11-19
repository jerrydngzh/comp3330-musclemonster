package hku.cs.comp3330_musclemonster.pet

import android.content.Context
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import hku.cs.comp3330_musclemonster.utils.Constants
import kotlinx.coroutines.tasks.await
import java.util.Date
import android.util.Log

class PetManager(private val context: Context) {

    private val db = FirebaseFirestore.getInstance()
    private val prefs = context.getSharedPreferences(Constants.SP, Context.MODE_PRIVATE)
    private val username: String
        get() = prefs.getString(Constants.INTENT_ARG_USERNAME, "guest") ?: "guest"

    private val petDocRef get() = db.collection("users").document(username).collection("pet").document("data")

    val defHappiness = 70
    val defPetName = "My pet"

    var happiness = defHappiness
        private set

    var petName: String = defPetName
        private set
    suspend fun loadPet() {
        try {
            val doc = petDocRef.get().await()

            // Pet exists, so check if user missed exercise for 1+ weeks
            if (doc.exists()) {
                happiness = (doc.getLong("happiness")?.toInt() ?: defHappiness).coerceIn(0, 100)
                petName = doc.getString("petName")?.takeIf { it.isNotEmpty() } ?: defPetName
                val lastWorkout = doc.getTimestamp("lastWorkout")
                val lastWeeksSubtracted = doc.getLong("lastWeeksSubtracted")?.toInt() ?: 0

                Log.d("PetManager", "Current happiness: $happiness")
                Log.d("PetManager", "Last workout: $lastWorkout")
                Log.d("PetManager", "Last weeks subtracted: $lastWeeksSubtracted")

                var weeksSinceWorkout = 0
                if (lastWorkout != null) {
                    val currentMs = System.currentTimeMillis()
                    val workoutMs = lastWorkout.toDate().time
                    weeksSinceWorkout = ((currentMs - workoutMs) / (1000L * 60 * 60 * 24 * 7)).toInt()
                }

                val weeksToSubtract = (weeksSinceWorkout - lastWeeksSubtracted).coerceAtLeast(0)
                Log.d("PetManager", "Weeks since workout: $weeksSinceWorkout, weeks to subtract: $weeksToSubtract")

                if (weeksToSubtract > 0) {
                    val newHappiness = (happiness - 15 * weeksToSubtract).coerceAtLeast(0)
                    val data = hashMapOf(
                        "happiness" to newHappiness,
                        "lastWeeksSubtracted" to weeksSinceWorkout
                    )
                    petDocRef.set(data, SetOptions.merge()).await()
                    happiness = newHappiness
                    Log.d("PetManager", "Updated lastWeeksSubtracted to $weeksSinceWorkout in Firestore")
                }
            } else {
                // Pet does not exist, so save it with default values
                val initialData = hashMapOf(
                    "happiness" to defHappiness,
                    "lastWeeksSubtracted" to 0,
                    "petName" to defPetName
                )
                petDocRef.set(initialData).await()
            }
        } catch (e: Exception) {
            happiness = defHappiness
        }
    }

    // Call when user logs a workout
    suspend fun workoutCompleted(workoutDateMillis: Long) {
        val workoutDate = Date(workoutDateMillis)
        val doc = petDocRef.get().await()
        var lastWorkout = Timestamp(workoutDate)
        var lastWeeksSubtracted = 0
        Log.d("PetManager", "Last workout: $lastWorkout")

        if (doc.exists()) {
            happiness = doc.getLong("happiness")?.toInt() ?: defHappiness
            lastWeeksSubtracted = doc.getLong("lastWeeksSubtracted")?.toInt() ?: 0

            val prevLastWorkout = doc.getTimestamp("lastWorkout") ?: lastWorkout
            // If new workout is before previous lastWorkout, keep lastWorkout as the previous
            if (prevLastWorkout.toDate().time > workoutDate.time) {
                lastWorkout = prevLastWorkout
                Log.d("PetManager", "Updated last workout to $lastWorkout")
            } else {
                // If new workout is after, then update lastWeeksSubtracted to count weeks after this new workout
                val prevMs = prevLastWorkout.toDate().time
                val newMs = workoutDate.time
                val weeksBetween = ((newMs - prevMs) / (1000L * 60 * 60 * 24 * 7)).toInt()

                Log.d("PetManager", "Weeks between workouts: $weeksBetween")

                if (weeksBetween > 0) {
                    lastWeeksSubtracted = (lastWeeksSubtracted - weeksBetween).coerceAtLeast(0)
                    Log.d("PetManager", "Adjusted lastWeeksSubtracted: $lastWeeksSubtracted")
                }
            }
        }

        // Update happiness
        happiness = (happiness + 20).coerceAtMost(100)

        Log.d("PetManager", "Updated happiness to $happiness")
        val data = hashMapOf<String, Any>(
            "happiness" to happiness,
            "lastWorkout" to lastWorkout,
            "lastWeeksSubtracted" to lastWeeksSubtracted
        )
        petDocRef.set(data, SetOptions.merge()).await()
    }

    suspend fun updatePetName(newName: String) {
        val cleanName = newName.trim().takeIf { it.isNotEmpty() } ?: defPetName
        petName = cleanName
        petDocRef.update("petName", cleanName).await()
    }
}