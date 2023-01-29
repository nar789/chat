package com.rndeep.fns_fantoo.repositories

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.rndeep.fns_fantoo.data.remote.BaseNetRepo
import com.rndeep.fns_fantoo.data.remote.ErrorBody
import com.rndeep.fns_fantoo.data.remote.ResultWrapper
import com.rndeep.fns_fantoo.data.remote.api.TranslateService
import com.rndeep.fns_fantoo.data.remote.model.trans.TransMessage
import com.rndeep.fns_fantoo.di.NetworkModule
import com.rndeep.fns_fantoo.utils.ConstVariable
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class TranslateRepository @Inject constructor(
    @NetworkModule.TranslateServer private val translateService: TranslateService
) :BaseNetRepo(){

    suspend fun translateWords(transMessage:List<String>,transLang :String):Pair<List<TransMessage>?,ErrorBody?>{
        val bodyObject : JsonObject = JsonObject()
        val langArray : JsonArray = JsonArray()
        langArray.add(transLang)
        val messages : JsonArray =JsonArray()
        transMessage.forEach {
            val messageBody =JsonObject().apply {
                addProperty("origin",it)
                addProperty("text",it)
            }
            messages.add(messageBody)
        }
        bodyObject.add("language",langArray)
        bodyObject.add("messages",messages)
        safeApiCall(Dispatchers.IO){
            translateService.getTranslateWord(bodyObject)
        }.run {
            return when(this){
                is ResultWrapper.Success->{
                    if(this.data.status=="success"){
                        this.data.messages.forEach { transMessage ->
                            if(!transMessage.isTranslated){
                                return Pair(null,ErrorBody(ConstVariable.ERROR_TRANSLATE,null,null))
                            }
                        }
                        if(this.data.messages.isEmpty()){
                            Pair(null,ErrorBody(ConstVariable.ERROR_TRANSLATE,null,null))
                        }else{
                            Pair(this.data.messages,null)
                        }
                    }else{
                        Pair(null,ErrorBody(ConstVariable.ERROR_TRANSLATE,null,null))
                    }
                }
                is ResultWrapper.GenericError->{
                    Pair(null,ErrorBody(this.code,this.message,this.errorData))
                }
                is ResultWrapper.NetworkError->{
                    Pair(null,ErrorBody(ConstVariable.ERROR_NETWORK,null,null))
                }
            }
        }
    }

}