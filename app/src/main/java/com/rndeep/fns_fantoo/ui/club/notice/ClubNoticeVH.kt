package com.rndeep.fns_fantoo.ui.club.notice

import android.text.SpannableString
import android.text.Spanned
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.model.post.ClubPostData
import com.rndeep.fns_fantoo.databinding.ClubNoticeLayoutBinding
import com.rndeep.fns_fantoo.utils.*

class ClubNoticeVH(
    private val binding: ClubNoticeLayoutBinding,
    private val listener: ClubNoticeAdapter.ClubNoticeClickListener?
) :RecyclerView.ViewHolder(binding.root) {

    fun noticeBind(noticeItem : ClubPostData){

        setProfileAvatar(binding.ivNoticeThumbnail,noticeItem.profileImg)

        binding.tvNoticeCategory.text=noticeItem.categoryName2

        binding.tvNoticeNickName.text=noticeItem.nickname

        binding.tvNoticeDate.text=TimeUtils.diffTimeWithCurrentTime(noticeItem.createDate)

        settingHeadTitle(
            noticeItem.content,
            noticeItem.attachList.isNullOrEmpty()
        )

        binding.tvCommentCount.text= noticeItem.replyCount?.toString()?:"0"

        clickFunc(noticeItem)

    }

    private fun clickFunc(noticeItem: ClubPostData){
        binding.ivNoticeOptions.setOnClickListener {
            listener?.onOptionClick(noticeItem.postId)
        }
        itemView.setOnClickListener {
            listener?.onItemClick(noticeItem.postId,noticeItem.clubId,noticeItem.categoryCode)
        }
    }

    private fun settingHeadTitle(text: String?, isAttach: Boolean) {
        val imageHeight = SizeUtils.getDpValue(16f, itemView.context).toInt()
        val imageSpan = if (isAttach) {
            itemView.context.getDrawable(R.drawable.posting_file)
                ?.apply {
                    setBounds(0, 0, imageHeight, imageHeight)
                }?.let {
                    VerticalImageSpan(it)
                }
        } else {
            itemView.context.getDrawable(R.drawable.posting_text)
                ?.apply {
                    setBounds(0, 0, imageHeight, imageHeight)
                }?.let {
                    VerticalImageSpan(it)
                }
        }
        val s = SpannableString("   ${text}").apply {
            setSpan(imageSpan, 1, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        binding.tvNoticeContent.text = s
    }

}