package com.gicproject.salamkioskapp.presentation

import android.content.Context
import android.content.IntentFilter
import android.hardware.usb.UsbManager
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gicproject.salamkioskapp.UsbBroadCastReceiver
import com.gicproject.salamkioskapp.common.Resource
import com.gicproject.salamkioskapp.domain.repository.DataStoreRepository
import com.gicproject.salamkioskapp.domain.use_case.MyUseCases
import com.szsicod.print.escpos.PrinterAPI
import com.szsicod.print.io.InterfaceAPI
import com.szsicod.print.io.USBAPI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MyViewModel @Inject constructor(
    private val surveyUseCases: MyUseCases,
    val repository: DataStoreRepository
) : ViewModel() {

    private val _stateSetting = mutableStateOf(SettingScreenState())
    val stateSetting: State<SettingScreenState> = _stateSetting

    private val _stateSelectDepartment = mutableStateOf(SelectDepartmentScreenState())
    val stateSelectDepartment: State<SelectDepartmentScreenState> = _stateSelectDepartment


    private val _isRefreshingSetting = MutableStateFlow(false)
    val isRefreshingSetting: StateFlow<Boolean>
        get() = _isRefreshingSetting.asStateFlow()

    var isFirst = true

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

}