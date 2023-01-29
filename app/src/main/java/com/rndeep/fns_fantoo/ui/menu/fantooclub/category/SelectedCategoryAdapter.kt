package com.rndeep.fns_fantoo.ui.menu.fantooclub.category

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.databinding.ListItemSelectedCategoryBinding

class SelectedCategoryAdapter(
    private val section: String,
    private val onItemClicked: (Unit) -> Unit
) : RecyclerView.Adapter<SelectedCategoryAdapter.SectionViewHolder>() {

    inner class SectionViewHolder(private val binding: ListItemSelectedCategoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.section.text = section
            binding.toList.setOnClickListener {
                onItemClicked(Unit)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SectionViewHolder {
        val binding = ListItemSelectedCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SectionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SectionViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int {
        return 1
    }
}