package com.rndeep.fns_fantoo.ui.common.profile

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.FragmentProfileDetailBinding
import com.rndeep.fns_fantoo.ui.common.BaseFragment
import com.rndeep.fns_fantoo.ui.common.custombottomsheet.BottomSheetAdapter
import com.rndeep.fns_fantoo.ui.common.custombottomsheet.BottomSheetItem
import com.rndeep.fns_fantoo.ui.common.custombottomsheet.CustomBottomSheet
import com.rndeep.fns_fantoo.utils.setDarkStatusBarIcon
import com.rndeep.fns_fantoo.utils.setImageWithPlaceHolder
import com.rndeep.fns_fantoo.utils.showErrorSnackBar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileDetailFragment : BaseFragment<FragmentProfileDetailBinding>(FragmentProfileDetailBinding::inflate) {

    val profileViewModel : ProfileDetailViewModel by viewModels()

    val getArgs :ProfileDetailFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        profileViewModel.isUserBlock = getArgs.isBlock
    }

    override fun initUi() {
        setStatusBar()
        settingObsever()

        profileViewModel.setUserNickName(getArgs.nickname)
        profileViewModel.setUserPhoto(getArgs.userPhoto)
    }

    override fun initUiActionEvent() {
        binding.ivProfileImage.setOnClickListener {
            findNavController().navigate(
                ProfileDetailFragmentDirections.actionProfileDetailFragmentToProfileImageViewerFragment2(
                    getString(R.string.imageUrlBase,profileViewModel.userPhotoLiveData.value)
                )
            )
        }
        binding.profileDetailToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.profileDetailToolbar.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.menu_profile_options ->{
                    showBottomSheet()
                    true
                }
                else ->{
                    false
                }
            }
        }
    }

    private fun settingObsever() {
        profileViewModel.userNickNameLiveData.observe(this) {
            binding.tvProfileNickname.text = it
        }

        profileViewModel.userPhotoLiveData.observe(this){
            setImageWithPlaceHolder(
                binding.ivProfileImage,
                getString(R.string.imageUrlBase,it),
                R.drawable.profile_character_xl,
                R.drawable.profile_character_xl
                )
        }

        profileViewModel.errorDataLiveData.observe(this){
            activity?.showErrorSnackBar(binding.root,it)
        }
    }

    private fun setStatusBar(){
        activity?.let {
            it.window.statusBarColor=it.getColor(R.color.gray_25)
            it.setDarkStatusBarIcon()
        }
    }

    private fun showBottomSheet(){
        activity?.let {
            CustomBottomSheet().apply {
                setBottomItems(
                    arrayListOf(
                        BottomSheetItem(null,
                            if(profileViewModel.isUserBlock){
                                it.getString(R.string.a_unblock_this_user)
                            }else{
                                it.getString(R.string.a_block_this_user)
                            },
                            null,
                            null)
                    )
                )
                setOnCliCkListener(object : BottomSheetAdapter.OnItemClickListener{
                    override fun onItemClick(name: String, pos: Int, oldPos: Int?) {
                        when(name){
                            it.getString(R.string.a_block_this_user),it.getString(R.string.a_unblock_this_user)->{
                                if(getArgs.clubId!=null){
                                    profileViewModel.patchClubMemberBlock(getArgs.clubId!!,getArgs.memberId!!.toInt())
                                }else{
                                    profileViewModel.patchCommunityMemberBlock(getArgs.targetUid!!,getArgs.isBlock)
                                }
                                dismiss()
                            }
                        }
                    }
                })
            }.show(childFragmentManager,"profileBlock")
        }
    }

}