package com.github.rkhusainov.instagram.view

import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.rkhusainov.instagram.R
import com.github.rkhusainov.instagram.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment() {

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var user: User

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
