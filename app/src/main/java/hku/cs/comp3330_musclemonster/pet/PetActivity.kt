package hku.cs.comp3330_musclemonster.pet

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import hku.cs.comp3330_musclemonster.R
import hku.cs.comp3330_musclemonster.databinding.ActivityPetBinding
import kotlinx.coroutines.launch

class PetActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPetBinding
    private val pet by lazy { PetManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }

        binding.etPetName.setOnFocusChangeListener { _, hasFocus ->
            binding.etPetName.isCursorVisible = hasFocus

            if (!hasFocus) {
                val newName = binding.etPetName.text.toString().trim()

                if (newName.isNotEmpty()) {
                    lifecycleScope.launch {
                        pet.updatePetName(newName)
                    }
                }
            }
        }

        // Remove focus when clicking elsewhere
        binding.root.setOnClickListener {
            binding.etPetName.clearFocus()
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            pet.loadPet()
            updatePet()
        }
    }

    private fun updatePet() = with(binding) {
        etPetName.setText(pet.petName)
        progressMood.progress = pet.happiness
        tvProgressText.text = "${pet.happiness}%"

        ivPet.setImageResource(when (pet.happiness) {
            in 80..100 -> R.drawable.pet_very_happy
            in 60..79 -> R.drawable.pet_happy
            in 45..59 -> R.drawable.pet_neutral
            in 30..44 -> R.drawable.pet_sad
            else -> R.drawable.pet_very_sad
        })
        tvPetSpeech.text = when (pet.happiness) {
            in 80..100 -> listOf(
                "You're the best gym buddy!",
                "Let's crush another workout!",
                "We are UNSTOPPABLE!"
            ).random()

            in 60..79 -> listOf(
                "Feeling strong today!",
                "You can do this!",
                "Keep it up!"
            ).random()

            in 45..59 -> listOf(
                "I miss our workouts...",
                "Ready when you are!",
                "Let's keep going!"
            ).random()

            in 30..44 -> listOf(
                "I miss my training buddy",
                "Let's get moving soon!",
                "I'm getting sleepy... let's train!"
            ).random()

            else -> listOf(
                "I feel weak…",
                "I miss you",
                "I've been resting for too long... ready for a workout?"
            ).random()
        }
    }
}