package com.rndeep.fns_fantoo.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.LeadingMarginSpan
import android.util.DisplayMetrics
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import androidx.core.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.RequestOptions.bitmapTransform
import com.google.android.material.snackbar.Snackbar
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.CustomUiToastmessageBinding
import com.rndeep.fns_fantoo.ui.common.custombottomsheet.BottomSheetAdapter
import com.rndeep.fns_fantoo.ui.common.custombottomsheet.BottomSheetItem
import com.rndeep.fns_fantoo.ui.common.custombottomsheet.CustomBottomSheet
import jp.wasabeef.glide.transformations.MaskTransformation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.BufferedSink
import okio.source
import timber.log.Timber
import java.util.regex.Pattern


// Fragment
fun Fragment.setProfileAvatar(
    imageView: ImageView,
    imageUri: String?,
    @DrawableRes placeHolderId: Int? =null
) {
    // Inflate the drawable for proper tinting
    val placeholder = placeHolderId ?: R.drawable.profile_club_character
    val placeholderDrawable = AppCompatResources.getDrawable(imageView.context, placeholder)
    when (imageUri) {
        null -> {
            Glide.with(imageView.context)
                .load(placeholderDrawable)
                .into(imageView)
        }
        else -> {
            Glide.with(imageView.context)
                .load(imageUri)
                .error(placeholderDrawable)
                .apply(bitmapTransform(MultiTransformation(CenterCrop(),
                    MaskTransformation(R.drawable.bg_etc_profiles)
                )))
                .into(imageView)
        }
    }
}

fun Fragment.getImageUrlFromCDN(url: String?): String? = when (url) {
    null -> null
    else -> getString(R.string.imageUrlBase, url)
}

fun Fragment.getThumbnailImageUrlFromCDN(url: String?): String? = when (url) {
    null -> null
    else -> getString(R.string.imageThumbnailUrl, url)
}

fun Fragment.getVideoUrlFromCDN(url: String?): String? = when (url) {
    null -> null
    else -> getString(R.string.videoUrlHLSBase, url)
}

fun Fragment.setImageWithPlaceHolder(
    imageView: ImageView,
    imageUri: String?,
    @DrawableRes maskingId : Int? =null,
    @DrawableRes placeHolderId :Int? =null
){
    val placeholder :Drawable? = if(placeHolderId!=null){
        AppCompatResources.getDrawable(imageView.context,placeHolderId)
    }else{
        AppCompatResources.getDrawable(imageView.context,R.drawable.club_no_image)
    }
    var maskingOption =RequestOptions()
    if(maskingId!=null)  maskingOption = bitmapTransform(MultiTransformation(CenterCrop(),
        MaskTransformation(maskingId)
    ))
    when (imageUri) {
        null -> {
            Glide.with(imageView.context)
                .load(placeholder)
                .apply(maskingOption)
                .into(imageView)
        }
        else -> {
            Glide.with(imageView.context)
                .load(imageUri)
                .apply(maskingOption)
                .error(placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView)
        }
    }

}

fun Fragment.setGifImage(imageView: ImageView, resourceId:Int){
    Glide.with(imageView.context).asGif().load(resourceId).into(imageView)
}

fun Activity.setGifImage(imageView: ImageView, resourceId:Int){
    Glide.with(imageView.context).asGif().load(resourceId).into(imageView)
}

fun Fragment.showKeyboard(view: View) {
    ViewCompat.getWindowInsetsController(view)?.show(WindowInsetsCompat.Type.ime())
}

fun Fragment.dismissKeyboard(view: View) {
    ViewCompat.getWindowInsetsController(view)?.hide(WindowInsetsCompat.Type.ime())
}

inline fun Fragment.launchAndRepeatWithViewLifecycle(
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    crossinline block: suspend CoroutineScope.() -> Unit
) {
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.lifecycle.repeatOnLifecycle(minActiveState) {
            block()
        }
    }
}

