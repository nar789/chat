package com.rndeep.fns_fantoo.ui.home

import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.data.local.model.AdInfo
import com.rndeep.fns_fantoo.databinding.CategoryHomePostBinding
import com.rndeep.fns_fantoo.utils.*

class HomeCategoryAdVH(private val binding : CategoryHomePostBinding): RecyclerView.ViewHolder(binding.root){

    fun postSetting(item : AdInfo?, postType:String){
        itemView.visibility=if(item==null) View.GONE else View.VISIBLE
        settingViewType(postType)
        item?.let {
            settingTopView(it.ADImage.ko,it.ADTitle.ko,it.adContents.ko)
            settingContentView(it.ADTitle.ko,it.ADImage.ko)
            binding.rlBottomView.visibility= View.GONE
            settingClickFunc(it.ADLink.varue)
        }
    }

    private fun settingViewType(type:String){
        val lp1 = (binding.tvPostTitle.layoutParams as RelativeLayout.LayoutParams)
        val lp2 = (binding.flThumbnails.layoutParams as RelativeLayout.LayoutParams)

        if(type== ConstVariable.POST_TYPE_FEED){
            lp1.width= ViewGroup.LayoutParams.MATCH_PARENT
            lp2.width= ViewGroup.LayoutParams.MATCH_PARENT
            lp2.height = SizeUtils.getDpValue(250f,itemView.context).toInt()
            lp2.topMargin= SizeUtils.getDpValue(10f,itemView.context).toInt()
            lp2.rightMargin=0

            lp2.addRule(RelativeLayout.BELOW,binding.tvPostTitle.id)
            lp2.removeRule(RelativeLayout.RIGHT_OF)
            lp2.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT)

            binding.flThumbnails.visibility=View.VISIBLE
            binding.tvPostContent.visibility=View.VISIBLE
            binding.tvPostContent.setLines(4)
        }else if(type==ConstVariable.POST_TYPE_LIST){
            lp1.width= ViewGroup.LayoutParams.MATCH_PARENT
            binding.flThumbnails.visibility=View.GONE
            binding.tvPostContent.visibility=View.GONE
        }else{
            lp1.width= SizeUtils.getDpValue(270f,itemView.context).toInt()
            lp2.width= SizeUtils.getDpValue(100f,itemView.context).toInt()
            lp2.height = SizeUtils.getDpValue(100f,itemView.context).toInt()
            lp2.topMargin=0
            lp2.rightMargin= SizeUtils.getDpValue(10f,itemView.context).toInt()

            lp2.removeRule(RelativeLayout.BELOW)
            lp2.addRule(RelativeLayout.RIGHT_OF, binding.tvPostTitle.id)
            lp2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
        }


    }

    private fun settingTopView(thumbnail:String, nickName:String, boardName:String){

        setProfileAvatar(binding.ivPostProfileThumbnail,thumbnail)
        binding.ivPostProfileThumbnail.clipToOutline=true

        binding.ivPostProfileNickName.text=nickName

        binding.ivPostProfileBoardName.text="${boardName}"

    }

    private fun settingContentView(contentTitle:String,thumbnailList:String){
        binding.tvPostTitle.text=contentTitle

        setImageWithPlaceHolder(binding.ivThumbnailFirst,thumbnailList)

        binding.thumbnailGrid.visibility=View.VISIBLE
        binding.ivThumbnailFirst.visibility=View.VISIBLE
        binding.ivThumbnailSecond.visibility=View.GONE
        binding.flThumbnailThird.visibility=View.GONE
        binding.clOgTagContainer.visibility=View.GONE

    }

    private fun settingClickFunc(url :String){
        binding.rlContentContainer.setOnClickListener {

        }
    }


}