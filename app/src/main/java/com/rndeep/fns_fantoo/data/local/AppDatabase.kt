package com.rndeep.fns_fantoo.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.rndeep.fns_fantoo.data.local.dao.*
import com.rndeep.fns_fantoo.data.local.model.*
import com.rndeep.fns_fantoo.data.remote.model.post.BoardPostData
import com.rndeep.fns_fantoo.utils.ConstVariable.DATABASE_NAME

@Database(
    entities = [BannerItem::class, TrendTagItem::class, CommonRecommendClub::class,
        CurationDataItem::class, MyClubs::class, MyProfile::class, Country::class, Language::class,
        LibraryPosts::class, LibraryComment::class, LibrarySave::class,BoardPagePosts::class,
        BoardPostData::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(RecommendClubConverters::class,BoardPostConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun homeBannerDao(): HomeBannerDao
    abstract fun popularTagDao(): PopularTrendTagDao
    abstract fun recommendClubDao(): RecommendClubDao
    abstract fun curationDao(): CurationDao
    abstract fun myClubsDao(): MyClubsDao
    abstract fun myProfileDao(): MyProfileDao
    abstract fun countryDao(): CountryDao
    abstract fun languageDao(): LanguageDao
    abstract fun libraryPostsDao(): LibraryPostsDao
    abstract fun libraryCommentDao(): LibraryCommentDao
    abstract fun librarySaveDao(): LibrarySaveDao
    abstract fun boardPagePostDao(): BoardPagePostDao

    companion object {

        fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
                // pre-populate the database
                .addCallback(
                    object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // from asset0
                        }
                    }
                )
                .build()
        }

    }

}
