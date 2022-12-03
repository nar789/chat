package com.rndeep.fns_fantoo.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.rndeep.fns_fantoo.data.local.model.CommonRecommendClub
import com.rndeep.fns_fantoo.data.local.model.CommonRecommendSelect

@Dao
interface RecommendClubDao {

    @Query("SELECT * FROM tb_recommend_club")
    suspend fun getAllRecommendClub():List<CommonRecommendClub>

    @Query("SELECT recommend_club_group_title,recommend_club_group_subtitle,recommend_club_list,recommend_club_page_type FROM tb_recommend_club WHERE recommend_club_page_type LIKE :type")
    fun getAllRecommendClubLiveData(type: String): LiveData<CommonRecommendSelect>

    @Query("SELECT recommend_club_group_title,recommend_club_group_subtitle,recommend_club_list,recommend_club_page_type FROM tb_recommend_club WHERE recommend_club_page_type LIKE :type")
    suspend fun getRecommendClubAllItemOfType(type: String): CommonRecommendSelect

    @Insert
    suspend fun interItems(vararg item: CommonRecommendClub) : List<Long>

    @Insert
    suspend fun insertItemList(itemList :List<CommonRecommendClub>) : List<Long>

    @Delete
    suspend fun deleteItem(item : CommonRecommendClub)

    @Query("Delete FROM tb_recommend_club WHERE recommend_club_page_type LIKE :type")
    suspend fun deleteAllItem(type:String)
}