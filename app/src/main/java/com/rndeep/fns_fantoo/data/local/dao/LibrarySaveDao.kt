package com.rndeep.fns_fantoo.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rndeep.fns_fantoo.data.local.model.LibrarySave
import kotlinx.coroutines.flow.Flow

@Dao
interface LibrarySaveDao {

    @Query("SELECT * FROM library_save ORDER BY id ASC")
    fun getLibrarySaves(): Flow<List<LibrarySave>>

    @Query("SELECT * FROM library_save WHERE save_type = :type ORDER BY id ASC")
    fun getLibrarySavesByType(type: Int): Flow<List<LibrarySave>>

    @Query("SELECT COUNT(*) FROM library_save ")
    fun getLibrarySavesCount() : Flow<Int>

    @Query("SELECT COUNT(*) FROM library_save WHERE save_type = :type")
    fun getLibrarySavesCountByType(type: Int): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addLibrarySave(librarySave: LibrarySave)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(librarySave: List<LibrarySave>)

    @Query("DELETE FROM library_save")
    suspend fun deleteAll()
}