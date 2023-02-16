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
import android.graphics.BitmapFactory
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
import com.gicproject.salamkioskapp.domain.model.SelectDepartment
import com.gicproject.salamkioskapp.presentation.*
import com.gicproject.salamkioskapp.ui.theme.SalamKioskAppTheme
import com.gicproject.salamkioskapp.utils.PermissionUtil
import com.identive.libs.SCard
import com.szsicod.print.escpos.PrinterAPI
import com.szsicod.print.utils.BitmapUtils
import dagger.hilt.android.AndroidEntryPoint


private const val TAG = "MainActivity"

//,  NfcAdapter.ReaderCallback
@AndroidEntryPoint
class MainActivity : ComponentActivity(){

    private var viewModel: MyViewModel? = null


    val scard = SCard()


    private var mPrinter: PrinterAPI? = null

    private var mNfcAdapter: NfcAdapter? = null;


    override fun onPause() {
        super.onPause()
        //identiv card reader
        scard.setSCardListener(baseContext, "", null)
        //if (mNfcAdapter != null) mNfcAdapter!!.disableReaderMode(this)
    }


    public override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
    }

/*
    fun setMyFatoorah(){

        MFSDK.init(MY_FATOORAH_TOKEN, MFCountry.OMAN, MFEnvironment.TEST)

        val request = MFInitiatePaymentRequest(0.200, MFCurrencyISO.OMAN_OMR)
        MFSDK.initiatePayment(
            request,
            MFAPILanguage.EN
        ) { result: MFResult<MFInitiatePaymentResponse> ->
            when (result) {
                is MFResult.Success ->{

                    Log.d(TAG, "Response: initate " + Gson().toJson(result.response))

// executePayment

                    val exectueRequest = MFExecutePaymentRequest(1, 0.200)
                    MFSDK.executePayment(
                        this,
                        exectueRequest,
                        MFAPILanguage.EN,
                        onInvoiceCreated = {
                            Log.d(TAG, "invoiceId: $it")
                        }
                    ) { invoiceId: String, result: MFResult<MFGetPaymentStatusResponse> ->
                        when (result) {
                            is MFResult.Success ->
                                Log.d(TAG, "Response: execute " + Gson().toJson(result.response))
                            is MFResult.Fail ->
                                Log.d(TAG, "Fail: execute " + Gson().toJson(result.error))
                        }
                    }

                    val mfCardInfo = MFCardInfo("8888880000000001", "09", "25", "1000", "test test")
                    val requestDirect = MFExecutePaymentRequest(2, 0.100)

                    MFSDK.executeDirectPayment(
                        this,
                        requestDirect,
                        mfCardInfo,
                        MFAPILanguage.EN,
                        onInvoiceCreated = {
                            Log.d(TAG, "invoiceId: $it")
                        }
                    ) { invoiceId: String, result: MFResult<MFDirectPaymentResponse> ->
                        when (result) {
                            is MFResult.Success ->
                                Log.d(TAG, "Response:direct " + Gson().toJson(result.response))
                            is MFResult.Fail ->
                                Log.d(TAG, "Fail:direct " + Gson().toJson(result.error))
                        }
                    }
                }

                is MFResult.Fail ->
                    Log.d(TAG, "Fail: initate " + Gson().toJson(result.error))
            }
        }


    }
*/
/*    override fun onTagDiscovered(tag: Tag?){
// Read and or write to Tag here to the appropriate Tag Technology type class
        // in this example the card should be an Ndef Technology Type
        // Read and or write to Tag here to the appropriate Tag Technology type class
        // in this example the card should be an Ndef Technology Type

        val SELECT = byteArrayOf(
            0x00.toByte(), 0xA4.toByte(), 0x04.toByte(), 0x00.toByte(), 0x0A.toByte(),  // Length
            0x63, 0x64, 0x63, 0x00, 0x00, 0x00, 0x00, 0x32, 0x32, 0x31 // AID
        )
        if (tag != null) {
            val tagIsoDep = IsoDep.get(tag)
            val provider: IProvider = Provider(tagIsoDep)
// Define config
// Define config
            val config: EmvTemplate.Config = EmvTemplate.Config()
                .setContactLess(true) // Enable contact less reading (default: true)
                .setReadAllAids(true) // Read all aids in card (default: true)
                .setReadTransactions(true) // Read all transactions (default: true)
                .setReadCplc(false) // Read and extract CPCLC data (default: false)
                .setRemoveDefaultParsers(false) // Remove default parsers for GeldKarte and EmvCard (default: false)
                .setReadAt(true) // Read and extract ATR/ATS and description

// Create Parser
// Create Parser
            val parser = EmvTemplate.Builder() //
                .setProvider(provider) // Define provider
                .setConfig(config) // Define config
                //.setTerminal(terminal) (optional) you can define a custom terminal implementation to create APDU
                .build()

// Read card

// Read card
            val card: EmvCard = parser.readEmvCard()

            Log.d(TAG, "onCreate card info: ${card}")
        }


        val mNdef = Ndef.get(tag)

        // Check that it is an Ndef capable card

        // Check that it is an Ndef capable card
        if (mNdef != null) {

            // If we want to read
            // As we did not turn on the NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK
            // We can get the cached Ndef message the system read for us.
            val mNdefMessage = mNdef.cachedNdefMessage


            // Or if we want to write a Ndef message

            // Create a Ndef Record
            val mRecord = NdefRecord.createTextRecord("en", "English String")

            // Add to a NdefMessage
            val mMsg = NdefMessage(mRecord)

            // Catch errors
            try {
                mNdef.connect()
                mNdef.writeNdefMessage(mMsg)

                // Success if got to here
                runOnUiThread {
                    Toast.makeText(
                        applicationContext,
                        "Write to NFC Success",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                // Make a Sound
                try {
                    val notification: Uri =
                        RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                    val r = RingtoneManager.getRingtone(
                        applicationContext,
                        notification
                    )
                    r.play()
                } catch (e: Exception) {
                    // Some error playing sound
                }
            } catch (e: FormatException) {
                // if the NDEF Message to write is malformed
            } catch (e: TagLostException) {
                // Tag went out of range before operations were complete
            } catch (e: IOException) {
                // if there is an I/O failure, or the operation is cancelled
            } finally {
                // Be nice and try and close the tag to
                // Disable I/O operations to the tag from this TagTechnology object, and release resources.
                try {
                    mNdef.close()
                } catch (e: IOException) {
                    // if there is an I/O failure, or the operation is cancelled
                }
            }
        }
    }*/

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



        mPrinter = PrinterAPI.getInstance()


     //   mNfcAdapter = NfcAdapter.getDefaultAdapter(this);


        initPermission()

      //  setMyFatoorah()


