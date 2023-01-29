package com.rndeep.fns_fantoo.ui.common.recommendclub

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.android.flexbox.AlignContent
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.model.RecommendationClub
import com.rndeep.fns_fantoo.databinding.TabHomeRecommendClubLayoutBinding
import com.rndeep.fns_fantoo.utils.SizeUtils
import com.rndeep.fns_fantoo.utils.setProfileAvatar
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecommendClubListAdapter : ListAdapter<RecommendationClub,RecommendClubListAdapter.RecommendClubListVH>(diff) {

    interface OnRecommendClubClickListener {
        fun onClubClick(v: View, id: Int, name: String)
        fun onJoinClick(v: View, id: Int, name: String)
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
                    //flexWrap = FlexWrap.WRAP
                    alignItems = AlignItems.FLEX_START
                    justifyContent = JustifyContent.FLEX_START
                    //maxLine=2  //아이템이 많을시 각아이템이 개행처리되어 일부분만 표시됨 주석처리
                }
            clubItem.hashtagList?.let {
                tagAdapter.setItems(it as ArrayList<String>)
            }
            tagAdapter.setTextColor(itemView.context.getColor(R.color.gray_900))
            binding.rcClubTagList.adapter = tagAdapter

            setProfileAvatar(binding.ivClubProfileImage,itemView.context.getString(R.string.imageThumbnailUrl,clubItem.profileImgUrl))

            //썸네일이 null 일 경우 기본 이미지
            Glide.with(itemView.context)
                .load(itemView.context.getString(R.string.imageThumbnailUrl,clubItem.bgImg))
                .apply(RequestOptions())
                    //ShapeableImageView로 대체
                /*.transform(CenterCrop(),RoundedCornersTransformation(
                    SizeUtils.getDpValue(14f,itemView.context).toInt(),
                    0,
                    RoundedCornersTransformation.CornerType.BOTTOM))*/
                .error(itemView.context.getDrawable(R.drawable.profile_main_club_bg))
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
//
//            if(clubItem.joinStatus){
//                binding.tvRecommendClubJoin.text=itemView.context.getString(R.string.g_to_joined)
//                binding.tvRecommendClubJoin.background=itemView.context.getDrawable(R.drawable.bg_btn_club_main_joined)
//                binding.tvRecommendClubJoin.includeFontPadding=false
//            }else{
//                binding.tvRecommendClubJoin.setOnClickListener {
//                    listener?.onJoinClick(itemView, clubItem.clubManageId, clubItem.clubName)
//                }
//                binding.tvRecommendClubJoin.background=itemView.context.getDrawable(R.drawable.bg_btn_club_main_join)
//                binding.tvRecommendClubJoin.includeFontPadding=false
//                binding.tvRecommendClubJoin.text=itemView.context.getString(R.string.g_to_join)
//            }

            when(clubItem.joinStatus){
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