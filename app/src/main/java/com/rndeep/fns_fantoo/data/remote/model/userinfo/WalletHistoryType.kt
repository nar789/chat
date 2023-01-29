package com.rndeep.fns_fantoo.data.remote.model.userinfo

import androidx.annotation.StringRes
import com.rndeep.fns_fantoo.R

enum class WalletHistoryType(@StringRes val stringRes: Int) {
    ALL(R.string.j_all), PAID(R.string.j_saving), USED(R.string.s_use)
}