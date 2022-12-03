package com.rndeep.fns_fantoo.ui.common.recommendclub

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.local.model.CommonRecommendSelect
import com.rndeep.fns_fantoo.databinding.CommonRecommendClubBinding
import com.rndeep.fns_fantoo.utils.ConstVariable
import com.rndeep.fns_fantoo.utils.HorizontalMarginItemDecoration
import com.rndeep.fns_fantoo.utils.addSingleItemDecoRation
import timber.log.Timber

class ClubRecommendVH(private val binding: CommonRecommendClubBinding) :
    RecyclerView.ViewHolder(binding.root) {


    private val recommendClubSideMarginDeco =
        HorizontalMarginItemDecoration(10f, -5f, itemView.context)
    private val recommendTagDeco = HorizontalMarginItemDecoration(20f, 5f, itemView.context)
    val recommendClubListAdapter = RecommendClubListAdapter()
    fun recommendBind(
        item: CommonRecommendSelect,
        recommendClubClick: RecommendClubListAdapter.OnRecommendClubClickListener?
    ) {
        //추천 클럽의 생성 위치에 따라 backGroundColor 가 다르다.
        if (item.pageType == ConstVariable.DB_CLUBPAGE_NEW || item.pageType == ConstVariable.DB_CLUBPAGE_HOT) {
            binding.recommendClubContainer.setBackgroundColor(itemView.context.getColor(R.color.gray_25))
            binding.ivRecommendCharacter.visibility = View.GONE
        } else {
            binding.recommendClubContainer.setBackgroundColor(itemView.context.getColor(R.color.bg_bg_light_gray_50))
            binding.ivRecommendCharacter.visibility = View.VISIBLE
        }
        //추천 클럽 형식 (인기 추천,신규 추천 등)
        binding.tvClubRecommend.text = item.title
        //추천클럽 형식에 맞는 서브 설명
        binding.tvClubRecommendSubTitle.text = item.subText

        //추천 클럽 리스트
        binding.clNoClubList.visibility=View.GONE
        binding.rcClubRecommendList.visibility=View.VISIBLE
        binding.rcClubRecommendList.layoutManager =
            LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
        recommendClubListAdapter.submitList(item.recommendClubList)
        recommendClubListAdapter.setOnRecommendClubClickListener(recommendClubClick)
        binding.rcClubRecommendList.adapter = recommendClubListAdapter
        binding.rcClubRecommendList.addSingleItemDecoRation(recommendClubSideMarginDeco)

        //인기 클럽 추천일 경우 Category 를 사용한다.
        when (item.pageType) {
            ConstVariable.DB_CLUBPAGE_HOT -> {
                val sortAdapter = RecommendClubSortAdapter()
                binding.rcMyClubCategory.visibility = View.VISIBLE
                binding.rcMyClubCategory.layoutManager =
                    LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
                binding.rcMyClubCategory.addSingleItemDecoRation(recommendTagDeco)
                binding.rcMyClubCategory.adapter = sortAdapter
            }
            else -> {
                binding.rcMyClubCategory.visibility = View.GONE
            }
        }

    }
}