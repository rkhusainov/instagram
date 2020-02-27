package com.github.rkhusainov.instagram.view

import android.app.Activity.RESULT_OK
import android.content.ContentValues.TAG
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
import androidx.fragment.app.FragmentManager
import com.github.rkhusainov.instagram.R
import com.github.rkhusainov.instagram.model.User
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class EditProfileFragment : Fragment(), PasswordDialog.Listener {

    private val TAKE_PICTURE_REQUEST_CODE = 1
    val simpleDateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
    private lateinit var user: User
    private lateinit var pendingUser: User
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var storage: StorageReference
    private lateinit var imageUri: Uri

    companion object {
        fun newInstance(): EditProfileFragment {
            return EditProfileFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        firebaseInit()

        return inflater.inflate(R.layout.fragment_edit_profile, container, false)
    }

    private fun firebaseInit() {
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        storage = FirebaseStorage.getInstance().reference
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getDataFromFirebase()

        close_image.setOnClickListener {
            fragmentManager?.popBackStack()
        }

        save_image.setOnClickListener {
            updateProfile()
        }

        change_photo_text.setOnClickListener {
            takeCameraPicture()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == TAKE_PICTURE_REQUEST_CODE && resultCode == RESULT_OK) {
            val uid = auth.currentUser!!.uid
            storage.child("users/$uid/photo").putFile(imageUri).addOnCompleteListener {
                if (it.isSuccessful) {
                    val downloadTask = it.result!!.metadata!!.reference!!.downloadUrl
                    downloadTask.addOnSuccessListener { uri ->
                        database.child("users/$uid/photo").setValue(uri.toString())
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    user = user.copy(photo = uri.toString())

                                    if (this@EditProfileFragment.isVisible) {
                                        profile_image.loadUserPhoto(user.photo)
                                    }
                                } else {
                                    Toast.makeText(
                                        context,
                                        it.exception!!.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    }
                } else {
                    Toast.makeText(context, it.exception!!.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    private fun getDataFromFirebase() {
        database.child("users").child(auth.currentUser!!.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(data: DataSnapshot) {
                    user = data.getValue(User::class.java)!!
                    name_input.setText(user.name)
                    username_input.setText(user.username)
                    website_input.setText(user.website)
                    bio_input.setText(user.bio)
                    email_input.setText(user.email)
                    phone_input.setText(user.phone?.toString())

                    profile_image.loadUserPhoto(user.photo)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "onCancelled: ", error.toException())
                }
            })
    }

    private fun updateProfile() {
        pendingUser = readInputs()
        val error = validate(pendingUser)
        if (error == null) {
            if (pendingUser.email == this.user.email) {
                updateUser(pendingUser)
            } else {
                val fm: FragmentManager = activity!!.supportFragmentManager
                val passwordDialog = PasswordDialog()
                passwordDialog.setTargetFragment(this, 0)
                passwordDialog.show(fm, "password_dialog")
            }
        } else {
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
        }
    }

    private fun readInputs(): User {
        return User(
            name = name_input.text.toString(),
            username = username_input.text.toString(),
            email = email_input.text.toString(),
            website = website_input.text.toStringOrNull(),
            bio = bio_input.text.toStringOrNull(),
            phone = phone_input.text.toString().toLongOrNull()
        )
    }

    override fun onPasswordConfirm(password: String) {
        if (password.isNotEmpty()) {
            val credential = EmailAuthProvider.getCredential(user.email, password)
            auth.currentUser!!.reauthenticate(credential).addOnCompleteListener {
                if (it.isSuccessful) {
                    auth.currentUser!!.updateEmail(pendingUser.email).addOnCompleteListener {
                        if (it.isSuccessful) {
                            updateUser(pendingUser)
                        } else {
                            Toast.makeText(context, it.exception!!.message, Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                } else {
                    Toast.makeText(context, it.exception!!.message, Toast.LENGTH_SHORT).show()
                }
            }

        } else {
            Toast.makeText(context, "You should enter your password", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUser(user: User) {
        val updatesMap = mutableMapOf<String, Any?>()
        if (user.name != this.user.name) updatesMap["name"] = user.name
        if (user.username != this.user.username) updatesMap["username"] = user.username
        if (user.website != this.user.website) updatesMap["website"] = user.website
        if (user.bio != this.user.bio) updatesMap["bio"] = user.bio
        if (user.email != this.user.email) updatesMap["email"] = user.email
        if (user.phone != this.user.phone) updatesMap["phone"] = user.phone
        database.child("users").child(auth.currentUser!!.uid).updateChildren(updatesMap)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(context, "Profile saved", Toast.LENGTH_SHORT).show()
                    fragmentManager?.popBackStack()
                } else {
                    Toast.makeText(context, it.exception!!.message, Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun validate(user: User): String? =
        when {
            user.name.isEmpty() -> "Please enter name"
            user.username.isEmpty() -> "Please enter username"
            user.email.isEmpty() -> "Please enter email"
            else -> null
        }
}