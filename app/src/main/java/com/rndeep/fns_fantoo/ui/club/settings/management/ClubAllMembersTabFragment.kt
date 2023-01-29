package com.rndeep.fns_fantoo.ui.club.settings.management

import android.icu.text.NumberFormat
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.ErrorBody
import com.rndeep.fns_fantoo.data.remote.dto.ClubMemberInfoDto
import com.rndeep.fns_fantoo.ui.common.RecyclerViewItemClickListener
import com.rndeep.fns_fantoo.ui.common.viewgroup.CommonSearchableTopbar
import com.rndeep.fns_fantoo.databinding.FragmentClubManagementTabAllMemberBinding
import com.rndeep.fns_fantoo.ui.club.settings.ClubSettingBaseFragment
import com.rndeep.fns_fantoo.ui.club.settings.ClubSettingItemDecoration
import com.rndeep.fns_fantoo.ui.club.settings.management.adapter.AllMembersAdapter
import com.rndeep.fns_fantoo.ui.club.settings.management.viewmodel.ClubAllMembersViewModel
import com.rndeep.fns_fantoo.utils.ConstVariable
import com.rndeep.fns_fantoo.utils.ConstVariable.ClubDef.Companion.KEY_CLUB_INFO_MEMBERID
import com.rndeep.fns_fantoo.utils.ConstVariable.ClubSetting.Companion.KEY_MEMBERS_ENTRY_ROUTE_TYPE
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

