package com.rndeep.fns_fantoo.di

import android.content.ContentResolver
import android.content.Context
import com.rndeep.fns_fantoo.data.local.dao.*
import com.rndeep.fns_fantoo.data.remote.api.*
import com.rndeep.fns_fantoo.repositories.*
import com.rndeep.fns_fantoo.ui.chatting.imagepicker.ImagePickerRepository
import com.rndeep.fns_fantoo.ui.club.challenge.ClubChallengeRepository
import com.rndeep.fns_fantoo.ui.club.create.ClubCreateRepository
import com.rndeep.fns_fantoo.ui.club.join.ClubJoinRepository
import com.rndeep.fns_fantoo.ui.club.detail.ClubPageRepository
import com.rndeep.fns_fantoo.ui.club.search.ClubSearchRepository
import com.rndeep.fns_fantoo.ui.club.ClubRepository
import com.rndeep.fns_fantoo.ui.club.notice.ClubNoticeRepository
import com.rndeep.fns_fantoo.ui.common.post.PostRepository
import com.rndeep.fns_fantoo.ui.club.post.DetailPostRepository
import com.rndeep.fns_fantoo.ui.community.CommunityRepository
import com.rndeep.fns_fantoo.ui.community.board.CommunityBoardRepository
import com.rndeep.fns_fantoo.ui.community.boardlist.CommunityBoardShowAllRepository
import com.rndeep.fns_fantoo.ui.community.comment.CommunityCommentRepository
import com.rndeep.fns_fantoo.ui.community.mypage.CommunityMyRepository
import com.rndeep.fns_fantoo.ui.home.alram.HomeAlarmRepository
import com.rndeep.fns_fantoo.ui.home.tabhome.HomeRepository
import com.rndeep.fns_fantoo.ui.home.tabpopular.PopularRepository
import com.rndeep.fns_fantoo.ui.home.tabpopular.trendpostlist.PopularTrendRepository
import com.rndeep.fns_fantoo.ui.login.AuthRepository
import com.rndeep.fns_fantoo.ui.menu.MenuRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class RepositoryModule {

    @Singleton
    @Provides
    fun provideAuthRepository(@NetworkModule.AuthServer authService: AuthService): AuthRepository =
        AuthRepository(authService)

    @Singleton
    @Provides
    fun provideMenuRepository(
        myClubsDao: MyClubsDao,
        myProfileDao: MyProfileDao,
        libraryPostsDao: LibraryPostsDao,
        libraryCommentDao: LibraryCommentDao,
        librarySaveDao: LibrarySaveDao
    ): MenuRepository =
        MenuRepository(
            myClubsDao,
            myProfileDao,
            libraryPostsDao,
            libraryCommentDao,
            librarySaveDao
        )

    @Singleton
    @Provides
    fun provideFantooClubRepository(
        @ApplicationContext context: Context,
        @NetworkModule.ApiServer fantooClubService: FantooClubService
    ): FantooClubRepository =
        FantooClubRepository(context, fantooClubService)

    @Singleton
    @Provides
    fun provideUserInfoRepository(
        @NetworkModule.ApiServer userInfoService: UserInfoService,
        myProfileDao: MyProfileDao
    ): UserInfoRepository =
        UserInfoRepository(userInfoService, myProfileDao)

    @Singleton
    @Provides
    fun provideHomeRepository(
        boardPagePostDao: BoardPagePostDao,
        bannerDao: HomeBannerDao,
        recommendClubDao: RecommendClubDao,
        @NetworkModule.ApiServer homeService: HomeService,
        @NetworkModule.ApiServer postService: PostService,
        @NetworkModule.ApiServer userInfoService: UserInfoService,
        myProfileDao: MyProfileDao
    ): HomeRepository =
        HomeRepository(
            boardPagePostDao,
            bannerDao,
            homeService,
            recommendClubDao,
            postService,
            userInfoService,
            myProfileDao
        )

    @Singleton
    @Provides
    fun providePopularRepository(
        boardPagePostDao: BoardPagePostDao,
        popularTrendTagDao: PopularTrendTagDao,
        curationDao: CurationDao,
        recommendClubDao: RecommendClubDao,
        @NetworkModule.ApiServer homeService: HomeService,
        @NetworkModule.ApiServer postService: PostService
    ): PopularRepository =
        PopularRepository(
            boardPagePostDao,
            popularTrendTagDao,
            curationDao,
            homeService,
            recommendClubDao,
            postService
        )

    @Singleton
    @Provides
    fun providePopularTrendRepository(
        boardPagePostDao: BoardPagePostDao,
        @NetworkModule.ApiServer homeService: HomeService,
        @NetworkModule.ApiServer postService: PostService
    ): PopularTrendRepository =
        PopularTrendRepository(homeService, boardPagePostDao, postService)

    @Singleton
    @Provides
    fun provideHomeAlarmRepository(
        @NetworkModule.ApiServer homeService: HomeService,
    ): HomeAlarmRepository =
        HomeAlarmRepository(homeService)

    @Singleton
    @Provides
    fun provideCommunityRepository(
        boardPagePostDao: BoardPagePostDao,
        @NetworkModule.ApiServer communityService: CommunityService,
        @NetworkModule.ApiServer postService: PostService
    ): CommunityRepository =
        CommunityRepository(boardPagePostDao, communityService, postService)

    @Singleton
    @Provides
    fun provideCommunityBoardShowAllRepository(
        boardPagePostDao: BoardPagePostDao,
        @NetworkModule.ApiServer communityService: CommunityService,
        @NetworkModule.ApiServer postService: PostService
    ): CommunityBoardShowAllRepository =
        CommunityBoardShowAllRepository(boardPagePostDao, communityService, postService)

    @Singleton
    @Provides
    fun provideCommunityBoardRepository(
        boardPagePostDao: BoardPagePostDao,
        @NetworkModule.ApiServer communityService: CommunityService,
        @NetworkModule.ApiServer postDetailService: PostDetailService
    ): CommunityBoardRepository =
        CommunityBoardRepository(boardPagePostDao, communityService, postDetailService)

    @Singleton
    @Provides
    fun provideCommunityMyRepository(
        boardPagePostDao: BoardPagePostDao,
        @NetworkModule.ApiServer postService: PostService
    ): CommunityMyRepository =
        CommunityMyRepository(boardPagePostDao, postService)

    @Singleton
    @Provides
    fun provideClubRepository(
        boardPagePostDao: BoardPagePostDao,
        recommendClubDao: RecommendClubDao,
        @NetworkModule.ApiServer clubService: ClubService,
        @NetworkModule.ApiServer postService: PostService
    ): ClubRepository =
        ClubRepository(boardPagePostDao, recommendClubDao, clubService, postService)

    @Singleton
    @Provides
    fun provideClubJoinRepository(@NetworkModule.ApiServer clubService: ClubService): ClubJoinRepository =
        ClubJoinRepository(clubService)

    @Singleton
    @Provides
    fun provideClubPageRepository(
        boardPagePostDao: BoardPagePostDao,
        @NetworkModule.ApiServer postService: PostService,
        @NetworkModule.ApiServer clubService: ClubService
    ): ClubPageRepository =
        ClubPageRepository(boardPagePostDao, postService,clubService)

    @Singleton
    @Provides
    fun provideClubSearchRepository(@NetworkModule.ApiServer clubService: ClubService): ClubSearchRepository =
        ClubSearchRepository(clubService)

    @Singleton
    @Provides
    fun provideClubCreateRepository(@NetworkModule.ApiServer clubService: ClubService): ClubCreateRepository =
        ClubCreateRepository(clubService)


    @Singleton
    @Provides
    fun provideDataStoreRepository(@ApplicationContext app: Context): DataStoreRepository =
        DataStoreRepositoryImpl(app)

    @Singleton
    @Provides
    fun provideClubChallengeRepository(@NetworkModule.ApiServer clubService: ClubService): ClubChallengeRepository =
        ClubChallengeRepository(clubService)

    @Singleton
    @Provides
    fun provideDetailPostRepository(@NetworkModule.ApiServer detailService: PostDetailService): DetailPostRepository =
        DetailPostRepository(detailService)

    @Singleton
    @Provides
    fun providePostRepository(
        boardPagePostDao: BoardPagePostDao,
        @NetworkModule.ApiServer postService: PostService
    ): PostRepository =
        PostRepository(boardPagePostDao, postService)

    @Singleton
    @Provides
    fun provideCommunityCommentRepository(
        @NetworkModule.ApiServer postDetailService: PostDetailService
    ): CommunityCommentRepository =
        CommunityCommentRepository(postDetailService)

    @Singleton
    @Provides
    fun provideUploadRepository(
        @NetworkModule.CloudFlareServer cloudFlareService: CloudFlareService
    ): UploadRepository = UploadRepository(cloudFlareService)

    @Singleton
    @Provides
    fun provideMyLibraryRepository(
        @NetworkModule.ApiServer communityService: CommunityService,
        @NetworkModule.ApiServer clubService: ClubService
    ): MyLibraryRepository =
        MyLibraryRepository(communityService, clubService)

    @Singleton
    @Provides
    fun provideTranslateRepository(
        @NetworkModule.TranslateServer translateService: TranslateService
    ): TranslateRepository = TranslateRepository(translateService)

    @Singleton
    @Provides
    fun provideClubNoticeRepository(
        @NetworkModule.ApiServer clubService: ClubService
    ) : ClubNoticeRepository =
        ClubNoticeRepository(clubService)

    @Singleton
    @Provides
    fun provideImagePickerRepository(contentResolver: ContentResolver) : ImagePickerRepository =
        ImagePickerRepository(contentResolver)

}