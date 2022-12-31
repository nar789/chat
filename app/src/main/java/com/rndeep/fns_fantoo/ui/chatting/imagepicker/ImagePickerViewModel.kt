package com.rndeep.fns_fantoo.ui.chatting.imagepicker

import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.rndeep.fns_fantoo.repositories.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class ImagePickerViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val repository: ImagePickerRepository) : ViewModel() {

    lateinit var imageList: Flow<PagingData<Uri>>

    private val _checkedImages = mutableStateListOf<Uri>()
    val checkedImages: List<Uri> get() = _checkedImages

    fun sendImages() {
        checkedImages.forEach { chatRepository.uploadImage(it) }
    }

    fun selectImage(imageItem: Uri) {
        if (checkedImages.contains(imageItem)) {
            _checkedImages.remove(imageItem)
        } else {
            _checkedImages.add(imageItem)
        }
    }

    fun loadImageList() {
        imageList = repository.loadImages()
    }
}