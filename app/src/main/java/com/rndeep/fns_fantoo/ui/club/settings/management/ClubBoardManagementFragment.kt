package com.rndeep.fns_fantoo.ui.club.settings.management

import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.model.ClubCategoryItemPacerable
import com.rndeep.fns_fantoo.data.remote.model.ClubSubCategoryItem
import com.rndeep.fns_fantoo.ui.common.RecyclerViewItemClickListener
import com.rndeep.fns_fantoo.databinding.FragmentClubSettingBoardManagementBinding
import com.rndeep.fns_fantoo.ui.club.settings.ClubSettingBaseFragment
import com.rndeep.fns_fantoo.ui.club.settings.management.adapter.ArchiveBoardAdapter
import com.rndeep.fns_fantoo.ui.club.settings.management.swipe.ItemStartDragListener
import com.rndeep.fns_fantoo.ui.club.settings.management.swipe.SwipeHelperCallback
import com.rndeep.fns_fantoo.ui.club.settings.management.viewmodel.ClubBoardManagementViewModel
import com.rndeep.fns_fantoo.utils.ConstVariable
import com.rndeep.fns_fantoo.utils.ConstVariable.ClubDef.Companion.KEY_CLUB_CATEGORY_ITEM
import com.rndeep.fns_fantoo.utils.ConstVariable.KEY_UID
import com.rndeep.fns_fantoo.utils.showErrorSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber


