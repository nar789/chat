package com.rndeep.fns_fantoo.repositories

import com.rndeep.fns_fantoo.data.remote.BaseNetRepo
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.api.ClubService
import com.rndeep.fns_fantoo.data.remote.api.CommunityService
import com.rndeep.fns_fantoo.data.remote.dto.ClubStoragePostListDto
import com.rndeep.fns_fantoo.data.remote.dto.ClubStorageReplyListDto
import com.rndeep.fns_fantoo.data.remote.dto.CommunityMyPostResponse
import com.rndeep.fns_fantoo.data.remote.dto.CommunityMyReplyResponse
import com.rndeep.fns_fantoo.di.NetworkModule
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class MyLibraryRepository @Inject constructor(
    @NetworkModule.ApiServer private val communityService: CommunityService,
    @NetworkModule.ApiServer private val clubService: ClubService
) : BaseNetRepo() {

    // Community
    suspend fun fetchCommunityMyPosts(
        accessToken: String,
        integUid: String,
        nextId: Int,
        size: Int
    ): ResultWrapper<CommunityMyPostResponse> = safeApiCall(Dispatchers.IO) {
        communityService.fetchCommunityMyPosts(accessToken, integUid, nextId, size)
    }

    suspend fun fetchCommunityMyComments(
        accessToken: String,
        integUid: String,
        nextId: Int,
        size: Int
    ): ResultWrapper<CommunityMyReplyResponse> = safeApiCall(Dispatchers.IO) {
        communityService.fetchCommunityMyComments(accessToken, integUid, nextId, size)
    }

    suspend fun fetchCommunityMyBookmarks(
        accessToken: String,
        integUid: String,
        nextId: Int,
        size: Int
    ): ResultWrapper<CommunityMyPostResponse> = safeApiCall(Dispatchers.IO) {
        communityService.fetchCommunityMyBookmarks(accessToken, integUid, nextId, size)
    }

    // Club
    suspend fun fetchClubMyPosts(
        accessToken: String,
        integUid: String,
        nextId: String,
        size: String
    ): ResultWrapper<ClubStoragePostListDto> = safeApiCall(Dispatchers.IO) {
        clubService.fetchClubMyPosts(accessToken, integUid, nextId, size)
    }

    suspend fun fetchClubMyComments(
        accessToken: String,
        integUid: String,
        nextId: String,
        size: String
    ): ResultWrapper<ClubStorageReplyListDto> = safeApiCall(Dispatchers.IO) {
        clubService.fetchClubMyComments(accessToken, integUid, nextId, size)
    }

    suspend fun fetchClubMyBookmarks(
        accessToken: String,
        integUid: String,
        nextId: String,
        size: String
    ): ResultWrapper<ClubStoragePostListDto> = safeApiCall(Dispatchers.IO) {
        clubService.fetchClubMyBookmarks(accessToken, integUid, nextId, size)
    }
}