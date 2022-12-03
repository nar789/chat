package com.rndeep.fns_fantoo.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rndeep.fns_fantoo.data.local.model.LibraryComment
import kotlinx.coroutines.flow.Flow

@Dao
interface LibraryCommentDao {

    @Query("SELECT * FROM library_comment ORDER BY id ASC")
    fun getLibraryComment(): Flow<List<LibraryComment>>

    @Query("SELECT * FROM library_comment WHERE comment_type = :type ORDER BY id ASC")
    fun getLibraryCommentByType(type: Int): Flow<List<LibraryComment>>

    @Query("SELECT COUNT(*) FROM library_comment ")
    fun getLibraryCommentCount() : Flow<Int>

    @Query("SELECT COUNT(*) FROM library_comment WHERE comment_type = :type")
    fun getLibraryCommentCountByType(type: Int): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addLibraryComment(libraryComment: LibraryComment)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(libraryComments: List<LibraryComment>)

    @Query("DELETE FROM library_posts")
    suspend fun deleteAll()
}