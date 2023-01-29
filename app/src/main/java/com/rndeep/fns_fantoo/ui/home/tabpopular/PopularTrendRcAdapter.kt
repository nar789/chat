package com.rndeep.fns_fantoo.ui.home.tabpopular

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.local.model.TrendTagItem
import com.rndeep.fns_fantoo.ui.home.HomeTabFragmentDirections

class PopularTrendRcAdapter : RecyclerView.Adapter<PopularTrendRcAdapter.TrendRcVH>() {


    private var testTag = listOf<TrendTagItem>()

    inner class TrendRcVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(testText: TrendTagItem) {

            itemView.findViewById<TextView>(R.id.tvTrendText).text = "#${testText.trendTagName}"

            itemView.setOnClickListener {
                itemView.findNavController().navigate(
                    HomeTabFragmentDirections.actionHomeTabFragmentToTrendPostFragment(testText.trendTagName)
                )
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrendRcVH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.category_popular_trend_img_layout, parent, false)

        return TrendRcVH(view)
    }

    override fun onBindViewHolder(holder: TrendRcVH, position: Int) {
        if (holder.bindingAdapterPosition == RecyclerView.NO_POSITION) {
            return
        }
        holder.bind(testTag[holder.bindingAdapterPosition])
    }

    override fun getItemCount() = testTag.size

    fun setItems(items: List<TrendTagItem>) {
        this.testTag = items
        notifyItemRangeChanged(0, testTag.size)
    }

}
