package com.gicproject.salamkioskapp.presentation

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.widget.Toast
import android.speech.tts.TextToSpeech
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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


    private val _isRefreshingSetting = MutableStateFlow(false)
    val isRefreshingSetting: StateFlow<Boolean>
        get() = _isRefreshingSetting.asStateFlow()

    var isFirst = true


    init {
        initPreference()

    }

    fun resetDepartmentScreen() {
        _stateSelectDepartment.value = SelectDepartmentScreenState()
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

    private  var  textToSpeech:TextToSpeech? = null
    fun textToSpeech(context: Context, text: String){

        textToSpeech = TextToSpeech(
            context
        ) {
            if (it == TextToSpeech.SUCCESS) {
                textToSpeech?.let { txtToSpeech ->
                    txtToSpeech.language = Locale.US
                    txtToSpeech.setSpeechRate(0.5f)

                    txtToSpeech.speak(
                        text,
                        TextToSpeech.QUEUE_ADD,
                        null,
                        null
                    )
                }
            }

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
        if (setCounter) {
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

    fun saveDepartment(departmentName: String?, departmentNameAr: String?, deptId: Int?) {
        viewModelScope.launch {
            if (deptId != null) {
                repository.putString(Constants.KEY_DEPARTMENT_NAME_AR, departmentNameAr.toString())
                repository.putString(Constants.KEY_DEPARTMENT_NAME, departmentName.toString())
                repository.putString(Constants.KEY_DEPARTMENT_ID, deptId.toString())
                initDepartmentNamePreference()
            }
        }
    }

    fun stateSettingResetAfterBranch() {
        _stateSetting.value =
            _stateSetting.value.copy(counters = emptyList(), department = emptyList())
    }


    var isFirstSelectDepartmentApi = true
    fun onEvent(event: MyEvent) {
        when (event) {
            is MyEvent.GetBranches -> {
                surveyUseCases.getBranches().onEach { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let {
                                _stateSetting.value =
                                    _stateSetting.value.copy(branches = it, error = "")
                            }
                        }
                        is Resource.Error -> {
                            _stateSetting.value = _stateSetting.value.copy(
                                error = result.message
                                    ?: "An unexpected error occurred getting branches"
                            )
                        }
                        is Resource.Loading -> {
                            _stateSetting.value = _stateSetting.value.copy(isLoading = true)

                        }
                    }
                }.launchIn(viewModelScope)
            }


            is MyEvent.GetSelectServices -> {
                if (_selectedBranchId.value.isNotEmpty()) {
                    surveyUseCases.getSelectServices(_selectedBranchId.value, event.deptId)
                        .onEach { result ->
                            surveyUseCases.getSelectServices(
                                "1","2"
                             //   _selectedBranchId.value,
                               // _selectedDepartmentId.value
                            ).onEach { result ->
                                when (result) {
                                    is Resource.Success -> {
                                        result.data?.let {
                                            isFirstSelectDepartmentApi = false
                                            viewModelScope.launch {
                                                _stateSelectDepartment.value =
                                                    SelectDepartmentScreenState(departments = it)

                                            }
                                        }
                                    }
                                    is Resource.Error -> {
                                        _stateSelectDepartment.value = SelectDepartmentScreenState(
                                            error = result.message
                                                ?: "An unexpected error occurred",
                                            isLoading = false,
                                        )
                                        // delay(2000)
                                        //  onEvent(MyEvent.GetDepartment)
                                    }
                                    is Resource.Loading -> {
                                        if (isFirstSelectDepartmentApi) {
                                            _stateSelectDepartment.value =
                                                SelectDepartmentScreenState(isLoading = true)

                                        }
                                    }
                                }
                            }.launchIn(viewModelScope)
                        }.launchIn(viewModelScope)
                } else {
                    _stateSelectDepartment.value = _stateSelectDepartment.value.copy(
                        error = "Select Department and Branch",
                        isLoading = false,
                    )
                }

            }

            is MyEvent.GetDepartments -> {
                surveyUseCases.getDepartments().onEach { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let {
                                _stateSetting.value = _stateSetting.value.copy(
                                    department = it,
                                    error = "",
                                    isLoading = false
                                )
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
                if (_selectedBranchId.value.isNotEmpty()) {
                    surveyUseCases.getCounters(selectedBranchId.value).onEach { result ->
                        when (result) {
                            is Resource.Success -> {
                                result.data?.let {
                                    _stateSetting.value = _stateSetting.value.copy(
                                        counters = it,
                                        error = "",
                                        isLoading = false
                                    )
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
        }
    }
}







