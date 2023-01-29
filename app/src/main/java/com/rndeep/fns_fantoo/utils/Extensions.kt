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
import android.util.TypedValue
import android.view.*
import android.view.ViewTreeObserver.OnGlobalLayoutListener
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

fun Fragment.getErrorMessage(code: String): String {
    return when(code) {
        "FE1000" -> getString(R.string.Error_FE1000)
        "FE1001" -> getString(R.string.Error_FE1001)
        "FE1002" -> getString(R.string.Error_FE1002)
        "FE1003" -> getString(R.string.Error_FE1003)
        "FE1004" -> getString(R.string.Error_FE1004)
        "FE1005" -> getString(R.string.Error_FE1005)
        "FE1006" -> getString(R.string.Error_FE1006)
        "FE1007" -> getString(R.string.Error_FE1007)
        "FE1008" -> getString(R.string.Error_FE1008)
        "FE1009" -> getString(R.string.Error_FE1009)
        "FE1010" -> getString(R.string.Error_FE1010)
        "FE1011" -> getString(R.string.Error_FE1011)
        "FE1012" -> getString(R.string.Error_FE1012)
        "FE1013" -> getString(R.string.Error_FE1013)
        "FE1014" -> getString(R.string.Error_FE1014)
        "FE1015" -> getString(R.string.Error_FE1015)
        "FE1016" -> getString(R.string.Error_FE1016)
        "FE1017" -> getString(R.string.Error_FE1017)
        "FE1018" -> getString(R.string.Error_FE1018)
        "FE1019" -> getString(R.string.Error_FE1019)
        "FE1020" -> getString(R.string.Error_FE1020)
        "FE1021" -> getString(R.string.Error_FE1021)
        "FE1022" -> getString(R.string.Error_FE1022)
        "FE1023" -> getString(R.string.Error_FE1023)
        "FE1024" -> getString(R.string.Error_FE1024)
        "FE1025" -> getString(R.string.Error_FE1025)
        "FE1026" -> getString(R.string.Error_FE1026)
        "FE1027" -> getString(R.string.Error_FE1027)
        "FE1028" -> getString(R.string.Error_FE1028)
        "FE1029" -> getString(R.string.Error_FE1029)
        "FE1030" -> getString(R.string.Error_FE1030)
        "FE1031" -> getString(R.string.Error_FE1031)
        "FE1032" -> getString(R.string.Error_FE1032)
        "FE1033" -> getString(R.string.Error_FE1033)
        "FE1034" -> getString(R.string.Error_FE1034)
        //
        "FE2000" -> getString(R.string.Error_FE2000)
        "FE2001" -> getString(R.string.Error_FE2001)
        "FE2002" -> getString(R.string.Error_FE2002)
        "FE2003" -> getString(R.string.Error_FE2003)
        "FE2004" -> getString(R.string.Error_FE2004)
        "FE2005" -> getString(R.string.Error_FE2005)
        "FE2006" -> getString(R.string.Error_FE2006)
        "FE2007" -> getString(R.string.Error_FE2007)
        "FE2008" -> getString(R.string.Error_FE2008)
        "FE2009" -> getString(R.string.Error_FE2009)
        "FE2010" -> getString(R.string.Error_FE2010)
        "FE2011" -> getString(R.string.Error_FE2011)
        "FE2012" -> getString(R.string.Error_FE2012)
        "FE2013" -> getString(R.string.Error_FE2013)
        "FE2014" -> getString(R.string.Error_FE2014)
        "FE2015" -> getString(R.string.Error_FE2015)
        "FE2016" -> getString(R.string.Error_FE2016)
        "FE2017" -> getString(R.string.Error_FE2017)
        "FE2018" -> getString(R.string.Error_FE2018)
        "FE2019" -> getString(R.string.Error_FE2019)
        "FE2020" -> getString(R.string.Error_FE2020)
        "FE2021" -> getString(R.string.Error_FE2021)
        "FE2022" -> getString(R.string.Error_FE2022)
        "FE2023" -> getString(R.string.Error_FE2023)
        "FE2025" -> getString(R.string.Error_FE2025)
        "FE2824" -> getString(R.string.Error_FE2824)
        "FE2825" -> getString(R.string.Error_FE2825)
        "FE2826" -> getString(R.string.Error_FE2826)
        "FE2827" -> getString(R.string.Error_FE2827)
        "FE2828" -> getString(R.string.Error_FE2828)
        "FE2829" -> getString(R.string.Error_FE2829)
        "FE2830" -> getString(R.string.Error_FE2830)
        "FE2831" -> getString(R.string.Error_FE2831)
        "FE2832" -> getString(R.string.Error_FE2832)
        "FE2833" -> getString(R.string.Error_FE2833)
        "FE2834" -> getString(R.string.Error_FE2834)
        "FE2835" -> getString(R.string.Error_FE2835)
        "FE2836" -> getString(R.string.Error_FE2836)
        "FE2837" -> getString(R.string.Error_FE2837)
        "FE2838" -> getString(R.string.Error_FE2838)
        "FE2839" -> getString(R.string.Error_FE2839)
        "FE2840" -> getString(R.string.Error_FE2840)
        //
        "FE3000" -> getString(R.string.Error_FE3000)
        "FE3001" -> getString(R.string.Error_FE3001)
        "FE3002" -> getString(R.string.Error_FE3002)
        "FE3003" -> getString(R.string.Error_FE3003)
        "FE3004" -> getString(R.string.Error_FE3004)
        "FE3005" -> getString(R.string.Error_FE3005)
        "FE3006" -> getString(R.string.Error_FE3006)
        "FE3007" -> getString(R.string.Error_FE3007)
        "FE3008" -> getString(R.string.Error_FE3008)
        "FE3009" -> getString(R.string.Error_FE3009)
        "FE3010" -> getString(R.string.Error_FE3010)
        "FE3011" -> getString(R.string.Error_FE3011)
        "FE3012" -> getString(R.string.Error_FE3012)
        "FE3013" -> getString(R.string.Error_FE3013)
        "FE3014" -> getString(R.string.Error_FE3014)
        "FE3015" -> getString(R.string.Error_FE3015)
        "FE3016" -> getString(R.string.Error_FE3016)
        "FE3017" -> getString(R.string.Error_FE3017)
        "FE3018" -> getString(R.string.Error_FE3018)
        "FE3019" -> getString(R.string.Error_FE3019)
        "FE3020" -> getString(R.string.Error_FE3020)
        "FE3021" -> getString(R.string.Error_FE3021)
        "FE3022" -> getString(R.string.Error_FE3022)
        "FE3023" -> getString(R.string.Error_FE3023)
        "FE3024" -> getString(R.string.Error_FE3024)
        "FE3025" -> getString(R.string.Error_FE3025)
        "FE3026" -> getString(R.string.Error_FE3026)
        "FE3027" -> getString(R.string.Error_FE3027)
        "FE3028" -> getString(R.string.Error_FE3028)
        "FE3029" -> getString(R.string.Error_FE3029)
        "FE3030" -> getString(R.string.Error_FE3030)
        "FE3031" -> getString(R.string.Error_FE3031)
        "FE3032" -> getString(R.string.Error_FE3032)
        "FE3033" -> getString(R.string.Error_FE3033)
        "FE3034" -> getString(R.string.Error_FE3034)
        "FE3035" -> getString(R.string.Error_FE3035)
        "FE3036" -> getString(R.string.Error_FE3036)
        "FE3037" -> getString(R.string.Error_FE3037)
        "FE3038" -> getString(R.string.Error_FE3038)
        "FE3039" -> getString(R.string.Error_FE3039)
        "FE3040" -> getString(R.string.Error_FE3040)
        "FE3041" -> getString(R.string.Error_FE3041)
        "FE3042" -> getString(R.string.Error_FE3042)
        "FE3043" -> getString(R.string.Error_FE3043)
        "FE3044" -> getString(R.string.Error_FE3044)
        "FE3045" -> getString(R.string.Error_FE3045)
        "FE3046" -> getString(R.string.Error_FE3046)
        "FE3047" -> getString(R.string.Error_FE3047)
        "FE3049" -> getString(R.string.Error_FE3049)
        "FE3050" -> getString(R.string.Error_FE3050)
        "FE3051" -> getString(R.string.Error_FE3051)
        //
        "FE3100" -> getString(R.string.Error_FE3100)
        "FE3101" -> getString(R.string.Error_FE3101)
        "FE3102" -> getString(R.string.Error_FE3102)
        "FE3103" -> getString(R.string.Error_FE3103)
        "FE3104" -> getString(R.string.Error_FE3104)
        //
        "FE3200" -> getString(R.string.Error_FE3200)
        //
        "AE5000" -> getString(R.string.Error_AE5000)
        "AE5001" -> getString(R.string.Error_AE5001)
        "AE5002" -> getString(R.string.Error_AE5002)
        "AE5003" -> getString(R.string.Error_AE5003)
        "AE5004" -> getString(R.string.Error_AE5004)
        "AE5005" -> getString(R.string.Error_AE5005)
        "AE5006" -> getString(R.string.Error_AE5006)
        "FE5007" -> getString(R.string.Error_FE5007)
        "FE5008" -> getString(R.string.Error_FE5008)
        "FE5009" -> getString(R.string.Error_FE5009)
        "FE5010" -> getString(R.string.Error_FE5010)
        "FE5100" -> getString(R.string.Error_FE5100)
        "FE5101" -> getString(R.string.Error_FE5101)
        else -> code
    }
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

fun FragmentActivity.addOnGlobalLayoutListener(globalLayoutListener: OnGlobalLayoutListener){
    val parentView = (findViewById<ViewGroup>(android.R.id.content)).getChildAt(0)
    parentView.viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener)
}

