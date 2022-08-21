package com.aifactory.imagine

import android.Manifest.permission.CAMERA
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.FileNotFoundException

class MainActivity : AppCompatActivity() {

    companion object {
        val GRANT_CAMERA_PERMISSION_REQUEST_CODE = 1;
        val CAMERA_REQUEST_CODE = 2;
        val GALLERY_REQUEST_CODE = 3;
    }

    private lateinit var openGallery: ImageView
    private lateinit var openCamera: ImageView
    private lateinit var imagePreview: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bindViews()
        configureActions()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when(requestCode) {
                CAMERA_REQUEST_CODE -> {
                    val photo: Bitmap? = data?.extras?.get("data") as? Bitmap
                    photo?.let {
                        imagePreview.setImageBitmap(it)
                    }
                }
                GALLERY_REQUEST_CODE -> {
                    try {
                        data?.data?.let {
                            imagePreview.setImageBitmap(
                                BitmapFactory.decodeStream(
                                    contentResolver.openInputStream(it)
                                )
                            )

                        }
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                        Toast.makeText(this, "Something went wrong.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            GRANT_CAMERA_PERMISSION_REQUEST_CODE -> {
                loadImageFromCamera()
            }
        }
    }

    private fun bindViews() {
        openGallery = findViewById(R.id.openGallery)
        openCamera = findViewById(R.id.openCamera)
        imagePreview = findViewById(R.id.imagePreview)
        openGallery.rootView.setBackgroundColor(Color.WHITE)
    }

    private fun configureActions() {
        Log.d("MainActivity", ContextCompat
            .checkSelfPermission(this, CAMERA).toString())
        openGallery.setOnClickListener {
            loadImageFromGallery()
        }
        openCamera.setOnClickListener {
            if (
                ContextCompat
                    .checkSelfPermission(this, CAMERA) == PackageManager.PERMISSION_DENIED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(CAMERA),
                    GRANT_CAMERA_PERMISSION_REQUEST_CODE
                )
            } else { loadImageFromCamera() }
        }
    }

    private fun loadImageFromCamera() {
        startActivityForResult(Intent(MediaStore.ACTION_IMAGE_CAPTURE), CAMERA_REQUEST_CODE)
    }

    private fun loadImageFromGallery() {
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, GALLERY_REQUEST_CODE)
    }

}