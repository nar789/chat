package com.rndeep.fns_fantoo.utils

import android.content.ContentResolver
import android.net.Uri
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rndeep.fns_fantoo.repositories.ChatRepository
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.io.InputStream


private const val RESULT_SUCCESS = "success"
private const val RESULT_FAIL = "fail"
private const val IMAGE_LIMIT = (1024 * 1024).toLong()

fun String?.isSuccess(): Boolean = this == RESULT_SUCCESS

fun <T> String.toObject(): T = Gson().fromJson(this, object : TypeToken<T>() {}.type)

inline fun <reified T> String.toObjectList(): List<T> {
    val list: ArrayList<Map<String, Any?>>? = this.toObject()
    return mutableListOf<T>().apply {
        list?.forEach {
            add(mapToObject(it, T::class.java) ?: return@forEach)
        }
    }
}

fun <T> mapToObject(map: Map<String, Any?>?, type: Class<T>): T? {
    if (map == null) return null

    val gson = Gson()
    val json = gson.toJson(map)
    return gson.fromJson(json, type)
}

//convert a data class to a map
private fun <T> T.serializeToMap(): Map<String, String> {
    return convert()
}

//convert a map to a data class
private inline fun <reified T> Map<String, Any>.toDataClass(): T {
    return convert()
}

//convert an object of type I to type O
private inline fun <I, reified O> I.convert(): O {
    val gson = Gson()
    val json = gson.toJson(this)
    return gson.fromJson(json, object : TypeToken<O>() {}.type)
}

fun Uri.convertFileToByteArray(contentResolver: ContentResolver, limit: Long = IMAGE_LIMIT): ByteArray? {
    try {
        val descriptor = contentResolver.openFileDescriptor(this, "r")
        val inputStream: InputStream =
            FileInputStream(descriptor?.fileDescriptor)
        val fileSize = inputStream.available()
        if (fileSize > limit) {
            return null
        }

        val bytes: ByteArray
        val buffer = ByteArray(8192)
        var bytesRead: Int
        val output = ByteArrayOutputStream()
        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
            output.write(buffer, 0, bytesRead)
        }
        bytes = output.toByteArray()
        descriptor?.close()
        inputStream.close()
        return bytes
    } catch (e: Exception) {
        Timber.e("convertBase64Data error: ${e.message}", e)
        e.printStackTrace()
    }
    return null
}
