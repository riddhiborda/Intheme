package com.intheme.dev.ui.dailogs


import android.app.Dialog
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.intheme.dev.data.model.response.BranchResponse
import com.intheme.dev.data.model.response.CustomerResponse
import com.intheme.dev.databinding.DlgBranchBottomBinding
import com.intheme.dev.databinding.DlgCustomerDetailBottomBinding
import com.intheme.dev.ui.adaptor.BranchSelectAdaptor
import com.intheme.dev.utils.ApiState
import com.intheme.dev.utils.ArgConst.Companion.ARG_CUSTOMER_DETAIL
import com.intheme.dev.utils.ArgConst.Companion.ARG_MESSAGE
import com.intheme.dev.utils.ArgConst.Companion.ARG_TITLE
import com.intheme.dev.utils.PaginationScrollListener
import com.intheme.dev.utils.errorHandling
import com.intheme.dev.utils.fromJson
import com.intheme.dev.utils.gone
import com.intheme.dev.utils.launchWhenStarted
import com.intheme.dev.utils.loge
import com.intheme.dev.utils.reqAct
import com.intheme.dev.utils.text
import com.intheme.dev.utils.toJson
import com.intheme.dev.utils.visible
import com.intheme.dev.viewmodel.BranchViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CustomerDetailsDialog : BottomSheetDialogFragment() {
    private lateinit var binding: DlgCustomerDetailBottomBinding

    private lateinit var customer : CustomerResponse

    var isEdit :((customer: CustomerResponse)->Unit)?=null

    companion object {
        fun newInstance(customerResponse: CustomerResponse): CustomerDetailsDialog {
            val dialog = CustomerDetailsDialog()
            val args = Bundle()
            args.putString(ARG_CUSTOMER_DETAIL, customerResponse.toJson())
            dialog.arguments = args
            return dialog
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), theme).apply {
            setOnShowListener { dialog ->
                val bottomSheet = (dialog as BottomSheetDialog)
                    .findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
                bottomSheet?.let {
                    val behavior = BottomSheetBehavior.from(it)
                    behavior.state = BottomSheetBehavior.STATE_EXPANDED
                    behavior.skipCollapsed = true
                    behavior.isDraggable = false
                    val layoutParams = it.layoutParams as ViewGroup.MarginLayoutParams
                    layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
                    it.layoutParams = layoutParams
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DlgCustomerDetailBottomBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        setOnClick()
    }

    @Suppress("DEPRECATION")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.setOnKeyListener { _, keyCode, _ ->
            keyCode == KeyEvent.KEYCODE_BACK
        }
    }

    private fun initViews() {
        val customerDetailStr =  arguments?.getString(ARG_CUSTOMER_DETAIL)
        customer = customerDetailStr?.fromJson<CustomerResponse>()?:CustomerResponse()

        binding.tvName.text(customer.name?:"--")
        binding.tvEmail.text(customer.email?:"--")
        binding.tvMobileNo.text((customer.mobileNo?:0).toString())
        binding.tvAddressLine1.text(customer.addressLine1?:"--")
        binding.tvAddressLine2.text(customer.addressLine2?:"--")
        binding.tvAddressLine3.text(customer.addressLine3?:"--")
        binding.tvPinCode.text((customer.pin?:0).toString())

        binding.tvBranchName.text(customer.branchDetails?.branchName?:"--")
        binding.tvBranchLocation.text(customer.branchDetails?.branchLocation?:"--")

        binding.tvManagerName.text(customer.managerName?:"--")
        binding.tvManagerEmail.text(customer.managerEmail?:"--")
        binding.tvManagerMobile.text(customer.managerMobile?:"--")

        binding.tvOperatorName.text(customer.operatorName?:"--")
        binding.tvOperatorEmail.text(customer.operatorEmail?:"--")

        binding.tvCreatedByName.text(customer.createdBy?.name?:"--")
        binding.tvCreatedByEmail.text(customer.createdBy?.email?:"--")

    }

    private fun setOnClick(){
        binding.btnBack.setOnClickListener {
            dismiss()
        }
        binding.ivEdit.setOnClickListener {
            isEdit?.invoke(customer)
        }
    }

}