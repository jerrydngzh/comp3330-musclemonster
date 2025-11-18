package hku.cs.comp3330_musclemonster.social

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import hku.cs.comp3330_musclemonster.DashboardActivity
import hku.cs.comp3330_musclemonster.R

class MyPostsActivity : AppCompatActivity() {

    private lateinit var rv: RecyclerView
    private lateinit var btnCloseMyPosts: ImageButton
    private lateinit var btnNavPost: Button
    private lateinit var btnMyPosts: Button
    private lateinit var btnAddFriends: Button
    private lateinit var btnFeed: Button

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val items = ArrayList<Post>()
    private lateinit var adapter: PostAdapter
    private var username: String = "guest"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_posts)

        rv = findViewById(R.id.rvMyPosts)
        rv.layoutManager = LinearLayoutManager(this)
        rv.setHasFixedSize(false) // allow variable-height items to scroll smoothly

        username = intent.getStringExtra("username") ?: "guest"

        btnCloseMyPosts = findViewById(R.id.btnCloseMyPosts)
        btnNavPost = findViewById(R.id.btnNavPost)
        btnMyPosts = findViewById(R.id.btnMyPosts)
        btnAddFriends = findViewById(R.id.btnAddFriends)
        btnFeed = findViewById(R.id.btnFeed)

        adapter = PostAdapter(items)
        rv.adapter = adapter

        btnCloseMyPosts.setOnClickListener {
            val i = Intent(this, DashboardActivity::class.java)
            i.putExtra("username", username)
            startActivity(i)
            finish()
        }

        // Bottom bar placeholders
        btnNavPost.setOnClickListener {
            val i = Intent(this, PostActivity::class.java)
            i.putExtra("username", username)
            startActivity(i)
        }
        btnMyPosts.isEnabled = false

        btnFeed.setOnClickListener {
            val i = Intent(this, FeedActivity::class.java)
            i.putExtra("username", username)
            startActivity(i)
        }

        btnAddFriends.setOnClickListener {
            val i = Intent(this, FriendsActivity::class.java)
            i.putExtra("username", username)
            startActivity(i)
        }



        loadPosts()
    }

    private fun loadPosts() {
        db.collection("users")
            .document(username)
            .collection("posts")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { qs ->
                items.clear()
                for (doc in qs.documents) {
                    val text = doc.getString("text") ?: ""
                    val likes = (doc.getLong("likes") ?: 0L)
                    val ts = doc.getTimestamp("createdAt") ?: Timestamp.now()
                    items.add(Post(text = text, likes = likes, createdAt = ts))
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to load: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    data class Post(
        val text: String = "",
        val likes: Long = 0L,
        val createdAt: Timestamp? = null
    )

    class PostAdapter(private val data: List<Post>) : RecyclerView.Adapter<PostAdapter.VH>() {
        class VH(v: View) : RecyclerView.ViewHolder(v) {
            val tvPostText: TextView = v.findViewById(R.id.tvPostText)
            val tvHeart: TextView = v.findViewById(R.id.tvHeart)
            val tvLikes: TextView = v.findViewById(R.id.tvLikes)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_my_post, parent, false)
            return VH(v)
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            val item = data[position]
            holder.tvPostText.text = item.text
            holder.tvLikes.text = item.likes.toString()
            // Heart is static for now (no like action required)
        }

        override fun getItemCount(): Int = data.size
    }
}
