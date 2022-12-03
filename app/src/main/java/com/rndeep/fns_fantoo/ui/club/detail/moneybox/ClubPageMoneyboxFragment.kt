package com.rndeep.fns_fantoo.ui.club.detail.moneybox

import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.rndeep.fns_fantoo.databinding.FragmentClubPageMoneyboxBinding
import com.rndeep.fns_fantoo.data.remote.model.MoneyBoxItem
import com.rndeep.fns_fantoo.ui.common.BaseFragment
import com.rndeep.fns_fantoo.utils.ConstVariable
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ClubPageMoneyboxFragment : BaseFragment<FragmentClubPageMoneyboxBinding>(FragmentClubPageMoneyboxBinding::inflate) {

    private val moneyBoxAdapter = MoneyBoxAdapter()
    private val viewModel : ClubPageMoneyboxViewModel by viewModels()

    override fun initUi() {
        binding.rcMoneyBoxList.layoutManager=LinearLayoutManager(context)
        binding.rcMoneyBoxList.adapter=moneyBoxAdapter
        settingObserve()
        callValue()
    }

    override fun initUiActionEvent() {
        moneyBoxAdapter.setMoneyboxItem(listOf(
            MoneyBoxItem(ConstVariable.MONEYBOXINFO),
            MoneyBoxItem(ConstVariable.MONTHLYRANKING),
            MoneyBoxItem(ConstVariable.WITHDRAWLIST),
        ))
    }

    private fun callValue(){
        viewModel.getMoneyBoxInfo()
        viewModel.getMoneyBoxRanking()
        viewModel.getMoneyBoxWithDrawItem()
    }

    private fun settingObserve(){
        viewModel.moneyBoxInfoLiveData.observe(this){
            moneyBoxAdapter.setMoneyboxInfo(it)
        }

        viewModel.moneyBoxRankLiveData.observe(this){
            moneyBoxAdapter.setMoneyBoxRanking(it)
        }

        viewModel.moneyBoxWithdrawLiveData.observe(this){
            moneyBoxAdapter.setMoneyBoxWithDrawItem(it)
        }
    }
}