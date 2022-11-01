package com.gicproject.salamkioskapp.presentation

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
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
import kotlinx.coroutines.delay
import java.util.*


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SelectDepartmentScreen(
    navController: NavController,
    viewModel: MyViewModel,
) {

    val listState = rememberLazyListState()

    val second = remember { mutableStateOf(120) }

    var showDialog = remember { mutableStateOf(false) }
    if (showDialog.value) {
        if (showDialog.value) {
            Dialog(
                properties =  DialogProperties(
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
                        .fillMaxWidth().padding(vertical=80.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(
                        modifier = Modifier
                            .background(Color.White)
                            .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        CustomButton(onClick = {
                            navController.currentBackStackEntry?.savedStateHandle?.set(
                                Constants.STATE_EXTRA, false
                            )
                            navController.navigate(Screen.InsertCivilIdScreen.route)
                        }, text = "Appointment")
                        CustomButton(onClick = {
                            navController.navigate(Screen.SelectDoctorTimeScreen.route)
                        }, text = "Without Appointment")
                    }
                }
            }
        }
    }

    LaunchedEffect(key1 = Unit, block = {
        while (true) {
            delay(1000)
            second.value = second.value - 1
            if (second.value == 0) {
                navController.popBackStack()
            }
        }
    })
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
                   GoBack(navController)
                }
            }
            HeartBeatTime(second = second)
            HeaderDesign("Select Department")

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    CustomButton(onClick = {
                        showDialog.value = true
                    }, text = "Surgery")

                    CustomButton(onClick = {
                        showDialog.value = true
                    }, text = "Paediatrics")
                }
                Spacer(modifier = Modifier.height(30.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    CustomButton(onClick = {
                        showDialog.value = true
                    }, text = "Cardiology")

                    CustomButton(onClick = {
                        showDialog.value = true
                    }, text = "ENT")
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
    }
}

@Composable
fun HeartBeatAnimation() {
    val composition by rememberLottieComposition(LottieCompositionSpec.JsonString(heartBeatJson))
    LottieAnimation(
        composition,
        iterations = LottieConstants.IterateForever,
        modifier = Modifier.size(80.dp),
        isPlaying = true,

        )
}


@Composable
fun GoBack(navController: NavController){
    Button(onClick = { navController.popBackStack() },
        modifier = Modifier
            .padding(20.dp)
            .shadow(50.dp, shape = RoundedCornerShape(5.dp)),
        shape = RoundedCornerShape(30.dp)
    ) {
        Icon(
            Icons.Default.KeyboardArrowLeft,
            contentDescription = "",
            tint = Color.Black,
            modifier = Modifier.size(50.dp)
        )
        Spacer(modifier = Modifier.width(20.dp))
        Text("Go Back", color = Color.Black, fontSize = 25.sp)
        Spacer(modifier = Modifier.width(10.dp))
    }
}

@Composable
fun HeartBeatTime(second: MutableState<Int>){
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Bottom
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            HeartBeatAnimation()
            Spacer(modifier = Modifier.width(20.dp))
            Text(second.value.toString(), fontSize = 40.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(20.dp))
        }
    }
}

