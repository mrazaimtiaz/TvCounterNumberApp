package com.gicproject.kcbsignatureapp.presentation


import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.PictureDrawable
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.caverock.androidsvg.SVG
import com.gicproject.kcbsignatureapp.R
import com.gicproject.kcbsignatureapp.common.Constants
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.delay
import java.io.File
import java.nio.charset.Charset
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

    val context = LocalContext.current

    val isShowDialog = remember { mutableStateOf(false) }
    if (state.showToast.isNotBlank()) {
        if (state.showToast == "S") {
            LaunchedEffect(key1 = true) {
                isShowDialog.value = true
                delay(2000)
                isShowDialog.value = false
            }
        } else {
            Toast.makeText(context, state.showToast, Toast.LENGTH_SHORT).show()
            viewModel.emptyToast()
        }


    }


    if (isShowDialog.value) {
        Dialog(
            onDismissRequest = {
                isShowDialog.value = false
            },
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .background(Color.White)
                    .padding(30.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Signature Saved Successfully", fontSize = 30.sp)
                DoneAnimation()
            }
        }
    }
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
                            //viewModel.emptyMainState()
                            //viewModel.readData()
                            viewModel.onEvent(MyEvent.GetEmployeeListData)
                            viewModel.openEmployeeListPage()
                        }, onClickAutoDetect = {
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
                    if (state.employeeListPage) {
                        EmployeeListPage(
                            onClickBack = {
                                viewModel.backToCivilIdPage()
                            },
                            viewModel = viewModel,
                        )
                    }
                    if (state.employeeInfoPage) {
                        EmployeeInfoPage(
                            onClickBack = {
                                viewModel.backToEmployeeListPage()
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

    val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()

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
                lifecycleOwner, cameraSelector, preview, imageCapture
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
                modifier = Modifier.size(100.dp, 100.dp)
            )
        }
        Spacer(modifier = Modifier.width(20.dp))
        Button(modifier = Modifier.padding(top = 20.dp), onClick = {
            viewModel.openSignaturePage()
        }) {
            Text(text = "Capture Signature")
        }
    }



    Row(modifier = Modifier.fillMaxSize()) {

        /* Column(
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

         }*/
        Column(
            modifier = Modifier
                .weight(3.0f)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                OutlinedTextField(readOnly = true,
                    value = civilIdText,
                    label = { Text("Civil ID") },
                    onValueChange = {
                        viewModel.onEvent(MyEvent.CivilIdChanged(it))
                    })
                Spacer(modifier = Modifier.width(20.dp))
                OutlinedTextField(readOnly = true,
                    value = expiryDate,
                    label = { Text("Expiry Date") },
                    onValueChange = {
                        viewModel.onEvent(MyEvent.ExpiryDateChanged(it))
                    })
            }
            OutlinedTextField(modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                value = fullNameText,
                label = { Text("Full Name English") },
                onValueChange = {
                    viewModel.onEvent(MyEvent.FullNameChanged(it))
                })
            OutlinedTextField(modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                value = fullNameArText,
                label = { Text("Full Name Arabic") },
                onValueChange = {
                    viewModel.onEvent(MyEvent.FullNameArChanged(it))
                })
            Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                OutlinedTextField(readOnly = true,
                    value = nationalityText,
                    label = { Text("Nationality") },
                    onValueChange = {
                        viewModel.onEvent(MyEvent.NationalityChanged(it))
                    })
                Spacer(modifier = Modifier.width(20.dp))
                OutlinedTextField(readOnly = true,
                    value = dobText,
                    label = { Text("Date of Birth") },
                    onValueChange = {
                        viewModel.onEvent(MyEvent.DOBChanged(it))
                    })
            }
            Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                OutlinedTextField(readOnly = true,
                    value = genderText,
                    label = { Text("Gender") },
                    onValueChange = {
                        viewModel.onEvent(MyEvent.GenderChanged(it))
                    })
                Spacer(modifier = Modifier.width(20.dp))
                OutlinedTextField(readOnly = true,
                    value = emailText,
                    label = { Text("Email") },
                    onValueChange = {
                        viewModel.onEvent(MyEvent.EmailChanged(it))
                    })
            }
            Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                OutlinedTextField(readOnly = true,
                    value = tel1Text,
                    label = { Text("Tel 1") },
                    onValueChange = {
                        viewModel.onEvent(MyEvent.Tel1Changed(it))
                    })
                Spacer(modifier = Modifier.width(20.dp))
                OutlinedTextField(readOnly = true,
                    value = tel2Text,
                    label = { Text("Tel 2") },
                    onValueChange = {
                        viewModel.onEvent(MyEvent.Tel2Changed(it))
                    })
            }
        }
    }
}


