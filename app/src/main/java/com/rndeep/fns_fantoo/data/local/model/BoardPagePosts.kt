package com.rndeep.fns_fantoo.data.local.model

import androidx.room.*
import com.google.gson.Gson
import com.rndeep.fns_fantoo.data.remote.model.post.ClubPostData
import com.rndeep.fns_fantoo.data.remote.model.post.BoardPostData
import com.rndeep.fns_fantoo.data.remote.model.community.DetailAttachList
import com.rndeep.fns_fantoo.data.remote.model.post.ClubPostAttachList
import com.rndeep.fns_fantoo.data.remote.model.post.HonorItem

/**
 * boardPkId             : DB의 primarykey
 * type                  : 글의 타입 (Notice,Community 등등 )
 * boardPostItem         : 게시글 아이템
 * boardPostShowPosition : 게시글이 보여지는 곳의 위치
 */
@Entity(tableName = "tb_board_post")
data class BoardPagePosts(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "board_pk_id")
    val boardPkId :Int=0,
    @ColumnInfo(name = "board_post_type")
    val type :String,
    @Embedded(prefix = "community_")
    var boardPostItem : BoardPostData?=null,
    @Embedded(prefix = "club_")
    var clubPostData : ClubPostData?=null,
    @ColumnInfo(name = "board_post_show_position")
    val boardPostShowPosition :String
)

class BoardPostConverters {
    @TypeConverter
    fun fromBoardPostDataHolder(array: BoardPostData?): String {
        return Gson().toJson(array)
    }

    @TypeConverter
    fun toBoardPostDataHolder(str: String): BoardPostData? {
        return Gson().fromJson(str, BoardPostData::class.java)
    }
    @TypeConverter
    fun fromClubPostDataHolder(array: ClubPostData?): String {
        return Gson().toJson(array)
    }

    @TypeConverter
    fun toClubPostDataHolder(str: String): ClubPostData? {
        return Gson().fromJson(str, ClubPostData::class.java)
    }

    @TypeConverter
    fun fromDetailAttachListHolder(array :List<DetailAttachList>?):String{
        return Gson().toJson(array)
    }

    @TypeConverter
    fun toDetailAttachListHolder(str :String?):List<DetailAttachList>{
        val arrStr =if(str=="null") "[]" else str
        return Gson().fromJson(arrStr,Array<DetailAttachList>::class.java).toList()
    }

    @TypeConverter
    fun fromClubPostAttachListHolder(array :List<ClubPostAttachList>?):String{
        return Gson().toJson(array)
    }

    @TypeConverter
    fun toClubPostAttachListHolder(str :String?):List<ClubPostAttachList>{
        val arrStr =if(str=="null") "[]" else str
        return Gson().fromJson(arrStr,Array<ClubPostAttachList>::class.java).toList()
    }

    @TypeConverter
    fun fromLikeDisLikeItemHolder(array: HonorItem?): String {
        return Gson().toJson(array)
    }

    @TypeConverter
    fun toLikeDisLikeItemHolder(str: String): HonorItem? {
        return Gson().fromJson(str, HonorItem::class.java)
    }



}

