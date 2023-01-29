package com.rndeep.fns_fantoo.ui.club.settings.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.ui.common.RecyclerViewItemClickListener
import com.rndeep.fns_fantoo.databinding.BottomSheetMenuItemsBinding
import com.rndeep.fns_fantoo.ui.club.settings.data.MenuItem
import timber.log.Timber

class MenuBottomAdapter(): RecyclerView.Adapter<MenuBottomAdapter.MenuItemVH>(){

    private var menuItemArrayList:ArrayList<MenuItem> = ArrayList()
    private lateinit var selectedMenuItem: MenuItem
    private var menuItemClickListener : RecyclerViewItemClickListener? = null

    constructor(menuItemArrayList:ArrayList<MenuItem>):this(){
        this.menuItemArrayList = menuItemArrayList
    }

    fun setMenuItemArrayList(menuItemArrayList:ArrayList<MenuItem>){
        this.menuItemArrayList = menuItemArrayList
    }

    fun setOnMenuItemClickListener(itemClickListener: RecyclerViewItemClickListener){
        menuItemClickListener = itemClickListener
    }

    fun setSelectMenuItem(menuItem: MenuItem){
        selectedMenuItem = menuItem
    }

    inner class MenuItemVH(val binding:BottomSheetMenuItemsBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(menuItem:MenuItem){
            val isSelectedItem = if(::selectedMenuItem.isInitialized){(menuItem == selectedMenuItem)}else false
            binding.tvMenu1.isEnabled = isSelectedItem
            binding.tvMenu1.text = menuItem.title1
            binding.tvMenu2.text = menuItem.title2
            binding.ivIcon.visibility = if(isSelectedItem) View.VISIBLE else View.INVISIBLE
            binding.root.setOnClickListener {
                menuItemClickListener?.onItemClick(menuItem)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuItemVH {
        return MenuItemVH(BottomSheetMenuItemsBinding.bind(LayoutInflater.from(parent.context).inflate(
            R.layout.club_settings_bottom_sheet_menu_items, parent, false)))
    }

    override fun onBindViewHolder(holder: MenuItemVH, position: Int) {
        try {
            holder.bind(menuItemArrayList[position])
        }catch (e:IndexOutOfBoundsException){
            Timber.e("MenuBottomAdapter IndexOutOfBoundsException.")
        }
    }

    override fun getItemCount(): Int {
        return menuItemArrayList.size
    }
}