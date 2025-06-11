package com.intheme.dev.ui.dailogs

import android.os.Bundle
import com.intheme.dev.base.BaseDialogFragment
import com.intheme.dev.databinding.DlgConfomationBinding
import com.intheme.dev.utils.ArgConst.Companion.ARG_MESSAGE
import com.intheme.dev.utils.ArgConst.Companion.ARG_TITLE
import com.intheme.dev.utils.ConformationDlgClick


class ConformationDialog: BaseDialogFragment<DlgConfomationBinding>(DlgConfomationBinding::inflate) {

    var onClick :((ConformationDlgClick)->Unit)?=null

    companion object {
        fun newInstance(title: String, message: String): ConformationDialog {
            val dialog = ConformationDialog()
            val args = Bundle()
            args.putString(ARG_TITLE, title)
            args.putString(ARG_MESSAGE, message)
            dialog.arguments = args
            return dialog
        }
    }

    override fun initView() {
        val title = arguments?.getString(ARG_TITLE)
        val message = arguments?.getString(ARG_MESSAGE)
        binding?.tvTitle?.text = title
        binding?.tvDesc?.text = message
    }
    override fun setupObservers() {

    }
    override fun setOnClicks() {
      binding?.tvYes?.setOnClickListener {
          onClick?.invoke(ConformationDlgClick.YES)
          dismiss()
      }
      binding?.tvNo?.setOnClickListener {
          onClick?.invoke(ConformationDlgClick.NO)
          dismiss()
      }
    }
}