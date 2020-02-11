package com.github.rkhusainov.instagram.view

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.github.rkhusainov.instagram.R
import com.github.rkhusainov.instagram.model.User
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_edit_profile.*

class EditProfileFragment : Fragment(), PasswordDialog.Listener {

    private lateinit var user: User
    private lateinit var pendingUser: User
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    companion object {
        fun newInstance(): EditProfileFragment {
            return EditProfileFragment()
        }
    }

    init {
        getDataFromFirebase()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_profile, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        close_image.setOnClickListener {
            fragmentManager?.popBackStack()
        }

        save_image.setOnClickListener {
            updateProfile()
        }
    }

    private fun getDataFromFirebase() {
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        database.child("users").child(auth.currentUser!!.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(data: DataSnapshot) {
                    user = data.getValue(User::class.java)!!
                    name_input.setText(user.name, TextView.BufferType.EDITABLE)
                    username_input.setText(user.username, TextView.BufferType.EDITABLE)
                    website_input.setText(user.website, TextView.BufferType.EDITABLE)
                    bio_input.setText(user.bio, TextView.BufferType.EDITABLE)
                    email_input.setText(user.email, TextView.BufferType.EDITABLE)
                    phone_input.setText(user.phone.toString(), TextView.BufferType.EDITABLE)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "onCancelled: ", error.toException())
                }
            })
    }

    private fun updateProfile() {
        pendingUser = User(
            name = name_input.text.toString(),
            username = username_input.text.toString(),
            website = website_input.text.toString(),
            bio = bio_input.text.toString(),
            email = email_input.text.toString(),
            phone = phone_input.text.toString().toLong()
        )

        val error = validate(pendingUser)
        if (error == null) {
            if (pendingUser.email == this.user.email) {
                updateUser(pendingUser)
            } else {
                val fm: FragmentManager = activity!!.supportFragmentManager
                val passwordDialog = PasswordDialog()
                passwordDialog.setTargetFragment(this, 0)
                passwordDialog.show(fm, "password_dialog")
//                PasswordDialog().show(fragmentManager!!, "password_dialog")

            }
        } else {
            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPasswordConfirm(password: String) {
        val credential = EmailAuthProvider.getCredential(user.email, password)
        auth.currentUser!!.reauthenticate(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                auth.currentUser!!.updateEmail(pendingUser.email).addOnCompleteListener{
                    if (it.isSuccessful) {
                        updateUser(pendingUser)
                    } else {
                Toast.makeText(context, it.exception!!.message, Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(context, it.exception!!.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUser(user: User) {
        val updatesMap = mutableMapOf<String, Any>()
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