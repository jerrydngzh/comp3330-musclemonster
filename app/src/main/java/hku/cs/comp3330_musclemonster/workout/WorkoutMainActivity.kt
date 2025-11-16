package hku.cs.comp3330_musclemonster.workout

import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import hku.cs.comp3330_musclemonster.databinding.ActivityWorkoutMainBinding

class WorkoutMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWorkoutMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWorkoutMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // TODO: init the fragment if no prev data

    }
}