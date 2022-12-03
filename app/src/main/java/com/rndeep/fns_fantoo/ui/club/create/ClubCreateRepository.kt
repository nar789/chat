package com.rndeep.fns_fantoo.ui.club.create

import com.rndeep.fns_fantoo.data.remote.BaseNetRepo
import com.rndeep.fns_fantoo.data.remote.ErrorBody
import com.rndeep.fns_fantoo.data.remote.ErrorData
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.api.ClubService
import com.rndeep.fns_fantoo.data.remote.model.ClubNameCheck
import com.rndeep.fns_fantoo.data.remote.model.club.ClubInterestItem
import com.rndeep.fns_fantoo.di.NetworkModule
import com.rndeep.fns_fantoo.utils.ConstVariable
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class ClubCreateRepository @Inject constructor(
    @NetworkModule.ApiServer private val clubService : ClubService
) :BaseNetRepo(){

    suspend fun checkDuplicateClubName(clubName : String) : Pair<ClubNameCheck?,ErrorBody?> {
        safeApiCall(Dispatchers.IO){clubService.checkClubName(clubName)}.run {
            return when(this){
                is ResultWrapper.Success->{
                    Pair(this.data,null)
                }
                is ResultWrapper.GenericError ->{
                    Pair(null,ErrorBody(this.code,this.message,this.errorData))
                }
                is ResultWrapper.NetworkError -> {
                    Pair(null,ErrorBody(ConstVariable.ERROR_WAIT_FOR_SECOND,null,null))
                }
            }
        }
    }

    suspend fun fetchClubCreateCategory():Pair<List<ClubInterestItem>?,ErrorBody?>{
        safeApiCall(Dispatchers.IO){clubService.getClubCreateInterestList()}.run {
            var errorCode: String? =null
            var errorMsg: String? =null
            var errorData: ErrorData? =null
            var interestItemList : List<ClubInterestItem> = listOf()
            when(this){
                is ResultWrapper.Success->{
                    interestItemList=this.data.clubInterestCategoryDtoList
                }
                is ResultWrapper.GenericError->{
                    errorCode = this.code
                    errorMsg = this.message
                    errorData =this.errorData
                }
                is ResultWrapper.NetworkError->{
                    errorCode = ConstVariable.ERROR_NETWORK
                }
            }
            return if(errorCode==null){
                Pair(interestItemList,null)
            }else{
                Pair(null,ErrorBody(errorCode,errorMsg,errorData))
            }
        }
    }

    suspend fun requestCreateClub(
        activeCountry: String,
        bannerImageId: String?,
        checkToken: String,
        clubName: String,
        uId :String,
        interestCategory :Int,
        langCode:String,
        openMemberJoinYn :Boolean,
        clubOpenYn :Boolean,
        profileImageId:String?
    ) :String?{
        safeApiCall(Dispatchers.IO){clubService.createClub(HashMap<String,Any?>().apply {
            this["activeCountryCode"] = activeCountry
            this["bgImg"] = bannerImageId
            this["checkToken"] = checkToken
            this["clubName"] = clubName
            this["integUid"] = uId
            this["interestCategoryId"] = interestCategory
            this["languageCode"] = langCode
            this["memberJoinAutoYn"] = openMemberJoinYn
            this["openYn"] = clubOpenYn
            this["profileImg"] = profileImageId
        })}.run {
            return when(this){
                is ResultWrapper.Success->{
                    this.data.clubId
                }
                is ResultWrapper.GenericError->{
                    null
                }
                is ResultWrapper.NetworkError->{
                    null
                }
            }
        }
    }

}