package com.rndeep.fns_fantoo.ui.common.post

import android.app.Dialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.upstream.*
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.PostDetailImageLayoutBinding
import com.rndeep.fns_fantoo.data.local.model.PostThumbnail
import com.rndeep.fns_fantoo.ui.club.post.ClubPostDetailVH
import com.rndeep.fns_fantoo.ui.community.postdetail.viewholder.CommunityPostDetailVH
import com.rndeep.fns_fantoo.ui.community.postdetail.viewholder.NoticePostVH
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class PostDetailImageAdapter :ListAdapter<PostThumbnail, PostDetailImageAdapter.PostDetailImageVH>(
    diff
) {

    interface OnFullScreenClickListener{
        fun onFullScreen(isFull : Boolean,isPort:Boolean)
    }

    private var fullScreenClickListener : OnFullScreenClickListener? =null

    fun setOnFullScreenClickListener(listener: OnFullScreenClickListener){
        this.fullScreenClickListener=listener
    }

    inner class PostDetailImageVH(private val binding : PostDetailImageLayoutBinding,private val fullScreenClickListener: OnFullScreenClickListener?) : RecyclerView.ViewHolder(binding.root){
        private var exoPlayer : ExoPlayer? =null
        private var mediaItem : MediaItem? =null
        private var mFullScreenDialog :Dialog? =null
        private var mExoPlayerFullscreen =false

        private var isPortVideo = true

        fun bind(thumbnail : PostThumbnail){
            when(thumbnail.type){
                "image"->{
                    binding.ivPostImage.visibility=View.VISIBLE
                    binding.playerGroup.visibility=View.GONE
                    binding.clLinkContainer.visibility=View.GONE
                    Glide.with(itemView)
                        .load(thumbnail.url)
                        .into(binding.ivPostImage)
                }
                "video"->{
                    binding.ivPostImage.visibility=View.GONE
                    binding.playerGroup.visibility=View.VISIBLE
                    binding.clLinkContainer.visibility=View.GONE
                    mediaItem =MediaItem.fromUri(thumbnail.url)
//                    mediaItem =MediaItem.fromUri(Uri.parse("https://www.learningcontainer.com/wp-content/uploads/2020/05/sample-mp4-file.mp4"))
                    initFullScreen(fullScreenClickListener)
                    CoroutineScope(Dispatchers.Main).launch {
                        settingVideo(fullScreenClickListener)
                    }
                }
                "link"->{
                    binding.ivPostImage.visibility=View.GONE
                    binding.playerGroup.visibility=View.GONE
                    binding.clLinkContainer.visibility=View.VISIBLE

                }
                else ->{
                    binding.ivPostImage.visibility=View.VISIBLE
                    binding.playerGroup.visibility=View.GONE
                    binding.clLinkContainer.visibility=View.GONE
                    Glide.with(itemView)
                        .load(thumbnail.url)
                        .into(binding.ivPostImage)
                }
            }
        }

        fun getIsVideoNull()=exoPlayer
        fun getIsVideoViewNull()=binding.exoPlayerView.player

        fun settingVideo(listener: OnFullScreenClickListener?){
            mediaItem?: return
            checkRemainExoplayer()

            exoPlayer=ExoPlayer.Builder(itemView.context).build()
            binding.exoPlayerView.player=exoPlayer

            val factory =DefaultDataSource.Factory(itemView.context)
            val mediaSource = HlsMediaSource.Factory(factory).createMediaSource(mediaItem!!)
            exoPlayer?.setMediaSource(mediaSource)
            exoPlayer?.prepare()
            exoPlayer?.videoSize
            val btnFullScreen =binding.exoPlayerView.findViewById<ImageView>(R.id.ivFullBtn)
            btnFullScreen.setOnClickListener {
                listener?.onFullScreen(mExoPlayerFullscreen,isPortVideo)
                if(!mExoPlayerFullscreen){
                    openFullScreenDialog()
                }else{
                    closeFullscreenDialog()
                }
            }
            exoPlayer?.addListener( object :Player.Listener{
                override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                    when(playbackState){
                        Player.STATE_IDLE->{
                            //?????? ??????
                        }
                        Player.STATE_BUFFERING->{
                            //?????? ??????
                            binding.proLoading.visibility=View.GONE
                        }
                        Player.STATE_READY->{
                            //?????? ?????? ??????
                            binding.proLoading.visibility=View.GONE
                            binding.exoPlayerView.visibility=View.VISIBLE
                            isPortVideo=(((exoPlayer?.videoSize?.width?:1) / (exoPlayer?.videoSize?.height?:1).toFloat())<=1)
                        }
                        Player.STATE_ENDED->{
                            //?????? ??????
                        }
                        else ->{

                        }
                    }
                }

                override fun onEvents(player: Player, events: Player.Events) {
                    super.onEvents(player, events)
                    (binding.exoPlayerView.layoutParams as FrameLayout.LayoutParams).height=FrameLayout.LayoutParams.WRAP_CONTENT
                }

                override fun onPlayerError(error: PlaybackException) {
                    super.onPlayerError(error)
                    Timber.d("palyer loading error Envent ")
                }
            })

        }

        fun checkRemainExoplayer(){
            if(exoPlayer!=null){
                binding.proLoading.visibility=View.GONE
                return
            }else{
                binding.proLoading.visibility=View.VISIBLE
            }
        }

        fun releaseExoPlayer(){
            exoPlayer?.stop()
            exoPlayer?.release()
            binding.exoPlayerView.player=null
            exoPlayer=null
        }

        fun pauseExoPlayer(){
            exoPlayer?:return
            if(exoPlayer!!.isPlaying){
                exoPlayer?.pause()
            }
        }

        fun initFullScreen(listener: OnFullScreenClickListener?){
            mFullScreenDialog= object : Dialog(itemView.context, android.R.style.Theme_Black_NoTitleBar_Fullscreen) {
                override fun onBackPressed() {
                    if (mExoPlayerFullscreen) {
                        listener?.onFullScreen(mExoPlayerFullscreen,isPortVideo)
                        closeFullscreenDialog()
                    }
                    super.onBackPressed()
                }
            }
        }

        fun openFullScreenDialog(){
            (binding.exoPlayerView.parent as ViewGroup).removeView(binding.exoPlayerView)
            mFullScreenDialog?.let {
                binding.exoPlayerView.resizeMode= AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH
                it.addContentView(binding.exoPlayerView,ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT))
                mExoPlayerFullscreen=true
                it.show()
            }
        }

        fun closeFullscreenDialog(){
            (binding.exoPlayerView.parent as ViewGroup).removeView(binding.exoPlayerView)
            (binding.videoFrame).addView(binding.exoPlayerView)
            mExoPlayerFullscreen=false
            binding.exoPlayerView.resizeMode= AspectRatioFrameLayout.RESIZE_MODE_FIT
            mFullScreenDialog?.dismiss()
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostDetailImageVH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.post_detail_image_layout,parent,false)
        return PostDetailImageVH(PostDetailImageLayoutBinding.bind(view),fullScreenClickListener)
    }

    override fun onBindViewHolder(holder: PostDetailImageVH, position: Int) {
        holder.bind(currentList[holder.bindingAdapterPosition])
    }

    fun playerStateChange(vh :RecyclerView.ViewHolder, type:String, fullscreenListener : OnFullScreenClickListener?){
        currentList.forEachIndexed { index, postThumbnail ->
            if(postThumbnail.type=="video"){
                when(vh){
                    is NoticePostVH -> {
                        val playerVH =vh.getBindingImageRc().findViewHolderForAdapterPosition(index)
                        if(playerVH is PostDetailImageVH){
                            when(type){
                                "prepare"->{
                                    playerVH.settingVideo(fullscreenListener)
                                }
                                "pause"->{
                                    playerVH.pauseExoPlayer()
                                }
                                "stop"->{
                                    playerVH.releaseExoPlayer()
                                }
                            }
                        }
                    }
                    is CommunityPostDetailVH -> {
                        val playerVH =vh.getBindingImageRc().findViewHolderForAdapterPosition(index)
                        if(playerVH is PostDetailImageVH){
                            playerVH.pauseExoPlayer()
                            when(type){
                                "prepare"->{
                                    playerVH.settingVideo(fullscreenListener)
                                }
                                "pause"->{
                                    playerVH.pauseExoPlayer()
                                }
                                "stop"->{
                                    playerVH.releaseExoPlayer()
                                }
                            }
                        }
                    }
                    is ClubPostDetailVH -> {
                        val playerVH =vh.getBindingImageRc().findViewHolderForAdapterPosition(index)
                        if(playerVH is PostDetailImageVH){
                            playerVH.pauseExoPlayer()
                            when(type){
                                "prepare"->{
                                    playerVH.settingVideo(fullscreenListener)
                                }
                                "pause"->{
                                    playerVH.pauseExoPlayer()
                                }
                                "stop"->{
                                    playerVH.releaseExoPlayer()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    companion object{
        val diff =object : DiffUtil.ItemCallback<PostThumbnail>(){
            override fun areItemsTheSame(oldItem: PostThumbnail, newItem: PostThumbnail): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: PostThumbnail,
                newItem: PostThumbnail
            ): Boolean {
                return (oldItem.url==newItem.url) || (oldItem.type == newItem.type)
            }

        }
    }
}