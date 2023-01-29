package com.rndeep.fns_fantoo.ui.club.settings.management

import android.icu.text.NumberFormat
import android.view.View
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.ErrorBody
import com.rndeep.fns_fantoo.data.remote.dto.ClubWithDrawMemberInfoDto
import com.rndeep.fns_fantoo.ui.common.RecyclerViewItemClickListener
import com.rndeep.fns_fantoo.ui.common.viewgroup.CommonSearchableTopbar
import com.rndeep.fns_fantoo.databinding.FragmentClubManagementTabBanMemberBinding
import com.rndeep.fns_fantoo.ui.club.settings.ClubSettingBaseFragment
import com.rndeep.fns_fantoo.ui.club.settings.ClubSettingItemDecoration
import com.rndeep.fns_fantoo.ui.club.settings.MenuBottomSheetDialogFragment
import com.rndeep.fns_fantoo.ui.club.settings.data.MenuItem
import com.rndeep.fns_fantoo.ui.club.settings.management.adapter.BanMembersAdapter
import com.rndeep.fns_fantoo.ui.club.settings.management.viewmodel.ClubBanMembersViewModel
import com.rndeep.fns_fantoo.utils.ConstVariable
import com.rndeep.fns_fantoo.utils.ConstVariable.KEY_UID
import com.rndeep.fns_fantoo.utils.showErrorSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.launch
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.collections.ArrayList

