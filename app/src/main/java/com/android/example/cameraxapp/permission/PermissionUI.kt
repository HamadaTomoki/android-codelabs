@file:OptIn(ExperimentalPermissionsApi::class)

package com.android.example.cameraxapp.permission

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState

@Composable
fun PermissionUI(
    multiplePermissions: MultiplePermissionsState,
    snackbarHostState: SnackbarHostState
) {

    val ctx = LocalContext.current

    // shouldShowRequestRationale 関数を使用して、ユーザーがリクエストを許可済みかどうかを確認します。
    // 以前ユーザーがリクエストを許可しなかった場合trueを返しますが、「今後表示しない」を選択していた場合はfalseを返します。
    val showPermissionRationale by remember { mutableStateOf(multiplePermissions.shouldShowRationale) }

    if (multiplePermissions.allPermissionsGranted) {
        return
    }

    LaunchedEffect(showPermissionRationale) {
        multiplePermissions.launchMultiplePermissionRequest()
        if (showPermissionRationale) {
            Log.d(TAG, "true")
            when (showSnackbar(snackbarHostState)) {
                SnackbarResult.Dismissed -> {
                    Log.d(TAG, "User dismissed permission rationale.")
                }
                SnackbarResult.ActionPerformed -> {
                    Log.d(TAG, "User granted permission. Launching permission request..")
                    multiplePermissions.launchMultiplePermissionRequest()
                }
            }
        } else {
            Log.d(TAG, "false")
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package: ${ctx.packageName}"))
            ctx.startActivity(intent)
        }
    }
}

suspend fun showSnackbar(snackbarHostState: SnackbarHostState) = snackbarHostState.showSnackbar(
    message = "このアクションを実行するには、許可が必要があります。",
    actionLabel = "許可する",
    duration = SnackbarDuration.Long
)

val TAG = "PermissionUI"
