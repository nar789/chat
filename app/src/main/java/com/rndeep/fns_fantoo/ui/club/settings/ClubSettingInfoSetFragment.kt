package com.rndeep.fns_fantoo.ui.club.settings

import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.model.Country
import com.rndeep.fns_fantoo.data.remote.model.Language
import com.rndeep.fns_fantoo.ui.common.RecyclerViewItemClickListener
import com.rndeep.fns_fantoo.databinding.FragmentClubSettingInfoSetBinding
import com.rndeep.fns_fantoo.ui.common.LanguageBottomSheet
import com.rndeep.fns_fantoo.ui.club.settings.data.MenuItem
import com.rndeep.fns_fantoo.ui.club.settings.viewmodel.ClubInfoSetViewModel
import com.rndeep.fns_fantoo.ui.common.AppLanguageBottomSheet
import com.rndeep.fns_fantoo.utils.ConstVariable
import com.rndeep.fns_fantoo.utils.ConstVariable.ClubDef.Companion.KEY_CLUB_INFO_CLUBID
import com.rndeep.fns_fantoo.utils.ConstVariable.KEY_UID
import com.rndeep.fns_fantoo.utils.LanguageUtils
import com.rndeep.fns_fantoo.utils.showErrorSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class ClubSettingInfoSetFragment :
    ClubSettingBaseFragment<FragmentClubSettingInfoSetBinding>(FragmentClubSettingInfoSetBinding::inflate) {

    private val clubInfoSetViewModel: ClubInfoSetViewModel by viewModels()

    private lateinit var clubId: String
    private lateinit var uid: String
    private lateinit var menuBottomSheetDialogFragment: MenuBottomSheetDialogFragment
    private lateinit var menuItemArrayList: ArrayList<MenuItem>

    override fun initUi() {
        val args: ClubSettingInfoSetFragmentArgs by navArgs()
        clubId = args.clubId
        uid = args.integUid.toString()
        Timber.d("uid ${args.integUid}")

        clubInfoSetViewModel.networkErrorLiveData.observe(viewLifecycleOwner) {
            showDialog(getString(R.string.alert_network_error))
        }

        clubInfoSetViewModel.loadingStatusLiveData.observe(viewLifecycleOwner) { loadingState ->
            setLoadingView(loadingState)
        }

        clubInfoSetViewModel.languageLiveData.observe(viewLifecycleOwner) { language ->
            lifecycleScope.launch {
                if(language != null) {
                    viewModel.getLanguageCode()?.let { appLanguageCode ->
                        updateClubLangauge(if (appLanguageCode == LanguageUtils.KOREAN) language.nameKr else language.name)
                    }
                }
            }
        }

        clubInfoSetViewModel.countryLiveData.observe(viewLifecycleOwner) { country ->
            lifecycleScope.launch {
                if(country != null) {
                    viewModel.getLanguageCode()?.let { appLanguageCode ->
                        updateActiveCountry(if (appLanguageCode == LanguageUtils.KOREAN) country.nameKr else country.nameEn)
                    }
                }
            }
        }

        clubInfoSetViewModel.clubAllInfoLiveData.observe(viewLifecycleOwner) { clubAllInfoResponse ->
            try {
                if (clubAllInfoResponse.code == ConstVariable.RESULT_SUCCESS_CODE) {
                    Timber.d("clubAllInfoResponse $clubAllInfoResponse")
                    val clubAllInfo = clubAllInfoResponse.clubAllInfo
                    if (clubAllInfo != null) {
                        updateClubPublicSetStateText(
                            if (clubAllInfo.openYn) getString(R.string.g_open_public) else getString(
                                R.string.b_hidden
                            )
                        )
                        updateClubJoinMethodText(if(clubAllInfo.memberJoinAutoYn)getString(R.string.j_auto)else getString(R.string.s_approval))
                        updateClubMembersPublicSetText(if(clubAllInfo.memberListOpenYn)getString(R.string.g_open_public)else getString(R.string.b_hidden))
                        updateClubMemberCountPublicSetText(if(clubAllInfo.memberCountOpenYn)getString(R.string.g_open_public)else getString(R.string.b_hidden))
                        clubInfoSetViewModel.getLanguage(clubAllInfo.languageCode)
                        clubInfoSetViewModel.getCountry(clubAllInfo.activeCountryCode)
                    }
                } else {
                    requireContext().showErrorSnackBar(binding.root, clubAllInfoResponse.code)
                }
            } catch (e: Exception) {
                Timber.e("${e.printStackTrace()}")
            }
        }

        clubInfoSetViewModel.getClubAllInfo(clubId, uid)
    }

    override fun initUiActionEvent() {
        binding.topbar.setOnBackButtonClickListener {
            navController.popBackStack()
        }
        binding.rlClubProfileSet.setOnClickListener {

            val bundle = bundleOf(KEY_CLUB_INFO_CLUBID to clubId, KEY_UID to uid)
            val clubAllInfoPacerable = clubInfoSetViewModel.clubAllInfoLiveData.value?.clubAllInfo?.toClubAllInfoPacerable()
            if(clubAllInfoPacerable != null){
                bundle.putParcelable("clubAllInfo", clubAllInfoPacerable)
            }
            navController.navigate(
                R.id.action_clubSettingInfoSetFragment_to_clubSettingClubProfileSetFragment,
                bundle
            )
        }

        binding.rlClubVisibilitySet.setOnClickListener {
            activity?.let {
                menuItemArrayList = ArrayList()
                menuItemArrayList.add(MenuItem(
                    0,
                    getString(R.string.g_open_public),
                    getString(R.string.se_g_open_post_in_board)
                ))
                menuItemArrayList.add(
                    MenuItem(
                        1,
                        getString(R.string.b_hidden),
                        getString(R.string.se_k_hide_post_in_club)
                    )
                )
                menuBottomSheetDialogFragment = MenuBottomSheetDialogFragment(menuItemArrayList)
                menuBottomSheetDialogFragment.title = getString(R.string.k_club_visibility_settings)
                menuBottomSheetDialogFragment.setOnMenuItemClickListener(object :
                    RecyclerViewItemClickListener {
                    override fun onItemClick(objects: Any) {
                        val selectedMenuItem = objects as MenuItem
                        Timber.d("club public/private set select item = ${selectedMenuItem.title1}")
                        val setPublicValue = selectedMenuItem.id == 0
                        clubInfoSetViewModel.setClubAllInfo(
                            clubId,
                            hashMapOf(
                                KEY_UID to uid,
                                ConstVariable.ClubDef.KEY_CLUB_INFO_OPENYN to setPublicValue
                            )
                        )
                    }
                })
                if (clubInfoSetViewModel.clubAllInfoLiveData.value != null) {
                    val clubAllInfo = clubInfoSetViewModel.clubAllInfoLiveData.value!!.clubAllInfo
                    if (clubAllInfo != null) {
                        val isClubOpen = clubAllInfo.openYn
                        val currentMenuItem = menuItemArrayList[if (isClubOpen) 0 else 1]
                        menuBottomSheetDialogFragment.currentMenuItem = currentMenuItem
                    }
                }
                menuBottomSheetDialogFragment.show(requireActivity().supportFragmentManager, "")
            }
        }

        binding.rlClubCategorySet.setOnClickListener {
            try {
                val bundle = bundleOf(KEY_CLUB_INFO_CLUBID to clubId, KEY_UID to uid)
                val clubAllInfoPacerable =
                    clubInfoSetViewModel.clubAllInfoLiveData.value?.clubAllInfo?.toClubAllInfoPacerable()
                if (clubAllInfoPacerable != null) {
                    bundle.putParcelable("clubAllInfo", clubAllInfoPacerable)
                }
                navController.navigate(
                    R.id.action_clubSettingInfoSetFragment_to_clubSettingCategoryFragment,
                    bundle
                )
            }catch (e:Exception){
                Timber.e("${e.printStackTrace()}")
            }
        }

        binding.rlClubJoinMethod.setOnClickListener {
            activity?.let {
                menuItemArrayList = ArrayList()
                val menuItem = MenuItem(
                    0,
                    getString(R.string.j_auto),
                    getString(R.string.g_join_immediately_after_apply)
                )
                menuItemArrayList.add(menuItem)
                menuItemArrayList.add(
                    MenuItem(
                        1,
                        getString(R.string.s_approval),
                        getString(R.string.k_join_after_approves)
                    )
                )
                menuBottomSheetDialogFragment = MenuBottomSheetDialogFragment(menuItemArrayList)
                menuBottomSheetDialogFragment.title = getString(R.string.g_join_accept_method)
                menuBottomSheetDialogFragment.setOnMenuItemClickListener(object :
                    RecyclerViewItemClickListener {
                    override fun onItemClick(objects: Any) {
                        val selectedMenuItem = objects as MenuItem
                        val setJoinAutoValue = selectedMenuItem.id == 0
                        clubInfoSetViewModel.setClubAllInfo(
                            clubId,
                            hashMapOf(
                                KEY_UID to uid,
                                ConstVariable.ClubDef.KEY_CLUB_INFO_MEMBER_JOIN_AUTOYN to setJoinAutoValue
                            )
                        )
                    }
                })
                if (clubInfoSetViewModel.clubAllInfoLiveData.value != null) {
                    val clubAllInfo = clubInfoSetViewModel.clubAllInfoLiveData.value!!.clubAllInfo
                    if (clubAllInfo != null) {
                        val currentMenuItem =
                            menuItemArrayList[if (clubAllInfo.memberJoinAutoYn) 0 else 1]
                        menuBottomSheetDialogFragment.currentMenuItem = currentMenuItem
                    }
                }
                menuBottomSheetDialogFragment.show(requireActivity().supportFragmentManager, "")
            }
        }

        binding.rlClubMemberVisibility.setOnClickListener {
            activity?.let {
                menuItemArrayList = ArrayList()
                val menuItem = MenuItem(
                    0,
                    getString(R.string.g_open_public),
                    getString(R.string.k_open_all_club_members)
                )
                menuItemArrayList.add(menuItem)
                menuItemArrayList.add(
                    MenuItem(
                        1,
                        getString(R.string.b_hidden),
                        getString(R.string.k_member_only_available_club_president)
                    )
                )
                menuBottomSheetDialogFragment = MenuBottomSheetDialogFragment(menuItemArrayList)
                menuBottomSheetDialogFragment.title =
                    getString(R.string.m_member_list_revealed)
                menuBottomSheetDialogFragment.setOnMenuItemClickListener(object :
                    RecyclerViewItemClickListener {
                    override fun onItemClick(objects: Any) {
                        val selectedMenuItem = objects as MenuItem
                        val setValue = selectedMenuItem.id == 0
                        clubInfoSetViewModel.setClubAllInfo(
                            clubId, hashMapOf(
                                KEY_UID to uid,
                                ConstVariable.ClubDef.KEY_CLUB_INFO_MEMBERLIST_OPENYN to setValue
                            )
                        )
                    }
                })
                if (clubInfoSetViewModel.clubAllInfoLiveData.value != null) {
                    val clubAllInfo = clubInfoSetViewModel.clubAllInfoLiveData.value!!.clubAllInfo
                    if (clubAllInfo != null) {
                        val currentMenuItem =
                            menuItemArrayList[if (clubAllInfo.memberListOpenYn) 0 else 1]
                        menuBottomSheetDialogFragment.currentMenuItem = currentMenuItem
                    }
                }
                menuBottomSheetDialogFragment.show(requireActivity().supportFragmentManager, "")
            }
        }

        binding.rlClubMemberCountVisibility.setOnClickListener {
            activity?.let {
                menuItemArrayList = ArrayList()
                val menuItem = MenuItem(
                    0,
                    getString(R.string.g_open_public),
                    getString(R.string.k_show_all_member_count)
                )
                menuItemArrayList.add(menuItem)
                menuItemArrayList.add(
                    MenuItem(
                        1,
                        getString(R.string.b_hidden),
                        getString(R.string.k_can_check_club_president)
                    )
                )
                menuBottomSheetDialogFragment = MenuBottomSheetDialogFragment(menuItemArrayList)
                menuBottomSheetDialogFragment.title =
                    getString(R.string.m_member_count_revealed)
                menuBottomSheetDialogFragment.setOnMenuItemClickListener(object :
                    RecyclerViewItemClickListener {
                    override fun onItemClick(objects: Any) {
                        val selectedMenuItem = objects as MenuItem
                        val setValue = selectedMenuItem.id == 0
                        clubInfoSetViewModel.setClubAllInfo(
                            clubId,
                            hashMapOf(
                                KEY_UID to uid,
                                ConstVariable.ClubDef.KEY_CLUB_INFO_MEMBERCOUNT_OPENYN to setValue
                            )
                        )
                    }
                })
                if (clubInfoSetViewModel.clubAllInfoLiveData.value != null) {
                    val clubAllInfo = clubInfoSetViewModel.clubAllInfoLiveData.value!!.clubAllInfo
                    if (clubAllInfo != null) {
                        val currentMenuItem =
                            menuItemArrayList[if (clubAllInfo.memberCountOpenYn) 0 else 1]
                        menuBottomSheetDialogFragment.currentMenuItem = currentMenuItem
                    }
                }
                menuBottomSheetDialogFragment.show(requireActivity().supportFragmentManager, "")
            }
        }

        binding.rlClubSearchWord.setOnClickListener {
            val bundle = bundleOf(KEY_CLUB_INFO_CLUBID to clubId, KEY_UID to uid)
            navController.navigate(
                R.id.action_clubSettingInfoSetFragment_to_clubSettingSearchWordFragment,
                bundle
            )
        }

        binding.rlClubMainLang.setOnClickListener {
            activity?.let { fragmentActivity ->
                val languageBottomSheet = AppLanguageBottomSheet()
                languageBottomSheet.title = getString(R.string.j_main_language_settings)
                languageBottomSheet.itemClickListener =
                    (object : AppLanguageBottomSheet.ItemClickListener {
                        override fun onItemClick(item: Language) {
                            lifecycleScope.launch {
                                Timber.d("clubLang select ${item.nameKr}")
                                clubInfoSetViewModel.setClubAllInfo(
                                    clubId,
                                    hashMapOf(
                                        KEY_UID to uid,
                                        ConstVariable.ClubDef.KEY_CLUB_INFO_LANGUAGECODE to item.langCode
                                    )
                                )
                                languageBottomSheet.dismiss()
                            }
                        }
                    })
                if (clubInfoSetViewModel.clubAllInfoLiveData.value != null) {
                    val clubAllInfo = clubInfoSetViewModel.clubAllInfoLiveData.value!!.clubAllInfo
                    if (clubAllInfo != null) {
                        languageBottomSheet.selectLanguageCode = clubAllInfo.languageCode
                    }
                }
                languageBottomSheet.show(fragmentActivity.supportFragmentManager, "")
            }
        }

        binding.rlClubMyCountry.setOnClickListener {
            activity?.let {
                //countryItem? 회원가입때 표시되는 거주 국가 선택표시?
                val languageBottomSheet = LanguageBottomSheet()
                languageBottomSheet.title = getString(R.string.j_main_activity_contry_settings)
                languageBottomSheet.itemClickListener =
                    (object : LanguageBottomSheet.ItemClickListener {
                        override fun onItemClick(item: Country) {
                            lifecycleScope.launch {
                                clubInfoSetViewModel.setClubAllInfo(
                                    clubId,
                                    hashMapOf(
                                        KEY_UID to uid,
                                        ConstVariable.ClubDef.KEY_CLUB_INFO_ACTIVE_COUNTRYCODE to item.iso2
                                    )
                                )
                                languageBottomSheet.dismiss()
                            }
                        }
                    })
                if (clubInfoSetViewModel.clubAllInfoLiveData.value != null) {
                    val clubAllInfo = clubInfoSetViewModel.clubAllInfoLiveData.value!!.clubAllInfo
                    if (clubAllInfo != null) {
                        languageBottomSheet.selectLanguageCode = clubAllInfo.activeCountryCode
                    }
                }
                languageBottomSheet.show(it.supportFragmentManager, "")
            }
        }
    }

    //오른쪽 서브 타이틀 갱신
    //클럽 공개 설정
    private fun updateClubPublicSetStateText(subTitle: String) {
        binding.tvClubVisibility.text = subTitle
    }

    //가입 승인 방식
    private fun updateClubJoinMethodText(subTitle: String) {
        binding.tvClubJoinMethod.text = subTitle
    }

    //멤버목록공개
    private fun updateClubMembersPublicSetText(subTitle: String) {
        binding.tvClubMemberVisibility.text = subTitle
    }

    //멤버수 공개
    private fun updateClubMemberCountPublicSetText(subTitle: String) {
        binding.tvClubMemberCountVisibility.text = subTitle
    }

    //주언어 설정
    private fun updateClubLangauge(subTitle: String) {
        binding.tvClubMainLang.text = subTitle
    }

    //주 활동국가
    private fun updateActiveCountry(subTitle: String) {
        binding.tvClubMyCountry.text = subTitle
    }

    private fun setLoadingView(isShow: Boolean) {
        binding.loadingView.visibility = if (isShow) View.VISIBLE else View.GONE
    }
}