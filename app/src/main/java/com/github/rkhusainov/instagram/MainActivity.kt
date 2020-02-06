package com.github.rkhusainov.instagram

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.bottom_navigation_view.*

class MainActivity : AppCompatActivity() {

    private val TAG = "HomeActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
}
