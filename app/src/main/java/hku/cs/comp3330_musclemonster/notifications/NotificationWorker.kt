package hku.cs.comp3330_musclemonster.notifications

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import hku.cs.comp3330_musclemonster.pet.PetManager

class NotificationWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        Log.d("NotificationWorker", "Worker is running...")

        val petManager = PetManager(applicationContext)
        val petName = petManager.getPetName()

        // Define list of messages
        val messages = listOf(
            "$petName is getting sleepy -- Let's get energized with a workout today!",
            "$petName is waiting to celebrate a workout with you!",
            "A little progress each day adds up to big results. You can do it!",
            "$petName misses you... Cheer them up with a quick workout!",
            "Don't forget your goals! Your workout buddy is here to cheer you on."
        )

        // Select a random message
        val randomMessage = messages.random()

        // Show the notification
        NotificationHelper.showDailyReminderNotification(
            applicationContext,
            "Muscle Monster",
            randomMessage
        )

        return Result.success()
    }
}
