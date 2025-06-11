package com.intheme.dev.ui.activity

import android.annotation.SuppressLint
import androidx.activity.viewModels
import com.intheme.dev.R
import com.intheme.dev.base.BaseActivity
import com.intheme.dev.data.model.EventModel
import com.intheme.dev.data.model.request.CustomerRequest
import com.intheme.dev.data.model.response.CustomerResponse
import com.intheme.dev.databinding.ActivityAddCustomerBinding
import com.intheme.dev.ui.dailogs.BranchSelectDialog
import com.intheme.dev.ui.dailogs.ProgressDialog
import com.intheme.dev.utils.ApiState
import com.intheme.dev.utils.ArgConst.Companion.ARG_BRANCH_ADMIN_ID
import com.intheme.dev.utils.ArgConst.Companion.ARG_CUSTOMER_DETAIL
import com.intheme.dev.utils.ArgConst.Companion.ARG_IS_EDIT
import com.intheme.dev.utils.Constants.Companion.BRANCH_SELECT_DIALOG
import com.intheme.dev.utils.EVENT_TYPE
import com.intheme.dev.utils.GlobalEventBus
import com.intheme.dev.utils.VALIDATION_CUSTOMER
import com.intheme.dev.utils.errorHandling
import com.intheme.dev.utils.fromJson
import com.intheme.dev.utils.launchWhenStarted
import com.intheme.dev.utils.setVisibleView
import com.intheme.dev.utils.showIfNotExists
import com.intheme.dev.utils.showSuccessToast
import com.intheme.dev.utils.startActivity
import com.intheme.dev.viewmodel.CustomerViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddCustomerActivity : BaseActivity<ActivityAddCustomerBinding>(ActivityAddCustomerBinding::inflate) {

    private var isStarted: Boolean = false
    private var isEdit : Boolean = false
    private var branchSelectName:String=""
    private var branchSelectId:String=""
    private var id:String=""
    private val customerViewModel: CustomerViewModel by viewModels()
    private val progressDialog: ProgressDialog by lazy {
        ProgressDialog()
    }


    @SuppressLint("SetTextI18n")
    override fun initView() {
        isEdit = intent.getBooleanExtra(ARG_IS_EDIT,false)
        id = intent.getStringExtra(ARG_BRANCH_ADMIN_ID)?:""
        val getCustomerDetail = intent.getStringExtra(ARG_CUSTOMER_DETAIL)?:""
        val detail: CustomerResponse? = if(getCustomerDetail.isEmpty()) null else getCustomerDetail.fromJson<CustomerResponse>()
        binding.edName.setText(detail?.name?:"")
        binding.edPhone.setText((detail?.mobileNo?:0).toString())
        binding.edEmail.setText(detail?.email?:"")
        branchSelectId = detail?.branchDetails?.branchId?:""
        branchSelectName = detail?.branchDetails?.branchName?:""
        binding.edManageName.setText(detail?.managerName?:"")
        binding.edManageEmail.setText(detail?.managerEmail?:"")
        binding.edManagePhone.setText(detail?.managerMobile?:"")
        binding.edBranch.setText(detail?.branchDetails?.branchName?:"")
        binding.edManageAddress1.setText(detail?.addressLine1?:"")
        binding.edManageAddress2.setText(detail?.addressLine2?:"")
        binding.edManageAddress3.setText(detail?.addressLine3?:"")
        binding.edPin.setText((detail?.pin?:0).toString())
        binding.edOperatorName.setText(detail?.operatorName?:"")
        binding.edOperatorEmail.setText(detail?.operatorEmail?:"")
        binding.edgstin.setText(detail?.gstin?:"")
        if(isEdit){
            binding.lblToolbar.text = getString(R.string.update_customer)
        }
        binding.tilBranch.setVisibleView(customerViewModel.getUserDetail().role == "admin")
    }

    override fun setupObservers() {
        launchWhenStarted {
            customerViewModel.mCreateAndUpdateCustomerStateFlow.collect {
                when (it) {
                    is ApiState.Loading -> {
                        progressDialog.showProgress(this@AddCustomerActivity)
                        isStarted = true
                    }

                    is ApiState.Failure -> {
                        progressDialog.dismissProgress()
                        errorHandling(logout = {
                            customerViewModel.setLogout()
                            startActivity<LoginActivity> {  }
                            finish()
                            finishAffinity()
                        },it.isNetworkError, it.msg)
                        isStarted = false
                    }

                    is ApiState.Success -> {
                        progressDialog.dismissProgress()
                        it.data.message.showSuccessToast(this@AddCustomerActivity)
                        isStarted = false
                        GlobalEventBus.event.emit(EventModel(EVENT_TYPE.ADD_UPDATE_CUSTOMER,""))
                        onBackPress()
                    }
                    else -> {

                    }
                }
            }
        }
    }

    override fun setOnClicks() {
        binding.edBranch.setOnClickListener {
            showBranchDialog()
        }

        binding.btnSubmit.setOnClickListener {
            if (binding.edName.text.toString().isEmpty()) {
                showValidation(VALIDATION_CUSTOMER.NAME,getString(R.string.enter_full_name))
            } else if (binding.edEmail.text.toString().isEmpty()) {
                showValidation(VALIDATION_CUSTOMER.EMAIL,getString(R.string.enter_email_id))
            } else if (binding.edPhone.text.toString().isEmpty()) {
                showValidation(VALIDATION_CUSTOMER.MOBILE_NO,getString(R.string.enter_mobile_no))
            } else if (binding.edBranch.text.toString().isEmpty() && customerViewModel.getUserDetail().role == "admin") {
                showValidation(VALIDATION_CUSTOMER.BRANCH,getString(R.string.select_branch))
            }else {
                showValidation(VALIDATION_CUSTOMER.APICALL,null)
                val name = binding.edName.text.toString()
                val email = binding.edEmail.text.toString()
                val mobile = binding.edPhone.text.toString()
                val managerName = binding.edManageName.text.toString()
                val managerEmail = binding.edManageEmail.text.toString()
                val managerMobile = binding.edManagePhone.text.toString()
                val addressLine1 = binding.edManageAddress1.text.toString()
                val addressLine2 = binding.edManageAddress2.text.toString()
                val addressLine3 = binding.edManageAddress3.text.toString()
                val operatorName = binding.edOperatorName.text.toString()
                val operatorEmail = binding.edOperatorEmail.text.toString()
                val gstin = binding.edgstin.text.toString()
                val customerRequest = CustomerRequest(
                    name = name,
                    email = email,
                    mobileNo = mobile,
                    branchId = branchSelectId,
                    managerName = managerName,
                    managerEmail = managerEmail,
                    managerMobile = managerMobile,
                    addressLine1 = addressLine1,
                    addressLine2 = addressLine2,
                    addressLine3 = addressLine3,
                    operatorName = operatorName,
                    operatorEmail = operatorEmail,
                    gstin = gstin
                )
                customerViewModel.addAndUpdateCustomer(customerRequest = customerRequest,strId = id)
            }
        }

        binding.ivBack.setOnClickListener {
            onBackPress()
        }
    }

    private fun showValidation(validateType:VALIDATION_CUSTOMER?, msg: String?) {
        binding.tilName.isErrorEnabled = false
        binding.tilName.errorIconDrawable = null
        binding.tilEmail.isErrorEnabled = false
        binding.tilEmail.errorIconDrawable = null
        binding.tilPhone.isErrorEnabled = false
        binding.tilPhone.errorIconDrawable = null
        binding.tilBranch.isErrorEnabled = false
        binding.tilBranch.errorIconDrawable = null
        when (validateType) {
            VALIDATION_CUSTOMER.NAME -> {
                binding.tilName.isErrorEnabled = true
                binding.tilName.error = msg
            }
            VALIDATION_CUSTOMER.EMAIL -> {
                binding.tilEmail.isErrorEnabled = true
                binding.tilEmail.error = msg
            }
            VALIDATION_CUSTOMER.MOBILE_NO -> {
                binding.tilPhone.isErrorEnabled = true
                binding.tilPhone.error = msg
            }
            VALIDATION_CUSTOMER.BRANCH -> {
                binding.tilBranch.isErrorEnabled = true
                binding.tilBranch.error = msg
            }
            else->{

            }
        }
    }

    override fun onBackPress() {
        if(!isStarted){
            finish()
        }
    }

    private fun showBranchDialog() {
        BranchSelectDialog().also {
            it.branchSelect = { branch,_->
                branchSelectName = branch.branchName?:""
                branchSelectId = branch.id?:""
                binding.edBranch.setText(branch.branchName?:"")
            }
            it.showIfNotExists(supportFragmentManager, BRANCH_SELECT_DIALOG)
        }
    }

}