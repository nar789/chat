package com.rndeep.fns_fantoo.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.rndeep.fns_fantoo.data.local.model.CurationDataItem

@Dao
interface CurationDao {

    @Query("SELECT * FROM tb_popular_curation")
    suspend fun getAllCuration():List<CurationDataItem>

    @Query("SELECT * FROM tb_popular_curation")
    fun getAllCurationLiveData(): LiveData<List<CurationDataItem>>

    @Insert
    suspend fun interItems(vararg item: CurationDataItem)

    @Insert
    suspend fun insertItemList(itemList :List<CurationDataItem>) :List<Long>

    @Delete
    suspend fun deleteItem(item : CurationDataItem)

    @Query("Delete FROM tb_popular_curation")
    suspend fun deleteAllItem()

}