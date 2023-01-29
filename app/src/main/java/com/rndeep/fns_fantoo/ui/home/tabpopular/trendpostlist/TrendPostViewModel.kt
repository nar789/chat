package com.rndeep.fns_fantoo.ui.home.tabpopular.trendpostlist

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.rndeep.fns_fantoo.data.local.model.BoardPagePosts
import com.rndeep.fns_fantoo.ui.common.post.PostRepository
import com.rndeep.fns_fantoo.ui.common.post.CommonPostViewModel
import com.rndeep.fns_fantoo.repositories.DataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrendPostViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val popualrTrendRepository: PopularTrendRepository,
    private val dataStoreRepository: DataStoreRepository
) : CommonPostViewModel(postRepository,dataStoreRepository) {


    private val _trendPostLiveData = MutableLiveData<List<BoardPagePosts>>()
    val trendPostLiveData: LiveData<List<BoardPagePosts>> get() = _trendPostLiveData
    fun getTrendPostItem(context: Context) {
        viewModelScope.launch {
            _trendPostLiveData.value = popualrTrendRepository.callTrendPostData(0,context)
        }
    }

}