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

class SearchFragment : Fragment() {

    private lateinit var itemListener: MenuItemListener

    companion object {
        fun newInstance(): SearchFragment =
            SearchFragment()
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
        Log.d(ContentValues.TAG, "onCreateView: 1")
        itemListener.menuItemCallback(1)

        return inflater.inflate(R.layout.fragment_search, container, false)
    }
}
