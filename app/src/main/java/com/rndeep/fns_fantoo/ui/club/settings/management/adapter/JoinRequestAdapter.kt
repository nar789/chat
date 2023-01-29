package com.rndeep.fns_fantoo.ui.club.settings.management.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.model.clubsetting.ClubJoinWaitMemberWithMeta
import com.rndeep.fns_fantoo.databinding.TabClubSettingJoinItemBinding
import com.rndeep.fns_fantoo.utils.setProfileAvatar
import timber.log.Timber

class JoinRequestAdapter(val headerView: com.rndeep.fns_fantoo.databinding.TabClubSettingJoinItemHeaderBinding) :
    PagingDataAdapter<UiDataSelectWrapper, RecyclerView.ViewHolder>(JoinRequestUserDiffCallback()) {

    var checkBoxChangeListener: CheckBoxChangeListener? = null
    var allItemCheckedListener: AllItemCheckedListener? = null

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewHoder: RecyclerView.ViewHolder
        headerView.root.setOnClickListener {
            if (itemCount != 0) {
                val bCheck = !headerView.cbSelect.isChecked
                headerView.cbSelect.isChecked = bCheck
                snapshot().items.let {
                    for (data in it){
                        data.isSelected = bCheck
                    }
                    notifyDataSetChanged()
                }
                allItemCheckedListener?.onAllItemChecked(bCheck)
            }
        }
        viewHoder = RequestListVH(
            TabClubSettingJoinItemBinding.bind(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.tab_club_setting_join_item, parent, false
                )
            )
        )
        return viewHoder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        getItem(position)?.let {
            (holder as RequestListVH).bind(it)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun removeCheckedItems() {
        val currentMutableList = snapshot().toMutableList()
        currentMutableList.removeAll {
            it!!.isSelected
        }
        headerView.cbSelect.isChecked = false
        notifyDataSetChanged()
    }

    inner class RequestListVH(val binding: TabClubSettingJoinItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(data: UiDataSelectWrapper) {
            val profileUrl = ""
            if (profileUrl.isNotEmpty()) {
                setProfileAvatar(binding.ivProfile, profileUrl)
            }

            binding.cbSelect.isChecked = data.isSelected
            binding.cbSelect.setOnCheckedChangeListener { buttonView, isChecked ->
                checkBoxChangeListener?.let {
                    data.isSelected = isChecked
                    //it.onCheckChanged(data)
                    it.onSelectedDataList(getCheckedItemList())
                    headerView.cbSelect.isChecked = isAllItemChecked()
                }
            }
            binding.tvNick.text =
                data.clubJoinWaitMemberWithMeta.clubJoinWaitMember.nickname
            binding.tvRequestDate.text =
                binding.root.context.getString(R.string.s_application_date)+
                data.clubJoinWaitMemberWithMeta.clubJoinWaitMember.createDate
            binding.root.setOnClickListener {
                binding.cbSelect.isChecked = !binding.cbSelect.isChecked
            }
        }
    }

    private fun isAllItemChecked(): Boolean {
        var allItemChecked = true
        for (wrapData in snapshot().items) {
            if (!wrapData.isSelected) {
                allItemChecked = false
                break
            }
        }
        return allItemChecked
    }

    fun getCheckedItemList(): ArrayList<UiDataSelectWrapper> {
        val selectedDataList = ArrayList<UiDataSelectWrapper>()
        for (wrapData: UiDataSelectWrapper in snapshot().items) {
            if (wrapData.isSelected) {
                selectedDataList.add(wrapData)
            }
        }
        return selectedDataList
    }

    interface AllItemCheckedListener {
        fun onAllItemChecked(isAllItemChecked: Boolean)
    }

    interface CheckBoxChangeListener {
        fun onCheckChanged(wrapperData: UiDataSelectWrapper)
        fun onSelectedDataList(selectedDataArrayList: ArrayList<UiDataSelectWrapper>)
    }

    class JoinRequestUserDiffCallback : DiffUtil.ItemCallback<UiDataSelectWrapper>() {
        override fun areItemsTheSame(oldItem: UiDataSelectWrapper, newItem: UiDataSelectWrapper): Boolean {
            return oldItem.clubJoinWaitMemberWithMeta.clubJoinWaitMember.joinId == newItem.clubJoinWaitMemberWithMeta.clubJoinWaitMember.joinId
        }

        override fun areContentsTheSame(
            oldItem: UiDataSelectWrapper,
            newItem: UiDataSelectWrapper
        ): Boolean {
            return oldItem == newItem
        }

    }
}

data class UiDataSelectWrapper(val clubJoinWaitMemberWithMeta: ClubJoinWaitMemberWithMeta, var isSelected: Boolean)