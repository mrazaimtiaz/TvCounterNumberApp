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
import android.os.Environment
import android.os.Parcelable
import android.util.Log
import android.widget.Toast


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
import com.gicproject.salamkioskapp.pacicardlibrary.PaciCardReaderAbstract
import com.gicproject.salamkioskapp.pacicardlibrary.PaciCardReaderMAV3
import com.gicproject.salamkioskapp.presentation.*
import com.gicproject.salamkioskapp.ui.theme.SalamKioskAppTheme
import com.gicproject.salamkioskapp.utils.PermissionUtil
import com.identive.libs.SCard
import com.identive.libs.SCard.SCardIOBuffer
import com.identive.libs.WinDefs
import com.szsicod.print.escpos.PrinterAPI
import com.szsicod.print.io.InterfaceAPI
import com.szsicod.print.io.USBAPI
import com.szsicod.print.io.UsbNativeAPI
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import okhttp3.internal.and
import java.io.File
import java.util.*
import java.util.concurrent.ConcurrentHashMap


private const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var viewModel: MyViewModel? = null

    val scard = SCard()

    var initialized = false

    var deviceList = ArrayList<String>()
    var readers: Array<CharSequence>? = null


    private var mPrinter: PrinterAPI? = null


    fun hexToByteArray(hexString: String): ByteArray {
        val len = hexString.length
        val data = ByteArray(len / 2)
        var i = 0
        while (i < len) {
            data[i / 2] = ((((hexString[i].digitToIntOrNull(16)
                ?: (-1 shl 4)) or hexString[i + 1].digitToIntOrNull(16)!!) ?: -1)).toByte()
            i += 2
        }
        return data
    }

    override fun onPause() {
        super.onPause()
        scard.setSCardListener(baseContext, "", null)
    }

    init {
        System.loadLibrary("usb1.0");
        //串口
        System.loadLibrary("serial_icod");
        //图片
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


        /*var status: Long
        if (!initialized) {
            status = scard.SCardEstablishContext(baseContext)
            if (0L != status) {
                Log.d(TAG, "onCreate: Establish context error")
            } else {
                status = scard.SCardListReaders(baseContext, deviceList)
                if (0L != status) {
                    Log.d(TAG, "onCreate: List readers error")
                } else {
                    readers = deviceList.toTypedArray<CharSequence>()
                    Log.d(TAG, "onCreate: ${readers?.get(0)}")
                    initialized = true
                }
            }
        }
        onCardEvent()*/

        mPrinter = PrinterAPI.getInstance()

        initPermission()


       CoroutineScope(Dispatchers.IO).launch {
           delay(7000)
           funcPrinterConnect();
       }


        /* //Allows you to enumerate and communicate with connected USB devices.
         //Allows you to enumerate and communicate with connected USB devices.



         var permission =   mUsbManager.requestPermission(device, mPermissionIntent)


         var usbInterface: UsbInterface

         val connection = mUsbManager.openDevice(device)

         Log.d("TAG", "onCreate: connection ${permission} ${connection}")

         var epOut: UsbEndpoint? = null
         var epIn: UsbEndpoint? = null
         for (i in 0 until device!!.interfaceCount) {
             usbInterface = device!!.getInterface(i)
             connection.claimInterface(usbInterface, true)
             for (j in 0 until usbInterface.endpointCount) {
                 val ep = usbInterface.getEndpoint(j)
                 if (ep.type == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                     if (ep.direction == UsbConstants.USB_DIR_OUT) {
                         // from host to device
                         epOut = ep
                     } else if (ep.direction == UsbConstants.USB_DIR_IN) {
                         // from device to host
                         epIn = ep
                     }
                 }
             }
         }

         write(connection, epOut, "62000000000000000000".toByteArray());
         if (epIn != null) {
             read(connection, epIn)
         };

         var result = StringBuilder()
         var buffer = ByteArray(epIn!!.maxPacketSize)
         var byteCount = 0
         byteCount = connection.bulkTransfer(epIn, buffer, buffer.size, 10000)












         //For Printing logs you can use result variable

         //For Printing logs you can use result variable
         if (byteCount >= 0) {
             for (bb in buffer) {
                 result.append(String.format(" %02X ", bb))
             }

             //Buffer received was : result.toString()
         } else {
             //Something went wrong as count was : " + byteCount
         }

         Log.d("TAG", "onCreate: byteCount $byteCount ")

         result = java.lang.StringBuilder()


         val data = byteArrayOf(0x00.toByte(), 0xCA.toByte(), 0x00.toByte(), 0x5A.toByte())

         var dataTransferred: Int =
             connection.bulkTransfer(epOut, data, data.size, 1000)
         if (!(dataTransferred == 0 || dataTransferred == data.size)) {
             throw Exception("Error durring sending command [" + dataTransferred + " ; " + data.size + "]") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
         }

         val responseBuffer = epIn?.let { ByteArray(it.maxPacketSize) }
         if (responseBuffer != null) {
             dataTransferred = connection.bulkTransfer(
                 epIn,
                 responseBuffer,
                 responseBuffer.size,
                 1000
             )
         }
         if (dataTransferred >= 0) {
             Log.d("TAG", "onCreate:responseBuffer ${responseBuffer.contentToString()} ")
         }

         CoroutineScope(Dispatchers.Default).launch {
             delay(3000)
             val ReaderHandler: ConcurrentHashMap<String?, PaciCardReaderAbstract?> =
                 ConcurrentHashMap<String?, PaciCardReaderAbstract?>()
             val paci = PaciCardReaderMAV3(
                 "true" == System.getProperty(
                     "sun.security.smartcardio.t0GetResponse", "true"
                 ), null,connection,epOut, epIn
             )

             var civilidText = paci!!.GetData("", "CIVIL-NO")
             Log.d("TAG", "onCreate:civilidText $civilidText")
         }
 */


        setContent {
            viewModel = hiltViewModel()

            SalamKioskAppTheme(darkTheme = false) {

                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background
                ) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = Screen.SelectOptionScreen.route
                    ) {
                        composable(
                            route = Screen.SelectOptionScreen.route
                        ) {
                            SelectOptionScreen(navController, viewModel!!)
                        }
                        composable(
                            route = Screen.SelectDepartmentScreen.route
                        ) {
                            SelectDepartmentScreen(navController, viewModel!!)
                        }
                        composable(
                            route = Screen.SelectDoctorScreen.route
                        ) {
                            SelectDoctorScreen(navController, viewModel!!)
                        }
                        composable(
                            route = Screen.SelectDoctorTimeScreen.route
                        ) {
                            SelectDoctorTimeScreen(navController, viewModel!!)
                        }
                        composable(
                            route = Screen.DoctorPayScreen.route
                        ) {
                            DoctorPayScreen(navController, viewModel!!)
                        }
                        composable(
                            route = Screen.InsertKnetScreen.route
                        ) {
                            InsertKnetScreen(navController, viewModel!!)
                        }
                        composable(
                            route = Screen.InsertCivilIdScreen.route
                        ) {
                            var extra =
                                navController.previousBackStackEntry?.savedStateHandle?.get<Boolean?>(
                                    Constants.STATE_EXTRA
                                )
                            InsertCivilIdScreen(extra, navController, viewModel!!)
                        }
                        composable(
                            route = Screen.SelectServiceScreen.route
                        ) {
                            SelectServiceScreen(navController, viewModel!!)
                        }
                        composable(
                            route = Screen.SelectChildServiceScreen.route
                        ) {
                            SelectChildServiceScreen(navController, viewModel!!)
                        }

                        composable(
                            route = Screen.LinkPayScreen.route
                        ) {
                            LinkPayScreen(navController, viewModel!!)
                        }
                    }
                }
            }
        }
    }

    fun onCardEvent() {
        scard.setSCardListener(
            baseContext, readers!![0] as String
        ) { event ->
            Log.d("testcardstatus", "listener-event $event")
            Log.d("testcardstatus", "listener-event $event")
            if (0 == event) {
                Log.d(TAG, "onCardEvent: card removed")
            } else if (1 == event) {
                Log.d(TAG, "onCardEvent: card present")
                var status: Long = 0
                if (!initialized) {
                    status = scard.SCardEstablishContext(baseContext)
                    if (0L != status) {
                        Log.d(TAG, "onCreate: Establish context error")
                    } else {
                        status = scard.SCardListReaders(baseContext, deviceList)
                        if (0L != status) {
                            Log.d(TAG, "onCreate: List readers error")
                        } else {
                            readers = deviceList.toTypedArray<CharSequence>()
                            Log.d(TAG, "onCreate: ${readers?.get(0)}")
                            initialized = true
                        }
                    }
                }
                if (initialized) {
                    status = scard.SCardConnect(
                        readers?.get(0) as String,
                        WinDefs.SCARD_SHARE_EXCLUSIVE,
                        WinDefs.SCARD_PROTOCOL_TX.toInt()
                    )
                    Log.d(TAG, "onClick: 1 $status")
                    if (0L == status) {
                        val value = "A0000006"
                        val length = value.length
                        val valueByte: ByteArray = hexToByteArray(value)
                        val inbuf = byteArrayOf(0x00, 0xA4.toByte(), 0x04, 0x00)
                        Log.d(TAG, "onClick: " + valueByte.size)
                        val transmit: SCardIOBuffer = scard.SCardIOBuffer()
                        transmit.setnInBufferSize(valueByte.size)
                        transmit.abyInBuffer = valueByte
                        transmit.setnOutBufferSize(0x8000)
                        transmit.abyOutBuffer = ByteArray(0x8000)
                        val status1: Long = scard.SCardTransmit(transmit)
                        Log.d(
                            TAG,
                            "onClick:resul " + transmit.abyInBuffer + "---" + transmit.abyInBuffer.size
                        )
                        var rstr = ""
                        var sstr = ""
                        for (i in 0 until transmit.getnBytesReturned()) {
                            val temp: Int = transmit.abyOutBuffer[i] and 0xFF
                            if (temp < 16) {
                                rstr =
                                    rstr.uppercase(Locale.getDefault()) + "0" + Integer.toHexString(
                                        transmit.abyOutBuffer[i].toInt()
                                    )
                                sstr =
                                    sstr.uppercase(Locale.getDefault()) + "0" + Integer.toHexString(
                                        transmit.abyOutBuffer[i].toInt()
                                    ) + " "
                            } else {
                                rstr =
                                    rstr.uppercase(Locale.getDefault()) + Integer.toHexString(temp)
                                sstr =
                                    sstr.uppercase(Locale.getDefault()) + Integer.toHexString(temp) + " "
                            }
                        }
                        Log.d(TAG, "onClick:result $rstr")
                        Log.d(TAG, "onClick:result1 $sstr")
                    }
                }

                val ReaderHandler: ConcurrentHashMap<String?, PaciCardReaderAbstract?> =
                    ConcurrentHashMap<String?, PaciCardReaderAbstract?>()
                val paci = PaciCardReaderMAV3(
                    "true" == System.getProperty(
                        "sun.security.smartcardio.t0GetResponse", "true"
                    ), scard
                )

                CoroutineScope(Dispatchers.IO).launch {
                    var civilidText: String = ""
                    var serialNoText: String = ""
                    var fullNameText: String = ""
                    var firstNameText: String = ""
                    var secondNameText: String = ""
                    var thirdNameText: String = ""
                    var fourNameText: String = ""
                    var fullNameArText: String = ""
                    var firstNameArText: String = ""
                    var secondNameArText: String = ""
                    var thirdNameArText: String = ""
                    var fourNameArText: String = ""
                    var fullAddressText: String = ""
                    var genderText: String = ""
                    var bloodGroupText: String = ""
                    var passportNoText: String = ""
                    var occupationText: String = ""
                    var dobText: String = ""
                    var nationalityText: String = ""
                    var expiryText: String = ""
                    var tel1Text: String = ""
                    var tel2Text: String = ""
                    var emailText: String = ""


                    try {
                        var text = "";
                        try {
                            civilidText = paci!!.GetData("", "CIVIL-NO")
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                        try {
                            firstNameText = paci.GetData("1", "LATIN-NAME-1")
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                        try {
                            secondNameText = paci.GetData("1", "LATIN-NAME-2")
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                        try {
                            thirdNameText = paci.GetData("1", "LATIN-NAME-3")
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                        try {
                            firstNameArText = paci!!.GetData("", "ARABIC-NAME-1")
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                        try {
                            secondNameArText = paci.GetData("", "ARABIC-NAME-2")
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                        try {
                            thirdNameArText = paci.GetData("", "ARABIC-NAME-3")
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                        try {
                            fourNameArText = paci.GetData("", "ARABIC-NAME-4")
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                        /* try {
                             genderText = paci.GetData("1", "SEX-LATIN-TEXT")
                         } catch (e: java.lang.Exception) {
                             e.printStackTrace()
                         }*/
                        try {
                            tel1Text = paci.GetData("1", "TEL-1")
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                        /* try {
                             nationalityText = paci.GetData("1", "NATIONALITY-LATIN-ALPHA-CODE")

                         } catch (e: java.lang.Exception) {
                             e.printStackTrace()
                         }*/
                        // ////MOI-REFERENCE  //TEL-2
                        /*try {
                            tel2Text = paci.GetData("1", "MOI-REFERENCE-INDIC")

                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }*/
                        try {
                            emailText = paci.GetData("1", "MOI-REFERENCE")
                            ////MOI-REFERENCE  //E-MAIL-ADDRESS
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                        /* try {
                             dobText = paci.GetData("1", "BIRTH-DATE")
                         } catch (e: java.lang.Exception) {
                             e.printStackTrace()
                         }*/
                        try {
                            expiryText = paci.GetData("1", "CARD-EXPIRY-DATE")
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                        fullNameArText =
                            "$firstNameArText $secondNameArText $thirdNameArText $fourNameArText"
                        fullNameText = "$firstNameText $secondNameText $thirdNameText $fourNameText"

                        try {
                            expiryText = expiryText.substring(0, 4) + "-" + expiryText.substring(
                                4, 6
                            ) + "-" + expiryText.substring(6, 8)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        try {
                            dobText = dobText.substring(0, 4) + "-" + dobText.substring(
                                4, 6
                            ) + "-" + dobText.substring(6, 8)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        Log.d(TAG, "onCardEvent: get result $civilidText $firstNameArText $secondNameArText `6f")
                        withContext(Dispatchers.Main){
                            Toast.makeText(this@MainActivity," $civilidText $firstNameArText",Toast.LENGTH_LONG).show()

                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

            }
        }

    }

    private var mUsbBroadCastReceiver: UsbBroadCastReceiver? = null
     private fun funcPrinterConnect() {
      CoroutineScope(Dispatchers.IO).launch {
            if (mPrinter!!.isConnect) {
                mPrinter?.disconnect()
            }


            if (mUsbBroadCastReceiver == null) {
                val intentFilter = IntentFilter()
                intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
                intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
                mUsbBroadCastReceiver = UsbBroadCastReceiver()
                registerReceiver(mUsbBroadCastReceiver, intentFilter)
            }

            var io: InterfaceAPI? = null                   // USB
                  //  io = USBAPI(this@MainActivity)

                    io = UsbNativeAPI()



            if (io != null) {
                val ret = mPrinter?.connect(io)
            }

          try {
              // 打印方法：printString
              // 打印例范文本
              val str = """
                WelcomeWelcomeWelcome
                Welcome
                Welcome
                Welcome
                Welcome
                Welcome
                Welcome
                123456789
                123456789
                123456789
                123456789
                
                """.trimIndent()
              mPrinter!!.setPrintColorSize(4)
              mPrinter?.printString("Text test printing:\n")
              mPrinter?.printFeed()
              mPrinter?.printString(str, "GBK", true)
              mPrinter?.printFeed()
              val ret = mPrinter?.cutPaper(66, 0)

          } catch (e: java.lang.Exception) {
              e.printStackTrace()
          }
        }
    }

    // 打印机断开连接
     private fun funcPrinterDisConnect() {
       CoroutineScope(Dispatchers.IO).launch {
            try {
                // 打印方法：disconnect
                // 打印例范文本
                val ret = mPrinter!!.disconnect()

            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }


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
private class UsbBroadCastReceiver : BroadcastReceiver() {
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