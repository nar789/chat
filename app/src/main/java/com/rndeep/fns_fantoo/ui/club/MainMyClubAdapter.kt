package com.rndeep.fns_fantoo.ui.club

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.model.MainClub
import com.rndeep.fns_fantoo.data.remote.model.MyClubListItem
import com.rndeep.fns_fantoo.databinding.TabClubMyClubLayoutBinding
import com.rndeep.fns_fantoo.utils.setProfileAvatar

class MainMyClubAdapter() : RecyclerView.Adapter<MainMyClubAdapter.MyClubVH>() {
    private var myClubList = listOf<MyClubListItem>()
    inner class MyClubVH(private val binding: TabClubMyClubLayoutBinding) :RecyclerView.ViewHolder(binding.root){

        fun bind(item:MyClubListItem){
            setProfileAvatar(binding.ivClubProfile,itemView.context.getString(R.string.imageThumbnailUrl,item.profileImg))

            binding.tvClubText.text=item.clubName

            itemView.setOnClickListener {
                val action = ClubTabFragmentDirections.actionClubTabFragmentToClubPageDetail(item.clubId)
                itemView.findNavController().navigate(action)
            }

            binding.tvManagerCheck.visibility=if(item.isOwner) View.VISIBLE else View.GONE

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyClubVH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.tab_club_my_club_layout,parent,false)
        return MyClubVH(TabClubMyClubLayoutBinding.bind(view))
    }

    override fun onBindViewHolder(holder: MyClubVH, position: Int) {
        if(holder.bindingAdapterPosition==RecyclerView.NO_POSITION){
            return
        }
        holder.bind(myClubList[holder.bindingAdapterPosition])
    }

    override fun getItemCount()=myClubList.size

    @SuppressLint("NotifyDataSetChanged")
    fun setItem(items:List<MyClubListItem>){
        this.myClubList=items
        notifyDataSetChanged()
    }
}