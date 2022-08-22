package com.gicproject.kcbsignatureapp.presentation

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.InitializationException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Composable
fun TestCameraScreen(
    navController: NavController,
    viewModel: MyViewModel,
    outputDirectory: File,
    executor: Executor,
) {
    val lensFacing = CameraSelector.LENS_FACING_FRONT
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val preview = androidx.camera.core.Preview.Builder().build()
    val previewView = remember { PreviewView(context) }
    val imageCapture: ImageCapture = remember { ImageCapture.Builder().build() }
    val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(lensFacing)
        .build()

    LaunchedEffect(key1 = lensFacing) {
        if (!viewModel.isCameraInitialized.value) {
            val cameraProvider = context.getCameraProvider(onError = {
                viewModel.setCameraInitialized(false)
            }, onSucess = {
                viewModel.setCameraInitialized(true)
            })
            cameraProvider.unbindAll()
            try {
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )
                Log.d("TAG", "TestCameraScreen: job complete")
            } catch(e: Exception) {
            }

        }
    }

    preview.setSurfaceProvider(previewView.surfaceProvider)


    Scaffold(
        modifier = Modifier.background(color = Color.White)
    ) { innerPadding ->
        Modifier.padding(innerPadding)
    }
    Column(
        modifier = Modifier.padding(16.dp), content = {
            Button(onClick = {
                takePhoto(
                    filenameFormat = "yyyy-MM-dd-HH-mm-ss-SSS",
                    imageCapture = imageCapture,
                    outputDirectory = outputDirectory,
                    executor = executor,
                    onImageCaptured = {
                        viewModel.setShowCamera(false);
                        viewModel.setPhotoUri(it)
                        viewModel.setShowPhoto(true)
                    },
                    onError = {
                        Log.e("kilo", "View error:", it)
                        viewModel.setCameraInitialized(false)
                    }
                )


            }, content = {
                Text(text = "Capture Image From Camera")
            })
            if (viewModel.shouldShowCamera.value) {
                AndroidView({ previewView }, modifier = Modifier.size(1.dp))
            }

            Spacer(modifier = Modifier.padding(16.dp))
            if (viewModel.shouldShowPhoto.value) {
                Image(
                    painter = rememberAsyncImagePainter(viewModel.photoUri.value),
                    contentDescription = null,
                    modifier = Modifier.size(400.dp)
                )
            }
        })
}


fun takePhoto(
    filenameFormat: String,
    imageCapture: ImageCapture,
    outputDirectory: File,
    executor: Executor,
    onImageCaptured: (Uri) -> Unit,
    onError: (ImageCaptureException) -> Unit,
) {

    val photoFile = File(
        outputDirectory,
        SimpleDateFormat(filenameFormat, Locale.US).format(System.currentTimeMillis()) + ".jpg"
    )

    val metadata = ImageCapture.Metadata().apply {
        isReversedHorizontal = true
    }
    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).setMetadata(metadata).build()

    imageCapture.takePicture(outputOptions, executor, object : ImageCapture.OnImageSavedCallback {
        override fun onError(exception: ImageCaptureException) {
            Log.e("kilo", "Take photo error:", exception)
            onError(exception)
        }

        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
            Log.e("kilo", "Take photo error: Success")
            val savedUri = Uri.fromFile(photoFile)
            onImageCaptured(savedUri)
        }
    })

}

suspend fun Context.getCameraProvider(
    onError: () -> Unit,
    onSucess: () -> Unit
): ProcessCameraProvider =
    suspendCoroutine { continuation ->
        ProcessCameraProvider.getInstance(this).also { cameraProvider ->
            try {
                val consume = continuation.resume(cameraProvider.get())
                cameraProvider.addListener({

                }, ContextCompat.getMainExecutor(this))
                onSucess()
            } catch (e: IllegalArgumentException) {
                Toast.makeText(this, e.message.toString(), Toast.LENGTH_LONG).show()
                onError()
            } catch (e: InitializationException) {
                Toast.makeText(this, e.message.toString(), Toast.LENGTH_LONG).show()
                onError()
            } catch (e: java.lang.Exception) {
                Toast.makeText(this, e.message.toString(), Toast.LENGTH_LONG).show()
                onError()
            }

        }
    }


@Composable
fun CameraView(
    outputDirectory: File,
    executor: Executor,
    onImageCaptured: (Uri) -> Unit,
    onError: (ImageCaptureException) -> Unit
) {
    // 3
    Box(contentAlignment = Alignment.BottomCenter, modifier = Modifier.fillMaxSize()) {

        IconButton(
            modifier = Modifier.padding(bottom = 20.dp),
            onClick = {
                Log.i("kilo", "ON CLICK")

            },
            content = {
                Button(
                    onClick = {

                    },
                ) {
                    Text("Capture Image")
                }
            }
        )
    }
}
