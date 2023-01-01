package com.rndeep.fns_fantoo.ui.chatting.imagepicker

import android.content.ContentResolver
import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.paging.*
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import javax.inject.Inject

class ImagePickerRepository @Inject constructor(private val contentResolver: ContentResolver) {
    fun loadImages(): Flow<PagingData<Uri>> {
        return Pager(config = PagingConfig(20, 20, true, 20), pagingSourceFactory = {
            ImageDataSource(contentResolver)
        }).flow
    }
}

class ImageDataSource(private val contentResolver: ContentResolver) : PagingSource<Int, Uri>() {

    override fun getRefreshKey(state: PagingState<Int, Uri>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Uri> {
        val currentKey = params.key ?: 0
        val loadSize = params.loadSize
        val offset = currentKey * loadSize

        return try {
            val images = getImages(loadSize, offset)
            LoadResult.Page(
                data = images,
                prevKey = null,
                nextKey = if (images.isEmpty()) null else currentKey.plus(1)
            )
        } catch (e: Exception) {
            Timber.e("imagePicker load exception: ${e.message}", e)
            LoadResult.Error(e)
        }
    }


    private fun getImages(limit: Int, offset: Int): List<Uri> {
        val galleryImageUrls = mutableListOf<Uri>()
        val collection = MediaStore.Files.getContentUri("external")
        val projection = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.DATE_ADDED,
            MediaStore.Files.FileColumns.MEDIA_TYPE,
        )
        createCursor(
            contentResolver = contentResolver,
            collection = collection,
            projection = projection,
            limit = limit,
            offset = offset
        )?.use { cursor ->
            while (cursor.moveToNext()) {
                val idIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                if (idIndex < 0) continue
                val id = cursor.getLong(idIndex)
                galleryImageUrls.add(
                    ContentUris.withAppendedId(collection, id)
                )
            }
        }
        return galleryImageUrls
    }

    private fun createCursor(
        contentResolver: ContentResolver,
        collection: Uri,
        projection: Array<String>,
        limit: Int,
        offset: Int
    ): Cursor? = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
            val selection = createSelectionBundle(limit, offset)
            contentResolver.query(collection, projection, selection, null)
        }
        else -> {
            val orderDirection = "DESC"
            val selectionArgs =
                "(${MediaStore.Files.FileColumns.MEDIA_TYPE} IN ${MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE})"
            val order =
                "${MediaStore.Images.Media.DATE_TAKEN} $orderDirection limit $limit offset $offset"
            contentResolver.query(collection, projection, selectionArgs, null, order)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createSelectionBundle(
        limit: Int, offset: Int
    ) = Bundle().apply {
        putInt(ContentResolver.QUERY_ARG_LIMIT, limit)
        putInt(ContentResolver.QUERY_ARG_OFFSET, offset)
        putStringArray(
            ContentResolver.QUERY_ARG_SORT_COLUMNS,
            arrayOf(MediaStore.MediaColumns.DATE_ADDED)
        )
        putInt(
            ContentResolver.QUERY_ARG_SORT_DIRECTION,
            ContentResolver.QUERY_SORT_DIRECTION_DESCENDING
        )
        val whereCondition =
            "${MediaStore.Files.FileColumns.MEDIA_TYPE} = ? OR ${MediaStore.Files.FileColumns.MEDIA_TYPE} = ?"
        putString(ContentResolver.QUERY_ARG_SQL_SELECTION, whereCondition)
        putStringArray(
            ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS,
            arrayOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString())
        )
    }
}
