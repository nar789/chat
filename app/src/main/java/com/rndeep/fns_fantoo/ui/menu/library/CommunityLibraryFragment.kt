package com.rndeep.fns_fantoo.ui.menu.library

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.FragmentLibraryCommunityBinding
import com.rndeep.fns_fantoo.utils.addLibraryItemDecorationDivider
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

/**
 *  Community Library
 */
@AndroidEntryPoint
class CommunityLibraryFragment : Fragment() {

    private lateinit var binding: FragmentLibraryCommunityBinding
    private lateinit var libraryCommunityPostsAdapter: LibraryCommunityPostsAdapter
    private lateinit var libraryCommunityCommentAdapter: LibraryCommunityCommentAdapter
    private lateinit var librarySaveAdapter: LibraryCommunityPostsAdapter
    private val viewModel: CommunityLibraryViewModel by viewModels()

    private var postCount = 0
    private var commentCount = 0
    private var saveCount = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLibraryCommunityBinding.inflate(inflater, container, false).apply {
            chipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
                Timber.d("chekedIds : $checkedIds")
                if (checkedIds.size > 0) {
                    setState(checkedIds[0])
                }
            }

            libraryCommunityPostsAdapter = LibraryCommunityPostsAdapter { item ->
                Timber.d("clicked item: $item")
                val direction =
                    LibraryFragmentDirections.actionLibraryFragmentToCommunitypost(
                        DETAIL_COMMUNITY_POST_TYPE,
                        item.code,
                        item.postId.toInt()
                    )
                findNavController().navigate(direction)
            }

            libraryCommunityCommentAdapter = LibraryCommunityCommentAdapter { item ->
                Timber.d("clicked item: $item")
                val replyId = if (item.parentReplyId != 0) {
                    item.parentReplyId
                } else {
                    item.replyId
                }
                val direction =
                    LibraryFragmentDirections.actionLibraryFragmentToComment(
                        item.comPostId,
                        replyId,
                    )
                findNavController().navigate(direction)
            }

            librarySaveAdapter = LibraryCommunityPostsAdapter { item ->
                Timber.d("clicked item: $item")
                val direction =
                    LibraryFragmentDirections.actionLibraryFragmentToCommunitypost(
                        DETAIL_COMMUNITY_POST_TYPE,
                        item.code,
                        item.postId.toInt()
                    )
                findNavController().navigate(direction)
            }

            libraryList.run {
                adapter = libraryCommunityPostsAdapter
                addLibraryItemDecorationDivider()
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.communityMyPosts.observe(viewLifecycleOwner) { list ->
            Timber.d("communityMyPost list: $list , postCount: ${list.size}")
            libraryCommunityPostsAdapter.submitList(list)
            postCount = list.size
        }

        viewModel.communityMyComments.observe(viewLifecycleOwner) { list ->
            Timber.d("libraryComments list : $list")
            libraryCommunityCommentAdapter.submitList(list)
            commentCount = list.size
        }

        viewModel.communityMyBookmarks.observe(viewLifecycleOwner) { list ->
            Timber.d("librarySave list : $list")
            librarySaveAdapter.submitList(list)
            saveCount = list.size
        }
    }

    private fun setState(chipId: Int) {
        var count = 0
        val message = when (chipId) {
            binding.chipWrite.id -> {
                binding.libraryList.adapter = libraryCommunityPostsAdapter
                count = postCount
                getString(R.string.se_j_no_write_post)
            }
            binding.chipComment.id -> {
                binding.libraryList.adapter = libraryCommunityCommentAdapter
                count = commentCount
                getString(R.string.se_j_no_write_comment)
            }
            binding.chipSave.id -> {
                binding.libraryList.adapter = librarySaveAdapter
                count = saveCount
                getString(R.string.se_j_no_save_post)
            }
            else -> "no message"
        }
        val visibility = if (count == 0) View.VISIBLE else View.GONE
        binding.emptyMsg.text = message
        binding.emptyImg.visibility = visibility
        binding.emptyMsg.visibility = visibility
    }

    companion object {
        const val DETAIL_COMMUNITY_POST_TYPE = "community"
    }
}