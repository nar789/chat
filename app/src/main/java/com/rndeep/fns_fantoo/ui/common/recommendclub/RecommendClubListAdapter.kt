package com.rndeep.fns_fantoo.ui.common.recommendclub

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.local.model.CommonRecommendClubList
import com.rndeep.fns_fantoo.data.remote.model.RecommendationClub
import com.rndeep.fns_fantoo.databinding.TabHomeRecommendClubLayoutBinding
import com.rndeep.fns_fantoo.utils.SizeUtils
import com.rndeep.fns_fantoo.utils.setImageWithPlaceHolder
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

class RecommendClubListAdapter : ListAdapter<RecommendationClub,RecommendClubListAdapter.RecommendClubListVH>(diff) {

    interface OnRecommendClubClickListener {
        fun onClubClick(v: View, id: String, name: String)
        fun onJoinClick(v: View, id: String, name: String)
    }

    private var recommendClubClickListener: OnRecommendClubClickListener? = null

    fun setOnRecommendClubClickListener(recommendClubClickListener: OnRecommendClubClickListener?) {
        this.recommendClubClickListener = recommendClubClickListener
    }

    inner class RecommendClubListVH(private val binding: TabHomeRecommendClubLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val tagAdapter = RecommendClubTagAdapter()

        @SuppressLint("UseCompatLoadingForDrawables", "ClickableViewAccessibility")
        fun recommendListBind(
            clubItem: RecommendationClub,
            listener: OnRecommendClubClickListener?
        ) {
            //추천 클럽의 이름
            binding.tvClubName.text = clubItem.clubName

            //클럽 태그는 FlexBoxLayoutManager 로
            binding.rcClubTagList.layoutManager =
                FlexboxLayoutManager(itemView.context, FlexDirection.ROW).apply {
                    justifyContent = JustifyContent.FLEX_START
                    maxLine=2
                }
            clubItem.clubSearchWord?.let {
                tagAdapter.setItems(it as ArrayList<String>)
            }
            tagAdapter.setTextColor(itemView.context.getColor(R.color.gray_900))
            binding.rcClubTagList.adapter = tagAdapter

            //썸네일이 null 일 경우 기본 이미지
            Glide.with(itemView.context)
                .load(clubItem.profileImgUrl)
                .apply(RequestOptions())
                .transform(CenterCrop(),RoundedCornersTransformation(
                    SizeUtils.getDpValue(14f,itemView.context).toInt(),
                    0,
                    RoundedCornersTransformation.CornerType.BOTTOM))
                .error(itemView.context.getDrawable(R.drawable.club_no_image))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.ivClubThumbnail)
            binding.ivClubThumbnail.clipToOutline=true

            itemView.setOnClickListener {
                listener?.onClubClick(itemView, clubItem.clubManageId, clubItem.clubName)
            }
            binding.rcClubTagList.setOnTouchListener { _,motionEvent->
                if(motionEvent.action == MotionEvent.ACTION_UP){
//                    listener?.onClubClick(itemView, clubItem.clubManageId, clubItem.clubName)
                    itemView.performClick()
                }else{
                    false
                }
            }
            when(clubItem.status){
                0->{
                    binding.tvRecommendClubJoin.setOnClickListener {
                        listener?.onJoinClick(itemView, clubItem.clubManageId, clubItem.clubName)
                    }
                    binding.tvRecommendClubJoin.background=itemView.context.getDrawable(R.drawable.bg_btn_club_main_join)
                    binding.tvRecommendClubJoin.includeFontPadding=false
                    binding.tvRecommendClubJoin.text=itemView.context.getString(R.string.g_to_join)
                }
                1,2->{
                    //1 : 가입 , 2: 운영자
                    binding.tvRecommendClubJoin.text=itemView.context.getString(R.string.g_to_joined)
                    binding.tvRecommendClubJoin.background=itemView.context.getDrawable(R.drawable.bg_btn_club_main_joined)
                    binding.tvRecommendClubJoin.includeFontPadding=false
                }
                3->{
                    //3 : 가입중
                    binding.tvRecommendClubJoin.text=itemView.context.getString(R.string.g_to_wait_join)
                    binding.tvRecommendClubJoin.background=itemView.context.getDrawable(R.drawable.bg_btn_club_main_joined)
                    binding.tvRecommendClubJoin.includeFontPadding=false
                }
                else ->{
                    binding.tvRecommendClubJoin.setOnClickListener {
                        listener?.onJoinClick(itemView, clubItem.clubManageId, clubItem.clubName)
                    }
                    binding.tvRecommendClubJoin.background=itemView.context.getDrawable(R.drawable.bg_btn_club_main_join)
                    binding.tvRecommendClubJoin.includeFontPadding=false
                    binding.tvRecommendClubJoin.text=itemView.context.getString(R.string.g_to_join)
                }
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecommendClubListVH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.tab_home_recommend_club_layout, parent, false)
        return RecommendClubListVH(TabHomeRecommendClubLayoutBinding.bind(view))
    }

    override fun onBindViewHolder(holder: RecommendClubListVH, position: Int) {
        holder.recommendListBind(
            currentList[holder.bindingAdapterPosition],
            recommendClubClickListener
        )
    }

    companion object{
        val diff = object : DiffUtil.ItemCallback<RecommendationClub>(){
            override fun areItemsTheSame(
                oldItem: RecommendationClub,
                newItem: RecommendationClub
            ): Boolean {
                return oldItem==newItem
            }

            override fun areContentsTheSame(
                oldItem: RecommendationClub,
                newItem: RecommendationClub
            ): Boolean {
                return (oldItem.clubManageId==newItem.clubManageId)
            }
        }
    }
}