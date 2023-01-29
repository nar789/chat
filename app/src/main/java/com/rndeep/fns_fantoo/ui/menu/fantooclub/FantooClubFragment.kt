package com.rndeep.fns_fantoo.ui.menu.fantooclub

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.tabs.TabLayoutMediator
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.FragmentFantooClubBinding
import com.rndeep.fns_fantoo.ui.login.LoginMainActivity
import com.rndeep.fns_fantoo.ui.menu.*
import com.rndeep.fns_fantoo.ui.menu.fantooclub.info.DialogInfo
import com.rndeep.fns_fantoo.ui.menu.fantooclub.info.InfoDialog
import com.rndeep.fns_fantoo.ui.menu.fantooclub.info.InfoDialogClickType
import com.rndeep.fns_fantoo.utils.ConstVariable
import com.rndeep.fns_fantoo.utils.setStatusBar
import com.rndeep.fns_fantoo.utils.startShare
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

/**
 * Fantoo Club UI
 */
@AndroidEntryPoint
class FantooClubFragment : Fragment() {

    private lateinit var binding: FragmentFantooClubBinding
    private lateinit var loginDialog: MenuDialogFragment
    private lateinit var infoDialog: InfoDialog

    private val viewModel: FantooClubViewModel by viewModels()
    private val args: FantooClubFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFantooClubBinding.inflate(inflater, container, false).apply {
            toolbar.setNavigationOnClickListener { view ->
                view.findNavController().navigateUp()
            }
        }

        val tabLayout = binding.tabs
        val viewPager = binding.viewPager

