package com.rndeep.fns_fantoo.ui.club

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rndeep.fns_fantoo.databinding.TabClubAdBinding

class ClubCategoryAdVH(private val binding : TabClubAdBinding): RecyclerView.ViewHolder(binding.root){


    private fun settingTopView(thumbnail:String, nickName:String, boardName:String){

        binding.tvAdNickName.text=nickName

        binding.tvAdInfo.text= boardName

    }

    private fun settingContentView(contentTitle:String,thumbnailList:String){
        binding.tvPostTitle.text=contentTitle

        Glide.with(itemView.context)
            .load(thumbnailList)
            .into(binding.ivPostContentThumbnail)
        binding.ivPostContentThumbnail.visibility= View.VISIBLE
    }

    private fun settingClickFunc(){
        itemView.setOnClickListener {

        }
    }


}