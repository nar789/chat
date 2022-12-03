package com.rndeep.fns_fantoo.ui.home

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.viewpager2.widget.ViewPager2
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.FragmentHomeTabBinding
import com.rndeep.fns_fantoo.ui.home.tabhome.HomeFragment
import com.rndeep.fns_fantoo.ui.home.tabpopular.PopularFragment
import com.rndeep.fns_fantoo.ui.common.BaseFragment
import com.rndeep.fns_fantoo.utils.ConstVariable
import com.rndeep.fns_fantoo.utils.setWhiteStatusBarIcon
import com.rndeep.fns_fantoo.utils.showErrorSnackBar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeTabFragment : BaseFragment<FragmentHomeTabBinding>(FragmentHomeTabBinding::inflate) {
    //categoryList
    private val categoryListAdapter = CategoryListAdapter()
    private var categoryItems = listOf<String>()

    //viewpage2의 Adapter
    lateinit var hometabViewPagerAdapter: HomeTabViewPagerAdapter

    private lateinit var fragmentItems: List<Fragment>

    private val viewModel :HomeTabViewModel by viewModels()

    private var currentItem: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingValue()
    }

    override fun initUi() {
        //상태바 색상 변경
        activity?.let {
//            ViewCompat.getWindowInsetsController(it.window.decorView)?.isAppearanceLightStatusBars =
//                false
            activity?.window?.statusBarColor = it.getColor(R.color.primary_300)
            it.setWhiteStatusBarIcon()
        }
        initToolbar()
        initView()
    }

    override fun initUiActionEvent() {

    }

    fun scrollToUp() {
        when (val currentFragment =hometabViewPagerAdapter.getFragment(binding.rcCategoryvp.currentItem)) {
            is HomeFragment -> {
                currentFragment.scrollTop()
            }
            is PopularFragment -> {
                currentFragment.scrollTop()
            }
        }

    }

    //값 셋팅
    private fun settingValue() {
        categoryItems = listOf(getString(R.string.en_home), getString(R.string.en_popular))
        fragmentItems = listOf(
            HomeFragment().apply {

            },
            PopularFragment().apply {

            }
        )

    }

    //초기 뷰 셋팅
    private fun initView() {
        //상단 카테고리탭 어댑터 설정 (향후 스크롤로 변경 가능성이 있어 recyclerview 로 작성)
        binding.rcCategoryList.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rcCategoryList.adapter = categoryListAdapter
        //데이터 변경시 깜빡임 제거
        if (binding.rcCategoryList.itemAnimator is SimpleItemAnimator) {
            (binding.rcCategoryList.itemAnimator as SimpleItemAnimator).supportsChangeAnimations =
                false
        }
        //HomeTab에 메인 화면 viewpager2
        binding.rcCategoryvp.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        hometabViewPagerAdapter = HomeTabViewPagerAdapter(childFragmentManager, lifecycle)
        hometabViewPagerAdapter.setFragmentList(fragmentItems)
        binding.rcCategoryvp.adapter = hometabViewPagerAdapter

        //viewpager2 스와이프로 페이지 변경시 상단 category view 변경과 페이지에 맞는 데이터를 요청
        binding.rcCategoryvp.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                categoryListAdapter.selectCategoryPos(position)
                currentItem = categoryListAdapter.getSelectName()
                when (position) {
                    0 -> {
//                        if (isHomeInit) categorySelect(categoryItems[0])
                    }
                    1 -> {
//                        if (isPopularInit) categorySelect(categoryItems[1])
                    }
                }
            }
        })

        //상단 category 아이템 설정
        categoryListAdapter.setListItem(categoryItems)
        //카테고리를 touch 하여 변경시 viewpager2의 화면도 변경한다.
        categoryListAdapter.setOnCategoryListener(object :
            CategoryListAdapter.OnCategoryClickListener {
            override fun OnCategoryClick(v: View, pos: Int, name: String) {
                currentItem = name
                when (pos) {
                    0 -> {
                        if (categoryListAdapter.getSelectName() == categoryItems[0]) return
                    }
                    1 -> {
                        if (categoryListAdapter.getSelectName() == categoryItems[1]) return
                    }
                }

                binding.rcCategoryvp.setCurrentItem(pos, true)

            }
        })
        if (currentItem == null) {
            if(viewModel.isUser()){
                binding.rcCategoryvp.setCurrentItem(0, false)
            }else{
                binding.rcCategoryvp.setCurrentItem(1, false)
            }
        }
    }

    //toolbar 정의
    private fun initToolbar() {
        binding.homeToolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_alarm -> {
                    if(!viewModel.isUser()){
                        activity?.showErrorSnackBar(binding.root,ConstVariable.ERROR_NOT_MEMBER)
                        return@setOnMenuItemClickListener false
                    }
                    findNavController().navigate(HomeTabFragmentDirections.actionHomeTabFragmentToHomeAlarmFragment())
                    true
                }
                R.id.menu_event -> {

                    true
                }
                else -> false
            }
        }
    }

}