package hku.cs.comp3330_musclemonster

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class CreateAccountActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_account_page)

        val etUsername = findViewById<TextInputEditText>(R.id.etNewUsername)
        val etPassword = findViewById<TextInputEditText>(R.id.etNewPassword)
        val btnSubmit = findViewById<MaterialButton>(R.id.btnSubmitAccount)

        val db = Firebase.firestore

        btnSubmit.setOnClickListener {
            val username = etUsername.text?.toString()?.trim().orEmpty()
            val password = etPassword.text?.toString()?.trim().orEmpty()

            // 🔹 Empty input check
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Enter username and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 🔹 Password length check
            if (password.length < 8) {
                Toast.makeText(this, "Password must be at least 8 characters long", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 🔹 Check if username already exists
            db.collection("users").document(username).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        Toast.makeText(this, "Username already taken", Toast.LENGTH_SHORT).show()
                    } else {
                        // Username is free, create account
                        val data = mapOf(
                            "username" to username,
                            "password" to password
                        )

                        db.collection("users")
                            .document(username)
                            .set(data)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Account created", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error checking username: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
