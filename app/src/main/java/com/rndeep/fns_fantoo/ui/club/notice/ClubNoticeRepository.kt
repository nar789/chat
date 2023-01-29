package com.rndeep.fns_fantoo.ui.club.notice

import com.rndeep.fns_fantoo.data.remote.BaseNetRepo
import com.rndeep.fns_fantoo.data.remote.ErrorBody
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.api.ClubService
import com.rndeep.fns_fantoo.data.remote.model.post.ClubPostData
import com.rndeep.fns_fantoo.di.NetworkModule
import com.rndeep.fns_fantoo.utils.ConstVariable
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class ClubNoticeRepository @Inject constructor(
    @NetworkModule.ApiServer private val clubService: ClubService,
) :BaseNetRepo(){

    suspend fun fetchClubNoticeList(
        isUser: Boolean,
        clubId: String,
        categoryCode: String,
        uId:String?,
        nextId: Int?,
        size: Int
    ): Triple<List<ClubPostData>,Int,ErrorBody?> {
        safeApiCall(Dispatchers.IO){
            if(isUser){
                clubService.getClubCategoryPost(categoryCode,clubId,uId!!,nextId?.toString(), size.toString(),"")
            }else{
                clubService.getClubCategoryPostForGuest(categoryCode,clubId,nextId?.toString(), size.toString(),"")
            }
        }.run {
            return when(this){
                is ResultWrapper.Success->{
                    Triple(this.data.postList,this.data.nextId?:-1,null)
                }
                is ResultWrapper.GenericError->{
                    Triple(listOf(),-1,ErrorBody(this.code,this.message,this.errorData))
                }
                is ResultWrapper.NetworkError->{
                    Triple(listOf(),-1,ErrorBody(ConstVariable.ERROR_NETWORK,null,null))
                }
            }
        }

    }

}