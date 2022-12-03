package com.rndeep.fns_fantoo.ui.club.challenge

import android.annotation.SuppressLint
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.CategoryHomePostBinding
import com.rndeep.fns_fantoo.data.remote.model.ClubChallengeItem
import com.rndeep.fns_fantoo.data.remote.model.post.ClubPostAttachList
import com.rndeep.fns_fantoo.utils.*
import javax.security.auth.Subject

class ClubChallengeAdapter() : RecyclerView.Adapter<ClubChallengeAdapter.ClubChallengeVH>() {

    private var challengeItem = listOf<ClubChallengeItem>()

    inner class ClubChallengeVH(private val binding: CategoryHomePostBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun challengeBind(item: ClubChallengeItem) {
            binding.apply {
                clPostContainer.background=itemView.context.getDrawable(R.color.gray_25)
                ivPostProfileBoardName.visibility = View.GONE
                ivGrayDot.visibility = View.GONE
                tvPostContent.visibility = View.GONE
                flThumbnails.visibility = View.GONE
                llOptionContainer.visibility = View.GONE
                llHonorContainer.visibility = View.GONE
                llCommentContainer.visibility=View.GONE
                clOgTagContainer.visibility = View.GONE

                clPostContainer.elevation=0f
                setProfileAvatar(ivPostProfileThumbnail,null,R.drawable.profile_character_manager)
                ivPostProfileNickName.text = "FANTOO"
                ivPostCreateTime.text = TimeUtils.diffTimeWithCurrentTime(item.createDate)

                item.subject = if(item.status==1){
                    itemView.context.getString(R.string.se_s_post_hide_by_report)
                }else if(item.status==2){
                    itemView.context.getString(R.string.se_j_deleted_comment_by_writer)
                }else if(item.blockType==1){
                    itemView.context.getString(R.string.c_block_account_title)
                }else if(item.blockType==2){
                    itemView.context.getString(R.string.se_c_blocked_post)
                }else{
                    item.subject
                }
                setSubjectText(item.status,item.blockType,item.attachList,item.subject)
            }

            if(item.status==0 && item.blockType==0)
                itemView.setOnClickListener {
                    itemView.findNavController().navigate(
                        ClubChallengeFragmentDirections.actionClubChallengeFragmentToClubPost(
                            ConstVariable.TYPE_CLUB_CHALLENGE,"","-1",item.id
                        )
                    )
                }
        }

        private fun setSubjectText(status :Int, blockType:Int,attachType:List<ClubPostAttachList>?,subject: String){
            if(status!=0 || blockType !=0){
                binding.tvPostTitle.setTextColor(itemView.context.getColor(R.color.gray_400))
                binding.tvPostTitle.setTextAppearance(R.style.Body21420Regular)
                binding.tvPostTitle.text = subject
            }else{
                binding.tvPostTitle.setTextColor(itemView.context.getColor(R.color.gray_870))
                settingImageHeader(attachType,subject)
            }
        }

        private fun settingImageHeader(imageType: List<ClubPostAttachList>?, originTitle: String) {
            val imageHeight = SizeUtils.getDpValue(16f, itemView.context).toInt()
            val imageSpan = if(imageType.isNullOrEmpty()){
                    itemView.context.getDrawable(R.drawable.posting_text)
                        ?.apply {
                            setBounds(0, 0, imageHeight, imageHeight)
                        }?.let {
                            VerticalImageSpan(it)
                        }
                }else{
                    when(imageType[0].attachType){
                        1,0  -> {
                            itemView.context.getDrawable(R.drawable.posting_file)
                                ?.apply {
                                    setBounds(0, 0, imageHeight, imageHeight)
                                }?.let {
                                    VerticalImageSpan(it)
                                }
                        }
                        else -> {
                            itemView.context.getDrawable(R.drawable.posting_text)
                                ?.apply {
                                    setBounds(0, 0, imageHeight, imageHeight)
                                }?.let {
                                    VerticalImageSpan(it)
                                }
                        }
                    }
                }
            imageSpan ?: return
            val sbString = SpannableStringBuilder("  $originTitle").apply {
                setSpan(imageSpan, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            binding.tvPostTitle.setTextAppearance(R.style.Body21420Regular)
            binding.tvPostTitle.text = sbString
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClubChallengeVH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.category_home_post, parent, false)
        return ClubChallengeVH(CategoryHomePostBinding.bind(view))
    }

    override fun onBindViewHolder(holder: ClubChallengeVH, position: Int) {
        val item = challengeItem[holder.bindingAdapterPosition]
        holder.challengeBind(item)
    }

    override fun getItemCount() = challengeItem.size

    @SuppressLint("NotifyDataSetChanged")
    fun setChallengeItem(items: List<ClubChallengeItem>) {
        challengeItem = items
        notifyDataSetChanged()
    }

}