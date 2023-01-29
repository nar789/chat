package com.rndeep.fns_fantoo.ui.editor

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class EditorBottomSheetViewModel @Inject constructor(

) : ViewModel() {

    private val _bottomSheetBoardItems = MutableLiveData<List<BoardItem>>()
    val bottomSheetBoardItems: LiveData<List<BoardItem>> = _bottomSheetBoardItems

    private val _checkPeekHeight = MutableLiveData<Unit>()
    val checkPeekHeight: LiveData<Unit> = _checkPeekHeight

    fun initBoardItem(items: List<BoardItem>) {
        _bottomSheetBoardItems.value = items
    }

    fun updateBoardItem(item: BoardItem) {
        Timber.d("updateBoardItem : $item")
        val boardItems = _bottomSheetBoardItems.value
        boardItems?.let{ list ->
            list.forEach { boardItem ->
                boardItem.selected = boardItem.name == item.name
            }
            _bottomSheetBoardItems.value = list
        }
    }

    fun updateCheckPeekHeight() {
        Timber.d("updateCheckPeekHeight")
        viewModelScope.launch {
            delay(250)
            _checkPeekHeight.postValue(Unit)
        }
    }
}