package com.rndeep.fns_fantoo.ui.club.create

import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.core.view.get
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.FragmentClubCreateInterestBinding
import com.rndeep.fns_fantoo.ui.common.BaseFragment
import com.rndeep.fns_fantoo.ui.common.dialog.CommonDialog
import com.rndeep.fns_fantoo.utils.ConstVariable
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class ClubCreateInterestFragment :
    BaseFragment<FragmentClubCreateInterestBinding>(FragmentClubCreateInterestBinding::inflate) {

    private val interestListAdapter = ClubCreateInterestListAdapter()

    private var canGotoNext = false

    private val interestViewModel: ClubCreateInterestViewModel by viewModels()

    //받는 데이터
    val getItem: ClubCreateInterestFragmentArgs by navArgs()
    override fun initUi() {
        initToolbar()
        initView()
        settingObserve()
        interestViewModel.getCategoryItems()
    }

    override fun initUiActionEvent() {
        interestListAdapter.setOnInterestClickListener(object :
            ClubCreateInterestListAdapter.InterestClickListener {
            override fun interestClick(v: View, pos: Int, item: Int) {
                interestListAdapter.selectItem(pos)
                val s = SpannableString(binding.clubInterestToolbar.menu[0].title)
                s.setSpan(
                    ForegroundColorSpan(requireContext().getColor(R.color.state_active_primary_default)),
                    0,
                    s.length,
                    0
                )
                binding.clubInterestToolbar.menu[0].title = s
                canGotoNext = true
            }
        })

    }

    private fun initToolbar() {
        val s = SpannableString(binding.clubInterestToolbar.menu[0].title)
        s.setSpan(
            ForegroundColorSpan(requireContext().getColor(R.color.gray_300)),
            0,
            s.length,
            0
        )
        binding.clubInterestToolbar.menu[0].title = s

        binding.clubInterestToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.clubInterestToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_club_create_complete -> {
                    //완료
                    if (canGotoNext) {
                        interestViewModel.callCreateClub(
                            getItem.clubCreateInfo,
                            interestListAdapter.getSelectItem(),
                            requireContext()
                        )
                    }
                    true
                }
                else -> {
                    false
                }
            }
        }
    }

    private fun initView() {
        binding.loadingView.visibility=View.GONE
        binding.clubInterestRecycler.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.clubInterestRecycler.adapter = interestListAdapter
    }

    private fun settingObserve() {
        interestViewModel.categoryItemLiveData.observe(this) {
            interestListAdapter.setItems(it)
        }

        interestViewModel.loadingCreateClubLiveData.observe(this){
            if(it){
                binding.loadingView.visibility=View.VISIBLE
            }else{
                binding.loadingView.visibility=View.GONE
            }
        }
        interestViewModel.errorCodeLiveData.observe(this){
            when(it){
                ConstVariable.ERROR_IMAGE_UPLOAD_FAIL->{
                    CommonDialog.Builder().apply {
                        this.title=getString(R.string.alert_occur_error_when_image_upload)
                        this.message=getString(R.string.alert_occur_error_when_image_upload_retry)
                        this.positiveButtonString=getString(R.string.h_confirm)
                        this.positiveButtonClickListener=object : CommonDialog.ClickListener{
                            override fun onClick() {
                                findNavController().navigate(
                                    ClubCreateInterestFragmentDirections.actionClubCreateInterestFragmentToClubTabFragment()
                                )
                            }
                        }
                    }.build().show(childFragmentManager,"errorDialog")
                }
            }
        }

        interestViewModel.clubCreateResultLiveData.observe(this){
            if(it==null){
                CommonDialog.Builder().apply {
                    this.title=getString(R.string.k_club_create_fail)
                    this.message=getString(R.string.se_k_club_create_fail)
                    this.positiveButtonString=getString(R.string.h_confirm)
                    this.positiveButtonClickListener=object : CommonDialog.ClickListener{
                        override fun onClick() {
                            findNavController().navigate(
                                ClubCreateInterestFragmentDirections.actionClubCreateInterestFragmentToClubTabFragment()
                            )
                        }
                    }
                }.build().show(childFragmentManager,"failDialog")
            }else{
                CommonDialog.Builder().apply {
                    this.title=getString(R.string.k_club_create_success)
                    this.message=getString(R.string.se_k_club_create_success)
                    this.positiveButtonString=getString(R.string.a_move)
                    this.positiveButtonClickListener=object : CommonDialog.ClickListener{
                        override fun onClick() {
                            findNavController().navigate(
                                ClubCreateInterestFragmentDirections.actionClubCreateInterestFragmentToClubPageDetail(
                                    it
                                )
                            )
                        }
                    }
                    this.isCanceledOnTouchOutside=false
                }.build().show(childFragmentManager,"successDialog")
            }
        }
    }
}