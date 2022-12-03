package com.rndeep.fns_fantoo.ui.home.tabhome

import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.databinding.CategoryHomeBannerBinding
import com.rndeep.fns_fantoo.data.local.model.BannerItem
import com.rndeep.fns_fantoo.utils.HorizontalMarginItemDecoration
import com.rndeep.fns_fantoo.utils.addSingleItemDecoRation

class HomeCategoryBannerVH(private val binding: CategoryHomeBannerBinding) : RecyclerView.ViewHolder(binding.root){
    private var recyclerviewState : Parcelable? =null

    private var bannerSize = 0

    private var autoSlider = Handler(Looper.getMainLooper())

    private val sliderRunnable = Runnable {
        binding.rcBanner.smoothScrollToPosition((binding.rcBanner.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()+1)
    }

    private val itemDeco = HorizontalMarginItemDecoration(20f,20f,itemView.context)

    private var currentPos=0

    fun bannerBind(bannerData :List<BannerItem>){
        if (bannerData.isEmpty()){
            itemView.visibility= View.GONE
        }else{
            itemView.visibility= View.VISIBLE
            bannerSize=bannerData.size
            val vpAdapter = HomeBannerRcAdapter()
            vpAdapter.setItems(bannerData)
            binding.rcBanner.adapter=vpAdapter
            binding.rcBanner.layoutManager=LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL,false)

            recyclerviewState?.let {
                (binding.rcBanner.layoutManager as LinearLayoutManager).onRestoreInstanceState(it)
            }

            settingRCSetting()
        }
    }

    private fun settingRCSetting(){

        // 페이징 처리 도와주는 Helper
        if(binding.rcBanner.onFlingListener==null){
            val snapHelper = PagerSnapHelper()
            snapHelper.attachToRecyclerView(binding.rcBanner)
        }

        //간격 주는 itemDecoration
        binding.rcBanner.addSingleItemDecoRation(itemDeco)

        //infinite scroll
        binding.rcBanner.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val lastItemPosition =(recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                if(lastItemPosition!=-1 && (currentPos!=lastItemPosition)){
                    val itemTotalCount = recyclerView.adapter?.itemCount?.minus(1)
                    currentPos=lastItemPosition
                    autoSlider.removeCallbacks(sliderRunnable)
                    autoSlider.postDelayed(sliderRunnable,5000)
                    binding.tvBannerCounting.text ="${(lastItemPosition%bannerSize)+1}/${bannerSize}"

                    if(recyclerView.adapter is HomeBannerRcAdapter){
                        //마지막 아이템
                        if(lastItemPosition == itemTotalCount) {
                            binding.rcBanner.post {
                                (recyclerView.adapter as HomeBannerRcAdapter).addAllItem()
                            }
                        }
                    }
                    //view가 재생성 될 시 position 유지를 위해 상태 저장
                    recyclerviewState= (binding.rcBanner.layoutManager as LinearLayoutManager).onSaveInstanceState()
                }
            }
        })

    }

    fun startBannerSlider(){
        //자동 배너 이동 시작시 트리거
        autoSlider.postDelayed(sliderRunnable,5000)
    }

    fun stopBannerSlider(){
        //자동 배너 이동 시작시 트리거
        autoSlider.removeCallbacks(sliderRunnable)
    }
}