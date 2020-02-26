package com.github.rkhusainov.instagram.view


import android.app.Activity.RESULT_OK
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.github.rkhusainov.instagram.R
import kotlinx.android.synthetic.main.fragment_share.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ShareFragment : Fragment() {

    private val TAKE_PICTURE_REQUEST_CODE = 1
    private lateinit var imageUri: Uri
    val simpleDateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)

    private lateinit var itemListener: MenuItemListener

    companion object {
        fun newInstance(): ShareFragment =
            ShareFragment()
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
        Log.d(ContentValues.TAG, "onCreateView: 2")
        itemListener.menuItemCallback(2)
        return inflater.inflate(R.layout.fragment_share, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        takeCameraPicture()

        back_image.setOnClickListener {
            fragmentManager?.popBackStack()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == TAKE_PICTURE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Glide.with(this).load(imageUri).centerCrop().into(post_image)
            } else {
                fragmentManager?.popBackStack()
            }
        }
    }

    private fun takeCameraPicture() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(context!!.packageManager) != null) {
            val imageFile = createImageFile()
            imageUri = FileProvider.getUriForFile(
                context!!,
                "com.github.rkhusainov.instagram.fileprovider", imageFile
            )
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            startActivityForResult(intent, TAKE_PICTURE_REQUEST_CODE)
        }
    }

    private fun createImageFile(): File {
        val storageDir = context!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${simpleDateFormat.format(Date())}_",
            ".jpg",
            storageDir
        )
    }
}
