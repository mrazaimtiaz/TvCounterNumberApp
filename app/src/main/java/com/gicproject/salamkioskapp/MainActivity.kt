package com.gicproject.salamkioskapp


import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.hardware.usb.*
import android.os.Bundle
import android.telephony.TelephonyManager
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
import dagger.hilt.android.AndroidEntryPoint
import org.simalliance.openmobileapi.SEService
import org.simalliance.openmobileapi.SEService.CallBack
import org.simalliance.openmobileapi.Session
import kotlin.experimental.and


private const val TAG = "MainActivity"
@AndroidEntryPoint
class MainActivity : ComponentActivity() {





    private var viewModel: MyViewModel? = null

    private val ISD_AID = byteArrayOf(0x00.toByte(), 0xCA.toByte(), 0x00.toByte(), 0x5A.toByte())


    private val telephonyManager: TelephonyManager? = null

    private var _session: Session? = null
    public var _service: SEService? = null

    private fun bytesToString(bytes: ByteArray): String? {
        val sb = StringBuffer()
        for (b in bytes) sb.append(String.format("%02x ", b and 0xFF.toByte()))
        val stringOfByte = sb.toString()
        Log.e("TAG," ,"----------- stringOfByte $stringOfByte")
        return stringOfByte
    }


    fun testLogicalChannel(aid: ByteArray?) {
        try {
            logText(
                """
                LogicalChannel test: ${
                    if (aid == null) "default applet" else bytesToString(
                        aid
                    )
                }
                
                """.trimIndent()
            )
            val channel = _session!!.openLogicalChannel(aid)
            val cmd = byteArrayOf(0x00.toByte(), 0xCA.toByte(), 0x00.toByte(), 0x5A.toByte())
            //            byte[] cmd = new byte[]{(byte)0xA0, (byte)0xA4, (byte)0x00, (byte)0x00, (byte)0x02, (byte)0x6F, (byte)0x7E};
            // original            byte[] cmd = new byte[]{(byte) 0x80, (byte) 0xCA, (byte) 0x9F, 0x7F, 0x00};
            logText(
                """ -> ${bytesToString(cmd)}
"""
            )
            val rsp = channel.transmit(cmd)
            logText(
                """ <- ${bytesToString(rsp)}

"""
            )
            channel.close()
        } catch (e: java.lang.Exception) {
            logText(
                """
                Exception on LogicalChannel: ${e.message}
                
                
                """.trimIndent()
            )
        }
    }

    fun write(connection: UsbDeviceConnection, epOut: UsbEndpoint?, command: ByteArray) {
      var  result = java.lang.StringBuilder()
        connection.bulkTransfer(epOut, command, command.size, 10000)
        //For Printing logs you can use result variable
        for (bb in command) {
            result.append(String.format(" %02X ", bb))
        }
    }

    fun read(connection: UsbDeviceConnection, epIn: UsbEndpoint): Int {
       var result = java.lang.StringBuilder()
        val buffer = ByteArray(epIn.maxPacketSize)
        var byteCount = 0
        byteCount = connection.bulkTransfer(epIn, buffer, buffer.size, 10000)

//For Printing logs you can use result variable
        if (byteCount >= 0) {
            for (bb in buffer) {
                result.append(String.format(" %02X ", bb))
            }

            //Buffer received was : result.toString()
        } else {
            //Something went wrong as count was : " + byteCount
        }
        Log.d(TAG, "read DataZ: $result $byteCount")
        return byteCount
    }