fun Fragment.getYoutubeId(url: String): String {
    val pattern = Pattern.compile(
        "https?:\\/\\/(?:[0-9A-Z-]+\\.)?(?:youtu\\.be\\/|youtube\\.com\\S*[^\\w\\-\\s])([\\w\\-]{11})(?=[^\\w\\-]|$)(?![?=&+%\\w]*(?:['\"][^<>]*>|<\\/a>))[?=&+%\\w]*",
        Pattern.CASE_INSENSITIVE)

    var id = ""
    val matcher = pattern.matcher(url ?: "")
    if (matcher.matches()) {
        id = matcher.group(1) ?: ""
    }
    return id
}

fun Fragment.startShare(title: String, share: String) {
    Timber.d("start share")
    ShareCompat.IntentBuilder(requireContext())
        .setType("text/plain")
        .setChooserTitle(title)
        .setText(share)
        .startChooser()
}

fun Fragment.setStatusBar(@ColorRes color: Int, isLight: Boolean) {
    val window = requireActivity().window
    val decorView = window.decorView
    val windowInsetsControllerCompat = WindowInsetsControllerCompat(window, decorView)
    window.statusBarColor = requireContext().getColor(color)
    windowInsetsControllerCompat.isAppearanceLightStatusBars = isLight
    WindowCompat.setDecorFitsSystemWindows(window, true)
}

// RecyclerView
fun RecyclerView.addItemDecorationWithoutFirstDivider() {

    if (layoutManager !is LinearLayoutManager)
        return

    addItemDecoration(object :
        DividerItemDecoration(context, (layoutManager as LinearLayoutManager).orientation) {

        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            super.getItemOffsets(outRect, view, parent, state)

            val position = parent.getChildAdapterPosition(view)
            Timber.d("addItemDecorationWithoutFirstDivider, position: $position")
            if (position == 0) {
//                outRect.set(0, 0, 0, 0)
                ContextCompat.getDrawable(
                    context,
                    R.drawable.divider_light_gray50
                )?.let {
                    setDrawable(
                        it
                    )
                }
            } else {
                ContextCompat.getDrawable(
                    context,
                    R.drawable.divider_library
                )?.let {
                    setDrawable(
                        it
                    )
                }
            }
        }
    })
}

fun RecyclerView.addLibraryItemDecorationDivider() {

    if (layoutManager !is LinearLayoutManager)
        return

    addItemDecoration(object :
        DividerItemDecoration(context, (layoutManager as LinearLayoutManager).orientation) {

        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)

            ContextCompat.getDrawable(
                context,
                R.drawable.divider_library
            )?.let {
                setDrawable(
                    it
                )
            }
        }
    })
}

fun RecyclerView.addSingleItemDecoRation(vararg itemDecorations :RecyclerView.ItemDecoration){
    val itemDecoCount = itemDecorationCount
    if(itemDecoCount>0){
        for (a in 0 until itemDecoCount){
            for(itemDeco in itemDecorations){
                if(getItemDecorationAt(a)==itemDeco){
                    removeItemDecorationAt(a)
                }
            }
        }
    }
    for(a in itemDecorations){
        addItemDecoration(a)
    }
}

fun RecyclerView.clearDecorations() {
    if (itemDecorationCount > 0) {
        for (i in itemDecorationCount - 1 downTo 0) {
            removeItemDecorationAt(i)
        }
    }
}

fun RecyclerView.anchorSmoothScrollToPosition(position: Int, anchorPosition: Int = 3) {
    layoutManager?.apply {
        when (this) {
            is LinearLayoutManager -> {
                val topItem = findFirstVisibleItemPosition()
                val distance = topItem - position
                val anchorItem = when {
                    distance > anchorPosition -> position + anchorPosition
                    distance < -anchorPosition -> position - anchorPosition
                    else -> topItem
                }
                if (anchorItem != topItem) {
                    smoothScrollToPosition(anchorItem)
                    scrollToPosition(anchorItem)
                }
                post {
                    smoothScrollToPosition(position)
                }
            }
            else -> smoothScrollToPosition(position)
        }
    }
}

fun RecyclerView.checkLastItemVisible(itemClick :(isLast :Boolean)-> Unit){
    this.clearOnScrollListeners()
    this.addOnScrollListener(object :RecyclerView.OnScrollListener(){
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val lastItemPosition =(recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
            val itemTotalCount = recyclerView.adapter?.itemCount?.minus(1)
            itemClick(lastItemPosition==itemTotalCount)
        }
    })
}