//        int printWidth = (80 - 8) * 8;
       /* val printWidth = (58 - 10) * 8
        var bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.icod_2)
        bitmap = BitmapUtils.reSize(
            bitmap,
            printWidth,
            bitmap.getHeight() * printWidth / bitmap.getWidth()
        )*/

        setContent {
            viewModel = hiltViewModel()
            //telpo card reader
           /* viewModel?.settingReader(this)
            viewModel?.setAssets(assets)*/
            //idnetiv card reader
           viewModel?.initializedCardReader(scard, baseContext)


            if (mUsbBroadCastReceiver == null) {
                val intentFilter = IntentFilter()
                intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
                intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
                mUsbBroadCastReceiver = UsbBroadCastReceiver()
                registerReceiver(mUsbBroadCastReceiver, intentFilter)
                viewModel?.initPrinter(mPrinter, this@MainActivity)

            }


            //viewModel?.funcPrinterImage(bitmap)



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
                            viewModel?.resetInsertCivilIdScreen()
                            viewModel?.resetServicesScreen()
                            SelectDepartmentScreen(navController, viewModel!!)
                        }
                        composable(
                            route = Screen.SelectServiceScreen.route
                        ) {
                            viewModel?.resetInsertCivilIdScreen()

                            var selectDepartment =
                                navController.previousBackStackEntry?.savedStateHandle?.get<SelectDepartment?>(
                                    Constants.STATE_SELECT_DEPARTMENT
                                )
                            viewModel?.readCivilIdOff()
                            SelectServiceScreen(selectDepartment,navController, viewModel!!)
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
                            route = Screen.SelectTestServiceScreen.route
                        ) {
                            viewModel?.readCivilIdOff()
                            SelectTestServiceScreen(navController, viewModel!!)
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

    override fun onResume() {
        super.onResume()
       /* if (mNfcAdapter != null) {
            val options = Bundle()
            // Work around for some broken Nfc firmware implementations that poll the card too fast
            options.putInt(NfcAdapter.EXTRA_READER_PRESENCE_CHECK_DELAY, 250)

            // Enable ReaderMode for all types of card and disable platform sounds
            mNfcAdapter!!.enableReaderMode(
                this,
                this,
                NfcAdapter.FLAG_READER_NFC_A or
                        NfcAdapter.FLAG_READER_NFC_B or
                        NfcAdapter.FLAG_READER_NFC_F or
                        NfcAdapter.FLAG_READER_NFC_V or
                        NfcAdapter.FLAG_READER_NFC_BARCODE or
                        NfcAdapter.FLAG_READER_NO_PLATFORM_SOUNDS,
                options
            )
        }*/
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