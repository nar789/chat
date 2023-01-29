package com.rndeep.fns_fantoo.ui.chatting.imagepicker

import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.rndeep.fns_fantoo.repositories.ChatRepository
import com.rndeep.fns_fantoo.ui.common.viewmodel.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImagePickerViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val repository: ImagePickerRepository) : ViewModel() {

    lateinit var imageList: Flow<PagingData<Uri>>

    private val _checkedImages = mutableStateListOf<Uri>()
    val checkedImages: List<Uri> get() = _checkedImages

    private val _showErrorToast = SingleLiveEvent<String>()
    val showErrorToast: LiveData<String> = _showErrorToast

    init {
        viewModelScope.launch {
            chatRepository.showErrorToast.filterNotNull().collect {
                _showErrorToast.value = it
            }
        }
    }

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