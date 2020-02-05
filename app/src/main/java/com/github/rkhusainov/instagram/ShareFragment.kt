package com.github.rkhusainov.instagram


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class ShareFragment : Fragment() {

    companion object {
        fun newInstance(): ShareFragment = ShareFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_share, container, false)
    }
}
