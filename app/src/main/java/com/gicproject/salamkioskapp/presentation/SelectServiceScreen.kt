package com.gicproject.salamkioskapp.presentation

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.airbnb.lottie.compose.*
import com.gicproject.salamkioskapp.R
import com.gicproject.salamkioskapp.Screen
import com.gicproject.salamkioskapp.common.Constants
import com.gicproject.salamkioskapp.common.Constants.Companion.heartBeatJson
import com.gicproject.salamkioskapp.domain.model.SelectDepartment
import com.gicproject.salamkioskapp.domain.model.SelectService
import com.google.accompanist.flowlayout.FlowColumn
import com.google.accompanist.flowlayout.FlowCrossAxisAlignment
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import kotlinx.coroutines.delay
import java.util.*

import androidx.compose.ui.text.font.GenericFontFamily
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.lifecycle.viewmodel.compose.viewModel


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SelectServiceScreen(
    selectDepartment: SelectDepartment?,
    navController: NavController,
    viewModel: MyViewModel,
) {

    val listState = rememberLazyListState()

    val second = remember { mutableStateOf(30) }

    val state = viewModel.stateSelectService.value


    var showDialog = remember { mutableStateOf(false) }
    if (showDialog.value) {
        /*val second = remember { mutableStateOf(30) }
        LaunchedEffect(key1 = Unit, block = {
            while (true) {
                delay(1000)
                second.value = second.value - 1
                if (second.value == 0) {
                    showDialog.value = false
                }
            }
        })*/
        Dialog(
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = true,
                usePlatformDefaultWidth = false
            ),
            onDismissRequest = {
                showDialog.value = false
            },

            ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .background(Color.White)
                    .fillMaxWidth()
                    .padding(top = 80.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier
                        .background(Color.White)
                        .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    CustomButton(onClick = {
                        showDialog.value = false
                        navController.currentBackStackEntry?.savedStateHandle?.set(
                            Constants.STATE_EXTRA, false
                        )
                        navController.navigate(Screen.InsertCivilIdScreen.route)
                    }, text = "Appointment")
                    CustomButton(onClick = {
                        viewModel.onEvent(
                            MyEvent.GetBookTicket(
                                serviceID = viewModel.selectService.ServicesPKID.toString(),
                                isHandicap = false,
                                isVip = false,
                                languageID = "0",
                                appointmentCode = "-1",
                                isaapt = false,
                                refid = "-1",
                                DoctorServiceID = "-1",
                                ticketDesignId = viewModel.selectService.ServicesTicketDesignerFKID.toString()
                            )
                        )
                    }, text = "Without Appointment")
                }

                Spacer(modifier = Modifier.height(20.dp))
                Row() {
                    Button(
                        onClick = { showDialog.value = false },
                        modifier = Modifier
                            .padding(20.dp)
                            .shadow(50.dp, shape = RoundedCornerShape(5.dp)),
                        shape = RoundedCornerShape(30.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primaryVariant)
                    ) {
                        Icon(
                            Icons.Default.KeyboardArrowLeft,
                            contentDescription = "",
                            modifier = Modifier.size(50.dp)
                        )
                        Spacer(modifier = Modifier.width(20.dp))
                        Text("Go Back", fontSize = 25.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                    }
                    // HeartBeatTimeRow(second = second)
                }
            }
        }
    }

    LaunchedEffect(true) {

        Log.d(
            "TAG",
            "SelectServiceScreen: department id ${selectDepartment?.DepartmentPKID.toString()}"
        )
        viewModel.onEvent(MyEvent.GetSelectServices(selectDepartment?.DepartmentPKID.toString()))
    }
    LaunchedEffect(key1 = Unit, block = {
        while (true) {
            delay(1000)
            second.value = second.value - 1
            if (second.value == 0) {
                navController.popBackStack(Screen.SelectDepartmentScreen.route, false)
            }
        }
    })
    if (state.success.isNotBlank()) {
        navController.popBackStack(Screen.SelectDepartmentScreen.route, false)
    }
    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    MaterialTheme.colors.surface,
                )
        ) {
            Modifier.padding(innerPadding)
            Column(
                modifier = Modifier.fillMaxSize()
            ) {

                Image(
                    painter = painterResource(id = Constants.BACKGROUND_IMAGE),
                    contentScale = ContentScale.FillBounds,
                    contentDescription = "bg",
                    modifier = Modifier.fillMaxSize()
                )


            }
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Bottom
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(20.dp)
                ) {
                    GoBack(navController = navController)
                }
            }
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Bottom
            ) {
                HeartBeatTime(second = second)
            }
            HeaderDesign("Select Service", navController)
            if (state.error.isNotBlank()) {
                Text(
                    state.error,
                    color = MaterialTheme.colors.error,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(top = 180.dp)
                )
            }
            FlowColumn(
                Modifier.fillMaxSize(),
                crossAxisAlignment = FlowCrossAxisAlignment.Center,
                mainAxisAlignment = FlowMainAxisAlignment.Center,
            ) {
                LazyVerticalGrid(
                    verticalArrangement = Arrangement.Center,
                    horizontalArrangement = Arrangement.Center,
                    state = rememberLazyGridState(),
                    contentPadding = PaddingValues(70.dp),
                    modifier = Modifier
                        .width(730.dp)
                        .height(750.dp),
                    columns = GridCells.Fixed(2),
                ) {
                    items(state.services.size) { index ->
                        ServiceInfo(state.services[index], navController, onClick = {
                            showDialog.value = true
                            viewModel.selectService = state.services[index]
                            /* viewModel.onEvent(MyEvent.GetBookTicket(
                                 serviceID = state.services[index].ServicesPKID.toString(),
                                 isHandicap = false,
                                 isVip = false,
                                 languageID = "0",
                                 appointmentCode = "-1",
                                 isaapt = false,
                                 refid = "-1",
                                 DoctorServiceID = "-1",
                                 ticketDesignId = state.services[index].ServicesTicketDesignerFKID.toString()
                             ))*/
                        })

                    }
                }
            }


            /* if (state.error.isNotBlank()) {

             }
             if (state.isLoading) {
                 CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
             }
             if (state.success.isNotBlank()) {
                 LaunchedEffect(key1 = true) {

                 }
             }*/
        }
        if (state.isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f)),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun ServiceInfo(service: SelectService, navController: NavController, onClick: () -> Unit) {

    val fontName = "GE_SS_Two_Bold"

    val provider = GoogleFont.Provider(
        providerAuthority = "com.google.android.gms.fonts",
        providerPackage = "com.google.android.gms",
        certificates = R.array.com_google_android_gms_fonts_certs
    )
    val fontFamily = FontFamily(
        androidx.compose.ui.text.googlefonts.Font(googleFont = GoogleFont(fontName), provider)
    )
    val arabicBold = TextStyle(fontFamily = fontFamily)

    var DoctorDisplayNameEnFontSIze: TextUnit = with(LocalDensity.current) {
        service.ServicesFontSize?.toInt()?.toSp() ?: 88.toSp()
    }

    //  var DoctorDisplayNameEnFontColor: Color = state.doctorDetail.DoctorDisplayNameEnFontColor?.color ?: "#2e3192".color


    /*  val DoctorDisplayNameEnFontName =state.doctorDetail.DoctorDisplayNameEnFontName ?: fontName
      lateinit var fontFamilyDoctorDisplayNameEnFontName : FontFamily
      if(DoctorDisplayNameEnFontName != "GE_SS_Two_Bold"){
          fontFamilyDoctorDisplayNameEnFontName =  FontFamily(
              androidx.compose.ui.text.googlefonts.Font(googleFont = GoogleFont(DoctorDisplayNameEnFontName),provider))
      }else{
          fontFamilyDoctorDisplayNameEnFontName=  FontFamily(Font(R.font.ge_bold))
      }*/
    Box(
        modifier = Modifier
            .width(250.dp)
            .height(250.dp)
            .padding(horizontal = 8.dp, vertical = 12.dp)
            .background(color = Color.White, shape = RoundedCornerShape(20.dp))
            .clickable {
                onClick()
            }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                var bitmap: ImageBitmap? = null
                if (service.ServicesBackGroundImage != null) {
                    try {
                        bitmap = service.ServicesBackGroundImage!!.toBitmap().asImageBitmap()
                    } catch (e: java.lang.Exception) {
                        bitmap = null
                    }
                }
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap,
                        contentDescription = "",
                        modifier = Modifier
                            .width(180.dp)
                            .height(100.dp)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .width(180.dp)
                            .height(100.dp)
                    )
                }


            }
            Spacer(modifier = Modifier.height(15.dp))

            Text(
                (service.ServicesNameEN
                    ?: "") + "\n" + (service.ServicesNameAR ?: ""),
                color = Color.Black,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                (service.ServicesDescription
                    ?: "") + "\n" + (service.ServicesDescriptionAr ?: ""),
                color = MaterialTheme.colors.secondary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

        }


    }
}

fun String.toBitmap(): Bitmap {
    Base64.decode(this, Base64.DEFAULT).apply {
        return BitmapFactory.decodeByteArray(this, 0, size)
    }
}



