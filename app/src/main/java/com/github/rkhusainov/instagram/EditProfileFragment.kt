package com.github.rkhusainov.instagram

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.github.rkhusainov.instagram.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_edit_profile.*

class EditProfileFragment : Fragment() {

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
    }

    private fun getDataFromFirebase() {
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val database = FirebaseDatabase.getInstance().reference
        database.child("users").child(user!!.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(data: DataSnapshot) {
                    val user = data.getValue(User::class.java)
                    name_input.setText(user!!.name, TextView.BufferType.EDITABLE)
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
}