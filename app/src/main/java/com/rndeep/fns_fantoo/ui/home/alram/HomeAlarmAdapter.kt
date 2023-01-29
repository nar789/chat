package com.rndeep.fns_fantoo.ui.home.alram

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.data.remote.model.HomeAlarmData
import com.rndeep.fns_fantoo.databinding.TabHomeAlarmLayoutBinding
import com.rndeep.fns_fantoo.utils.TimeUtils
import com.rndeep.fns_fantoo.utils.setProfileAvatar

class HomeAlarmAdapter : RecyclerView.Adapter<HomeAlarmAdapter.HomeAlarmVH>() {

    inner class HomeAlarmVH(private val binding: TabHomeAlarmLayoutBinding,private val listener: AlarmClickListener?) :
        RecyclerView.ViewHolder(binding.root) {

        fun alarmBind(alarmItem: HomeAlarmData) {
            //썸네일
            setProfileAvatar(binding.ivAlarmThumbnail,itemView.context.getString(R.string.imageThumbnailUrl,alarmItem.alarmThumbnail))
            //게시물 제목
            if (alarmItem.title == null) {
                binding.tvAlarmSubTitle.visibility = View.GONE
            } else {
                binding.tvAlarmSubTitle.visibility = View.VISIBLE
                binding.tvAlarmSubTitle.text = alarmItem.title
            }
            //알림 내용
            binding.tvAlarmTitle.text = alarmItem.body

            //보내는 사람 및 시간
            binding.tvSender.text = alarmItem.alarmSender
            binding.tvSendTime.text = TimeUtils.diffTimeWithCurrentTime(alarmItem.alarmSendDate)

            //읽음 여부
            if (alarmItem.isRead==true) {
                binding.alarmContainer.background = itemView.context.getDrawable(R.color.gray_25)
            } else {
                binding.alarmContainer.background = itemView.context.getDrawable(R.color.bg_light_gray_50)
            }

            itemView.setOnClickListener {
                listener?.onAlarmClick(alarmItem.alimId, bindingAdapterPosition, alarmItem.isRead?:false)
            }
        }

    }

    interface AlarmClickListener {
        fun onAlarmClick(alarmId: Int, position: Int, isRead: Boolean)
    }

    private var alarmClickListener: AlarmClickListener? = null

    fun setOnAlarmClickListener(listener: AlarmClickListener) {
        this.alarmClickListener = listener
    }

    private var alarmItems = listOf<HomeAlarmData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeAlarmVH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.tab_home_alarm_layout, parent, false)
        return HomeAlarmVH(TabHomeAlarmLayoutBinding.bind(view),alarmClickListener)
    }

    override fun onBindViewHolder(holder: HomeAlarmVH, position: Int) {
        holder.alarmBind(alarmItems[holder.bindingAdapterPosition])
    }

    override fun getItemCount() = alarmItems.size

    fun setItems(items: List<HomeAlarmData>) {
        this.alarmItems = items
        notifyDataSetChanged()
    }

    fun changeRead(readPos: Int) {
        this.alarmItems[readPos].isRead = true
        notifyItemChanged(readPos)
    }

    fun changeAllRead() {
        for (a in this.alarmItems) {
            a.isRead = true
        }
        notifyItemRangeChanged(0, this.alarmItems.size)
    }
}