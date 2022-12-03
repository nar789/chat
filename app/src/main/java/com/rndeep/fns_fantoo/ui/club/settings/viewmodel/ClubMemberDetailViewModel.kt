package com.rndeep.fns_fantoo.ui.club.settings.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.dto.ClubMemberBlockInfoResponse
import com.rndeep.fns_fantoo.data.remote.dto.ClubMemberInfoResponse
import com.rndeep.fns_fantoo.data.remote.model.post.ClubCommentBlockResultResponse
import com.rndeep.fns_fantoo.data.remote.model.post.ClubPostBlockResultResponse
import com.rndeep.fns_fantoo.ui.club.settings.repository.ClubSettingRepository
import com.rndeep.fns_fantoo.ui.common.viewmodel.SingleLiveEvent
import com.rndeep.fns_fantoo.utils.ConstVariable
import com.rndeep.fns_fantoo.utils.ConstVariable.KEY_UID
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClubMemberDetailViewModel @Inject constructor(private val clubSettingRepository: ClubSettingRepository) :
    ViewModel() {

    val loadingStatusLiveData = SingleLiveEvent<Boolean>()

    private var _networkErrorLiveData = SingleLiveEvent<Boolean>()
    val networkErrorLiveData: SingleLiveEvent<Boolean>
        get() = _networkErrorLiveData

    var _memberInfoLiveData = MutableLiveData<ClubMemberInfoResponse>()
    val memberInfoLiveData: LiveData<ClubMemberInfoResponse>
        get() = _memberInfoLiveData

    fun getMemberInfo(clubId: String, uid: String, memberId: String) = viewModelScope.launch {
        loadingStatusLiveData.value = true
        val response =
            clubSettingRepository.getClubMemberInfo(clubId = clubId, memberId = memberId, uid = uid)
        loadingStatusLiveData.value = false
        when (response) {
            is ResultWrapper.Success -> {
                _memberInfoLiveData.value = ClubMemberInfoResponse(
                    ConstVariable.RESULT_SUCCESS_CODE,
                    "",
                    response.data,
                    null
                )
            }
            is ResultWrapper.GenericError -> {
                _memberInfoLiveData.value = ClubMemberInfoResponse(
                    response.code!!,
                    response.message!!,
                    null,
                    response.errorData
                )
            }
            is ResultWrapper.NetworkError -> {
                _networkErrorLiveData.value = true
            }
        }
    }

    private var _memberBlockInfoLiveData = MutableLiveData<Boolean>()
    val memberBlockInfoLiveData : LiveData<Boolean>
        get() = _memberBlockInfoLiveData

    var _memberBlockInfoResultLiveData = MutableLiveData<ClubMemberBlockInfoResponse>()
    val memberBlockInfoResultLiveData : LiveData<ClubMemberBlockInfoResponse>
        get() = _memberBlockInfoResultLiveData

    fun getMemberBlockInfo(clubId: String, uid: String, memberId: String) = viewModelScope.launch {
        val response = clubSettingRepository.getMemberBlockInfo(clubId = clubId, uid = uid, memberId = memberId)
        when(response){
            is ResultWrapper.Success ->{
                _memberBlockInfoResultLiveData.value = ClubMemberBlockInfoResponse(ConstVariable.RESULT_SUCCESS_CODE, "", response.data, null, null)
                _memberBlockInfoLiveData.value = response.data.blockYn
            }
            is ResultWrapper.GenericError ->{
                _memberBlockInfoResultLiveData.value = ClubMemberBlockInfoResponse(response.code!!, response.message!!, null, null, response.errorData)
            }
            is ResultWrapper.NetworkError ->{
                _networkErrorLiveData.value = true
            }
        }
    }

    var _memberBlockSetResultLiveData = MutableLiveData<ClubMemberBlockInfoResponse>()
    val memberBlockSetResultLiveData : LiveData<ClubMemberBlockInfoResponse>
        get() = _memberBlockSetResultLiveData

    fun setMemberBlock(clubId: String, memberId: String, uid: String, navDirections: NavDirections?) = viewModelScope.launch {
        val response = clubSettingRepository.setMemberBlock(clubId, memberId, hashMapOf(KEY_UID to uid))
        when(response){
            is ResultWrapper.Success ->{
                _memberBlockSetResultLiveData.value = ClubMemberBlockInfoResponse(ConstVariable.RESULT_SUCCESS_CODE, "", response.data, navDirections, null)
                _memberBlockInfoLiveData.value = response.data.blockYn
            }
            is ResultWrapper.GenericError ->{
                _memberBlockSetResultLiveData.value = ClubMemberBlockInfoResponse(response.code!!, response.message!!, null, null, response.errorData)
            }
            is ResultWrapper.NetworkError ->{
                _networkErrorLiveData.value = true
            }
        }
    }

    private var _postBlockSetLiveData = MutableLiveData<ClubPostBlockResultResponse>()
    val postBlockSetLiveData : LiveData<ClubPostBlockResultResponse>
        get() = _postBlockSetLiveData

    fun setPostBlock(clubId: String, categroyCode:String, postId:String, requireHashMap: HashMap<String,Any>, navDirections: NavDirections?) = viewModelScope.launch {
        val response = clubSettingRepository.setPostBlock(clubId = clubId, categoryCode = categroyCode, postId = postId,
            requestData = requireHashMap)
        when(response){
            is ResultWrapper.Success ->{
                _postBlockSetLiveData.value = ClubPostBlockResultResponse(ConstVariable.RESULT_SUCCESS_CODE, "", response.data, navDirections, null)
            }
            is ResultWrapper.GenericError ->{
                _postBlockSetLiveData.value = ClubPostBlockResultResponse(response.code!!, response.message!!, null, null, response.errorData!!)
            }
            is ResultWrapper.NetworkError ->{
                _networkErrorLiveData.value = true
            }
        }
    }

    private var _commentBlockSetLiveData = MutableLiveData<ClubCommentBlockResultResponse>()
    val commentBlockSetLiveData : LiveData<ClubCommentBlockResultResponse>
        get() = _commentBlockSetLiveData

    fun setCommentBlock(clubId: String, categroyCode:String, postId:String, replyId:String, requireHashMap: HashMap<String,Any>, navDirections: NavDirections?) = viewModelScope.launch {
        val response = clubSettingRepository.setCommentBlock(clubId = clubId, categoryCode = categroyCode, postId = postId,
        replyId = replyId, requestData = requireHashMap)
        when(response){
            is ResultWrapper.Success ->{
                _commentBlockSetLiveData.value = ClubCommentBlockResultResponse(ConstVariable.RESULT_SUCCESS_CODE, "", response.data, navDirections, null)
            }
            is ResultWrapper.GenericError ->{
                _commentBlockSetLiveData.value = ClubCommentBlockResultResponse(response.code!!, response.message!!, null, null, response.errorData!!)
                }
            is ResultWrapper.NetworkError ->{
                _networkErrorLiveData.value = true
            }
        }
    }

    fun setSubCommentBlock(clubId: String, categroyCode:String, postId:String, replyId:String, parentReplyId:String, requireHashMap: HashMap<String,Any>, navDirections: NavDirections?) = viewModelScope.launch {
        val response = clubSettingRepository.setSubCommentBlock(clubId = clubId, categoryCode = categroyCode, postId = postId,
            replyId = replyId, parentReplyId = parentReplyId, requestData = requireHashMap)
        when(response){
            is ResultWrapper.Success ->{
                _commentBlockSetLiveData.value = ClubCommentBlockResultResponse(ConstVariable.RESULT_SUCCESS_CODE, "", response.data, navDirections, null)
            }
            is ResultWrapper.GenericError ->{
                _commentBlockSetLiveData.value = ClubCommentBlockResultResponse(response.code!!, response.message!!, null, null, response.errorData!!)
            }
            is ResultWrapper.NetworkError ->{
                _networkErrorLiveData.value = true
            }
        }
    }

}