package com.github.rkhusainov.instagram.view

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.rkhusainov.instagram.R
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {

    private lateinit var callbackListener: CallbackListener
    private lateinit var itemListener: MenuItemListener

    companion object {
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is CallbackListener) {
            callbackListener = context
        }

        if (context is MenuItemListener) {
            itemListener = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        itemListener.menuItemCallback(0)
        Log.d(TAG, "onCreateView: 0")
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sign_out_text.setOnClickListener {
            callbackListener.signOut()
        }
    }
}

interface CallbackListener {
    fun signOut()
}
