package com.gicproject.kcbsignatureapp.presentation

import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.rememberAsyncImagePainter
import com.gicproject.kcbsignatureapp.BuildConfig
import com.gicproject.kcbsignatureapp.SignatureLib.SignaturePadAdapter
import com.gicproject.kcbsignatureapp.SignatureLib.SignaturePadView
import java.io.File
import java.util.concurrent.Executor

private const val SIGNATURE_PAD_HEIGHT = 120

@Composable
fun SignaturePadPage(onClickBack: () -> Unit,viewModel: MyViewModel,) {



    val mutableSvg = remember { mutableStateOf("") }

    IconButton(onClick = onClickBack) {
        Icon(
            Icons.Filled.KeyboardArrowLeft,
            "back arrow",
            tint = MaterialTheme.colors.primary,
            modifier = Modifier
                .size(100.dp, 100.dp)
        )
    }

    Row(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .weight(2.0f)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
                var signaturePadAdapter: SignaturePadAdapter? = null
                val penColor = remember { mutableStateOf(Color.Black) }
            Text(
                "Signature Below",
                color = Color.Gray,
                textAlign = TextAlign.Center,
                fontSize =14.sp,
                fontWeight =  FontWeight.Bold,
            )
                Box(
                    modifier = Modifier
                        .width(700.dp)
                        .height(300.dp)
                        .border(
                            width = 2.dp,
                            color = Color.Gray,
                        )
                ) {
                    SignaturePadView(
                        penMaxWidth = 4.dp,
                        penMinWidth = 1.dp,
                        onReady = {
                            signaturePadAdapter = it
                        },
                        penColor = penColor.value,
                        onSigned = {
                            if (BuildConfig.DEBUG) {
                                Log.d("ComposeActivity", "onSigned")
                            }
                        },
                        onClear = {
                            if (BuildConfig.DEBUG) {
                                Log.d(
                                    "ComposeActivity",
                                    "onClear isEmpty:" + signaturePadAdapter?.isEmpty
                                )
                            }
                        },
                        onStartSigning = {
                            if (BuildConfig.DEBUG) {
                                Log.d("ComposeActivity", "onStartSigning")
                            }
                        })
                }
                Row (
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Button(onClick = {
                        mutableSvg.value = signaturePadAdapter?.getSignatureSvg() ?: ""
                        viewModel.setSignatureSvg(mutableSvg.value)
                        viewModel.onEvent(MyEvent.AddEmployeeData)
                      //  viewModel.backToCivilIdPage()
                    }) {
                        Text("Save")
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    Button(onClick = {
                        mutableSvg.value = ""
                        signaturePadAdapter?.clear()
                    }) {
                        Text("Clear")
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    Button(onClick = {
                        penColor.value = Color.Red
                    }) {
                        Text("Red")
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    Button(onClick = {
                        penColor.value = Color.Black
                    }) {
                        Text("Black")
                    }
                }
                //Text(text = "SVG: " + mutableSvg.value)


        }
       /* Column(
            modifier = Modifier
                .weight(3.0f)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

        }*/
    }
}


