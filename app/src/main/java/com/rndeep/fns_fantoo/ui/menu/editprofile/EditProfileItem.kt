package com.rndeep.fns_fantoo.ui.menu.editprofile

import androidx.annotation.StringRes
import com.rndeep.fns_fantoo.R


data class EditProfileItem(
    var itemType: EditProfileItemType,
    var value: String?,
    val isIcon: Boolean
)

enum class EditProfileItemType(@StringRes val stringRes: Int) {
    AVATAR(R.string.p_edit_profile), NICKNAME(R.string.n_nickname),
    CONCERN(R.string.g_interests), COUNTRY(R.string.g_country), GENDER(R.string.s_gender), BIRTHDAY(R.string.s_birthday)
}