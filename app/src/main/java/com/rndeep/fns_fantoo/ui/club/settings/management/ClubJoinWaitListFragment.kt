package com.rndeep.fns_fantoo.ui.club.settings.management

import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.ErrorBody
import com.rndeep.fns_fantoo.databinding.FragmentClubSettingJoinWaitBinding
import com.rndeep.fns_fantoo.ui.common.dialog.CommonDialog
import com.rndeep.fns_fantoo.ui.club.settings.ClubSettingBaseFragment
import com.rndeep.fns_fantoo.ui.club.settings.ClubSettingItemDecoration
import com.rndeep.fns_fantoo.ui.club.settings.management.adapter.JoinRequestAdapter
import com.rndeep.fns_fantoo.ui.club.settings.management.adapter.UiDataSelectWrapper
import com.rndeep.fns_fantoo.ui.club.settings.management.viewmodel.ClubJoinWaitViewModel
import com.rndeep.fns_fantoo.utils.ConstVariable
import com.rndeep.fns_fantoo.utils.ConstVariable.KEY_UID
import com.rndeep.fns_fantoo.utils.showErrorSnackBar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.launch
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import kotlin.collections.ArrayList

class ClubJoinWaitListFragment :
    ClubSettingBaseFragment<FragmentClubSettingJoinWaitBinding>(FragmentClubSettingJoinWaitBinding::inflate) {

    private val clubJoinWaitViewModel: ClubJoinWaitViewModel by viewModels()
    private lateinit var clubId: String
    private lateinit var uid: String
    override fun initUi() {
        val args: ClubJoinWaitListFragmentArgs by navArgs()
        clubId = args.clubId
        uid = args.integUid.toString()

        val joinRequestAdapter = JoinRequestAdapter(binding.listHeader)
        joinRequestAdapter.checkBoxChangeListener =
            object : JoinRequestAdapter.CheckBoxChangeListener {
                override fun onCheckChanged(wrapperData: UiDataSelectWrapper) {}

                override fun onSelectedDataList(selectedDataArrayList: ArrayList<UiDataSelectWrapper>) {
                    selectedDataArrayList.let {
                        binding.llButtons.visibility = if (it.size > 0) View.VISIBLE else View.GONE
                        binding.listHeader.tvHeaderTitle.text =
                            if (it.size > 0) getString(R.string.s_select) else getString(R.string.j_all)
                    }
                }
            }
        joinRequestAdapter.allItemCheckedListener =
            object : JoinRequestAdapter.AllItemCheckedListener {
                override fun onAllItemChecked(isAllItemChecked: Boolean) {
                    binding.llButtons.visibility = if (isAllItemChecked) View.VISIBLE else View.GONE
                }
            }

        binding.rvUserList.adapter = joinRequestAdapter
        binding.rvUserList.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        clubJoinWaitViewModel.networkErrorLiveData.observe(viewLifecycleOwner){
            showDialog(getString(R.string.alert_network_error))
        }

        //가입승인
        clubJoinWaitViewModel.joinClubApproveResultLiveData.observe(viewLifecycleOwner) { baseResponse ->
            when (baseResponse.code) {
                ConstVariable.RESULT_SUCCESS_CODE -> {
                    joinRequestAdapter.removeCheckedItems()
                    clubJoinWaitViewModel.getJoinWaitMemberCount(clubId, uid)
                    getJoinWaitMemberList()
                }
                else ->{
                    requireContext().showErrorSnackBar(binding.root, baseResponse.code)
                }
            }
        }

        //가입거절
        clubJoinWaitViewModel.joinClubRejectResultLiveData.observe(viewLifecycleOwner) { baseResponse ->
            when (baseResponse.code) {
                ConstVariable.RESULT_SUCCESS_CODE -> {
                    joinRequestAdapter.removeCheckedItems()
                    clubJoinWaitViewModel.getJoinWaitMemberCount(clubId, uid)
                    getJoinWaitMemberList()
                }
                else ->{
                    requireContext().showErrorSnackBar(binding.root, baseResponse.code)
                }
            }
        }

        clubJoinWaitViewModel.clubJoinWaitMemberCountLiveData.observe(viewLifecycleOwner){
            clubJoinWaitMemberCount ->
            clubJoinWaitMemberCount?.let {
                binding.listHeader.tvHeaderCount.text = it.joinCount.toString()
            }
        }

        joinRequestAdapter.addLoadStateListener { loadState ->
            val errorState = when {
                loadState.append is LoadState.Error -> loadState.append as LoadState.Error
                loadState.prepend is LoadState.Error -> loadState.prepend as LoadState.Error
                loadState.refresh is LoadState.Error -> loadState.refresh as LoadState.Error
                else -> null
            }
            when (val throwable = errorState?.error) {
                is IOException -> {}
                is HttpException -> {
                    val errorBody = throwable.response()?.errorBody()?.string()
                    try {
                        val gsonErrorBody = Gson().fromJson(
                            errorBody,
                            ErrorBody::class.java
                        )
                        val code = gsonErrorBody.code
                        val message = gsonErrorBody.msg
                        binding.listEmpty.root.visibility = View.VISIBLE
                        binding.listHeader.tvHeaderCount.text = "0"
                        code?.let {
                            requireContext().showErrorSnackBar(binding.root, code)
                        }
                    } catch (e: Exception) {
                        Timber.e("${e.printStackTrace()}")
                    }
                }
            }
        }

        lifecycleScope.launch {
            joinRequestAdapter.loadStateFlow.distinctUntilChangedBy {
                it.refresh
            }.collectLatest {
                val hasItem = joinRequestAdapter.itemCount > 0
                binding.listEmpty.root.visibility = if (hasItem) View.GONE else View.VISIBLE
                binding.rvUserList.visibility = if (hasItem) View.VISIBLE else View.GONE
                if (!hasItem) {
                    binding.llButtons.visibility = View.GONE
                }
                setDividerItemDecoration(hasItem)
            }
        }
        clubJoinWaitViewModel.getJoinWaitMemberCount(clubId, uid)
        getJoinWaitMemberList()
    }

    private fun getJoinWaitMemberList() {
        lifecycleScope.launch {
            clubJoinWaitViewModel.getJoinWaitMemberList(clubId, uid).collectLatest {
                (binding.rvUserList.adapter as JoinRequestAdapter).submitData(it)
            }
        }
    }

    override fun initUiActionEvent() {
        binding.topbar.setOnBackButtonClickListener {
            navController.popBackStack()
        }

        binding.btnAllow.setOnClickListener {
            //가입 승인
            clubJoinWaitViewModel.joinClubApprove(
                clubId, hashMapOf(
                    KEY_UID to uid,
                    "joinIdList" to (binding.rvUserList.adapter as JoinRequestAdapter).getCheckedItemList()
                        .map { it.clubJoinWaitMemberWithMeta.clubJoinWaitMember.joinId }
                )
            )
        }

        binding.btnDeny.setOnClickListener {
            showDialog(
                getString(R.string.g_rejection_of_join_approval),
                getString(R.string.se_g_refuse_join),
                "",
                getString(R.string.g_rejection),
                getString(R.string.c_cancel),
                object : CommonDialog.ClickListener {
                    //가입 거절
                    override fun onClick() {
                        clubJoinWaitViewModel.joinClubReject(
                            clubId,
                            hashMapOf(
                                KEY_UID to uid,
                                "joinIdList" to (binding.rvUserList.adapter as JoinRequestAdapter).getCheckedItemList()
                                    .map { it.clubJoinWaitMemberWithMeta.clubJoinWaitMember.joinId }
                            )
                        )
                    }
                },
                null
            )
        }
    }

    private fun setDividerItemDecoration(set: Boolean) {
        if (set) {
            if (binding.rvUserList.itemDecorationCount < 1) {
                val decoration = ClubSettingItemDecoration(
                    ContextCompat.getDrawable(
                        requireActivity(),
                        R.drawable.divider_line
                    )
                )
                //decoration.setHasDiffHeaderItem(true)
                binding.rvUserList.addItemDecoration(decoration)
            }
        } else {
            if (binding.rvUserList.itemDecorationCount > 0) {
                binding.rvUserList.removeItemDecorationAt(0)
            }
        }
    }
}