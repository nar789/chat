package com.rndeep.fns_fantoo.ui.community.board.notice

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.FragmentNoticePageBinding
import com.rndeep.fns_fantoo.ui.community.BoardInfo
import com.rndeep.fns_fantoo.ui.common.BaseFragment
import com.rndeep.fns_fantoo.utils.ConstVariable
import com.rndeep.fns_fantoo.ui.common.custombottomsheet.CustomBottomSheet
import com.rndeep.fns_fantoo.utils.SizeUtils
import com.rndeep.fns_fantoo.ui.common.custombottomsheet.BottomSheetAdapter
import com.rndeep.fns_fantoo.ui.common.custombottomsheet.BottomSheetItem
import com.rndeep.fns_fantoo.utils.checkLastItemVisible
import com.rndeep.fns_fantoo.utils.showErrorSnackBar
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class NoticePageFragment :
    BaseFragment<FragmentNoticePageBinding>(FragmentNoticePageBinding::inflate) {
    private val noticeViewModel: NoticePageViewModel by viewModels()


    val getItem: NoticePageFragmentArgs by navArgs()
    lateinit var noticeItem: BoardInfo

    //게시판 공지 어댑터
    private val boardNoticeAdapter = BoardNoticeAdapter()

    //postOptionsItem
    private lateinit var postOptionsItem: ArrayList<BottomSheetItem>

    private var isLoading = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        noticeItem = getItem.noticeInfo

        postOptionsItem = arrayListOf(
            BottomSheetItem(
                R.drawable.icon_outline_save,
                activity?.getString(R.string.j_to_save) ?: "",
                null,
                false
            )
        )

    }

    override fun initUi() {
        initToolbar()
        settingObserver()
        detectRecyclerviewBottom()
        binding.rcNoticeList.layoutManager = LinearLayoutManager(requireContext())
        binding.rcNoticeList.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                val itemPosition = parent.getChildAdapterPosition(view)
                if (itemPosition == RecyclerView.NO_POSITION) return
                outRect.bottom = SizeUtils.getDpValue(8f, requireContext()).toInt()
            }
        })
        binding.rcNoticeList.adapter = boardNoticeAdapter

        binding.layoutNoListView.root.visibility = View.GONE
        noticeViewModel.getBoardNoticeItem(noticeItem.boardId)
    }

    fun detectRecyclerviewBottom() {
        binding.rcNoticeList.checkLastItemVisible {
            if (boardNoticeAdapter.itemCount < 9) {
                return@checkLastItemVisible
            }
            if (it && !isLoading) {
                isLoading = true
                noticeViewModel.getBoardNoticeItem(noticeItem.boardId)
            }
        }
    }

    override fun initUiActionEvent() {
        boardNoticeAdapter.setOnNoticeItemClickListener(object :
            BoardNoticeAdapter.NoticeItemClickListener {
            override fun onItemClick(postId: Int) {
                findNavController().navigate(
                    NoticePageFragmentDirections.actionNoticePageFragment4ToCommunitypost(
                        ConstVariable.TYPE_COMMUNITY_NOTICE,
                        noticeItem.boardId,
                        postId
                    )
                )
            }

            override fun onOptionClick(postId: Int) {
                CustomBottomSheet().apply {
                    setBottomItems(postOptionsItem)
                    setOnCliCkListener(object : BottomSheetAdapter.OnItemClickListener {
                        override fun onItemClick(name: String, pos: Int, oldPos: Int?) {
                            dismiss()
                        }
                    })
                }.show(parentFragmentManager, "notice")
            }
        })
    }

    fun initToolbar() {
        //뒤로가기
        binding.noticeToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    fun settingObserver() {
        noticeViewModel.commonNoticeLiveData.observe(viewLifecycleOwner) {
            if(noticeViewModel.noticeNextId!=0){
                isLoading=false
            }
            if (it.isEmpty() && boardNoticeAdapter.itemCount == 0) {
                binding.layoutNoListView.root.visibility = View.VISIBLE
            } else {
                binding.layoutNoListView.root.visibility = View.GONE
                boardNoticeAdapter.setItems(it)
            }
        }

        noticeViewModel.errorCodeLiveData.observe(viewLifecycleOwner) {
            activity?.showErrorSnackBar(binding.root, it)
        }

        noticeViewModel.loadingStateLiveData.observe(this){
            binding.fantooLoadingView.visibility = if(it) View.VISIBLE else View.GONE
        }
    }


    override fun onPause() {
        super.onPause()
        noticeViewModel.noticeNextId=0
        isLoading=true
    }
}