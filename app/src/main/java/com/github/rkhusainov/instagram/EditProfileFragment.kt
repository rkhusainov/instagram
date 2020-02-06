package com.github.rkhusainov.instagram

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_edit_profile.*

class EditProfileFragment : Fragment() {
    companion object {
        fun newInstance(): EditProfileFragment {
            return EditProfileFragment()
        }
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
}