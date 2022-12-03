package com.rndeep.fns_fantoo.ui.club.detail

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.size
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.model.ClubBasicInfo
import com.rndeep.fns_fantoo.data.remote.model.ClubPagePostListType
import com.rndeep.fns_fantoo.data.remote.model.clubsetting.ClubDelegatingInfoDto
import com.rndeep.fns_fantoo.databinding.FragmentClubDetailPageBinding
import com.rndeep.fns_fantoo.ui.club.detail.archive.ClubPageArchiveFragment
import com.rndeep.fns_fantoo.ui.club.detail.freeboard.ClubPageFreeBoardFragment
import com.rndeep.fns_fantoo.ui.club.detail.home.ClubPageHomeFragment
import com.rndeep.fns_fantoo.ui.common.BaseFragment
import com.rndeep.fns_fantoo.ui.common.dialog.CommonDialog
import com.rndeep.fns_fantoo.ui.editor.EditorInfo
import com.rndeep.fns_fantoo.ui.editor.EditorType
import com.rndeep.fns_fantoo.utils.*
import com.rndeep.fns_fantoo.utils.ConstVariable.ClubDetail.Companion.KEY_CATEGORY_CODE
import com.rndeep.fns_fantoo.utils.ConstVariable.ClubDetail.Companion.KEY_DETAIL_CLUB_ID
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Math.abs

