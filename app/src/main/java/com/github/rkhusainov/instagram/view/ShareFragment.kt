package com.github.rkhusainov.instagram.view


import android.app.Activity.RESULT_OK
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.github.rkhusainov.instagram.R
import com.github.rkhusainov.instagram.model.FeedPost
import com.github.rkhusainov.instagram.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.fragment_share.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ShareFragment : Fragment() {

    private val TAKE_PICTURE_REQUEST_CODE = 1
    private lateinit var imageUri: Uri
    private lateinit var user: User
    val simpleDateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: StorageReference


    private lateinit var itemListener: MenuItemListener

    companion object {
        fun newInstance(): ShareFragment =
            ShareFragment()
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
        Log.d(ContentValues.TAG, "onCreateView: 2")
        itemListener.menuItemCallback(2)

        firebaseInit()

        val currentUser = database.child("users").child(auth.currentUser!!.uid)
        currentUser.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(data: DataSnapshot) {
                user = data.getValue(User::class.java)!!
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(ContentValues.TAG, "onCancelled: ", error.toException())
            }

        })
        return inflater.inflate(R.layout.fragment_share, container, false)
    }

    private fun firebaseInit() {
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        storage = FirebaseStorage.getInstance().reference
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        takeCameraPicture()

        back_image.setOnClickListener {
            fragmentManager?.popBackStack()
        }

        share_text.setOnClickListener {
            share()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == TAKE_PICTURE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Glide.with(this).load(imageUri).centerCrop().into(post_image)
            } else {
                fragmentManager?.popBackStack()
            }
        }
    }

    private fun takeCameraPicture() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(context!!.packageManager) != null) {
            val imageFile = createImageFile()
            imageUri = FileProvider.getUriForFile(
                context!!,
                "com.github.rkhusainov.instagram.fileprovider", imageFile
            )
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            startActivityForResult(intent, TAKE_PICTURE_REQUEST_CODE)
        }
    }

    private fun createImageFile(): File {
        val storageDir = context!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${simpleDateFormat.format(Date())}_",
            ".jpg",
            storageDir
        )
    }

    private fun share() {
        val uid = auth.currentUser!!.uid
        storage.child("users").child(uid).child("images").child(imageUri.lastPathSegment!!)
            .putFile(imageUri)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    it.result!!.metadata!!.reference!!.downloadUrl.addOnSuccessListener { url ->
                        database.child("images").child(uid).push()
                            .setValue(url.toString())
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    database.child("feed-posts").child(uid).push()
                                        .setValue(makeFeedPost(uid, url)).addOnCompleteListener {
                                            if (it.isSuccessful) {
                                                fragmentManager
                                                    ?.beginTransaction()
                                                    ?.replace(
                                                        R.id.fragment_container,
                                                        ProfileFragment.newInstance()
                                                    )
                                                    ?.commit()
                                            }
                                        }
                                } else {
                                    Toast.makeText(
                                        context, it.exception!!.message!!, Toast.LENGTH_SHORT
                                    ).show();
                                }
                            }

                    }
                } else {
                    Toast.makeText(context, it.exception!!.message!!, Toast.LENGTH_SHORT).show();
                }
            }
    }

    private fun makeFeedPost(
        uid: String,
        url: Uri
    ): FeedPost {
        return FeedPost(
            uid = uid,
            username = user.username,
            image = url.toString(),
            caption = caption_edit_text.toString(),
            photo = user.photo
        )
    }
}



