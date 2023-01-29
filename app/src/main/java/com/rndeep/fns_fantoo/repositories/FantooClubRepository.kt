package com.rndeep.fns_fantoo.repositories

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.rndeep.fns_fantoo.data.remote.BaseNetRepo
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.api.FantooClubService
import com.rndeep.fns_fantoo.data.remote.dto.*
import com.rndeep.fns_fantoo.data.remote.model.ClubBasicInfo
import com.rndeep.fns_fantoo.data.remote.model.IntegUid
import com.rndeep.fns_fantoo.data.remote.model.fantooclub.*
import com.rndeep.fns_fantoo.data.remote.pagingsource.FantooClubCommentPagingSource
import com.rndeep.fns_fantoo.data.remote.pagingsource.FantooClubPostPagingSource
import com.rndeep.fns_fantoo.data.remote.pagingsource.FantooClubPostSearchPagingSource
import com.rndeep.fns_fantoo.di.NetworkModule
import com.rndeep.fns_fantoo.ui.menu.fantooclub.FantooClub
import com.rndeep.fns_fantoo.ui.menu.fantooclub.category.Category
import com.rndeep.fns_fantoo.ui.menu.fantooclub.contents.Contents
import com.rndeep.fns_fantoo.ui.menu.fantooclub.detail.comments.Comments
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import javax.inject.Inject