// RecyclerView.ViewHolder
fun RecyclerView.ViewHolder.setProfileAvatar(
imageView: ImageView,
imageUri: String?,
@DrawableRes placeHolderId: Int? =null
) {

    val placeholder = placeHolderId ?: R.drawable.profile_club_character
    val placeholderDrawable = AppCompatResources.getDrawable(imageView.context, placeholder)
    when (imageUri) {
        null -> {
            Glide.with(imageView.context)
                .load(placeholderDrawable)
                .apply(bitmapTransform(MultiTransformation(CenterCrop(),
                    MaskTransformation(R.drawable.bg_etc_profiles)
                )))
                .into(imageView)
        }
        else -> {
            Glide.with(imageView.context)
                .load(imageUri)
                .error(placeholderDrawable)
                .apply(bitmapTransform(MultiTransformation(CenterCrop(),
                    MaskTransformation(R.drawable.bg_etc_profiles)
                )))
                .into(imageView)
        }
    }
}


fun RecyclerView.ViewHolder.setImageWithPlaceHolder(
    imageView: ImageView,
    imageUri: String?,
    @DrawableRes maskingId : Int? =null,
    @DrawableRes placeHolderId :Int? =null
){
    val placeholder :Drawable? = if(placeHolderId!=null){
        AppCompatResources.getDrawable(itemView.context,placeHolderId)
    }else{
        AppCompatResources.getDrawable(itemView.context,R.drawable.club_no_image)
    }
    var maskingOption =RequestOptions()
    if(maskingId!=null)  maskingOption = bitmapTransform(MultiTransformation(CenterCrop(),
            MaskTransformation(maskingId)
        ))
    when (imageUri) {
        null -> {
            Glide.with(imageView.context)
                .load(placeholder)
                .apply(maskingOption)
                .error(placeholder)
                .into(imageView)
        }
        else -> {
            Glide.with(imageView.context)
                .load(imageUri)
                .apply(maskingOption)
                .error(placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView)
        }
    }
}

fun RecyclerView.ViewHolder.getYoutubeThumbnail(url: String): String {
    val pattern = Pattern.compile(
        "https?:\\/\\/(?:[0-9A-Z-]+\\.)?(?:youtu\\.be\\/|youtube\\.com\\S*[^\\w\\-\\s])([\\w\\-]{11})(?=[^\\w\\-]|$)(?![?=&+%\\w]*(?:['\"][^<>]*>|<\\/a>))[?=&+%\\w]*",
        Pattern.CASE_INSENSITIVE)

    var id = ""
    val matcher = pattern.matcher(url ?: "")
    if (matcher.matches()) {
        id = matcher.group(1) ?: ""
    }
    return "https://img.youtube.com/vi/$id/hqdefault.jpg"
}

fun RecyclerView.ViewHolder.getImageUrlFromCDN(context: Context, url: String?): String? = when (url) {
    null -> null
    else -> context.getString(R.string.imageUrlBase, url)
}

fun RecyclerView.ViewHolder.getVideoThumbnailFromCDN(context: Context, url: String?): String? = when (url) {
    null -> null
    else -> context.getString(R.string.videoThumbnail, url)
}

//context
fun Context.statusBarHeight(): Int {
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")

    return if (resourceId > 0) resources.getDimensionPixelSize(resourceId)
    else 0
}

fun Context.navigationHeight(): Int {
    val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")

    return if (resourceId > 0) resources.getDimensionPixelSize(resourceId)
    else 0
}


//FragmentActivity
fun FragmentActivity.setStatusBarTransparent() {
    window.apply {
        statusBarColor = Color.TRANSPARENT
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {    // API 30 에 적용
        WindowCompat.setDecorFitsSystemWindows(window, false)
    } else {
        window.apply {
            setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }
    }
}

fun FragmentActivity.setStatusBarBack() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {    // API 30 에 적용
        WindowCompat.setDecorFitsSystemWindows(window, true)
    } else {
        window.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }
    }
}
fun FragmentActivity.hasSoftKeys(): Boolean {
    val display = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        display
    } else {
        windowManager.defaultDisplay
    } ?: return true

    val realDisplayMetrics = DisplayMetrics()
    display.getRealMetrics(realDisplayMetrics)

    val realHeight = realDisplayMetrics.heightPixels
    val realWidth = realDisplayMetrics.widthPixels

    val displayMetrics = DisplayMetrics()
    display.getMetrics(displayMetrics)

    val displayHeight = displayMetrics.heightPixels
    val displayWidth = displayMetrics.widthPixels

    return realWidth - displayWidth > 0 || realHeight - displayHeight > 0
}