@AndroidEntryPoint
class ClubBoardManagementFragment :
    ClubSettingBaseFragment<FragmentClubSettingBoardManagementBinding>(
        FragmentClubSettingBoardManagementBinding::inflate
    ) {

    private val clubBoardManagementViewModel: ClubBoardManagementViewModel by viewModels()

    private lateinit var clubId: String
    private lateinit var uid: String
    private var isArchiveEditMode = false
    private var freeboardCategoryItemPacerable: ClubCategoryItemPacerable? = null
    var itemTouchHelper: ItemTouchHelper? = null

    override fun initUi() {
        val args: ClubBoardManagementFragmentArgs by navArgs()
        clubId = args.clubId
        uid = args.integUid.toString()

        lifecycleScope.launch {

            val archiveAdapter = ArchiveBoardAdapter(clubBoardManagementViewModel.getLanguageCode())
            binding.rvArchive.adapter = archiveAdapter
            binding.rvArchive.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

            archiveAdapter.setDragStartListener(object : ItemStartDragListener {
                override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
                    itemTouchHelper?.startDrag(viewHolder)
                }
            })
            archiveAdapter.setOnItemClickListener(object : RecyclerViewItemClickListener {
                override fun onItemClick(objects: Any) {
                    Timber.d("archiveItemClick $objects")
                    navController.navigate(
                        R.id.action_clubBoardManagementFragment_to_clubBoardSettingFragment,
                        bundleOf(
                            KEY_UID to uid,
                            ConstVariable.ClubDef.KEY_CLUB_INFO_CLUBID to clubId,
                            KEY_BOARDSETTING_TYPE to BoardSetType.MODIFY,
                            KEY_CATEGORY_SUBITEM to (objects as ClubSubCategoryItem).toClubSubCategoryItemPacerable()
                        )
                    )
                }
            })

            binding.tvArchiveEditDone.isEnabled = false
            val swipeHelperCallback = SwipeHelperCallback(archiveAdapter)
            itemTouchHelper = ItemTouchHelper(swipeHelperCallback)
            itemTouchHelper!!.attachToRecyclerView(binding.rvArchive)
        }

        clubBoardManagementViewModel.networkErrorLiveData.observe(viewLifecycleOwner){
            showDialog(getString(R.string.alert_network_error))
        }

        clubBoardManagementViewModel.categoryListOrderSetResultLiveData.observe(viewLifecycleOwner) { response ->
            when (response.code) {
                ConstVariable.RESULT_SUCCESS_CODE -> {

                }
                else -> {
                    requireContext().showErrorSnackBar(binding.root, response.code)
                }
            }
        }

        clubBoardManagementViewModel.getCategoryListResultLiveData.observe(viewLifecycleOwner) { categoryListResponse ->
            categoryListResponse?.let {
                clubBoardManagementViewModel.currentCategoryListLiveData =
                    categoryListResponse.categoryList
                freeboardCategoryItemPacerable =
                    categoryListResponse.categoryList.find { it.categoryCode.contains("freeboard") }
                        ?.toClubCategoryItemPacerable()
                val archiveParentObj =
                    categoryListResponse.categoryList.filter { !"freeboard".contains(it.categoryCode) }
                val archiveCategoryList = archiveParentObj[0].categoryList
                (binding.rvArchive.adapter as ArchiveBoardAdapter).setItemList(archiveCategoryList)
                binding.tvArchiveEditDone.isEnabled =
                    archiveCategoryList.isNotEmpty() && archiveCategoryList.size > 1
            }
        }

        clubBoardManagementViewModel.getSettingCategoryList(clubId, uid)
    }

    override fun initUiActionEvent() {
        binding.topbar.setOnBackButtonClickListener {
            navController.popBackStack()
        }

        binding.tvArchiveEditDone.setOnClickListener {
            if (isArchiveEditMode) {
                //게시판 순서 저장
                //clubBoardManagementViewModel.setBoardList((binding.rvArchive.adapter as ArchiveBoardAdapter).getItemList().map { it.copy() })
                val itemsList = clubBoardManagementViewModel.currentCategoryListLiveData

                val archiveParentObj =
                    itemsList.filter { !"freeboard".contains(it.categoryCode) }
                val categoryList =
                    (binding.rvArchive.adapter as ArchiveBoardAdapter).getItemList()
                clubBoardManagementViewModel.setCategoryListOrder(
                    clubId, archiveParentObj[0].categoryCode,
                    hashMapOf(
                        "categoryNameList" to categoryList.map { it.categoryName },
                        KEY_UID to uid
                    )
                )
            }
            updateArchiveEditMode(!isArchiveEditMode)
        }

        //편집취소
        binding.tvArchiveEditCancel.setOnClickListener {
            //이전 리스트상태로 복구
            val itemsList = clubBoardManagementViewModel.currentCategoryListLiveData
            val archiveParentObj =
                itemsList.filter { !"freeboard".contains(it.categoryCode) }
            val archiveCategoryList = archiveParentObj[0].categoryList
            (binding.rvArchive.adapter as ArchiveBoardAdapter).setItemList(archiveCategoryList)
            updateArchiveEditMode(false)
        }

        //자유게시판(이름고정)
        binding.rlFreeBoard.setOnClickListener {
            navController.navigate(
                R.id.action_clubBoardManagementFragment_to_clubBoardSettingFragment, bundleOf(
                    KEY_UID to uid,
                    ConstVariable.ClubDef.KEY_CLUB_INFO_CLUBID to clubId,
                    KEY_BOARDSETTING_TYPE to BoardSetType.FREEBOARD,
                    KEY_CLUB_CATEGORY_ITEM to freeboardCategoryItemPacerable
                )
            )
        }

        //게시판 추가
        binding.btnAddArchive.setOnClickListener {
            navController.navigate(
                R.id.action_clubBoardManagementFragment_to_clubBoardSettingFragment, bundleOf(
                    KEY_UID to uid,
                    ConstVariable.ClubDef.KEY_CLUB_INFO_CLUBID to clubId,
                    KEY_BOARDSETTING_TYPE to BoardSetType.ADD,
                    KEY_CATEGORY_SUBITEM to null
                )
            )
        }
    }

    private fun updateArchiveEditMode(isEditMode: Boolean) {
        binding.tvArchiveEditDone.text =
            if (isEditMode) getString(R.string.a_done) else getString(R.string.p_edit)
        binding.tvArchiveEditCancel.visibility = if (isEditMode) View.VISIBLE else View.GONE
        binding.btnAddArchive.visibility = if (isEditMode) View.GONE else View.VISIBLE
        (binding.rvArchive.adapter as ArchiveBoardAdapter).setViewEditMode(isEditMode)
        isArchiveEditMode = isEditMode
    }

    companion object {
        const val KEY_BOARDSETTING_TYPE = "boardSetType"
        const val KEY_CATEGORY_ITEM = "clubCategoryItem"
        const val KEY_CATEGORY_SUBITEM = "clubSubCategoryItem"
    }
}

enum class BoardSetType {
    FREEBOARD,
    ADD,
    MODIFY
}