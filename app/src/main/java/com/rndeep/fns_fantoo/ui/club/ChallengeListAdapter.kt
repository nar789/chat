package com.rndeep.fns_fantoo.ui.club

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.TabClubChallengeLayoutBinding
import com.rndeep.fns_fantoo.data.remote.model.ClubChallengeItem
import com.rndeep.fns_fantoo.utils.ConstVariable

class ChallengeListAdapter : RecyclerView.Adapter<ChallengeListAdapter.ChallengeListVH>() {
    private var challengeItem :List<ClubChallengeItem> = listOf()

    inner class ChallengeListVH(private val binding: TabClubChallengeLayoutBinding) :RecyclerView.ViewHolder(binding.root){

        fun challengeBind(item : ClubChallengeItem){
            if(item.type==ConstVariable.CHALLENGE_NO_ITEM){
                setNoChallengeView()
            }else{
                binding.ivChallengeIcon.visibility=View.VISIBLE
                binding.tvChallengeTitle.gravity=Gravity.START
                binding.tvChallengeTitle.text=if(item.status==1){
                    itemView.context.getString(R.string.se_s_post_hide_by_report)
                }else if(item.status==2) {
                    itemView.context.getString(R.string.se_j_deleted_post_by_writer)
                }else if(item.blockType==1){
                    itemView.context.getString(R.string.c_block_account_title)
                }else if(item.blockType==2){
                    itemView.context.getString(R.string.se_c_blocked_post)
                }else{
                    item.subject
                }
                binding.tvChallengeTitle.setTextAppearance(R.style.Body21420Regular)
                binding.tvChallengeTitle.setTextColor(
                    if(item.status==0 && item.blockType ==0){
                        itemView.context.getColor(R.color.gray_900)
                    }else{
                        itemView.context.getColor(R.color.gray_400)
                    }
                )

                if(item.status==0 && item.blockType==0)
                    itemView.setOnClickListener {
                        //챌린지 터치
                        itemView.findNavController().navigate(
                            ClubTabFragmentDirections.actionClubTabFragmentToClubPost(
                                ConstVariable.TYPE_CLUB_CHALLENGE,"","-1",item.id
                            )
                        )
                    }
            }
        }

        fun setNoChallengeView(){
            binding.ivChallengeIcon.visibility=View.GONE
            binding.tvChallengeTitle.text=itemView.context.getString(R.string.se_j_not_exist_active_challenge)
            binding.tvChallengeTitle.gravity=Gravity.CENTER
            binding.tvChallengeTitle.setTextAppearance(
                R.style.Caption11218Regular
            )
            binding.tvChallengeTitle.setTextColor(
                itemView.context.getColor(R.color.gray_500)
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChallengeListVH {
        val view =LayoutInflater.from(parent.context).inflate(R.layout.tab_club_challenge_layout,parent,false)
        return ChallengeListVH(TabClubChallengeLayoutBinding.bind(view))
    }

    override fun onBindViewHolder(holder: ChallengeListVH, position: Int) {
        holder.challengeBind(challengeItem[holder.bindingAdapterPosition])
    }

    override fun getItemCount()=challengeItem.size

    fun setChallengeItem(items : List<ClubChallengeItem>){
        this.challengeItem=items
        notifyItemChanged(0,challengeItem.size)
    }
}