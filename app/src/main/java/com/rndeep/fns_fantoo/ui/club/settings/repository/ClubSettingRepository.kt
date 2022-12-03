package com.rndeep.fns_fantoo.ui.club.settings.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.rndeep.fns_fantoo.data.local.dao.CountryDao
import com.rndeep.fns_fantoo.data.local.dao.LanguageDao
import com.rndeep.fns_fantoo.data.local.model.Country
import com.rndeep.fns_fantoo.data.remote.BaseNetRepo
import com.rndeep.fns_fantoo.data.remote.api.ClubService
import com.rndeep.fns_fantoo.data.remote.dto.ClubMemberInfoWithMeta
import com.rndeep.fns_fantoo.data.remote.dto.ClubStoragePostListWithMeta
import com.rndeep.fns_fantoo.data.remote.dto.ClubStorageReplyListWithMeta
import com.rndeep.fns_fantoo.data.remote.dto.ClubWithDrawMemberInfoWithMeta
import com.rndeep.fns_fantoo.data.remote.model.clubsetting.ClubJoinWaitMemberWithMeta
import com.rndeep.fns_fantoo.di.NetworkModule
import com.rndeep.fns_fantoo.utils.ConstVariable.KEY_UID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ClubSettingRepository @Inject constructor(
    @NetworkModule.ApiServer private val clubService: ClubService,
    private val langDao: LanguageDao,
    private val countryDao: CountryDao
) :
    BaseNetRepo() {

    suspend fun loginClub(clubId: String, requestData: HashMap<String, Any>) =
        safeApiCall(Dispatchers.IO) {
            clubService.loginClub(clubId = clubId, body = requestData)
        }

    suspend fun getClubCloseStateInfo(clubId: String, uid: String) = safeApiCall(Dispatchers.IO) {
        clubService.getClubCloseStateInfo(clubId, uid)
    }

    suspend fun requestClubDelegate(
        clubId: String,
        memberId: String,
        requestData: HashMap<String, Any>
    ) = safeApiCall(Dispatchers.IO) {
        clubService.requestClubDelegate(clubId, memberId, requestData)
    }

    suspend fun cancelClubDelegating(
        clubId: String,
        memberId: String,
        requestData: HashMap<String, Any>
    ) = safeApiCall(Dispatchers.IO) {
        clubService.cancelClubDelegating(clubId, memberId, requestData)
    }

    suspend fun getClubDelegatingInfo(clubId: String, uid: String) = safeApiCall(Dispatchers.IO) {
        clubService.getClubDelegatingInfo(clubId, uid)
    }

    suspend fun requestMemberBan(
        clubId: String,
        memberId: String,
        requestData: HashMap<String, Any>
    ) = safeApiCall(Dispatchers.IO) {
        clubService.requestMemberBan(clubId, memberId, requestData)
    }

    suspend fun getClubBasicInfo(clubId: String, uid: String) = safeApiCall(Dispatchers.IO) {
        clubService.getClubBasicInfo(clubId, uid)
    }

    suspend fun getClubAllInfo(clubId: String, uid: String) = safeApiCall(Dispatchers.IO) {
        clubService.getClubAllInfo(clubId, uid)
    }

    suspend fun setClubAllInfo(clubId: String, requestData: HashMap<String, Any>) =
        safeApiCall(Dispatchers.IO) {
            clubService.setClubAllInfo(clubId, requestData)
        }

    suspend fun getClubManageInfo(clubId: String) = safeApiCall(Dispatchers.IO) {
        clubService.getClubManageInfo(clubId)
    }

    suspend fun checkClubName(clubName: String) = safeApiCall(Dispatchers.IO) {
        clubService.checkClubName(clubName)
    }

    suspend fun getClubMemberInfo(
        clubId: String,
        memberId: String,
        uid: String
    ) = safeApiCall(
        Dispatchers.IO
    ) { clubService.getClubMemberInfo(clubId, memberId, clubId, uid, memberId) }

    suspend fun getClubStorageCount(clubId: String, uid: String) = safeApiCall(Dispatchers.IO) {
        clubService.getClubStorageCount(clubId, uid)
    }

    suspend fun setClubAlarm(requestData: HashMap<String, String>, clubId: String) =
        safeApiCall(Dispatchers.IO) {
            clubService.setClubAlarm(requestData, clubId)
        }

    suspend fun getAlarmState(clubId: String, uid: String) = safeApiCall(Dispatchers.IO) {
        clubService.getAlarmState(clubId, uid)
    }

    suspend fun checkClubNickName(clubId: String, nickName: String) = safeApiCall(Dispatchers.IO) {
        clubService.checkClubMemberNickname(clubId, nickName)
    }

    suspend fun modifyMyProfileOfClub(clubId: String, requestData: HashMap<String, String>) =
        safeApiCall(Dispatchers.IO) {
            clubService.modifyMyProfileOfClub(clubId, requestData)
        }

    suspend fun joinClubApprove(clubId: String, requestData: HashMap<String, Any>) =
        safeApiCall(Dispatchers.IO) {
            clubService.joinClubApprove(clubId, requestData)
        }

    suspend fun joinClubReject(clubId: String, requestData: HashMap<String, Any>) =
        safeApiCall(Dispatchers.IO) {
            clubService.joinClubReject(clubId, requestData)
        }

    suspend fun leaveClub(clubId: String, uid: String) = safeApiCall(Dispatchers.IO) {
        clubService.leaveClub(clubId, hashMapOf(KEY_UID to uid))
    }

    suspend fun getClubInterestCategory() = safeApiCall(Dispatchers.IO) {
        clubService.getClubInterestCategory()
    }

    suspend fun getHashTagList(clubId: String, uid: String) = safeApiCall(Dispatchers.IO) {
        clubService.getHashTagList(clubId, uid)
    }

    suspend fun saveHashTagList(clubId: String, requestData: HashMap<String, Any>) =
        safeApiCall(Dispatchers.IO) {
            clubService.setHashTagList(clubId, requestData)
        }

    suspend fun requestClubClose(clubId: String, requestData: HashMap<String, Any>) =
        safeApiCall(Dispatchers.IO) {
            clubService.requestClubClose(clubId, requestData)
        }

    suspend fun cancelClubClose(clubId: String, uid: String, requestData: HashMap<String, Any>) =
        safeApiCall(Dispatchers.IO) {
            clubService.cancelClubClose(clubId, uid, requestData)
        }

    suspend fun getMemberBlockInfo(clubId: String, uid: String, memberId: String) =
        safeApiCall(Dispatchers.IO) {
            clubService.getMemberBlockInfo(clubId = clubId, memberId = memberId, integUid = uid)
        }

    suspend fun setMemberBlock(
        clubId: String,
        memberId: String,
        requestData: HashMap<String, String>
    ) = safeApiCall(Dispatchers.IO) {
        clubService.setMemberBlock(clubId, memberId, requestData)
    }

    suspend fun setPostBlock(clubId: String, categoryCode: String, postId: String, requestData: HashMap<String, Any>) = safeApiCall(Dispatchers.IO){
        clubService.setPostBlock(
            clubId = clubId,
            categoryCode = categoryCode,
            postId = postId,
            requestData = requestData
        )
    }

    suspend fun setCommentBlock(
        clubId: String, categoryCode: String, postId: String, replyId: String,
        requestData: HashMap<String, Any>
    ) = safeApiCall(Dispatchers.IO) {
        clubService.setCommentBlock(
            clubId = clubId,
            categoryCode = categoryCode,
            postId = postId,
            replyId = replyId,
            requestData
        )
    }

    suspend fun setSubCommentBlock(
        clubId: String,
        categoryCode: String,
        postId: String,
        parentReplyId: String,
        replyId: String,
        requestData: HashMap<String, Any>
    ) = safeApiCall(Dispatchers.IO) {
        clubService.setSubCommentBlock(
            clubId = clubId,
            categoryCode = categoryCode,
            postId = postId,
            parentReplyId = parentReplyId,
            replyId = replyId,
            requestData
        )
    }

    suspend fun updateWithDrawMember(
        clubId: String,
        withdrawId: String,
        requestData: HashMap<String, Any>
    ) = safeApiCall(Dispatchers.IO) {
        clubService.updateWithdrawMember(clubId, withdrawId, requestData)
    }

    suspend fun getSettingCategoryList(clubId: String, uid: String) = safeApiCall(Dispatchers.IO) {
        clubService.getClubSettingCategoryList(clubId, uid)
    }

    suspend fun setCategoryListOrder(
        clubId: String,
        categoryCode: String,
        requestData: HashMap<String, Any>
    ) = safeApiCall(Dispatchers.IO) {
        clubService.setCategoryListOrder(clubId, categoryCode, requestData)
    }


    suspend fun modifySettingCategory(
        clubId: String,
        categoryCode: String,
        requestData: HashMap<String, Any>
    ) = safeApiCall(Dispatchers.IO) {
        clubService.modifySettingCategory(
            clubId = clubId, categoryCode = categoryCode,
            requestData = requestData
        )
    }

    suspend fun deleteSettingCategory(
        clubId: String,
        categoryCode: String,
        requestData: HashMap<String, Any>
    ) = safeApiCall(Dispatchers.IO) {
        clubService.deleteSettingCategory(clubId, categoryCode, requestData)
    }

    suspend fun createSettingCategory(
        clubId: String,
        categoryCode: String,
        requestData: HashMap<String, Any>
    ) = safeApiCall(Dispatchers.IO) {
        clubService.createSettingCategory(clubId, categoryCode, requestData)
    }

    suspend fun saveFreeboardOpenWord(
        clubId: String,
        categoryCode: String,
        requestData: HashMap<String, Any>
    ) = safeApiCall(Dispatchers.IO) {
        clubService.saveFreeboardOpenWord(
            clubId = clubId,
            categoryCode = categoryCode,
            requestData = requestData
        )
    }

    suspend fun getMemberCount(clubId: String, uid: String) = safeApiCall(Dispatchers.IO) {
        clubService.getMemberCount(clubId, uid)
    }

    suspend fun getJoinWaitMemberCount(clubId: String, uid: String) = safeApiCall(Dispatchers.IO) {
        clubService.getJoinWaitMemberCount(clubId, uid)
    }

    suspend fun getWithDrawMemberCount(clubId: String, uid: String) = safeApiCall(Dispatchers.IO) {
        clubService.getWithDrawMemberCount(clubId, uid)
    }

    fun getClubMemberList(clubId: String, uid: String): Flow<PagingData<ClubMemberInfoWithMeta>> {
        return Pager(
            config = PagingConfig(pageSize = 3, enablePlaceholders = false),
            pagingSourceFactory = { ClubMembersPagingSource(clubService, clubId, uid, "") },
            initialKey = 0
        ).flow
    }

    fun getSearchMemberList(
        clubId: String,
        uid: String,
        keyword: String
    ): Flow<PagingData<ClubMemberInfoWithMeta>> {
        return Pager(
            config = PagingConfig(pageSize = 3, enablePlaceholders = false),
            pagingSourceFactory = { ClubMembersPagingSource(clubService, clubId, uid, keyword) },
            initialKey = 0
        ).flow
    }

    fun getJoinWaitMemberList(
        clubId: String,
        uid: String
    ): Flow<PagingData<ClubJoinWaitMemberWithMeta>> {
        return Pager(
            config = PagingConfig(pageSize = 3, enablePlaceholders = false),
            pagingSourceFactory = { ClubJoinWaitMemberPagingSource(clubService, clubId, uid) },
            initialKey = 0
        ).flow
    }

    fun getWithDrawMemberList(
        clubId: String,
        uid: String
    ): Flow<PagingData<ClubWithDrawMemberInfoWithMeta>> {
        return Pager(
            config = PagingConfig(pageSize = 3, enablePlaceholders = false),
            pagingSourceFactory = { ClubBanMemberPagingSource(clubService, clubId, uid) },
            initialKey = 0
        ).flow
    }

    fun getStoragePostList(
        clubId: String,
        uid: String,
        memberId: String
    ): Flow<PagingData<ClubStoragePostListWithMeta>> {
        return Pager(
            config = PagingConfig(pageSize = 3, enablePlaceholders = false),
            pagingSourceFactory = {
                ClubStoragePostPagingSource(
                    clubService,
                    clubId,
                    uid,
                    memberId
                )
            },
            initialKey = 0
        ).flow
    }

    fun getStorageReplyList(
        clubId: String,
        uid: String,
        memberId: String
    ): Flow<PagingData<ClubStorageReplyListWithMeta>> {
        return Pager(
            config = PagingConfig(pageSize = 3, enablePlaceholders = false),
            pagingSourceFactory = {
                ClubStorageReplyPagingSource(
                    clubService,
                    clubId,
                    uid,
                    memberId
                )
            },
            initialKey = 0
        ).flow
    }

    fun getStorageBookmarkList(
        clubId: String,
        uid: String,
        memberId: String
    ): Flow<PagingData<ClubStoragePostListWithMeta>> {
        return Pager(
            config = PagingConfig(pageSize = 3, enablePlaceholders = false),
            pagingSourceFactory = {
                ClubStorageBookmarkPagingSource(
                    clubService,
                    clubId,
                    uid,
                    memberId
                )
            },
            initialKey = 0
        ).flow
    }

    suspend fun getLanguage(langCode: String): com.rndeep.fns_fantoo.data.local.model.Language? {
        return langDao.getLanguage(langCode)
    }

    suspend fun getCountryByIso2(iso2: String): Country? {
        return countryDao.getCountryByIso2(iso2)
    }

}