@Composable
fun CivilIdPage(
    loading: Boolean,
    isAutoDetect: Boolean,
    onClick: () -> Unit,
    onClickAutoDetect: (value: Boolean) -> Unit
) {

    Row(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .weight(2.0f)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            /*Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Auto Detect Card")
                Spacer(modifier = Modifier.width(50.dp))
                Switch(
                    checked = isAutoDetect,
                    onCheckedChange = {onClickAutoDetect(it)}
                )
            }*/
            // and Click Below Button
            Text(
                "Please Insert Civil ID",
                color = MaterialTheme.colors.primary,
                textAlign = TextAlign.Center,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
            )
            if (!loading) {
                Button(
                    modifier = Modifier.padding(top = 20.dp), onClick = onClick
                ) {
                    Text(text = "Employee List")
                }
                if (!isAutoDetect) {
                    //  Pulsating {
                    Button(
                        modifier = Modifier.padding(top = 20.dp), onClick = onClick
                    ) {
                        Text(text = "Employee List")
                    }
                    //  }
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
fun EmployeeListPage(
    onClickBack: () -> Unit, viewModel: MyViewModel,
) {

    var stateEmployeeList = viewModel.stateMain.value


    Row(horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onClickBack) {
            Icon(
                Icons.Filled.KeyboardArrowLeft,
                "back arrow",
                tint = MaterialTheme.colors.primary,
                modifier = Modifier.size(100.dp, 100.dp)
            )
        }
    }

    var query: String by rememberSaveable { mutableStateOf("") }
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        TextField(

            value = query,
            onValueChange = { onQueryChanged ->
                query = onQueryChanged
                viewModel.myFilter.filter(query)

            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    tint = MaterialTheme.colors.onBackground,
                    contentDescription = "Search icon"
                )
            },
            trailingIcon = {
                IconButton(onClick = {
                    query = ""
                    viewModel.myFilter.filter(query)
                }) {
                    Icon(
                        imageVector = Icons.Rounded.Clear,
                        tint = MaterialTheme.colors.onBackground,
                        contentDescription = "Clear icon"
                    )
                }
            },
            maxLines = 1,
            colors = TextFieldDefaults.textFieldColors(backgroundColor = Color.Transparent),
            placeholder = { Text(text = "Search") },
            textStyle = MaterialTheme.typography.subtitle1,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            modifier = Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colors.background, shape = RectangleShape)
        )
    }

    val column1Weight = .2f // 30%
    val column2Weight = .4f // 70%
    val column3Weight = .4f // 30%
    val column4Weight = .2f // 30%
    // The LazyColumn will be our table. Notice the use of the weights below
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Here is the header
            item {
                Row(Modifier.background(Color.Gray)) {

                    TableCell(text = "Emp Num",
                        weight = column1Weight,
                        Modifier
                            .border(0.dp, MaterialTheme.colors.onBackground.copy(alpha = 0.5f))
                            .weight(column1Weight)
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                            .clickable {
                                viewModel.sortingList("1")
                            })
                    TableCell(text = "Full Name",
                        weight = column2Weight,
                        Modifier
                            .border(0.dp, MaterialTheme.colors.onBackground.copy(alpha = 0.5f))
                            .weight(column1Weight)
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                            .clickable {
                                viewModel.sortingList("2")
                            })
                    TableCell(text = "Dept Name",
                        weight = column3Weight,
                        Modifier
                            .border(0.dp, MaterialTheme.colors.onBackground.copy(alpha = 0.5f))
                            .weight(column1Weight)
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                            .clickable {
                                viewModel.sortingList("3")
                            })
                    TableCell(text = "Status",
                        weight = column4Weight,
                        Modifier
                            .border(0.dp, MaterialTheme.colors.onBackground.copy(alpha = 0.5f))
                            .weight(column1Weight)
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                            .clickable {
                                viewModel.sortingList("4")
                            })
                }
            }
            // Here are all the lines of your table.
            items(stateEmployeeList.employeeSearchList) { item ->

                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.onEvent(
                                MyEvent.GetEmployeeSignatureData(
                                    item.NATIONALIDENTIFIER ?: ""
                                )
                            )
                            viewModel.openEmployeeInfoPage(item)
                        }) {
                    TableCell(text = item.EMPLOYEENUMBER ?: "", weight = column1Weight)
                    TableCell(text = item.FULLNAME ?: "", weight = column2Weight)
                    TableCell(text = item.ORGANIZATIONNAME ?: "", weight = column3Weight)
                    Icon(
                        if (item.SIGNATUREEXISTS == "Y") Icons.Filled.Check else Icons.Filled.Close,
                        "back arrow",
                        tint = if (item.SIGNATUREEXISTS == "Y") Color.Green else Color.Red,
                        modifier = Modifier
                            .border(
                                0.dp, MaterialTheme.colors.onBackground.copy(alpha = 0.5f)
                            )
                            .weight(column4Weight)
                            .size(28.dp, 28.dp)
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }


    LazyColumn(
        modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(1.dp)
    ) {

        items(stateEmployeeList.employeeSearchList) { item ->
            Log.d("TAG", "EmployeeListPage:size ${stateEmployeeList.employeeList.size}")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(20.dp))
                Text(
                    (item.FULLNAME ?: "") + " - " + (item.EMPLOYEENUMBER ?: ""),
                )
                Spacer(modifier = Modifier.width(20.dp))
                Icon(
                    if (item.SIGNATUREEXISTS == "Y") Icons.Filled.Check else Icons.Filled.Close,
                    "back arrow",
                    tint = if (item.SIGNATUREEXISTS == "Y") Color.Green else Color.Red,
                    modifier = Modifier.size(40.dp, 40.dp)
                )
            }
        }
    }
}


