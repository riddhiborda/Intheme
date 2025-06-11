package com.intheme.dev.ui.dailogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import com.intheme.dev.R

class ProgressDialog   {
    private var dialog: Dialog? = null

    fun showProgress(context: Context?) {
        if (dialog != null) {
            return
        }
        context?.let {
            dialog = Dialog(it, R.style.AppCompatAlertDialogStyleBig)
            dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog?.setContentView(R.layout.dlg_progress)
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog?.setCanceledOnTouchOutside(false)
            dialog?.setCancelable(false)
            dialog?.show()
        }
    }

    fun dismissProgress() {
        if(dialog?.isShowing == true){
            dialog?.dismiss()
            dialog = null
        }
    }

    val isShowing: Boolean
        get() = dialog?.isShowing == true

}