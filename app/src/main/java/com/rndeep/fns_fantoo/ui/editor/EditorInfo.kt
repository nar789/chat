package com.rndeep.fns_fantoo.ui.editor

import android.os.Parcelable
import com.rndeep.fns_fantoo.data.remote.model.editor.ModifyCommunityPost
import kotlinx.parcelize.Parcelize

@Parcelize
data class EditorInfo(
    var editorType: EditorType,
    var modifyCommunityPost: ModifyCommunityPost?,
    var clubId: String?,
    var clubMemberLevel: Int?
) : Parcelable