package com.rndeep.fns_fantoo.ui.home.alram

import com.rndeep.fns_fantoo.data.remote.BaseNetRepo
import com.rndeep.fns_fantoo.data.remote.ErrorBody
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.api.HomeService
import com.rndeep.fns_fantoo.data.remote.model.HomeAlarmData
import com.rndeep.fns_fantoo.di.NetworkModule
import com.rndeep.fns_fantoo.ui.common.TempData
import com.rndeep.fns_fantoo.utils.ConstVariable
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class HomeAlarmRepository @Inject constructor(
     @NetworkModule.ApiServer private val homeService: HomeService,
) : BaseNetRepo() {

    //Alarm ?
    suspend fun getAlarmData(uId :String,nextId :String?,size:String):Triple<List<HomeAlarmData>?,String,ErrorBody?>{
        safeApiCall(Dispatchers.IO){
            homeService.getAlarmList(uId,nextId,size)
        }.run {
            when(this){
                is ResultWrapper.Success->{
                   return Triple(this.data.alimList,this.data.nextId.toString(),null)
                }
                is ResultWrapper.GenericError->{
                    return Triple(null,"-1",ErrorBody(this.code,this.message,this.errorData))
                }
                is ResultWrapper.NetworkError -> {
                    return Triple(null,"-1",ErrorBody(ConstVariable.ERROR_NETWORK,null,null))
                }
            }
        }
    }
}