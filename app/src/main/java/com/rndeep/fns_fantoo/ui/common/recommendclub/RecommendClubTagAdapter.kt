package com.rndeep.fns_fantoo.ui.common.recommendclub

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R

class RecommendClubTagAdapter : RecyclerView.Adapter<RecommendClubTagAdapter.ClubTagRcVH>() {


    private var clubTags = ArrayList<String>()
    private var textColor :Int? =null

    inner class ClubTagRcVH(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(tagStr :String,color :Int?) {
            val view =itemView.findViewById<TextView>(R.id.tvTrendText)
            view.text="#${tagStr}"
            view.setTextAppearance(R.style.Caption11218Regular)
            view.background=null
            color?.let {
                view.setTextColor(it)
            }
            val container =itemView.findViewById<LinearLayout>(R.id.llTagContainer)
            container.setPadding(0,0,0,0)
            view.setPadding(0,0,5,0)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClubTagRcVH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.category_popular_trend_img_layout, parent, false)

        return ClubTagRcVH(view)
    }

    override fun onBindViewHolder(holder: ClubTagRcVH, position: Int) {
        if(holder.bindingAdapterPosition== RecyclerView.NO_POSITION){
            return
        }
        holder.bind(clubTags[holder.bindingAdapterPosition],textColor)
    }

    override fun getItemCount() = clubTags.size

    fun setItems(items : ArrayList<String>){
        this.clubTags=items
        notifyItemRangeChanged(0,clubTags.size)
    }

    fun setTextColor(color :Int){
        textColor=color
        notifyItemRangeChanged(0,clubTags.size)
    }
}