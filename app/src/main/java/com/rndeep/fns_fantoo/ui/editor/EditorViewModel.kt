package com.rndeep.fns_fantoo.ui.editor

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import androidx.lifecycle.*
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.dto.ComposeClubPost
import com.rndeep.fns_fantoo.data.remote.dto.ComposeCommunityPost
import com.rndeep.fns_fantoo.data.remote.model.ClubCategoryItem
import com.rndeep.fns_fantoo.data.remote.model.ClubSubCategoryItem
import com.rndeep.fns_fantoo.data.remote.model.community.CategoryBoardCategoryList
import com.rndeep.fns_fantoo.data.remote.model.editor.Attach
import com.rndeep.fns_fantoo.data.remote.model.editor.AttachClub
import com.rndeep.fns_fantoo.data.remote.model.editor.Hashtag
import com.rndeep.fns_fantoo.data.remote.model.editor.ModifyCommunityPost
import com.rndeep.fns_fantoo.repositories.DataStoreKey
import com.rndeep.fns_fantoo.repositories.DataStoreRepository
import com.rndeep.fns_fantoo.repositories.UploadRepository
import com.rndeep.fns_fantoo.ui.club.ClubRepository
import com.rndeep.fns_fantoo.ui.community.CommunityRepository
import com.rndeep.fns_fantoo.utils.asMultipart
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import timber.log.Timber
import java.io.File
import java.util.*
import javax.inject.Inject

