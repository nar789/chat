package com.rndeep.fns_fantoo.ui.menu

import com.rndeep.fns_fantoo.data.local.dao.*
import com.rndeep.fns_fantoo.data.local.model.MyClubs
import com.rndeep.fns_fantoo.data.local.model.MyProfile
import com.rndeep.fns_fantoo.data.remote.BaseNetRepo
import timber.log.Timber
import javax.inject.Inject

class MenuRepository @Inject constructor(
    private val myClubsDao: MyClubsDao,
    private val myProfileDao: MyProfileDao,
    private val libraryPostsDao: LibraryPostsDao,
    private val libraryCommentDao: LibraryCommentDao,
    private val librarySaveDao: LibrarySaveDao,
) : BaseNetRepo() {

    fun getClubs() = myClubsDao.getClubs()

    fun getFavoritedClubs() = myClubsDao.getClubByFavorited()

    fun getMyClubsCount() = myClubsDao.getClubCount()

    fun getMyFavoritedClubsCount() = myClubsDao.getFavoritedClubCount()

    suspend fun addMyClub(myClub: MyClubs) {
        myClubsDao.addClub(myClub)
    }

//    suspend fun addDummyLibraryPosts(context: Context) {
//        try {
//            val filename = "libraryPosts.json"
//            context.assets.open(filename).use { inputStream ->
//                JsonReader(inputStream.reader()).use { jsonReader ->
//                    val libraryPosts = object : TypeToken<List<LibraryPosts>>() {}.type
//                    val posts: List<LibraryPosts> = Gson().fromJson(jsonReader, libraryPosts)
//                    Timber.d("dummy posts : $posts")
//                    libraryPostsDao.insertAll(posts)
//                }
//            }
//        } catch (ex: Exception) {
//            Timber.e("Error : $ex")
//        }
//    }

    suspend fun deleteAll() {
        myClubsDao.deleteAll()
    }

    fun getMyProfile() = myProfileDao.getMyProfile()

    suspend fun updateProfile(myProfile: MyProfile) = myProfileDao.updateProfile(myProfile)

    fun getLibraryPosts() = libraryPostsDao.getLibraryPosts()
    fun getLibraryPostsByType(type: Int) = libraryPostsDao.getLibraryPostsByType(type)
    fun getLibraryPostsCount() = libraryPostsDao.getLibraryPostsCount()
    fun getLibraryPostsCountByType(type: Int) = libraryPostsDao.getLibraryPostsCountByType(type)

    fun getLibraryComment() = libraryCommentDao.getLibraryComment()
    fun getLibraryCommentByType(type: Int) = libraryCommentDao.getLibraryCommentByType(type)
    fun getLibraryCommentCount() = libraryCommentDao.getLibraryCommentCount()
    fun getLibraryCommentCountByType(type: Int) = libraryCommentDao.getLibraryCommentCountByType(type)

    fun getLibrarySaves() = librarySaveDao.getLibrarySaves()
    fun getLibrarySavesByType(type: Int) = librarySaveDao.getLibrarySavesByType(type)
    fun getLibrarySavesCount() = librarySaveDao.getLibrarySavesCount()
    fun getLibrarySavesCountByType(type: Int) = librarySaveDao.getLibrarySavesCountByType(type)
}