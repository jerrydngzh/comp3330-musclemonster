package hku.cs.comp3330_musclemonster.pet

import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import hku.cs.comp3330_musclemonster.pet.PetManager
import hku.cs.comp3330_musclemonster.R
import hku.cs.comp3330_musclemonster.databinding.ActivityPetBinding
import kotlinx.coroutines.launch
import android.content.res.ColorStateList

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

        // Clicking outside removes focus + hides keyboard
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

//        if (pet.triggerSparkle) {
//            progressMood.progressTintList = android.graphics.ColorStateList.valueOf(0xFFFFD700.toInt())
//            ivPet.startAnimation(AnimationUtils.loadAnimation(this@PetActivity, R.anim.sparkle))
//            progressMood.postDelayed({
//                progressMood.progressTintList = android.graphics.ColorStateList.valueOf(0xFF4CAF50.toInt())
//                pet.consumeSparkle()
//            }, 1200)
//        }
    }
}