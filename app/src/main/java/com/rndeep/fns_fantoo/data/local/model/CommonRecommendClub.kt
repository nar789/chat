package com.rndeep.fns_fantoo.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.rndeep.fns_fantoo.data.remote.model.RecommendationClub

@Entity(tableName = "tb_recommend_club")
data class CommonRecommendClub(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "recommend_club_id")
    var recommendId :Int=0,
    @ColumnInfo(name = "recommend_club_group_title")
    var title:String="",
    @ColumnInfo(name = "recommend_club_group_subtitle")
    var subText:String="",
    @ColumnInfo(name = "recommend_club_list")
    var recommendClubList: List<RecommendationClub> = listOf(),
    @ColumnInfo(name = "recommend_club_page_type")
    var pageType :String = "noting"
)
data class CommonRecommendClubList(
    @SerializedName("clubManageId")
    @ColumnInfo(name = "recommend_club_id")
    val clubManageId: Int,
    @SerializedName("clubName")
    @ColumnInfo(name = "recommend_club_title")
    var title: String="",
    @SerializedName("profileImgUrl")
    @ColumnInfo(name = "recommend_club_thumbnail")
    var clubThumbnail: String?="",
    @SerializedName("clubSearchWord")
    @ColumnInfo(name = "recommend_club_tags")
    var clubTag:List<String>? = listOf(),
)

data class CommonRecommendSelect(
    @ColumnInfo(name = "recommend_club_group_title")
    var title:String="",
    @ColumnInfo(name = "recommend_club_group_subtitle")
    var subText:String="",
    @ColumnInfo(name = "recommend_club_list")
    var recommendClubList: List<RecommendationClub> = listOf(),
    @ColumnInfo(name = "recommend_club_page_type")
    var pageType :String = "noting"
)

class RecommendClubConverters {
    @TypeConverter
    fun fromCommonRecommendClubListHolder(array: List<RecommendationClub>): String {
        return Gson().toJson(array)
    }

    @TypeConverter
    fun toCommonRecommendClubListHolder(str: String): List<RecommendationClub> {
        return Gson().fromJson(str, Array<RecommendationClub>::class.java).toList()
    }

}