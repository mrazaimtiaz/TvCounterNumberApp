package com.gicproject.salamkioskapp.presentation

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.gicproject.salamkioskapp.R
import com.gicproject.salamkioskapp.Screen
import com.gicproject.salamkioskapp.common.Constants
import kotlinx.coroutines.delay


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun InsertCivilIdScreen(
    isService: Boolean?,
    navController: NavController,
    viewModel: MyViewModel,
) {

    val listState = rememberLazyListState()

    val second = remember { mutableStateOf(120) }

    var textCivilId = remember { mutableStateOf("") }

    LaunchedEffect(key1 = Unit, block = {
        while (true) {
            delay(1000)
            second.value = second.value - 1
            if (second.value == 0) {
                navController.popBackStack(Screen.SelectDepartmentScreen.route, false)
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
            HeaderDesign("Insert Civil ID of Patient",navController)

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
                    PayKnetAnimation()
                }
               /* Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .width(500.dp)
                            .background(Color.White, shape = RoundedCornerShape(10.dp))
                    ) {
                        Text(
                            textCivilId.value,
                            color = Color.Black,
                            fontSize = 35.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
                Spacer(modifier = Modifier.height(30.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    var context = LocalContext.current
                    Row() {
                        NumberKeypad({
                            if (textCivilId.value.length != 12) {
                                textCivilId.value = textCivilId.value + "1"
                            } else {
                                //Toast.makeText(context, "Current Length 12", Toast.LENGTH_SHORT)
 //                                   .show()
                            }
                        }, "1")
                        Spacer(modifier = Modifier.width(10.dp))
                        NumberKeypad({
                            if (textCivilId.value.length != 12) {
                                textCivilId.value = textCivilId.value + "2"
                            } else {
                                //Toast.makeText(context, "Current Length 12", Toast.LENGTH_SHORT)
 //                                   .show()
                            }
                        }, "2")
                        Spacer(modifier = Modifier.width(10.dp))
                        NumberKeypad({
                            if (textCivilId.value.length != 12) {
                                textCivilId.value = textCivilId.value + "3"
                            } else {
                                //Toast.makeText(context, "Current Length 12", Toast.LENGTH_SHORT)
 //                                   .show()
                            }
                        }, "3")
                        Spacer(modifier = Modifier.width(10.dp))
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Row() {
                        NumberKeypad({
                            if (textCivilId.value.length != 12) {
                                textCivilId.value = textCivilId.value + "4"
                            } else {
                                //Toast.makeText(context, "Current Length 12", Toast.LENGTH_SHORT)
 //                                   .show()
                            }
                        }, "4")
                        Spacer(modifier = Modifier.width(10.dp))
                        NumberKeypad({
                            if (textCivilId.value.length != 12) {
                                textCivilId.value = textCivilId.value + "5"
                            } else {
                                //Toast.makeText(context, "Current Length 12", Toast.LENGTH_SHORT)
 //                                   .show()
                            }
                        }, "5")
                        Spacer(modifier = Modifier.width(10.dp))
                        NumberKeypad({
                            if (textCivilId.value.length != 12) {
                                textCivilId.value = textCivilId.value + "6"
                            } else {
                                //Toast.makeText(context, "Current Length 12", Toast.LENGTH_SHORT)
 //                                   .show()
                            }
                        }, "6")
                        Spacer(modifier = Modifier.width(10.dp))
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Row() {
                        NumberKeypad({
                            if (textCivilId.value.length != 12) {
                                textCivilId.value = textCivilId.value + "7"
                            } else {
                                //Toast.makeText(context, "Current Length 12", Toast.LENGTH_SHORT)
 //                                   .show()
                            }
                        }, "7")
                        Spacer(modifier = Modifier.width(10.dp))
                        NumberKeypad({
                            if (textCivilId.value.length != 12) {
                                textCivilId.value = textCivilId.value + "8"
                            } else {
                                //Toast.makeText(context, "Current Length 12", Toast.LENGTH_SHORT)
 //                                   .show()
                            }
                        }, "8")
                        Spacer(modifier = Modifier.width(10.dp))
                        NumberKeypad({
                            if (textCivilId.value.length != 12) {
                                textCivilId.value = textCivilId.value + "9"
                            } else {
                                //Toast.makeText(context, "Current Length 12", Toast.LENGTH_SHORT)
 //                                   .show()
                            }
                        }, "9")
                        Spacer(modifier = Modifier.width(10.dp))
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Row() {
                        NumberKeypad({
                            textCivilId.value = ""
                        }, isIconClose = true)
                        Spacer(modifier = Modifier.width(10.dp))
                        NumberKeypad({
                            if (textCivilId.value.length != 12) {
                                textCivilId.value = textCivilId.value + "0"
                            } else {
                                //Toast.makeText(context, "Current Length 12", Toast.LENGTH_SHORT)
 //                                   .show()
                            }
                        }, "0")
                        Spacer(modifier = Modifier.width(10.dp))
                        NumberKeypad({
                            if (textCivilId.value.isNotBlank()) {
                                textCivilId.value =
                                    textCivilId.value.substring(0, textCivilId.value.length - 1);
                            }

                        }, isIconBack = true)
                        Spacer(modifier = Modifier.width(10.dp))
                    }
                    Spacer(modifier = Modifier.height(80.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SubmitButton(onClick = {
                            if(isService == true){
                                navController.navigate(Screen.SelectServiceScreen.route)
                            }else{
                                navController.navigate(Screen.DoctorPayScreen.route)
                            }
                        }, text = "Proceed")
                    }
                }*/

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
fun NumberKeypad(
    onClick: () -> Unit,
    text: String = "",
    isIconClose: Boolean = false,
    isIconBack: Boolean = false
) {
    Button(
        onClick = onClick,
        colors = if (isIconClose || isIconBack) {
            ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.secondary)
        } else {
            ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primaryVariant)
        },
        modifier = Modifier
            .width(80.dp)
            .height(80.dp)
            .shadow(20.dp, shape = CircleShape),
        shape = CircleShape
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(5.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isIconClose) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "",
                    tint = Color.Red,
                    modifier = Modifier.size(70.dp)
                )
            } else if (isIconBack) {
                Icon(
                    Icons.Default.KeyboardArrowLeft,
                    contentDescription = "",
                    tint = Color.Yellow,
                    modifier = Modifier.size(70.dp)
                )
            } else {

                Text(
                    text, fontSize = 40.sp,  textAlign = TextAlign.Center
                )
            }
        }

    }
}

