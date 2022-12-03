package com.rndeep.fns_fantoo.ui.menu

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.core.text.HtmlCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.local.model.GenderType
import com.rndeep.fns_fantoo.data.local.model.MyProfile
import com.rndeep.fns_fantoo.databinding.FragmentMenuBinding
import com.rndeep.fns_fantoo.ui.login.LoginMainActivity
import com.rndeep.fns_fantoo.utils.ConstVariable
import com.rndeep.fns_fantoo.utils.getImageUrlFromCDN
import com.rndeep.fns_fantoo.utils.setProfileAvatar
import com.rndeep.fns_fantoo.utils.setStatusBar
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


/**
 * Menu UI
 */
@AndroidEntryPoint
class MenuFragment : Fragment() {

    private lateinit var binding: FragmentMenuBinding
    private lateinit var dialogFragment: MenuDialogFragment
    private val viewModel: MenuViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMenuBinding.inflate(inflater, container, false)
        binding.completeProfile.visibility = View.VISIBLE

        val explainText = HtmlCompat.fromHtml(
            getString(R.string.complete_profile_explain_text),
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
        binding.completeProfileExplain.text = explainText

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setStatusBar(R.color.bg_light_gray_50, true)
        clearToolbarMenu()
        binding.toolbar.inflateMenu(R.menu.menus_menu)
        binding.toolbar.setOnMenuItemClickListener {
            Timber.d("onViewCreated, setOnMenuItemClickListener, id : ${it.itemId}")
            when (it.itemId) {
                R.id.action_setting -> {
                    // Navigate to settings screen
                    if (viewModel.isLogin) {
                        findNavController().navigate(R.id.action_menuFragment_to_settingFragment)
                    } else {
                        showLoginMessage()
                    }
                    true
                }
                else -> false
            }
        }

        setNonMemberUserVisible()

        viewModel.myProfile.observe(viewLifecycleOwner) { profile ->
            Timber.d("myProfile: $profile")
            if (profile != null) {
                profile.nickname?.let {
                    binding.nickname.text = it
                }
                setProfileAvatar(binding.profileImg, getImageUrlFromCDN(profile.imageUrl))
                checkProfileCompleteCount(profile)
            }
        }

        binding.profileInfo.setOnClickListener {
            findNavController().navigate(R.id.action_menuFragment_to_editProfileFragment)
        }

        binding.editProfileBtn.setOnClickListener {
            findNavController().navigate(R.id.action_menuFragment_to_editProfileFragment)
        }

        binding.completeProfile.setOnClickListener {
            findNavController().navigate(R.id.action_menuFragment_to_editProfileFragment)
        }

        binding.libraryInfoContainer.setOnClickListener {
            if (viewModel.isLogin) {
                findNavController().navigate(R.id.action_menuFragment_to_libraryFragment)
            } else {
                showLoginMessage()
            }
        }

        binding.myClubContainer.setOnClickListener {
            if (viewModel.isLogin) {
                navigateByItem(MenuItemType.MY_CLUB)
            } else {
                showLoginMessage()
            }
        }

        binding.myWalletContainer.setOnClickListener {
            if (viewModel.isLogin) {
                navigateByItem(MenuItemType.MY_WALLET)
            } else {
                showLoginMessage()
            }
        }

        binding.inviteFriendContainer.setOnClickListener {
            if (viewModel.isLogin) {
                navigateByItem(MenuItemType.INVITE_FRIEND)
            } else {
                showLoginMessage()
            }
        }

        binding.eventContainer.setOnClickListener {
            navigateByItem(MenuItemType.EVENT)
        }

        binding.fantooTvContainer.setOnClickListener {
            navigateByItem(MenuItemType.FANTOO_TV)
        }

        binding.hanryuTimesContainer.setOnClickListener {
            navigateByItem(MenuItemType.HANRYU_TIMES)
        }

        binding.nonMemberInfo.setOnClickListener {
            showLoginMessage()
        }

        viewModel.myWriteCount.observe(viewLifecycleOwner) { count ->
            count?.let {
                if (viewModel.isLogin) {
                    binding.myWriteNum.text = count.postCnt.toString()
                    binding.myCommentNum.text = count.replyCnt.toString()
                    binding.mySaveNum.text = count.bookmarkCnt.toString()
                } else {
                    binding.myWriteNum.text = "-"
                    binding.myCommentNum.text = "-"
                    binding.mySaveNum.text = "-"
                }
            }
        }
    }

