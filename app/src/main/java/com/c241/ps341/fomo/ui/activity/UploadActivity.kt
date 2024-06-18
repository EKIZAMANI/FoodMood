package com.c241.ps341.fomo.ui.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.c241.ps341.fomo.databinding.ActivityUploadBinding

@Suppress("DEPRECATION")
class UploadActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUploadBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)

        with(binding) {
            setContentView(root)

            ivBack.setOnClickListener {
                finish()
            }

            ivImage.setImageURI(Uri.parse(intent.getStringExtra("imageUri")))

            ivImage.setOnClickListener {
                if (ContextCompat.checkSelfPermission(
                        this@UploadActivity,
                        Manifest.permission.CAMERA
                    ) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(
                        this@UploadActivity,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this@UploadActivity,
                        arrayOf(
                            Manifest.permission.CAMERA,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ),
                        1
                    )
                } else {
                    showOptions()
                }
            }

            btnSubmit.setOnClickListener {
                Toast.makeText(
                    this@UploadActivity,
                    "The form has been uploaded",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
                startActivity(Intent(this@UploadActivity, DetailActivity::class.java))
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                showOptions()
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 1) {
            val imageUri: Uri? = data?.data
            if (imageUri != null) {
                val intent = Intent(applicationContext, UploadActivity::class.java)
                intent.putExtra("imageUri", imageUri.toString())
                startActivity(intent)
                finish()
            } else {
                val extras = data?.extras
                val imageBitmap = extras?.get("data") as? Bitmap
                if (imageBitmap != null) {
                    val path = MediaStore.Images.Media.insertImage(
                        applicationContext.contentResolver,
                        imageBitmap,
                        "Title",
                        null
                    )
                    val uri = Uri.parse(path)
                    val intent = Intent(applicationContext, UploadActivity::class.java)
                    intent.putExtra("imageUri", uri.toString())
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    private fun showOptions() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val chooser = Intent.createChooser(intent, "Pilih gambar")
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent, captureIntent))
        startActivityForResult(chooser, 1)
    }
}