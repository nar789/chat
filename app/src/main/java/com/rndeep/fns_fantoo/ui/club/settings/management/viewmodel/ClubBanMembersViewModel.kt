package com.rndeep.fns_fantoo.ui.club.settings.management.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.dto.*
import com.rndeep.fns_fantoo.data.remote.model.ClubMemberInfo
import com.rndeep.fns_fantoo.data.remote.model.clubsetting.ClubMemberCountDto
import com.rndeep.fns_fantoo.ui.club.settings.repository.ClubSettingRepository
import com.rndeep.fns_fantoo.ui.common.viewmodel.SingleLiveEvent
import com.rndeep.fns_fantoo.utils.ConstVariable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClubBanMembersViewModel @Inject constructor(private val clubSettingRepository: ClubSettingRepository) : ViewModel(){

    private var _networkErrorLiveData = SingleLiveEvent<Boolean>()
    val networkErrorLiveData: SingleLiveEvent<Boolean>
        get() = _networkErrorLiveData

    fun getWithDrawMemberList(clubId:String, uid: String) : Flow<PagingData<ClubWithDrawMemberInfoWithMeta>> {
        return clubSettingRepository.getWithDrawMemberList(clubId, uid).cachedIn(viewModelScope)
    }

    private var _withDrawMemberCountLiveData = MutableLiveData<ClubMemberCountDto?>()
    val withDrawMemberCountLiveData : LiveData<ClubMemberCountDto?>
        get() = _withDrawMemberCountLiveData

    fun getWithDrawMemberCount(clubId: String, uid: String) = viewModelScope.launch {
        val response = clubSettingRepository.getWithDrawMemberCount(clubId, uid)
        when(response){
            is ResultWrapper.Success ->{
                _withDrawMemberCountLiveData.value = response.data
            }
            is ResultWrapper.GenericError ->{

            }
            is ResultWrapper.NetworkError ->{
                _networkErrorLiveData.value = true
            }
        }
    }

    private var _withDrawMemberJoinAllowResultLiveData = MutableLiveData<ClubMemberWithdrawResponse?>()
    val withDrawMemberJoinAllowResultLiveData : LiveData<ClubMemberWithdrawResponse?>
        get() = _withDrawMemberJoinAllowResultLiveData
    fun updateJoinAllowWithDrawMember(clubId: String, memberId: String, requestHashMap: HashMap<String, Any>) = viewModelScope.launch {
        val response = clubSettingRepository.updateWithDrawMember(clubId, memberId, requestHashMap)
        when(response){
            is ResultWrapper.Success ->{
                _withDrawMemberJoinAllowResultLiveData.value = ClubMemberWithdrawResponse(ConstVariable.RESULT_SUCCESS_CODE, "", response.data, null)
            }
            is ResultWrapper.GenericError ->{
                _withDrawMemberJoinAllowResultLiveData.value = ClubMemberWithdrawResponse(response.code!!, response.message!!, null, response.errorData)
            }
            is ResultWrapper.NetworkError ->{
                _networkErrorLiveData.value = true
            }
        }
    }
}