package android.buiducha.translateapp.util

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

fun checkAudioRecordPermission(activity: Activity) {
    if (ActivityCompat.checkSelfPermission(
            activity,
            android.Manifest.permission.RECORD_AUDIO
        ) != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(android.Manifest.permission.RECORD_AUDIO),
            1
        )
    }
}

fun checkCameraPermission(activity: Activity): Boolean {
    if (ActivityCompat.checkSelfPermission(
        activity,
        android.Manifest.permission.CAMERA
    ) != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(android.Manifest.permission.CAMERA),
            2
        )
        return false
    }
    return true
}