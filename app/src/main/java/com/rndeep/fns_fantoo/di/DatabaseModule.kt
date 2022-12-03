package com.rndeep.fns_fantoo.di

import android.content.Context
import com.rndeep.fns_fantoo.data.local.AppDatabase
import com.rndeep.fns_fantoo.data.local.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.buildDatabase(context)
    }

    @Provides
    fun provideMyClubsDao(appDatabase: AppDatabase): MyClubsDao {
        return appDatabase.myClubsDao()
    }

    @Provides
    fun provideHomeBannerDao(appDatabase: AppDatabase) : HomeBannerDao{
        return appDatabase.homeBannerDao()
    }

    @Provides
    fun provideRecommendClubDao(appDatabase: AppDatabase) : RecommendClubDao{
        return appDatabase.recommendClubDao()
    }

    @Provides
    fun provideCurationDao(appDatabase: AppDatabase) :CurationDao{
        return appDatabase.curationDao()
    }

    @Provides
    fun providePopularTagDao(appDatabase: AppDatabase) : PopularTrendTagDao{
        return appDatabase.popularTagDao()
    }

    @Provides
    fun provideMyProfileDao(appDatabase: AppDatabase): MyProfileDao {
        return appDatabase.myProfileDao()
    }

    @Provides
    fun provideCountryDao(appDatabase: AppDatabase): CountryDao {
        return appDatabase.countryDao()
    }

    @Provides
    fun provideLanguageDao(appDatabase: AppDatabase): LanguageDao {
        return appDatabase.languageDao()
    }

    @Provides
    fun provideLibraryPostsDao(appDatabase: AppDatabase): LibraryPostsDao {
        return appDatabase.libraryPostsDao()
    }

    @Provides
    fun provideLibraryCommentDao(appDatabase: AppDatabase): LibraryCommentDao {
        return appDatabase.libraryCommentDao()
    }

    @Provides
    fun provideLibrarySaveDao(appDatabase: AppDatabase): LibrarySaveDao {
        return appDatabase.librarySaveDao()
    }

    @Provides
    fun provideBoardPagePostDao(appDatabase: AppDatabase) : BoardPagePostDao{
        return appDatabase.boardPagePostDao()
    }

}