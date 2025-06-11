package com.intheme.dev.ui.activity

import android.content.Intent
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.intheme.dev.R
import com.intheme.dev.base.BaseActivity
import com.intheme.dev.data.model.response.CustomerResponse
import com.intheme.dev.databinding.ActivityCustomerListBinding
import com.intheme.dev.ui.adaptor.CustomerListAdaptor
import com.intheme.dev.ui.dailogs.ConformationDialog
import com.intheme.dev.ui.dailogs.CustomerDetailsDialog
import com.intheme.dev.ui.dailogs.ProgressDialog
import com.intheme.dev.utils.ApiState
import com.intheme.dev.utils.ArgConst.Companion.ARG_CUSTOMER_DETAIL
import com.intheme.dev.utils.ArgConst.Companion.ARG_CUSTOMER_ID
import com.intheme.dev.utils.ArgConst.Companion.ARG_IS_EDIT
import com.intheme.dev.utils.ConformationDlgClick
import com.intheme.dev.utils.Constants.Companion.BRANCH_SELECT_DIALOG
import com.intheme.dev.utils.Constants.Companion.CONFORMATION_DIALOG
import com.intheme.dev.utils.EVENT_TYPE
import com.intheme.dev.utils.GlobalEventBus
import com.intheme.dev.utils.PaginationScrollListener
import com.intheme.dev.utils.errorHandling
import com.intheme.dev.utils.gone
import com.intheme.dev.utils.launchWhenStarted
import com.intheme.dev.utils.showIfNotExists
import com.intheme.dev.utils.showSuccessToast
import com.intheme.dev.utils.startActivity
import com.intheme.dev.utils.toJson
import com.intheme.dev.utils.visible
import com.intheme.dev.viewmodel.CustomerViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.properties.Delegates

@AndroidEntryPoint
class CustomerListActivity : BaseActivity<ActivityCustomerListBinding>(ActivityCustomerListBinding::inflate) {
    private var CURRENT_PAGES by Delegates.notNull<Int>()
    private var TOTAL_PAGES by Delegates.notNull<Int>()
    private var isPaginationLoading = false
    private var isInitLoading = false
    private val customerViewModel: CustomerViewModel by viewModels()

    private val customerListAdaptor: CustomerListAdaptor by lazy {
        CustomerListAdaptor()
    }

    private val progressDialog: ProgressDialog by lazy {
        ProgressDialog()
    }

    override fun initView() {
        val layoutManager = LinearLayoutManager(this@CustomerListActivity)
        binding.rvListItem.also {
            it.adapter = customerListAdaptor
            it.layoutManager = layoutManager
            it.addOnScrollListener(PaginationScrollListener(layoutManager) {
                binding.rvListItem.post {
                    if ((CURRENT_PAGES < TOTAL_PAGES) && !isPaginationLoading) {
                        loadMoreItems()
                    }
                }
            })
        }
        loadInitItem()
    }

    private fun loadMoreItems() {
        isPaginationLoading = true
        isInitLoading = false
        CURRENT_PAGES += 1
        customerViewModel.getCustomerList(
            CURRENT_PAGES
        )
    }

    private fun loadInitItem() {
        isPaginationLoading = false
        isInitLoading = true
        CURRENT_PAGES = 1
        customerViewModel.getCustomerList(
            CURRENT_PAGES
        )
    }

