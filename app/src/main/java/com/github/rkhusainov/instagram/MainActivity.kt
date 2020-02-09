package com.github.rkhusainov.instagram

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.bottom_navigation_view.*

class MainActivity : AppCompatActivity(), CallbackListener {

    private val TAG = "HomeActivity"
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        auth = FirebaseAuth.getInstance()
/*
        auth.signInWithEmailAndPassword("rudyiceage@gmail.com", "password")
            .addOnCompleteListener{
                if (it.isSuccessful) {
                    Log.d(TAG, "SingIn: success")
                } else {
                    Log.e(TAG,"signIn: failure", it.exception)
                }
            }
*/

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .addToBackStack(null)
                .replace(R.id.fragment_container, HomeFragment.newInstance())
                .commit()
        }

        bottom_navigation_view.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_item_home -> {
                    supportFragmentManager
                        .beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.fragment_container, HomeFragment.newInstance())
                        .commit()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.nav_item_search -> {
                    supportFragmentManager
                        .beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.fragment_container, SearchFragment.newInstance())
                        .commit()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.nav_item_share -> {
                    supportFragmentManager
                        .beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.fragment_container, ShareFragment.newInstance())
                        .commit()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.nav_item_likes -> {
                    supportFragmentManager
                        .beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.fragment_container, LikesFragment.newInstance())
                        .commit()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.nav_item_profile -> {
                    supportFragmentManager
                        .beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.fragment_container, ProfileFragment.newInstance())
                        .commit()
                    return@setOnNavigationItemSelectedListener true
                }
                else -> {
                    Log.e(TAG, "unknown nav item clicked $it")
                    return@setOnNavigationItemSelectedListener false
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    override fun signOut() {
        auth.signOut()

        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}
