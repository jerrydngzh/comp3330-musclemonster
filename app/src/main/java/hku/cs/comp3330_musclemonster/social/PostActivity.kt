package hku.cs.comp3330_musclemonster.social

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import hku.cs.comp3330_musclemonster.DashboardActivity
import hku.cs.comp3330_musclemonster.R
import hku.cs.comp3330_musclemonster.utils.Constants

class PostActivity : AppCompatActivity() {

    private lateinit var etPost: EditText
    private lateinit var btnSubmitPost: Button
    private lateinit var btnClose: ImageButton

    private lateinit var btnNavPost: Button
    private lateinit var btnMyPosts: Button
    private lateinit var btnAddFriends: Button
    private lateinit var btnFeed: Button

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        etPost = findViewById(R.id.etPost)
        btnSubmitPost = findViewById(R.id.btnSubmitPost)
        btnClose = findViewById(R.id.btnClose)

        btnNavPost = findViewById(R.id.btnNavPost)
        btnMyPosts = findViewById(R.id.btnMyPosts)
        btnAddFriends = findViewById(R.id.btnAddFriends)
        btnFeed = findViewById(R.id.btnFeed)

        val username = intent.getStringExtra(Constants.INTENT_ARG_USERNAME) ?: "guest"


        btnClose.setOnClickListener {
            val i = Intent(this, DashboardActivity::class.java)
            i.putExtra(Constants.INTENT_ARG_USERNAME, username)
            startActivity(i)
            finish()
        }

        btnSubmitPost.setOnClickListener {
            val text = etPost.text.toString().trim()
            if (text.isEmpty()) {
                Toast.makeText(this, "Write something first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val data = hashMapOf(
                "username" to username,
                "text" to text,
                "createdAt" to FieldValue.serverTimestamp(),
                "likes" to 0 // ✅ new like counter starts at 0
            )

            // Save post under users/{username}/posts/{autoId}
            db.collection("users")
                .document(username)
                .collection("posts")
                .add(data)
                .addOnSuccessListener {
                    Toast.makeText(this, "Posted!", Toast.LENGTH_SHORT).show()
                    etPost.text.clear()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        btnNavPost.isEnabled = false

        btnMyPosts.setOnClickListener {
            val i = Intent(this, MyPostsActivity::class.java)
            i.putExtra(Constants.INTENT_ARG_USERNAME, username)
            startActivity(i)
        }

        btnAddFriends.setOnClickListener {
            val i = Intent(this, FriendsActivity::class.java)
            i.putExtra(Constants.INTENT_ARG_USERNAME, username)
            startActivity(i)
        }

        btnFeed.setOnClickListener {
            val i = Intent(this, FeedActivity::class.java)
            i.putExtra(Constants.INTENT_ARG_USERNAME, username)
            startActivity(i)
        }
    }
}
