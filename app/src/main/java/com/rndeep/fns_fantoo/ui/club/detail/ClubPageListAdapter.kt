package com.rndeep.fns_fantoo.ui.club.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.local.model.BoardPagePosts
import com.rndeep.fns_fantoo.data.remote.model.club.ClubNoticeItem
import com.rndeep.fns_fantoo.databinding.CategoryHomePostBinding
import com.rndeep.fns_fantoo.databinding.TabClubHomeNoticeBinding
import com.rndeep.fns_fantoo.databinding.TabClubPageArchiveTopNameLayoutBinding
import com.rndeep.fns_fantoo.ui.club.detail.archive.ArchiveImageAdapter
import com.rndeep.fns_fantoo.ui.club.detail.archive.ArchiveNameVH
import com.rndeep.fns_fantoo.ui.club.detail.home.ClubHomeNoticeVH
import com.rndeep.fns_fantoo.ui.common.adapter.PostDiffUtil
import com.rndeep.fns_fantoo.ui.common.post.PostClubVH
import com.rndeep.fns_fantoo.ui.community.board.BoardListeners
import com.rndeep.fns_fantoo.utils.ConstVariable

class ClubPageListAdapter : ListAdapter<BoardPagePosts,RecyclerView.ViewHolder>(diff) {
    private val POSTCLUBTYPE = 2
    private val POSTADTYPE = 4
    private val POSTNOTICE = 5
    private val POSTTITLE = 6

    private var listOrFeed = ConstVariable.POST_TYPE_FEED
    private var typeOfView = ConstVariable.VIEW_CLUB_PAGE_HOME

    private var selectArchiveName = ""

    inner class CategoryErrorVH(view: View) : RecyclerView.ViewHolder(view)

    private var noticeItems = listOf<ClubNoticeItem>()

    private var clickListener: BoardListeners.OnBoardPostClickListener? = null

    fun setOnOptionsClickListener(listener: BoardListeners.OnBoardPostClickListener) {
        this.clickListener = listener
    }

    private var noticeItemClickListener : BoardListeners.OnBoardNoticeClickListener? =null

    fun setOnNoticeItemClickListener(listener: BoardListeners.OnBoardNoticeClickListener){
        this.noticeItemClickListener=listener
    }

    private var archiveNameClickListener : ArchiveImageAdapter.ArchiveTopTypeClickListener? = null

    fun setOnArchiveNameClickListener(listener: ArchiveImageAdapter.ArchiveTopTypeClickListener){
        this.archiveNameClickListener=listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            POSTCLUBTYPE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.category_home_post, parent, false)
                PostClubVH(CategoryHomePostBinding.bind(view), clickListener,listOrFeed,typeOfView)
            }
            POSTNOTICE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.tab_club_home_notice, parent, false)
                ClubHomeNoticeVH(TabClubHomeNoticeBinding.bind(view),noticeItemClickListener)
            }
            POSTTITLE -> {
                val view =LayoutInflater.from(parent.context)
                    .inflate(R.layout.tab_club_page_archive_top_name_layout,parent,false)
                ArchiveNameVH(TabClubPageArchiveTopNameLayoutBinding.bind(view),ConstVariable.ARCHIVEPOSTTYPE,archiveNameClickListener)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.blank_layout_for_error, parent, false)
                CategoryErrorVH(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is PostClubVH -> {
                holder.clubViewBind(
                    currentList[holder.bindingAdapterPosition].clubPostData,
                    currentList[holder.bindingAdapterPosition].boardPkId
                )
            }
            is ClubHomeNoticeVH -> {
                holder.bind(noticeItems)
            }
            is ArchiveNameVH -> {
                holder.bind(selectArchiveName)
            }
        }
    }

    override fun getItemViewType(position: Int) = when (currentList[position].type) {
        ConstVariable.TYPE_CLUB -> {
            POSTCLUBTYPE
        }
        ConstVariable.TYPE_AD -> {
            POSTADTYPE
        }
        ConstVariable.TYPE_COMMUNITY_NOTICE -> {
            POSTNOTICE
        }
        "title" -> {
            POSTTITLE
        }
        else -> {
            -1
        }
    }

    fun setListOrFeed(type: String) {
        listOrFeed = type
    }

    fun setViewType(type : String){
        typeOfView=type
    }

    fun setSelectArchiveName(name :String){
        this.selectArchiveName=name
    }

    fun setNoticeListItem(items: List<ClubNoticeItem>) {
        this.noticeItems = items
    }

    companion object{
        val diff =object : DiffUtil.ItemCallback<BoardPagePosts>() {
            override fun areItemsTheSame(
                oldItem: BoardPagePosts,
                newItem: BoardPagePosts
            ): Boolean {
                return (oldItem.clubPostData?.postId==newItem.clubPostData?.postId)
                        &&oldItem.clubPostData?.content == newItem.clubPostData?.content
                        &&oldItem.clubPostData?.subject == newItem.clubPostData?.subject
                        &&oldItem.clubPostData?.status==newItem.clubPostData?.status
                        &&oldItem.clubPostData?.blockType==newItem.clubPostData?.blockType
                        &&oldItem.clubPostData?.deleteType==newItem.clubPostData?.deleteType
                        &&oldItem.clubPostData?.replyCount==newItem.clubPostData?.replyCount

            }

            override fun areContentsTheSame(
                oldItem: BoardPagePosts,
                newItem: BoardPagePosts
            ): Boolean {

                return oldItem==newItem
            }

        }
    }

}