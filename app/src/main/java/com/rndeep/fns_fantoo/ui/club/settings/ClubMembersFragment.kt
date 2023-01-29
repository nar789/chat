package com.rndeep.fns_fantoo.ui.club.settings

import android.annotation.SuppressLint
import android.view.View
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.ErrorBody
import com.rndeep.fns_fantoo.data.remote.dto.ClubMemberInfoDto
import com.rndeep.fns_fantoo.databinding.FragmentClubMembersBinding
import com.rndeep.fns_fantoo.ui.club.settings.adapter.ClubMembersAdapter
import com.rndeep.fns_fantoo.ui.club.settings.viewmodel.ClubMembersViewModel
import com.rndeep.fns_fantoo.ui.common.RecyclerViewItemClickListener
import com.rndeep.fns_fantoo.ui.common.viewgroup.CommonSearchableTopbar
import com.rndeep.fns_fantoo.utils.ConstVariable
import com.rndeep.fns_fantoo.utils.showErrorSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.launch
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException

@AndroidEntryPoint
class ClubMembersFragment :
    ClubSettingBaseFragment<FragmentClubMembersBinding>(FragmentClubMembersBinding::inflate) {

    private val clubMembersViewModel: ClubMembersViewModel by viewModels()

    private lateinit var clubId: String
    private lateinit var uid: String
    private lateinit var myMemberId:String
    override fun initUi() {
        val args: ClubMembersFragmentArgs by navArgs()
        clubId = args.clubId
        uid = args.integUid.toString()
        myMemberId = args.myMemberId

        val adapter =
            ClubMembersAdapter()
        adapter.setOnItemClickListener(object : RecyclerViewItemClickListener {
            override fun onItemClick(objects: Any) {
                try {
                    val clubMember = objects as ClubMemberInfoDto
                    navController.navigate(
                        R.id.action_clubMembersFragment_to_club_member, bundleOf(
                            ConstVariable.ClubDef.KEY_CLUB_INFO_CLUBID to clubId,
                            ConstVariable.KEY_UID to uid,
                            ClubMemberDetailFragment.KEY_MEMBER_ID to clubMember.memberId,
                            "myMemberId" to myMemberId
                        )
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })

        binding.rcMembers.adapter = adapter
        binding.rcMembers.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        adapter.addLoadStateListener { loadState ->
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
                        setEmptyView(true)
                        updateTopCountText("0")
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
            clubMembersViewModel.getMembersList(clubId, uid).collectLatest {
                (binding.rcMembers.adapter as ClubMembersAdapter).submitData(it)
            }
        }

        lifecycleScope.launch {
            (binding.rcMembers.adapter as ClubMembersAdapter).loadStateFlow.distinctUntilChangedBy {
                it.refresh
            }.collectLatest {
                setLoadingView(it.refresh is LoadState.Loading)
                updateDividerItemDecoration(adapter.itemCount)
                setEmptyView(adapter.itemCount == 0)
                if(binding.topbar.isSearchMode()) {
                    if (adapter.itemCount == 0) {
                        updateTopCountText("0")
                    } else {
                        try {
                            updateTopCountText((binding.rcMembers.adapter as ClubMembersAdapter).snapshot().items[0].totalMemberCount.toString())
                        }catch (e:Exception){
                            Timber.e("${e.printStackTrace()}")
                        }
                    }
                }else{
                    clubMembersViewModel.getMemberCount(clubId, uid)
                }
            }
        }

        clubMembersViewModel.clubMemberCountLiveData.observe(viewLifecycleOwner){
            clubMemberCount ->
            clubMemberCount?.let {
                updateTopCountText(it.memberCount.toString())
            }
        }
    }

    @SuppressLint("ServiceCast")
    override fun initUiActionEvent() {
        binding.topbar.setOnBackButtonClickListener {
            navController.popBackStack()
        }

        binding.topbar.setSearchModeChangeListener(object :
            CommonSearchableTopbar.SearchModeChangedListener {
            override fun onModeChanged(isSearchMode: Boolean, view: EditText) {
                if (!isSearchMode) {
                    lifecycleScope.launch {
                        clubMembersViewModel.getMembersList(clubId, uid).collectLatest {
                            (binding.rcMembers.adapter as ClubMembersAdapter).submitData(it)
                        }
                        binding.llResultEmpty.visibility = View.GONE
                    }
                }
            }
        })

        binding.topbar.setOnSearchClickListener(object :
            CommonSearchableTopbar.SearchClickListener {
            override fun onSearchClick(searchString: String) {
                if (searchString.isNotEmpty()) {
                    lifecycleScope.launch {
                        clubMembersViewModel.getSearchMembersList(clubId, uid, searchString)
                            .collectLatest {
                                (binding.rcMembers.adapter as ClubMembersAdapter).submitData(it)
                            }
                    }
                }
            }
        })
    }

    private fun setEmptyView(isShow: Boolean) {
        Timber.d("setEmptyView $isShow")
        if (isShow) {
            if (binding.topbar.isSearchMode()) {
                binding.tvResultEmpty.text = String.format(
                    getString(R.string.se_g_no_result_member_search),
                    binding.topbar.getEditTextString()
                )
                binding.llResultEmpty.visibility = View.VISIBLE
            } else {
                binding.tvEmptyMember.visibility = View.VISIBLE
            }
            binding.rcMembers.visibility = View.GONE
        } else {
            binding.llResultEmpty.visibility = View.GONE
            binding.tvEmptyMember.visibility = View.GONE
            binding.rcMembers.visibility = View.VISIBLE
        }
    }

    private fun updateTopCountText(countStr: String) {
        binding.topListCountLayout.tvMembersCount.text = countStr
    }

    private fun updateDividerItemDecoration(listItemCount: Int) {
        try {
            if (listItemCount > 0) {
                if (binding.rcMembers.itemDecorationCount < 1) {
                    binding.rcMembers.addItemDecoration(
                        ClubSettingItemDecoration(
                            ContextCompat.getDrawable(
                                requireActivity(),
                                R.drawable.divider_line
                            )
                        )
                    )
                }
            } else {
                if (binding.rcMembers.itemDecorationCount > 0) {
                    binding.rcMembers.removeItemDecoration(
                        binding.rcMembers.getItemDecorationAt(
                            0
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Timber.e("updateDividerItemDecoration ${e.message}")
        }
    }

    private fun setLoadingView(isShow: Boolean) {
        binding.loadingView.visibility = if (isShow) View.VISIBLE else View.GONE
    }
}
