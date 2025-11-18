package hku.cs.comp3330_musclemonster.workout

import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import hku.cs.comp3330_musclemonster.databinding.ActivityWorkoutMainBinding
import hku.cs.comp3330_musclemonster.workout.fragments.WorkoutFragment

class WorkoutTrackerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWorkoutMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWorkoutMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val currentUser = intent.getStringExtra("user_id").toString()

        // init a new fragment if there wasn't a prev state
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(
                    binding.fragmentWorkoutContainer.id,
                    // note: editing a workout, supply a workoutId/pull locally saved data from some global viewmodel?
                    WorkoutFragment.newInstance(currentUser, workoutId = ""))
                .commit()
        }
    }
}