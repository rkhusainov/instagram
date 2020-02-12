package com.github.rkhusainov.instagram.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.github.rkhusainov.instagram.R
import com.github.rkhusainov.instagram.model.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_register_email.*
import kotlinx.android.synthetic.main.fragment_register_namepass.*

class RegisterActivity : AppCompatActivity(), EmailFragment.Listener, NamePassFragment.Listener {

    private val TAG = "RegisterActivity"

    private var email: String? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.register_container, EmailFragment())
                .commit()
        }
    }

    override fun onNext(email: String) {
        if (email.isNotEmpty()) {
            this.email = email
            auth.fetchSignInMethodsForEmail(email).addOnCompleteListener {
                if (it.isSuccessful) {
                    if (it.result?.signInMethods?.isEmpty() != false) {
                        supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.register_container, NamePassFragment())
                            .addToBackStack(null)
                            .commit()
                    } else {
                        showToast("This email already exist")
                    }
                } else {
                    showToast(it.exception!!.message!!)
                }
            }
        } else {
            showToast("Please enter email")
        }
    }

    override fun onRegister(fullName: String, password: String) {
        if (fullName.isNotEmpty() && password.isNotEmpty()) {
            val email = this.email
            if (email != null) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            val user = makeUser(fullName, email)
                            val reference = database.child("users").child(it.result!!.user!!.uid)
                            reference.setValue(user)
                                .addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        startHomeActivity()
                                    } else {
                                        unknownRegisterError(it)
                                    }
                                }
                        } else {
                            unknownRegisterError(it)
                        }
                    }
            } else {
                Log.e(TAG, "onRegister: ")
                showToast("Please enter email")
                supportFragmentManager.popBackStack()
            }
        } else {
            showToast("Please enter full name and password")
        }
    }

    private fun unknownRegisterError(it: Task<out Any>) {
        Log.e(TAG, "failed to create user profile ", it.exception);
        showToast("Something wrong happened. Please try again later")
    }

    private fun startHomeActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun makeUser(fullName: String, email: String):
            User {
        val username = makeUsername(fullName)
        return User(name = fullName, username = username, email = email)
    }

    private fun makeUsername(fullName: String) =
        fullName.toLowerCase().replace(" ", ".")
}

class EmailFragment : Fragment() {

    private lateinit var listener: Listener

    interface Listener {
        fun onNext(email: String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as Listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register_email, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        coordinateBtnAndInputs(
            next_btn,
            email_input
        )

        next_btn.setOnClickListener {
            val email = email_input.text.toString()
            listener.onNext(email)
        }
    }
}

class NamePassFragment : Fragment() {

    private lateinit var listener: Listener

    interface Listener {
        fun onRegister(fullName: String, password: String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as Listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register_namepass, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        coordinateBtnAndInputs(
            register_btn,
            full_name_input,
            password_input
        )

        register_btn.setOnClickListener {
            val fullName = full_name_input.text.toString()
            val password = password_input.text.toString()
            listener.onRegister(fullName, password)
        }
    }
}
