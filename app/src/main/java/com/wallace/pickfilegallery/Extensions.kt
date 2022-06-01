package com.wallace.pickfilegallery

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.StrictMode
import android.provider.MediaStore
import android.util.Base64
import androidx.activity.result.ActivityResultLauncher
import com.wallace.pickfilegallery.LogUtils.logD
import java.io.ByteArrayOutputStream
import java.lang.Exception

fun ActivityResultLauncher<Intent>.pickImageFromGallery(context: Context) {
    val builder = StrictMode.VmPolicy.Builder()
    StrictMode.setVmPolicy(builder.build())

    var chooserIntent: Intent? = null
    var intentList: MutableList<Intent?> = ArrayList()

    val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
    intentList = addIntentsToList(context, intentList, pickIntent)

    val takePhoto = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    intentList = addIntentsToList(context, intentList, takePhoto)

    if (intentList.size > 0) {
        chooserIntent = Intent.createChooser(
            intentList.removeAt(intentList.size - 1),
            "Selecione uma imagem"
        )
        chooserIntent.putExtra(
            Intent.EXTRA_INITIAL_INTENTS,
            intentList.toTypedArray()
        )
    }

    this.launch(Intent.createChooser(chooserIntent, "Selecione uma imagem"))
}

fun addIntentsToList(
    context: Context,
    list: MutableList<Intent?>,
    intent: Intent
): MutableList<Intent?> {
    val resInfo = context.packageManager.queryIntentActivities(intent, 0)
    for (resolveInfo in resInfo) {
        val packageName = resolveInfo.activityInfo.packageName
        val targetedIntent = Intent(intent)
        targetedIntent.setPackage(packageName)
        list.add(targetedIntent)
        logD("Intent: " + intent.action + " package: " + packageName)
    }
    return list
}