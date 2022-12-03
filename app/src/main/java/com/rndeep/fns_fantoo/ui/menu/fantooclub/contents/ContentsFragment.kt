package com.rndeep.fns_fantoo.ui.menu.fantooclub.contents

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.model.fantooclub.FantooClubPost
import com.rndeep.fns_fantoo.databinding.FragmentContentsBinding
import com.rndeep.fns_fantoo.ui.login.LoginMainActivity
import com.rndeep.fns_fantoo.ui.menu.*
import com.rndeep.fns_fantoo.ui.menu.fantooclub.FantooClubFragment
import com.rndeep.fns_fantoo.ui.menu.fantooclub.FantooClubFragmentDirections
import com.rndeep.fns_fantoo.ui.menu.fantooclub.FantooClubPagerAdapter
import com.rndeep.fns_fantoo.ui.menu.fantooclub.more.MoreBottomSheet
import com.rndeep.fns_fantoo.utils.ConstVariable
import com.rndeep.fns_fantoo.utils.addLibraryItemDecorationDivider
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


/**
 * Contents UI
 */
@AndroidEntryPoint
class ContentsFragment : Fragment() {

    private lateinit var binding: FragmentContentsBinding
    private lateinit var contentsAdapter: ContentsAdapter
    private lateinit var moreMenu: MoreBottomSheet
    private lateinit var loginDialog: MenuDialogFragment
    private lateinit var msgDialog: MenuDialogFragment

    private val viewModel: ContentsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { bundle ->
            bundle.getString(FantooClubPagerAdapter.KEY_CLUB_ID)?.let { viewModel.setClubId(it) }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentContentsBinding.inflate(inflater, container, false).apply {
            contentsAdapter = ContentsAdapter { contents, clickType ->
                Timber.d("clicked contents: $contents, clickType: $clickType")
                when(clickType) {
                    ContentsClickType.ITEM -> {
                        viewModel.setInitErrorMsg()
                        navigateToDetail(contents)
                    }
                    ContentsClickType.MORE -> {
                        showMoreMenu()
                    }
                    ContentsClickType.LIKE -> {
                        Timber.d("click like")
                        viewModel.setFantooClubPostLikeAndDislike(LikeType.LIKE.type, contents.categoryCode, contents.postId.toString())
                    }
                    ContentsClickType.DISLIKE -> {
                        Timber.d("click dislike")
                        viewModel.setFantooClubPostLikeAndDislike(LikeType.DISLIKE.type, contents.categoryCode, contents.postId.toString())
                    }
                    ContentsClickType.HONOR -> {
                        Timber.d("click honor")
                    }
                }
            }

            homeList.run {
                adapter = contentsAdapter
                setHasFixedSize(true)
                addLibraryItemDecorationDivider()
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.fetchFantooClubHomePosts(viewModel.getClubId())

        viewModel.homePosts.observe(viewLifecycleOwner) { list ->
            Timber.d("homePosts list : $list")
            contentsAdapter.submitList(list)
        }

        viewModel.likeInfo.observe(viewLifecycleOwner) { info ->
            Timber.d("likeInfo : $info")
            info?.let {
                viewModel.fetchFantooClubHomePosts(viewModel.getClubId())
            }
        }

        viewModel.errorMsg.observe(viewLifecycleOwner) { msg ->
            Timber.d("errorMsg : $msg")
            msg?.let {
                if ("FE1013" == it) {
                    showLoginMessage()
                } else {
                    showErrorMessage(msg)
                }
            }
        }
    }

    private fun navigateToDetail(contents: FantooClubPost) {
        when (viewModel.getClubId()) {
            FantooClubFragment.FANTOO_TV_ID -> {
                val direction =
                    FantooClubFragmentDirections.actionFantooClubFragmentToContentsDetailFragment(
                        contents
                    )
                findNavController().navigate(direction)
            }
            FantooClubFragment.HANRYU_TIMES_ID -> {
                contents.link.let { link ->
                    val direction =
                        link?.let {
                            FantooClubFragmentDirections.actionFantooClubFragmentToHanryuNewsDetailFragment(
                                it
                            )
                        }
                    if (direction != null) {
                        findNavController().navigate(direction)
                    }
                }
            }
        }
    }

    private fun showMoreMenu() {
        moreMenu = MoreBottomSheet(viewModel.moreMenuItems) {
            Timber.d("item : $it")
        }
        moreMenu.show(requireActivity().supportFragmentManager, MoreBottomSheet.TAG)
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

    private fun showErrorMessage(msg: String) {
        val message = DialogMessage(
            DialogTitle(
                msg,
                null,
                null
            ),
            DialogButton(getString(R.string.h_confirm), null, false),
            isCompleted = false
        )
        msgDialog = MenuDialogFragment(message) { clickType ->
            when (clickType) {
                DialogClickType.OK -> {
                    Timber.d("ok")
                    msgDialog.dismiss()
                }
                else -> {}
            }
        }
        msgDialog.show(requireActivity().supportFragmentManager, MenuDialogFragment.DIALOG_MENU)
    }

    private fun openLoginDialog() {
        Timber.d("openDialog")
        loginDialog.show(
            requireActivity().supportFragmentManager,
            MenuDialogFragment.DIALOG_MENU
        )
    }

    private fun goToLoginPage() {
        val intent = Intent(requireContext(), LoginMainActivity::class.java)
        intent.putExtra(ConstVariable.INTENT.EXTRA_NAV_START_DESTINATION_ID, R.id.loginFragment)
        startActivity(intent)
        requireActivity().finish()
    }

}