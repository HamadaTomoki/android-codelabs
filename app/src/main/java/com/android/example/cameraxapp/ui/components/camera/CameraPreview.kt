package com.android.example.cameraxapp.ui.components.camera

import android.app.SearchManager
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.camera.core.CameraSelector
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    var keyword by remember { mutableStateOf("") }
    val codeScanner = CodeScanner(ctx as ComponentActivity, previewView) { codes: List<Barcode> ->
        codes.forEach {
            val searchValue = it.rawValue
            if (keyword != searchValue) {
                val intent = Intent(Intent.ACTION_WEB_SEARCH).apply {
                    putExtra(SearchManager.QUERY, searchValue)
                }
                ctx.startActivity(intent)
            }
            keyword = it.rawValue.toString()
        }
    }
    LaunchedEffect(lensFacing) {
        codeScanner.cameraProvider()
    }
    AndroidView({ previewView }, modifier = Modifier.fillMaxSize(), {})
}