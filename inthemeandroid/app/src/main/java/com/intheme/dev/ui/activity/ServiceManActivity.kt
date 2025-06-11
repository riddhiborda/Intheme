package com.intheme.dev.ui.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.intheme.dev.R
import com.intheme.dev.base.BaseActivity
import com.intheme.dev.data.model.response.User
import com.intheme.dev.databinding.ActivityAddBranchAdminBinding
import com.intheme.dev.databinding.ActivityServiceManBinding
import com.intheme.dev.ui.adaptor.BranchAdminListAdaptor
import com.intheme.dev.ui.adaptor.ServiceManListAdaptor
import com.intheme.dev.ui.dailogs.ConformationDialog
import com.intheme.dev.ui.dailogs.ProgressDialog
import com.intheme.dev.utils.ApiState
import com.intheme.dev.utils.ArgConst.Companion.ARG_BRANCH_ADMIN_ID
import com.intheme.dev.utils.ArgConst.Companion.ARG_BRANCH_ADMIN_IMAGE
import com.intheme.dev.utils.ArgConst.Companion.ARG_BRANCH_ID
import com.intheme.dev.utils.ArgConst.Companion.ARG_BRANCH_NAME
import com.intheme.dev.utils.ArgConst.Companion.ARG_EMAIL_ID
import com.intheme.dev.utils.ArgConst.Companion.ARG_IS_EDIT
import com.intheme.dev.utils.ArgConst.Companion.ARG_MOBILE_NO
import com.intheme.dev.utils.ArgConst.Companion.ARG_NAME
import com.intheme.dev.utils.ConformationDlgClick
import com.intheme.dev.utils.Constants.Companion.CONFORMATION_DIALOG
import com.intheme.dev.utils.EVENT_TYPE
import com.intheme.dev.utils.GlobalEventBus
import com.intheme.dev.utils.PaginationScrollListener
import com.intheme.dev.utils.errorHandling
import com.intheme.dev.utils.gone
import com.intheme.dev.utils.launchWhenStarted
import com.intheme.dev.utils.showSuccessToast
import com.intheme.dev.utils.startActivity
import com.intheme.dev.utils.visible
import com.intheme.dev.viewmodel.BranchAdminViewModel
import com.intheme.dev.viewmodel.ServiceManViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.properties.Delegates

@AndroidEntryPoint
class ServiceManActivity : BaseActivity<ActivityServiceManBinding>(ActivityServiceManBinding::inflate) {

    private var CURRENT_PAGES by Delegates.notNull<Int>()
    private var TOTAL_PAGES by Delegates.notNull<Int>()
    private var isPaginationLoading = false
    private var isInitLoading = false
    private val serviceManViewModel: ServiceManViewModel by viewModels()

    private val serviceManListAdaptor: ServiceManListAdaptor by lazy {
        ServiceManListAdaptor(this@ServiceManActivity)
    }

    private val progressDialog: ProgressDialog by lazy {
        ProgressDialog()
    }

    override fun initView() {
        val layoutManager = LinearLayoutManager(this@ServiceManActivity)
        binding.rvListItem.also {
            it.adapter = serviceManListAdaptor
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

    override fun setupObservers() {
        launchWhenStarted {
            serviceManViewModel.mGetServiceManListStateFlow.collect {
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
                            serviceManListAdaptor.addData(it.data.data.data)
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
            GlobalEventBus.event.sharedFlow.collect { event ->
                when (event.eventType) {
                    EVENT_TYPE.ADD_SERVICE_MAN -> {
                        serviceManListAdaptor.clearArrayList()
                        loadInitItem()
                    }
                    EVENT_TYPE.UPDATE_SERVICE_MAN -> {
                        serviceManListAdaptor.clearArrayList()
                        loadInitItem()
                    }
                    else->{}
                }
            }
        }

        launchWhenStarted {
            serviceManViewModel.mDeleteServiceManStateFlow.collect {
                when (it) {
                    is ApiState.Loading -> {
                        progressDialog.showProgress(this@ServiceManActivity)
                    }

                    is ApiState.Failure -> {
                        progressDialog.dismissProgress()
                        errorHandling(logout = {

                        },it.isNetworkError, it.msg)
                    }

                    is ApiState.Success -> {
                        progressDialog.dismissProgress()
                        it.data.message.showSuccessToast(this@ServiceManActivity)
                        serviceManListAdaptor.clearArrayList()
                        loadInitItem()
                    }
                    else -> {

                    }
                }
            }
        }
    }

    override fun setOnClicks() {
        binding.ivBack.setOnClickListener {
            onBackPress()
        }

        binding.ivAdd.setOnClickListener {
            startActivity<AddServiceManActivity>()
        }

        serviceManListAdaptor.onEditClick = { branchAdminResponse, pos ->
            startActivity<AddServiceManActivity>{
                this.putExtra(ARG_IS_EDIT,true)
                this.putExtra(ARG_BRANCH_ADMIN_ID,branchAdminResponse.id)
                this.putExtra(ARG_BRANCH_ADMIN_IMAGE,branchAdminResponse.image)
                this.putExtra(ARG_NAME,branchAdminResponse.name)
                this.putExtra(ARG_EMAIL_ID,branchAdminResponse.email)
                this.putExtra(ARG_MOBILE_NO,branchAdminResponse.mobileNo)
                this.putExtra(ARG_BRANCH_ID,branchAdminResponse.branchDetails?.branchId?:"")
                this.putExtra(ARG_BRANCH_NAME,branchAdminResponse.branchDetails?.branchName?:"")
            }
        }

        serviceManListAdaptor.onDeleteClick = { user, pos ->
            mDeleteServiceManDialog(user)
        }
    }

    override fun onBackPress() {
        finish()
    }

    private fun mDeleteServiceManDialog(user: User?) {
        val conformationDialog = ConformationDialog.newInstance(
            title = getString(R.string.delete_service_man),
            message = getString(R.string.delete_service_man_detail)
        )
        conformationDialog.onClick = { it ->
            when (it) {
                ConformationDlgClick.YES -> {
                    user?.id?.let {  serviceManViewModel.deleteServiceMan(it) }
                }
                ConformationDlgClick.NO -> {

                }
            }
        }
        conformationDialog.show(supportFragmentManager, CONFORMATION_DIALOG)
    }

    private fun loadMoreItems() {
        isPaginationLoading = true
        isInitLoading = false
        CURRENT_PAGES += 1
        serviceManViewModel.getServiceManList(
            CURRENT_PAGES,
            10
        )
    }

    private fun loadInitItem() {
        isPaginationLoading = false
        isInitLoading = true
        CURRENT_PAGES = 1
        serviceManViewModel.getServiceManList(
            CURRENT_PAGES,
            10
        )
    }

}