fun FragmentActivity.setDarkStatusBarIcon(){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        window.insetsController?.setSystemBarsAppearance(
            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
        )
    } else {
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            } else {
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
    }
}

fun FragmentActivity.setWhiteStatusBarIcon(){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        window.insetsController?.setSystemBarsAppearance(0, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS)
    } else {
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility =0
    }
}

fun Context.showCustomSnackBar(snackBarView: View, message :String):Snackbar {

    val snackbar =  Snackbar.make(snackBarView,"",Snackbar.LENGTH_SHORT)
    val snackBarLayout = snackbar.view as Snackbar.SnackbarLayout

    snackBarLayout.setPadding(0,0,0, resources.getDimension(R.dimen.snackbar_margin_bottom).toInt())

    val inflaterView =LayoutInflater.from(snackBarView.context).inflate(R.layout.custom_ui_toastmessage,null,false)
    snackbar.view.setBackgroundColor(Color.TRANSPARENT)
    val binding : CustomUiToastmessageBinding = CustomUiToastmessageBinding.bind(inflaterView)
    binding.root.setOnClickListener {
        if(snackbar.isShown) snackbar.dismiss()
    }
    binding.tvToastMessage.text=message
    snackBarLayout.addView(inflaterView,0)

    snackbar.show()
    return snackbar
}

fun Context.showErrorSnackBar(rootView: View,errorCode :String){
    try {
        var errorMessage = ""
        when(errorCode){
        ConstVariable.ERROR_NOT_MEMBER->{
            errorMessage=getString(R.string.error_code_2000)
        }
        ConstVariable.ERROR_WAIT_FOR_SECOND ->{
            errorMessage=getString(R.string.error_code_2001)
        }
        ConstVariable.ERROR_PLEASE_LATER_LOGIN ->{
            errorMessage=getString(R.string.error_code_2002)
        }
        ConstVariable.ERROR_NO_CATEGORY_CLUB->{
            errorMessage=getString(R.string.error_code_cl500)
        }
        ConstVariable.ERROR_IMAGE_UPLOAD_FAIL->{
            errorMessage=getString(R.string.error_code_im4004)
        }
        ConstVariable.ERROR_FE3045->{
            errorMessage=getString(R.string.error_code_fe3045)
        }
        ConstVariable.ERROR_FE3005->{
            errorMessage=getString(R.string.Error_FE3005)
        }
        ConstVariable.ERROR_FE2025->{
            errorMessage=getString(R.string.Error_FE2025)
        }
        else ->{
            errorMessage= getString(
                resources.getIdentifier(
                    "Error_" + errorCode,
                    "string",
                    rootView.context.packageName
                )
            )
        }
    }

        showCustomSnackBar(rootView, errorMessage)
    }catch (e:Exception){
        showCustomSnackBar(rootView, errorCode)
        Timber.e("${e.printStackTrace()}")
    }
}

fun Context.getErrorString(errorCode :String):String{
    try {
        return getString(
            resources.getIdentifier(
                "Error_" + errorCode,
                "string",
                this.packageName
            )
        )
    }catch (e:Exception){
        Timber.e("${e.printStackTrace()}")
    }
    return ""
}

@SuppressLint("Range")
fun Uri.asMultipart(name: String,fileName : String?, contentResolver: ContentResolver): MultipartBody.Part? {
    return contentResolver.query(this, null, null, null, null)?.let {
        if (it.moveToNext()) {
            val displayName = fileName?:it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME));
            val requestBody = object : RequestBody() {
                override fun contentType(): MediaType? {
                    return contentResolver.getType(this@asMultipart)?.toMediaType()
                }

                override fun writeTo(sink: BufferedSink) {
                    sink.writeAll(contentResolver.openInputStream(this@asMultipart)?.source()!!)
                }
            }
            it.close()
            MultipartBody.Part.createFormData(name, displayName, requestBody)
        } else {
            it.close()
            null
        }
    }
}

