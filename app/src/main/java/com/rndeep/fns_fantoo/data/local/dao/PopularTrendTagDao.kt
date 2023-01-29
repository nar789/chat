package com.rndeep.fns_fantoo.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.rndeep.fns_fantoo.data.local.model.TrendTagItem

@Dao
interface PopularTrendTagDao {
    @Query("SELECT * FROM tb_popular_trend_tag")
    suspend fun getAllTrendTag():List<TrendTagItem>

    @Query("SELECT * FROM tb_popular_trend_tag")
    fun getAllTrendTagLiveData():LiveData<List<TrendTagItem>>

    @Insert
    suspend fun insertTagItems(vararg items: TrendTagItem)

    @Insert
    suspend fun insertTagItemList(itemList :List<TrendTagItem>) :List<Long>

    @Delete
    suspend fun deleteTagItem(item : TrendTagItem)

    @Query("DELETE FROM tb_popular_trend_tag")
    suspend fun deleteAllTagItem()

}