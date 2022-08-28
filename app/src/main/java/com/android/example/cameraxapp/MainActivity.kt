package com.android.example.cameraxapp

import PermissionAction
import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.android.example.cameraxapp.permission.PermissionUI
import com.android.example.cameraxapp.ui.theme.CameraXAppTheme


// PreviewView カメラに映る映像をプレビューするために使用されるView
// CameraProvider カメラのライフサイクルをComposableにバインドするために使用されるシングルトン設計のプロバイダー

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val (permission, permissionAction) = remember { mutableStateOf<PermissionAction>(PermissionAction.OnPermissionDenied) }
            val context = LocalContext.current
            val snackbarHostState = remember { SnackbarHostState() }
            CameraXAppTheme {
                Box {
                    PermissionUI(
                        context = context,
                        permissions = arrayOf(
                            Manifest.permission.CAMERA,
                            Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ),
                        permissionRationale = "In order to get the current location, we require the location permission to be granted.",
                        snackbarHostState = snackbarHostState,
                        permissionAction = permissionAction
                    )
                    Button(onClick = {}) {
                        Text(text = "TAKE PHOTO")
                    }
                    Button(onClick = {}) {
                        Text(text = "START CAPTURE")
                    }
                }
            }
        }
    }
}

@Composable
fun CameraView(
    onImageCaptured: () -> Boolean,
    onError: (ImageCaptureException) -> Unit
) {
    val context = LocalContext.current
    Box {
    }
}

@Composable
private fun CameraPreview(
    imageCapture: ImageCapture,
    lensFacing: CameraSelector.LensFacing,
    cameraUIAction: (CameraUIAction) -> Unit
) {
    val context = LocalContext.current
    val previewView = remember { PreviewView(context) }
    AndroidView({ previewView }, modifier = Modifier.fillMaxSize()) {
    }
}

sealed class CameraUIAction {
    object OnCameraClick : CameraUIAction()
    object OnGalleryViewClick : CameraUIAction()
    object OnSwitchCameraClick : CameraUIAction()
}