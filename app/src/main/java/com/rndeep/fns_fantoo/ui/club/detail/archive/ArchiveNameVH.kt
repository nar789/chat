package com.rndeep.fns_fantoo.ui.club.detail.archive

import android.view.View
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.databinding.TabClubPageArchiveTopNameLayoutBinding
import com.rndeep.fns_fantoo.ui.club.detail.ClubPageListAdapter
import com.rndeep.fns_fantoo.utils.ConstVariable

class ArchiveNameVH(
    private val binding: TabClubPageArchiveTopNameLayoutBinding,
    private val archiveType: String,
    private val listener : ArchiveImageAdapter.ArchiveTopTypeClickListener?
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(boardName: String) {
        binding.tvBoardName.text = boardName
        when(archiveType){
            ConstVariable.ARCHIVEIMAGETYPE->{
                binding.archiveDivider.visibility=View.GONE
            }
            ConstVariable.ARCHIVEPOSTTYPE->{
                binding.archiveDivider.visibility=View.VISIBLE
            }
        }
        binding.ivBackList.setOnClickListener {
            listener?.onListClick()
        }
    }

}