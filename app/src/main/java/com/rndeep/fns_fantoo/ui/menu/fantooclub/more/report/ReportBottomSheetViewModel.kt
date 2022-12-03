package com.rndeep.fns_fantoo.ui.menu.fantooclub.more.report

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
class ReportBottomSheetViewModel @Inject constructor(
) : ViewModel() {

    private val _reportMessageItems = MutableLiveData<List<ReportMessageItem>>()
    val reportMessageItems: LiveData<List<ReportMessageItem>> = _reportMessageItems

    private val _checkPeekHeight = MutableLiveData<Unit>()
    val checkPeekHeight: LiveData<Unit> = _checkPeekHeight

    fun initReportMessageItems(items: List<ReportMessageItem>) {
        _reportMessageItems.value = items
    }

    fun updateReportMessageItems(item: ReportMessageItem) {
        Timber.d("updateReportMessageItems : $item")
        val menuItems = _reportMessageItems.value
        menuItems?.let { list ->
            list.forEach { menuItem ->
                menuItem.selected = menuItem.id == item.id
            }
            _reportMessageItems.value = list
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