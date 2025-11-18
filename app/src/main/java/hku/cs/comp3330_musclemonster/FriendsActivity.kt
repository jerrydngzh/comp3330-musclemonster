package hku.cs.comp3330_musclemonster

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class FriendsActivity : AppCompatActivity() {

    private lateinit var etFriendUsername: EditText
    private lateinit var btnAddFriendConfirm: Button
    private lateinit var btnCloseFriends: ImageButton

    private lateinit var btnNavPost: Button
    private lateinit var btnMyPosts: Button
    private lateinit var btnAddFriends: Button
    private lateinit var btnFeed: Button

    private val db = FirebaseFirestore.getInstance()
    private var username: String = "guest"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends)

        username = intent.getStringExtra("username") ?: "guest"

        etFriendUsername    = findViewById(R.id.etFriendUsername)
        btnAddFriendConfirm = findViewById(R.id.btnAddFriendConfirm)
        btnCloseFriends     = findViewById(R.id.btnCloseFriends)

        btnNavPost    = findViewById(R.id.btnNavPost)
        btnMyPosts    = findViewById(R.id.btnMyPosts)
        btnAddFriends = findViewById(R.id.btnAddFriends)
        btnFeed       = findViewById(R.id.btnFeed)

        // We are on Friends screen
        btnAddFriends.isEnabled = false

        btnCloseFriends.setOnClickListener {
            val i = Intent(this, DashboardActivity::class.java)
            i.putExtra("username", username)
            startActivity(i)
            finish()
        }

        btnAddFriendConfirm.setOnClickListener {
            val friendName = etFriendUsername.text.toString().trim()
            if (friendName.isEmpty()) {
                Toast.makeText(this, "Enter a username", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (friendName == username) {
                Toast.makeText(this, "You can’t add yourself", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 1) Ensure target user exists
            db.collection("users").document(friendName).get()
                .addOnSuccessListener { doc ->
                    if (!doc.exists()) {
                        Toast.makeText(this, "User does not exist", Toast.LENGTH_SHORT).show()
                        return@addOnSuccessListener
                    }

                    // 2) Prevent duplicates
                    val friendDocRef = db.collection("users")
                        .document(username)
                        .collection("friends")
                        .document(friendName)

                    friendDocRef.get()
                        .addOnSuccessListener { existing ->
                            if (existing.exists()) {
                                Toast.makeText(this, "Already added as friend", Toast.LENGTH_SHORT).show()
                                return@addOnSuccessListener
                            }

                            // 3) Add friend
                            val friendData = mapOf("username" to friendName)
                            friendDocRef.set(friendData)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Friend added!", Toast.LENGTH_SHORT).show()
                                    etFriendUsername.text.clear()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(this, "Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        // Bottom nav
        btnNavPost.setOnClickListener {
            val i = Intent(this, PostActivity::class.java)
            i.putExtra("username", username)
            startActivity(i)
        }
        btnMyPosts.setOnClickListener {
            val i = Intent(this, MyPostsActivity::class.java)
            i.putExtra("username", username)
            startActivity(i)
        }
        btnFeed.setOnClickListener {
            val i = Intent(this, FeedActivity::class.java)
            i.putExtra("username", username)
            startActivity(i)
        }

    }
}