    override fun setupObservers() {
        launchWhenStarted {
            GlobalEventBus.event.sharedFlow.collect { event ->
                when (event.eventType) {
                    EVENT_TYPE.ADD_UPDATE_CUSTOMER -> {
                        customerListAdaptor.clearArrayList()
                        loadInitItem()
                    }
                    else -> {}
                }
            }
        }
        launchWhenStarted {
            customerViewModel.mGetCustomerListStateFlow.collect {
                when (it) {
                    is ApiState.Loading -> {
                        if(isPaginationLoading){
                            binding.pbBottom.visible()
                            binding.rvListItem.gone()
                            binding.tvNoDataFound.gone()
                            binding.pbLoader.gone()
                        }else{
                            binding.pbBottom.gone()
                            binding.rvListItem.gone()
                            binding.tvNoDataFound.gone()
                            binding.pbLoader.visible()
                        }
                    }

                    is ApiState.Failure -> {
                        binding.pbBottom.gone()
                        binding.rvListItem.gone()
                        binding.tvNoDataFound.visible()
                        binding.pbLoader.gone()
                        isPaginationLoading = false
                        isInitLoading = false
                        errorHandling(logout = {
                            customerViewModel.setLogout()
                            startActivity<LoginActivity> {  }
                            finish()
                            finishAffinity()
                        },it.isNetworkError, it.msg)
                    }

                    is ApiState.Success -> {
                        binding.pbBottom.gone()
                        binding.pbLoader.gone()
                        isPaginationLoading = false
                        isInitLoading = false
                        TOTAL_PAGES=it.data.data.lastPage?:0
                        if(it.data.data.data.isNullOrEmpty()){
                            binding.tvNoDataFound.visible()
                            binding.rvListItem.gone()
                        }else{
                            customerListAdaptor.addData(it.data.data.data)
                            binding.tvNoDataFound.gone()
                            binding.rvListItem.visible()
                        }
                    }
                    else -> {

                    }
                }
            }
        }
        launchWhenStarted {
            customerViewModel.mCreateAndUpdateCustomerStateFlow.collect {
                when (it) {
                    is ApiState.Loading -> {
                        progressDialog.showProgress(this@CustomerListActivity)
                    }

                    is ApiState.Failure -> {
                        progressDialog.dismissProgress()
                        errorHandling(logout = {
                            customerViewModel.setLogout()
                            startActivity<LoginActivity> {  }
                            finish()
                            finishAffinity()
                        },it.isNetworkError, it.msg)
                    }

                    is ApiState.Success -> {
                        progressDialog.dismissProgress()
                        it.data.message.showSuccessToast(this@CustomerListActivity)
                        customerListAdaptor.clearArrayList()
                        loadInitItem()
                    }
                    else -> {

                    }
                }
            }
        }

        launchWhenStarted {
            customerViewModel.mDeleteCustomerStateFlow.collect {
                when (it) {
                    is ApiState.Loading -> {
                        progressDialog.showProgress(this@CustomerListActivity)
                    }

                    is ApiState.Failure -> {
                        progressDialog.dismissProgress()
                        errorHandling(logout = {
                            customerViewModel.setLogout()
                            startActivity<LoginActivity> {  }
                            finish()
                            finishAffinity()
                        },it.isNetworkError, it.msg)
                    }

                    is ApiState.Success -> {
                        progressDialog.dismissProgress()
                        it.data.message.showSuccessToast(this@CustomerListActivity)
                        customerListAdaptor.clearArrayList()
                        loadInitItem()
                    }
                    else -> {

                    }
                }
            }
        }
    }

    override fun setOnClicks() {
        customerListAdaptor.onEditAndDeleteClick = { customerResponse,pos,isEdit,isView,isDelete ->
            if(isEdit){
                val intent = Intent(this@CustomerListActivity,AddCustomerActivity::class.java)
                intent.putExtra(ARG_IS_EDIT,true)
                intent.putExtra(ARG_CUSTOMER_ID,customerResponse.id)
                intent.putExtra(ARG_CUSTOMER_DETAIL,customerResponse.toJson())
                startActivity(intent)
            }
            if(isDelete){
                mDeleteCustomerDialog(customerResponse, pos)
            }
            if(isView){
                showBranchDialog(customerResponse)
            }
        }
        binding.ivBack.setOnClickListener {
            finish()
        }
        binding.ivAdd.setOnClickListener {
            startActivity(Intent(this@CustomerListActivity,AddCustomerActivity::class.java))
        }
    }

    override fun onBackPress() {
       finish()
    }

    private fun mDeleteCustomerDialog(customerResponse: CustomerResponse, pos:Int) {
        val conformationDialog = ConformationDialog.newInstance(
            title = getString(R.string.delete_branch),
            message = getString(R.string.delete_branch_detail)
        )
        conformationDialog.onClick = { it ->
            when (it) {
                ConformationDlgClick.YES -> {
                    customerResponse.id?.let {  customerViewModel.deleteCustomer(it) }
                }
                ConformationDlgClick.NO -> {

                }
            }
        }
        conformationDialog.show(supportFragmentManager, CONFORMATION_DIALOG)
    }

    private fun showBranchDialog(customerResponse: CustomerResponse) {
        val customerDetailsDialog = CustomerDetailsDialog.newInstance(
            customerResponse =customerResponse
        )
        customerDetailsDialog.also {
            it.isEdit = { customer ->
                val intent = Intent(this@CustomerListActivity,AddCustomerActivity::class.java)
                intent.putExtra(ARG_IS_EDIT,true)
                intent.putExtra(ARG_CUSTOMER_ID,customer.id)
                intent.putExtra(ARG_CUSTOMER_DETAIL,customer.toJson())
                startActivity(intent)
                it.dismiss()
            }
            it.showIfNotExists(supportFragmentManager, BRANCH_SELECT_DIALOG)
        }
    }

}