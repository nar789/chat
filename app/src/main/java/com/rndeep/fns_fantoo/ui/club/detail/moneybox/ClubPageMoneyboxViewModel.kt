package com.rndeep.fns_fantoo.ui.club.detail.moneybox

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rndeep.fns_fantoo.data.remote.model.MoneyBoxRanking
import com.rndeep.fns_fantoo.data.remote.model.MoneyBoxTopInfo
import com.rndeep.fns_fantoo.data.remote.model.MoneyBoxWithDrawItem
import com.rndeep.fns_fantoo.ui.club.detail.ClubPageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClubPageMoneyboxViewModel @Inject constructor(
    private val repository : ClubPageRepository
) :ViewModel(){

    val moneyBoxInfoLiveData = MutableLiveData<MoneyBoxTopInfo>()
    fun getMoneyBoxInfo(){
        viewModelScope.launch {
            moneyBoxInfoLiveData.value=repository.callOfMoneyBoxInfo()
        }
    }

    //랭킹 정보 가져오기
    val moneyBoxRankLiveData = MutableLiveData<List<MoneyBoxRanking>>()
    fun getMoneyBoxRanking(){
        viewModelScope.launch {
            moneyBoxRankLiveData.value=repository.callOfMoneyBoxRank()
        }
    }

    //입출금 내역
    val moneyBoxWithdrawLiveData = MutableLiveData<MoneyBoxWithDrawItem>()
    fun getMoneyBoxWithDrawItem(){
        viewModelScope.launch {
            moneyBoxWithdrawLiveData.value=repository.callOfMoneyBoxWithdraw()
        }
    }

}