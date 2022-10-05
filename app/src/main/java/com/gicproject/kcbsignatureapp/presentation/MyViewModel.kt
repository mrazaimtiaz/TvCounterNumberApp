package com.gicproject.kcbsignatureapp.presentation

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.Log.d
import android.widget.Filter
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gicproject.kcbsignatureapp.OkhttpManager
import com.gicproject.kcbsignatureapp.common.Constants
import com.gicproject.kcbsignatureapp.common.Resource
import com.gicproject.kcbsignatureapp.domain.model.EmployeeData
import com.gicproject.kcbsignatureapp.domain.model.GetPersonSendModel
import com.gicproject.kcbsignatureapp.domain.repository.DataStoreRepository
import com.gicproject.kcbsignatureapp.domain.use_case.MyUseCases
import com.gicproject.kcbsignatureapp.pacicardlibrary.PaciCardReaderAbstract
import com.gicproject.kcbsignatureapp.pacicardlibrary.PaciCardReaderMAV3
import com.suprema.BioMiniFactory
import com.suprema.CaptureResponder
import com.suprema.IBioMiniDevice
import com.suprema.IBioMiniDevice.*
import com.suprema.IUsbEventHandler
import com.suprema.util.Logger
import com.telpo.tps550.api.fingerprint.FingerPrint
import com.telpo.tps550.api.reader.SmartCardReader
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.charset.Charset
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject


@HiltViewModel
class MyViewModel @Inject constructor(
    private val surveyUseCases: MyUseCases,
    val repository: DataStoreRepository
) : ViewModel() {

    private val _isRefreshingSetting = MutableStateFlow(false)
    val isRefreshingSetting: StateFlow<Boolean>
        get() = _isRefreshingSetting.asStateFlow()

    private var _stateMain = mutableStateOf(MainScreenState())
    val stateMain: MutableState<MainScreenState> = _stateMain

    private val _isCameraInitialized = mutableStateOf(false)
    val isCameraInitialized: State<Boolean> = _isCameraInitialized

    private val _shouldShowCamera = mutableStateOf(false)
    val shouldShowCamera: State<Boolean> = _shouldShowCamera

    private val _shouldShowPhoto = mutableStateOf(false)
    val shouldShowPhoto: State<Boolean> = _shouldShowPhoto

    private val _photoUri = mutableStateOf(null as Uri?)
    val photoUri: State<Uri?> = _photoUri

    private var _statusFingerPrint = mutableStateOf("")
    val statusFingerPrint: MutableState<String> = _statusFingerPrint

    private val _fingerPrintUri = mutableStateOf(null as Bitmap?)
    val fingerPrintUri: State<Bitmap?> = _fingerPrintUri

    private val _signatureSvg = mutableStateOf(null as String?)
    val signatureSvg: State<String?> = _signatureSvg

    private val _isAutoDetectCard = mutableStateOf(true)
    val isAutoDetectCard: State<Boolean> = _isAutoDetectCard

    private val _svgLoading = mutableStateOf(false)
    val svgLoading: State<Boolean> = _svgLoading


    lateinit var reader: SmartCardReader


    val myFilter: Filter = object : Filter() {
        //Automatic on background thread
        override fun performFiltering(charSequence: CharSequence): FilterResults {
            Log.d(
                "TAG",
                "performFiltering: $charSequence"
            )
            val filteredList: MutableList<EmployeeData?> = ArrayList<EmployeeData?>()
            if (charSequence.isEmpty()) {
                filteredList.addAll(stateMain.value.employeeList)
            } else {
                for (i in stateMain.value.employeeList.indices) {
                    if (stateMain.value.employeeList[i].FULLNAME?.toLowerCase(Locale.ROOT)
                            ?.contains(charSequence.toString().toLowerCase(Locale.ROOT)) == true
                        || stateMain.value.employeeList[i].EMPLOYEENUMBER?.toLowerCase(Locale.ROOT)
                            ?.contains(charSequence.toString().toLowerCase(Locale.ROOT)) == true
                        || stateMain.value.employeeList[i].NATIONALIDENTIFIER?.toLowerCase(Locale.ROOT)
                            ?.contains(charSequence.toString().toLowerCase(Locale.ROOT)) == true
                        || stateMain.value.employeeList[i].JOBNAME?.toLowerCase(Locale.ROOT)
                            ?.contains(charSequence.toString().toLowerCase(Locale.ROOT)) == true
                    ) {
                        filteredList.add(stateMain.value.employeeList[i])
                    }
                }
            }
            val filterResults = FilterResults()
            filterResults.values = filteredList
            return filterResults
        }

        //Automatic on UI thread
        override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
            _stateMain.value =
                stateMain.value.copy(employeeSearchList = filterResults.values as List<EmployeeData>)

        }
    }

    init {
        initAutoDetectPreference()
    }

    private fun initAutoDetectPreference() {
        viewModelScope.launch {
            val value = repository.getBoolean(Constants.KEY_AUTO_DETECT_CARD)
            if (value != null) {
                _isAutoDetectCard.value = value
            }
        }
    }

    fun setAutoDetect(autoDetectValue: Boolean) {
        viewModelScope.launch {
            _isAutoDetectCard.value = autoDetectValue
            repository.putBoolean(Constants.KEY_AUTO_DETECT_CARD, autoDetectValue)
        }
    }

    fun setCameraInitialized(value: Boolean) {
        _isCameraInitialized.value = value
    }

    fun setPhotoUri(value: Uri?) {
        _photoUri.value = value
    }

    fun setFingerPrintUri(value: Bitmap?) {
        _fingerPrintUri.value = value
    }

    fun setSignatureSvg(value: String?) {
        _signatureSvg.value = value
    }


    fun setShowCamera(value: Boolean) {
        _shouldShowCamera.value = value
    }

    fun setShowPhoto(value: Boolean) {
        _shouldShowPhoto.value = value
    }

    fun emptyMainState() {
        _stateMain.value = MainScreenState()
        _photoUri.value = null
        _fingerPrintUri.value = null
        _shouldShowCamera.value = true
        _shouldShowPhoto.value = false
        //     _statusFingerPrint.value = ""
    }


    fun onEvent(event: MyEvent) {
        when (event) {
            is MyEvent.GetEmployeeSignatureData -> {
                d("TAG", "onEvent: error event getemployeedata signature")
                surveyUseCases.getEmployeeSignature(
                    GetPersonSendModel(
                        p_proc_name = "APPS.XXKCB_HR_MOBILE1_SS_PKG.GET_EMPLOYEE_SIGNATURE",
                        P_NATIONAL_ID = event.id
                    )
                ).onEach { result ->
                    when (result) {
                        is Resource.Success -> {
                            d("TAG", "onEvent: error event getemployeedata")

                            result.data?.let {
                                if (it.isNotEmpty()) {
                                        _stateMain.value = _stateMain.value.copy(
                                            employeeSignatures = it,
                                            showToast = "",
                                        )

                                } else {
                                    _stateMain.value = _stateMain.value.copy(
                                        employeeSignatures = emptyList(),
                                        showToast = "Empty List",
                                        signaturePage = false
                                    )
                                }


                            }
                        }
                        is Resource.Error -> {
                            d("TAG", "onEvent: error event getemployeedata")
                            _stateMain.value = _stateMain.value.copy(
                                employeeSignatures = emptyList(),
                                signaturePage = false
                            )
                        }
                        is Resource.Loading -> {
                            _stateMain.value =
                                _stateMain.value.copy(isLoadingEmployeeInfo = true, showToast = "")
                            //  _stateSetting.value = SettingScreenState(isLoading = true)
                        }
                    }
                }.launchIn(viewModelScope)
            }
            is MyEvent.GetEmployeeData -> {
                d("TAG", "onEvent: error event getemployeedata1")
                surveyUseCases.getEmployeeData(
                    GetPersonSendModel(
                        p_proc_name = "APPS.XXKCB_HR_MOBILE1_SS_PKG.LIST_EMP_WITH_SIGNATURE",
                        P_NATIONAL_ID = event.id
                    )
                ).onEach { result ->
                    when (result) {
                        is Resource.Success -> {
                            d("TAG", "onEvent: error event getemployeedata")

                            result.data?.let {
                                if (it.isNotEmpty()) {
                                    if (it[0].NATIONALIDENTIFIER.toString() == event.id) {
                                        _stateMain.value = _stateMain.value.copy(
                                            isLoadingCivilId = false,
                                            fingerPrintPage = true,
                                            civilIdPage = false,
                                            employeeListPage = false,
                                            showToast = "",
                                            signaturePage = false
                                        )
                                    } else {
                                        _stateMain.value = _stateMain.value.copy(
                                            isLoadingCivilId = false,
                                            fingerPrintPage = false,
                                            employeeListPage = false,
                                            civilIdPage = true,
                                            showToast = "CivilId not matched ${event.id} / ${it[0].NATIONALIDENTIFIER} \n البطاقة المدنية غير متطابقة ${event.id} / ${it[0].NATIONALIDENTIFIER}",
                                            signaturePage = false
                                        )
                                    }
                                } else {
                                    _stateMain.value = _stateMain.value.copy(
                                        isLoadingCivilId = false,
                                        fingerPrintPage = false,
                                        employeeListPage = false,
                                        civilIdPage = true,
                                        showToast = "Empty List",
                                        signaturePage = false
                                    )
                                }


                            }
                        }
                        is Resource.Error -> {
                            d("TAG", "onEvent: error event getemployeedata")
                            _stateMain.value = _stateMain.value.copy(
                                isLoadingCivilId = false,
                                fingerPrintPage = false,
                                employeeListPage = false,
                                civilIdPage = true,
                                showToast = "Server Error",
                                signaturePage = false
                            )
                        }
                        is Resource.Loading -> {
                            _stateMain.value =
                                _stateMain.value.copy(isLoadingCivilId = true, showToast = "")
                            //  _stateSetting.value = SettingScreenState(isLoading = true)
                        }
                    }
                }.launchIn(viewModelScope)
            }
            is MyEvent.GetEmployeeListData -> {
                d("TAG", "onEvent: error event getemployeedata1")
                surveyUseCases.getEmployeeData(
                    GetPersonSendModel(
                        p_proc_name = "APPS.XXKCB_HR_MOBILE1_SS_PKG.LIST_EMP_WITH_SIGNATURE",
                        P_NATIONAL_ID = "0"
                    )
                ).onEach { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let {

                                d("TAG", "onEvent: error event getemployeedata ${it.size}")
                                _stateMain.value = _stateMain.value.copy(
                                    isLoadingEmployeeList = false,
                                    employeeList = it,
                                    employeeSearchList = it
                                )
                            }

                        }
                        is Resource.Error -> {
                            d("TAG", "onEvent: error event getemployeedata")
                            _stateMain.value = _stateMain.value.copy(
                                showToast = result.message.toString(),
                            )


                        }
                        is Resource.Loading -> {
                            _stateMain.value =
                                _stateMain.value.copy(isLoadingEmployeeList = true, showToast = "")
                            //  _stateSetting.value = SettingScreenState(isLoading = true)
                        }
                    }
                }.launchIn(viewModelScope)
            }
            is MyEvent.AddEmployeeData -> {
                //  progressbarSubmitButton.setVisibility(View.VISIBLE)
                _svgLoading.value = true
                d("TAG", "onEvent: 1 addemployeedata")

                val th = Thread {
                    var client: OkHttpClient
                    try {

                        OkhttpManager.getInstance().trustrCertificates = assetManager.open("")
                        client = OkhttpManager.getInstance().build()
                    } catch (e: IOException) {
                        _svgLoading.value = false
                        client = OkHttpClient()
                    }
                    var MEDIA_TYPE_PNG: MediaType
                    val buildernew: MultipartBody.Builder =
                        MultipartBody.Builder().setType(MultipartBody.FORM)

                    MEDIA_TYPE_PNG = "image/jpeg".toMediaType()
                    if (_photoUri.value != null) {
                        val imageBody: RequestBody =
                            _photoUri.value!!.toFile().asRequestBody(MEDIA_TYPE_PNG)
                        buildernew.addFormDataPart(
                            "cameraimage",
                            "cameraimage",
                            imageBody
                        )
                    }
                    if (_fingerPrintUri.value != null) {
                        val fileFingerPrint =
                            bitmapToFile(_fingerPrintUri.value!!, "fingerprintimage")
                        if (fileFingerPrint != null) {
                            val imageBody: RequestBody =
                                fileFingerPrint.asRequestBody(MEDIA_TYPE_PNG)
                            buildernew.addFormDataPart(
                                "fingerprintimage",
                                "fingerprintimage",
                                imageBody
                            )
                        }
                    }

                    if (_signatureSvg.value != null) {
                        buildernew.addFormDataPart(
                            "signature.svg",
                            "signature.svg",
                            RequestBody.create(
                                "image/svg+xml".toMediaType(),
                                _signatureSvg.value!!.toByteArray(Charset.defaultCharset())
                            )

                        )
                    }


                    /*if(_fingerPrintUri.value != null){
                        val imageBody: RequestBody =
                            _fingerPrintUri.value!!.toFile().asRequestBody(MEDIA_TYPE_PNG)
                        buildernew.addFormDataPart(
                            "cameraimage",
                            "cameraimage",
                            imageBody
                        )
                    }*/

                    d("TAG", "onEvent: 2 addemployeedata")


                    val requestBody: MultipartBody = buildernew.build()
                    d(
                        "TAG",
                        "onEvent: add employee ${Constants.BASE_URL}mid/api/Paci/ADD_EMPLOYEE_SIGNATURE?cid=" + _stateMain.value.civilidText
                    )
                    val request: Request = Request.Builder()
                        .url("${Constants.BASE_URL}mid/api/Paci/ADD_EMPLOYEE_SIGNATURE?cid=" + _stateMain.value.civilidText)
                        .post(requestBody)
                        .build()
                    try {
                        var response: Response? = null
                        response = client.newCall(request).execute()
                        val s = response.body!!.string()
                        _svgLoading.value = false
                        try {
                            val jsonObj = JSONObject(s)
                            val a = jsonObj.getString("x_status")
                            val message = jsonObj.getString("x_message")
                            //x_message
                            d("TAG", "onEvent: status $a")
                            d("TAG", "onEvent: message $message")
                            if (a.contains("S")) {
                                backToCivilIdPage("S")
                            } else {
                                _stateMain.value =
                                    _stateMain.value.copy(showToast = "الحالة ليست صحيحة ، لا يمكن الحفظ \n Status not True, Cannot Saved")

                            }
                        } catch (e: JSONException) {
                            _stateMain.value = _stateMain.value.copy(showToast = "Could not parse")
                            e.printStackTrace()
                        }

                    } catch (e: IOException) {
                        _svgLoading.value = false
                        _stateMain.value = _stateMain.value.copy(showToast = "Could not connect")
                        //Toast.makeText(LoaActivity2.this,e.getMessage(), Toast.LENGTH_LONG).show();
                        e.printStackTrace()
                        Log.d("TAG", "my_submit2: " + e.message)
                        /*   Handler(Looper.getMainLooper()).post {
                               Toast.makeText(
                                   this,
                                   e.message,
                                   Toast.LENGTH_LONG
                               ).show()
                           }*/
                    }
                }
                th.start()
            }
            is MyEvent.CivilIdChanged -> {
                _stateMain.value = _stateMain.value.copy(civilidText = event.text)
            }
            is MyEvent.FullNameChanged -> {
                _stateMain.value = _stateMain.value.copy(fullNameText = event.text)
            }
            is MyEvent.FirstNameChanged -> {
                _stateMain.value = _stateMain.value.copy(firstNameText = event.text)
            }
            is MyEvent.SecondNameChanged -> {
                _stateMain.value = _stateMain.value.copy(secondNameText = event.text)
            }
            is MyEvent.ThirdNameChanged -> {
                _stateMain.value = _stateMain.value.copy(thirdNameText = event.text)
            }
            is MyEvent.FullNameArChanged -> {
                _stateMain.value = _stateMain.value.copy(fullNameArText = event.text)
            }
            is MyEvent.SecondNameArChanged -> {
                _stateMain.value = _stateMain.value.copy(secondNameArText = event.text)
            }
            is MyEvent.FirstNameArChanged -> {
                _stateMain.value = _stateMain.value.copy(firstNameArText = event.text)
            }
            is MyEvent.ThirdNameArChanged -> {
                _stateMain.value = _stateMain.value.copy(thirdNameArText = event.text)
            }
            is MyEvent.GenderChanged -> {
                _stateMain.value = _stateMain.value.copy(genderText = event.text)
            }
            is MyEvent.FullAddressChanged -> {
                _stateMain.value = _stateMain.value.copy(fullAddressText = event.text)
            }
            is MyEvent.BloodGroupChanged -> {
                _stateMain.value = _stateMain.value.copy(bloodGroupText = event.text)
            }
            is MyEvent.PassportNoChanged -> {
                _stateMain.value = _stateMain.value.copy(passportNoText = event.text)
            }
            is MyEvent.OccupationChanged -> {
                _stateMain.value = _stateMain.value.copy(occupationText = event.text)
            }
            is MyEvent.SerialNoChanged -> {
                _stateMain.value = _stateMain.value.copy(serialNoText = event.text)
            }
            is MyEvent.NationalityChanged -> {
                _stateMain.value = _stateMain.value.copy(nationalityText = event.text)
            }
            is MyEvent.DOBChanged -> {
                _stateMain.value = _stateMain.value.copy(dobText = event.text)
            }
            is MyEvent.EmailChanged -> {
                _stateMain.value = _stateMain.value.copy(emailText = event.text)
            }
            is MyEvent.Tel2Changed -> {
                _stateMain.value = _stateMain.value.copy(tel2Text = event.text)
            }
            is MyEvent.Tel1Changed -> {
                _stateMain.value = _stateMain.value.copy(tel1Text = event.text)
            }
            is MyEvent.ExpiryDateChanged -> {
                _stateMain.value = _stateMain.value.copy(expiryText = event.text)
            }
        }
    }

    fun settingReader(context: Context) {
        reader = SmartCardReader(context)
        FingerPrint.fingericPower(1)
        FingerPrint.fingerPrintPower(1)

        viewModelScope.launch(Dispatchers.Main) {
            _stateMain.value = _stateMain.value.copy(isLoadingCivilId = true, showToast = "")
            delay(3000)
            reader.open(1)
            _stateMain.value = _stateMain.value.copy(isLoadingCivilId = false, showToast = "")
            autoDetectCivilId()
        }
    }

    fun bitmapToFile(bitmap: Bitmap, fileNameToSave: String): File? { // File name like "image.png"
        //create a file to write bitmap data
        var file: File? = null
        return try {
            file = File(
                Environment.getExternalStorageDirectory()
                    .toString() + File.separator + fileNameToSave
            )
            file.createNewFile()

            //Convert bitmap to byte array
            val bos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos) // YOU can also save it in JPEG
            val bitmapdata = bos.toByteArray()

            //write the bytes in file
            val fos = FileOutputStream(file)
            fos.write(bitmapdata)
            fos.flush()
            fos.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            file // it will return null
        }
    }

    fun backToCivilIdPage(toastString: String = "") {
        _stateMain.value = _stateMain.value.copy(
            isLoadingCivilId = false,
            fingerPrintPage = false,
            employeeInfoPage = false,
            employeeListPage = false,
            civilIdPage = true,
            showToast = toastString,
            signaturePage = false
        )
    }
    fun backToEmployeeListPage() {
        _stateMain.value = _stateMain.value.copy(
            isLoadingCivilId = false,
            fingerPrintPage = false,
            employeeInfoPage = false,
            employeeListPage = true,
            civilIdPage = false,
            signaturePage = false
        )
    }

    lateinit var assetManager: AssetManager
    fun setAssets(mAssetManager: AssetManager) {
        assetManager = mAssetManager
    }

    fun backToFingerPrintPage() {
        _stateMain.value = _stateMain.value.copy(
            isLoadingCivilId = false,
            fingerPrintPage = true,
            employeeInfoPage = false,
            employeeListPage = false,
            civilIdPage = false,
            showToast = "",
            signaturePage = false
        )
    }

    fun openSignaturePage() {
        _stateMain.value = _stateMain.value.copy(
            isLoadingCivilId = false,
            fingerPrintPage = false,
            employeeListPage = false,
            employeeInfoPage = false,
            civilIdPage = false,
            signaturePage = true
        )
    }

    fun openEmployeeListPage() {
        _stateMain.value = _stateMain.value.copy(
            isLoadingCivilId = false,
            fingerPrintPage = false,
            employeeInfoPage = false,
            civilIdPage = false,
            signaturePage = false,
            employeeListPage = true,
        )
    }

    fun openEmployeeInfoPage(employeeData: EmployeeData) {
        _stateMain.value = _stateMain.value.copy(
            isLoadingCivilId = false,
            fingerPrintPage = false,
            civilIdPage = false,
            signaturePage = false,
            employeeListPage = false,
            employeeInfoShow = employeeData,
            employeeInfoPage = true
        )
    }


    var cardInserted = false
    fun autoDetectCivilId() {

        if (isAutoDetectCard.value) {
//            d("TAG", "autoDetectCivilId: ${cardInserted} readerPresent: ${reader.iccPowerOn()}")

            if (stateMain.value.civilIdPage) {
                reader.iccPowerOff()

                if (!cardInserted) {
                    if (reader.iccPowerOn()) {
                        d("TAG", "autoDetectCivilId: cardinsert")
                        if (stateMain.value.civilIdPage) {
                            cardInserted = true
                            emptyMainState()
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
                    if (!reader.iccPowerOn()) {
                        d("TAG", "autoDetectCivilId: cardremoved")
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


    }


    fun readData(civilIdMatch: String = "") {
        _stateMain.value = _stateMain.value.copy(isLoadingCivilId = true, showToast = "")
        if (reader.iccPowerOn()) {
            val ReaderHandler: ConcurrentHashMap<String?, PaciCardReaderAbstract?> =
                ConcurrentHashMap<String?, PaciCardReaderAbstract?>()
            val paci = PaciCardReaderMAV3(
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
                        if(civilIdMatch.isNotBlank()){
                            if(civilidText.equals(civilIdMatch)){
                                _stateMain.value = _stateMain.value.copy(
                                    isLoadingCivilId = false,
                                    fingerPrintPage = true,
                                    civilIdPage = false,
                                    employeeInfoPage = false,
                                    employeeListPage = false,
                                    showToast = "",
                                    signaturePage = false
                                )
                            }else{
                              _stateMain.value = _stateMain.value.copy(showToast = "CivilId not Matched ${civilIdMatch} / $civilidText \n الهوية المدنية غير متطابقة ${civilIdMatch} / $civilidText")
                            }
                        }else{
                            onEvent(MyEvent.GetEmployeeData(civilidText)) //test
                        }
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

                    onEvent(MyEvent.CivilIdChanged(civilidText))
                    onEvent(MyEvent.SerialNoChanged(serialNoText))
                    onEvent(MyEvent.FirstNameChanged(firstNameText))
                    onEvent(MyEvent.SecondNameChanged(secondNameText))
                    onEvent(MyEvent.ThirdNameChanged(thirdNameText))
                    onEvent(MyEvent.FullNameChanged(fullNameText))
                    onEvent(MyEvent.FirstNameArChanged(firstNameArText))
                    onEvent(MyEvent.SecondNameArChanged(secondNameArText))
                    onEvent(MyEvent.ThirdNameArChanged(thirdNameArText))
                    onEvent(MyEvent.FullNameArChanged(fullNameArText))
                    onEvent(MyEvent.FullAddressChanged(fullAddressText))
                    onEvent(MyEvent.GenderChanged(genderText))
                    onEvent(MyEvent.BloodGroupChanged(bloodGroupText))
                    onEvent(MyEvent.PassportNoChanged(passportNoText))
                    onEvent(MyEvent.Tel1Changed(tel1Text))
                    onEvent(MyEvent.NationalityChanged(nationalityText))
                    onEvent(MyEvent.Tel2Changed(tel2Text))
                    onEvent(MyEvent.EmailChanged(emailText))
                    onEvent(MyEvent.DOBChanged(dobText))
                    onEvent(MyEvent.ExpiryDateChanged(expiryText))

                    if (isAutoDetectCard.value) {
                        autoDetectCivilId()
                    }


                } catch (e: Exception) {
                    e.printStackTrace()
                }
               /* _stateMain.value = _stateMain.value.copy(
                    isLoadingCivilId = false,
                    fingerPrintPage = true,
                    civilIdPage = false,
                    signaturePage = false
                )*/  //test
            }
        } else {
            _stateMain.value = _stateMain.value.copy(isLoadingCivilId = false, showToast = "No CivilID Found \n لم يتم العثور على بطاقة هوية مدنية ")
        }

    }

    //finger print section
    private val _orderAsc = mutableStateOf(true)
    val orderAsc: State<Boolean> = _orderAsc
    private val _sortingName = mutableStateOf("")
    val sortingName: State<String> = _sortingName
    fun sortingList(columnNumber: String) {
        var list = _stateMain.value.employeeSearchList
        if (columnNumber == "1") {
            _sortingName.value ="1"
            if (orderAsc.value) {
                list = list.sortedBy {
                    it.EMPLOYEENUMBER
                }
                _orderAsc.value = false
            } else {
                list = list.sortedByDescending { it.EMPLOYEENUMBER }
                _orderAsc.value = true
            }
        } else if (columnNumber == "2") {
            _sortingName.value ="2"
            if (orderAsc.value) {
                list = list.sortedBy { it.FULLNAME }
                _orderAsc.value = false
            } else {
                list = list.sortedByDescending { it.FULLNAME }
                _orderAsc.value = true
            }
        } else if (columnNumber == "3") {
            _sortingName.value ="3"
            if (orderAsc.value) {
                list = list.sortedBy { it.ORGANIZATIONNAME }
                _orderAsc.value = false
            } else {
                list = list.sortedByDescending { it.ORGANIZATIONNAME }
                _orderAsc.value = true
            }
        } else if (columnNumber == "4") {
            _sortingName.value ="4"
            if (orderAsc.value) {
                list = list.sortedBy { it.SIGNATUREEXISTS }
                _orderAsc.value = false
            } else {
                list = list.sortedByDescending { it.SIGNATUREEXISTS }
                _orderAsc.value = true
            }
        }
        _stateMain.value = _stateMain.value.copy(employeeSearchList = list)
    }

    fun emptyToast() {
        _stateMain.value = _stateMain.value.copy(showToast = "")
    }

    fun doAutoCapture() {
        Logger.d("buttonCaptureAuto clicked")
        mTemplateData = null
        mCaptureOption.extractParam.captureTemplate = true
        mCaptureOption.captureFuntion = IBioMiniDevice.CaptureFuntion.CAPTURE_AUTO
        _fingerPrintUri.value = null
        mCaptureOption.frameRate = IBioMiniDevice.FrameRate.LOW
        statusFingerPrint.value = "Capturing"
        if (mCurrentDevice != null) {
            val result: Int? = mCurrentDevice?.captureAuto(mCaptureOption, mCaptureCallBack)
            if (result == ErrorCode.ERR_NOT_SUPPORTED.value()) {
                statusFingerPrint.value = "This device is not support auto Capture!"
            }
        }
    }

    private fun doAbortCapture() {
        Thread(Runnable {
            if (mCurrentDevice != null) {
                if (!mCurrentDevice!!.isCapturing) {
                    //   statusFingerPrint.value = "Capture Function is already aborted."
                    mCaptureOption.captureFuntion = CaptureFuntion.NONE
                    return@Runnable
                }
                val result = mCurrentDevice!!.abortCapturing()
                Logger.d("run: abortCapturing : $result")
                if (result == 0) {
                    if (mCaptureOption.captureFuntion != CaptureFuntion.NONE)

                    //   statusFingerPrint.value =  mCaptureOption.captureFuntion.name + " is aborted."

                        mCaptureOption.captureFuntion = CaptureFuntion.NONE
                } else {
                    if (result == ErrorCode.ERR_CAPTURE_ABORTING.value()) {
                        //      statusFingerPrint.value =  "abortCapture is still running."

                    } else
                        statusFingerPrint.value = "abort capture fail!"
                }
            }
        }).start()
    }

    private val mDetect_core = 0
    private val mTemplateQualityEx = 0
    private val mCaptureStartTime: Long = 0
    var mCaptureCallBack: CaptureResponder = object : CaptureResponder() {
        override fun onCapture(context: Any, fingerState: FingerState) {
            super.onCapture(context, fingerState)
        }

        override fun onCaptureEx(
            context: Any,
            option: CaptureOption,
            capturedImage: Bitmap,
            capturedTemplate: TemplateData,
            fingerState: FingerState
        ): Boolean {
            Logger.d("START! : " + mCaptureOption.captureFuntion.toString())
            if (capturedTemplate != null) {
                Logger.d("TemplateData is not null!")
                mTemplateData = capturedTemplate

            }

//            if((option.captureFuntion != IBioMiniDevice.CaptureFuntion.START_CAPTURING && option.captureFuntion != IBioMiniDevice.CaptureFuntion.NONE))
            if (capturedTemplate != null) {
                Logger.d("check additional capture result.")
                if (mCurrentDevice != null && mCurrentDevice!!.lfdLevel > 0) {
                    statusFingerPrint.value = "LFD SCORE : " + mCurrentDevice!!.lfdScoreFromCapture
                }
                if (mDetect_core == 1) {
                    val _coord = mCurrentDevice!!.coreCoordinate
                    statusFingerPrint.value =
                        "Core Coordinate X : " + _coord[0] + " Y : " + _coord[1]
                }
                if (mTemplateQualityEx == 1) {
                    val _templateQualityExValue = mCurrentDevice!!.templateQualityExValue
                    statusFingerPrint.value = "template Quality : $_templateQualityExValue"
                }
            }

            //fpquality example
            if (mCurrentDevice != null) {
                val imageData = mCurrentDevice!!.captureImageAsRAW_8
                if (imageData != null) {
                    val mode = FpQualityMode.NQS_MODE_DEFAULT
                    val _fpquality = mCurrentDevice!!.getFPQuality(
                        imageData,
                        mCurrentDevice!!.imageWidth,
                        mCurrentDevice!!.imageHeight,
                        mode.value()
                    )
                    Logger.d("_fpquality : $_fpquality")
                }
            }
            if (option.captureFuntion == CaptureFuntion.CAPTURE_AUTO) {
                // Get a handler that can be used to post to the main thread
                val mainHandler = Handler(Looper.getMainLooper())
                val myRunnable = Runnable {
                    statusFingerPrint.value = "Captured OK"
                    Logger.i("capture time = " + (System.currentTimeMillis() - mCaptureStartTime))
                    _fingerPrintUri.value = capturedImage
                    doAbortCapture()

                } // This is your code
                mainHandler.post(myRunnable)
            }
            statusFingerPrint.value = "Captured OK"
            return true
        }

        override fun onCaptureError(context: Any, errorCode: Int, error: String) {
            if (errorCode == ErrorCode.CTRL_ERR_IS_CAPTURING.value()) {
                statusFingerPrint.value = "Capturing"
            } else if (errorCode == ErrorCode.CTRL_ERR_CAPTURE_ABORTED.value()) {
                Logger.d("CTRL_ERR_CAPTURE_ABORTED occured.")
            } else if (errorCode == ErrorCode.CTRL_ERR_FAKE_FINGER.value()) {
                statusFingerPrint.value = "Fake Finger Detected"
                if (mCurrentDevice != null && mCurrentDevice!!.lfdLevel > 0) {
                    statusFingerPrint.value = "LFD SCORE : " + mCurrentDevice!!.lfdScoreFromCapture
                }
            } else {
                statusFingerPrint.value =
                    mCaptureOption.captureFuntion.name + " is fail by " + error
                statusFingerPrint.value = "Please try again."
            }
        }
    }


    private val ACTION_USB_PERMISSION: String? = "com.android.example.USB_PERMISSION"
    fun initUsbListener(mContext: Context, usbManager: UsbManager) {
        mUsbManager = usbManager
        val pi = PendingIntent.getBroadcast(
            mContext,
            0,
            Intent(ACTION_USB_PERMISSION),
            0
        )
        mContext.registerReceiver(
            mUsbReceiver,
            IntentFilter(ACTION_USB_PERMISSION)
        )
        val attachfilter = IntentFilter(UsbManager.ACTION_USB_DEVICE_ATTACHED)
        mContext.registerReceiver(mUsbReceiver, attachfilter)
        val detachfilter = IntentFilter(UsbManager.ACTION_USB_DEVICE_DETACHED)
        mContext.registerReceiver(mUsbReceiver, detachfilter)
        addDeviceToUsbDeviceList(mContext)
    }

    var mUsbDevice: UsbDevice? = null
    private val mUsbReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            when (action) {
                ACTION_USB_PERMISSION -> {
                    d("TAG", "onReceive: ACTION_USB_PERMISSION")
                    val hasUsbPermission =
                        intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)
                    if (hasUsbPermission && mUsbDevice != null) {
                        d(
                            "TAG",
                            mUsbDevice?.getDeviceName() + " is acquire the usb permission. activate this device."
                        )
                        if (mUsbDevice != null) d(
                            "TAG",
                            "ACTIVATE_USB_DEVICE : " + mUsbDevice?.getDeviceName()
                        )
                        createBioMiniDevice(mContext = context)
                    } else {
                        d("TAG", "USB permission is not granted!")
                    }
                }
                UsbManager.ACTION_USB_DEVICE_ATTACHED -> {
                    d("TAG", "ACTION_USB_DEVICE_ATTACHED")
                    addDeviceToUsbDeviceList(context)
                }
                UsbManager.ACTION_USB_DEVICE_DETACHED -> {
                    d("TAG", "ACTION_USB_DEVICE_DETACHED")
                    //  statusFingerPrint.value =getResources().getString(R.string.usb_detached))
                    removeDevice()
                }
                else -> {}
            }
        }
    }

    private fun removeDevice() {
        d("TAG", "ACTION_USB_DEVICE_DETACHED")
        if (mBioMiniFactory != null) {
            mBioMiniFactory!!.removeDevice(mUsbDevice)
            mBioMiniFactory!!.close()
        }
        mUsbDevice = null
        mCurrentDevice = null
        // if (mImageView != null) mImageView.setImageBitmap(null)
    }

    private var mUsbManager: UsbManager? = null
    private var mPermissionIntent: PendingIntent? = null
    fun addDeviceToUsbDeviceList(mContext: Context) {
        d("TAG", "start!")
        if (mUsbManager == null) {
            d("TAG", "mUsbManager is null")
            return
        }
        if (mUsbDevice != null) {
            d("TAG", "usbdevice is not null!")
            return
        }
        val deviceList: HashMap<String, UsbDevice> = mUsbManager!!.getDeviceList()
        val deviceIter: Iterator<UsbDevice> = deviceList.values.iterator()
        while (deviceIter.hasNext()) {
            val _device = deviceIter.next()
            if (_device.vendorId == 0x16d1) {
                d("TAG", "found suprema usb device")
                mUsbDevice = _device
                if (mUsbManager?.hasPermission(mUsbDevice) == false) {
                    d("TAG", "This device need to Usb Permission!")
                    mPermissionIntent = PendingIntent.getBroadcast(
                        mContext,
                        0,
                        Intent(ACTION_USB_PERMISSION),
                        0
                    )
                    mUsbManager?.requestPermission(mUsbDevice, mPermissionIntent)
                } else {
                    d("TAG", "This device alread have USB permission! please activate this device.")
                    //                    _rsApi.deviceAttached(mUsbDevice);
                    if (mUsbDevice != null) d(
                        "TAG",
                        "ACTIVATE_USB_DEVICE : " + mUsbDevice!!.deviceName
                    )
                    createBioMiniDevice(mContext = mContext)
                }
            } else {
                d("TAG", "This device is not suprema device!  : " + _device.vendorId)
            }
        }
    }

    private var mBioMiniFactory: BioMiniFactory? = null
    private var mTemplateData: TemplateData? = null
    private val mCaptureOption = CaptureOption()
    var mCurrentDevice: IBioMiniDevice? = null
    private fun createBioMiniDevice(mContext: Context) {
        d("TAG", "START!")
        if (mUsbDevice == null) {
            statusFingerPrint.value = "Device Not Connected"
            return
        }
        if (mBioMiniFactory != null) {
            mBioMiniFactory?.close()
        }
        d("TAG", "new BioMiniFactory( )")
        mBioMiniFactory = object : BioMiniFactory(mContext, mUsbManager) {
            //for android sample
            override fun onDeviceChange(event: IUsbEventHandler.DeviceChangeEvent, dev: Any?) {
                d("TAG", "onDeviceChange : $event")
                d("TAG", "START!")
            }
        }
        d("TAG", "new BioMiniFactory( ) : $mBioMiniFactory")
        //     boolean _transferMode = mSettingFragment.mDataStorage.getUseNativeUsbModeParam();
        //Logger.d("_transferMode : " + _transferMode);
        //  setTransferMode(true,false);
        val _result: Boolean? = mBioMiniFactory?.addDevice(mUsbDevice)
        if (_result == true) {
            mCurrentDevice = mBioMiniFactory?.getDevice(0)
            if (mCurrentDevice != null) {
                statusFingerPrint.value = "device attached"

                d("TAG", "mCurrentDevice attached : $mCurrentDevice")
                //    mViewPager.setCurrentItem(0);
                viewModelScope.launch(Dispatchers.Main) {
                    if (mCurrentDevice != null /*&& mCurrentDevice.getDeviceInfo() != null*/) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            //    requestWritePermission()
                        }
                        _fingerPrintUri.value = null
                    }
                }
            } else {
                d("TAG", "mCurrentDevice is null")
            }
        } else {
            d("TAG", "addDevice is fail!")
        }
        mBioMiniFactory?.setTransferMode(IBioMiniDevice.TransferMode.MODE2);
    }


}