@OptIn(ExperimentalPagerApi::class)
@Composable
fun EmployeeInfoPage(
    onClickBack: () -> Unit, viewModel: MyViewModel,
) {

    var stateEmployeeInfo = viewModel.stateMain.value
    val employeeData = stateEmployeeInfo.employeeInfoShow


    Row(horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onClickBack) {
            Icon(
                Icons.Filled.KeyboardArrowLeft,
                "back arrow",
                tint = MaterialTheme.colors.primary,
                modifier = Modifier.size(100.dp, 100.dp)
            )
        }
    }
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Row(modifier = Modifier.fillMaxWidth().padding(2.dp)) {
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.Start) {
                ShowRowText(
                    title = "رقم الموظف",
                    text = employeeData?.EMPLOYEENUMBER ?: "",
                    bgColor = Color(0xFFDBE1FF)
                )
                ShowRowText(title = "الاسم", text = employeeData?.FULLNAME ?: "")
                ShowRowText(
                    title = "اسم االمستخدم",
                    text = employeeData?.USERNAME ?: "",
                    bgColor = Color(0xFFDBE1FF)
                )

            }
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.Start) {
                ShowRowText(
                    title = "رقم البطاقة",
                    text = employeeData?.NATIONALIDENTIFIER ?: "",
                    bgColor = Color(0xFFDBE1FF)
                )
                ShowRowText(title = "اسم العمل", text = employeeData?.JOBNAME ?: "")
                ShowRowText(
                    title = "اسم المنظمة",
                    text = employeeData?.ORGANIZATIONNAME ?: "",
                    bgColor = Color(0xFFDBE1FF)
                )
            }
        }
        if (!stateEmployeeInfo.isLoadingEmployeeInfo) {
            LazyRow(
                modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(1.dp)
            ){
                items(stateEmployeeInfo.employeeSignatures){item ->
                    val decodeValue: ByteArray = Base64.decode(
                        item.EMPLOYEESIGNATURE, Base64.DEFAULT
                    )
                    val text = String(decodeValue, Charset.forName("UTF-8"))
                    val svg = SVG.getFromString(text)
                    val drawable = PictureDrawable(svg.renderToPicture())
                    var bitmap = Bitmap.createBitmap(
                        drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
                    )
                    val canvas = Canvas(bitmap)
                    canvas.drawPicture(drawable.picture)
                    Column(modifier = Modifier.padding(5.dp)) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "logo",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .border(
                                    1.dp,
                                    MaterialTheme.colors.primary,
                                    shape = RoundedCornerShape(10.dp)
                                )
                        )
                        Text(text = item.ATTACHMENTDATE)
                    }
                }
            }
            if(stateEmployeeInfo.employeeSignatures.isEmpty()){
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Text(text = "No Signature Found - لم يتم العثور على توقيع", color = Color.Red)

                    if (!stateEmployeeInfo.isLoadingCivilId) {
                        Button(onClick = {
                            viewModel.emptyMainState()
                            viewModel.readData(employeeData?.NATIONALIDENTIFIER ?: "1") }) {
                            Text("Add Signature \n أضف التوقيع", textAlign = TextAlign.Center)
                        }
                    } else {
                        CircularProgressIndicator()
                    }

                }
            }else{
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    if (!stateEmployeeInfo.isLoadingCivilId) {
                        Button(onClick = {
                            viewModel.emptyMainState()
                            viewModel.readData(employeeData?.NATIONALIDENTIFIER ?: "1")
                        }) {
                            Text("Update Signature  \n توقيع التحديث", textAlign = TextAlign.Center)
                        }
                    } else {
                        CircularProgressIndicator()
                    }
                }
            }
        } else {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun ShowRowText(title: String, text: String, bgColor: Color = Color(0xFFEFEFEF)) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor)
    ) {
        Text(text = title, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
        Text(text = text, modifier = Modifier.weight(3f))
    }
}

