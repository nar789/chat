package com.rndeep.fns_fantoo.data.local.dao

import androidx.room.*
import com.rndeep.fns_fantoo.data.local.model.GenderType
import com.rndeep.fns_fantoo.data.local.model.MyProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface MyProfileDao {

    @Query("SELECT * FROM my_profile")
    fun getMyProfile(): Flow<MyProfile>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(profile: MyProfile)

    @Update
    suspend fun updateProfile(profile: MyProfile)

    @Query("UPDATE my_profile SET image_url = :imageId")
    suspend fun updateImage(imageId: String)

    @Query("UPDATE my_profile SET nickname = :nickname")
    suspend fun updateNickname(nickname: String)

    @Query("UPDATE my_profile SET concern = :interest")
    suspend fun updateInterest(interest: String)

    @Query("UPDATE my_profile SET country = :country")
    suspend fun updateCountry(country: String)

    @Query("UPDATE my_profile SET gender = :genderType")
    suspend fun updateGender(genderType: GenderType)

    @Query("UPDATE my_profile SET birthday = :birthday")
    suspend fun updateBirthday(birthday: String)

    @Query("DELETE FROM my_profile")
    suspend fun deleteProfile()
}