package hku.cs.comp3330_musclemonster

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_page)

        val etUsername = findViewById<TextInputEditText>(R.id.etUsername)
        val etPassword = findViewById<TextInputEditText>(R.id.etPassword)
        val btnLogin   = findViewById<MaterialButton>(R.id.btnLogin)
        val btnCreate  = findViewById<MaterialButton>(R.id.btnCreateAccount)

        val db = FirebaseFirestore.getInstance()

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

                        // ✅ Navigate directly to PostActivity instead of Dashboard
                        val intent = Intent(this, PostActivity::class.java)
                        intent.putExtra("username", username)
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
}