fun String.toBitmap(): Bitmap {
    Base64.decode(this, Base64.DEFAULT).apply {
        return BitmapFactory.decodeByteArray(this, 0, size)
    }
}

@Composable
fun Pulsating(pulseFraction: Float = 1.2f, content: @Composable () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition()

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = pulseFraction, animationSpec = infiniteRepeatable(
            animation = tween(1000), repeatMode = RepeatMode.Reverse
        )
    )

    Box(modifier = Modifier.scale(scale)) {
        content()
    }
}

@Composable
fun DoneAnimation() {
    val composition by rememberLottieComposition(LottieCompositionSpec.JsonString(Constants.doneJson))
    LottieAnimation(
        composition, modifier = Modifier.size(100.dp)
    )
}

@Composable
private fun RowScope.TableCell(
    text: String,
    weight: Float = 1f,
    modifier: Modifier = Modifier
        .border(0.dp, MaterialTheme.colors.onBackground.copy(alpha = 0.5f))
        .weight(weight)
        .padding(horizontal = 4.dp, vertical = 2.dp)
) {
    val textStyle = MaterialTheme.typography.body1
    val fontWidth = textStyle.fontSize.value / 2.2f // depends of font used(
    val width = (fontWidth * weight).coerceAtMost(500f)
    val textColor = MaterialTheme.colors.onBackground
    Text(
        text = text,
        maxLines = 1,
        fontSize = 17.sp,
        softWrap = false,
        overflow = TextOverflow.Ellipsis,
        color = textColor,
        modifier = modifier
    )
}