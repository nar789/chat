package com.rndeep.fns_fantoo.ui.community.board

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.data.remote.model.post.BoardPostData
import com.rndeep.fns_fantoo.data.remote.model.post.PostListData

object BoardListeners{
    //공지 클릭 리스너
    interface OnBoardNoticeClickListener {
        fun onNoticeClick(v: View, postId: Int,clubId : String?)
        fun onNoticeMore()
    }
    //카테고리 클릭 리스너
    interface OnBoardCategoryClickListener {
        fun onCategoryClick(v: View, id: String, position: Int)
    }
    //언어 설정 클릭 리스너
    interface OnBoardFilterClickListener {
        fun onFilterClick(v: View, id: String)
    }
    //커뮤니티 게시글 클릭 리스너
    interface OnBoardPostClickListener{
        fun onLikeClick(dbId:Int, postItem : PostListData, postType:String, changePos :Int, holder: RecyclerView.ViewHolder)
        fun onDisLikeClick(dbId:Int, postItem : PostListData, postType:String, changePos :Int, holder: RecyclerView.ViewHolder)
        fun onHonorClick(dbId:Int, postItem : PostListData, postType:String, changePos :Int, holder: RecyclerView.ViewHolder)
        fun onOptionClick(dbId:Int, postAuthId :String, postType:String, changePos :Int, postId :Int, isPieceBlockYn : Boolean?,isUserBlockYN:Boolean?, code:String)
        fun onPostClick(categoryId :String,postId :Int, postType:String,clubId :String?=null)
        fun onProfileClick(postItem :PostListData)
    }
}