        viewModel.setClubId(args.clubId)
        viewPager.adapter = FantooClubPagerAdapter(this, args.clubId)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = getTabTitle(position)
        }.attach()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setStatusBar(R.color.transparent, false)

        if (viewModel.isLogin) {
            viewModel.fetchFantooClubFollow()
        }
        viewModel.fetchFantooClubBasicInfo()

        binding.toolbar.menu.clear()
        binding.toolbar.inflateMenu(R.menu.search_menu)
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_search -> {
                    Timber.d("go search menu")
                    val direction =
                        FantooClubFragmentDirections.actionFantooClubFragmentToSearchFragment(
                            viewModel.getClubId()
                        )
                    findNavController().navigate(direction)
                    true
                }
                else -> false
            }
        }

        var isToolbarShown = false

        binding.appbar.addOnOffsetChangedListener { appBarLayout, verticalOffset ->

            val shouldShowToolbar =
                appBarLayout.totalScrollRange + verticalOffset < ACTIONBAR_HEIGHT
            binding.appbar.isActivated = shouldShowToolbar
            if (isToolbarShown != shouldShowToolbar) {
                binding.fantooTvContainer.visibility = View.GONE
                isToolbarShown = shouldShowToolbar
            } else {
                binding.fantooTvContainer.visibility = View.VISIBLE
                isToolbarShown = false
            }
        }

        viewModel.clubInfo.observe(viewLifecycleOwner) { info ->
            info?.let {
                binding.toolbar.title = it.clubName
                binding.title.text = it.clubName
                binding.desc.text = info.introduction
                binding.followerCount.text =
                    String.format(getString(R.string.m_nop_with_arg), info.memberCount.toString())
                if(!viewModel.isLogin) {
                    changeFollowBtn(false)
                }

                val clubId = args.clubId
                binding.logo.setImageResource(getClubLogo(clubId))
                binding.appbar.setBackgroundResource(getClubBg(clubId))
            }
        }

        viewModel.isFollowInfo.observe(viewLifecycleOwner) { isFollow ->
            isFollow?.let {
                changeFollowBtn(it.followYn)
            }
        }

        viewModel.followInfo.observe(viewLifecycleOwner) { follow ->
            follow?.let {
                changeFollowBtn(it.followYn)
                binding.followerCount.text =
                    String.format(getString(R.string.m_nop_with_arg), it.followCount.toString())
            }
        }

        binding.info.setOnClickListener {
            showInfoDialog()
        }

        binding.followBtn.setOnClickListener {
            if (viewModel.isLogin) {
                viewModel.setFantooClubFollow()
            } else {
                showLoginMessage()
            }
        }
    }

    private fun getTabTitle(position: Int): String? {
        return when (position) {
            FantooClubPagerAdapter.HOME_PAGE_INDEX -> getString(R.string.h_home)
            FantooClubPagerAdapter.CATEGORY_PAGE_INDEX -> getClubCategoryTitle(viewModel.getClubId())
            else -> null
        }
    }

    private fun getClubCategoryTitle(clubId: String): String {
        return when (clubId) {
            FANTOO_TV_ID -> getString(R.string.c_channel)
            else -> getString(R.string.news_section)
        }
    }

    private fun getClubLogo(clubId: String): Int {
        return when (clubId) {
            FANTOO_TV_ID -> R.drawable.ic_fantoo_tv
            else -> R.drawable.ic_hanryu_times
        }
    }

    private fun getClubBg(clubId: String): Int {
        return when (clubId) {
            FANTOO_TV_ID -> R.drawable.bg_fantoo_tv
            else -> R.drawable.bg_hanryu_times
        }
    }

    private fun getShareText(clubId: String): String {
        return when (clubId) {
            FANTOO_TV_ID -> "https://www.youtube.com/c/FANTOOTVofficial/featured"
            else -> "https://www.hanryutimes.com/"
        }
    }

    private fun showLoginMessage() {
        val message = DialogMessage(
            DialogTitle(
                getString(R.string.r_need_login),
                getString(R.string.se_r_need_login),
                null
            ),
            DialogButton(getString(R.string.h_confirm), getString(R.string.c_cancel), true),
            isCompleted = false
        )
        loginDialog = MenuDialogFragment(message) { clickType ->
            when (clickType) {
                DialogClickType.OK -> {
                    Timber.d("ok")
                    goToLoginPage()
                }
                DialogClickType.CANCEL -> {
                    Timber.d("cancel")
                }
            }
            loginDialog.dismiss()
        }
        openLoginDialog()
    }

    private fun openLoginDialog() {
        Timber.d("openDialog")
        loginDialog.show(
            requireActivity().supportFragmentManager,
            MenuDialogFragment.DIALOG_MENU
        )
    }

    private fun showInfoDialog() {
        val isFollow = viewModel.isFollowInfo.value != null && viewModel.isFollowInfo.value!!.followYn
        viewModel.clubInfo.value?.let { info ->
            infoDialog = InfoDialog(
                DialogInfo(
                    info.clubName,
                    info.introduction,
                    binding.followerCount.text.toString(),
                    isFollow,
                    viewModel.isLogin,
                    getClubLogo(args.clubId)
                )
            ) { clickType ->
                when (clickType) {
                    InfoDialogClickType.FOLLOW -> {
                        if (viewModel.isLogin) {
                            viewModel.setFantooClubFollow()
                        } else {
                            showLoginMessage()
                        }
                    }
                    InfoDialogClickType.SHARE -> {
                        startShare(info.clubName, getShareText(args.clubId))
                    }
                    InfoDialogClickType.CLOSE -> {
                        infoDialog.dismiss()
                    }
                }
            }
            infoDialog.show(
                requireActivity().supportFragmentManager,
                InfoDialog.DIALOG_INFO
            )
        }
    }

    private fun goToLoginPage() {
        val intent = Intent(requireContext(), LoginMainActivity::class.java)
        intent.putExtra(ConstVariable.INTENT.EXTRA_NAV_START_DESTINATION_ID, R.id.loginFragment)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun changeFollowBtn(isFollow: Boolean) {
        Timber.d("changeFollowBtn, isFollow: $isFollow")
        if (isFollow) {
            binding.followBtn.apply {
                backgroundTintList =
                    ColorStateList.valueOf(requireContext().getColor(R.color.transparent))
                strokeColor = ColorStateList.valueOf(requireContext().getColor(R.color.primary_100))
                setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_25))
                text = getString(R.string.p_following)
            }
        } else {
            binding.followBtn.apply {
                backgroundTintList =
                    ColorStateList.valueOf(requireContext().getColor(R.color.primary_default))
                strokeColor = ColorStateList.valueOf(requireContext().getColor(R.color.primary_100))
                setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_25))
                text = getString(R.string.p_follow)
            }
        }
    }

    companion object {
        const val ACTIONBAR_HEIGHT = 56
        const val FANTOO_TV_ID = "fantoo_tv"
        const val HANRYU_TIMES_ID = "hanryu_times"
    }
}