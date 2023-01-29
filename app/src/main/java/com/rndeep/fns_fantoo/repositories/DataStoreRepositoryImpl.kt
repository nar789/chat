package com.rndeep.fns_fantoo.repositories

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import org.json.JSONArray
import javax.inject.Inject

private const val PREFERENCES_NAME = "fns_preferences"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PREFERENCES_NAME)

class DataStoreKey{
    companion object {
        const val PREF_KEY_LANGUAGE_CODE = "language_code"
        const val PREF_KEY_SELECT_LANGUAGE_CODE = "select_language_code"
        const val PREF_KEY_ACCESS_TOKEN = "access_token"
        const val PREF_KEY_REFRESH_ACCESS_TOKEN = "refresh_access_token"
        const val PREF_KEY_REFRESH_TOKEN_PUBLISH_TIME = "refresh_token_publish_date"
        const val PREF_KEY_UID = "uid"
        const val PREF_KEY_IS_LOGINED = "logined"
        const val PREF_KEY_LAST_LOGIN_TYPE = "last_login_type"
        const val PREF_KEY_IS_PERMISSION_CHECKED = "permission_checked"
        const val PREF_KEY_CLUBSEARCHWORDLIST = "club_search_word_list_key"
        const val PREF_KEY_CLUBDETAILSEARCHWORDLIST = "club_detail_search_word_list_key"
        const val PREF_KEY_COMMUNITYSEARCHWORDLIST = "community_search_word_list_key"
        const val PREF_KEY_COMMUNITY_FAVORITE_SORT_TYPE = "community_favorite_sort_type_key"
        const val PREF_KEY_RECENT_NEWS_SEARCH = "recent_news_search"
        const val PREF_KEY_FCM_TOKEN = "fcm_token"
        const val PREF_KEY_IS_FIRST_PROFILE_COMPLETE = "first_profile_complete"
        const val PREF_KEY_BOARD_LIST_FAVORITE_STATE = "board_list_favorite_state"
        const val PREF_KEY_MY_CLUBS_IS_FAVORITE = "my_clubs_favorite"
        const val PREF_KEY_API_URL = "api_url"
        const val PREF_KEY_CLOUDFLARE_URL = "cloud_flare_url"
        const val PREF_KEY_SYSTEM_COUNTRY = "system_country"
    }
}

class DataStoreRepositoryImpl @Inject constructor(private val context: Context)
    : DataStoreRepository {

    override suspend fun putString(key: String, value: String) {
        val preferencesKey = stringPreferencesKey(key)
        context.dataStore.edit { preferences ->
            preferences[preferencesKey] = value
        }
    }

    override suspend fun clearString(key:String){
        val preferencesKey = stringPreferencesKey(key)
        context.dataStore.edit { preferences ->
            preferences.remove(preferencesKey)
        }
    }

    override suspend fun putInt(key: String, value: Int) {
        val preferencesKey = intPreferencesKey(key)
        context.dataStore.edit { preferences ->
            preferences[preferencesKey] = value
        }
    }

    override suspend fun putLong(key: String, value:Long){
        val preferencesKey = longPreferencesKey(key)
        context.dataStore.edit { preferences ->
            preferences[preferencesKey] = value
        }
    }

    override suspend fun putBoolean(key: String, value: Boolean) {
        val preferencesKey = booleanPreferencesKey(key)
        context.dataStore.edit { preferences ->
            preferences[preferencesKey] = value
        }
    }

    override suspend fun putStringArray(key: String, value: ArrayList<String>) {
        val jsonArr =JSONArray()
        for( a in value){
            jsonArr.put(a)
        }
        val setItem = jsonArr.toString()
        val preferencesKey = stringPreferencesKey(key)
        context.dataStore.edit { preferences ->
            preferences[preferencesKey] =setItem
        }
    }

    override suspend fun getString(key: String): String? {
        val preferencesKey = stringPreferencesKey(key)
        val preferences = context.dataStore.data.first()
        return preferences[preferencesKey]
    }

    override suspend fun getInt(key: String): Int? {
        val preferencesKey = intPreferencesKey(key)
        val preferences = context.dataStore.data.first()
        return preferences[preferencesKey]
    }

    override suspend fun getLong(key: String): Long? {
        val preferencesKey = longPreferencesKey(key)
        val preferences = context.dataStore.data.first()
        return preferences[preferencesKey]
    }

    override suspend fun getBoolean(key: String): Boolean? {
        val preferencesKey = booleanPreferencesKey(key)
        val preferences = context.dataStore.data.first()
        return preferences[preferencesKey]
    }

    override suspend fun getStringArray(key: String): ArrayList<String>? {
        val preferencesKey = stringPreferencesKey(key)
        val preferences = context.dataStore.data.first()
        val item = preferences[preferencesKey] ?: return null
        val resultArray = ArrayList<String>()
        val jsonItem =JSONArray(item)
        for(a in 0 until jsonItem.length()){
            if(jsonItem.optString(a)=="") continue
            resultArray.add(jsonItem.optString(a))
        }
        return resultArray
    }
}