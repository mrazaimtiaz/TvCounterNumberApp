package com.gicproject.salamkioskapp.presentation

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavController
import com.gicproject.salamkioskapp.Screen
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun SettingScreen(
    navController: NavController,
    viewModel: MyViewModel,
) {

    val context = LocalContext.current
    val state = viewModel.stateSetting.value
    val isRefreshing by viewModel.isRefreshingSetting.collectAsState()



    var isLocationExpanded by remember { mutableStateOf(false) }

    var selectedIndex by remember { mutableStateOf(-1) }




    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = MaterialTheme.colors.primary,
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack(Screen.SelectDepartmentScreen.route, false)
                    }) {
                        Icon(
                            Icons.Filled.KeyboardArrowLeft,
                            "back arrow",
                            tint = Color.Black,
                            modifier = Modifier
                                .size(100.dp, 100.dp)
                        )
                    }

                },
            )
        },
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
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(top = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator()
                    }

                    IntentToSetting(context)
                    Button(
                        modifier = Modifier.padding(top = 30.dp),
                        onClick = {
                            viewModel.funcPrinterConnect()

                        }) {
                        Icon(
                            Icons.Filled.Settings,
                            "setting",
                            tint = Color.White,
                            modifier = Modifier
                                .size(30.dp, 30.dp)
                        )

                        Text(text = "Print Test", Modifier.padding(start = 10.dp))
                    }

                    Button(
                        modifier = Modifier.padding(top = 30.dp),
                        onClick = {
                            viewModel.initializeReaderAgain()

                        }) {
                        Icon(
                            Icons.Filled.Settings,
                            "setting",
                            tint = Color.White,
                            modifier = Modifier
                                .size(30.dp, 30.dp)
                        )

                        Text(text = "Initialize Card Reader", Modifier.padding(start = 10.dp))
                    }
                }
                if (state.error.isNotBlank()) {
                }
            }
        }
    }
}

@Composable
fun IntentToSetting(context: Context) {
    Button(
        modifier = Modifier.padding(top = 30.dp),
        onClick = {
            startActivity(context, Intent(Settings.ACTION_SETTINGS), null)

        }) {
        Icon(
            Icons.Filled.Settings,
            "setting",
            tint = Color.White,
            modifier = Modifier
                .size(30.dp, 30.dp)
        )

        Text(text = "Open Settings", Modifier.padding(start = 10.dp))
    }
}


