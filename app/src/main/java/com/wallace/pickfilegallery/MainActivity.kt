package com.wallace.pickfilegallery

import android.Manifest.permission.*
import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast.LENGTH_LONG
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.android.material.snackbar.Snackbar
import com.wallace.pickfilegallery.LogUtils.logD
import com.wallace.pickfilegallery.databinding.ActivityMainBinding
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        treatResultIntent(result)
    }

    private val requestMultiplePermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        treatResultPermissions(permissions)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnPick.setOnClickListener {
            requestMultiplePermissions.launch(
                arrayOf(CAMERA, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE)
            )
        }
    }

    private fun treatResultIntent(result: ActivityResult?) {
        (result?.resultCode == Activity.RESULT_OK).run {
            result?.data?.let {
                val isFromGallery = it.data != null
                val isFromCamera = it.extras?.get("data") != null

                when {
                    isFromCamera -> treatImageFromCamera(it)
                    isFromGallery -> treatImageFromGallery(it)
                    else -> Snackbar.make(binding.ctlRoot, "Error to attach content.", LENGTH_LONG).show()
                }
            }
        }
    }

    private fun treatResultPermissions(permissions: MutableMap<String, Boolean>) {
        val hasCameraGranted = permissions.entries.find {
            it.key == CAMERA
        }?.value == true

        val hasStorageGranted = permissions.entries.find {
            it.key == WRITE_EXTERNAL_STORAGE || it.key == READ_EXTERNAL_STORAGE
        }?.value == true

        if (hasCameraGranted && hasStorageGranted) {
            resultLauncher.pickImageFromGallery(this)
        } else {
            Snackbar.make(
                binding.ctlRoot,
                "Permissions must be be accepted for a better experience",
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    private fun treatImageFromCamera(it: Intent) {
        val img = it.extras?.get("data") as Bitmap
        binding.imvContent.setImageBitmap(img)
    }

    private fun treatImageFromGallery(it: Intent) {
        val selectedImage: Uri? = it.data
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
        if (selectedImage != null) {
            val cursor: Cursor? = contentResolver.query(
                selectedImage,
                filePathColumn,
                null,
                null,
                null
            )
            if (cursor != null) {
                cursor.moveToFirst()
                val columnIndex: Int = cursor.getColumnIndex(filePathColumn[0])
                val picturePath: String = cursor.getString(columnIndex)

                val imageFile = FileProvider.getUriForFile(
                    this@MainActivity,
                    BuildConfig.PROVIDER_AUTHORITIES, File(
                        picturePath
                    )
                )
                val imageBitmap = if(Build.VERSION.SDK_INT < 28) {
                    MediaStore.Images.Media.getBitmap(
                        contentResolver,
                        imageFile
                    )
                } else {
                    val source = ImageDecoder.createSource(contentResolver, imageFile)
                    ImageDecoder.decodeBitmap(source)
                }

                val currentTimeMillis = System.currentTimeMillis()
                val fileName = "correspondente_doc_foto_$currentTimeMillis"
                val outStream = FileOutputStream(File(cacheDir, fileName))
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outStream)
                outStream.close()

                val bitmap = BitmapFactory.decodeFile(cacheDir.absolutePath.plus("/".plus(fileName)))
                binding.imvContent.setImageBitmap(bitmap)

                cursor.close()
            }
        }
    }
}