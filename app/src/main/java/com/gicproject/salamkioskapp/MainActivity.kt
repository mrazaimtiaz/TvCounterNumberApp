package com.gicproject.salamkioskapp


import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.os.Parcelable
import android.util.Log


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
import kotlinx.coroutines.*
import java.util.*


private const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var viewModel: MyViewModel? = null



    val scard = SCard()


    private var mPrinter: PrinterAPI? = null



    override fun onPause() {
        super.onPause()
        scard.setSCardListener(baseContext, "", null)
    }

    init {
        System.loadLibrary("usb1.0");
        System.loadLibrary("serial_icod");
        System.loadLibrary("image_icod");
    }

    private fun initPermission() {
        val permissionArray = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        val noCheckPermissionArray: Array<String>? =
            PermissionUtil.checkPermissions(this, permissionArray)
        if (noCheckPermissionArray != null && noCheckPermissionArray.isNotEmpty()) {
            PermissionUtil.applyPermission(this, noCheckPermissionArray)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        //final SCard scard = new SCard();
      //  scard.USBRequestPermission(applicationContext)
        var usbManager_ = getSystemService(USB_SERVICE) as UsbManager;
        var permissionIntent = PendingIntent.getBroadcast(this, 0,  Intent("com.android.example.USB_PERMISSION"), PendingIntent.FLAG_IMMUTABLE);

        val deviceListTemp: HashMap<String, UsbDevice> = usbManager_.deviceList
        val filter = IntentFilter("com.android.example.USB_PERMISSION")
        Log.i(TAG, "registered broadcast receiver!")
        val deviceIterator: Iterator<UsbDevice> = deviceListTemp.values.iterator()
        while (deviceIterator.hasNext()) {
            val device = deviceIterator.next()
            Log.i(
                TAG,
                "Camera: device Id " + device.deviceId + " device mName : " + device.deviceName
            )
            if (!usbManager_.hasPermission(device)) {
                Log.i(TAG, "requesting permission")
                usbManager_.requestPermission(device, permissionIntent)
            }
        }


        mPrinter = PrinterAPI.getInstance()


        initPermission()



        setContent {
            viewModel = hiltViewModel()

            viewModel?.initializedCardReader(scard,baseContext)

                if (mUsbBroadCastReceiver == null) {
                    val intentFilter = IntentFilter()
                    intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
                    intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
                    mUsbBroadCastReceiver = UsbBroadCastReceiver()
                    registerReceiver(mUsbBroadCastReceiver, intentFilter)
                    viewModel?.initPrinter(mPrinter,this@MainActivity)

                }



            SalamKioskAppTheme(darkTheme = false) {

                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background
                ) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = Screen.SelectDepartmentScreen.route
                    ) {
                        composable(
                            route = Screen.SelectOptionScreen.route
                        ) {
                            viewModel?.readCivilIdOff()
                            SelectOptionScreen(navController, viewModel!!)
                        }
                        composable(
                            route = Screen.SelectDepartmentScreen.route
                        ) {
                            viewModel?.readCivilIdOff()
                            SelectDepartmentScreen(navController, viewModel!!)
                        }
                        composable(
                            route = Screen.SelectDoctorScreen.route
                        ) {
                            viewModel?.readCivilIdOff()
                            SelectDoctorScreen(navController, viewModel!!)
                        }
                        composable(
                            route = Screen.SelectDoctorTimeScreen.route
                        ) {
                            viewModel?.readCivilIdOff()
                            SelectDoctorTimeScreen(navController, viewModel!!)
                        }
                        composable(
                            route = Screen.DoctorPayScreen.route
                        ) {
                            viewModel?.readCivilIdOff()
                            DoctorPayScreen(navController, viewModel!!)
                        }
                        composable(
                            route = Screen.InsertKnetScreen.route
                        ) {
                            viewModel?.readCivilIdOff()
                            InsertKnetScreen(navController, viewModel!!)
                        }
                        composable(
                            route = Screen.InsertCivilIdScreen.route
                        ) {
                            viewModel?.readCivilIdOn()
                            var extra =
                                navController.previousBackStackEntry?.savedStateHandle?.get<Boolean?>(
                                    Constants.STATE_EXTRA
                                )
                            InsertCivilIdScreen(extra, navController, viewModel!!)
                        }
                        composable(
                            route = Screen.SelectServiceScreen.route
                        ) {
                            viewModel?.readCivilIdOff()
                            SelectServiceScreen(navController, viewModel!!)
                        }
                        composable(
                            route = Screen.SelectChildServiceScreen.route
                        ) {
                            viewModel?.readCivilIdOff()
                            SelectChildServiceScreen(navController, viewModel!!)
                        }

                        composable(
                            route = Screen.LinkPayScreen.route
                        ) {
                            viewModel?.readCivilIdOff()
                            LinkPayScreen(navController, viewModel!!)
                        }
                        composable(
                            route = Screen.SettingScreen.route
                        ) {
                            viewModel?.readCivilIdOff()
                            SettingScreen(navController, viewModel!!)
                        }
                    }
                }
            }
        }
    }



    private var mUsbBroadCastReceiver: UsbBroadCastReceiver? = null



    // 打印文本



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


//usb 插拔监听
class UsbBroadCastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val device = intent.getParcelableExtra<Parcelable>(UsbManager.EXTRA_DEVICE) as UsbDevice?
    /*    when (intent.action) {
            UsbManager.ACTION_USB_DEVICE_ATTACHED ->                     // 当USB设备连接到USB总线时，在主机模式下发送此意图。
                onToast("plug-in device vid:" + device!!.vendorId + "  pid:" + device.productId)
            UsbManager.ACTION_USB_DEVICE_DETACHED ->                     // 当USB设备在主机模式下脱离USB总线时发送此意图。
            //    onToast("Pull out device vid:" + device!!.vendorId + "  pid:" + device.productId)
        }*/
    }
}