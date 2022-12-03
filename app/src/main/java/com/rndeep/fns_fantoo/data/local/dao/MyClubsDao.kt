package com.rndeep.fns_fantoo.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rndeep.fns_fantoo.data.local.model.MyClubs
import kotlinx.coroutines.flow.Flow

@Dao
interface MyClubsDao {

    @Query("SELECT * FROM my_clubs ORDER BY club_id ASC")
    fun getClubs(): Flow<List<MyClubs>>

    @Query("SELECT * FROM my_clubs WHERE club_title = :clubName ORDER BY club_id ASC")
    fun getClubByName(clubName: String): Flow<List<MyClubs>>

    @Query("SELECT * FROM my_clubs WHERE club_id = :clubId")
    fun getClubById(clubId: Long): Flow<MyClubs>

    @Query("SELECT * FROM my_clubs WHERE is_favorite = 1")
    fun getClubByFavorited(): Flow<List<MyClubs>>

    @Query("SELECT COUNT(*) FROM my_clubs ")
    fun getClubCount() : Flow<Int>

    @Query("SELECT COUNT(*) FROM my_clubs WHERE is_favorite = 1")
    fun getFavoritedClubCount() : Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addClub(club: MyClubs)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(clubs: List<MyClubs>)

    @Query("DELETE FROM my_clubs")
    suspend fun deleteAll()
}