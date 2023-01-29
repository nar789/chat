package com.rndeep.fns_fantoo.data.remote

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException

abstract class BaseNetRepo {

    suspend fun <T> safeApiCall(
        dispatcher: CoroutineDispatcher,
        apiCall: suspend () -> T
    ): ResultWrapper<T> {
        return withContext(dispatcher) {
            try {
                ResultWrapper.Success(apiCall.invoke())
            } catch (throwable: Throwable) {
                when (throwable) {
                    is IOException -> ResultWrapper.NetworkError
                    is HttpException -> {
                        val httpCode = throwable.code().toString()
                        val errorBody = throwable.response()?.errorBody()?.string()
                        Timber.d("http error: $httpCode , body: ${errorBody.toString()}")
                        try {
                            val gsonErrorBody = Gson().fromJson(
                                errorBody,
                                ErrorBody::class.java
                            )
                            val code = gsonErrorBody.code
                            val message = gsonErrorBody.msg
                            val dataObj = gsonErrorBody.dataObj
                            ResultWrapper.GenericError(code, message, dataObj)
                        }catch (e : JsonSyntaxException){
                            //통신 실팩 결과값이 ErrorBody::class 형태로 안내려와 sync 에러가 발생할 경우 예외처리
                            ResultWrapper.GenericError(null, null, null)
                        }
                    }
                    else -> {
                        Timber.e("throwable: $throwable")
                        ResultWrapper.GenericError(null, throwable.message, null)
                    }
                }
            }
        }
    }
}
