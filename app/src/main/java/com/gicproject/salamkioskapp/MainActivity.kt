package com.gicproject.salamkioskapp


/*import com.myfatoorah.sdk.entity.executepayment.MFExecutePaymentRequest
import com.myfatoorah.sdk.entity.executepayment_cardinfo.MFCardInfo
import com.myfatoorah.sdk.entity.executepayment_cardinfo.MFDirectPaymentResponse
import com.myfatoorah.sdk.entity.initiatepayment.MFInitiatePaymentRequest
import com.myfatoorah.sdk.entity.initiatepayment.MFInitiatePaymentResponse
import com.myfatoorah.sdk.entity.paymentstatus.MFGetPaymentStatusResponse
import com.myfatoorah.sdk.enums.MFAPILanguage
import com.myfatoorah.sdk.enums.MFCountry
import com.myfatoorah.sdk.enums.MFCurrencyISO
import com.myfatoorah.sdk.enums.MFEnvironment
import com.myfatoorah.sdk.views.MFResult
import com.myfatoorah.sdk.views.MFSDK*/
import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.nfc.NfcAdapter
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gicproject.salamkioskapp.common.Constants
import com.gicproject.salamkioskapp.presentation.*
import com.gicproject.salamkioskapp.ui.theme.SalamKioskAppTheme
import com.gicproject.salamkioskapp.utils.PermissionUtil
import com.identive.libs.SCard
import com.szsicod.print.escpos.PrinterAPI
import dagger.hilt.android.AndroidEntryPoint


private const val TAG = "MainActivity"

//,  NfcAdapter.ReaderCallback
@AndroidEntryPoint
class MainActivity : ComponentActivity(){

    private var viewModel: MyViewModel? = null




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            viewModel = hiltViewModel()
            SalamKioskAppTheme(darkTheme = false) {

                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background
                ) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = Screen.SelectDepartmentScreen.route
                    ) {
                        composable(
                            route = Screen.SelectDepartmentScreen.route
                        ) {
                            SelectDepartmentScreen(navController, viewModel!!)
                        }
                        composable(
                            route = Screen.SettingScreen.route
                        ) {
                            SettingScreen(navController, viewModel!!)
                        }
                    }
                }
            }
        }
    }




}


@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SalamKioskAppTheme {
        Greeting("Android")
    }
}

