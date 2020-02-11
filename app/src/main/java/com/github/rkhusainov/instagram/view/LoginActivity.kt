package com.github.rkhusainov.instagram.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.github.rkhusainov.instagram.R
import com.github.rkhusainov.instagram.coordinateBtnAndInputs
import com.github.rkhusainov.instagram.showToast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener

class LoginActivity : AppCompatActivity(), KeyboardVisibilityEventListener {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        KeyboardVisibilityEvent.setEventListener(this, this)
        coordinateBtnAndInputs(login_btn, email_input, password_input)
        auth = FirebaseAuth.getInstance()

        login_btn.setOnClickListener {
            val email = email_input.text.toString()
            val password = password_input.text.toString()
            if (validate(email, password)) {
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                }
            } else {
                showToast("Please enter email and password")
            }
        }

        create_account_text.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }


    override fun onVisibilityChanged(isKeyboardOpen: Boolean) {
        if (isKeyboardOpen) {
            create_account_text.visibility = View.GONE
        } else {
            create_account_text.visibility = View.VISIBLE
        }
    }

    private fun validate(email: String, password: String) =
        email.isNotEmpty() && password.isNotEmpty()
}