/*
//	text += var3!!.GetData("", "A-TITLE") + "\n"
text += paci!!.GetData("", "ARABIC-NAME-1") + "\n"
text +=  paci.GetData("", "ARABIC-NAME-2") + "\n"
//text +=  paci.GetData("", "ARABIC-NAME-3") + "\n"
//	text +=  paci.GetData("", "ARABIC-NAME-4") + "\n"
//	text +=  paci.GetData("1", "ArabicName")
text +=  paci.GetData("1", "LATIN-NAME-1") + "\n"
text +=  paci.GetData("1", "LATIN-NAME-2") + "\n"
//	text +=  paci.GetData("1", "LATIN-NAME-3") + "\n"
//text +=  paci.GetData("1", "LATIN-NAME-4") + "\n"
//	text +=  paci.GetData("1", "EnglishName")
//	text +=  paci.GetData("1", "NATIONALITY-LATIN-ALPHA-CODE") + "\n"
//	text +=  paci.GetData("1", "NATIONALITY-ARABIC-TEXT") + "\n"
text +=  paci.GetData("1", "BIRTH-DATE") + "\n"
//	text +=  paci.GetData("1", "CARD-ISSUE-DATE") + "\n"
//	text +=  paci.GetData("1", "CARD-EXPIRY-DATE") + "\n"
//	text +=  paci.GetData("1", "CARD-SERIAL-NO") + "\n"
//text +=  paci.GetData("1", "DOCUMENT-NO") + "\n"
//	text +=  paci.GetData("1", "MOI-REFERENCE") + "\n"
//	text +=  paci.GetData("1", "MOI-REFERENCE-INDIC") + "\n"
//	text +=  paci.GetData("1", "ADDITIONAL-F-1") + "\n"
//	text +=  paci.GetData("1", "ADDITIONAL-F-2") + "\n"
//	text +=  paci.GetData("1", "SEX-LATIN-TEXT") + "\n"
//text +=  paci.GetData("1", "SEX-ARABIC-TEXT") + "\n"
text +=  paci.GetData("1", "BLOCK-NO") + "\n"
text +=  paci.GetData("1", "STREET-NAME") + "\n"
text +=  paci.GetData("1", "DESTRICT") + "\n"
//text +=  paci.GetData("1", "UNIT-TYPE") + "\n"
//	text +=  paci.GetData("1", "UNIT-NO") + "\n"
//	text +=  paci.GetData("1", "BUILDING-PLOT-NO") + "\n"
//	text +=  paci.GetData("1", "FLOOR-NO") + "\n"
//	text +=  paci.GetData("1", "ADDRESS-UNIQUE-KEY") + "\n"
text +=  paci.GetData("1", "BLOOD-TYPE") + "\n"
//	text +=  paci.GetData("1", "GUARDIAN-CIVIL-ID-NO") + "\n"
text +=  paci.GetData("1", "TEL-1") + "\n"
//	text +=  paci.GetData("1", "TEL-2") + "\n"
//	text +=  paci.GetData("1", "E-MAIL-ADDRESS") + "\n"*/

//Log.d("TAG", "sendAPDUkOnClick: binary" +  var3.GetData("1", "PHOTO"));
//  serialNoText =  paci.GetData("1", "CARD-SERIAL-NO") //not actual serial number

// passportNoText =  paci.GetData("1", "LATIN-NAME-4")
// fullAddressText +=  paci.GetData("1", "BUILDING-PLOT-NO") + " "
// fullAddressText +=  paci.GetData("1", "FLOOR-NO")+ " "
// fullAddressText +=  paci.GetData("1", "BLOCK-NO")+ " "
// fullAddressText +=  paci.GetData("1", "STREET-NAME")+ " "
// fullAddressText +=  paci.GetData("1", "DESTRICT")+ " "
// fullAddressText +=  paci.GetData("1", "UNIT-TYPE")+ " "
// fullAddressText +=  paci.GetData("1", "UNIT-NO")+ " "
//  fullAddressText +=  paci.GetData("1", "ADDRESS-UNIQUE-KEY")+ " "
// bloodGroupText =  paci.GetData("1", "BLOOD-TYPE")
