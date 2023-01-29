package com.rndeep.fns_fantoo.ui.club.settings

import android.graphics.Rect
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.dto.ClubAllInfoPacerable
import com.rndeep.fns_fantoo.data.remote.dto.ClubInterestCategoryDto
import com.rndeep.fns_fantoo.databinding.FragmentClubSettingCategoryBinding
import com.rndeep.fns_fantoo.ui.club.settings.adapter.ClubCategoryAdapter
import com.rndeep.fns_fantoo.ui.club.settings.viewmodel.ClubCategoryViewModel
import com.rndeep.fns_fantoo.utils.ConstVariable
import com.rndeep.fns_fantoo.utils.ConstVariable.KEY_UID
import com.rndeep.fns_fantoo.utils.SizeUtils
import com.rndeep.fns_fantoo.utils.showCustomSnackBar
import com.rndeep.fns_fantoo.utils.showErrorSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class ClubSettingCategoryFragment :
    ClubSettingBaseFragment<FragmentClubSettingCategoryBinding>(FragmentClubSettingCategoryBinding::inflate) {

    companion object {
        private const val GRID_COLUM_COUNT = 3
    }

    private val clubCategoryViewModel: ClubCategoryViewModel by viewModels()

    private lateinit var clubId: String
    private lateinit var uid: String
    private var clubAllInfo: ClubAllInfoPacerable? = null

    override fun initUi() {
        val args: ClubSettingCategoryFragmentArgs by navArgs()
        clubId = args.clubId
        uid = args.integUid.toString()
        clubAllInfo = args.clubAllInfo

        clubCategoryViewModel._currentCategoryCode.value = clubAllInfo?.interestCategoryCode

        clubCategoryViewModel.networkErrorLiveData.observe(viewLifecycleOwner) {
            showDialog(getString(R.string.alert_network_error))
        }

        clubCategoryViewModel.loadingStatusLiveData.observe(viewLifecycleOwner) { loadingState ->
            setLoadingView(loadingState)
        }

        clubCategoryViewModel.categoryListLiveData.observe(viewLifecycleOwner) { categoryListResultResponse ->
            categoryListResultResponse?.let {
                when (categoryListResultResponse.code) {
                    ConstVariable.RESULT_SUCCESS_CODE -> {
                        categoryListResultResponse.dataObj?.let {
                            (binding.rcCategory.adapter as ClubCategoryAdapter).replaceTo(it.clubInterestCategoryDtoList)
                        }
                    }
                    else ->{
                        requireContext().showErrorSnackBar(binding.root, categoryListResultResponse.code)
                    }
                }
            }
        }

        clubCategoryViewModel.saveCategoryResultLiveData.observe(viewLifecycleOwner) { clubAllInfoResponse ->
            clubAllInfoResponse?.let {
                when (clubAllInfoResponse.code) {
                    ConstVariable.RESULT_SUCCESS_CODE -> {
                        context?.showCustomSnackBar(binding.root, getString(R.string.se_j_done_save))
                        lifecycleScope.launch {
                            delay(500)
                            navController.popBackStack()
                        }
                    }
                    else ->{
                        requireContext().showErrorSnackBar(binding.root, clubAllInfoResponse.code)
                    }
                }
            }
        }

        binding.commonTopbar.setTailTextEnable(false)

        val clubCategoryAdapter = ClubCategoryAdapter { categoryInfo: ClubInterestCategoryDto ->
            onItemSelected(
                categoryInfo
            )
        }
        clubAllInfo?.interestCategoryCode?.let {
            try {
                clubCategoryAdapter.setSelectedInterestCategoryCode(it)
            }catch (e:Exception){
                Timber.e("${e.printStackTrace()}")
            }
        }
        binding.rcCategory.adapter = clubCategoryAdapter
        binding.rcCategory.layoutManager = GridLayoutManager(context, GRID_COLUM_COUNT)
        binding.rcCategory.addItemDecoration(
            ItemDividerGrid(
                GRID_COLUM_COUNT, 10f,
                10f, 0f, 0f
            )
        )
        clubCategoryViewModel.getInterestCategoryList()
    }

    override fun initUiActionEvent() {
        binding.commonTopbar.setOnBackButtonClickListener {
            navController.popBackStack()
        }
        binding.commonTopbar.setTailTextClickListener {
            //카테고리 설정 저장처리
            clubCategoryViewModel.saveCategory(
                clubId,
                hashMapOf(
                    ConstVariable.ClubDef.KEY_CLUB_INFO_INTEREST_CATEGORY_CODE to (binding.rcCategory.adapter as ClubCategoryAdapter).getSelectedInterestCategoryCode(),
                    KEY_UID to uid
                )
            )
        }
    }

    private fun onItemSelected(categoryInfo: ClubInterestCategoryDto) {
        binding.commonTopbar.setTailTextEnable(clubCategoryViewModel.currentCategoryCode.value != categoryInfo.clubInterestCategoryId.toString())
    }

    private fun setLoadingView(isShow: Boolean) {
        binding.loadingView.visibility = if (isShow) View.VISIBLE else View.GONE
    }

    class ItemDividerGrid(
        private val numberOfColumns: Int, private val rowSpacingDP: Float = 0f,
        private val columnSpacingDP: Float = 0f, private val edgeSpacingVerticalDP: Float = 0f,
        private val edgeSpacingHorizontalDP: Float = 0f
    ) : RecyclerView.ItemDecoration() {

        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            val position = parent.getChildAdapterPosition(view)
            val numberOfRows = (parent.adapter?.itemCount ?: -1) / numberOfColumns
            val column = position % numberOfColumns
            val row = position / numberOfColumns
            val context = view.context
            ///horizontal
            when (column) {
                0 -> {
                    outRect.left = SizeUtils.getDpValue(edgeSpacingVerticalDP, context).toInt()
                    outRect.right = SizeUtils.getDpValue(columnSpacingDP / 2, context).toInt()
                }
                numberOfColumns - 1 -> {
                    outRect.left = SizeUtils.getDpValue(columnSpacingDP / 2, context).toInt()
                    outRect.right = SizeUtils.getDpValue(edgeSpacingVerticalDP, context).toInt()
                }
                else -> {
                    outRect.left = SizeUtils.getDpValue(columnSpacingDP / 4, context).toInt()
                    outRect.right = SizeUtils.getDpValue(columnSpacingDP / 4, context).toInt()
                }
            }
            //vertical
            when (row) {
                0 -> {
                    outRect.top = SizeUtils.getDpValue(edgeSpacingHorizontalDP, context).toInt()
                    outRect.bottom = SizeUtils.getDpValue(rowSpacingDP / 2, context).toInt()
                }
                numberOfRows -> {
                    outRect.top = SizeUtils.getDpValue(rowSpacingDP / 2, context).toInt()
                    outRect.bottom = SizeUtils.getDpValue(edgeSpacingHorizontalDP, context).toInt()
                }
                else -> {
                    outRect.top = SizeUtils.getDpValue(rowSpacingDP / 4, context).toInt()
                    outRect.bottom = SizeUtils.getDpValue(rowSpacingDP / 4, context).toInt()
                }
            }
        }
    }


}