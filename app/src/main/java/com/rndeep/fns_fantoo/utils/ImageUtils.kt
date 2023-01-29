package com.rndeep.fns_fantoo.utils

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.webkit.MimeTypeMap
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.bumptech.glide.request.transition.Transition
import timber.log.Timber
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ImageUtils {

    companion object{
        lateinit var currentPhotoPath: String
        @Throws(IOException::class)
        fun createImageFile(context: Context): File? {
            // Create an image file name
            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            return File.createTempFile(
                "JPEG_${timeStamp}_", /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
            ).apply {
                // Save a file: path for use with ACTION_VIEW intents
                currentPhotoPath = absolutePath
            }
        }

        /*fun getRandomProfileCharacterResourceId(context: Context):Int{
            val resouceName = "profile_character"+((Math.random()*5)+1).toInt()//1~5
            return context.resources.getIdentifier(resouceName, "drawable",
                context.packageName)
        }*/

        fun getImageSizeFromUri(context: Context, uri: Uri):Int{
            var fileSize = -1
            if(uri.scheme.equals(ContentResolver.SCHEME_CONTENT)){
                try{
                    val fileInputStream = context?.contentResolver?.openInputStream(uri)
                    if (fileInputStream != null) {
                        //Timber.d("takePhotoFromAlbumLauncher fileSize = ${fileInputStream.available()}")
                        fileSize = fileInputStream.available()
                    }
                }catch(e:Exception){
                    Timber.e("take photo, file size check err:"+e.message)
                }
            }
            return fileSize
        }

        fun getMimeTypeFromUri(context: Context, uri: Uri):String?{
            var type:String? = null
            if (ContentResolver.SCHEME_CONTENT == uri.scheme) {
                type = context.contentResolver.getType(uri)
            }else{
                val fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
                if(fileExtension != null){
                    type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension)
                }
            }
            return type
        }

        fun getImageFullUriForCloudFlare(imageUriKey:String, isThumbnail:Boolean):String{
            return if(isThumbnail){
                ConstVariable.CloudFlare.IMAGE_PRE_URL+imageUriKey+ConstVariable.CloudFlare.IMAGE_OPT_THUMBNAIL
            }else{
                ConstVariable.CloudFlare.IMAGE_PRE_URL+imageUriKey+ConstVariable.CloudFlare.IMAGE_OPT_PUBLIC
            }
        }

    }
}