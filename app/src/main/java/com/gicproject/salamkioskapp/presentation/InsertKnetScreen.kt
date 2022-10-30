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
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.gicproject.salamkioskapp.common.Constants.Companion.heartBeatJson
import com.gicproject.salamkioskapp.common.Constants.Companion.insertCardJson
import kotlinx.coroutines.delay
import java.util.*


@Composable
fun InsertKnetScreen(
    navController: NavController,
    viewModel: MyViewModel,
) {

    val listState = rememberLazyListState()

    val second = remember { mutableStateOf(120) }



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
                    painter = painterResource(id = R.drawable.background),
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
                    Button(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.KeyboardArrowLeft,
                            contentDescription = "",
                            tint = Color.Black
                        )
                        Text("Go Back", color = Color.Black)
                    }
                }
            }
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
                    Text(second.value.toString(), fontSize = 30.sp, fontWeight = FontWeight.Bold)
                }
            }
            HeaderDesign("Knet")

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
                Spacer(modifier = Modifier.height(60.dp))
                Button(onClick = { navController.popBackStack() }) {
                    Icon(
                        Icons.Default.Send,
                        contentDescription = "",
                        tint = Color.Black, modifier = Modifier.size(30.dp)
                    )
                    Spacer(modifier = Modifier.width(40.dp))
                    Text("Proceed", color = Color.Black, fontSize = 30.sp)
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
fun PayKnetAnimation() {
    val composition by rememberLottieComposition(LottieCompositionSpec.JsonString(insertCardJson),)
    LottieAnimation(
        composition,
        iterations = LottieConstants.IterateForever,  modifier = Modifier.size(300.dp), isPlaying = true,

        )
}



