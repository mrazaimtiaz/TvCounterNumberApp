package com.gicproject.kcbsignatureapp.presentation

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log.d
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gicproject.kcbsignatureapp.common.Constants
import com.gicproject.kcbsignatureapp.domain.repository.DataStoreRepository
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

@HiltViewModel
class MyViewModel @Inject constructor(
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

    private val _isAutoDetectCard = mutableStateOf(false)
    val isAutoDetectCard: State<Boolean> = _isAutoDetectCard


    lateinit var reader: SmartCardReader

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
            _stateMain.value = _stateMain.value.copy(isLoadingCivilId = true)
            delay(3000)
            reader.open(1)
            _stateMain.value = _stateMain.value.copy(isLoadingCivilId = false)
            autoDetectCivilId()
        }
    }

    fun backToCivilIdPage() {
        _stateMain.value = _stateMain.value.copy(
            isLoadingCivilId = false,
            fingerPrintPage = false,
            civilIdPage = true,
            signaturePage = false
        )
    }

    fun backToFingerPrintPage() {
        _stateMain.value = _stateMain.value.copy(
            isLoadingCivilId = false,
            fingerPrintPage = true,
            civilIdPage = false,
            signaturePage = false
        )
    }

    fun openSignaturePage() {
        _stateMain.value = _stateMain.value.copy(
            isLoadingCivilId = false,
            fingerPrintPage = false,
            civilIdPage = false,
            signaturePage = true
        )
    }


    var cardInserted = false
    fun autoDetectCivilId() {
        if (isAutoDetectCard.value) {
            d("TAG", "autoDetectCivilId: ${cardInserted} readerPresent: ${reader.iccPowerOn()}")
            if (!cardInserted) {
                if (reader.iccPowerOn()) {
                    d("TAG", "autoDetectCivilId: cardinsert")
                    if (stateMain.value.civilIdPage) {
                        cardInserted = true
                        readData()
                    }
                }
            } else {
                if (!reader.iccPowerOn()) {
                    d("TAG", "autoDetectCivilId: cardremoved")
                    cardInserted = false
                }
            }
        }
        viewModelScope.launch {
            delay(3000)
            autoDetectCivilId()
        }


    }


    fun readData() {
        _stateMain.value = _stateMain.value.copy(isLoadingCivilId = true)
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


                } catch (e: Exception) {
                    e.printStackTrace()
                }
                _stateMain.value = _stateMain.value.copy(
                    isLoadingCivilId = false,
                    fingerPrintPage = true,
                    civilIdPage = false,
                    signaturePage = false
                )
            }
        } else {


            _stateMain.value = _stateMain.value.copy(isLoadingCivilId = false)
        }

    }

    //finger print section

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
