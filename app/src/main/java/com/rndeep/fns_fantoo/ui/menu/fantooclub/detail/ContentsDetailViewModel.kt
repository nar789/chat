package com.rndeep.fns_fantoo.ui.menu.fantooclub.detail

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.dto.FantooClubLikeResponse
import com.rndeep.fns_fantoo.data.remote.model.IntegUid
import com.rndeep.fns_fantoo.data.remote.model.ReportMessageItem
import com.rndeep.fns_fantoo.data.remote.model.fantooclub.*
import com.rndeep.fns_fantoo.repositories.*
import com.rndeep.fns_fantoo.ui.menu.fantooclub.more.MoreMenuItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ContentsDetailViewModel @Inject constructor(
    private val application: Application,
    private val uploadRepository: UploadRepository,
    private val dataStoreRepository: DataStoreRepository,
    private val fantooClubRepository: FantooClubRepository,
    private val translateRepository: TranslateRepository
) : ViewModel() {

    private val _hashtags = MutableLiveData<List<String>>(emptyList())
    val hashtags: LiveData<List<String>> = _hashtags

    private val _comments = MutableLiveData<List<FantooClubComment>>()
    val comments: LiveData<List<FantooClubComment>> = _comments

    private val _detailLike = MutableLiveData<FantooClubLikeResponse?>()
    val detailLike: LiveData<FantooClubLikeResponse?> = _detailLike

    private val _commentLike = MutableLiveData<FantooClubLikeResponse?>()
    val commentLike: LiveData<FantooClubLikeResponse?> = _commentLike

    private val _errorMsg = MutableLiveData<String?>()
    val errorMsg: LiveData<String?> = _errorMsg

    private val _composeSuccess = MutableLiveData<Boolean>()
    val composeSuccess: LiveData<Boolean> = _composeSuccess

    private val _uploadImageSuccess = MutableLiveData<List<FantooClubPostAttach>?>()
    val uploadImageSuccess: LiveData<List<FantooClubPostAttach>?> = _uploadImageSuccess

    private val _sendBtnEnabled = MutableLiveData<Boolean>()
    val sendBtnEnabled: LiveData<Boolean> = _sendBtnEnabled

    private val _isScroll = MutableLiveData<Boolean>()
    val isScroll: LiveData<Boolean> = _isScroll

    private val _isSaved = MutableLiveData<Boolean>()
    val isSaved: LiveData<Boolean> = _isSaved

    private val _translatedContents = MutableLiveData<FantooClubPost>()
    val translatedContents: LiveData<FantooClubPost> = _translatedContents

    private val _updateCommentsAdapter = MutableLiveData<Int>()
    val updateCommentsAdapter: LiveData<Int> = _updateCommentsAdapter

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
    private var isTranslatedContents = false

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

    fun fetchFantooClubComments(
        clubId: String,
        categoryCode: String,
        postId: String
    ): Flow<PagingData<FantooClubComment>> {
        return fantooClubRepository.getFantooClubCommentResultStream(
            clubId, categoryCode, postId, integUid
        ).cachedIn(viewModelScope)
    }

    fun composeFantooClubComment(
        clubId: String,
        categoryCode: String,
        postId: String,
        composeComment: ComposeComment
    ) = viewModelScope.launch {
        val response = fantooClubRepository.composeFantooClubComment(
            clubId,
            categoryCode,
            postId,
            composeComment
        )
        Timber.d("composeFantooClubComment, responseData : $response")
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
                Timber.d("deleteFantooClubComment, success")
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

    fun setFantooClubPostLikeAndDislike(
        clubId: String,
        likeType: String,
        categoryCode: String,
        postId: String
    ) =
        viewModelScope.launch {
            val response = fantooClubRepository.setFantooClubPostLikeAndDislike(
                clubId,
                likeType,
                categoryCode,
                postId,
                IntegUid(integUid)
            )
            Timber.d("setFantooClubLikeAndDislike, responseData : $response")
            when (response) {
                is ResultWrapper.Success -> {
                    _detailLike.value = response.data
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
                            COMMENT_ATTACH_IMAGE_TYPE
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

    fun startTranslateContents(contents: FantooClubPost) = viewModelScope.launch {
        if (isTranslatedContents) {
            contents.translatedTitle = null
            contents.translatedBody = null
            _translatedContents.value = contents
            isTranslatedContents = false
        } else {
            val title = contents.subject!!
            val body = contents.content
            val translateList = if (body == null) {
                listOf(title)
            } else {
                listOf(title, body)
            }
            Timber.d("startTranslateContents, contents.title: $title , body: $body")
            contents.let {
                translateRepository.translateWords(
                    translateList,
                    dataStoreRepository.getString(DataStoreKey.PREF_KEY_LANGUAGE_CODE) ?: "en"
                ).run {
                    this.first?.let { messages ->
                        Timber.d("startTranslateContents, size: ${messages.size} , messages: $messages")
                        if (messages.isNotEmpty()) {
                            contents.translatedTitle = messages[0].text
                            contents.translatedBody = messages[1].text
                            _translatedContents.value = contents
                            isTranslatedContents = true
                        }
                    }
                    this.second?.let { errorBody ->
                        Timber.d("startTranslateComment error: $errorBody")
                    }
                }
            }
        }
    }

    fun startTranslateComment(comment: FantooClubComment, position: Int) = viewModelScope.launch {
        val content = comment.content
        Timber.d("startTranslateComment, content : $content")
        content?.let {
            translateRepository.translateWords(
                listOf(it),
                dataStoreRepository.getString(DataStoreKey.PREF_KEY_LANGUAGE_CODE) ?: "en"
            ).run {
                this.first?.let { messages ->
                    if (messages.isNotEmpty()) {
                        comment.translatedContent = messages[0].text
                        _updateCommentsAdapter.value = position
                    }
                }
                this.second?.let { errorBody ->
                    Timber.d("startTranslateComment error: $errorBody")
                }
            }
        }
    }

    fun setHashtags(list: List<String>) {
        _hashtags.value = list
    }

    fun setReplyContent(content: String) {
        _sendBtnEnabled.value = content.isNotEmpty()
    }

    fun setIsScroll(scrolled: Boolean) {
        _isScroll.value = scrolled
    }

    fun startSend() {
        Timber.d("start send comment")
    }

    fun saveContents(saved: Boolean) {
        Timber.d("save contents, isSaved: $saved")
        setIsSaved(saved)
    }

    fun setIsSaved(saved: Boolean) {
        _isSaved.value = saved
    }

    fun setFantooClubComposeComment(
        fantooClubComment: FantooClubComment?,
        mode: ComposeCommentMode
    ) {
        fantooClubComposeComment = FantooClubComposeComment(fantooClubComment, mode)
    }

    fun getFantooClubComposeComment() = fantooClubComposeComment
    fun getReportMessages() = reportMessages
    fun getUid() = integUid
    fun getIsLogin() = isLogin
    fun getIsFollow() = isFollow

    companion object {
        const val COMMENT_ATTACH_IMAGE_TYPE = 0
    }

}