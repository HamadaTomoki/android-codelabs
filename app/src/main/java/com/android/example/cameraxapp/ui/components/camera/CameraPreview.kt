package com.android.example.cameraxapp.ui.components.camera

import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.camera.core.CameraSelector
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.android.example.cameraxapp.utils.CodeScanner
import com.google.mlkit.vision.barcode.common.Barcode

@Composable
fun CameraPreview(
    lensFacing: Int = CameraSelector.LENS_FACING_BACK
) {
    val ctx = LocalContext.current
    val previewView = remember { PreviewView(ctx) }
    val codeScanner = CodeScanner(ctx as ComponentActivity, previewView) { codes: List<Barcode> ->
        codes.forEach {
            Toast.makeText(ctx, it.rawValue, Toast.LENGTH_LONG).show()
        }
    }
    LaunchedEffect(lensFacing) {
        codeScanner.cameraProvider()
    }
    AndroidView({ previewView }, modifier = Modifier.fillMaxSize(), {})
}