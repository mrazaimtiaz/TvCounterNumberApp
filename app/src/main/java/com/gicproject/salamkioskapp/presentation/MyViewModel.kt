package com.gicproject.salamkioskapp.presentation

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gicproject.salamkioskapp.UsbBroadCastReceiver
import com.gicproject.salamkioskapp.common.Constants
import com.gicproject.salamkioskapp.common.Constants.Companion.NO_BRANCH_SELECTED
import com.gicproject.salamkioskapp.common.Constants.Companion.NO_COUNTER_SELECTED
import com.gicproject.salamkioskapp.common.Constants.Companion.NO_DEPARTMENT_SELECTED
import com.gicproject.salamkioskapp.common.Resource
import com.gicproject.salamkioskapp.domain.model.SelectService
import com.gicproject.salamkioskapp.domain.repository.DataStoreRepository
import com.gicproject.salamkioskapp.domain.use_case.MyUseCases
import com.gicproject.salamkioskapp.pacicardlibrary.PaciCardReaderAbstract
import com.gicproject.salamkioskapp.pacicardlibrary.PaciCardReaderMAV3
import com.github.devnied.emvnfccard.iso7816emv.*
import com.identive.libs.SCard
import com.identive.libs.WinDefs
import com.szsicod.print.escpos.PrinterAPI
import com.szsicod.print.io.InterfaceAPI
import com.szsicod.print.io.USBAPI
import com.szsicod.print.utils.BitmapUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import okhttp3.internal.and
import java.io.IOException
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject


private const val TAG = "MyViewModel"

