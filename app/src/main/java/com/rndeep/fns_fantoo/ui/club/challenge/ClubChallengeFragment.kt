package com.rndeep.fns_fantoo.ui.club.challenge

import android.graphics.Rect
import android.util.TypedValue
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.FragmentCommunityBoardBinding
import com.rndeep.fns_fantoo.ui.common.BaseFragment
import com.rndeep.fns_fantoo.utils.addSingleItemDecoRation
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ClubChallengeFragment :
    BaseFragment<FragmentCommunityBoardBinding>(FragmentCommunityBoardBinding::inflate) {

    private val clubChallengeViewModel: ClubChallengeViewModel by viewModels()

    private val clubChallengeAdapter by lazy { ClubChallengeAdapter() }

    private val itemDecoration by lazy {
        object : RecyclerView.ItemDecoration() {

            private val lastGap: Int = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                10f,
                context?.resources?.displayMetrics
            ).toInt()
            private val sideGap: Int = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                4f,
                context?.resources?.displayMetrics
            ).toInt()

            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                val itempostion = parent.getChildAdapterPosition(view)
                if (itempostion == RecyclerView.NO_POSITION) return
                val topOffset = if (isFirstItem(itempostion)) 0 else sideGap
                val bottomOffset = if (isLastItem(itempostion, parent)) lastGap else sideGap
                outRect.set(0, topOffset, 0, bottomOffset)
            }

            private fun isFirstItem(index: Int) = index == 0
            private fun isLastItem(index: Int, recyclerView: RecyclerView) =
                index == recyclerView.adapter!!.itemCount - 1
        }

    }

    override fun initUi() {
        binding.communityBoardToolbar.title = getString(R.string.c_challenge)
        binding.btnScrollUp.visibility = View.GONE
        binding.fabBoardPostEdit.visibility = View.GONE
        binding.rcBoardPostList.layoutManager = LinearLayoutManager(context)
        binding.layoutNoListView.root.visibility = View.GONE
        binding.rcBoardPostList.addSingleItemDecoRation(itemDecoration)
        binding.rcBoardPostList.adapter = clubChallengeAdapter

        if (clubChallengeViewModel.challengeListLiveData.value == null) {
            clubChallengeViewModel.changeLoadingState(true)
        }
        clubChallengeViewModel.getChallengeList()
        settingObserver()

    }

    override fun initUiActionEvent() {
        binding.communityBoardToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    fun settingObserver() {
        clubChallengeViewModel.challengeListLiveData.observe(viewLifecycleOwner) {
            clubChallengeViewModel.changeLoadingState(false)
            clubChallengeAdapter.setChallengeItem(it)
        }

        clubChallengeViewModel.loadingStateLiveData.observe(viewLifecycleOwner) {
            binding.fantooLoadingView.visibility = if (it) View.VISIBLE else View.GONE
        }
    }

    override fun onPause() {
        super.onPause()
        clubChallengeViewModel.challengeNextId=null
    }

}