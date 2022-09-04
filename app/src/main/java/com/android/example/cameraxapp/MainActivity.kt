@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)

package com.android.example.cameraxapp

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.android.example.cameraxapp.permission.PermissionUI
import com.android.example.cameraxapp.ui.components.snackbar.DefaultSnackbar
import com.android.example.cameraxapp.ui.theme.CameraXAppTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


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

@Composable
private fun CameraPreview(
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


sealed class CameraUIAction {
    object OnCameraClick : CameraUIAction()
    object OnGalleryViewClick : CameraUIAction()
    object OnSwitchCameraClick : CameraUIAction()
}


class CodeScanner(
    private val activity: ComponentActivity,
    private val previewView: PreviewView,
    callback: (List<Barcode>) -> Unit
) {
    private val workerExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    private val scanner: BarcodeScanner = BarcodeScanning.getClient()
    private val analyzer: CodeAnalyzer = CodeAnalyzer(scanner, callback)

    init {
        activity.lifecycle.addObserver(
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_DESTROY) {
                    workerExecutor.shutdown()
                    scanner.close()
                }
            }
        )
    }

    suspend fun cameraProvider(): ProcessCameraProvider =
        suspendCoroutine { continuation ->
            ProcessCameraProvider.getInstance(activity).also { cameraProvider ->
                cameraProvider.addListener(
                    {
                        continuation.resume(cameraProvider.get())
                        val preview = Preview.Builder().build().also { it.setSurfaceProvider(previewView.surfaceProvider) }
                        val analysis = ImageAnalysis.Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()
                            .also {
                                it.setAnalyzer(workerExecutor, analyzer)
                            }
                        cameraProvider.get().also {
                            it.unbindAll()
                            it.bindToLifecycle(
                                activity, CameraSelector.DEFAULT_BACK_CAMERA, preview, analysis
                            )
                        }
                    }, ContextCompat.getMainExecutor(activity)
                )
            }
        }

}

class CodeAnalyzer(
    private val scanner: BarcodeScanner,
    private val callback: (List<Barcode>) -> Unit
) : ImageAnalysis.Analyzer {
    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val image = imageProxy.image
        if (image != null) {
            val inputImage = InputImage.fromMediaImage(image, imageProxy.imageInfo.rotationDegrees)
            scanner.process(inputImage)
                .addOnSuccessListener { callback(it) }
                .addOnCompleteListener { imageProxy.close() }
        } else {
            imageProxy.close()
        }
    }
}
