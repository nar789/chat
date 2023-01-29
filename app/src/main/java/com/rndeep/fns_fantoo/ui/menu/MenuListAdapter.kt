package com.rndeep.fns_fantoo.ui.menu

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.*
import timber.log.Timber

class MenuListAdapter(
    private val onItemClicked: (MenuItem) -> Unit
) : ListAdapter<MenuItem, RecyclerView.ViewHolder>(MenuDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        TYPE_TITLE -> {
            val binding =
                ListItemMenuTitleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            MenuTitleViewHolder(binding)
        }
        TYPE_ITEM -> {
            val binding =
                ListItemMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            MenuViewHolder(binding)
        }
        TYPE_ACCOUNT_INFO -> {
            val binding =
                ListItemMenuAccountBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            MenuAccountViewHolder(binding)
        }
        TYPE_VERSION -> {
            val binding =
                ListItemMenuVersionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            MenuVersionViewHolder(binding)
        }
        TYPE_SWITCH -> {
            val binding =
                ListItemMenuSwitchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            MenuSwitchViewHolder(binding)
        }
        else -> {
            throw IllegalStateException("Not Found ViewHolder Type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when(holder) {
            is MenuViewHolder -> holder.bind(item as MenuItem.Item)
            is MenuTitleViewHolder -> holder.bind(item as MenuItem.Title)
            is MenuAccountViewHolder -> holder.bind(item as MenuItem.AccountItem)
            is MenuVersionViewHolder -> holder.bind(item as MenuItem.VersionItem)
            is MenuSwitchViewHolder -> holder.bind(item as MenuItem.SwitchItem)
        }
    }

    override fun getItemViewType(position: Int) = when(getItem(position)) {
        is MenuItem.Title -> TYPE_TITLE
        is MenuItem.AccountItem -> TYPE_ACCOUNT_INFO
        is MenuItem.VersionItem -> TYPE_VERSION
        is MenuItem.SwitchItem -> TYPE_SWITCH
        else -> TYPE_ITEM
    }

    inner class MenuViewHolder(private val binding: ListItemMenuBinding) :
        RecyclerView.ViewHolder(binding.root) {

        internal fun bind(item: MenuItem.Item) {
            binding.apply {
                val visibility = if (item.value != null) View.VISIBLE else View.GONE
                itemMenuMoreIc.visibility = visibility
                when(item.icon) {
                    IconType.IMAGE -> {
                        itemMenuMoreIc.visibility = View.VISIBLE
                    }
                    else -> {
                        itemMenuMoreIc.visibility = View.GONE
                    }
                }
                itemValue.text = item.value
                itemMenuTitle.text = root.context.getString(item.name.stringRes)
                root.setOnClickListener {
                    onItemClicked(item)
                }
            }
        }
    }

    inner class MenuTitleViewHolder(private val binding: ListItemMenuTitleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MenuItem.Title) {
            binding.apply {
                title.text = item.title
            }
        }
    }

    inner class MenuAccountViewHolder(private val binding: ListItemMenuAccountBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MenuItem.AccountItem) {
            binding.apply {
                itemTitle.text = root.context.getString(item.name.stringRes)
                itemValue.text = item.value
                val loginIcon = getAccountIcon(item.icon)
                accountImg.setImageResource(loginIcon)
            }
        }

        private fun getAccountIcon(type: LoginType) = when (type) {
            LoginType.APPLE -> R.drawable.btn_logo_apple
            LoginType.GOOGLE -> R.drawable.btn_logo_google
            LoginType.FACEBOOK -> R.drawable.btn_logo_facebook
            LoginType.LINE -> R.drawable.btn_logo_line
            LoginType.KAKAO -> R.drawable.btn_logo_kakaotalk
            LoginType.EMAIL -> R.drawable.btn_logo_email
        }
    }

    inner class MenuVersionViewHolder(private val binding: ListItemMenuVersionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MenuItem.VersionItem) {
            binding.apply {
                itemTitle.text = root.context.getString(item.name.stringRes)
                itemValue.text = item.version
                if(item.isUpdate) {
                    updateBtn.isEnabled = true
                    updateBtn.backgroundTintList = ColorStateList.valueOf(root.context.getColor(R.color.primary_default))
                    updateBtn.setOnClickListener {
                        onItemClicked(item)
                    }
                }
            }
        }
    }

    inner class MenuSwitchViewHolder(private val binding: ListItemMenuSwitchBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MenuItem.SwitchItem) {
            binding.apply {
                itemTitle.text = root.context.getString(item.name.stringRes)
                menuSwitch.isChecked = item.isChecked
                menuSwitch.setOnCheckedChangeListener { compoundButton, b ->
                    item.isChecked = b
                    onItemClicked(item)
                }
            }
        }
    }

    companion object {
        private const val TYPE_TITLE = 0
        private const val TYPE_ITEM = 1
        private const val TYPE_ACCOUNT_INFO = 2
        private const val TYPE_VERSION = 3
        private const val TYPE_SWITCH = 4

        private val MenuDiff = object : DiffUtil.ItemCallback<MenuItem>() {
            override fun areItemsTheSame(oldItem: MenuItem, newItem: MenuItem): Boolean {
                val isMenuSame = oldItem is MenuItem.Item
                        && newItem is MenuItem.Item
                        && oldItem.value == newItem.value
                val isAccountSame = oldItem is MenuItem.AccountItem
                        && newItem is MenuItem.AccountItem
                        && oldItem.value == newItem.value
                val isSwitchSame = oldItem is MenuItem.SwitchItem
                        && newItem is MenuItem.SwitchItem
                        && oldItem.isChecked == newItem.isChecked
                val isVersionSame = oldItem is MenuItem.VersionItem
                        && newItem is MenuItem.VersionItem
                        && oldItem.isUpdate == newItem.isUpdate

                return isMenuSame && isAccountSame && isSwitchSame && isVersionSame
            }

            override fun areContentsTheSame(oldItem: MenuItem, newItem: MenuItem): Boolean {
                return oldItem == newItem
            }
        }
    }

}