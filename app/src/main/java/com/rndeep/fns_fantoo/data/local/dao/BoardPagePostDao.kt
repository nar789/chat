package com.rndeep.fns_fantoo.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.rndeep.fns_fantoo.data.local.model.BoardPagePosts
import com.rndeep.fns_fantoo.data.local.model.CommonRecommendClub
import com.rndeep.fns_fantoo.data.local.model.CommonRecommendSelect

@Dao
interface BoardPagePostDao {

    @Query("SELECT * FROM tb_board_post")
    suspend fun getAllBoardPostItem():List<BoardPagePosts>

    @Query("SELECT * FROM tb_board_post WHERE board_post_show_position LIKE :showPos")
    suspend fun getAllBoardPostItemOfShowType(showPos :String):List<BoardPagePosts>

    @Query("SELECT * FROM tb_board_post WHERE board_pk_id LIKE :pkId")
    suspend fun getBoardPostItem(pkId :Int) : BoardPagePosts

    @Query("SELECT * FROM tb_board_post WHERE community_integUid LIKE '%' || :auth_id AND club_integUid LIKE '%' || :auth_id")
    suspend fun getPostItemWithAuthID(auth_id:String):List<BoardPagePosts>

    @Insert
    suspend fun insertItems(vararg item: BoardPagePosts) : List<Long>

    @Insert
    suspend fun insertItemList(itemList :List<BoardPagePosts>) : List<Long>

    @Update
    suspend fun updateItem(item : BoardPagePosts) :Int

    @Update
    suspend fun updateItemList(items : List<BoardPagePosts>) :Int

    @Delete
    suspend fun deleteItem(item : BoardPagePosts)

    @Query("Delete FROM tb_board_post ")
    suspend fun deleteAllItem()

    @Query("Delete FROM tb_board_post WHERE board_post_show_position LIKE :showPos")
    suspend fun deleteAllItemOfShowType(showPos: String)

}