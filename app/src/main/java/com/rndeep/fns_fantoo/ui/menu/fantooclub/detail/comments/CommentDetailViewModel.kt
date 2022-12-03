package com.rndeep.fns_fantoo.ui.menu.fantooclub.detail.comments

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.dto.FantooClubLikeResponse
import com.rndeep.fns_fantoo.data.remote.model.IntegUid
import com.rndeep.fns_fantoo.data.remote.model.ReportMessageItem
import com.rndeep.fns_fantoo.data.remote.model.fantooclub.*
import com.rndeep.fns_fantoo.repositories.DataStoreKey
import com.rndeep.fns_fantoo.repositories.DataStoreRepository
import com.rndeep.fns_fantoo.repositories.FantooClubRepository
import com.rndeep.fns_fantoo.repositories.UploadRepository
import com.rndeep.fns_fantoo.ui.menu.fantooclub.detail.ComposeCommentMode
import com.rndeep.fns_fantoo.ui.menu.fantooclub.detail.ContentsDetailViewModel
import com.rndeep.fns_fantoo.ui.menu.fantooclub.detail.FantooClubComposeComment
import com.rndeep.fns_fantoo.ui.menu.fantooclub.more.MoreMenuItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CommentDetailViewModel @Inject constructor(
    private val application: Application,
    private val uploadRepository: UploadRepository,
    private val dataStoreRepository: DataStoreRepository,
    private val fantooClubRepository: FantooClubRepository
) : ViewModel() {

    private val _comments = MutableLiveData<List<FantooClubComment>>()
    val comments: LiveData<List<FantooClubComment>> = _comments

    private val _composeSuccess = MutableLiveData<Boolean>()
    val composeSuccess: LiveData<Boolean> = _composeSuccess

    private val _uploadImageSuccess = MutableLiveData<List<FantooClubPostAttach>?>()
    val uploadImageSuccess: LiveData<List<FantooClubPostAttach>?> = _uploadImageSuccess

    private val _sendBtnEnabled = MutableLiveData<Boolean>()
    val sendBtnEnabled: LiveData<Boolean> = _sendBtnEnabled

    private val _isScroll = MutableLiveData<Boolean>()
    val isScroll: LiveData<Boolean> = _isScroll

    private val _errorMsg = MutableLiveData<String?>()
    val errorMsg: LiveData<String?> = _errorMsg

    private val _commentLike = MutableLiveData<FantooClubLikeResponse?>()
    val commentLike: LiveData<FantooClubLikeResponse?> = _commentLike

    private var fantooClubComposeComment = FantooClubComposeComment()

    val commentMenuItems = mutableListOf(
        MoreMenuItem(R.drawable.icon_outline_siren, listOf(R.string.a_report_comment), false),
        MoreMenuItem(R.drawable.icon_outline_hide, listOf(R.string.a_block_comment), false)
    )

    val myCommentMenuItems = mutableListOf(
        MoreMenuItem(R.drawable.icon_outline_edit, listOf(R.string.a_modify_comment), false),
        MoreMenuItem(R.drawable.icon_outline_trash, listOf(R.string.a_delete_comment), false)
    )

    val blockedCommentMenuItems = mutableListOf(
        MoreMenuItem(R.drawable.icon_outline_hide, listOf(R.string.a_see_comment), false)
    )

    val reportCommentMenuItems = mutableListOf(
        MoreMenuItem(R.drawable.icon_outline_siren, listOf(R.string.a_report_comment), false)
    )

    private var isLogin = false
    private var isFollow = false
    lateinit var integUid: String

    private var reportMessages: List<ReportMessageItem>? = null

    init {
        viewModelScope.launch {
            dataStoreRepository.getBoolean(DataStoreKey.PREF_KEY_IS_LOGINED)?.let {
                isLogin = it
            }
            integUid = dataStoreRepository.getString(DataStoreKey.PREF_KEY_UID).toString()
            fetchReportMessages()
        }
    }

    fun fetchFantooClubFollow(clubId: String) = viewModelScope.launch {
        val response = fantooClubRepository.fetchFantooClubFollow(clubId, integUid)
        Timber.d("fetchFantooClubFollow responseData : $response")
        when (response) {
            is ResultWrapper.Success -> {
                isFollow = response.data.followYn
            }
            is ResultWrapper.GenericError -> {
                Timber.d("response error code : ${response.code} , server msg : ${response.message} , message : ${response.errorData?.message}")
            }
            is ResultWrapper.NetworkError -> {
                // TODO handling network error
                Timber.d("network error")
            }
        }
    }

    fun fetchFantooClubCommentReplies(
        clubId: String,
        categoryCode: String,
        postId: String,
        replyId: String
    ) = viewModelScope.launch {
        val response =
            fantooClubRepository.fetchFantooClubCommentReplies(
                clubId,
                categoryCode,
                postId,
                replyId,
                integUid
            )
        Timber.d("fetchFantooClubCommentsDetail, responseData : $response")
        when (response) {
            is ResultWrapper.Success -> {
                _comments.value = response.data.replyList
            }
            is ResultWrapper.GenericError -> {
                Timber.d("response error code : ${response.code} , server msg : ${response.message} , message : ${response.errorData?.message}")
            }
            is ResultWrapper.NetworkError -> {
                // TODO handling network error
                Timber.d("network error")
            }
        }
    }

    fun composeFantooClubCommentReply(
        clubId: String,
        categoryCode: String,
        postId: String,
        replyId: String,
        composeCommentReply: ComposeCommentReply
    ) = viewModelScope.launch {
        val response = fantooClubRepository.composeFantooClubCommentReply(
            clubId,
            categoryCode,
            postId,
            replyId,
            composeCommentReply
        )
        Timber.d("composeFantooClubCommentReply, responseData : $response")
        when (response) {
            is ResultWrapper.Success -> {
                Timber.d("composeFantooClubComment, success")
                _composeSuccess.value = true
            }
            is ResultWrapper.GenericError -> {
                Timber.d("response error code : ${response.code} , server msg : ${response.message} , message : ${response.errorData?.message}")
                _composeSuccess.value = false
            }
            is ResultWrapper.NetworkError -> {
                // TODO handling network error
                Timber.d("network error")
            }
        }
    }

    fun modifyFantooClubComment(
        clubId: String,
        categoryCode: String,
        postId: String,
        replyId: String,
        composeComment: ComposeComment
    ) = viewModelScope.launch {
        val response = fantooClubRepository.modifyFantooClubComment(
            clubId,
            categoryCode,
            postId,
            replyId,
            composeComment
        )
        Timber.d("modifyFantooClubComment, responseData : $response")
        when (response) {
            is ResultWrapper.Success -> {
                Timber.d("modifyFantooClubComment, success")
                _composeSuccess.value = true
            }
            is ResultWrapper.GenericError -> {
                Timber.d("response error code : ${response.code} , server msg : ${response.message} , message : ${response.errorData?.message}")
                _composeSuccess.value = false
            }
            is ResultWrapper.NetworkError -> {
                // TODO handling network error
                Timber.d("network error")
            }
        }
    }

    fun modifyFantooClubCommentReply(
        clubId: String,
        categoryCode: String,
        postId: String,
        replyId: String,
        rereplyId: String,
        composeCommentReply: ComposeCommentReply
    ) = viewModelScope.launch {
        val response = fantooClubRepository.modifyFantooClubCommentReply(
            clubId,
            categoryCode,
            postId,
            replyId,
            rereplyId,
            composeCommentReply
        )
        Timber.d("modifyFantooClubCommentReply, responseData : $response")
        when (response) {
            is ResultWrapper.Success -> {
                Timber.d("modifyFantooClubCommentReply, success")
                _composeSuccess.value = true
            }
            is ResultWrapper.GenericError -> {
                Timber.d("response error code : ${response.code} , server msg : ${response.message} , message : ${response.errorData?.message}")
                _composeSuccess.value = false
            }
            is ResultWrapper.NetworkError -> {
                // TODO handling network error
                Timber.d("network error")
            }
        }
    }

    fun deleteFantooClubComment(
        clubId: String,
        categoryCode: String,
        postId: String,
        replyId: String
    ) = viewModelScope.launch {
        val response = fantooClubRepository.deleteFantooClubComment(
            clubId,
            categoryCode,
            postId,
            replyId,
            IntegUid(integUid)
        )
        Timber.d("deleteFantooClubComment, responseData : $response")
        when (response) {
            is ResultWrapper.Success -> {
                Timber.d("deleteFantooClubComment, success")
                _composeSuccess.value = true
            }
            is ResultWrapper.GenericError -> {
                Timber.d("response error code : ${response.code} , server msg : ${response.message} , message : ${response.errorData?.message}")
            }
            is ResultWrapper.NetworkError -> {
                // TODO handling network error
                Timber.d("network error")
            }
        }
    }

    fun deleteFantooClubCommentReply(
        clubId: String,
        categoryCode: String,
        postId: String,
        replyId: String,
        rereplyId: String
    ) = viewModelScope.launch {
        val response = fantooClubRepository.deleteFantooClubCommentReply(
            clubId,
            categoryCode,
            postId,
            replyId,
            rereplyId,
            IntegUid(integUid)
        )
        Timber.d("deleteFantooClubCommentReply, responseData : $response")
        when (response) {
            is ResultWrapper.Success -> {
                Timber.d("deleteFantooClubCommentReply, success")
                _composeSuccess.value = true
            }
            is ResultWrapper.GenericError -> {
                Timber.d("response error code : ${response.code} , server msg : ${response.message} , message : ${response.errorData?.message}")
            }
            is ResultWrapper.NetworkError -> {
                // TODO handling network error
                Timber.d("network error")
            }
        }
    }

    fun reportFantooClubComment(
        clubId: String,
        categoryCode: String,
        postId: String,
        replyId: String,
        reportMessageId: Int
    ) = viewModelScope.launch {
        val response = fantooClubRepository.reportFantooClubComment(
            clubId,
            categoryCode,
            postId,
            replyId,
            ReportMessage(integUid, reportMessageId)
        )
        Timber.d("reportFantooClubComment, responseData : $response")
        when (response) {
            is ResultWrapper.Success -> {
                Timber.d("reportFantooClubComment, success")
                _composeSuccess.value = true
            }
            is ResultWrapper.GenericError -> {
                Timber.d("response error code : ${response.code} , server msg : ${response.message} , message : ${response.errorData?.message}")
                _errorMsg.value = response.message
            }
            is ResultWrapper.NetworkError -> {
                // TODO handling network error
                Timber.d("network error")
            }
        }
    }

    fun reportFantooClubCommentReply(
        clubId: String,
        categoryCode: String,
        postId: String,
        replyId: String,
        rereplyId: String,
        reportMessageId: Int
    ) = viewModelScope.launch {
        val response = fantooClubRepository.reportFantooClubCommentReply(
            clubId,
            categoryCode,
            postId,
            replyId,
            rereplyId,
            ReportMessage(integUid, reportMessageId)
        )
        Timber.d("reportFantooClubCommentReply, responseData : $response")
        when (response) {
            is ResultWrapper.Success -> {
                Timber.d("reportFantooClubCommentReply, success")
                _composeSuccess.value = true
            }
            is ResultWrapper.GenericError -> {
                Timber.d("response error code : ${response.code} , server msg : ${response.message} , message : ${response.errorData?.message}")
                _errorMsg.value = response.message
            }
            is ResultWrapper.NetworkError -> {
                // TODO handling network error
                Timber.d("network error")
            }
        }
    }

    fun blockFantooClubComment(
        clubId: String,
        categoryCode: String,
        postId: String,
        replyId: String
    ) = viewModelScope.launch {
        val response = fantooClubRepository.blockFantooClubComment(
            clubId,
            categoryCode,
            postId,
            replyId,
            IntegUid(integUid)
        )
        Timber.d("blockFantooClubComment, responseData : $response")
        when (response) {
            is ResultWrapper.Success -> {
                Timber.d("blockFantooClubComment, success")
                _composeSuccess.value = true
            }
            is ResultWrapper.GenericError -> {
                Timber.d("response error code : ${response.code} , server msg : ${response.message} , message : ${response.errorData?.message}")
                _errorMsg.value = response.message
            }
            is ResultWrapper.NetworkError -> {
                // TODO handling network error
                Timber.d("network error")
            }
        }
    }

    fun blockFantooClubCommentReply(
        clubId: String,
        categoryCode: String,
        postId: String,
        replyId: String,
        rereplyId: String
    ) = viewModelScope.launch {
        val response = fantooClubRepository.blockFantooClubCommentReply(
            clubId,
            categoryCode,
            postId,
            replyId,
            rereplyId,
            IntegUid(integUid)
        )
        Timber.d("blockFantooClubCommentReply, responseData : $response")
        when (response) {
            is ResultWrapper.Success -> {
                Timber.d("blockFantooClubCommentReply, success")
                _composeSuccess.value = true
            }
            is ResultWrapper.GenericError -> {
                Timber.d("response error code : ${response.code} , server msg : ${response.message} , message : ${response.errorData?.message}")
                _errorMsg.value = response.message
            }
            is ResultWrapper.NetworkError -> {
                // TODO handling network error
                Timber.d("network error")
            }
        }
    }

    fun setFantooClubCommentLikeAndDislike(
        clubId: String,
        likeType: String,
        categoryCode: String,
        postId: String,
        replyId: String
    ) =
        viewModelScope.launch {
            val response = fantooClubRepository.setFantooClubCommentLikeAndDislike(
                clubId,
                likeType,
                categoryCode,
                postId,
                replyId,
                IntegUid(integUid)
            )
            Timber.d("setFantooClubLikeAndDislike, responseData : $response")
            when (response) {
                is ResultWrapper.Success -> {
                    _commentLike.value = response.data
                }
                is ResultWrapper.GenericError -> {
                    Timber.d("response error code : ${response.code} , server msg : ${response.message} , message : ${response.errorData?.message}")
                    if (response.code == "FE1013") {
                        _errorMsg.value = response.code
                    } else {
                        _errorMsg.value = response.message
                    }
                }
                is ResultWrapper.NetworkError -> {
                    // TODO handling network error
                    Timber.d("network error")
                }
            }
        }

    fun setFantooClubCommentReplyLikeAndDislike(
        clubId: String,
        likeType: String,
        categoryCode: String,
        postId: String,
        parentReplyId: String,
        replyId: String
    ) =
        viewModelScope.launch {
            val response = fantooClubRepository.setFantooClubCommentReplyLikeAndDislike(
                clubId,
                likeType,
                categoryCode,
                postId,
                parentReplyId,
                replyId,
                IntegUid(integUid)
            )
            Timber.d("setFantooClubLikeAndDislike, responseData : $response")
            when (response) {
                is ResultWrapper.Success -> {
                    _commentLike.value = response.data
                }
                is ResultWrapper.GenericError -> {
                    Timber.d("response error code : ${response.code} , server msg : ${response.message} , message : ${response.errorData?.message}")
                    if (response.code == "FE1013") {
                        _errorMsg.value = response.code
                    } else {
                        _errorMsg.value = response.message
                    }
                }
                is ResultWrapper.NetworkError -> {
                    // TODO handling network error
                    Timber.d("network error")
                }
            }
        }

    fun fetchReportMessages() = viewModelScope.launch {
        val response = fantooClubRepository.fetchReportMessages()
        Timber.d("fetchReportMessages, responseData : $response")
        when (response) {
            is ResultWrapper.Success -> {
                reportMessages = response.data.reportList
            }
            is ResultWrapper.GenericError -> {
                Timber.d("response error code : ${response.code} , server msg : ${response.message} , message : ${response.errorData?.message}")
            }
            is ResultWrapper.NetworkError -> {
                // TODO handling network error
                Timber.d("network error")
            }
        }
    }

    fun uploadImage(file: MultipartBody.Part) = viewModelScope.launch {
        val cloudFlareKey = application.applicationContext.getString(R.string.cloudFlareKey)
        val response = uploadRepository.imageUploadToCloudFlare(cloudFlareKey, file)
        Timber.d("responseData : $response")
        when (response) {
            is ResultWrapper.Success -> {
                val resultData = response.data
                if (resultData.success) {
                    val attaches = mutableListOf<FantooClubPostAttach>()
                    attaches.add(
                        FantooClubPostAttach(
                            resultData.result.id,
                            ContentsDetailViewModel.COMMENT_ATTACH_IMAGE_TYPE
                        )
                    )
                    _uploadImageSuccess.value = attaches
                } else {
                    _uploadImageSuccess.value = null
                }
            }
            is ResultWrapper.GenericError -> {
                Timber.d("response error code : ${response.code} , server msg : ${response.message} , message : ${response.errorData?.message}")
                _uploadImageSuccess.value = null
            }
            is ResultWrapper.NetworkError -> {
                // TODO handling network error
                Timber.d("network error")
            }
        }
    }

    fun setFantooClubComposeComment(
        fantooClubComment: FantooClubComment?,
        mode: ComposeCommentMode
    ) {
        fantooClubComposeComment = FantooClubComposeComment(fantooClubComment, mode)
    }

    fun getFantooClubComposeComment() = fantooClubComposeComment
    fun getReportMessages() = reportMessages

    fun setReplyContent(content: String) {
        _sendBtnEnabled.value = content.isNotEmpty()
    }

    fun setIsScroll(scrolled: Boolean) {
        _isScroll.value = scrolled
    }

    fun getUid() = integUid
    fun getIsLogin() = isLogin
    fun getIsFollow() = isFollow
}