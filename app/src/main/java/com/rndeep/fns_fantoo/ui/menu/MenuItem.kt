package com.rndeep.fns_fantoo.ui.menu

import androidx.annotation.StringRes
import com.rndeep.fns_fantoo.R

sealed class MenuItem {
    data class Title(
        val title: String
    ) : MenuItem()

    data class Item(
        val name: MenuItemType,
        var value: String?,
        val icon: IconType
    ) : MenuItem()

    data class AccountItem(
        val name: MenuItemType,
        var value: String?,
        var icon: LoginType
    ) : MenuItem()

    data class VersionItem(
        val name: MenuItemType,
        val version: String,
        val isUpdate: Boolean
    ) : MenuItem()

    data class SwitchItem(
        val name: MenuItemType,
        var isChecked: Boolean
    ) : MenuItem()
}

enum class MenuItemType(@StringRes val stringRes: Int) {

    // MENU
    MY_CLUB(R.string.n_my_club),
    MY_WALLET(R.string.n_my_wallet),
    INVITE_FRIEND(R.string.c_invite_friend),
    EVENT(R.string.a_event),
    FANTOO_TV(R.string.p_fantoo_tv),
    HANRYU_TIMES(R.string.h_hanryutimes),

    // SETTINGS
    ACCOUNT_INFO(R.string.g_account_info),
    SELECT_TRANSLATE_LANGUAGE(R.string.b_select_trans_language),
    PUSH_NOTIFICATION_SETTING(R.string.p_setting_push_alarm),
    MARKETING_AGREE(R.string.m_agree_marketing_recieved),
    CURRENT_VERSION(R.string.h_current_version),
    NOTICE(R.string.g_notice),
    CONTACT_EMAIL(R.string.a_inquiry_email),
    TERMS_OF_SERVICE(R.string.s_term_use_service),
    PRIVACY_POLICY(R.string.g_term_privacy_info),
    YOUTH_PROTECTION_POLICY(R.string.c_term_youth),

    // EDIT PROFILE
    AVATAR(R.string.p_edit_profile),
    NICKNAME(R.string.n_nickname),
    CONCERN(R.string.g_interests),
    COUNTRY(R.string.g_country),
    GENDER(R.string.s_gender),
    BIRTHDAY(R.string.s_birthday),

    // ACCOUNT INFO
    SIGNUP_ACCOUNT(R.string.g_join_account),
    CHANGE_PASSWORD(R.string.b_change_password),
    LOGOUT(R.string.r_logout),
    DELETE_ACCOUNT(R.string.s_leave),
    QR_AUTH(R.string.a_certification_with_qr),

    // PUSH
    PUSH_COMMUNITY(R.string.k_community),
    PUSH_CLUB(R.string.k_club)
}

enum class IconType {
    NONE, IMAGE, SWITCH, BUTTON
}

enum class LoginType {
    APPLE, GOOGLE, FACEBOOK, KAKAO, LINE, EMAIL
}
