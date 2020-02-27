package com.github.rkhusainov.instagram.view

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.rkhusainov.instagram.R
import com.github.rkhusainov.instagram.model.FeedPost
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.feed_item.view.*
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

    private lateinit var callbackListener: CallbackListener
    private lateinit var itemListener: MenuItemListener

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: StorageReference

    private lateinit var feedAdapter: FeedAdapter

    companion object {
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is CallbackListener) {
            callbackListener = context
        }

        if (context is MenuItemListener) {
            itemListener = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        itemListener.menuItemCallback(0)
        Log.d(TAG, "onCreateView: 0")

        firebaseInit()

        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        feed_recycler.layoutManager = LinearLayoutManager(context)

        database.child("feed-posts").child(auth.currentUser!!.uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(data: DataSnapshot) {
                    val posts = data.children.map { it.getValue(FeedPost::class.java)!! }
                    Log.d(TAG, "feedPosts: ${posts.joinToString("\n", "\n")}")

                    feedAdapter = FeedAdapter(posts)

                    if (feed_recycler != null) {
                        feed_recycler.adapter = feedAdapter
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(ContentValues.TAG, "onCancelled: ", error.toException())
                }
            })
    }

    private fun firebaseInit() {
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        storage = FirebaseStorage.getInstance().reference
    }
}

interface CallbackListener {
    fun signOut()
}

class FeedAdapter(private val posts: List<FeedPost>) :
    RecyclerView.Adapter<FeedAdapter.FeedViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.feed_item, parent, false)
        return FeedViewHolder(view)
    }

    override fun getItemCount(): Int = posts.size

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        holder.view.post_image.loadImage(posts[position].image)
    }

    class FeedViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    private fun ImageView.loadImage(image: String) {
        Glide.with(this).load(image).centerCrop().into(this)
    }
}