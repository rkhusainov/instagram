package com.github.rkhusainov.instagram


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class LikesFragment : Fragment() {

    companion object {
        fun newInstance(): LikesFragment = LikesFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_likes, container, false)
    }
}
