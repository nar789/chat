package com.rndeep.fns_fantoo.ui.chatting.imagepicker

import android.content.ContentResolver
import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImagePickerViewModel @Inject constructor(private val repository: ImagePickerRepository) : ViewModel() {

    lateinit var imageList: Flow<PagingData<Uri>>

    private val _checkedImages = mutableStateListOf<Uri>()
    val checkedImages: List<Uri> get() = _checkedImages

    fun sendImages() {

    }

    fun selectImage(imageItem: Uri) {
        if (checkedImages.contains(imageItem)) {
            _checkedImages.remove(imageItem)
        } else {
            _checkedImages.add(imageItem)
        }
    }

    fun loadImageList(contentResolver: ContentResolver) {
        imageList = repository.loadImages(contentResolver)
    }
}