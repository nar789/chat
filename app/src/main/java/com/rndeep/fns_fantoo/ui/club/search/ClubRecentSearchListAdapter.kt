package com.rndeep.fns_fantoo.ui.club.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.TabClubRecentSearchLayoutBinding
import kotlin.collections.ArrayList

class ClubRecentSearchListAdapter :RecyclerView.Adapter<ClubRecentSearchListAdapter.RecentSearchListVH>() {

    private var searchText =ArrayList<String>()

    interface OnWordClickListener{
        fun onDeleteIconClick(v: View,text:String,position: Int)
        fun onWordClick(v:View,text:String,position: Int)
    }

    var clickListener : OnWordClickListener? =null

    fun setOnDeleteClickListener(click : OnWordClickListener){
        clickListener=click
    }

    inner class RecentSearchListVH(private val binding : TabClubRecentSearchLayoutBinding) :RecyclerView.ViewHolder(binding.root){

        fun bind(item:String, listener : OnWordClickListener?){
            binding.tvSearchText.text=item
            binding.tvSearchText.setOnClickListener {
                listener?.onWordClick(itemView,item,bindingAdapterPosition)
            }

            binding.ivDeleteSearch.setOnClickListener {
                listener?.onDeleteIconClick(itemView,item,bindingAdapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentSearchListVH {
        val view =LayoutInflater.from(parent.context).inflate(R.layout.tab_club_recent_search_layout,parent,false)
        return RecentSearchListVH(TabClubRecentSearchLayoutBinding.bind(view))
    }

    override fun onBindViewHolder(holder: RecentSearchListVH, position: Int) {
        holder.bind(searchText[holder.bindingAdapterPosition],clickListener)
    }

    override fun getItemCount()=searchText.size

    fun setSearchText(searchText:ArrayList<String>){
        this.searchText=searchText
        notifyItemRangeChanged(0,if(this.searchText.size==0) 1 else this.searchText.size )
    }

    fun removeItem(text:String,pos:Int){
        this.searchText.remove(text)
        notifyItemRemoved(pos)
    }

    fun clearAllSearchText(){
        val oldSize =this.searchText.size
        this.searchText.clear()
        notifyItemRangeRemoved(0,if(this.searchText.size==0) oldSize else this.searchText.size )
    }

    fun addText(text:String){
        val oldPos=searchText.size
        this.searchText.add(text)
        notifyItemInserted(oldPos+1)
    }
}