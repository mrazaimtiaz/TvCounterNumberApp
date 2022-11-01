package com.gicproject.salamkioskapp.presentation

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
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
fun SelectChildServiceScreen(
    navController: NavController,
    viewModel: MyViewModel,
) {

    val second = remember { mutableStateOf(120) }


    var showDialog = remember { mutableStateOf(false) }
    if (showDialog.value) {
        if (showDialog.value) {
            PaymentDialog(showDialog, navController)
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
            HeaderDesign("Select Test")

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(30.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                val total = remember { mutableStateOf(0) }

                val isTest1 = remember { mutableStateOf(false) }
                MyCheckBox("Test1", "15", isTest1, total)
                val isTest2 = remember { mutableStateOf(false) }
                MyCheckBox("Test2", "10", isTest2, total)
                val isTest3 = remember { mutableStateOf(false) }
                MyCheckBox("Test3", "12", isTest3, total)
                val isTest4 = remember { mutableStateOf(false) }
                MyCheckBox("Test4", "09", isTest4, total)
                Spacer(modifier = Modifier.height(10.dp))
                Text("Total: ${total.value} KD ", fontSize = 35.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(80.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SubmitButton(onClick = {
                        showDialog.value = true
                    }, text = "Proceed to Pay")
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
fun MyCheckBox(
    title: String,
    price: String,
    isCheck: MutableState<Boolean>,
    total: MutableState<Int>
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .background(color = Color.White, shape = RoundedCornerShape(10.dp))
            .height(60.dp)
            .width(300.dp)
            .clickable {
                isCheck.value = !isCheck.value
                if (isCheck.value) {
                    total.value = total.value + price.toInt()
                } else {
                    total.value = total.value - price.toInt()
                }
            },
    ) {
        Row(modifier = Modifier.weight(2f)) {
            Spacer(modifier = Modifier.width(5.dp))
            Card(
                modifier = Modifier.background(Color.White),
                elevation = 0.dp,
                shape = RoundedCornerShape(6.dp),
                border = BorderStroke(1.5.dp, color = MaterialTheme.colors.secondary)
            ) {
                Box(
                    modifier = Modifier
                        .size(35.dp)
                        .background(if (isCheck.value) MaterialTheme.colors.secondary else Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCheck.value)
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "",
                            tint = Color.White,
                            modifier = Modifier.size(35.dp)
                        )
                }
            }
        }
        Row(modifier = Modifier.weight(5f)) {
            Text(
                text = title,
                modifier = Modifier.padding(16.dp),
                fontSize = 25.sp,
                color = Color.Black
            )
        }

        Row(modifier = Modifier.weight(4f)) {
            Text(
                text = "$price KD",
                modifier = Modifier.padding(16.dp),
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp,
                color = Color.Black
            )
        }
    }
    Spacer(modifier = Modifier.height(30.dp))


}


