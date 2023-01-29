package com.rndeep.fns_fantoo.ui.club.detail

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.model.ClubBasicInfo
import com.rndeep.fns_fantoo.databinding.TabClubPageInfoDialogBinding
import com.rndeep.fns_fantoo.ui.common.AppContextWrapper
import com.rndeep.fns_fantoo.utils.setProfileAvatar
import java.text.SimpleDateFormat
import java.util.*

class ClubPageInfoDialog(
    private val clubInfoItem : ClubBasicInfo,
    private val clubDialogClickListener : ClubInfoDialogClickListener
) : DialogFragment(){

    private var _binding : TabClubPageInfoDialogBinding? =null
    private val binding get() = _binding!!


    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =inflater.inflate(R.layout.tab_club_page_info_dialog,container,false)
        _binding = TabClubPageInfoDialogBinding.bind(view)

        settingInitView()
        clickFunc()
        return binding.root
    }


    fun settingInitView(){
        //클럽 썸네일
        setProfileAvatar(binding.ivClubThumbnail,getString(R.string.imageThumbnailUrl,clubInfoItem.profileImg))
        //클럽 이름
        binding.tvClubTitle.text=clubInfoItem.clubName
        //클럽 주인장
        binding.tvClubOwnerName.text=clubInfoItem.clubMasterNickname
        //클럽 생성 시기
        if(context?.resources?.configuration!=null){
            val simpleDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val date = simpleDate.parse(clubInfoItem.createDate)
            binding.tvClubCreateDate.text = SimpleDateFormat("yyyy-MM-dd").format(date).toString()
        }else{
            binding.tvClubCreateDate.text =clubInfoItem.createDate
        }
        //클럽 멤버 숫자
        settingClubMemberCount(clubInfoItem.memberCount,clubInfoItem.memberCountOpenYn)
        //클럽 설명
        binding.tvClubDescription.text=clubInfoItem.introduction
        //클럽 좋아요 표시
        changeFavoriteIcon(clubInfoItem.favoriteYN)
    }

    fun clickFunc(){
        binding.ivClubBookmark.setOnClickListener {
            clubDialogClickListener.onBookMarkClick(this)
        }

        binding.btnClubShare.setOnClickListener {
            clubDialogClickListener.onShareClick()
            dismiss()
        }
        binding.btnDialogClose.setOnClickListener {
            dismiss()
        }
    }

    fun changeFavoriteIcon(isFavorite :Boolean){
        if(isFavorite){
            context?.let {
                binding.ivClubBookmark.setColorFilter(it.getColor(R.color.state_enable_secondary_default))
            }
        }else{
            context?.let {
                binding.ivClubBookmark.setColorFilter(it.getColor(R.color.gray_200))
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    private fun settingClubMemberCount(memberCount :Int,isMemberCountOpen :Boolean){
        if(!isMemberCountOpen){
            binding.tvClubMember.text=getString(R.string.b_hidden)
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

        binding.tvClubMember.text ="${changeCount} ${countVariationText}"
    }

    interface ClubInfoDialogClickListener{
        fun onBookMarkClick(dialog : DialogFragment)
        fun onShareClick()
    }

}