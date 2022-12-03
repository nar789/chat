package com.rndeep.fns_fantoo.ui.club.settings.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.dto.ClubMemberBlockInfoResponse
import com.rndeep.fns_fantoo.ui.club.settings.repository.ClubSettingRepository
import com.rndeep.fns_fantoo.utils.ConstVariable
import kotlinx.coroutines.launch
import javax.inject.Inject

class ClubMyPostBoxViewModel @Inject constructor(
    private val clubSettingRepository: ClubSettingRepository
): ViewModel() {

    var _memberBlockSetResultLiveData = MutableLiveData<ClubMemberBlockInfoResponse>()
    val memberBlockSetResultLiveData : LiveData<ClubMemberBlockInfoResponse>
        get() = _memberBlockSetResultLiveData

    fun setMemberBlock(clubId: String, memberId: String, uid: String) = viewModelScope.launch {
        val response = clubSettingRepository.setMemberBlock(clubId, memberId, hashMapOf(
            ConstVariable.KEY_UID to uid))
        when(response){
            is ResultWrapper.Success ->{
                _memberBlockSetResultLiveData.value = ClubMemberBlockInfoResponse(ConstVariable.RESULT_SUCCESS_CODE, "", response.data, null, null)
            }
            is ResultWrapper.GenericError ->{
                _memberBlockSetResultLiveData.value = ClubMemberBlockInfoResponse(response.code!!, response.message!!, null, null, response.errorData)
            }
            is ResultWrapper.NetworkError ->{

            }
        }
    }
}