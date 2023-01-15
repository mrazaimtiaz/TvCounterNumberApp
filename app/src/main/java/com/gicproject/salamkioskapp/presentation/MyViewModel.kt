package com.gicproject.salamkioskapp.presentation

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gicproject.salamkioskapp.UsbBroadCastReceiver
import com.gicproject.salamkioskapp.common.Resource
import com.gicproject.salamkioskapp.domain.repository.DataStoreRepository
import com.gicproject.salamkioskapp.domain.use_case.MyUseCases
import com.gicproject.salamkioskapp.pacicardlibrary.PaciCardReaderAbstract
import com.gicproject.salamkioskapp.pacicardlibrary.PaciCardReaderMAV3
import com.identive.libs.SCard
import com.identive.libs.WinDefs
import com.szsicod.print.escpos.PrinterAPI
import com.szsicod.print.io.InterfaceAPI
import com.szsicod.print.io.USBAPI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import okhttp3.internal.and
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject


private const val TAG = "MyViewModel"

@HiltViewModel
class MyViewModel @Inject constructor(
    private val surveyUseCases: MyUseCases,
    val repository: DataStoreRepository
) : ViewModel() {

    private val _stateSetting = mutableStateOf(SettingScreenState())
    val stateSetting: State<SettingScreenState> = _stateSetting

    private val _stateSelectDepartment = mutableStateOf(SelectDepartmentScreenState())
    val stateSelectDepartment: State<SelectDepartmentScreenState> = _stateSelectDepartment

    private val _stateSelectDoctor = mutableStateOf(SelectDoctorScreenState())
    val stateSelectDoctor: State<SelectDoctorScreenState> = _stateSelectDoctor


    private val _stateInsertCivilId = mutableStateOf(InsertCivilIdScreenState())
    val stateInsertCivilId: State<InsertCivilIdScreenState> = _stateInsertCivilId


    private val _readCivilId = mutableStateOf(false)


    private val _isRefreshingSetting = MutableStateFlow(false)
    val isRefreshingSetting: StateFlow<Boolean>
        get() = _isRefreshingSetting.asStateFlow()

    var isFirst = true


    fun readCivilIdOn(){
        _readCivilId.value = true
    }

    fun readCivilIdOff(){
        _readCivilId.value = false
    }

    fun initDoctorTimeScreenstate(){
        _stateSelectDoctor.value = SelectDoctorScreenState()
    }

    fun onEvent(event: MyEvent) {
        when (event) {
            is MyEvent.GetDepartment -> {
                surveyUseCases.getDeparments().onEach { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let {
                                isFirst = false
                                viewModelScope.launch {
                                    _stateSelectDepartment.value = SelectDepartmentScreenState(departments = it)

                                }
                            }
                        }
                        is Resource.Error -> {
                            _stateSelectDepartment.value = _stateSelectDepartment.value.copy(
                                error = result.message ?: "An unexpected error occurred",
                                isLoading = false,
                            )
                           // delay(2000)
                          //  onEvent(MyEvent.GetDepartment)
                        }
                        is Resource.Loading -> {
                            if(isFirst){
                                _stateSelectDepartment.value = SelectDepartmentScreenState(isLoading = true)

                            }
                        }
                    }
                }.launchIn(viewModelScope)
            }
            is MyEvent.GetDoctor -> {
                surveyUseCases.getDoctors().onEach { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let {
                                isFirst = false
                                viewModelScope.launch {
                                    _stateSelectDoctor.value = SelectDoctorScreenState(doctors = it)

                                }
                            }
                        }
                        is Resource.Error -> {
                            _stateSelectDoctor.value = _stateSelectDoctor.value.copy(
                                error = result.message ?: "An unexpected error occurred",
                                isLoading = false,
                            )
                            // delay(2000)
                            //  onEvent(MyEvent.GetDepartment)
                        }
                        is Resource.Loading -> {
                                _stateSelectDoctor.value = SelectDoctorScreenState(isLoading = true)
                        }
                    }
                }.launchIn(viewModelScope)
            }
            is MyEvent.GetPrintTicket -> {
                surveyUseCases.getPrintTicket().onEach { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let {
                                viewModelScope.launch {
                                    _stateSelectDoctor.value = _stateSelectDoctor.value.copy()
                                    funcPrinterConnect()

                                }
                            }
                        }
                        is Resource.Error -> {
                            _stateSelectDoctor.value = _stateSelectDoctor.value.copy(
                                error = result.message ?: "An unexpected error occurred",
                                isLoading = false,
                            )
                            // delay(2000)
                            //  onEvent(MyEvent.GetDepartment)
                        }
                        is Resource.Loading -> {
                            _stateSelectDoctor.value = SelectDoctorScreenState(isLoading = true)
                        }
                    }
                }.launchIn(viewModelScope)
            }
        }
    }

    private var mUsbBroadCastReceiver: UsbBroadCastReceiver? = null

    private var mPrinter: PrinterAPI? = null
    private var mContext: Context? = null

     fun initPrinter( printer: PrinterAPI?, context: Context){
        mPrinter = printer
        mContext = context
    }
     fun funcPrinterConnect() {
        CoroutineScope(Dispatchers.IO).launch {
            if (mPrinter?.isConnect == true) {
                mPrinter?.disconnect()
            }



            var io: InterfaceAPI? = null                   // USB
            io = USBAPI(mContext)

            //  io = UsbNativeAPI()



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
                mPrinter?.halfCut()
               val ret = mPrinter?.cutPaper(66, 0)
                mPrinter?.fullCut()
                mPrinter?.cutMark()

                //bitmap print
          /*      mPrinter!!.setPrintColorSize(4)
                mPrinter!!.printString("Picture test printing:\n")
                mPrinter!!.printFeed()
                mPrinter!!.printRasterBitmap(bmp)

//                    byte[] bmpBytes = PrintImageUtils.parseBmpToByte(bmp);
//                    mPrinter.sendOrder(bmpBytes);

//                    byte[] bmpBytes = PrintImageUtils.parseBmpToByte(bmp);
//                    mPrinter.sendOrder(bmpBytes);
                mPrinter!!.printFeed()
                mPrinter!!.printString("test is finished！\n")
                mPrinter!!.printFeed()

                mPrinter!!.cutPaper(66, 0)*/

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


    var initialized = false

    var deviceList = ArrayList<String>()
    var readers: Array<CharSequence>? = null

    lateinit var  mScard: SCard;
    lateinit var  mBaseContext: Context;
   fun  initializedCardReader(scard: SCard, baseContext: Context) {
       mBaseContext = baseContext
       mScard =scard
        var status: Long
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

        onCardEvent(scard, baseContext)
    }

    fun onCardEvent(scard: SCard, baseContext: Context) {
        if(!readers.isNullOrEmpty()){
            scard.setSCardListener(
                baseContext, readers!![0] as String
            ) { event ->

                Log.d("testcardstatus", "listener-event $event")
                Log.d("testcardstatus", "listener-event $event")
                if (0 == event) {
                    Log.d(TAG, "onCardEvent: card removed")
                } else if (1 == event) {
                    if(_readCivilId.value){
                        _stateInsertCivilId.value =  _stateInsertCivilId.value.copy(isLoading = true)
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
                                val transmit: SCard.SCardIOBuffer = scard.SCardIOBuffer()
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

                                _stateInsertCivilId.value =  _stateInsertCivilId.value.copy(isLoading = false)
                                withContext(Dispatchers.Main){
                                    Toast.makeText(baseContext," $civilidText $firstNameArText",
                                        Toast.LENGTH_LONG).show()

                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }
        }else{
            Log.d(TAG, "onCardEvent: Intializing again reader not found, Please try again")
            Toast.makeText(baseContext,"No Card Reader Found",
                Toast.LENGTH_LONG).show()
        }




    }

    fun initializeReaderAgain(){
        initialized = false
        initializedCardReader(mScard, mBaseContext)
    }


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

}