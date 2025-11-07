package hku.cs.comp3330_musclemonster

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val workoutTrackerButton: Button = findViewById(R.id.btnOpenWorkoutLogs)
        workoutTrackerButton.setOnClickListener {
            val intent = Intent(this, WorkoutTrackerActivity::class.java)
            startActivity(intent)
        }

        val dashboardButton: Button = findViewById(R.id.btnOpenDashboard)
        dashboardButton.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
        }
    }
}
