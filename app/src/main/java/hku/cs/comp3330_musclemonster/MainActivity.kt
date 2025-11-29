package hku.cs.comp3330_musclemonster

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore
import hku.cs.comp3330_musclemonster.dashboard.DashboardActivity
import hku.cs.comp3330_musclemonster.utils.Constants
import androidx.core.content.edit
import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import hku.cs.comp3330_musclemonster.notifications.NotificationHelper
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import hku.cs.comp3330_musclemonster.notifications.NotificationWorker
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Log.d("MainActivity", "Notification permission granted.")
            } else {
                Log.d("MainActivity", "Notification permission denied.")
            }
        }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_page)

        NotificationHelper.createNotificationChannel(this)
        askNotificationPermission()
        scheduleDailyReminder()

        val etUsername = findViewById<TextInputEditText>(R.id.etUsername)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val btnLogin   = findViewById<MaterialButton>(R.id.btnLogin)
        val btnCreate  = findViewById<MaterialButton>(R.id.btnCreateAccount)

        val db = FirebaseFirestore.getInstance()

        // clear existing SharedPreferences
        val sharedPreferences = getSharedPreferences(Constants.SP, MODE_PRIVATE)
        sharedPreferences.edit { clear() }

        // Go to Create Account page
        btnCreate.setOnClickListener {
            val intent = Intent(this, CreateAccountActivity::class.java)
            startActivity(intent)
        }

        // Login logic
        btnLogin.setOnClickListener {
            val username = etUsername.text?.toString()?.trim().orEmpty()
            val password = etPassword.text?.toString()?.trim().orEmpty()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Enter username and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            db.collection("users").document(username).get()
                .addOnSuccessListener { doc ->
                    if (!doc.exists()) {
                        Toast.makeText(this, "Account not found", Toast.LENGTH_SHORT).show()
                        return@addOnSuccessListener
                    }

                    val savedPassword = doc.getString("password") ?: ""
                    if (password == savedPassword) {
                        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()

                        // Navigate directly to PostActivity instead of Dashboard
                        // val intent = Intent(this, PostActivity::class.java)
                        // intent.putExtra("username", username)
                        val intent = Intent(this, DashboardActivity::class.java)
                        intent.putExtra(Constants.INTENT_ARG_USERNAME, username)
                        startActivity(intent)
                        finish() // optional, prevents back navigation to login
                    } else {
                        Toast.makeText(this, "Incorrect password", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Login failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun scheduleDailyReminder() {
        val periodicWorkRequest = PeriodicWorkRequestBuilder< NotificationWorker>(1, TimeUnit.DAYS)
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "DailyPetReminder", // A unique name for this job
            ExistingPeriodicWorkPolicy.KEEP, // If this job is already scheduled, do nothing.
            periodicWorkRequest
        )

        Log.d("MainActivity", "Daily reminder worker has been scheduled.")
    }
}
