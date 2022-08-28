package com.android.example.cameraxapp.util

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

private const val TAG = "Common"

class Common {
    companion object {

        fun checkIfPermissionGranted(context: Context, vararg permissions: String): Boolean = permissions.all { permission ->
            // checkSelfPermission 関数で特定の権限があるかどうかをチェックします。
            // ある場合は0(PackageManager.PERMISSION_GRANTED), ない場合は1(PackageManager.PERMISSION_DENIED)です。
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }


        fun shouldShowPermissionRationale(context: Context, vararg permissions: String): Boolean {
            val activity = context as Activity?
            if (activity == null) Log.d(TAG, "Activity is null")
            return permissions.all { permission ->
                // shouldShowRequestPermissionRationale 関数を使用して、ユーザーがリクエストを許可済みかどうかを確認します。
                // 以前ユーザーがリクエストを許可しなかった場合trueを返しますが、「今後表示しない」を選択していた場合はfalseを返します。
                ActivityCompat.shouldShowRequestPermissionRationale(
                    activity!!,
                    permission
                )
            }
        }
    }
}