@AndroidEntryPoint
class ClubPageFragment :
    BaseFragment<FragmentClubDetailPageBinding>(FragmentClubDetailPageBinding::inflate) {

    private val clubPageViewModel: ClubPageViewModel by viewModels()

    private val clubPageArg : ClubPageFragmentArgs by navArgs()

    //viewpager어댑터
    private lateinit var vpFragmentAdapter: ClubPageFragmentAdapter
    private val fragmentItem = ArrayList<ClubPagePostListType>()

    private lateinit var tabItemText: List<String>

    //클럽 가입 여부
    private var isClubJoin = false

    //클럽멤버Id
    private var myMemberId:Int? = 0

    //클럽 정보
    private var clubInfoData: ClubBasicInfo? = null

    //클럽 정보 다이얼로그
    private var infoDialog : DialogFragment? =null

    //멤버 레벨
    private var myMemberLevel: Int? = null

    override fun onResume() {
        super.onResume()
        settingBackPressFunction()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun initUi() {
        this.activity?.setStatusBarTransparent()
        initToolbar()
        settingViewSize()
        settingObserver()

        clubPageViewModel.getClubTopInfoItems(clubPageArg.clubId)
    }

    override fun initUiActionEvent() {
        clickFunc()
    }

    private fun initToolbar() {
        binding.clubDetailAppbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val percentage = abs(verticalOffset).toFloat() / appBarLayout!!.totalScrollRange
            binding.rlTopBarContainer.alpha = 1 - percentage
            if (abs(verticalOffset) - binding.clubDetailAppbar.totalScrollRange == 0) {
                //collpasing
                binding.collapsingToolbar.title = clubInfoData?.clubName?:""
            } else if (verticalOffset == 0) {
                // Expanded
                binding.collapsingToolbar.title = ""
            } else {
                // Idle
                binding.collapsingToolbar.title = ""
            }
        })

        binding.detailToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                //검색
                R.id.menu_club_detail_search -> {
                    findNavController().navigate(ClubPageFragmentDirections.actionClubDetailPageFragmentToClubDetailSearchFragment(clubPageArg.clubId))
                    true
                }
                //셋팅
                R.id.menu_club_detail_setting -> {
                    if(isClubJoin) {
                        findNavController().navigate(
                            ClubPageFragmentDirections.actionClubDetailPageFragmentToClubSettings(
                                clubId = clubPageArg.clubId,
                                myMemberId = myMemberId.toString()
                            )
                        )
                    }else{
                        activity?.showErrorSnackBar(binding.root,"FE3000")
                    }
                    true
                }
                else -> false
            }
        }
        //뒤로가기
        binding.detailToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }



    private fun clickFunc() {
//        //게시글 작성 FAB 버튼
        binding.fabClubPost.setOnClickListener {
            //비회원 클릭시
            if(isClubJoin){
                val editorInfo =
                    EditorInfo(EditorType.CLUB, clubPageArg.clubId.toString(), myMemberLevel)
                val directions = ClubPageFragmentDirections.actionClubDetailPageFragmentToEditorFragment(editorInfo)
                findNavController().navigate(directions)
            }else{
                activity?.showCustomSnackBar(binding.totalContainer,getString(R.string.se_k_can_to_write_after_join_club))
            }
        }

        //클럽 가입하기
        binding.tvClubJoin.setOnClickListener {
            if(!clubPageViewModel.isLogin()){
                activity?.showCustomSnackBar(binding.root,getString(R.string.error_code_2000))
                return@setOnClickListener
            }
            if(clubInfoData!!.clubJoinState==2){
                activity?.showCustomSnackBar(binding.root,getString(R.string.Error_FE3007))
                return@setOnClickListener
            }
            findNavController().navigate(
                ClubPageFragmentDirections.actionClubDetailPageFragmentToClubJoinFragment(
                    clubPageArg.clubId
                )
            )
        }

        binding.llClubInfoContainer.setOnClickListener {
            clubInfoData?.let {
                val dialog =
                    ClubPageInfoDialog(it, object : ClubPageInfoDialog.ClubInfoDialogClickListener {
                        override fun onBookMarkClick(dialog: DialogFragment) {
                            infoDialog=dialog
                            clubPageViewModel.patchClubFavoriteState(clubPageArg.clubId.toString())
                        }

                        override fun onShareClick() {

                        }
                    })
                dialog.show(parentFragmentManager, "clubInfo")
            }
        }
    }

    private fun settingObserver() {
        //클럽 페이지 상단 아이템
        clubPageViewModel.clubTopInfoLiveData.observe(this) {
            if(it==null){
                activity?.showCustomSnackBar(binding.root,"")
                findNavController().popBackStack()
                return@observe
            }
            myMemberLevel=it.memberLevel
            clubInfoData = it
            binding.tvClubName.text = it.clubName

            setImageWithPlaceHolder(
                binding.ivClubThumbnail,
                context?.getString(R.string.imageThumbnailUrl,it.profileImg),
                R.drawable.profile_character10
                )

            settingViewTextColors(requireContext().getColor(R.color.gray_25))
            Glide.with(this)
                .load(context?.getString(R.string.imageUrlBase,it.bgImg))
                .into(object : CustomTarget<Drawable>(){
                    override fun onResourceReady(
                        resource: Drawable,
                        transition: Transition<in Drawable>?
                    ) {
                        binding.clubDetailAppbar.background=resource
                    }
                    override fun onLoadCleared(placeholder: Drawable?) {
                    }
                })

            settingClubMemberCount(it.memberCount,it.memberCountOpenYn)

            binding.tvClubInfoText.text = it.introduction

            //클럽 참여 여부를 받고 fragment 에 셋팅 후 adapter에 set을 해줘야
            //첫 Home 카테고리도 createView 에서 정삭적인 동작을 한다 .
            myMemberId = it.memberId
            isClubJoin = it.clubJoinState==1
            if(!isClubJoin){
                binding.fabClubPost.visibility=View.GONE
            }else{
                binding.fabClubPost.visibility=View.VISIBLE
            }
            settingClubJoinView(it.clubJoinState)
            if(binding.clubDetailTab.tabCount<1){
                clubPageViewModel.getClubCategory(clubPageArg.clubId)
            }
        }

        clubPageViewModel.clubDelegateDialog.observe(this){
            if(it.delegateStatus==1){
                showDelegateDialog(it,clubInfoData?.clubName?:"")
            }else{

            }
        }

        clubPageViewModel.clubImageRGBLevel.observe(this) {
            var rgbLevelColorFilter: Int? = null
            if (it < 0.5) {
                rgbLevelColorFilter = requireContext().getColor(R.color.gray_25)
                activity?.setWhiteStatusBarIcon()
            } else {
                rgbLevelColorFilter = requireContext().getColor(R.color.gray_900)
                activity?.setDarkStatusBarIcon()
            }
            settingViewTextColors(rgbLevelColorFilter)
        }

        clubPageViewModel.clubDetailErrorLiveData.observe(this){
            when(it){
                ConstVariable.ERROR_NO_CATEGORY_CLUB->{
                    activity?.showErrorSnackBar(binding.root,it)
                    findNavController().popBackStack()
                }
            }
        }

        clubPageViewModel.clubCategoryListLiveData.observe(this){
            binding.clubDetailTab.removeAllTabs()
            fragmentItem.clear()
            val tabText = arrayListOf<String>()
            var categoryPos =0
            it.forEachIndexed { index, categoryItem ->
                if(categoryItem.categoryCode =="home" ||
                    categoryItem.categoryCode =="freeboard"||
                    categoryItem.categoryCode =="archive"){
                    binding.clubDetailTab.addTab(binding.clubDetailTab.newTab().apply {
                        this.text=categoryItem.categoryName
                    })
                    tabText.add(categoryItem.categoryName)
                }
                addClubCategoryFragment(categoryItem.categoryCode,clubPageArg.clubId.toString())
                if(arguments?.get("clubCategoryCode").toString().contains(categoryItem.categoryCode)){
                    categoryPos=index
                }
            }
            tabItemText=tabText

            if (binding.vpClubPagePostList.adapter == null) {
                //viewpager 아이템
                vpFragmentAdapter = ClubPageFragmentAdapter(this.childFragmentManager, lifecycle)
                vpFragmentAdapter.setFragmentItem(fragmentItem)
                binding.vpClubPagePostList.adapter = vpFragmentAdapter
                TabLayoutMediator(
                    binding.clubDetailTab,
                    binding.vpClubPagePostList
                ) { tab, pos -> tab.text=tabItemText[pos]  }.attach()
            }
            if(arguments?.get("clubCategoryCode") !=null){
                binding.clubDetailTab.selectTab(binding.clubDetailTab.getTabAt(categoryPos))
                when(val fg =fragmentItem[categoryPos].fragment){
                    is ClubPageFreeBoardFragment->{
                        fg.setDetailCategoryCode(arguments?.get("clubCategoryCode").toString())
                    }
                    is ClubPageArchiveFragment->{
                        fg.setDetailCategoryCode(arguments?.get("clubCategoryCode").toString())
                    }
                }
                arguments?.remove("clubCategoryCode")
            }
        }

        clubPageViewModel.clubFavoriteChangeLiveData.observe(this){
            infoDialog?:return@observe
            if(infoDialog is ClubPageInfoDialog){
                (infoDialog as ClubPageInfoDialog).changeFavoriteIcon(it)
            }
            infoDialog=null
        }

        clubPageViewModel.delegateState.observe(this){
            if(it){
                CommonDialog.Builder().apply {
                    message=getString(R.string.se_a_delegating_agree_with_arg, clubPageViewModel.delegateCompleteDate)
                    positiveButtonString=getString(R.string.h_confirm)
                }.build().show(parentFragmentManager,"confimDialog")
                clubPageViewModel.delegateCompleteDate=null
            }else{

            }
        }
    }
    //뷰 사이즈 조절
    private fun settingViewSize(){
        //상태바 설정
        binding.clubDetailAppbar.setPadding(
            0, requireContext().statusBarHeight(), 0, 0
        )

        (binding.clubDetailAppbar.layoutParams as CoordinatorLayout.LayoutParams).height =
            binding.clubDetailAppbar.layoutParams.height + requireContext().statusBarHeight()

        activity?.let {
            if(it.hasSoftKeys()){
                binding.totalContainer.setPadding(
                    0, 0, 0, requireContext().navigationHeight()
                )
            }
        }
    }

    //프래그먼트 셋팅
    private fun addClubCategoryFragment(categoryCode: String, clubId:String) {
        when (categoryCode) {
            "home" -> {
                fragmentItem.add(
                    ClubPagePostListType("Home", ClubPageHomeFragment().apply {
                        this.arguments = Bundle().apply {
                            putString(KEY_DETAIL_CLUB_ID, clubId)
                            putString(KEY_CATEGORY_CODE, categoryCode)
                        }
                    })
                )
            }
            "freeboard" -> {
                fragmentItem.add(
                    ClubPagePostListType("FreeBoard", ClubPageFreeBoardFragment().apply {
                        this.arguments = Bundle().apply {
                            putString(KEY_DETAIL_CLUB_ID, clubId)
                            putString(KEY_CATEGORY_CODE, categoryCode)
                        }
                    })
                )
            }
            "archive" -> {
                fragmentItem.add(
                    ClubPagePostListType("Archive", ClubPageArchiveFragment().apply {
                        this.arguments = Bundle().apply {
                            putString(KEY_DETAIL_CLUB_ID, clubId)
                            putString(KEY_CATEGORY_CODE, categoryCode)
                        }
                    })
                )
            }
//            추후 업데이될 기능 페이지
//            "safe" -> {
//                fragmentItem.add(
//                    ClubPagePostListType("SafeWallet", ClubPageMoneyboxFragment().apply {
//                        this.arguments = Bundle().apply {
//                            putString(KEY_DETAIL_CLUB_ID, clubId)
//                            putString(KEY_CATEGORY_CODE, categoryCode)
//                        }
//                    })
//                )
//            }
        }
    }

    private fun settingClubMemberCount(memberCount :Int,isMemberCountOpen :Boolean){
        if(!isMemberCountOpen){
            binding.tvClubMemberInfo.text=getString(R.string.m_membercount_with_2arg,getString(R.string.b_hidden),"")
            return
        }
        val countVariationText = when {
            memberCount >= 10000 -> {
                requireContext().getString(R.string.m_count_ten_thousand)
            }
            memberCount >= 1000 -> {
                requireContext().getString(R.string.c_count_thousand)
            }
            else -> {
                requireContext().getString(R.string.m_people_count)
            }
        }
        val changeCount = when {
            memberCount > 1000 -> {
                StringBuilder(memberCount.toString()).insert(1, ".").substring(0, 3)
            }
            else -> {
                memberCount.toString()
            }
        }

        binding.tvClubMemberInfo.text = requireContext().getString(
            R.string.m_membercount_with_2arg,
            changeCount,
            countVariationText
        )
    }
    private lateinit var backPressCallBack: OnBackPressedCallback
    private fun settingBackPressFunction(){
        backPressCallBack = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                CommonDialog.Builder().apply {
                    message=getString(R.string.se_k_leave_from_club)
                    negativeButtonString=getString(R.string.a_no)
                    positiveButtonString=getString(R.string.a_yes)
                    positiveButtonClickListener=object :CommonDialog.ClickListener{
                        override fun onClick() {
                            findNavController().popBackStack()
                        }
                    }
                }.build().show(parentFragmentManager,"quitDialog")
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, backPressCallBack)
    }

    private fun settingViewTextColors(rgbColor :Int){
        binding.tvClubName.setTextColor(rgbColor)
        binding.tvClubMemberInfo.setTextColor(rgbColor)
        binding.tvClubInfoText.setTextColor(rgbColor)
        binding.ivInformation.setColorFilter(rgbColor)
        binding.collapsingToolbar.setCollapsedTitleTextColor(rgbColor)
        binding.collapsingToolbar.setExpandedTitleColor(rgbColor)
        binding.detailToolbar.setNavigationIconTint(rgbColor)
        val menuIconColor = PorterDuffColorFilter(rgbColor, PorterDuff.Mode.SRC_IN)
        for (a in 0 until binding.detailToolbar.menu.size) {
            binding.detailToolbar.menu.getItem(a).icon.setColorFilter(menuIconColor)
        }
    }

    private fun showDelegateDialog(clubDelegateInfo :ClubDelegatingInfoDto,clubName :String){
        ClubMasterDelegateDialog(
            clubDelegateInfo,
            clubName,
            object :ClubMasterDelegateDialog.DelegateClickListener{
                override fun onAcceptDelegate(completeDate: String) {
                    clubPageViewModel.choiceClubMasterDelegate(clubPageArg.clubId.toString(),true)
                    clubPageViewModel.delegateCompleteDate=completeDate
                }
                override fun onRejectDelegate() {
                   clubPageViewModel.choiceClubMasterDelegate(clubPageArg.clubId.toString(),false)
                }
            }
        ).show(parentFragmentManager,"clubDelegate")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        backPressCallBack.remove()
        activity?.setStatusBarBack()
    }

    private fun settingClubJoinView(joinState :Int) {//클럽 가입 여부 (0:비회원, 1:가입, 2:가입 요청, 3:가입 불가)
        when(ConstVariable.ClubJoinStatus.compare(joinState)){
            ConstVariable.ClubJoinStatus.NOT_MEMBER ->{
                binding.tvClubJoin.visibility = View.VISIBLE
            }
            ConstVariable.ClubJoinStatus.JOINED ->{
                binding.tvClubJoin.visibility = View.GONE
            }
            ConstVariable.ClubJoinStatus.REQUESTING_JOIN ->{
                binding.tvClubJoin.visibility = View.VISIBLE
                binding.tvClubJoin.text = getString(R.string.g_to_wait_join)
            }
            ConstVariable.ClubJoinStatus.BLOCKED_JOIN ->{
                binding.tvClubJoin.visibility = View.VISIBLE
                binding.tvClubJoin.isEnabled = false
                binding.tvClubJoin.text = getString(R.string.k_blocked_join_to_club_by_manager)
                binding.tvClubJoin.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_border_radius_100_darkblue25)
                binding.tvClubJoin.backgroundTintList = ColorStateList.valueOf(requireContext().getColor(R.color.state_disabled_gray_200))
            }
        }
    }

}