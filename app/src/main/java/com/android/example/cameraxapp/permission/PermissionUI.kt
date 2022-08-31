@file:OptIn(ExperimentalPermissionsApi::class)

package com.android.example.cameraxapp.permission

import android.content.Context
import android.util.Log
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState

@Composable
fun PermissionUI(
    context: Context,
    multiplePermissions: MultiplePermissionsState,
    snackbarHostState: SnackbarHostState
) {

    val showPermissionRationale by remember { mutableStateOf(multiplePermissions.shouldShowRationale) }

    if (multiplePermissions.revokedPermissions.isEmpty()) {
        return
    }

    LaunchedEffect(showPermissionRationale) {
        val snackbarResult = showSnackbar(snackbarHostState)
        if (showPermissionRationale) {
            when (snackbarResult) {
                SnackbarResult.Dismissed -> {
                    Log.d(TAG, "User dismissed permission rationale.")
                }
                SnackbarResult.ActionPerformed -> {
                    Log.d(TAG, "User granted permission. Launching permission request..")
                    multiplePermissions.launchMultiplePermissionRequest()
                }
            }
        } else {
            Log.d(TAG, "Requesting permission again")
            multiplePermissions.launchMultiplePermissionRequest()
        }
    }
}

suspend fun showSnackbar(snackbarHostState: SnackbarHostState) = snackbarHostState.showSnackbar(
    message = "In order to get the current location, we require the location permission to be granted.",
    actionLabel = "Grant Access",
    duration = SnackbarDuration.Long
)

val TAG = "PermissionUI"
