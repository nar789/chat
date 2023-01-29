package com.rndeep.fns_fantoo.ui.club.create

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.dto.ClubInterestCategoryDto
import com.rndeep.fns_fantoo.data.remote.model.club.ClubInterestItem
import com.rndeep.fns_fantoo.databinding.FragmentClubCreateInterestLayoutBinding

class ClubCreateInterestListAdapter :RecyclerView.Adapter<ClubCreateInterestListAdapter.ClubCreateInterestViewHolder>(){
    inner class ClubCreateInterestViewHolder(private val binding : FragmentClubCreateInterestLayoutBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item : ClubInterestCategoryDto, listener: InterestClickListener?, selectPos:Int?){
            binding.tvInterestText.text=item.description
            if(selectPos==bindingAdapterPosition) binding.interestContainer.background=itemView.context.getDrawable(R.drawable.bg_club_create_category_active)
            else binding.interestContainer.background = itemView.context.getDrawable(R.drawable.bg_club_create_category)
            itemView.setOnClickListener {
                listener?.interestClick(itemView,bindingAdapterPosition,item.clubInterestCategoryId)
            }
        }
    }

    private var interestItems = listOf<ClubInterestCategoryDto>()

    private var selectPos : Int? =null

    interface InterestClickListener{
        fun interestClick(v : View,pos : Int,item: Int)
    }

    private var interestClickListener : InterestClickListener? =null

    fun setOnInterestClickListener(listener: InterestClickListener){
        this.interestClickListener=listener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ClubCreateInterestViewHolder {
        val view =LayoutInflater.from(parent.context).inflate(R.layout.fragment_club_create_interest_layout,parent,false)
        return ClubCreateInterestViewHolder(FragmentClubCreateInterestLayoutBinding.bind(view))
    }

    override fun onBindViewHolder(holder: ClubCreateInterestViewHolder, position: Int) {
        holder.bind(interestItems[holder.bindingAdapterPosition],interestClickListener,selectPos)
    }

    override fun getItemCount()=interestItems.size

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(items :List<ClubInterestCategoryDto>){
        this.interestItems=items
        notifyDataSetChanged()
    }

    fun selectItem(pos : Int){
        if(selectPos!=null){
            val oldPos =selectPos
            selectPos=pos
            notifyItemChanged(oldPos!!)
            notifyItemChanged(selectPos!!)
        }else{
            selectPos=pos
            notifyItemChanged(selectPos!!)
        }
    }

    fun getSelectItem()=interestItems[selectPos!!].clubInterestCategoryId
}