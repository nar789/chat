package com.rndeep.fns_fantoo.ui.home

import android.app.Activity
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.databinding.CategoryHomeNotLoginBinding
import com.rndeep.fns_fantoo.ui.home.tabhome.CategoryHomeAdapter

class HomeNotLoginVH(
    private val binding: CategoryHomeNotLoginBinding,
    private val loginListener: CategoryHomeAdapter.OnLoginClickListener?
) : RecyclerView.ViewHolder(binding.root) {
    fun bind() {
        binding.tvLoginButton.setOnClickListener {
            loginListener?.onLoginCLick()
//            val intent = Intent(itemView.context, LoginActivity::class.java)
//            itemView.context.startActivity(intent)
//            (itemView.context as Activity).finish()
        }
    }
}