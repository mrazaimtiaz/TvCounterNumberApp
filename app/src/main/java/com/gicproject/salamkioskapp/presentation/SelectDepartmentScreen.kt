package com.gicproject.salamkioskapp.presentation

import android.util.Log
import android.view.LayoutInflater
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
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
import com.gicproject.salamkioskapp.common.Constants.Companion.heartBeatJson
import com.gicproject.salamkioskapp.ui.theme.*
import com.google.accompanist.flowlayout.FlowColumn
import com.google.accompanist.flowlayout.FlowCrossAxisAlignment
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import kotlinx.coroutines.delay


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SelectDepartmentScreen(
    navController: NavController,
    viewModel: MyViewModel,
) {

    val state = viewModel.stateSelectDepartment.value

    LaunchedEffect(true) {
        while (true) {
            Log.d("TAG", "SelectDepartmentScreen: called GetSelectDepartments" )
            viewModel.onEvent(MyEvent.GetSelectServices("1"))
            delay(4000)
        }
    }
    var showDialog = remember { mutableStateOf(false) }


    val configuration = LocalConfiguration.current

    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    val arabicFont = FontFamily(Font(R.font.ge_bold))
    val englishFont = FontFamily(Font(R.font.questrial_regular))

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

            var padding: Dp =  with(LocalDensity.current) {
                (screenHeight / 50)
            }
            var textFont: TextUnit =  with(LocalDensity.current) {
                (screenHeight / 21).toSp()
            }
            var numberFont: TextUnit =  with(LocalDensity.current) {
                (screenHeight / 8).toSp()
            }
            var iconFont: Dp =  with(LocalDensity.current) {
                (screenHeight / 8)
            }
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Row(Modifier.fillMaxSize()) {
                    Box(
                        Modifier
                            .weight(2f)
                            .fillMaxHeight()) {



                        Row(
                            Modifier
                                .fillMaxSize()
                                .padding(padding)) {
                            Column(
                                Modifier
                                    .weight(2f)
                                    .fillMaxSize()) {
                                Card(modifier = Modifier
                                    .fillMaxWidth()
                                    .height(screenHeight / 6),shape = RoundedCornerShape(
                                    topStart = 30.dp,
                                ), backgroundColor = colorOneCounter) {
                                    Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()){
                                        Text("Number", fontSize = textFont, color = colorText, fontFamily = englishFont, fontWeight = FontWeight.Bold)
                                        Text("الرقم", fontSize = textFont, color = colorText, fontFamily = arabicFont)
                                    }

                                }
                                Row(modifier = Modifier
                                    .fillMaxWidth()
                                    .height(screenHeight / 6)
                                    .background(colorRowTwo), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                                    Column(Modifier.weight(2f), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("224", fontSize = numberFont, color = colorTextRow, fontFamily = englishFont, fontWeight = FontWeight.Bold)
                                    }
                                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon( Icons.Default.KeyboardArrowRight, contentDescription = "", tint = colorOneCounter, modifier = Modifier.size(iconFont))
                                    }

                                }
                                Row(modifier = Modifier
                                    .fillMaxWidth()
                                    .height(screenHeight / 6)
                                    .background(colorRowOne)) {


                                }
                                Row(modifier = Modifier
                                    .fillMaxWidth()
                                    .height(screenHeight / 6)
                                    .background(colorRowTwo)) {


                                }
                                Row(modifier = Modifier
                                    .fillMaxWidth()
                                    .height(screenHeight / 6)
                                    .background(colorRowOne)) {


                                }
                                Row(modifier = Modifier
                                    .fillMaxWidth()
                                    .height(screenHeight / 6)
                                    .background(colorRowTwo)) {


                                }


                            }
                            Column(
                                Modifier
                                    .weight(1f)
                                    .fillMaxSize()) {
                                Card(modifier = Modifier
                                    .fillMaxWidth()
                                    .height(screenHeight / 6),shape = RoundedCornerShape(
                                    topEnd = 30.dp,
                                ), backgroundColor = Color(0XFF08119B)) {
                                    Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()){
                                        Text("Counter", fontSize = textFont, color = colorText, fontFamily = englishFont, fontWeight = FontWeight.Bold)
                                        Text("كاونتر", fontSize = textFont, color = colorText, fontFamily = arabicFont)
                                    }

                                }
                                Row(modifier = Modifier
                                    .fillMaxWidth()
                                    .height(screenHeight / 6)
                                    .background(colorRowTwo), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                                    Text("3", fontSize = numberFont, color = colorTextRow, fontFamily = englishFont, fontWeight = FontWeight.Bold)


                                }
                                Row(modifier = Modifier
                                    .fillMaxWidth()
                                    .height(screenHeight / 6)
                                    .background(colorRowOne)) {


                                }
                                Row(modifier = Modifier
                                    .fillMaxWidth()
                                    .height(screenHeight / 6)
                                    .background(colorRowTwo)) {


                                }
                                Row(modifier = Modifier
                                    .fillMaxWidth()
                                    .height(screenHeight / 6)
                                    .background(colorRowOne)) {


                                }
                                Row(modifier = Modifier
                                    .fillMaxWidth()
                                    .height(screenHeight / 6)
                                    .background(colorRowTwo)) {


                                }
                            }
                        }

                    }
                    Box(
                        Modifier
                            .weight(1f)
                            .fillMaxHeight()) {
                        Column(
                            Modifier
                                .fillMaxSize()
                                .padding(padding)) {
                            Row(modifier = Modifier.weight(3f)){
                                Image(
                                    painter = painterResource(id = Constants.DOCTOR_SAMPLE_IMAGE),
                                    contentScale = ContentScale.Fit,
                                    contentDescription = "bg",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(padding)
                                )
                            }
                            Row(modifier = Modifier
                                .weight(7f)
                                .padding(padding)){
                                Card(modifier = Modifier
                                    .fillMaxSize(),shape = RoundedCornerShape(30.dp
                                )) {
                                    Column(modifier = Modifier.fillMaxSize()) {
                                        Row(modifier = Modifier
                                            .weight(1f)
                                            .fillMaxSize()
                                            .background(color = colorOneCounter), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                                            Column() {
                                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                                                    Text("Counter - ", fontSize = textFont, color = colorText, fontFamily = englishFont, fontWeight = FontWeight.Bold)
                                                    Text("كاونتر", fontSize = textFont, color = colorText, fontFamily = arabicFont)
                                                }
                                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()){
                                                    Text("224", fontSize = numberFont, color = colorText, fontFamily = englishFont, fontWeight = FontWeight.Bold,)
                                                }

                                            }
                                        }
                                        Row(modifier = Modifier
                                            .weight(1f)
                                            .fillMaxSize()
                                            .background(color = Color(0XFF08119B)), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                                            Column() {
                                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                                                    Text("Number - ", fontSize = textFont, color = colorText, fontFamily = englishFont, fontWeight = FontWeight.Bold)
                                                    Text("الرقم", fontSize = textFont, color = colorText, fontFamily = arabicFont)
                                                }
                                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()){
                                                    Text("3", fontSize = numberFont, color = colorText, fontFamily = englishFont, fontWeight = FontWeight.Bold)
                                                }

                                            }
                                        }

                                    }
                                }







                            }
                        }


                    }

                }
                val context = LocalContext.current
                Button(onClick = { viewModel.textToSpeech(context,"Counter No 2       Number 268 ") }) {

                }
               /* FlowColumn(
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
                            .height(950.dp),
                        columns = GridCells.Fixed(2),
                    ) {
                        items(state.departments.size) { index ->
                          var sizeFont =  with(LocalDensity.current) {
                                (screenHeight / 20).toSp()
                            }
                            Box(
                                Modifier
                                    .height((screenHeight / 3))
                                    .background(Color.Blue)
                                    .fillMaxWidth()
                            ) {

                            }
                            Text(state.departments[index].ServicesNameEN.toString(), fontSize = sizeFont)

                        }
                    }
                }*/
            }
            /*
             if (state.isLoading) {
                 CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
             }
             if (state.success.isNotBlank()) {
                 LaunchedEffect(key1 = true) {

                 }
             }*/

        }
        if (state.error.isNotBlank()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(15.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Bottom,
            ) {
                Text(state.error, color = MaterialTheme.colors.error, fontSize = 24.sp)
            }
        }


        if (state.isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colorTextRow.copy(alpha = 0.6f)),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                CircularProgressIndicator()
            }
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
fun GoBack(navController: NavController) {
    Button(
        onClick = { navController.popBackStack() },
        modifier = Modifier
            .padding(20.dp)
            .shadow(50.dp, shape = RoundedCornerShape(5.dp)),
        shape = RoundedCornerShape(30.dp)
    ) {
        Icon(
            Icons.Default.KeyboardArrowLeft,
            contentDescription = "",
            modifier = Modifier.size(50.dp)
        )
        Spacer(modifier = Modifier.width(20.dp))
        val fontEnglish = FontFamily(Font(R.font.questrial_regular))
        val fontArabic = FontFamily(Font(R.font.ge_dinar_one_medium))
        Row(){
            Text("Back  ", fontSize = 25.sp, fontFamily = fontEnglish)
            Text("عوده", fontSize = 25.sp, fontFamily = fontArabic)
        }
        Spacer(modifier = Modifier.width(10.dp))
    }
}

@Composable
fun HeartBeatTime(second: MutableState<Int>) {
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


@Composable
fun HeartBeatTimeRow(second: MutableState<Int>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.Bottom,
    ) {
        HeartBeatAnimation()
        Spacer(modifier = Modifier.width(20.dp))
        Text(second.value.toString(), fontSize = 40.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.width(20.dp))
    }
}

