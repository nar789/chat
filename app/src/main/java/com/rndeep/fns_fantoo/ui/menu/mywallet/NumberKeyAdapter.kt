package com.rndeep.fns_fantoo.ui.menu.mywallet

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.ListItemNumberKeyBinding

class NumberKeyAdapter(
    private val keyItem: ArrayList<String>,
    private val onItemClicked: (String) -> Unit
) : RecyclerView.Adapter<NumberKeyAdapter.NumberKeyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NumberKeyViewHolder {
        val binding =
            ListItemNumberKeyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NumberKeyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NumberKeyViewHolder, position: Int) {
        val item = keyItem[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = keyItem.size

    inner class NumberKeyViewHolder(private val binding: ListItemNumberKeyBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val itemClicked = onItemClicked
        fun bind(item: String) {
            binding.apply {
                number.text = item
                if(item.toInt() < 0) {
                    number.text = ""
                    number.setIconResource(R.drawable.icon_outline_delate)
                    number.setIconTintResource(R.color.black)
                    number.iconGravity = MaterialButton.ICON_GRAVITY_TEXT_TOP
                    number.iconPadding = 0
                }
                number.setOnClickListener {
                    itemClicked(item)
                }
            }
        }
    }

}