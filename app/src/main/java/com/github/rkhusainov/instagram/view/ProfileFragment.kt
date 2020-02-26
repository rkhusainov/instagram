package com.github.rkhusainov.instagram.view

import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.rkhusainov.instagram.R
import com.github.rkhusainov.instagram.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment() {

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var user: User
    private lateinit var imagesAdapter: ImagesAdapter

    private lateinit var itemListener: MenuItemListener

    companion object {
        fun newInstance(): ProfileFragment {
            return ProfileFragment()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is MenuItemListener) {
            itemListener = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(ContentValues.TAG, "onCreateView: 4")
        itemListener.menuItemCallback(4)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        images_recycler.layoutManager = GridLayoutManager(context, 3)

        database.child("images").child(auth.currentUser!!.uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(data: DataSnapshot) {
                    val images = data.children.map { it.getValue(String::class.java)!! }
                    imagesAdapter = ImagesAdapter(images + images + images)
                    if (images_recycler != null) {
                        images_recycler.adapter = imagesAdapter
                    }
                    imagesAdapter.updateRecycler()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(ContentValues.TAG, "onCancelled: ", error.toException())
                }

            })

        database.child("users").child(auth.currentUser!!.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(data: DataSnapshot) {
                    user = data.getValue(User::class.java)!!
                    profile_image.loadUserPhoto(user.photo)
                    username_text.text = user.username
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(ContentValues.TAG, "onCancelled: ", error.toException())
                }
            })

        edit_profile_button.setOnClickListener {
            fragmentManager
                ?.beginTransaction()
                ?.addToBackStack(null)
                ?.replace(R.id.fragment_container, EditProfileFragment.newInstance())
                ?.commit()
        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }
}

class ImagesAdapter(private val images: List<String>) :
    RecyclerView.Adapter<ImagesAdapter.ImagesViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagesViewHolder {
        val imageView = LayoutInflater.from(parent.context)
            .inflate(R.layout.image_item, parent, false) as ImageView
        return ImagesViewHolder(imageView)
    }

    fun updateRecycler() {
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = images.size

    override fun onBindViewHolder(holder: ImagesViewHolder, position: Int) {
        holder.image.loadImage(images[position])
    }

    private fun ImageView.loadImage(image: String) {
        Glide.with(this).load(image).centerCrop().into(this)
    }

    class ImagesViewHolder(val image: ImageView) : RecyclerView.ViewHolder(image) {

    }
}

class SquareImageView(context: Context, attributeSet: AttributeSet) :
    androidx.appcompat.widget.AppCompatImageView(context, attributeSet) {
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }
}