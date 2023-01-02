package com.gicproject.salamkioskapp.presentation

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Phone
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.gicproject.salamkioskapp.R
import com.gicproject.salamkioskapp.Screen
import com.gicproject.salamkioskapp.common.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LinkPayScreen(
    navController: NavController,
    viewModel: MyViewModel,
) {

    val listState = rememberLazyListState()

    val second = remember { mutableStateOf(120) }

    var numberEditText = remember { mutableStateOf("") }

    val focusNumber = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    var isError = remember { mutableStateOf(false) }

    var showDialog = remember { mutableStateOf(false) }

        if (showDialog.value) {
          DoneDialog(showDialog)
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
            HeaderDesign("Insert Mobile Number",navController)

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextField(
                        textStyle = TextStyle(fontSize = 30.sp),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusNumber.freeFocus()
                                focusManager.clearFocus()
                                showDialog.value = true
                                CoroutineScope(Dispatchers.Main).launch {
                                    delay(2000)
                                    navController.popBackStack(Screen.SelectDepartmentScreen.route,false)
                                }
                            }
                        ),
                        /*label = {
                            Text(
                                //text = "Mobile Number or Email - رقم الهاتف او بريد الكتروني",
                                text = "Mobile Number",
                                fontSize = 20.sp,
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(10.dp)
                            )
                        },*/
                        isError = isError.value,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Phone, contentDescription = "emailIcon", tint = Color.Black
                            )
                        },
                        value = numberEditText.value,
                        onValueChange = {
                            isError.value = false
                            numberEditText.value = it
                        },
                        modifier = Modifier
                            .focusRequester(focusNumber)
                            .background(
                                MaterialTheme.colors.secondary, RoundedCornerShape(percent = 20)
                            )
                            .padding(4.dp)
                            .height(90.dp)
                            .width(700.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Phone,
                            imeAction = ImeAction.Done
                        )
                    )
                    LaunchedEffect(true) {
                        delay(500)
                        focusNumber.requestFocus()
                    }
                }
                Spacer(modifier = Modifier.height(30.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    var context = LocalContext.current
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SubmitButton(onClick = {
                            focusNumber.freeFocus()
                            focusManager.clearFocus()
                            showDialog.value = true
                            CoroutineScope(Dispatchers.Main).launch {
                                delay(2000)
                                navController.popBackStack(Screen.SelectDepartmentScreen.route,false)

                            }


                        }, text = "Proceed")
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
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DoneDialog(showDialog: MutableState<Boolean>){
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
                .padding(vertical = 80.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "Link has Been sent to Your Mobile Number",
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontSize = 20.sp
            )
            DoneAnimation()
        }
    }
}
@Composable
fun DoneAnimation() {
    val composition by rememberLottieComposition(LottieCompositionSpec.JsonString(Constants.doneJson))
    LottieAnimation(
        composition,
        modifier = Modifier.size(80.dp),
        isPlaying = true,

        )
}
