package com.rndeep.fns_fantoo.ui.menu.myclub

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.ListItemMyClubBinding
import com.rndeep.fns_fantoo.data.remote.model.MyClubListItem
import com.rndeep.fns_fantoo.utils.getImageUrlFromCDN
import com.rndeep.fns_fantoo.utils.setProfileAvatar
import java.text.DecimalFormat

class MyClubsAdapter(
    private val onItemClicked: (MyClubListItem) -> Unit
) : ListAdapter<MyClubListItem, MyClubsAdapter.MyClubsViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyClubsViewHolder {
        val binding =
            ListItemMyClubBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyClubsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyClubsViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class MyClubsViewHolder(private val binding: ListItemMyClubBinding) :
        RecyclerView.ViewHolder(binding.root) {

        internal fun bind(item: MyClubListItem) {
            binding.apply {

                setProfileAvatar(clubImg, getImageUrlFromCDN(root.context, item.profileImg))

                clubTitle.text = item.clubName
                val decimalFormat = DecimalFormat(" #,###")
                clubMemberCnt.text = decimalFormat.format(item.memberCount)
                if (item.manageYn) {
                    clubAdmin.visibility = View.VISIBLE
                } else {
                    clubAdmin.visibility = View.GONE
                }
//                if (item.openYn) clubAdmin.visibility = View.VISIBLE else clubAdmin.visibility =
//                    View.GONE
                val color: Int =
                    if (item.favoriteYn) {
                        ContextCompat.getColor(binding.root.context, R.color.secondary_default)
                    } else {
                        ContextCompat.getColor(binding.root.context, R.color.gray_200)
                    }
                favoriteImg.imageTintList = ColorStateList.valueOf(color)

                root.setOnClickListener {
                    onItemClicked(item)
                }
            }
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<MyClubListItem>() {
            override fun areItemsTheSame(oldItem: MyClubListItem, newItem: MyClubListItem): Boolean {
                return oldItem.clubId == newItem.clubId
            }

            override fun areContentsTheSame(oldItem: MyClubListItem, newItem: MyClubListItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}