@HiltViewModel
class MyViewModel @Inject constructor(
    private val surveyUseCases: MyUseCases,
    val repository: DataStoreRepository
) : ViewModel() {
     var selectService = SelectService()
    private val _selectedCounterName = MutableStateFlow(NO_COUNTER_SELECTED)
    val selectedCounterName: StateFlow<String>
        get() = _selectedCounterName.asStateFlow()

    private val _selectedCounterId = MutableStateFlow("")
    val selectedCounterId: StateFlow<String>
        get() = _selectedCounterId.asStateFlow()

    private val _selectedBranchName = MutableStateFlow(NO_BRANCH_SELECTED)
    val selectedBranchName: StateFlow<String>
        get() = _selectedBranchName.asStateFlow()

    private val _selectedBranchId = MutableStateFlow("")
    val selectedBranchId: StateFlow<String>
        get() = _selectedBranchId.asStateFlow()

    private val _selectedDepartmentName = MutableStateFlow(NO_DEPARTMENT_SELECTED)
    val selectedDepartmentName: StateFlow<String>
        get() = _selectedDepartmentName.asStateFlow()

    private val _selectedDepartmentNameAr = MutableStateFlow(NO_DEPARTMENT_SELECTED)
    val selectedDepartmentNameAr: StateFlow<String>
        get() = _selectedDepartmentNameAr.asStateFlow()

    private val _selectedDepartmentId = MutableStateFlow("")
    val selectedDepartmentId: StateFlow<String>
        get() = _selectedDepartmentId.asStateFlow()

    private val _stateSetting = mutableStateOf(SettingScreenState())
    val stateSetting: State<SettingScreenState> = _stateSetting

    private val _stateSelectDepartment = mutableStateOf(SelectDepartmentScreenState())
    val stateSelectDepartment: State<SelectDepartmentScreenState> = _stateSelectDepartment

    private val _stateSelectDoctor = mutableStateOf(SelectDoctorScreenState())
    val stateSelectDoctor: State<SelectDoctorScreenState> = _stateSelectDoctor


    private val _stateInsertCivilId = mutableStateOf(InsertCivilIdScreenState())
    val stateInsertCivilId: State<InsertCivilIdScreenState> = _stateInsertCivilId


    private val _stateSelectService = mutableStateOf(SelectServiceScreenState())
    val stateSelectService: State<SelectServiceScreenState> = _stateSelectService


    private val _readCivilId = mutableStateOf(false)


    private val _isRefreshingSetting = MutableStateFlow(false)
    val isRefreshingSetting: StateFlow<Boolean>
        get() = _isRefreshingSetting.asStateFlow()

    var isFirst = true


    init {
        initPreference()

    }

    fun resetDepartmentScreen(){
        _stateSelectDepartment.value = SelectDepartmentScreenState()
    }
    fun resetServicesScreen(){
        _stateSelectService.value = SelectServiceScreenState()
    }
    fun resetInsertCivilIdScreen(){
        _stateInsertCivilId.value = InsertCivilIdScreenState()
    }
    //preferences
    private fun initPreference() {
        viewModelScope.launch {
            initCounterNamePreference()
            initDepartmentNamePreference()
            initBranchNamePreference()
        }
    }

    private suspend fun initCounterNamePreference() {
        val locationName = repository.getString(Constants.KEY_COUNTER_NAME)
        val locationId = repository.getString(Constants.KEY_COUNTER_ID)

        if (locationId != null) {
            _selectedCounterId.value = locationId
        }
        if (locationName != null) {
            _selectedCounterName.value = locationName
        }
    }

    private suspend fun initBranchNamePreference(setCounter: Boolean = false) {
        val branchName = repository.getString(Constants.KEY_BRANCH_NAME)
        val branchId = repository.getString(Constants.KEY_BRANCH_ID)

        if (branchId != null) {
            _selectedBranchId.value = branchId
        }
        if (branchName != null) {
            _selectedBranchName.value = branchName
        }
        if(setCounter){
          //  onEvent(MyEvent.GetCounters)
           // onEvent(MyEvent.GetDepartments)
        }
    }

    private suspend fun initDepartmentNamePreference() {
        val deptName = repository.getString(Constants.KEY_DEPARTMENT_NAME)
        val deptNameAr = repository.getString(Constants.KEY_DEPARTMENT_NAME_AR)
        val deptId = repository.getString(Constants.KEY_DEPARTMENT_ID)

        if (deptId != null) {
            _selectedDepartmentId.value = deptId
        }
        if (deptName != null) {
            _selectedDepartmentName.value = deptName
        }
        if (deptNameAr != null) {
            _selectedDepartmentNameAr.value = deptNameAr
        }
    }

    fun saveCounter(counterName: String?, counterId: Int?) {
        viewModelScope.launch {
            if (counterId != null) {
                repository.putString(Constants.KEY_COUNTER_NAME, counterName.toString())
                repository.putString(Constants.KEY_COUNTER_ID, counterId.toString())
                initCounterNamePreference()
            }
        }
    }

    fun saveBranch(branchName: String?, branchId: Int?) {
        viewModelScope.launch {
            if (branchId != null) {
                repository.putString(Constants.KEY_BRANCH_NAME, branchName.toString())
                repository.putString(Constants.KEY_BRANCH_ID, branchId.toString())
                initBranchNamePreference(true)


            }
        }
    }

    fun saveDepartment(departmentName: String?,departmentNameAr: String?, deptId: Int?) {
        viewModelScope.launch {
            if (deptId != null) {
                repository.putString(Constants.KEY_DEPARTMENT_NAME_AR, departmentNameAr.toString())
                repository.putString(Constants.KEY_DEPARTMENT_NAME, departmentName.toString())
                repository.putString(Constants.KEY_DEPARTMENT_ID, deptId.toString())
                initDepartmentNamePreference()
            }
        }
    }

    fun stateSettingResetAfterBranch(){
        _stateSetting.value = _stateSetting.value.copy(counters = emptyList(), department = emptyList())
    }


    fun readCivilIdOn(){
        _readCivilId.value = true
    }

    fun readCivilIdOff(){
        _readCivilId.value = false
    }

    fun initDoctorTimeScreenstate(){
        _stateSelectDoctor.value = SelectDoctorScreenState()
    }


    suspend fun convertBase64ToBitmap(b64: String): Bitmap? {
        var bitmap: Bitmap? = null
        try {
            val job = CoroutineScope(Dispatchers.Default).async {
                val imageAsBytes: ByteArray = Base64.decode(b64.toByteArray(), Base64.DEFAULT)
                bitmap = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.size)
            }
            job.await()
        } catch (e: IOException) {
            Log.d("TAG", "onResume: Exception 4 ")
            println(e)
        }
        return bitmap
    }
    var isFirstSelectDepartmentApi = true
    fun onEvent(event: MyEvent) {
        when (event) {
            is MyEvent.GetBranches -> {
                surveyUseCases.getBranches().onEach { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let {
                                _stateSetting.value=     _stateSetting.value.copy(branches = it, error = "")
                            }
                        }
                        is Resource.Error -> {
                            _stateSetting.value = _stateSetting.value.copy(
                                error = result.message ?: "An unexpected error occurred getting branches"
                            )
                        }
                        is Resource.Loading -> {
                                _stateSetting.value = _stateSetting.value.copy(isLoading = true)

                        }
                    }
                }.launchIn(viewModelScope)
            }
            is MyEvent.GetBookTicket -> {
                if(_selectedBranchId.value.isNotEmpty()){
                    surveyUseCases.getBookTicket(
                    event.serviceID,
                        event.isHandicap,
                        event.isVip,
                        event.languageID,
                        event.appointmentCode,
                        event.isaapt,
                        event.refid,
                        event.DoctorServiceID).onEach { result ->
                        when (result) {
                            is Resource.Success -> {
                                if(result.data == null){
                                    _stateSelectService.value =  _stateSelectService.value.copy(isLoading = false,error = "Result Data in getBookTicket api is null")
                                }
                                result.data?.let {
                                        surveyUseCases.getTicket(it.NewPKID ?: 1,1).onEach { result ->
                                            when (result) {
                                                is Resource.Success -> {
                                                    result.data?.let {
                                                        viewModelScope.launch {
                                                         //   funcPrinterImage(Constants.baseImage.toBitmap())
                                                            if(it.Ticket != null){


//        int printWidth = (80 - 8) * 8;
                                                              //  val printWidth = (58 - 10) * 8
                                                               var bitmap  =convertBase64ToBitmap(it.Ticket!!)
                                                                /*bitmap?.let { it1 ->
                                                                        bitmap = BitmapUtils.reSize(
                                                                            it1,
                                                                            printWidth,
                                                                            it1.getHeight() * printWidth / it1.getWidth()
                                                                        )
                                                                }*/
                                                                bitmap?.let {
                                                                    funcPrinterImage(
                                                                        it
                                                                    )
                                                                }
                                                               // var bitmap2  =convertBase64ToBitmap(Constants.baseImage2)

                                                                /*bitmap2?.let { it1 ->
                                                                    bitmap2 = BitmapUtils.reSize(
                                                                        it1,
                                                                        printWidth,
                                                                        it1.getHeight() * printWidth / it1.getWidth()
                                                                    )
                                                                    bitmap2?.let {
                                                                        funcPrinterImage(
                                                                            it
                                                                        )
                                                                    }

                                                                }*/
                                                            }else{
                                                                _stateSelectService.value = _stateSelectService.value.copy(
                                                                    error = result.message ?: "Empty Ticket String",
                                                                    isLoading = false,
                                                                )
                                                            }
                                                           /* convertBase64ToBitmap(Constants.baseImage2)?.let { it1 ->
                                                                funcPrinterImage(
                                                                    it1
                                                                )
                                                            }*/
                                                        }
                                                    }
                                                    if(result.data == null){
                                                        _stateSelectService.value =  _stateSelectService.value.copy(isLoading = false,error = "Result Data in getTicket api is null")
                                                    }
                                                }
                                                is Resource.Error -> {
                                                    _stateSelectService.value = _stateSelectService.value.copy(
                                                        error = result.message ?: "An unexpected error occurred",
                                                        isLoading = false,
                                                    )
                                                    // delay(2000)
                                                    //  onEvent(MyEvent.GetDepartment)
                                                }
                                                is Resource.Loading -> {
                                                    _stateSelectService.value = _stateSelectService.value.copy(isLoading = true)


                                                }
                                            }
                                        }.launchIn(viewModelScope)
                                }
                            }
                            is Resource.Error -> {
                                _stateSelectService.value =  _stateSelectService.value.copy(
                                    error = result.message ?: "An unexpected error occurred",
                                    isLoading = false,
                                )
                                // delay(2000)
                                //  onEvent(MyEvent.GetDepartment)
                            }
                            is Resource.Loading -> {
                                _stateSelectService.value = _stateSelectService.value.copy(isLoading = true)


                            }
                        }
                    }.launchIn(viewModelScope)
                }else{
                    _stateSelectService.value = _stateSelectService.value.copy(
                        error =  "Select Branch First",
                        isLoading = false,
                    )
                }

            }
            is MyEvent.GetSelectServices -> {
                if(_selectedBranchId.value.isNotEmpty()){
                    surveyUseCases.getSelectServices(_selectedBranchId.value,event.deptId).onEach { result ->
                        when (result) {
                            is Resource.Success -> {
                                result.data?.let {
                                    viewModelScope.launch {
                                        _stateSelectService.value = SelectServiceScreenState(services = it)

                                    }
                                }
                            }
                            is Resource.Error -> {
                                _stateSelectService.value = SelectServiceScreenState(
                                    error = result.message ?: "An unexpected error occurred",
                                    isLoading = false,
                                )
                                // delay(2000)
                                //  onEvent(MyEvent.GetDepartment)
                            }
                            is Resource.Loading -> {
                                    _stateSelectService.value = SelectServiceScreenState(isLoading = true)


                            }
                        }
                    }.launchIn(viewModelScope)
                }else{
                    _stateSelectService.value = _stateSelectService.value.copy(
                        error =  "Select Branch First",
                        isLoading = false,
                    )
                }

            }
            is MyEvent.GetSelectDepartments -> {
                if(_selectedBranchId.value.isNotEmpty() && _selectedDepartmentId.value.isNotEmpty()){
                    surveyUseCases.getSelectDepartments(_selectedBranchId.value,_selectedDepartmentId.value).onEach { result ->
                        when (result) {
                            is Resource.Success -> {
                                result.data?.let {
                                    isFirstSelectDepartmentApi = false
                                    viewModelScope.launch {
                                        _stateSelectDepartment.value = SelectDepartmentScreenState(departments = it)

                                    }
                                }
                            }
                            is Resource.Error -> {
                                _stateSelectDepartment.value = SelectDepartmentScreenState(
                                    error = result.message ?: "An unexpected error occurred",
                                    isLoading = false,
                                )
                                // delay(2000)
                                //  onEvent(MyEvent.GetDepartment)
                            }
                            is Resource.Loading -> {
                                if(isFirstSelectDepartmentApi){
                                    _stateSelectDepartment.value = SelectDepartmentScreenState(isLoading = true)

                                }
                            }
                        }
                    }.launchIn(viewModelScope)
                }else{
                    _stateSelectDepartment.value = _stateSelectDepartment.value.copy(
                        error =  "Select Department and Branch",
                        isLoading = false,
                    )
                }

            }
            is MyEvent.GetDepartments -> {
                    surveyUseCases.getDepartments().onEach { result ->
                        when (result) {
                            is Resource.Success -> {
                                result.data?.let {
                                    _stateSetting.value = _stateSetting.value.copy(department = it, error = "", isLoading = false)
                                }
                            }
                            is Resource.Error -> {
                                _stateSetting.value = _stateSetting.value.copy(
                                    error = result.message ?: "An unexpected error occurred"
                                )
                            }
                            is Resource.Loading -> {
                                _stateSetting.value = _stateSetting.value.copy(isLoading = true)
                            }
                        }
                    }.launchIn(viewModelScope)
            }
            is MyEvent.GetCounters -> {
                if(_selectedBranchId.value.isNotEmpty()){
                    surveyUseCases.getCounters(selectedBranchId.value).onEach { result ->
                        when (result) {
                            is Resource.Success -> {
                                result.data?.let {
                                    _stateSetting.value = _stateSetting.value.copy(counters = it, error = "", isLoading = false)
                                }
                            }
                            is Resource.Error -> {
                                _stateSetting.value = _stateSetting.value.copy(
                                    error = result.message ?: "An unexpected error occurred"
                                )
                            }
                            is Resource.Loading -> {
                                _stateSetting.value = _stateSetting.value.copy(isLoading = true)
                            }
                        }
                    }.launchIn(viewModelScope)
                }
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

    fun funcPrinterImage(bitmap: Bitmap) {
        CoroutineScope(Dispatchers.IO).launch {
            Log.d(TAG, "funcPrinterImage: called ")
            if (mPrinter?.isConnect == true) {
                mPrinter?.disconnect()
            }


            var io: InterfaceAPI? = null                   // USB
            io = USBAPI(mContext)

            //  io = UsbNativeAPI()


            val ret = mPrinter?.connect(io)

            try {
                //bitmap print
               // mPrinter!!.setPrintColorSize(4)
             //   mPrinter!!.printString("Picture test printing:\n")
              //  mPrinter!!.printFeed()

                mPrinter!!.setPrintColorSize(4)
             //   mPrinter!!.printString("Picture test printing:\n")
               // mPrinter!!.printFeed()
                mPrinter!!.printRasterBitmap(bitmap)

//                    byte[] bmpBytes = PrintImageUtils.parseBmpToByte(bmp);
//                    mPrinter.sendOrder(bmpBytes);

//                    byte[] bmpBytes = PrintImageUtils.parseBmpToByte(bmp);
//                    mPrinter.sendOrder(bmpBytes);
                mPrinter!!.printFeed()
            //    mPrinter!!.printString("test is finished！\n")
            //    mPrinter!!.printFeed()

                val ret = mPrinter!!.cutPaper(66, 0)
                //                    byte[] bmpBytes = PrintImageUtils.parseBmpToByte(bmp);
                //                    mPrinter.sendOrder(bmpBytes);

                //                    byte[] bmpBytes = PrintImageUtils.parseBmpToByte(bmp);
                //                    mPrinter.sendOrder(bmpBytes);
             //   mPrinter!!.printFeed()
               // mPrinter!!.printFeed()
              //  mPrinter!!.printString("test is finished！\n")
               // mPrinter!!.printFeed()

               // mPrinter?.fullCut()
                _stateSelectService.value =  _stateSelectService.value.copy(isLoading = false,success = "printed")

                // mPrinter!!.cutPaper(66, 0)

            } catch (e: java.lang.Exception) {
                _stateSelectService.value =  _stateSelectService.value.copy(isLoading = false,success = "printing error: ${e.printStackTrace()}")

                e.printStackTrace()
            }
        }
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



    lateinit var  mBaseContext: Context;

    //identiv card reader
    lateinit var  mScard: SCard;

    var initialized = false

    var deviceList = ArrayList<String>()
    var readers: Array<CharSequence>? = null
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



                        val SELECT = byteArrayOf(
                            0x00.toByte(), 0xA4.toByte(), 0x04.toByte(), 0x00.toByte(), 0x0A.toByte(),  // Length
                            0x63, 0x64, 0x63, 0x00, 0x00, 0x00, 0x00, 0x32, 0x32, 0x31 // AID
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
                                 try {
                                     genderText = paci.GetData("1", "SEX-LATIN-TEXT")
                                 } catch (e: java.lang.Exception) {
                                     e.printStackTrace()
                                 }
                                try {
                                    tel1Text = paci.GetData("1", "TEL-1")
                                } catch (e: java.lang.Exception) {
                                    e.printStackTrace()
                                }
                                 try {
                                     nationalityText = paci.GetData("1", "NATIONALITY-LATIN-ALPHA-CODE")

                                 } catch (e: java.lang.Exception) {
                                     e.printStackTrace()
                                 }
                                // ////MOI-REFERENCE  //TEL-2
                                try {
                                    tel2Text = paci.GetData("1", "MOI-REFERENCE-INDIC")

                                } catch (e: java.lang.Exception) {
                                    e.printStackTrace()
                                }
                                try {
                                    emailText = paci.GetData("1", "MOI-REFERENCE")
                                    ////MOI-REFERENCE  //E-MAIL-ADDRESS
                                } catch (e: java.lang.Exception) {
                                    e.printStackTrace()
                                }
                                 try {
                                     dobText = paci.GetData("1", "BIRTH-DATE")
                                 } catch (e: java.lang.Exception) {
                                     e.printStackTrace()
                                 }
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
                                if(civilidText.isBlank()){
                                    _stateInsertCivilId.value =  _stateInsertCivilId.value.copy(isLoading = false,error="Please Insert CivilID Correctly - Cannot Read Data")
                                }else{
                                    Log.d(TAG, "onCardEvent: get result $civilidText $firstNameArText $secondNameArText `6f")

                                    _stateInsertCivilId.value =  _stateInsertCivilId.value.copy(isLoading = false,error="$civilidText $firstNameArText getting text")
                                  /*  withContext(Dispatchers.Main){
                                        Toast.makeText(baseContext," $civilidText $firstNameArText",
                                            Toast.LENGTH_LONG).show()

                                    }*/
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
    fun initializeReaderAgain(){
        initialized = false
        initializedCardReader(mScard, mBaseContext)
    }


    //telpo card reader
/*    lateinit var assetManager: AssetManager
    fun setAssets(mAssetManager: AssetManager) {
        assetManager = mAssetManager
    }
    var reader: SmartCardReader? = null
    fun settingReader(context: Context,) {
        mBaseContext = context
        reader = SmartCardReader(context)
        FingerPrint.fingericPower(1)
        FingerPrint.fingerPrintPower(1)

        viewModelScope.launch(Dispatchers.Main) {
            _stateInsertCivilId.value =  _stateInsertCivilId.value.copy(isLoading = true)
            delay(3000)
            reader?.open(1)
            _stateInsertCivilId.value =  _stateInsertCivilId.value.copy(isLoading = false)
            autoDetectCivilId()
        }
    }
    private val _isAutoDetectCard = mutableStateOf(true)
    val isAutoDetectCard: State<Boolean> = _isAutoDetectCard

    var cardInserted = false
    fun autoDetectCivilId() {

        try{
            if (isAutoDetectCard.value) {
//            d("TAG", "autoDetectCivilId: ${cardInserted} readerPresent: ${reader.iccPowerOn()}")

                if (_readCivilId.value) {
                    reader?.iccPowerOff()

                    if (!cardInserted) {
                        if (reader?.iccPowerOn() == true) {
                            Log.d("TAG", "autoDetectCivilId: cardinsert")
                            if (_readCivilId.value) {
                                cardInserted = true
                                //  emptyMainState()
                                readData()
                            } else {
                                viewModelScope.launch {
                                    delay(500)
                                    autoDetectCivilId()
                                }
                            }
                        } else {
                            viewModelScope.launch {
                                delay(500)
                                autoDetectCivilId()
                            }
                        }
                    } else {
                        if (reader?.iccPowerOn() == false) {
                            Log.d("TAG", "autoDetectCivilId: cardremoved")
                            cardInserted = false
                        }
                        viewModelScope.launch {
                            delay(500)
                            autoDetectCivilId()
                        }
                    }
                } else {
                    viewModelScope.launch {
                        delay(500)
                        autoDetectCivilId()
                    }
                }
            } else {
                viewModelScope.launch {
                    delay(500)
                    autoDetectCivilId()
                }
            }
        }catch (e: java.lang.Exception){
            e.printStackTrace()
            viewModelScope.launch {
                delay(500)
                autoDetectCivilId()
            }
        }



    }

    fun readData() {
        _stateInsertCivilId.value =  _stateInsertCivilId.value.copy(isLoading = true)
        if (reader?.iccPowerOn() == true) {
            val ReaderHandler: ConcurrentHashMap<String?, PaciCardReaderAbstract?> =
                ConcurrentHashMap<String?, PaciCardReaderAbstract?>()
            val paci = PaciCardReaderMAV3Telpo(
                "true" == System.getProperty(
                    "sun.security.smartcardio.t0GetResponse", "true"
                ), reader
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
                    try {
                        genderText = paci.GetData("1", "SEX-LATIN-TEXT")
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                    try {
                        tel1Text = paci.GetData("1", "TEL-1")
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                    try {
                        nationalityText = paci.GetData("1", "NATIONALITY-LATIN-ALPHA-CODE")
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                    try {
                        tel2Text = paci.GetData("1", "TEL-2")
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                    try {
                        emailText = paci.GetData("1", "E-MAIL-ADDRESS")
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                    try {
                        dobText = paci.GetData("1", "BIRTH-DATE")
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
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

                    _stateInsertCivilId.value =  _stateInsertCivilId.value.copy(isLoading = false)
                    withContext(Dispatchers.Main){
                        Toast.makeText(mBaseContext," $civilidText $firstNameArText",
                            Toast.LENGTH_LONG).show()

                    }
                    if (isAutoDetectCard.value) {
                        autoDetectCivilId()
                    }


                } catch (e: Exception) {
                    e.printStackTrace()
                }
                *//*_stateMain.value = _stateMain.value.copy(
                    isLoadingCivilId = false,
                    fingerPrintPage = true,
                    civilIdPage = false,
                    signaturePage = false
                )*//*  //test
            }
        } else {
            _stateInsertCivilId.value =  _stateInsertCivilId.value.copy(isLoading = false)


        }

    }*/


}
/*

                   val provider: IProvider = MyProvider(scard)
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

                            Log.d("com.gicproject.salamkioskapp.TAG", "onCreate card info:1 ${card}")


class MyProvider     // Set the initial value for the class attribute x
    (private var  mScard: SCard) : IProvider {
    fun constructor() {}
//     lateinit var response: ByteArray
    @Throws(com.gicproject.salamkioskapp.emvnfccard.exception.CommunicationException::class)

    override fun transceive(pCommand: ByteArray): ByteArray {

    *//*    response = try {
            // send command to emv card
            //   mTagCom.connect();


        } catch (e: IOException) {
            throw com.gicproject.salamkioskapp.emvnfccard.exception.CommunicationException(e.message)
        }*//*
      //  Log.d(TAG, "transceive: $response")
        return sendApdu(pCommand)
    }

    override fun getAt(): ByteArray {
        // For NFC-A

            val cardState: SCardState = mScard.SCardState()
          var  status = mScard.SCardStatus(cardState)
        var atr = ""
            for (i in 0 until cardState.getnATRlen()) {
                val temp = cardState.abyATR[i] and 0xFF
                if (temp < 16) {
                    atr =
                        atr + "0" + Integer.toHexString(
                            cardState.abyATR[i].toInt()
                        )
                } else {
                    atr += Integer.toHexString(temp)
                }
            }
        val SELECT = byteArrayOf(
            0x00.toByte(), 0xA4.toByte(), 0x04.toByte(), 0x00.toByte(), 0x0A.toByte(),  // Length
            0x63, 0x64, 0x63, 0x00, 0x00, 0x00, 0x00, 0x32, 0x32, 0x31 // AID
        )
        return BytesUtils.fromString("00814D22088660300020E00001")
         //.toByteArray()
        // For NFC-B
        // return mTagCom.getHiLayerResponse();
        

    }




    private fun sendApdu(data: ByteArray): ByteArray {
        val transmit: SCardIOBuffer = mScard.SCardIOBuffer()

        transmit.setnInBufferSize(data.size)
        transmit.abyInBuffer = data
        transmit.setnOutBufferSize(0x8000)
        transmit.abyOutBuffer = ByteArray(0x8000)
        val status1: Long = mScard.SCardTransmit(transmit)
        Log.d(
            "TAG",
            "onClick:resul " + data + "---" + data.size
        )
        var rstr = ""
        var sstr = ""
        for (i in 0 until transmit.getnBytesReturned()) {
            val temp = transmit.abyOutBuffer[i] and 0xFF
            if (temp < 16) {
                rstr = rstr.uppercase(Locale.getDefault()) + "0" + Integer.toHexString(
                    transmit.abyOutBuffer[i].toInt()
                )
                sstr = sstr.uppercase(Locale.getDefault()) + "0" + Integer.toHexString(
                    transmit.abyOutBuffer[i].toInt()
                ) + " "
            } else {
                rstr = rstr.uppercase(Locale.getDefault()) + Integer.toHexString(temp)
                sstr = sstr.uppercase(Locale.getDefault()) + Integer.toHexString(temp) + " "
            }
        }
        Log.d("TAG", "onClick:result $rstr")
        Log.d("TAG", "onClick:result1 $sstr")
        for (i in 0 until transmit.getnBytesReturned()) {
            val temp = transmit.abyOutBuffer[i] and 0xFF
            if (temp < 16) {
                rstr = rstr.uppercase(Locale.getDefault()) + "0" + Integer.toHexString(
                    transmit.abyOutBuffer[i].toInt()
                )
                sstr = sstr.uppercase(Locale.getDefault()) + "0" + Integer.toHexString(
                    transmit.abyOutBuffer[i].toInt()
                ) + " "
            } else {
                rstr = rstr.uppercase(Locale.getDefault()) + Integer.toHexString(temp)
                sstr = sstr.uppercase(Locale.getDefault()) + Integer.toHexString(temp) + " "
            }
        }
        Log.d("TAG", "onClick:result $rstr")
        Log.d("TAG", "onClick:result1 $sstr")
        return PaciCardReaderMAV3.hexToByteArray(rstr)
    }

}*/
