package com.rndeep.fns_fantoo.ui.club.settings.tabs

import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.ErrorBody
import com.rndeep.fns_fantoo.databinding.FragmentClubSettingBoxPostBinding
import com.rndeep.fns_fantoo.ui.club.settings.ClubSettingBaseFragment
import com.rndeep.fns_fantoo.ui.club.settings.tabs.adapter.PostAdapter
import com.rndeep.fns_fantoo.ui.club.settings.tabs.viewmodel.PostViewModel
import com.rndeep.fns_fantoo.data.remote.dto.ClubStoragePostListWithMeta
import com.rndeep.fns_fantoo.utils.showErrorSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.launch
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException

@AndroidEntryPoint
class PostFragment(val clubId :String, val uid:String, val memberId:String, val clickListener:(ClubStoragePostListWithMeta)-> Unit) :
    ClubSettingBaseFragment<FragmentClubSettingBoxPostBinding>(FragmentClubSettingBoxPostBinding::inflate){

    private val postViewModel : PostViewModel by viewModels()

    override fun initUi() {
        val postAdapter = PostAdapter{selectedItem: ClubStoragePostListWithMeta -> clickListener(selectedItem)}
        binding.rv.adapter = postAdapter
        binding.rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        binding.emptyView.tvEmpty.text = getString(R.string.se_j_no_write_post)

        postAdapter.addLoadStateListener { loadState ->
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
                        setItemDecoreation(false)
                        setListViewVisibility(false)
                        code?.let{
                            requireContext().showErrorSnackBar(binding.root, code)
                        }
                    } catch (e: Exception) {
                        Timber.e("${e.printStackTrace()}")
                    }
                }
            }
        }

        lifecycleScope.launch {
            postViewModel.getStoragePostList(clubId, uid, memberId).collectLatest {
                postAdapter.submitData(it)
            }
        }

        lifecycleScope.launch {
            postAdapter.loadStateFlow.distinctUntilChangedBy {
                it.refresh
            }.collectLatest {
                setLoadingView(it.refresh is LoadState.Loading)
                setItemDecoreation(postAdapter.itemCount > 0)
                setListViewVisibility(postAdapter.itemCount > 0)
            }
        }
    }

    override fun initUiActionEvent() {
    }

    private fun setItemDecoreation(isEnable:Boolean){
        if(isEnable) {
            if(binding.rv.itemDecorationCount == 0) {
                val dividerItemDecoration =
                    DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
                ContextCompat.getDrawable(requireActivity(), R.drawable.club_setting_postbox_deco)
                    ?.let { dividerItemDecoration.setDrawable(it) }
                binding.rv.addItemDecoration(dividerItemDecoration)
            }
        }else{
            if(binding.rv.itemDecorationCount > 0) {
                binding.rv.removeItemDecorationAt(0)
            }
        }
    }

    private fun setListViewVisibility(showList: Boolean) {
        binding.rv.visibility = if (showList) View.VISIBLE else View.GONE
        binding.emptyView.root.visibility = if (showList) View.GONE else View.VISIBLE
    }

    private fun setLoadingView(isShow: Boolean) {
        binding.loadingView.visibility = if (isShow) View.VISIBLE else View.GONE
    }
}