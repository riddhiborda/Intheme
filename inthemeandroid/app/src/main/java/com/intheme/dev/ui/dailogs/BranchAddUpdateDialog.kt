package com.intheme.dev.ui.dailogs

import android.os.Bundle
import com.intheme.dev.base.BaseDialogFragment
import com.intheme.dev.databinding.DlgAddUpdateBranchBinding
import com.intheme.dev.databinding.DlgConfomationBinding
import com.intheme.dev.ui.activity.LoginActivity.VALIDATION_LOGIN
import com.intheme.dev.utils.ArgConst.Companion.ARG_BRANCH_ID
import com.intheme.dev.utils.ArgConst.Companion.ARG_BRANCH_LOCATION
import com.intheme.dev.utils.ArgConst.Companion.ARG_BRANCH_NAME
import com.intheme.dev.utils.ArgConst.Companion.ARG_ID
import com.intheme.dev.utils.ArgConst.Companion.ARG_IS_BRANCH_UPDATE
import com.intheme.dev.utils.ArgConst.Companion.ARG_MESSAGE
import com.intheme.dev.utils.ArgConst.Companion.ARG_TITLE
import com.intheme.dev.utils.ConformationDlgClick


class BranchAddUpdateDialog: BaseDialogFragment<DlgAddUpdateBranchBinding>(DlgAddUpdateBranchBinding::inflate) {

    var onClick :((ConformationDlgClick,id:String,name:String,location:String)->Unit)?=null

    var strBranchId = ""
    var strBranchName = ""
    var strBranchLocation = ""
    var isBranchUpdate = false

    enum class VALIDATION_BANACH {
        BRANCH_NAME,
        BRANCH_LOCATION
    }

    companion object {
        fun newInstance(title: String, branchId: String,branchName:String,branchLocation:String,isUpdate:Boolean): BranchAddUpdateDialog {
            val dialog = BranchAddUpdateDialog()
            val args = Bundle()
            args.putString(ARG_TITLE, title)
            args.putBoolean(ARG_IS_BRANCH_UPDATE, isUpdate)
            args.putString(ARG_BRANCH_ID, branchId)
            args.putString(ARG_BRANCH_NAME, branchName)
            args.putString(ARG_BRANCH_LOCATION, branchLocation)
            dialog.arguments = args
            return dialog
        }
    }

    override fun initView() {
        val title = arguments?.getString(ARG_TITLE)
        strBranchId = arguments?.getString(ARG_BRANCH_ID)?:""
        strBranchName = arguments?.getString(ARG_BRANCH_NAME)?:""
        strBranchLocation = arguments?.getString(ARG_BRANCH_LOCATION)?:""
        isBranchUpdate = arguments?.getBoolean(ARG_IS_BRANCH_UPDATE,false)?:false
        binding?.tvTitle?.text = title
        binding?.edBranchName?.setText(strBranchName)
        binding?.edBranchLocation?.setText(strBranchLocation)
        binding?.tvYes?.text = if(isBranchUpdate) "Update" else "Add"
    }

    override fun setupObservers() {

    }

    private fun showValidation(validateType: VALIDATION_BANACH?, msg: String?) {
        binding?.tilBranchName?.isErrorEnabled = false
        binding?.tilBranchName?.errorIconDrawable = null
        binding?.tilBranchLocation?.isErrorEnabled = false
        binding?.tilBranchLocation?.errorIconDrawable = null
        when (validateType) {
            VALIDATION_BANACH.BRANCH_NAME -> {
                binding?.tilBranchName?.isErrorEnabled = true
                binding?.tilBranchName?.error = msg
            }
            VALIDATION_BANACH.BRANCH_LOCATION -> {
                binding?.tilBranchLocation?.isErrorEnabled = true
                binding?.tilBranchLocation?.error = msg
            }
            else -> {

            }
        }
    }

    override fun setOnClicks() {
      binding?.tvYes?.setOnClickListener {
          if(binding?.edBranchName?.text?.isEmpty() == true){
             showValidation(VALIDATION_BANACH.BRANCH_NAME,"Branch Name")
          }else if(binding?.edBranchLocation?.text?.isEmpty() == true){
             showValidation(VALIDATION_BANACH.BRANCH_LOCATION,"Branch Location")
          }else{
             onClick?.invoke(ConformationDlgClick.YES,strBranchId,binding?.edBranchName?.text.toString(),binding?.edBranchLocation?.text.toString())
             dismiss()
          }
      }
      binding?.tvNo?.setOnClickListener {
          onClick?.invoke(ConformationDlgClick.NO,"","","")
          dismiss()
      }
    }
}