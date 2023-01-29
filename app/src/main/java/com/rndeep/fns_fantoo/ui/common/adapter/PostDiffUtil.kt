package com.rndeep.fns_fantoo.ui.common.adapter

import androidx.recyclerview.widget.DiffUtil
import com.rndeep.fns_fantoo.data.local.model.BoardPagePosts

class PostDiffUtil : DiffUtil.ItemCallback<BoardPagePosts>() {
    override fun areItemsTheSame(
        oldItem: BoardPagePosts,
        newItem: BoardPagePosts
    ): Boolean {
        return oldItem==newItem

    }

    override fun areContentsTheSame(
        oldItem: BoardPagePosts,
        newItem: BoardPagePosts
    ): Boolean {

        return (oldItem.boardPostItem?.postId==newItem.boardPostItem?.postId)
                &&oldItem.boardPostItem?.likeCnt==newItem.boardPostItem?.likeCnt
                &&oldItem.boardPostItem?.likeYn==newItem.boardPostItem?.likeYn
                &&oldItem.boardPostItem?.dislikeYn==newItem.boardPostItem?.dislikeYn
                &&oldItem.boardPostItem?.dislikeCnt==newItem.boardPostItem?.dislikeCnt
                &&oldItem.boardPostItem?.honorYn==newItem.boardPostItem?.honorYn
                &&oldItem.boardPostItem?.honorCnt==newItem.boardPostItem?.honorCnt
                &&oldItem.boardPostItem?.userBlockYn==newItem.boardPostItem?.userBlockYn
                &&oldItem.boardPostItem?.pieceBlockYn==newItem.boardPostItem?.pieceBlockYn
    }

}