    private fun performTest() {
        val readers = _service!!.readers
        logText("Available readers:  \n")
        for (reader in readers) logText(
            """	${reader.name}   - ${if (reader.isSecureElementPresent) "present" else "absent"}
"""
        )
        if (readers.size == 0) {
            logText("No reader available \n")
            return
        }
        for (reader in readers) {
            if (!reader.isSecureElementPresent) continue
            logText(
                """
                
                --------------------------------
                Selected reader: "${reader.name}"
                
                """.trimIndent()
            )
            try {
                _session = reader.openSession()
            } catch (e: java.lang.Exception) {
                e.message?.let { logText(it) }
            }
            if (_session == null) continue
            try {
                val atr: ByteArray = _session!!.getATR()
                logText(
                    """
                    ATR: ${if (atr == null) "unavailable" else bytesToString(atr)}
                    
                    
                    """.trimIndent()
                )
            } catch (e: java.lang.Exception) {
                logText(
                    """
                    Exception on getATR(): ${e.message}
                    
                    
                    """.trimIndent()
                )
            }
            testBasicChannel(null)
            testBasicChannel(ISD_AID)
            testLogicalChannel(null)
            testLogicalChannel(ISD_AID)
            _session!!.close()
        }
    }

    private fun logText(message: String) {
        Log.d("TAG", "------------ message $message")
   
    }

    fun testBasicChannel(aid: ByteArray?) {
        try {
            logText(
                """
                BasicChannel test: ${
                    if (aid == null) "default applet" else bytesToString(
                        aid
                    )
                }
                
                """.trimIndent()
            )
            val channel = _session!!.openBasicChannel(aid)

            // (byte)0xA0, (byte)0xA4, (byte)0x00, (byte)0x00, (byte)0x02, (byte)0x6F, (byte)0x7E
            // (byte)0xA0, (byte)0xA4, (byte)0x00, (byte)0x00, (byte)0x02, (byte)0x7F, (byte)0x20
            // (byte)0xA0, (byte)0xA4, (byte)0x00, (byte)0x00, (byte)0x02, (byte)0x7F, (byte)0x20
            val cmd = byteArrayOf(0x00.toByte(), 0xCA.toByte(), 0x00.toByte(), 0x5A.toByte())
            //            byte[] cmd = new byte[]{(byte)0xA0, (byte)0xA4, (byte)0x00, (byte)0x00, (byte)0x02, (byte)0x6F, (byte)0x7E};
            //            byte[] cmd = new byte[]{(byte) 0xA0, (byte) 0xA4, (byte) 0x00, (byte) 0x00, (byte) 0x02, (byte) 0x6F, (byte) 0x07};
            // original           byte[] cmd = new byte[]{(byte) 0x80, (byte) 0xCA, (byte) 0x9F, 0x7F, 0x00};
            logText(
                """ -> ${bytesToString(cmd)}
"""
            )
            val rsp = channel.transmit(cmd)
            logText(
                """ <- ${bytesToString(rsp)}

"""
            )
            channel.close()
        } catch (e: java.lang.Exception) {
            logText(
                """
                Exception on BasicChannel: ${e.message}
                
                
                """.trimIndent()
            )
        }
    }


    /**
     * Callback interface if informs that this SEService is connected to the SmartCardService
     */
    inner  class SEServiceCallback : CallBack {
        override fun serviceConnected(service: SEService) {

            _service = service
            performTest()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Allows you to enumerate and communicate with connected USB devices.
        //Allows you to enumerate and communicate with connected USB devices.
  
        val mUsbManager = getSystemService(Context.USB_SERVICE) as UsbManager
        //Explicitly asking for permission
        //Explicitly asking for permission
        val ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION"
        val mPermissionIntent =
            PendingIntent.getBroadcast(this, 0, Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_IMMUTABLE)
        val deviceList = mUsbManager.deviceList
        Log.d("TAG", "onCreate: devicelist ${deviceList.keys}")
        val device = deviceList["/dev/bus/usb/001/003"]
        Log.d("TAG", "onCreate: device ${device}")


         var permission =   mUsbManager.requestPermission(device, mPermissionIntent)


        var usbInterface: UsbInterface

        val connection = mUsbManager.openDevice(device)

        Log.d("TAG", "onCreate: connection ${permission} ${connection}")

        _service = SEService(this)
        performTest()

        setContent {
            viewModel = hiltViewModel()

            SalamKioskAppTheme(darkTheme = false) {

                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background
                ) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController, startDestination = Screen.SelectOptionScreen.route
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
                            var extra = navController.previousBackStackEntry?.savedStateHandle?.get<Boolean?>(
                            Constants.STATE_EXTRA)
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
