package com.rndeep.fns_fantoo.ui.community.mypage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.TabHomeMainViewBinding
import com.rndeep.fns_fantoo.utils.VerticalMarginItemDecoration

class MyPageAdapter : RecyclerView.Adapter<MyPageAdapter.CategoryPagerVH>() {

    private var adapterList = ArrayList<RecyclerView.Adapter<RecyclerView.ViewHolder>>()

    interface OnRefreshItem {
        fun onRefresh(v: SwipeRefreshLayout, currentAdapter :RecyclerView.Adapter<RecyclerView.ViewHolder>)
    }

    private var onRefreshItem: OnRefreshItem? = null

    fun setOnRefreshItem(refreshItem: OnRefreshItem) {
        this.onRefreshItem = refreshItem
    }

    interface OnScrollBottomDetectListener {
        fun isScrollBottom(v: View,currentAdapter :RecyclerView.Adapter<RecyclerView.ViewHolder>)
    }

    private var onScrollDetectListener: OnScrollBottomDetectListener? = null

    fun setOnScrollBottomDetectListener(onScrollDetect: OnScrollBottomDetectListener) {
        this.onScrollDetectListener = onScrollDetect
    }

    inner class CategoryPagerVH(private val binding: TabHomeMainViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var currentLast = false
        private val verticalItemDecoration = VerticalMarginItemDecoration(0f, 4f, itemView.context)
        fun bind(
            adapterItem: RecyclerView.Adapter<RecyclerView.ViewHolder>,
            refreshItem: OnRefreshItem?,
            bottomDetectListener: OnScrollBottomDetectListener?
        ) {
            binding.rcCategory.layoutManager = LinearLayoutManager(itemView.context)
            binding.rcCategory.removeItemDecoration(verticalItemDecoration)
            binding.rcCategory.addItemDecoration(verticalItemDecoration)
            binding.rcCategory.adapter = adapterItem
            binding.swipeRefresh.setOnRefreshListener {
                refreshItem?.onRefresh(binding.swipeRefresh,adapterItem)
            }
            val animator = binding.rcCategory.itemAnimator
            if (animator is SimpleItemAnimator) {
                (animator as SimpleItemAnimator).supportsChangeAnimations = false
            }
            binding.rcCategory.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val lastItemPosition =
                        (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                    val itemTotalCount = recyclerView.adapter?.itemCount?.minus(1)
                    if (itemTotalCount != null) {
                        if (itemTotalCount < 4) {
                            return
                        }
                        if (lastItemPosition == itemTotalCount) {
                            //마지막 아이템
                            if (!currentLast) {
                                currentLast = true
                                bottomDetectListener?.isScrollBottom(itemView,adapterItem)
                            }
                        } else {
                            currentLast = false
                        }
                    }
                }
            })
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryPagerVH {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.tab_home_main_view, parent, false)
        return CategoryPagerVH(TabHomeMainViewBinding.bind(view))
    }

    override fun onBindViewHolder(holder: CategoryPagerVH, position: Int) {
        if (holder.bindingAdapterPosition == RecyclerView.NO_POSITION) {
            return
        }
        holder.bind(
            adapterList[holder.bindingAdapterPosition],
            onRefreshItem,
            onScrollDetectListener
        )
    }

    override fun getItemCount() = adapterList.size

    fun setAdapterList(adapters: ArrayList<RecyclerView.Adapter<RecyclerView.ViewHolder>>) {
        this.adapterList = adapters
    }

}