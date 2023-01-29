package com.rndeep.fns_fantoo.ui.club.search

import com.rndeep.fns_fantoo.data.remote.BaseNetRepo
import com.rndeep.fns_fantoo.data.remote.ErrorBody
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.api.ClubService
import com.rndeep.fns_fantoo.data.remote.model.RecommendationClub
import com.rndeep.fns_fantoo.data.remote.model.club.ClubSearchItem
import com.rndeep.fns_fantoo.di.NetworkModule
import com.rndeep.fns_fantoo.utils.ConstVariable
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class ClubSearchRepository @Inject constructor(
    @NetworkModule.ApiServer private val clubService: ClubService
) :BaseNetRepo() {

    suspend fun getClubList(uId :String):List<ClubSearchItem>{
        safeApiCall(Dispatchers.IO){
            if(uId.isEmpty()){
                clubService.fetchGuestPopularRecommendClub100()
            }else {
                clubService.fetchPopularRecommendClub100(uId)
            }
        }.run {
            when (this) {
                is ResultWrapper.Success -> {
                    return this.data.clubList
                }
                is ResultWrapper.GenericError -> {

                }
                is ResultWrapper.NetworkError -> {

                }
            }
        }
        return listOf()
    }

    suspend fun getSearchResultClub(
        searchWord: String,
        nextId: Int?,
        size: Int
    ): Triple<List<ClubSearchItem>,Int?,ErrorBody?> {
        safeApiCall(Dispatchers.IO){clubService.fetchSearchClub(
            searchWord,
            nextId,
            size
        )}.run {
            return when(this){
                is ResultWrapper.Success->{
                    Triple(this.data.clubList,this.data.nextId,null)
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