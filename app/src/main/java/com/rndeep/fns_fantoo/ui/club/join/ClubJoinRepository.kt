package com.rndeep.fns_fantoo.ui.club.join

import com.rndeep.fns_fantoo.data.remote.BaseNetRepo
import com.rndeep.fns_fantoo.data.remote.ErrorBody
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.api.ClubService
import com.rndeep.fns_fantoo.di.NetworkModule
import com.rndeep.fns_fantoo.utils.ConstVariable
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class ClubJoinRepository @Inject constructor(
    @NetworkModule.ApiServer private val clubService: ClubService
) : BaseNetRepo() {


    suspend fun checkClubExists(clubId:String,uId:String):String?{
        safeApiCall(Dispatchers.IO){clubService.getClubBasicInfo(clubId,uId)}.run {
            return when(this){
                is ResultWrapper.Success->{
                    this.data.clubName
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

    suspend fun callRequestClubJoin(
        clubId: String,
        myUid: String,
        clubNickName: String,
        accessToken: String,
        checkToken : String,
        profileImgId : String?
    ): Pair<Boolean?,ErrorBody?> {
        val bodyMap: HashMap<String, Any> = HashMap<String, Any>()
        bodyMap["checkToken"] = checkToken
        bodyMap["integUid"] = myUid
        bodyMap["nickname"] = clubNickName
        bodyMap["profileImg"] = profileImgId?:""

        safeApiCall(Dispatchers.IO) {
            clubService.requestClubJoin(
                accessToken,
                clubId,
                bodyMap
            )
        }.run {
            when (this) {
                is ResultWrapper.Success -> {
                   return Pair(this.data.memberJoinAutoYn,null)
                }
                is ResultWrapper.GenericError->{
                    return Pair(null, ErrorBody(this.code,this.message,this.errorData))
                }
                is ResultWrapper.NetworkError->{
                    return Pair(null,ErrorBody(ConstVariable.ERROR_NETWORK,null,null))
                }
            }
        }
    }

    // true : 중복 ( 사용 불가) , false : 미중복 (사용 가능)
    suspend fun checkNicknameDuplicate(clubId: String, nickName: String): Pair<Boolean, String> {
        val checkRes = safeApiCall(Dispatchers.IO) {
            clubService.checkDuplicateNickName(
                clubId,
                nickName
            )
        }
        when (checkRes) {
            is ResultWrapper.Success -> {
                return Pair(checkRes.data.isCheck, checkRes.data.checkToken)
            }
            else -> return Pair(true, "")
        }
    }

}