class FantooClubRepository @Inject constructor(
    private val context: Context,
    @NetworkModule.ApiServer private val fantooClubService: FantooClubService
) : BaseNetRepo() {

    suspend fun fetchFantooClubPosts(
        clubId: String,
        categoryCode : String,
        integUid: String?,
        nextId: String?,
        size: String
    ): ResultWrapper<FantooClubPostsResponse> = safeApiCall(Dispatchers.IO) {
        fantooClubService.fetchFantooClubPosts(clubId, categoryCode, integUid, nextId, size)
    }

    fun getFantooClubPostResultStream(
        clubId: String,
        categoryCode: String,
        integUid: String?
    ): Flow<PagingData<FantooClubPost>> {
        return Pager(
            config = PagingConfig(enablePlaceholders = false, pageSize = NETWORK_PAGE_SIZE),
            pagingSourceFactory = { FantooClubPostPagingSource(fantooClubService, clubId, categoryCode, integUid) }
        ).flow
    }

    suspend fun fetchFantooClubBasicInfo(
        clubId: String
    ): ResultWrapper<ClubBasicInfo> = safeApiCall(Dispatchers.IO) {
        fantooClubService.getClubBasicInfo(clubId, "")
    }

    suspend fun fetchFantooClubFollow(
        clubId: String,
        integUid: String
    ): ResultWrapper<FantooClubIsFollowResponse> = safeApiCall(Dispatchers.IO) {
        fantooClubService.fetchFantooClubFollow(clubId, integUid)
    }

    suspend fun setFantooClubFollow(
        clubId: String,
        integUid: IntegUid
    ): ResultWrapper<FantooClubFollowResponse> = safeApiCall(Dispatchers.IO) {
        fantooClubService.setFantooClubFollow(clubId, integUid)
    }

    suspend fun setFantooClubPostLikeAndDislike(
        clubId: String,
        likeType: String,
        categoryCode: String,
        postId: String,
        integUid: IntegUid
    ): ResultWrapper<FantooClubLikeResponse> = safeApiCall(Dispatchers.IO) {
        fantooClubService.setFantooClubPostLikeAndDislike(clubId, likeType, categoryCode, postId, integUid)
    }

    suspend fun setFantooClubCommentLikeAndDislike(
        clubId: String,
        likeType: String,
        categoryCode: String,
        postId: String,
        replyId: String,
        integUid: IntegUid
    ): ResultWrapper<FantooClubLikeResponse> = safeApiCall(Dispatchers.IO) {
        fantooClubService.setFantooClubCommentLikeAndDislike(
            clubId,
            likeType,
            categoryCode,
            postId,
            replyId,
            integUid
        )
    }

    suspend fun setFantooClubCommentReplyLikeAndDislike(
        clubId: String,
        likeType: String,
        categoryCode: String,
        postId: String,
        parentReplyId : String,
        replyId: String,
        integUid: IntegUid
    ): ResultWrapper<FantooClubLikeResponse> = safeApiCall(Dispatchers.IO) {
        fantooClubService.setFantooClubCommentReplyLikeAndDislike(
            clubId,
            likeType,
            categoryCode,
            postId,
            parentReplyId,
            replyId,
            integUid
        )
    }

    suspend fun fetchFantooClubCategories(
        clubId: String,
        categoryCode: String,
        integUid: String?
    ): ResultWrapper<FantooClubCategoryResponse> = safeApiCall(Dispatchers.IO) {
        fantooClubService.fetchFantooClubCategories(clubId, categoryCode, integUid)
    }

    fun getFantooClubCommentResultStream(
        clubId: String,
        categoryCode: String,
        postId: String,
        integUid: String?
    ): Flow<PagingData<FantooClubComment>> {
        return Pager(
            config = PagingConfig(enablePlaceholders = false, pageSize = NETWORK_PAGE_SIZE),
            pagingSourceFactory = {
                FantooClubCommentPagingSource(
                    fantooClubService,
                    clubId,
                    categoryCode,
                    postId,
                    integUid
                )
            }
        ).flow
    }

    suspend fun fetchFantooClubCommentReplies(
        clubId: String,
        categoryCode: String,
        postId: String,
        replyId: String,
        integUid: String?
    ): ResultWrapper<FantooClubCommentsResponse> = safeApiCall(Dispatchers.IO) {
        fantooClubService.fetchFantooClubCommentReplies(clubId, categoryCode, postId, replyId, integUid)
    }

    suspend fun composeFantooClubComment(
        clubId: String,
        categoryCode: String,
        postId: String,
        composeComment: ComposeComment
    ) = safeApiCall(Dispatchers.IO) {
        fantooClubService.composeFantooClubComment(clubId, categoryCode, postId, composeComment)
    }

    suspend fun composeFantooClubCommentReply(
        clubId: String,
        categoryCode: String,
        postId: String,
        replyId: String,
        composeCommentReply: ComposeCommentReply
    ) = safeApiCall(Dispatchers.IO) {
        fantooClubService.composeFantooClubCommentReply(
            clubId,
            categoryCode,
            postId,
            replyId,
            composeCommentReply
        )
    }

    suspend fun modifyFantooClubComment(
        clubId: String,
        categoryCode: String,
        postId: String,
        replyId: String,
        composeComment: ComposeComment
    ) = safeApiCall(Dispatchers.IO) {
        fantooClubService.modifyFantooClubComment(clubId, categoryCode, postId, replyId, composeComment)
    }

    suspend fun modifyFantooClubCommentReply(
        clubId: String,
        categoryCode: String,
        postId: String,
        replyId: String,
        rereplyId: String,
        composeCommentReply: ComposeCommentReply
    ) = safeApiCall(Dispatchers.IO) {
        fantooClubService.modifyFantooClubCommentReply(
            clubId,
            categoryCode,
            postId,
            replyId,
            rereplyId,
            composeCommentReply
        )
    }

    suspend fun deleteFantooClubComment(
        clubId: String,
        categoryCode: String,
        postId: String,
        replyId: String,
        integUid: IntegUid
    ) = safeApiCall(Dispatchers.IO) {
        fantooClubService.deleteFantooClubComment(clubId, categoryCode, postId, replyId, integUid)
    }

    suspend fun deleteFantooClubCommentReply(
        clubId: String,
        categoryCode: String,
        postId: String,
        replyId: String,
        rereplyId: String,
        integUid: IntegUid
    ) = safeApiCall(Dispatchers.IO) {
        fantooClubService.deleteFantooClubCommentReply(
            clubId,
            categoryCode,
            postId,
            replyId,
            rereplyId,
            integUid
        )
    }

    suspend fun reportFantooClubComment(
        clubId: String,
        categoryCode: String,
        postId: String,
        replyId: String,
        reportMessage: ReportMessage
    ) = safeApiCall(Dispatchers.IO) {
        fantooClubService.reportFantooClubComment(clubId, categoryCode, postId, replyId, reportMessage)
    }

    suspend fun reportFantooClubCommentReply(
        clubId: String,
        categoryCode: String,
        postId: String,
        replyId: String,
        rereplyId: String,
        reportMessage: ReportMessage
    ) = safeApiCall(Dispatchers.IO) {
        fantooClubService.reportFantooClubCommentReply(
            clubId,
            categoryCode,
            postId,
            replyId,
            rereplyId,
            reportMessage
        )
    }

    suspend fun blockFantooClubComment(
        clubId: String,
        categoryCode: String,
        postId: String,
        replyId: String,
        integUid: IntegUid
    ) = safeApiCall(Dispatchers.IO) {
        fantooClubService.blockFantooClubComment(clubId, categoryCode, postId, replyId, integUid)
    }

    suspend fun blockFantooClubCommentReply(
        clubId: String,
        categoryCode: String,
        postId: String,
        replyId: String,
        rereplyId: String,
        integUid: IntegUid
    ) = safeApiCall(Dispatchers.IO) {
        fantooClubService.blockFantooClubCommentReply(
            clubId,
            categoryCode,
            postId,
            replyId,
            rereplyId,
            integUid
        )
    }

    fun searchFantooClubPosts(
        clubId: String,
        keyword: String
    ): Flow<PagingData<FantooClubPost>> {
        return Pager(
            config = PagingConfig(enablePlaceholders = false, pageSize = NETWORK_PAGE_SIZE),
            pagingSourceFactory = { FantooClubPostSearchPagingSource(fantooClubService, clubId, keyword) }
        ).flow
    }

    suspend fun fetchReportMessages(): ResultWrapper<ReportMessageResponse> = safeApiCall(Dispatchers.IO) {
        fantooClubService.fetchReportMessages()
    }


    fun getFantooClubInfo(clubId: Long): FantooClub? {
        return getDummyClubInfo(context, getClubNameById(clubId))
    }

    fun getFantooClubCategory(clubId: Long): List<Category>? {
        return getDummyCategory(context, getClubNameById(clubId))
    }

    fun getFantooClubContents(clubId: Long): List<Contents>? {
        return getDummyContents(context, getClubNameById(clubId))
    }

    fun getFantooClubSelectedContents(clubId: Long): List<Contents>? {
        return getDummySelectedContents(context, getClubNameById(clubId))
    }

    fun getSearchResultContents(clubId: Long): List<Contents>? {
        return getDummySelectedContents(context, getClubNameById(clubId))
    }

    fun getComments(contentsId: Long): List<Comments>? {
        Timber.d("contentsId: $contentsId")
        return if(contentsId.toInt() %2 ==0) {
            getDummyComments(context, "fantooTv")
        } else {
            null
        }
    }

    private fun getDummyClubInfo(context: Context, fileName: String) : FantooClub? {
        val file = "$fileName.json"
        try {
            context.assets.open(file).use { inputStream ->
                JsonReader(inputStream.reader()).use { jsonReader ->
                    val type = object : TypeToken<FantooClub>() {}.type
                    return Gson().fromJson(jsonReader, type)
                }
            }
        } catch (ex: Exception) {
            Timber.e("Error : $ex")
            return null
        }
    }

    private fun getDummyCategory(context: Context, fileName: String) : List<Category>? {
        val file = "${fileName}Category.json"
        try {
            context.assets.open(file).use { inputStream ->
                JsonReader(inputStream.reader()).use { jsonReader ->
                    val categories = object : TypeToken<List<Category>>() {}.type
                    return Gson().fromJson(jsonReader, categories)
                }
            }
        } catch (ex: Exception) {
            Timber.e("Error : $ex")
            return null
        }
    }

    private fun getDummyContents(context: Context, fileName: String) : List<Contents>? {
        val file = "${fileName}Contents.json"
        try {
            context.assets.open(file).use { inputStream ->
                JsonReader(inputStream.reader()).use { jsonReader ->
                    val contents = object : TypeToken<List<Contents>>() {}.type
                    return Gson().fromJson(jsonReader, contents)
                }
            }
        } catch (ex: Exception) {
            Timber.e("Error : $ex")
            return null
        }
    }

    private fun getDummySelectedContents(context: Context, fileName: String) : List<Contents>? {
        val file = "${fileName}SelectedContents.json"
        try {
            context.assets.open(file).use { inputStream ->
                JsonReader(inputStream.reader()).use { jsonReader ->
                    val contents = object : TypeToken<List<Contents>>() {}.type
                    return Gson().fromJson(jsonReader, contents)
                }
            }
        } catch (ex: Exception) {
            Timber.e("Error : $ex")
            return null
        }
    }

    private fun getDummyComments(context: Context, fileName: String) : List<Comments>? {
        val file = "${fileName}Comments.json"
        try {
            context.assets.open(file).use { inputStream ->
                JsonReader(inputStream.reader()).use { jsonReader ->
                    val comments = object : TypeToken<List<Comments>>() {}.type
                    return Gson().fromJson(jsonReader, comments)
                }
            }
        } catch (ex: Exception) {
            Timber.e("Error : $ex")
            return null
        }
    }

    private fun getClubNameById(clubId: Long): String {
        return when(clubId) {
            0L -> {
                // Fantoo TV
                "fantooTv"
            }
            else -> {
                // Hanryu Times
                "hanryuTimes"
            }
        }
    }

    companion object {
        const val CATEGORY_HOME = "home"
        private const val NETWORK_PAGE_SIZE = 20
    }
}