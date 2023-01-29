package com.rndeep.fns_fantoo.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rndeep.fns_fantoo.data.local.model.LibraryPosts
import kotlinx.coroutines.flow.Flow

@Dao
interface LibraryPostsDao {

    @Query("SELECT * FROM library_posts ORDER BY id ASC")
    fun getLibraryPosts(): Flow<List<LibraryPosts>>

    @Query("SELECT * FROM library_posts WHERE post_type = :type ORDER BY id ASC")
    fun getLibraryPostsByType(type: Int): Flow<List<LibraryPosts>>

    @Query("SELECT COUNT(*) FROM library_posts ")
    fun getLibraryPostsCount() : Flow<Int>

    @Query("SELECT COUNT(*) FROM library_posts WHERE post_type = :type")
    fun getLibraryPostsCountByType(type: Int): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addLibraryPosts(libraryPosts: LibraryPosts)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(libraryPosts: List<LibraryPosts>)

    @Query("DELETE FROM library_posts")
    suspend fun deleteAll()
}