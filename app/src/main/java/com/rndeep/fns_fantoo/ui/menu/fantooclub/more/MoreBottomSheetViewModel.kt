package com.rndeep.fns_fantoo.ui.menu.fantooclub.more

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
class MoreBottomSheetViewModel @Inject constructor(
) : ViewModel() {

    private val _moreMenuItems = MutableLiveData<List<MoreMenuItem>>()
    val moreMenuItems: LiveData<List<MoreMenuItem>> = _moreMenuItems

    private val _checkPeekHeight = MutableLiveData<Unit>()
    val checkPeekHeight: LiveData<Unit> = _checkPeekHeight

    fun initMoreMenuItems(items: List<MoreMenuItem>) {
        _moreMenuItems.value = items
    }

    fun updateMoreMenuItem(item: MoreMenuItem) {
        Timber.d("updateMoreMenuItem : $item")
        val menuItems = _moreMenuItems.value
        menuItems?.let{ list ->
            list.forEach { menuItem ->
                menuItem.selected = menuItem.title == item.title
            }
            _moreMenuItems.value = list
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