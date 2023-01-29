package com.rndeep.fns_fantoo.ui.home.alram

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rndeep.fns_fantoo.data.remote.model.HomeAlarmData
import com.rndeep.fns_fantoo.repositories.DataStoreKey
import com.rndeep.fns_fantoo.repositories.DataStoreRepository
import com.rndeep.fns_fantoo.ui.common.viewmodel.SingleLiveEvent
import com.rndeep.fns_fantoo.utils.ConstVariable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeAlarmViewModel @Inject constructor(
    private val homeAlarmRepository: HomeAlarmRepository,
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    private var isUser : Boolean =false
    private var uId :String? = null
    private var accessToken : String? = null

    init {
        viewModelScope.launch {
            dataStoreRepository.getBoolean(DataStoreKey.PREF_KEY_IS_LOGINED)?.let {
                isUser=it
                uId=dataStoreRepository.getString(DataStoreKey.PREF_KEY_UID)
                accessToken=dataStoreRepository.getString(DataStoreKey.PREF_KEY_ACCESS_TOKEN)
            }
        }

    }

    private val _alarmErrorData = SingleLiveEvent<String>()
    val alarmErrorData :LiveData<String> get() = _alarmErrorData

    private val _alarmListLiveData = MutableLiveData<List<HomeAlarmData>>()
    val alarmListLiveData: LiveData<List<HomeAlarmData>> get() = _alarmListLiveData
    private var alarmNextId : String? =null
    fun getAlarmList() {
        viewModelScope.launch {
            homeAlarmRepository.getAlarmData(uId!!,alarmNextId,"10").run {
                if(this.first!=null){
                    _alarmListLiveData.value = this.first!!
                }else{
                    _alarmErrorData.value = this.third?.code?:ConstVariable.ERROR_WAIT_FOR_SECOND
                }
                alarmNextId= this.second
            }
        }
    }

    private val _allReadResultLiveData = SingleLiveEvent<Boolean>()
    val allReadResultLiveData: LiveData<Boolean> get() = _allReadResultLiveData
    fun callAllReadAlarm() {
        _allReadResultLiveData.value = true
    }


    fun callAlarmTranslate() {

    }

}