/**
 * db_pkId : DB 검색를 위한 변수
 * type : Home / Popular 확인용 변수
 */
fun FragmentActivity.showBottomSheetOfPost(
    isMyPost: Boolean,
    isUser: Boolean,
    postType:String,
    clickListener: BottomSheetAdapter.OnItemClickListener,
    isPieceBlind: Boolean?,
    isUserBlind: Boolean?
) {
    val bottomSheet = CustomBottomSheet()
    bottomSheet.setBottomItems(
        if(!isUser){
            arrayListOf(
                BottomSheetItem(
                    R.drawable.icon_outline_share,
                    getString(R.string.g_to_share),
                    null,
                    null
                ),
            )
        }else if(isMyPost){
            arrayListOf(
                BottomSheetItem(
                    R.drawable.icon_outline_share,
                    getString(R.string.g_to_share),
                    null,
                    null
                ),
                BottomSheetItem(
                    R.drawable.icon_outline_join,
                    getString(R.string.s_modify),
                    null,
                    null
                ),
                BottomSheetItem(
                    R.drawable.icon_outline_siren,
                    getString(R.string.s_do_delete),
                    null,
                    null
                ),
            )
        }else{
            arrayListOf(
                BottomSheetItem(
                    R.drawable.icon_outline_save,
                    getString(R.string.j_to_save),
                    null,
                    null
                ),
                BottomSheetItem(
                    R.drawable.icon_outline_share,
                    getString(R.string.g_to_share),
                    null,
                    null
                ),
                if(postType==ConstVariable.TYPE_CLUB){
                    BottomSheetItem(
                        R.drawable.icon_outline_join,
                        getString(R.string.g_to_join),
                        null,
                        null
                    )
                    BottomSheetItem(
                        R.drawable.icon_outline_siren,
                        getString(R.string.s_to_report),
                        null,
                        null
                    )
                }else{
                    BottomSheetItem(
                        R.drawable.icon_outline_siren,
                        getString(R.string.s_to_report),
                        null,
                        null
                    )
                }
                ,

                BottomSheetItem(
                    R.drawable.icon_outline_hide,
                    getString(if (isPieceBlind == true) R.string.g_unhide_post else R.string.g_hide_post),
                    null,
                    null
                ),
                BottomSheetItem(
                    R.drawable.icon_outline_blockaccount,
                    getString(if (isUserBlind == true) R.string.a_unblock_this_user else R.string.a_block_this_user),
                    null,
                    null
                ),
            )
        }
    )
    bottomSheet.setOnCliCkListener(object : BottomSheetAdapter.OnItemClickListener {
        override fun onItemClick(name: String, pos: Int, oldPos: Int?) {
            clickListener.onItemClick(name, pos, oldPos)
            bottomSheet.dismiss()
        }
    })
    bottomSheet.show(supportFragmentManager, "Tag")
}

/**
 *  첫줄 앞단어 만큼 이하 라인 마진 정렬
 */
fun TextView.setLeftMarinSpan(leftStandardText:String, fullText:String){
    try{
        val startMargin = this.paint.measureText(leftStandardText).toInt()

        val hasNewLineChar = (fullText.indexOf("\n") != -1)
        val sb = SpannableStringBuilder()
        var len = 0
        if(hasNewLineChar){//개행문자가 포함된 경우에 leftMargin적용
            val splitted = fullText.split("\n")
            var firstParagraph = 0
            for (k in splitted.indices) {
                len = sb.length
                if(k > 0){
                    firstParagraph = startMargin
                }
                sb.append(splitted[k])
                sb.append("\n")
                sb.setSpan(
                    LeadingMarginSpan.Standard(firstParagraph, startMargin),
                    len,
                    len+1,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            this.text = sb
        }else {
            this.text = SpannableString(text).apply {
                setSpan(
                    LeadingMarginSpan.Standard(0, startMargin),
                    0,
                    length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }
    }catch (e:Exception){
        Timber.e("${e.printStackTrace()}")
    }
}

fun Float.toDp(): Float = this * Resources.getSystem().displayMetrics.density + 0.5f