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
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import hku.cs.comp3330_musclemonster.dashboard.DashboardActivity
import hku.cs.comp3330_musclemonster.R
import hku.cs.comp3330_musclemonster.utils.Constants
import java.util.Date

class FeedActivity : AppCompatActivity() {

    private lateinit var rv: RecyclerView
    private lateinit var btnCloseFeed: ImageButton

    private lateinit var btnNavPost: Button
    private lateinit var btnMyPosts: Button
    private lateinit var btnAddFriends: Button
    private lateinit var btnFeed: Button

    private val db = FirebaseFirestore.getInstance()
    private val items = ArrayList<FeedPost>()
    private lateinit var adapter: FeedAdapter
    private var username: String = "guest"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)

        username = intent.getStringExtra(Constants.INTENT_ARG_USERNAME) ?: "guest"

        rv = findViewById(R.id.rvFeed)
        btnCloseFeed = findViewById(R.id.btnCloseFeed)

        btnNavPost    = findViewById(R.id.btnNavPost)
        btnMyPosts    = findViewById(R.id.btnMyPosts)
        btnAddFriends = findViewById(R.id.btnAddFriends)
        btnFeed       = findViewById(R.id.btnFeed)

        rv.layoutManager = LinearLayoutManager(this)
        adapter = FeedAdapter(items) { post ->
            val ref = post.ref ?: return@FeedAdapter
            ref.update("likes", FieldValue.increment(1))
                .addOnSuccessListener {
                    post.likes += 1
                    adapter.notifyItemChanged(items.indexOf(post))
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to like: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
        rv.adapter = adapter

        btnCloseFeed.setOnClickListener {
            val i = Intent(this, DashboardActivity::class.java)
            i.putExtra(Constants.INTENT_ARG_USERNAME, username)
            startActivity(i)
            finish()
        }

        btnFeed.isEnabled = false
        btnNavPost.setOnClickListener {
            val i = Intent(this, PostActivity::class.java)
            i.putExtra(Constants.INTENT_ARG_USERNAME, username)
            startActivity(i)
        }
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

        loadFeed()
    }

    private fun loadFeed() {
        items.clear()

        // 1) Read your friend list: users/{username}/friends/{friendName}
        db.collection("users")
            .document(username)
            .collection("friends")
            .get()
            .addOnSuccessListener { fs ->
                val friendIds = fs.documents.map { it.id } // each doc id is the friend's username

                if (friendIds.isEmpty()) {
                    adapter.notifyDataSetChanged()
                    Toast.makeText(this, "No friends yet — add some to see a feed.", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                var remaining = friendIds.size

                // 2) For each friend, fetch their posts
                friendIds.forEach { friend ->
                    db.collection("users")
                        .document(friend)
                        .collection("posts")
                        .get()
                        .addOnSuccessListener { qs ->
                            for (doc in qs.documents) {
                                val text  = doc.getString("text") ?: ""
                                val likes = doc.getLong("likes") ?: 0L
                                val ts    = doc.getTimestamp("createdAt")?.toDate() ?: Date(0)

                                items.add(
                                    FeedPost(
                                        author = friend,
                                        text = text,
                                        likes = likes,
                                        createdAt = ts,
                                        ref = doc.reference
                                    )
                                )
                            }
                        }
                        .addOnFailureListener { /* ignore individual friend failure */ }
                        .addOnCompleteListener {
                            remaining--
                            if (remaining == 0) {
                                // 3) Merge & sort newest → oldest
                                items.sortByDescending { it.createdAt }
                                adapter.notifyDataSetChanged()
                            }
                        }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to load friends: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    data class FeedPost(
        val author: String = "",
        val text: String = "",
        var likes: Long = 0L,
        val createdAt: Date = Date(0),           // <-- changed to Date
        val ref: DocumentReference? = null
    )

    class FeedAdapter(
        private val data: List<FeedPost>,
        private val onLike: (FeedPost) -> Unit
    ) : RecyclerView.Adapter<FeedAdapter.VH>() {

        class VH(v: View) : RecyclerView.ViewHolder(v) {
            val tvAuthor: TextView = v.findViewById(R.id.tvPostAuthor)
            val tvText: TextView   = v.findViewById(R.id.tvPostText)
            val tvLikes: TextView  = v.findViewById(R.id.tvLikes)
            val btnLike: ImageButton = v.findViewById(R.id.btnLike)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_feed_post, parent, false)
            return VH(v)
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            val item = data[position]
            holder.tvAuthor.text = item.author
            holder.tvText.text   = item.text
            holder.tvLikes.text  = item.likes.toString()
            holder.btnLike.setOnClickListener { onLike(item) }
        }

        override fun getItemCount(): Int = data.size
    }
}