    private fun clearToolbarMenu() {
        Timber.d("clearToolbarMenu")
        binding.toolbar.menu.clear()
    }

    private fun navigateByItem(item: MenuItemType) {
        when (item) {
            MenuItemType.MY_CLUB -> {
                findNavController().navigate(R.id.action_menuFragment_to_myClubListFragment)
            }
            MenuItemType.MY_WALLET -> {
                findNavController().navigate(R.id.action_menuFragment_to_myWalletFragment)
            }
            MenuItemType.INVITE_FRIEND -> {
                val referralCode = viewModel.getMyReferralCode()
                val direction = MenuFragmentDirections.actionMenuFragmentToInviteFriendFragment(referralCode)
                findNavController().navigate(direction)
            }
            MenuItemType.EVENT -> {
                findNavController().navigate(R.id.action_menuFragment_to_eventFragment)
            }
            MenuItemType.FANTOO_TV -> {
                val direction =
                    MenuFragmentDirections.actionMenuFragmentToFantooClubFragment(FANTOO_TV_ID)
                findNavController().navigate(direction)
            }
            MenuItemType.HANRYU_TIMES -> {
                val direction =
                    MenuFragmentDirections.actionMenuFragmentToFantooClubFragment(HANRYU_TIMES_ID)
                findNavController().navigate(direction)
            }
            else -> {
                Timber.d("else $item")
            }
        }
    }

    private fun checkProfileCompleteCount(profile: MyProfile) {
        var count = MINIMUM_PROFILE_COUNT
        if (!profile.imageUrl.isNullOrEmpty()) count++
        if (profile.gender != GenderType.UNKNOWN) count++
        if (!profile.concern.isNullOrEmpty()) count++
        if (!profile.birthday.isNullOrEmpty()) count++
        Timber.d("check profile count : $count")
        setCompleteProfileNumber(count)
        if (count == COMPLETE_PROFILE_COUNT) {
            binding.completeProfile.visibility = View.GONE
        }
    }

    private fun setCompleteProfileNumber(num: Int) {
        val number = HtmlCompat.fromHtml(
            String.format(getString(R.string.complete_profile_number_text), num),
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
        binding.completeProfileNumber.text = number

        val progress = (num.toDouble() / COMPLETE_PROFILE_COUNT) * 100
        Timber.d("setCompleteProfileNumber progress : $progress")
        binding.completeProfileProgress.setProgressCompat(progress.toInt(), false)

    }

    private fun setNonMemberUserVisible() {
        if (!viewModel.isLogin) {
            binding.nonMemberInfo.visibility = View.VISIBLE
            binding.profileInfo.visibility = View.GONE
            binding.completeProfile.visibility = View.GONE
        }
    }

    private fun goToLoginPage() {
        val intent = Intent(requireContext(), LoginMainActivity::class.java)
        intent.putExtra(ConstVariable.INTENT.EXTRA_NAV_START_DESTINATION_ID, R.id.loginFragment)
        startActivity(intent)
        requireActivity().finish()
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
        dialogFragment = MenuDialogFragment(message) { clickType ->
            when (clickType) {
                DialogClickType.OK -> {
                    Timber.d("ok")
                    goToLoginPage()
                }
                DialogClickType.CANCEL -> {
                    Timber.d("cancel")
                }
            }
            dismissDialog()
        }
        openDialog()
    }

    private fun openDialog() {
        Timber.d("openDialog")
        dialogFragment.show(
            requireActivity().supportFragmentManager,
            MenuDialogFragment.DIALOG_MENU
        )
    }

    private fun dismissDialog() {
        Timber.d("dismissDialog")
        dialogFragment.dismiss()
    }

    companion object {
        const val MINIMUM_PROFILE_COUNT = 2
        const val COMPLETE_PROFILE_COUNT = 6
        const val HANRYU_TIMES_ID = "hanryu_times"
        const val FANTOO_TV_ID = "fantoo_tv"
    }
}