fun FragmentActivity.removeOnGlobalLayoutListener(globalLayoutListener: OnGlobalLayoutListener){
    val parentView = (findViewById<ViewGroup>(android.R.id.content)).getChildAt(0)
    parentView.viewTreeObserver.removeOnGlobalLayoutListener(globalLayoutListener)
}

fun FragmentActivity.isKeyboardShown(): Boolean {
    val view = (findViewById<ViewGroup>(android.R.id.content)).getChildAt(0)
    val defaultKeyboardHeightDP = 100
    val estimatedKeyboardDP =
        defaultKeyboardHeightDP + 48
    val rect = Rect()
    val estimatedKeyboardHeight = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        estimatedKeyboardDP.toFloat(),
        view.resources.displayMetrics
    ).toInt()
    view.getWindowVisibleDisplayFrame(rect)
    val heightDiff = view.rootView.height - (rect.bottom - rect.top)
    return heightDiff >= estimatedKeyboardHeight
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

fun Context.getStringByIdentifier(name:String):String{
    try{
        return getString(
            resources.getIdentifier(
                name,
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
                if (postType == ConstVariable.TYPE_CLUB) {
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
                } else {
                    BottomSheetItem(
                        R.drawable.icon_outline_siren,
                        getString(R.string.s_to_report),
                        null,
                        null
                    )
                },

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