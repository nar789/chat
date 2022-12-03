package com.rndeep.fns_fantoo.ui.editor

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class EditorInfo(
    var editorType: EditorType,
    var clubId: String?,
    var clubMemberLevel: Int?
) : Parcelable