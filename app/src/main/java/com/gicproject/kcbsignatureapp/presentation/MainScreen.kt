package com.gicproject.kcbsignatureapp.presentation


import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.gicproject.kcbsignatureapp.R
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import java.io.File
import java.util.concurrent.Executor


@Composable
fun MainScreen(
    navController: NavController,
    viewModel: MyViewModel,
    outputDirectory: File,
    executor: Executor,
) {

    val isRefreshing by viewModel.isRefreshingSetting.collectAsState()
    val state = viewModel.stateMain.value
    Scaffold(
    ) { innerPadding ->
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing),
            onRefresh = {
                Log.d("TAG", "SettingScreen: swipe refresh")
            },
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        MaterialTheme.colors.surface,
                    )
            ) {
                Modifier.padding(innerPadding)
                Row(
                    modifier = Modifier
                        .background(color = Color.White)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.Top
                ) {

                    /* WallClock(hour.value,
                         minute.value,
                         second.value,)*/
                    Spacer(modifier = Modifier.height(20.dp))
                    Image(
                        painter = painterResource(id = R.drawable.kcblogo),
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .width(210.dp)
                            .height(80.dp)
                            .padding(top = 20.dp),
                        contentDescription = "qrcode sample"
                    )
                }
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (state.civilIdPage) {
                        CivilIdPage(state.isLoadingCivilId, onClick = {
                            viewModel.emptyMainState()
                            viewModel.readData()
                        },onClickAutoDetect ={
                            viewModel.setAutoDetect(it)
                        }, isAutoDetect = viewModel.isAutoDetectCard.value)
                    }
                    if (state.fingerPrintPage) {
                        FingerPrintPage(onClickBack = {
                            viewModel.backToCivilIdPage()
                        }, viewModel = viewModel, outputDirectory, executor)
                    }
                    if (state.signaturePage) {
                        SignaturePadPage(
                            onClickBack = {
                                viewModel.backToFingerPrintPage()
                            },
                            viewModel = viewModel,
                        )
                    }

                    /*  Row(
                          horizontalArrangement = Arrangement.Center,
                          verticalAlignment = Alignment.CenterVertically,
                          modifier = Modifier
                              .fillMaxWidth()
                              .padding(top = 30.dp)
                      ) {
                          if (state.isLoadingSubmit) {
                              LinearProgressIndicator()
                          }
                      }*/
                }
            }
        }
    }
}

