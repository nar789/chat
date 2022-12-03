package com.rndeep.fns_fantoo.ui.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.constraintlayout.widget.ConstraintLayout
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.DialogMenuBinding

class MenuDialogFragment(
    private val message: DialogMessage,
    private val onItemClicked: (DialogClickType) -> Unit
) : AppCompatDialogFragment() {

    lateinit var binding: DialogMenuBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogMenuBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setMessage(message)
    }

    override fun getTheme(): Int {
        return R.style.Widget_App_Dialog
    }

    private fun setMessage(message: DialogMessage) {
        message.title.title?.let {
            binding.msgTitle.text = it
            binding.msgTitle.visibility = View.VISIBLE
        }
        message.title.subTitle?.let {
            binding.msgSubTitle.text = it
            binding.msgSubTitle.visibility = View.VISIBLE
        }
        message.title.extraMessage?.let {
            binding.msgExtraTitle.text = it
            binding.msgExtraTitle.visibility = View.VISIBLE

            val params = binding.okBtn.layoutParams as ConstraintLayout.LayoutParams
            params.topMargin = resources.getDimensionPixelSize(R.dimen.spacing_twelve)
            binding.okBtn.layoutParams = params
        }

        if (message.button.isTwoButton) {
            binding.okBtn.visibility = View.GONE
            binding.okTwoBtn.visibility = View.VISIBLE
            binding.okTwoBtn.text = message.button.okTitle
            binding.cancelTwoBtn.visibility = View.VISIBLE
            message.button.cancelTitle?.let {
                binding.cancelTwoBtn.text = it
            }

            binding.okTwoBtn.setOnClickListener {
                onItemClicked(DialogClickType.OK)
            }
            binding.cancelTwoBtn.setOnClickListener {
                onItemClicked(DialogClickType.CANCEL)
            }
        } else {
            binding.okBtn.visibility = View.VISIBLE
            binding.okBtn.text = message.button.okTitle
            binding.okTwoBtn.visibility = View.GONE
            binding.cancelTwoBtn.visibility = View.GONE

            binding.okBtn.setOnClickListener {
                onItemClicked(DialogClickType.OK)
            }
        }
        if (message.isCompleted) {
            binding.fanitContainer.visibility = View.VISIBLE
        } else {
            binding.fanitContainer.visibility = View.GONE
        }
    }

    companion object {
        const val DIALOG_MENU = "dialog_menu"
    }
}

