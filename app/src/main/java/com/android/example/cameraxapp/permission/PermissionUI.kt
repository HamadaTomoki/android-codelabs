package com.android.example.cameraxapp.permission

import PermissionAction
import android.content.Context
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import com.android.example.cameraxapp.util.Common

@Composable
fun PermissionUI(
    context: Context,
    permissions: Array<String>,
    permissionAction: (PermissionAction) -> Unit,
    showSnackbar: suspend () -> SnackbarResult
) {

    val permissionGranted = Common.checkIfPermissionGranted(context, *permissions)

    if (permissionGranted) {
        Log.d(TAG, "Permission already granted exiting..")
        permissionAction(PermissionAction.OnPermissionGranted)
        return
    }

    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestMultiplePermissions()) { permissionMap ->
            permissionMap.entries.forEach {
                val isGranted = it.value
                if (isGranted) {
                    Log.d(TAG, "Permission provided by user.")
                    permissionAction(PermissionAction.OnPermissionGranted)
                } else {
                    Log.d(TAG, "Permission denied by user.")
                    permissionAction(PermissionAction.OnPermissionDenied)
                }
            }
        }

    val showPermissionRationale = Common.shouldShowPermissionRationale(context, *permissions)

    LaunchedEffect(showPermissionRationale) {
        if (showPermissionRationale) {
            Log.d(TAG, "Showing permission rationale for $permissions")
            when (showSnackbar()) {
                SnackbarResult.Dismissed -> {
                    Log.d(TAG, "User dissmissed permission rationale for $permissions")
                    permissionAction(PermissionAction.OnPermissionDenied)
                }
                SnackbarResult.ActionPerformed -> {
                    Log.d(TAG, "User granted permission for $permissions rationale. Launching permission request..")
                    launcher.launch(permissions)
                }
            }
        } else {
            Log.d(TAG, "Requesting permission for $permissions again")
            launcher.launch(permissions)
        }
    }
}


val TAG = "PermissionUI"