@Composable
fun FingerPrintPage(
    onClickBack: () -> Unit, viewModel: MyViewModel,
    outputDirectory: File,
    executor: Executor,
) {

    val civilIdText = viewModel.stateMain.value.civilidText
    val serialNoText = viewModel.stateMain.value.serialNoText
    val fullNameText = viewModel.stateMain.value.fullNameText
    val firstNameText = viewModel.stateMain.value.firstNameText
    val secondNameText = viewModel.stateMain.value.secondNameText
    val thirdNameText = viewModel.stateMain.value.thirdNameText
    val fullNameArText = viewModel.stateMain.value.fullNameArText
    val firstNameArText = viewModel.stateMain.value.firstNameArText
    val secondNameArText = viewModel.stateMain.value.secondNameArText
    val thirdNameArText = viewModel.stateMain.value.thirdNameArText
    val fullAddressText = viewModel.stateMain.value.fullAddressText
    val genderText = viewModel.stateMain.value.genderText
    val bloodGroupText = viewModel.stateMain.value.bloodGroupText
    val passportNoText = viewModel.stateMain.value.passportNoText
    val occupationText = viewModel.stateMain.value.occupationText
    val tel1Text = viewModel.stateMain.value.tel1Text
    val tel2Text = viewModel.stateMain.value.tel2Text
    val nationalityText = viewModel.stateMain.value.nationalityText
    val emailText = viewModel.stateMain.value.emailText
    val dobText = viewModel.stateMain.value.dobText
    val expiryDate = viewModel.stateMain.value.expiryText


    //camera
    val lensFacing = CameraSelector.LENS_FACING_FRONT
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val preview = androidx.camera.core.Preview.Builder().build()
    val previewView = remember { PreviewView(context) }
    val imageCapture: ImageCapture = remember { ImageCapture.Builder().build() }

    val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(lensFacing)
        .build()

    LaunchedEffect(key1 = true) {
        Log.d("TAG", "FingerPrintPage: true")
        //  if (!viewModel.isCameraInitialized.value) {
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
        } catch (e: Exception) {
        }

        //    }
    }

    preview.setSurfaceProvider(previewView.surfaceProvider)


    Row() {
        IconButton(onClick = onClickBack) {
            Icon(
                Icons.Filled.KeyboardArrowLeft,
                "back arrow",
                tint = MaterialTheme.colors.primary,
                modifier = Modifier
                    .size(100.dp, 100.dp)
            )
        }
        Spacer(modifier = Modifier.width(20.dp))
        Button(
            modifier = Modifier.padding(top = 20.dp),
            onClick = {
                viewModel.openSignaturePage()
            }
        ) {
            Text(text = "Capture Signature")
        }
    }



    Row(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .weight(2.0f)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column() {
                Column(
                    modifier = Modifier
                        .weight(1.0f)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(viewModel.fingerPrintUri.value),
                        contentDescription = "Image",
                        modifier = Modifier
                            .size(90.dp, 130.dp)
                            .border(2.dp, Color.LightGray, shape = RoundedCornerShape(10.dp))
                    )
                    Row() {
                        Button(
                            modifier = Modifier.padding(top = 20.dp),
                            onClick = {
                                viewModel.doAutoCapture()
                            }
                        ) {
                            Text(text = "Capture Finger Print")
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Button(
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
                            modifier = Modifier.padding(top = 20.dp),
                            onClick = {
                                viewModel.setFingerPrintUri(null)
                            }
                        ) {
                            Text(text = "Reset")
                        }

                    }

                    Text(
                        "Status: " + viewModel.statusFingerPrint.value,
                        color = if (viewModel.statusFingerPrint.value == "Captured OK") MaterialTheme.colors.primary else Color.Gray,
                        textAlign = TextAlign.Center,
                        fontSize = if (viewModel.statusFingerPrint.value == "Captured OK") 14.sp else 12.sp,
                        fontWeight = if (viewModel.statusFingerPrint.value == "Captured OK") FontWeight.Bold else FontWeight.Normal,
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(1.0f)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (viewModel.shouldShowCamera.value) {
                        //intialize camera
                        Log.d("TAG", "FingerPrintPage: true caleed ")
                        AndroidView(
                            { previewView }, modifier = Modifier
                                .size(230.dp, 130.dp)
                                .border(2.dp, Color.LightGray, shape = RoundedCornerShape(10.dp))
                        )
                    }
                    if (viewModel.shouldShowPhoto.value) {
                        Image(
                            painter = rememberAsyncImagePainter(viewModel.photoUri.value),
                            contentDescription = "Image",
                            modifier = Modifier
                                .size(230.dp, 130.dp)
                                .border(2.dp, Color.LightGray, shape = RoundedCornerShape(10.dp))
                        )
                    }
                    Row() {
                        Button(
                            modifier = Modifier.padding(top = 20.dp),
                            onClick = {
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
                            }
                        ) {
                            Text(text = "Capture Image")
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Button(
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
                            modifier = Modifier.padding(top = 20.dp),
                            onClick = {
                                viewModel.setShowCamera(true)
                                viewModel.setShowPhoto(false)
                                viewModel.setPhotoUri(null)
                            }
                        ) {
                            Text(text = "Reset")
                        }
                    }
                }
            }

        }
        Column(
            modifier = Modifier
                .weight(3.0f)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                OutlinedTextField(
                    readOnly = true,
                    value = civilIdText,
                    label = { Text("Civil ID") },
                    onValueChange = {
                        viewModel.onEvent(MyEvent.CivilIdChanged(it))
                    }
                )
                Spacer(modifier = Modifier.width(20.dp))
                OutlinedTextField(
                    readOnly = true,
                    value = expiryDate,
                    label = { Text("Expiry Date") },
                    onValueChange = {
                        viewModel.onEvent(MyEvent.ExpiryDateChanged(it))
                    }
                )
            }
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                value = fullNameText,
                label = { Text("Full Name English") },
                onValueChange = {
                    viewModel.onEvent(MyEvent.FullNameChanged(it))
                }
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                value = fullNameArText,
                label = { Text("Full Name Arabic") },
                onValueChange = {
                    viewModel.onEvent(MyEvent.FullNameArChanged(it))
                }
            )
            Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                OutlinedTextField(
                    readOnly = true,
                    value = nationalityText,
                    label = { Text("Nationality") },
                    onValueChange = {
                        viewModel.onEvent(MyEvent.NationalityChanged(it))
                    }
                )
                Spacer(modifier = Modifier.width(20.dp))
                OutlinedTextField(
                    readOnly = true,
                    value = dobText,
                    label = { Text("Date of Birth") },
                    onValueChange = {
                        viewModel.onEvent(MyEvent.DOBChanged(it))
                    }
                )
            }
            Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                OutlinedTextField(
                    readOnly = true,
                    value = genderText,
                    label = { Text("Gender") },
                    onValueChange = {
                        viewModel.onEvent(MyEvent.GenderChanged(it))
                    }
                )
                Spacer(modifier = Modifier.width(20.dp))
                OutlinedTextField(
                    readOnly = true,
                    value = emailText,
                    label = { Text("Email") },
                    onValueChange = {
                        viewModel.onEvent(MyEvent.EmailChanged(it))
                    }
                )
            }
            Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                OutlinedTextField(
                    readOnly = true,
                    value = tel1Text,
                    label = { Text("Tel 1") },
                    onValueChange = {
                        viewModel.onEvent(MyEvent.Tel1Changed(it))
                    }
                )
                Spacer(modifier = Modifier.width(20.dp))
                OutlinedTextField(
                    readOnly = true,
                    value = tel2Text,
                    label = { Text("Tel 2") },
                    onValueChange = {
                        viewModel.onEvent(MyEvent.Tel2Changed(it))
                    }
                )
            }
        }
    }
}


@Composable
fun CivilIdPage(loading: Boolean,isAutoDetect: Boolean, onClick: () -> Unit,onClickAutoDetect: (value: Boolean) -> Unit) {

    Row(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .weight(2.0f)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Auto Detect Card")
                Spacer(modifier = Modifier.width(50.dp))
                Switch(
                    checked = isAutoDetect,
                    onCheckedChange = {onClickAutoDetect(it)}
                )
            }
            Text(
                "Please Insert Civil ID and Click Below Button",
                color = MaterialTheme.colors.primary,
                textAlign = TextAlign.Center,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
            )
            if (!loading) {
                Pulsating {
                    Button(
                        modifier = Modifier.padding(top = 20.dp),
                        onClick = onClick
                    ) {
                        Text(text = "Read Data")
                    }
                }


            } else {
                CircularProgressIndicator()
            }


        }
        Column(
            modifier = Modifier
                .weight(1.0f)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.civilidinsert),
                contentDescription = "civilid"
            )
        }
    }
}

@Composable
fun Pulsating(pulseFraction: Float = 1.2f, content: @Composable () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition()

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = pulseFraction,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(modifier = Modifier.scale(scale)) {
        content()
    }
}