package com.rndeep.fns_fantoo.ui.club.settings.management

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.model.ClubCategoryItemPacerable
import com.rndeep.fns_fantoo.data.remote.model.ClubSubCategoryItemPacerable
import com.rndeep.fns_fantoo.ui.common.RecyclerViewItemClickListener
import com.rndeep.fns_fantoo.databinding.FragmentClubSettingBoardSetBinding
import com.rndeep.fns_fantoo.ui.common.dialog.CommonDialog
import com.rndeep.fns_fantoo.ui.club.settings.ClubSettingBaseFragment
import com.rndeep.fns_fantoo.ui.club.settings.MenuBottomSheetDialogFragment
import com.rndeep.fns_fantoo.ui.club.settings.data.MenuItem
import com.rndeep.fns_fantoo.ui.club.settings.management.viewmodel.ClubBoardSettingViewModel
import com.rndeep.fns_fantoo.utils.ConstVariable
import com.rndeep.fns_fantoo.utils.ConstVariable.ClubDef.Companion.CLUB_BOARD_NAME_LENGTH_LIMIT
import com.rndeep.fns_fantoo.utils.ConstVariable.ClubDef.Companion.KEY_CLUB_CATEGORY_ITEM
import com.rndeep.fns_fantoo.utils.ConstVariable.ClubDef.Companion.KEY_CLUB_INFO_CLUBID
import com.rndeep.fns_fantoo.utils.ConstVariable.KEY_UID
import com.rndeep.fns_fantoo.utils.TextUtils
import com.rndeep.fns_fantoo.utils.showCustomSnackBar
import com.rndeep.fns_fantoo.utils.showErrorSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class ClubBoardSettingFragment :
    ClubSettingBaseFragment<FragmentClubSettingBoardSetBinding>(FragmentClubSettingBoardSetBinding::inflate) {

    private val clubBoardSettingViewModel: ClubBoardSettingViewModel by viewModels()

    companion object {
        const val BOARD_TYPE_IMAGE = 2
        const val BOARD_TYPE_NORMAL = 1
    }

    private lateinit var menuBottomSheetDialogFragment: MenuBottomSheetDialogFragment

    private lateinit var clubId: String
    private lateinit var uid: String
    private lateinit var boardSetType: BoardSetType
    private var categoryItem: ClubCategoryItemPacerable? = null
    private var categorySubItem: ClubSubCategoryItemPacerable? = null
    private var boardType = BOARD_TYPE_IMAGE

    override fun initUi() {
        val args: ClubBoardSettingFragmentArgs by navArgs()
        clubId = args.clubId
        uid = args.integUid.toString()
        categoryItem = args.clubCategoryItem
        boardSetType = args.boardSetType
        categorySubItem = args.clubSubCategoryItem
        /*boardSetType = arguments?.get(ClubBoardManagementFragment.KEY_BOARDSETTING_TYPE)
                as ClubBoardManagementFragment.Companion.BoardSetType*/
        when (boardSetType) {
            BoardSetType.FREEBOARD -> {
                binding.topbar.setTitle(getString(R.string.j_free_board))
                binding.etBoardName.setText(getString(R.string.j_free_board))
                binding.etBoardName.isClickable = false
                binding.etBoardName.isFocusable = false
                binding.rlBoardPublicSet.isClickable = false
                binding.rlBoardPublicSet.isEnabled = false
                clubBoardSettingViewModel.getSettingCategoryList(clubId, uid)
            }
            BoardSetType.MODIFY -> {
                categorySubItem?.let {
                    val boardName = categorySubItem!!.categoryName
                    boardName?.let {
                        binding.topbar.setTitle(boardName)
                        binding.etBoardName.setText(boardName)
                        binding.btnDelete.visibility = View.VISIBLE
                        binding.tvBoardPublic.text =
                            if (categorySubItem!!.openYn) getString(R.string.j_total_visibility) else getString(
                                R.string.m_member_revealed
                            )
                        binding.tvArchiveType.text =
                            if (categorySubItem!!.boardType == BOARD_TYPE_NORMAL) getString(R.string.a_general) else getString(
                                R.string.a_image
                            )
                    }
                    clubBoardSettingViewModel._categoryOpenYnLiveData.value =
                        categorySubItem!!.openYn
                }
            }
            BoardSetType.ADD -> {
                binding.topbar.setTitle(getString(R.string.a_add_archive_board))
                clubBoardSettingViewModel._categoryOpenYnLiveData.value = true
            }
        }

        binding.topbar.setTailTextEnable(false)
        setSubMenu(boardSetType == BoardSetType.FREEBOARD)
        updateBoardNameLengthCountText()

        clubBoardSettingViewModel.networkErrorLiveData.observe(viewLifecycleOwner){
            showDialog(getString(R.string.alert_network_error))
        }

        clubBoardSettingViewModel.boardListLiveData.observe(viewLifecycleOwner) { categoryListResponse ->
            categoryListResponse?.let {
                categoryItem =
                    categoryListResponse.categoryList.find { it.categoryCode.contains("freeboard") }
                        ?.toClubCategoryItemPacerable()
            }
        }

        clubBoardSettingViewModel.createCategoryResultLiveData.observe(viewLifecycleOwner) { result ->
            result?.let {
                when (result.code) {
                    ConstVariable.RESULT_SUCCESS_CODE -> {
                        lifecycleScope.launch {
                            hideSoftInput(binding.etBoardName)
                            context?.showCustomSnackBar(
                                binding.root,
                                getString(R.string.se_s_create_completed)
                            )
                            delay(500)
                            navController.popBackStack()
                        }
                    }
                    else -> {
                        requireContext().showErrorSnackBar(binding.root, result.code)
                    }
                }
            }
        }

        clubBoardSettingViewModel.modifyCategoryResultLiveData.observe(viewLifecycleOwner) { result ->
            Timber.d("modifyCategoryResultLiveData $result")
            lifecycleScope.launch {
                context?.showCustomSnackBar(binding.root, getString(R.string.se_j_done_save))
                delay(500)
                navController.popBackStack()
            }
        }
        clubBoardSettingViewModel.deleteSettingCategoryLiveData.observe(viewLifecycleOwner) { result ->
            when (result.code) {
                ConstVariable.RESULT_SUCCESS_CODE -> {
                    lifecycleScope.launch {
                        context?.showCustomSnackBar(binding.root, getString(R.string.a_done))
                        delay(500)
                        navController.popBackStack()
                    }
                }
                else ->{
                    requireContext().showErrorSnackBar(binding.root, result.code)
                }
            }
        }
    }

    override fun initUiActionEvent() {
        binding.topbar.setOnBackButtonClickListener {
            navController.popBackStack()
        }

        if (boardSetType == BoardSetType.FREEBOARD) {
            binding.topbar.setTailVisibility(View.GONE)
        }

        binding.topbar.setTailTextClickListener {
            when (boardSetType) {
                BoardSetType.ADD -> {
                    val unavailableMessage = checkingBoardName()
                    if (unavailableMessage.isNotEmpty()) {
                        showDialog(unavailableMessage)
                        return@setTailTextClickListener
                    }

                    clubBoardSettingViewModel.createSettingCategory(
                        clubId = clubId, categoryCode = "archive",
                        requestData = hashMapOf(
                            KEY_UID to uid,
                            "categoryName" to binding.etBoardName.text.toString(),
                            "openYn" to clubBoardSettingViewModel._categoryOpenYnLiveData.value!!,
                            "boardType" to boardType
                        )
                    )
                }
                BoardSetType.MODIFY -> {
                    val unavailableMessage = checkingBoardName()
                    if (unavailableMessage.isNotEmpty()) {
                        showDialog(unavailableMessage)
                        return@setTailTextClickListener
                    }
                    clubBoardSettingViewModel.modifySettingCategory(
                        clubId = clubId, categoryCode = categorySubItem!!.categoryCode,
                        hashMapOf(
                            KEY_UID to uid,
                            "categoryName" to binding.etBoardName.text.toString(),
                            "openYn" to clubBoardSettingViewModel._categoryOpenYnLiveData.value!!,
                            "boardType" to boardType
                        )
                    )
                }
                BoardSetType.FREEBOARD -> {

                }
            }
        }
        //말머리세팅
        binding.rlOpenningWordSet.setOnClickListener {
            navController.navigate(
                R.id.action_clubBoardSettingFragment_to_clubFreeBoardOpenWordSetFragment, bundleOf(
                    KEY_UID to uid,
                    KEY_CLUB_INFO_CLUBID to clubId,
                    KEY_CLUB_CATEGORY_ITEM to categoryItem
                )
            )
            binding.topbar.setTailTextEnable(true)
        }
        //게시판 공개 설정
        binding.rlBoardPublicSet.setOnClickListener {
            showPublicSetBottomSheet()
            binding.topbar.setTailTextEnable(true)
        }
        //아카이브 타입
        binding.rlArchiveType.setOnClickListener {
            showArchiveTypeSetBottomSheet()
            binding.topbar.setTailTextEnable(true)
        }

        //게시판 삭제
        binding.btnDelete.setOnClickListener {
            showDialog(
                getString(R.string.g_board_delete),
                getString(R.string.se_s_delete_board),
                "",
                getString(R.string.s_delete),
                getString(R.string.c_cancel),
                object : CommonDialog.ClickListener {
                    override fun onClick() {
                        clubBoardSettingViewModel.deleteSettingCategory(
                            clubId, categorySubItem!!.categoryCode, hashMapOf(
                                KEY_UID to uid
                            )
                        )
                    }
                },
                null
            )
        }
        binding.etBoardName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let {
                    binding.topbar.setTailTextEnable(true)
                    updateBoardNameLengthCountText()
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
    }

    @SuppressLint("SetTextI18n")
    private fun updateBoardNameLengthCountText() {
        if (binding.etBoardName.text.toString().isEmpty()) {
            binding.tvBoardNameCount.text =
                binding.etBoardName.text.toString().length.toString() +
                        getString(R.string.slash) +
                        CLUB_BOARD_NAME_LENGTH_LIMIT
        } else {
            context?.let {
                TextUtils.applyParticalColor(
                    binding.etBoardName.text.toString().length.toString(),
                    getString(R.string.slash) +
                            CLUB_BOARD_NAME_LENGTH_LIMIT,
                    it.getColor(R.color.primary_default), binding.tvBoardNameCount, true
                )
            }
        }
    }

    private fun setSubMenu(isFreeboard: Boolean) {
        binding.rlOpenningWordSet.visibility = if (isFreeboard) View.VISIBLE else View.GONE
        binding.rlBoardPublicSet.isClickable = !isFreeboard
        binding.rlArchiveType.visibility = if (isFreeboard) View.GONE else View.VISIBLE
    }

    private fun showPublicSetBottomSheet() {
        val menuItemArrayList = ArrayList<MenuItem>()
        menuItemArrayList.add(
            MenuItem(
                0,
                getString(R.string.j_total_visibility),
                getString(R.string.se_p_visibility_for_all)
            )
        )
        menuItemArrayList.add(
            MenuItem(
                1,
                getString(R.string.m_member_revealed),
                getString(R.string.se_k_visibility_only_members)
            )
        )
        val currentMenu =
            if (binding.tvBoardPublic.text == getString(R.string.j_total_visibility)) menuItemArrayList[0] else menuItemArrayList[1]
        menuBottomSheetDialogFragment = MenuBottomSheetDialogFragment(menuItemArrayList)
        menuBottomSheetDialogFragment.title = getString(R.string.g_board_public_settings)
        menuBottomSheetDialogFragment.currentMenuItem = currentMenu
        menuBottomSheetDialogFragment.setOnMenuItemClickListener(object :
            RecyclerViewItemClickListener {
            override fun onItemClick(objects: Any) {
                val selectedMenuItem = objects as MenuItem
                Timber.d("club public/private set select item = ${selectedMenuItem.title1}")
                clubBoardSettingViewModel._categoryOpenYnLiveData.value = selectedMenuItem.id == 0
                binding.tvBoardPublic.text =
                    if (selectedMenuItem.id == 0) getString(R.string.j_total_visibility) else getString(R.string.m_member_revealed)
            }
        })
        menuBottomSheetDialogFragment.show(requireActivity().supportFragmentManager, "")
    }

    private fun showArchiveTypeSetBottomSheet() {
        val menuItemArrayList = ArrayList<MenuItem>()
        menuItemArrayList.add(MenuItem(0, getString(R.string.a_image), ""))
        menuItemArrayList.add(
            MenuItem(
                1,
                getString(R.string.a_general),
                getString(R.string.d_move_weblink_text)
            )
        )
        val currentMenu = if(binding.tvArchiveType.text == getString(R.string.a_image))menuItemArrayList[0] else menuItemArrayList[1]
        menuBottomSheetDialogFragment = MenuBottomSheetDialogFragment(menuItemArrayList)
        menuBottomSheetDialogFragment.title = getString(R.string.a_select_archive_type)
        menuBottomSheetDialogFragment.currentMenuItem = currentMenu
        menuBottomSheetDialogFragment.setOnMenuItemClickListener(object :
            RecyclerViewItemClickListener {
            override fun onItemClick(objects: Any) {
                val selectedMenuItem = objects as MenuItem
                Timber.d("club archive type set select item = ${selectedMenuItem.title1}")
                boardType = if (selectedMenuItem.id == 0) BOARD_TYPE_IMAGE else BOARD_TYPE_NORMAL
                binding.tvArchiveType.text =
                    if (selectedMenuItem.id == 0) getString(R.string.a_image) else getString(R.string.a_general)
            }
        })
        menuBottomSheetDialogFragment.show(requireActivity().supportFragmentManager, "")
    }

    private fun checkingBoardName(): String {
        var resultMessage = ""
        val boardName = binding.etBoardName.text.toString()
        if (boardName.isNotEmpty() && (boardName[0] == ' ')) {
            resultMessage = getString(R.string.se_c_cannot_use_blank_first_character)
        }
        //사용할수 없는 문자?
        return resultMessage
    }

}