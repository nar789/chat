package com.rndeep.fns_fantoo.ui.club.challenge

import com.rndeep.fns_fantoo.data.remote.BaseNetRepo
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.api.ClubService
import com.rndeep.fns_fantoo.data.remote.model.ClubChallengeItem
import com.rndeep.fns_fantoo.di.NetworkModule
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class ClubChallengeRepository @Inject constructor(
    @NetworkModule.ApiServer private val clubService: ClubService
) :BaseNetRepo(){
    suspend fun getChallengeList(uId:String,size:Int,nextId:Int?) : Pair<List<ClubChallengeItem>,Int>{
        safeApiCall(Dispatchers.IO){clubService.getChallengePost(
            uId,
            size,
            nextId
        )}.run {
            when(this){
                is ResultWrapper.Success->{
                    return Pair(this.data.postList,this.data.nextId)
                }
                else ->{
                    return Pair(listOf(),-1)
                }
            }
        }
    }

}