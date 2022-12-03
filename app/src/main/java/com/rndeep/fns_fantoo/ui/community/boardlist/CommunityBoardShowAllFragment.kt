package com.rndeep.fns_fantoo.ui.community.boardlist

import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.model.community.CategoryBoardCategoryList
import com.rndeep.fns_fantoo.databinding.FragmentCommunityBoardShowAllBinding
import com.rndeep.fns_fantoo.ui.community.BoardInfo
import com.rndeep.fns_fantoo.ui.common.BaseFragment
import com.rndeep.fns_fantoo.utils.*
import com.rndeep.fns_fantoo.ui.common.custombottomsheet.BottomSheetAdapter
import com.rndeep.fns_fantoo.ui.common.custombottomsheet.CustomBottomSheet
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class CommunityBoardShowAllFragment :
    BaseFragment<FragmentCommunityBoardShowAllBinding>(FragmentCommunityBoardShowAllBinding::inflate) {

    private val boardAllViewModel: CommunityBoardShowAllViewModel by viewModels()

    private val communityBoardAllAdapter = CommunityBoardShowAllAdapter()

    private val itemDeco = object : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)
            val position = parent.getChildAdapterPosition(view)
            if (isLast(position, parent)) outRect.bottom = 100

        }

        private fun isLast(index: Int, recyclerView: RecyclerView) =
            index == recyclerView.adapter!!.itemCount - 1
    }

    private lateinit var dividerDeco :CustomDividerDecoration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dividerDeco= CustomDividerDecoration(1f,0f,requireContext().getColor(R.color.gray_400_opacity12),true)
    }

    override fun initUi() {
        initToolbar()
        initView()
        settingObserve()

    }

    override fun initUiActionEvent() {
        binding.switchRecommend.setOnTouchListener { view, motionEvent ->
            if(!boardAllViewModel.isMember() && motionEvent.action==MotionEvent.ACTION_DOWN){
                activity?.showErrorSnackBar(binding.root,ConstVariable.ERROR_PLEASE_LATER_LOGIN)
                return@setOnTouchListener true
            }else{
                return@setOnTouchListener false
            }
        }
        binding.switchRecommend.setOnCheckedChangeListener { compoundButton, checked ->
            boardAllViewModel.boardAllCategoryListItemLiveData.value?.let {
                settingBoardListItem(checked,it)
                boardAllViewModel.setFavoriteState(checked)
            }
        }

        communityBoardAllAdapter.setOnFavoriteClickListener(object :
            CommunityBoardShowAllAdapter.OnFavoriteClickListener {
            override fun onFavoriteIconClick(v: View, boardId: String, isBookmark: Boolean) {
                if(!boardAllViewModel.isMember()) {
                    activity?.showErrorSnackBar(
                        binding.root,
                        ConstVariable.ERROR_PLEASE_LATER_LOGIN
                    )
                    return
                }
                if (isBookmark) {
                    boardAllViewModel.unRegisterBookmark(boardId)
                } else {
                    boardAllViewModel.registerBookmark(boardId)
                }
            }

            override fun onBoardItemClick(v: View, position: Int, item: BoardInfo) {
                val action =
                    CommunityBoardShowAllFragmentDirections.actionCommunityBoardShowAllFragmentToCommunityboard(
                        item
                    )
                findNavController().navigate(action)
            }
        })

        binding.llBoardSortContainer.setOnClickListener {
            if(!boardAllViewModel.isMember()){
                activity?.showErrorSnackBar(binding.root,ConstVariable.ERROR_PLEASE_LATER_LOGIN)
                return@setOnClickListener
            }
            val sort = getString(R.string.j_sorting)
            context?.let {
                CustomBottomSheet().apply {
                    setTitleText(sort)
                    setBottomItems(boardAllViewModel.bottomSheetItem)
                    setSubTitleTextColor(it.getColor(R.color.state_enable_gray_400))
                    setOnCliCkListener(object : BottomSheetAdapter.OnItemClickListener {
                        override fun onItemClick(name: String, pos: Int, oldPos: Int?) {
                            when (name) {
                                resources.getString(R.string.p_order_by_user_recommend) -> {
                                    binding.tvSortText.text = resources.getString(R.string.p_order_by_user_recommend)
                                    boardAllViewModel.settingFavoriteItem(ConstVariable.SORTFANTOO)
                                }
                                resources.getString(R.string.a_order_by_popular) -> {
                                    binding.tvSortText.text = resources.getString(R.string.a_order_by_popular)
                                    boardAllViewModel.settingFavoriteItem(ConstVariable.SORTPOPULAR)

                                }
                            }
                            boardAllViewModel.showBottomSheetItem(pos)
                            dismiss()
                        }
                    })
                }.show(this.childFragmentManager, "sort")
            }
        }

    }

    private fun initToolbar() {
        binding.communityAllBoardToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    fun initView() {
        //게시판 리스트트
        binding.rcBoardList.layoutManager = LinearLayoutManager(requireContext())
        binding.rcBoardList.adapter = communityBoardAllAdapter
        binding.rcBoardList.addSingleItemDecoRation(dividerDeco,itemDeco)

        boardAllViewModel.settingFavoriteItem(ConstVariable.SORTFANTOO)

        //bottomSheetItem setting
        boardAllViewModel.settingBottomSheetItem(
            listOf(
                resources.getString(R.string.p_order_by_user_recommend),
                resources.getString(R.string.a_order_by_popular)
            ),
            listOf(
                resources.getString(R.string.p_order_by_manager_recommend),
                resources.getString(R.string.k_order_by_category_of_community)
            ),
        )
    }

    private fun settingObserve() {
        boardAllViewModel.boardAllCategoryListItemLiveData.observe(viewLifecycleOwner) {
            settingBoardListItem(binding.switchRecommend.isChecked,it)
        }

        boardAllViewModel.registerBookmarkLiveData.observe(viewLifecycleOwner) {
            if (!it) {
                activity?.showErrorSnackBar(binding.root,ConstVariable.ERROR_WAIT_FOR_SECOND)
                return@observe
            }
            when(boardAllViewModel.getSortType()){
                "Recommend" ->{
                    boardAllViewModel.settingFavoriteItem(ConstVariable.SORTFANTOO)
                }
                "Popular" -> {
                    boardAllViewModel.settingFavoriteItem(ConstVariable.SORTPOPULAR)
                }
            }
        }

        boardAllViewModel.boardListFavoriteStateLiveData.observe(this){
            binding.switchRecommend.isChecked=it
        }

    }

    fun settingBoardListItem(isFavorite :Boolean,boardListItem : List<CategoryBoardCategoryList>){
        if(isFavorite){
            val favoriteItem = arrayListOf<CategoryBoardCategoryList>().apply {
                boardListItem.forEach { item ->
                    if(item.favorite==true){
                        add(item)
                    }
                }
            }
            showHideNoListLayout(favoriteItem.isEmpty())
            communityBoardAllAdapter.setItems(favoriteItem)
        }else{
            showHideNoListLayout(false)
            communityBoardAllAdapter.setItems(boardListItem)
        }
    }

    fun showHideNoListLayout(isShow :Boolean){
        if (isShow) {
            binding.rcBoardList.visibility = View.GONE
            binding.topdivider.visibility = View.GONE
            binding.rlNoBoard.visibility = View.VISIBLE
        } else {
            binding.rcBoardList.visibility = View.VISIBLE
            binding.topdivider.visibility = View.VISIBLE
            binding.rlNoBoard.visibility = View.GONE
        }
    }
}