@HiltViewModel
class EditorViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val communityRepository: CommunityRepository,
    private val clubRepository: ClubRepository,
    private val uploadRepository: UploadRepository
) : ViewModel() {

    private var _attachedItem = MutableLiveData<List<MultimediaItem>>()
    val attachedItem: LiveData<List<MultimediaItem>>
        get() = _attachedItem

    private val _hashtags = MutableLiveData<List<String>>(emptyList())
    val hashtags: LiveData<List<String>> = _hashtags

    private var attachedItemList = mutableListOf<MultimediaItem>()

    val attachedItemFileList = mutableListOf<MultimediaFile>()

    private val cloudFlareIDList = mutableListOf<CloudFlareItem>()

    private val _cloudFlareSendResult = MutableLiveData<List<CloudFlareItem>>()
    val cloudFlareSendResult: LiveData<List<CloudFlareItem>> get() = _cloudFlareSendResult

    private var hashtagList = mutableListOf<String>()

    private val _hashtagScroll = MutableLiveData<Boolean>()
    val hashtagScroll: LiveData<Boolean> = _hashtagScroll

    private val _multimediaScroll = MutableLiveData<Boolean>()
    val multimediaScroll: LiveData<Boolean> = _multimediaScroll

    private val _textScroll = MutableLiveData<Boolean>()
    val textScroll: LiveData<Boolean> = _textScroll

    private val _isComplete = MutableLiveData<String?>()
    val isComplete: LiveData<String?> = _isComplete

    private val _showSubject = MutableLiveData<Boolean>()
    val showSubject: LiveData<Boolean> = _showSubject

    private val _title = MutableStateFlow("")
    private val _content = MutableStateFlow("")
    private val _board = MutableStateFlow("")
    private val _subject = MutableStateFlow("")

    val isRegisterBtnEnabled: Flow<Boolean> =
        combine(_title, _content, _board, _subject) { title, content, board, subject ->
            return@combine title.isNotEmpty() and content.isNotEmpty() and board.isNotEmpty() and subject.isNotEmpty()
        }

    var enterCnt = 0

    val boardItems = mutableListOf<BoardItem>()
    val subjectItems = mutableListOf<BoardItem>()

    private val _bottomSheetBoardItems = MutableLiveData<List<BoardItem>>()
    val bottomSheetBoardItems: LiveData<List<BoardItem>> = _bottomSheetBoardItems

    val emojiRegex = "[\\p{C}\\p{So}\uFE00-\uFE0F\\x{E0100}-\\x{E01EF}]".toRegex()

    private lateinit var accessToken: String
    private lateinit var integUid: String
    private lateinit var categories: List<CategoryBoardCategoryList>
    private lateinit var subCategories: List<CategoryBoardCategoryList>

    private lateinit var clubOneDepthCategories: List<ClubCategoryItem>
    private lateinit var clubTwoDepthCategories: List<ClubSubCategoryItem>

    private lateinit var editorInfo: EditorInfo
    private var composeCommunityPost: ComposeCommunityPost
    private var composeClubPost: ComposeClubPost
    var communityAttaches: MutableList<Attach> = mutableListOf()
    val clubAttaches: MutableList<AttachClub> = mutableListOf()

    private var _isNoticeRegisterChecked = MutableLiveData<Boolean>()
    val isNoticeRegisterChecked: LiveData<Boolean> = _isNoticeRegisterChecked

    private var _isTopFixedChecked = MutableLiveData<Boolean>()
    val isTopFixedChecked: LiveData<Boolean> = _isTopFixedChecked

    private var _modifyCommunityPost = MutableLiveData<ComposeCommunityPost>()
    val modifyCommunityPost: LiveData<ComposeCommunityPost> = _modifyCommunityPost

    private var _modifyCommunityBoard = MutableLiveData<CategoryBoardCategoryList>()
    val modifyCommunityBoard: LiveData<CategoryBoardCategoryList> = _modifyCommunityBoard

    private var _modifyCommunitySubject = MutableLiveData<CategoryBoardCategoryList>()
    val modifyCommunitySubject: LiveData<CategoryBoardCategoryList> = _modifyCommunitySubject

    var isModifyMode = false

    init {
        initAccessInfo()
        composeCommunityPost = ComposeCommunityPost(false, null, "", null, integUid, null, "")
        composeClubPost = ComposeClubPost(null, "", null, integUid, null, "", false)
        _isNoticeRegisterChecked.value = false
        _isTopFixedChecked.value = false
    }

    private fun initAccessInfo() = viewModelScope.launch {
        accessToken = dataStoreRepository.getString(DataStoreKey.PREF_KEY_ACCESS_TOKEN).toString()
        integUid = dataStoreRepository.getString(DataStoreKey.PREF_KEY_UID).toString()
    }

    fun initBoardItems(type: EditorType) {
        when (type) {
            EditorType.CLUB -> editorInfo.clubId?.let { initClubBoardItems(it) }
            EditorType.COMMUNITY -> initCommunityBoardItems()
        }
    }

    private fun initCommunityBoardItems() = viewModelScope.launch {
        val response = communityRepository.fetchCommunityCategory(accessToken, integUid)
        Timber.d("responseData : $response")
        when (response) {
            is ResultWrapper.Success -> {
                categories = response.data.categoryBoardList
                setBoardItems()
                if (isModifyMode) {
                    editorInfo.modifyCommunityPost?.let { modifyCommunityPost ->
                        setModifyModeCommunityBoard(modifyCommunityPost.boardCode)
                    }
                }
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

    private fun initClubBoardItems(clubId: String) = viewModelScope.launch {
        val response = clubRepository.fetchClubOneDepthCategories(clubId, integUid)
        Timber.d("responseData : $response")
        when (response) {
            is ResultWrapper.Success -> {
                clubOneDepthCategories = response.data.categoryList
                setBoardItems()
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

    private fun setBoardItems() {
        boardItems.clear()
        when (editorInfo.editorType) {
            EditorType.CLUB -> {
                clubOneDepthCategories.forEach { list ->
                    if (list.categoryCode == "freeboard" || list.categoryCode == "archive") {
                        boardItems.add(BoardItem(list.categoryCode, list.categoryName, false))
                    }
                }
            }
            EditorType.COMMUNITY -> {
                categories.forEach { list ->
                    boardItems.add(BoardItem(list.code, list.codeNameEn, false))
                }
            }
        }
        _bottomSheetBoardItems.value = boardItems
    }

    fun composePost() {
        when (editorInfo.editorType) {
            EditorType.CLUB -> composeClubPost()
            EditorType.COMMUNITY -> {
                if (editorInfo.modifyCommunityPost != null) {
                    modifyCommunityPost()
                } else {
                    composeCommunityPost()
                }
            }
        }
    }

    private fun composeCommunityPost() = viewModelScope.launch {
        setHashtags()
        val response =
            communityRepository.composeCommunityPost(
                accessToken,
                composeCommunityPost,
                _board.value
            )
        Timber.d("responseData : $response")
        when (response) {
            is ResultWrapper.Success -> {
                _isComplete.value = null
            }
            is ResultWrapper.GenericError -> {
                Timber.d("response error code : ${response.code} , server msg : ${response.message} , message : ${response.errorData?.message}")
                _isComplete.value = "code: ${response.code}, message: ${response.message}"
            }
            is ResultWrapper.NetworkError -> {
                // TODO handling network error
                Timber.d("network error")
            }
        }
    }

    private fun modifyCommunityPost() = viewModelScope.launch {
        setHashtags()
        val postId = editorInfo.modifyCommunityPost!!.postId
        val response =
            communityRepository.modifyCommunityPost(
                accessToken,
                composeCommunityPost,
                _board.value,
                postId
            )
        Timber.d("modifyCommunityPost, responseData : $response")
        when (response) {
            is ResultWrapper.Success -> {
                _isComplete.value = null
            }
            is ResultWrapper.GenericError -> {
                Timber.d("response error code : ${response.code} , server msg : ${response.message} , message : ${response.errorData?.message}")
                _isComplete.value = "code: ${response.code}, message: ${response.message}"
            }
            is ResultWrapper.NetworkError -> {
                // TODO handling network error
                Timber.d("network error")
            }
        }
    }

    private fun composeClubPost() = viewModelScope.launch {
        setHashtags()
        val response =
            editorInfo.clubId?.let {
                val categoryCode = if (_isNoticeRegisterChecked.value == true) {
                    CATEGORY_IS_NOTICE
                } else {
                    _subject.value
                }
                clubRepository.composeClubPost(
                    accessToken, composeClubPost,
                    it, categoryCode
                )
            }
        Timber.d("responseData : $response")
        when (response) {
            is ResultWrapper.Success -> {
                _isComplete.value = null
            }
            is ResultWrapper.GenericError -> {
                Timber.d("response error code : ${response.code} , server msg : ${response.message} , message : ${response.errorData?.message}")
                _isComplete.value = "code: ${response.code}, message: ${response.message}"
            }
            is ResultWrapper.NetworkError -> {
                // TODO handling network error
                Timber.d("network error")
            }
            else -> {
                Timber.d("else, editorInfo: $editorInfo")
            }
        }
    }

    fun initSubjectBoardItems(code: String) {
        when (editorInfo.editorType) {
            EditorType.CLUB -> editorInfo.clubId?.let { fetchClubTwoDepthCategories(it, code) }
            EditorType.COMMUNITY -> fetchCommunitySubCategory(code)
        }
    }

    private fun fetchCommunitySubCategory(code: String) = viewModelScope.launch {
        val response = communityRepository.fetchCommunitySubCategory(accessToken, code, integUid)
        Timber.d("responseData : $response")
        when (response) {
            is ResultWrapper.Success -> {
                subCategories = response.data.subCategoryList
                if (isModifyMode) {
                    editorInfo.modifyCommunityPost?.let { modifyCommunityPost ->
                        modifyCommunityPost.subCode?.let { subCode ->
                            setSubject(subCode)
                            val item = subCategories.find { it.code == subCode }
                            item?.let {
                                _modifyCommunitySubject.value = it
                            }
                        }
                    }
                } else {
                    if (subCategories.isEmpty()) {
                        setSubject(CATEGORY_IS_EMPTY)
                    } else {
                        setSubject("")
                        _showSubject.value = true
                    }
                }
                setSubjectItems()
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

    private fun fetchClubTwoDepthCategories(clubId: String, categoryCode: String) =
        viewModelScope.launch {
            val response =
                clubRepository.fetchClubTwoDepthCategories(clubId, categoryCode, integUid)
            Timber.d("responseData : $response")
            when (response) {
                is ResultWrapper.Success -> {
                    clubTwoDepthCategories = response.data.categoryList
                    if (clubTwoDepthCategories.isEmpty()) {
                        setSubject("archive")
                    } else {
                        setSubject("")
                        setSubjectItems()
                        _showSubject.value = true
                    }
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

    private fun setSubjectItems() {
        subjectItems.clear()
        when (editorInfo.editorType) {
            EditorType.CLUB -> {
                clubTwoDepthCategories.forEach { list ->
                    subjectItems.add(BoardItem(list.categoryCode, list.categoryName, false))
                }
            }
            EditorType.COMMUNITY -> {
                subCategories.forEach { list ->
                    subjectItems.add(BoardItem(list.code, list.codeNameEn, false))
                }
            }
        }
        _bottomSheetBoardItems.value = subjectItems
    }

    private fun updateAttachedFile(fileUrl: String?, mediaType: MultimediaType, context: Context) {
        Uri.parse(fileUrl).asMultipart(
            "file",
            "${mediaType.name}_${Date().time}",
            context.contentResolver
        ).run {
            this ?: return
            attachedItemFileList.add(MultimediaFile(this, mediaType, fileUrl!!))
        }
    }

    private fun updateAttachedItem(item: MultimediaItem, context: Context) {
        viewModelScope.launch {
            updateAttachedFile(item.url, item.type, context)
        }
        attachedItemList.add(item)
        _attachedItem.value = attachedItemList
    }

    fun attachImage(imageUrl: String, context: Context) {
        val imageItem = MultimediaItem(imageUrl, MultimediaType.IMAGE)
        updateAttachedItem(imageItem, context)
    }

    fun attachVideo(videoUrl: String, context: Context) {
        val videoItem = MultimediaItem(videoUrl, MultimediaType.VIDEO)
        updateAttachedItem(videoItem, context)
    }

    fun attachHashtag(hashtags: MutableList<String>) {
        hashtagList = hashtags
        Timber.d("attachHashtag, item : $hashtagList")
        _hashtags.value = hashtagList
    }

    fun addHashtag(hashtag: String) {
        hashtagList.add(hashtag)
        Timber.d("addHashtag, item : $hashtagList")
        _hashtags.value = hashtagList
    }

    fun deleteHashtag(hashtag: String) {
        Timber.d("deleteHashtag, item : $hashtag")
        hashtagList.remove(hashtag)
        _hashtags.value = hashtagList
    }

    fun deleteAttachedItem(item: MultimediaItem) {
        attachedItemList.remove(item)
        val removeFile = attachedItemFileList.find { it.fileUrl == item.url }
        Timber.d("deleteAttachedItem , remove item : $item, removeFile: $removeFile")
        attachedItemFileList.remove(removeFile)

        if (isModifyMode) {
            editorInfo.modifyCommunityPost?.let { modifyCommunityPost ->
                modifyCommunityPost.attaches?.let { list ->
                    Timber.d("modify mode deleteAttachedItem , list : $list")
                    val modifyList = list.toMutableList()
                    modifyList.remove(list.find { it.id == item.url })
                    modifyCommunityPost.attaches = modifyList
                    communityAttaches = modifyList
                    composeCommunityPost.attaches = communityAttaches
                    Timber.d("modify mode deleteAttachedItem , after list : ${modifyCommunityPost.attaches}")
                }
            }
        }
        _attachedItem.value = attachedItemList
    }

    fun updateHashtagScroll(scrolled: Boolean) {
        Timber.d("updateScroll , scrolled : $scrolled")
        viewModelScope.launch {
            delay(250)
            _hashtagScroll.postValue(scrolled)
        }
    }

    fun updateMultimediaScroll(scrolled: Boolean) {
        Timber.d("multimediaScroll , scrolled : $scrolled")
        viewModelScope.launch {
            delay(250)
            _multimediaScroll.postValue(scrolled)
        }
    }

    fun updateTextScroll(scrolled: Boolean) {
        Timber.d("textScroll , scrolled : $scrolled")
        viewModelScope.launch {
            delay(250)
            _textScroll.postValue(scrolled)
        }
    }

    fun setAnonymous(anonymous: Boolean) {
        composeCommunityPost.anonymYn = anonymous
    }

    fun setTitle(title: String) {
        _title.value = title
        composeCommunityPost.title = title
        composeClubPost.subject = title
    }

    fun setContent(content: String) {
        _content.value = content
        composeCommunityPost.content = content
        composeClubPost.content = content
    }

    private fun setBoard(board: String) {
        _board.value = board
    }

    private fun setSubject(subject: String) {
        _subject.value = subject
    }

    private fun setHashtags() {
        Timber.d("setHashtags, count = ${hashtagList.count()}")
        if (hashtagList.isNotEmpty()) {
            val tempHashtags: MutableList<Hashtag> = mutableListOf()
            hashtagList.forEach { hashtag ->
                tempHashtags.add(Hashtag(hashtag))
            }
            composeClubPost.hashtags = hashtagList
            composeCommunityPost.hashtags = tempHashtags
        }
    }

    fun updateBoardItem(item: BoardItem, boardType: BoardType) {
        when (boardType) {
            BoardType.BOARD -> {
                boardItems.forEach { boardItem ->
                    boardItem.selected = boardItem.code == item.code
                    if (boardItem.selected) {
                        setBoard(boardItem.code)
                    }
                }
                _bottomSheetBoardItems.value = boardItems
            }
            BoardType.SUBJECT -> {
                subjectItems.forEach { subjectItem ->
                    subjectItem.selected = subjectItem.code == item.code
                    if (subjectItem.selected) {
                        composeCommunityPost.subCode = subjectItem.code
                        setSubject(subjectItem.code)
                    }
                }
                _bottomSheetBoardItems.value = subjectItems
            }
        }
    }

    fun sendCloudFlareData(
        cloudKey: String,
        file: MultipartBody.Part,
        fileType: MultimediaType,
        position: Int,
        preferences: SharedPreferences,
        sendFile: File?,
    ) {
        viewModelScope.launch {
            when (fileType) {
                MultimediaType.IMAGE -> {
                    uploadRepository.fileUploadImageToCloudFlare(cloudKey, file).run {
                        if (this == null) {
                            cloudFlareIDList.add(CloudFlareItem(false, position, null, fileType))
                        } else {
                            cloudFlareIDList.add(CloudFlareItem(true, position, this, fileType))
                            when (editorInfo.editorType) {
                                EditorType.CLUB -> clubAttaches.add(
                                    AttachClub(
                                        this,
                                        CLUB_ATTACH_IMAGE,
                                        null
                                    )
                                )
                                EditorType.COMMUNITY -> communityAttaches.add(
                                    Attach(
                                        COMMUNITY_ATTACH_IMAGE,
                                        this
                                    )
                                )
                            }
                        }
                    }
                }
                MultimediaType.VIDEO -> {
                    uploadRepository.tusVideoUpload(
                        preferences,
                        sendFile!!,
                        cloudKey,
                        "VIDEO_${Date().time}"
                    ).run {
                        if (this == null) {
                            cloudFlareIDList.add(CloudFlareItem(false, position, null, fileType))
                        } else {
                            cloudFlareIDList.add(CloudFlareItem(true, position, this, fileType))
                            when (editorInfo.editorType) {
                                EditorType.CLUB -> clubAttaches.add(
                                    AttachClub(
                                        this,
                                        CLUB_ATTACH_VIDEO,
                                        null
                                    )
                                )
                                EditorType.COMMUNITY -> communityAttaches.add(
                                    Attach(
                                        COMMUNITY_ATTACH_VIDEO,
                                        this
                                    )
                                )
                            }
                        }
                    }
                }
            }
            composeCommunityPost.attaches = communityAttaches
            composeClubPost.attaches = clubAttaches
            if (cloudFlareIDList.size == attachedItemFileList.size) {
                _cloudFlareSendResult.value = cloudFlareIDList
            }
        }
    }

    fun setEditorInfo(editorInfo: EditorInfo) {
        this@EditorViewModel.editorInfo = editorInfo
        editorInfo.modifyCommunityPost?.let { modifyCommunityPost ->
            setModifyCommunityPost(modifyCommunityPost)
        }
    }

    private fun setModifyCommunityPost(modifyCommunityPost: ModifyCommunityPost) {
        modifyCommunityPost.attaches?.let { list ->
            communityAttaches = list.toMutableList()
            val items = list.map { attach ->
                MultimediaItem(attach.id, getAttachType(attach.attachType))
            }
            attachedItemList = items.toMutableList()
            _attachedItem.value = attachedItemList
        }
        modifyCommunityPost.hashtags?.let { list ->
            val items = list.map { hashtag ->
                hashtag.tag
            }
            hashtagList = items.toMutableList()
            _hashtags.value = hashtagList
        }
        composeCommunityPost.apply {
            title = modifyCommunityPost.title
            content = modifyCommunityPost.content
            subCode = modifyCommunityPost.subCode
            anonymYn = modifyCommunityPost.anonymYn
            attaches = modifyCommunityPost.attaches
            hashtags = modifyCommunityPost.hashtags
        }
        _modifyCommunityPost.value = composeCommunityPost
        isModifyMode = true
    }

    private fun setModifyModeCommunityBoard(code: String) {
        setBoard(code)
        val item = categories.find { it.code == code }
        item?.let {
            _modifyCommunityBoard.value = it
        }
        initSubjectBoardItems(code)
    }

    fun setIsNoticeRegisterChecked(checked: Boolean) {
        _isNoticeRegisterChecked.value = checked
        if (checked) {
            setBoard(CATEGORY_IS_FREEBOARD)
            setSubject(CATEGORY_IS_EMPTY)
        } else {
            _board.value = ""
            _subject.value = ""
        }
    }

    fun setIsTopFixedChecked(checked: Boolean) {
        _isTopFixedChecked.value = checked
        composeClubPost.topYn = checked
    }

    private fun getAttachType(type: String): MultimediaType {
        return when (type) {
            COMMUNITY_ATTACH_IMAGE -> MultimediaType.IMAGE
            else -> MultimediaType.VIDEO
        }
    }

    companion object {
        const val CLUB_ATTACH_IMAGE = 0
        const val CLUB_ATTACH_VIDEO = 1
        const val COMMUNITY_ATTACH_IMAGE = "image"
        const val COMMUNITY_ATTACH_VIDEO = "video"
        const val CATEGORY_IS_NOTICE = "notice"
        const val CATEGORY_IS_FREEBOARD = "freeboard"
        const val CATEGORY_IS_EMPTY = "empty"
    }
}