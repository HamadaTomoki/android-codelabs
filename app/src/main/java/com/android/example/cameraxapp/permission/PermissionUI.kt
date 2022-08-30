package com.android.example.cameraxapp.permission

import PermissionAction
import android.content.Context
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import com.android.example.cameraxapp.util.Common
import kotlinx.coroutines.launch

@Composable
fun PermissionUI(
    context: Context,
    permissions: Array<String>,
    permissionAction: (PermissionAction) -> Unit,
    snackbarHostState: SnackbarHostState
) {

    val permissionGranted = Common.checkIfPermissionGranted(context, *permissions)
    var snackbarResult = SnackbarResult.Dismissed
    val coroutineScope = rememberCoroutineScope()

    if (permissionGranted) {
        Log.d(TAG, "Permission already granted exiting..")
        permissionAction(PermissionAction.OnPermissionGranted)
        return
    } else {
        coroutineScope.launch {
            showSnackbar(snackbarHostState)
        }
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
            when (snackbarResult) {
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
            Log.d(TAG, "Requesting permission for ${permissions.joinToString(" and ")} again")
            launcher.launch(permissions)
        }
    }
}

suspend fun showSnackbar(snackbarHostState: SnackbarHostState) = snackbarHostState.showSnackbar(
    message = "In order to get the current location, we require the location permission to be granted.",
    actionLabel = "Grant Access",
    duration = SnackbarDuration.Long
)

val TAG = "PermissionUI"