@AndroidEntryPoint
class ClubAllMembersTabFragment
    (
    private val parentLifecycleListener: CopyOnWriteArrayList<ClubMembersManagement.ParentLifecycleListener>,
    private val searchableTopbar: CommonSearchableTopbar,
    private val entryRouteType: ConstVariable.ClubSetting.MembersEntryRouteType,
    private val clubId: String,
    private val uid: String
) :
    ClubSettingBaseFragment<FragmentClubManagementTabAllMemberBinding>
        (FragmentClubManagementTabAllMemberBinding::inflate) {

    private val clubAllMembersViewModel: ClubAllMembersViewModel by viewModels()

    override fun initUi() {
        val membersAdapter = AllMembersAdapter()
        membersAdapter.itemClickListener = object : RecyclerViewItemClickListener {
            override fun onItemClick(objects: Any) {
                val selectMember = objects as ClubMemberInfoDto
                val bundle = Bundle()
                bundle.putSerializable(KEY_MEMBERS_ENTRY_ROUTE_TYPE, entryRouteType)
                bundle.putString(ConstVariable.KEY_UID, uid)
                bundle.putString(ConstVariable.ClubDef.KEY_CLUB_INFO_CLUBID, clubId)
                bundle.putString(KEY_CLUB_INFO_MEMBERID, selectMember.memberId.toString())
                findNavController().navigate(R.id.clubManageMemberDetailFragment, bundle)
            }
        }

        binding.rv.adapter = membersAdapter
        if (membersAdapter.itemCount > 0) {
            setDividerItemDecoration(true)
        }

        binding.rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        membersAdapter.addLoadStateListener {
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

        lifecycleScope.launch {
            (binding.rv.adapter as AllMembersAdapter).loadStateFlow.distinctUntilChangedBy {
                it.refresh
            }.collectLatest {
                setLoadingView(it.refresh is LoadState.Loading)
                setDividerItemDecoration((binding.rv.adapter as AllMembersAdapter).itemCount > 0)
                if (it.refresh is LoadState.NotLoading) {
                    val isSearchMode = searchableTopbar.isSearchMode()
                    if ((binding.rv.adapter as AllMembersAdapter).snapshot().size > 0) {
                        try {
                            setListViewVisibility(true)
                        } catch (e: IndexOutOfBoundsException) {
                            Timber.e("updateHeaderView IndexOutOfBoundsException ${e.printStackTrace()}")
                        }
                    } else {
                        setListViewVisibility(false)
                        if (isSearchMode) {
                            updateSearchEmptyView(true, searchableTopbar.getEditTextString())
                        }
                    }
                    if(isSearchMode){
                        if((binding.rv.adapter as AllMembersAdapter).itemCount == 0) {
                            updateHeaderView("0")
                        }else{
                            try {
                                updateHeaderView((binding.rv.adapter as AllMembersAdapter).snapshot().items[0].totalMemberCount.toString())
                            }catch (e:Exception){
                                Timber.e("${e.printStackTrace()}")
                            }
                        }
                    }else{
                        clubAllMembersViewModel.getMemberCount(clubId, uid)
                    }
                }
            }
        }

        clubAllMembersViewModel.networkErrorLiveData.observe(viewLifecycleOwner){
            showDialog(getString(R.string.alert_network_error))
        }

        clubAllMembersViewModel.clubMemberCountLiveData.observe(viewLifecycleOwner){
                clubMemberCount ->
            clubMemberCount?.let {
                updateHeaderView(it.memberCount.toString())
            }
        }
        lifecycleScope.launch{
            getMembersList()
        }

        parentLifecycleListener.add(object :ClubMembersManagement.ParentLifecycleListener{
            override fun onResumeParentFragment() {//강제탈퇴처리후 화면돌아올때 onresume이 콜백되지 않는 문제로 추가
                lifecycleScope.launch{
                    clubAllMembersViewModel.getMemberCount(clubId, uid)
                    getMembersList()
                }
            }

            override fun onPauseParentFragment() {
            }
        })
    }


    override fun initUiActionEvent() {

    }

    private fun setListViewVisibility(showList: Boolean) {
        binding.rv.visibility = if (showList) View.VISIBLE else View.GONE
        binding.listEmpty.root.visibility = if (showList) View.GONE else View.VISIBLE
    }

    private fun updateHeaderView(membersCount: String) {
        try {
            Timber.d(
                "headerCount = " + NumberFormat.getNumberInstance(Locale.US)
                    .format(membersCount.toInt())
            )
            binding.listHeader.tvHeaderCount.text =
                NumberFormat.getNumberInstance(Locale.US).format(membersCount.toInt())
        } catch (e: Exception) {
            Timber.e("updateHeaderView err:  ${e.message}")
            binding.listHeader.tvHeaderCount.text = membersCount
        }
    }

    private fun updateSearchEmptyView(isShowEmptyView: Boolean, searchString: String) {
        binding.listEmpty.tvEmptyMember.visibility = View.GONE
        binding.listEmpty.clSearchEmpty.visibility =
            if (isShowEmptyView) View.VISIBLE else View.GONE
        binding.listEmpty.tvSearchEmpty.text = String.format(
            binding.listEmpty.root.context.getString(R.string.se_r_not_exist_search_member),
            searchString
        )
    }

    fun setDividerItemDecoration(isAdd: Boolean) {
        val dividerItemDecoration =
            ContextCompat.getDrawable(requireActivity(), R.drawable.divider_line)
                ?.let { ClubSettingItemDecoration(it) }
        if (isAdd) {
            if (binding.rv.itemDecorationCount < 1) {
                if (dividerItemDecoration != null) {
                    //dividerItemDecoration.setHasDiffHeaderItem(true)
                    binding.rv.addItemDecoration(dividerItemDecoration)
                }
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

    override fun onResume() {
        super.onResume()
        searchableTopbar.setSearchModeChangeListener(searchModeChangeListener)
        searchableTopbar.setOnSearchClickListener(searchClickListener)
    }

    override fun onPause() {
        super.onPause()
        searchableTopbar.removeSearchModeChangeListener(searchModeChangeListener)
        searchableTopbar.removeSearchClickListener(searchClickListener)
    }

    private val searchModeChangeListener =
        object : CommonSearchableTopbar.SearchModeChangedListener {
            override fun onModeChanged(isSearchMode: Boolean, view: EditText) {
                if (!isSearchMode) {
                    lifecycleScope.launch {
                        updateSearchEmptyView(false, "")
                        getMembersList()
                    }
                }
            }
        }

    private suspend fun getMembersList() {
        clubAllMembersViewModel.getMembersList(clubId, uid).collectLatest {
            (binding.rv.adapter as AllMembersAdapter).submitData(it)
        }
    }

    private val searchClickListener = object : CommonSearchableTopbar.SearchClickListener {
        override fun onSearchClick(searchString: String) {
            lifecycleScope.launch {
                clubAllMembersViewModel.getSearchMembersList(
                    clubId = clubId,
                    uid = uid,
                    keyword = searchString
                ).collectLatest {
                    (binding.rv.adapter as AllMembersAdapter).submitData(it)
                }
            }
        }
    }

    private fun setLoadingView(isShow: Boolean) {
        binding.loadingView.visibility = if (isShow) View.VISIBLE else View.GONE
    }
}