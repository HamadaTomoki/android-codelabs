@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class, ExperimentalPermissionsApi::class)

package com.android.example.cameraxapp

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.android.example.cameraxapp.ui.components.camera.CameraPreview
import com.android.example.cameraxapp.ui.components.permission.PermissionUI
import com.android.example.cameraxapp.ui.components.snackbar.DefaultSnackbar
import com.android.example.cameraxapp.ui.theme.CameraXAppTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState


// PreviewView カメラに映る映像をプレビューするために使用されるView
// CameraProvider カメラのライフサイクルをComposableにバインドするために使用されるシングルトン設計のプロバイダー
class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val multiplePermissions = rememberMultiplePermissionsState(
                listOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO,
                )
            )
            val snackbarHostState = remember { SnackbarHostState() }
            CameraXAppTheme {
                Scaffold(
                    snackbarHost = {
                        DefaultSnackbar(snackbarHostState = snackbarHostState, onAction = {
                            snackbarHostState.currentSnackbarData?.performAction()
                        })
                    },
                ) {
                    Box(Modifier.padding(it)) {
                        CameraPreview()
                        PermissionUI(
                            snackbarHostState = snackbarHostState,
                            multiplePermissions = multiplePermissions
                        )
                    }
                }
            }
        }
    }

}