@AndroidEntryPoint
class ClubBanMembersTabFragment(
    private val searchableTopbar: CommonSearchableTopbar,
    private val clubId: String,
    private val uid: String,
    private val onSearchModeChanged:(Boolean) -> Unit
) :
    ClubSettingBaseFragment<FragmentClubManagementTabBanMemberBinding>(
        FragmentClubManagementTabBanMemberBinding::inflate
    ) {
    private val clubBanMembersViewModel: ClubBanMembersViewModel by viewModels()

    private lateinit var menuBottomSheetDialogFragment: MenuBottomSheetDialogFragment
    private lateinit var menuItemArrayList: ArrayList<MenuItem>

    override fun initUi() {
        val adapter = BanMembersAdapter()
        adapter.setItemClickListener(object : RecyclerViewItemClickListener {
            override fun onItemClick(objects: Any) {
                Timber.d("BanMembers clickItem $objects")
                objects.let {
                    try {
                        val memberInfo = (objects as ClubWithDrawMemberInfoDto)
                        showBottomSheet(memberInfo)
                    } catch (e: Exception) {
                        Timber.e("${e.printStackTrace()}")
                    }
                }
            }
        })

        binding.rv.adapter = adapter
        binding.rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        adapter.addLoadStateListener {
                loadState ->
            val errorState = when {
                loadState.append is LoadState.Error -> loadState.append as LoadState.Error
                loadState.prepend is LoadState.Error ->  loadState.prepend as LoadState.Error
                loadState.refresh is LoadState.Error -> loadState.refresh as LoadState.Error
                else -> null
            }
            when(val throwable = errorState?.error){
                is IOException ->{}
                is HttpException ->{
                    val errorBody = throwable.response()?.errorBody()?.string()
                    try {
                        val gsonErrorBody = Gson().fromJson(
                            errorBody,
                            ErrorBody::class.java
                        )
                        val code = gsonErrorBody.code
                        val message = gsonErrorBody.msg
                        //val dataObj = gsonErrorBody.dataObj
                        setListViewVisibility(false)
                        updateHeaderView("0")
                        code?.let {
                            requireContext().showErrorSnackBar(binding.root, code)
                        }
                    }catch (e:Exception){
                        Timber.e("${e.printStackTrace()}")
                    }
                }
            }
        }

        clubBanMembersViewModel.networkErrorLiveData.observe(viewLifecycleOwner){
            showDialog(getString(R.string.alert_network_error))
        }

        clubBanMembersViewModel.withDrawMemberJoinAllowResultLiveData.observe(viewLifecycleOwner){
            clubMemberWithdrawResponse ->
            clubMemberWithdrawResponse?.let {
                when(clubMemberWithdrawResponse.code){
                    ConstVariable.RESULT_SUCCESS_CODE ->{
                        lifecycleScope.launch {
                            getBanMemberList()
                        }
                    }
                    else->{
                        requireContext().showErrorSnackBar(binding.root, clubMemberWithdrawResponse.code)
                    }
                }
            }
        }

        lifecycleScope.launch {
            adapter.loadStateFlow.distinctUntilChangedBy {
                it.refresh
            }.collectLatest {
                setLoadingView(it.refresh is LoadState.Loading)
                val hasItem = adapter.snapshot().isNotEmpty()
                setDividerItemDecoration(hasItem)
                setListViewVisibility(hasItem)
                updateEmptyView(adapter.snapshot().size)
            }
        }

        clubBanMembersViewModel.withDrawMemberCountLiveData.observe(viewLifecycleOwner){
            clubMemberCount ->
            clubMemberCount?.let {
                updateHeaderView(it.memberCount.toString())
            }
        }
        lifecycleScope.launch {
            clubBanMembersViewModel.getWithDrawMemberCount(clubId, uid)
            getBanMemberList()
        }
    }

    private suspend fun getBanMemberList() {
        clubBanMembersViewModel.getWithDrawMemberList(clubId, uid).collectLatest {
            (binding.rv.adapter as BanMembersAdapter).submitData(it)
        }
    }

    override fun initUiActionEvent() {
    }

    private fun setListViewVisibility(showList: Boolean) {
        binding.rv.visibility = if (showList) View.VISIBLE else View.GONE
        binding.listEmpty.root.visibility = if (showList) View.GONE else View.VISIBLE
    }

    fun setDividerItemDecoration(isAdd: Boolean) {
        val dividerItemDecoration =
            ClubSettingItemDecoration(
                ContextCompat.getDrawable(
                    requireActivity(),
                    R.drawable.divider_line
                )
            )
        if (isAdd) {
            if (binding.rv.itemDecorationCount == 0) {
                //dividerItemDecoration.setHasDiffHeaderItem(true)
                binding.rv.addItemDecoration(dividerItemDecoration)
            }
        } else {
            if (binding.rv.itemDecorationCount > 0) {
                try {
                    binding.rv.removeItemDecorationAt(0)
                } catch (e: IndexOutOfBoundsException) {
                    Timber.e("setDividerItemDecoration IndexOutOfBoundsException")
                }
            }
        }
    }

    fun showBottomSheet(memberInfo: ClubWithDrawMemberInfoDto) {
        menuItemArrayList = ArrayList()
        menuItemArrayList.add(MenuItem(0, getString(R.string.g_prohibition), getString(R.string.se_h_cannot_rejoin_this_id)))
        menuItemArrayList.add(
            MenuItem(
                1,
                getString(R.string.h_allow),
                getString(R.string.se_h_can_rejoin_this_id)
            )
        )
        menuBottomSheetDialogFragment = MenuBottomSheetDialogFragment(menuItemArrayList)
        menuBottomSheetDialogFragment.title = getString(R.string.j_setting_rejoin)
        menuBottomSheetDialogFragment.currentMenuItem = if(memberInfo.joinYn) menuItemArrayList[1] else menuItemArrayList[0]
        menuBottomSheetDialogFragment.setOnMenuItemClickListener(object :
            RecyclerViewItemClickListener {
            override fun onItemClick(objects: Any) {
                val selectedMenuItem = objects as MenuItem
                Timber.d("club public/private set select item = ${selectedMenuItem.title1}")
                clubBanMembersViewModel.updateJoinAllowWithDrawMember(clubId, memberInfo.clubWithdrawId.toString(),
                hashMapOf(KEY_UID to uid, "joinYn" to (selectedMenuItem.id == 1)))
            }
        })
        menuBottomSheetDialogFragment.show(requireActivity().supportFragmentManager, "")
    }

    override fun onResume() {
        super.onResume()
        searchableTopbar.setSearchModeChangeListener(searchModeChangeListener)
    }

    override fun onPause() {
        super.onPause()
        searchableTopbar.removeSearchModeChangeListener(searchModeChangeListener)
    }

    private val searchModeChangeListener =
        object : CommonSearchableTopbar.SearchModeChangedListener {
            override fun onModeChanged(isSearchMode: Boolean, view: EditText) {
                onSearchModeChanged(isSearchMode)
            }
        }

    private fun updateHeaderView(membersCount: String) {
        try {
            Timber.d(
                "ban headerCount = " + NumberFormat.getNumberInstance(Locale.US)
                    .format(membersCount.toInt())
            )
            binding.listHeader.tvHeaderCount.text =
                NumberFormat.getNumberInstance(Locale.US).format(membersCount.toInt())
        } catch (e: Exception) {
            Timber.e("HeaderVH err: " + e.message)
            binding.listHeader.tvHeaderCount.text = membersCount
        }
    }

    private fun updateEmptyView(showingListItemCount: Int) {
        if (showingListItemCount != 0) return
        if (searchableTopbar.isSearchMode()) {
            binding.listEmpty.tvEmptyMember.visibility = View.GONE
            binding.listEmpty.clSearchEmpty.visibility = View.VISIBLE
            binding.listEmpty.tvSearchEmpty.text = String.format(
                binding.listEmpty.root.context.getString(R.string.se_r_not_exist_search_member),
                searchableTopbar.getEditTextString()
            )
        } else {
            binding.listEmpty.clSearchEmpty.visibility = View.GONE
            binding.listEmpty.tvEmptyMember.visibility = View.VISIBLE
        }
    }

    private fun setLoadingView(isShow: Boolean) {
        binding.loadingView.visibility = if (isShow) View.VISIBLE else View.GONE
    }
}