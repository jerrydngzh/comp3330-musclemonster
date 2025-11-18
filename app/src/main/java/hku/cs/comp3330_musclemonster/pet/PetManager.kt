package hku.cs.comp3330_musclemonster.pet

import android.content.Context
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import hku.cs.comp3330_musclemonster.utils.Constants
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.util.concurrent.TimeUnit
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
            Log.d("PetManager", "Document exists: ${doc.exists()}")
            // Pet exists, so check if user missed exercise for 1+ weeks
            if (doc.exists()) {
                happiness = (doc.getLong("happiness")?.toInt() ?: defHappiness).coerceIn(0, 100)
                petName = doc.getString("petName")?.takeIf { it.isNotEmpty() } ?: defPetName
                val lastWorkout = doc.getTimestamp("lastWorkout")
                val lastWeeksSubtracted = doc.getLong("lastWeeksSubtracted")?.toInt() ?: 0

                Log.d("PetManager", "Current happiness: $happiness")
                Log.d("PetManager", "Last workout: $lastWorkout")
                Log.d("PetManager", "Last weeks subtracted: $lastWeeksSubtracted")

                val weeksSinceWorkout = if (lastWorkout != null) {
                    val msSinceWorkout = System.currentTimeMillis() - lastWorkout.toDate().time
                    TimeUnit.MILLISECONDS.toDays(msSinceWorkout).toInt() / 7
                } else 0

                val weeksToSubtract = (weeksSinceWorkout - lastWeeksSubtracted).coerceAtLeast(0)
                Log.d("PetManager", "Weeks since workout: $weeksSinceWorkout, weeks to subtract: $weeksToSubtract")

                if (weeksToSubtract > 0) {
                    val data = hashMapOf(
                        "happiness" to (happiness - 15 * weeksToSubtract).coerceAtLeast(0),
                        "lastWeeksSubtracted" to weeksSinceWorkout
                    )
                    petDocRef.set(data, SetOptions.merge()).await()
                    Log.d("PetManager", "Updated lastWeeksSubtracted to $weeksSinceWorkout in Firestore")
//                    happiness = (happiness - 15 * weeksToSubtract).coerceAtLeast(0)
//                    petDocRef.update(mapOf("lastWeeksSubtracted" to weeksSinceWorkout)).await()
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

        if (doc.exists()) {
            val prevLastWorkout = doc.getTimestamp("lastWorkout") ?: lastWorkout
            if (prevLastWorkout.toDate().time > workoutDate.time) {
                lastWorkout = prevLastWorkout
            }
        }

        // Update happiness
        happiness = (happiness + 20).coerceAtMost(100)

        val data = hashMapOf<String, Any>(
            "happiness" to happiness,
            "lastWorkout" to Timestamp(workoutDate)
        )
        petDocRef.set(data, SetOptions.merge()).await()
    }

    suspend fun updatePetName(newName: String) {
        val cleanName = newName.trim().takeIf { it.isNotEmpty() } ?: defPetName
        petName = cleanName
        petDocRef.update("petName", cleanName).await()
    }
}