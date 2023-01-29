package com.rndeep.fns_fantoo.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.rndeep.fns_fantoo.data.local.model.BannerItem

@Dao
interface HomeBannerDao {

    @Query("SELECT * FROM tb_home_banner")
    suspend fun getAllBanner():List<BannerItem>

//    @Query("SELECT * FROM tb_home_banner")
//    fun getAllBannerLiveData():LiveData<List<BannerItem>>

    @Insert
    suspend fun interItems(vararg item: BannerItem)

    @Insert
    suspend fun insertItemList(itemList :List<BannerItem>) :List<Long>

    @Delete
    suspend fun deleteItem(item : BannerItem)

    @Query("Delete FROM tb_home_banner")
    suspend fun deleteAllItem()

}
