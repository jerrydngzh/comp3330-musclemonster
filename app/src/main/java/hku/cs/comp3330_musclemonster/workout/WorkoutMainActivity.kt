package hku.cs.comp3330_musclemonster.workout

import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import hku.cs.comp3330_musclemonster.databinding.ActivityWorkoutMainBinding
import hku.cs.comp3330_musclemonster.workout.fragments.WorkoutFragment

class WorkoutMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWorkoutMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWorkoutMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // initializes the workout fragment if there is preexisting data from prev lifecycle
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(binding.fragmentWorkoutContainer.id, WorkoutFragment())
                .commit()
        }

        // TODO: setup saving? / navigation in/out of the workout logging
    }
}