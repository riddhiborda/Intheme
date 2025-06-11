package com.intheme.dev.ui.dailogs

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.google.android.material.dialog.MaterialAlertDialogBuilder


//@SuppressLint("InflateParams")
//fun Context.mMaintenanceModeDialog() {
//    var dialog: Dialog? = null
//    try {
//        dialog = Dialog(this, R.style.AppCompatAlertDialogStyleBig)
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        dialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
//        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        dialog.setCancelable(false)
//        val mInflater = LayoutInflater.from(this)
//        val mView = mInflater.inflate(R.layout.dlg_undermaintainus, null, false)
//        val tvYes: TextView = mView.findViewById(R.id.tvYes)
//        tvYes.setOnClickListener {
//            dialog.dismiss()
//        }
//        dialog.setContentView(mView)
//        dialog.show()
//    } catch (e: java.lang.Exception) {
//        e.printStackTrace()
//    }
//}



inline fun Context.showAlertDialog(
    strTitle:String = "",
    strMessage:String = "",
    strPositiveBtnText:String = "",
    strNegativeBtnText:String = "",
    crossinline positiveButtonCallBack : () -> Unit,
    crossinline negativeButtonCallBack : () -> Unit
) {
    if(strNegativeBtnText.isNotEmpty()){
        MaterialAlertDialogBuilder(this)
            .setTitle(strTitle)
            .setMessage(
                strMessage
            )
            .setPositiveButton(strPositiveBtnText) { _, _ ->
                positiveButtonCallBack.invoke()
            }
            .setNegativeButton(strNegativeBtnText) { _, _ ->
                negativeButtonCallBack.invoke()
            }
            .show()
    }else{
        MaterialAlertDialogBuilder(this)
            .setTitle(strTitle)
            .setMessage(
                strMessage
            )
            .setPositiveButton(strPositiveBtnText) { _, _ ->
                positiveButtonCallBack.invoke()
            }
            .show()
    }

}

// Show a dialog guiding the user to app settings
inline fun Context.showSettingsDialog(crossinline callback : () -> Unit) {
    MaterialAlertDialogBuilder(this)
        .setTitle("Permission Denied")
        .setMessage("You have denied permission multiple times. Please go to settings and enable the permission manually.")
        .setPositiveButton("Open Settings") { _, _ ->
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", this.`package`, null)
            }
            this.startActivity(intent)
        }
        .setNegativeButton("Cancel") { _, _ ->
            callback.invoke()
        }
        .show()
}