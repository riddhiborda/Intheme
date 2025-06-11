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
import com.intheme.dev.data.model.request.BranchRequest
import com.intheme.dev.data.model.response.BranchResponse
import com.intheme.dev.databinding.ActivityBranchListBinding
import com.intheme.dev.databinding.ActivityDashboardBinding
import com.intheme.dev.ui.activity.LoginActivity.VALIDATION_LOGIN
import com.intheme.dev.ui.adaptor.BranchListAdaptor
import com.intheme.dev.ui.dailogs.BranchAddUpdateDialog
import com.intheme.dev.ui.dailogs.ConformationDialog
import com.intheme.dev.ui.dailogs.ProgressDialog
import com.intheme.dev.utils.ApiState
import com.intheme.dev.utils.ConformationDlgClick
import com.intheme.dev.utils.Constants.Companion.ARG_EMAIL
import com.intheme.dev.utils.Constants.Companion.CONFORMATION_DIALOG
import com.intheme.dev.utils.PaginationScrollListener
import com.intheme.dev.utils.errorHandling
import com.intheme.dev.utils.gone
import com.intheme.dev.utils.launchWhenStarted
import com.intheme.dev.utils.showSuccessToast
import com.intheme.dev.utils.startActivity
import com.intheme.dev.utils.visible
import com.intheme.dev.viewmodel.AuthViewModel
import com.intheme.dev.viewmodel.BranchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.properties.Delegates

@AndroidEntryPoint
class BranchListActivity : BaseActivity<ActivityBranchListBinding>(ActivityBranchListBinding::inflate) {
    private var CURRENT_PAGES by Delegates.notNull<Int>()
    private var TOTAL_PAGES by Delegates.notNull<Int>()
    private var isPaginationLoading = false
    private var isInitLoading = false
    private val branchViewModel: BranchViewModel by viewModels()

    private val branchListAdaptor: BranchListAdaptor by lazy {
        BranchListAdaptor()
    }

    private val progressDialog: ProgressDialog by lazy {
        ProgressDialog()
    }

    override fun initView() {
        val layoutManager = LinearLayoutManager(this@BranchListActivity)
        binding.rvListItem.also {
            it.adapter = branchListAdaptor
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
            branchViewModel.mGetBranchListStateFlow.collect {
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
                            branchListAdaptor.addData(it.data.data.data)
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
            branchViewModel.mCreateBranchStateFlow.collect {
                when (it) {
                    is ApiState.Loading -> {
                        progressDialog.showProgress(this@BranchListActivity)
                    }

                    is ApiState.Failure -> {
                        progressDialog.dismissProgress()
                        errorHandling(logout = {
                            branchViewModel.setLogout()
                            startActivity<LoginActivity> {  }
                            finish()
                            finishAffinity()
                        },it.isNetworkError, it.msg)
                    }

                    is ApiState.Success -> {
                        progressDialog.dismissProgress()
                        it.data.message.showSuccessToast(this@BranchListActivity)
                        branchListAdaptor.clearArrayList()
                        loadInitItem()
                    }
                    else -> {

                    }
                }
            }
        }
        launchWhenStarted {
            branchViewModel.mUpdateBranchStateFlow.collect {
                when (it) {
                    is ApiState.Loading -> {
                        progressDialog.showProgress(this@BranchListActivity)
                    }

                    is ApiState.Failure -> {
                        progressDialog.dismissProgress()
                        errorHandling(logout = {
                            branchViewModel.setLogout()
                            startActivity<LoginActivity> {  }
                            finish()
                            finishAffinity()
                        },it.isNetworkError, it.msg)
                    }

                    is ApiState.Success -> {
                        progressDialog.dismissProgress()
                        it.data.message.showSuccessToast(this@BranchListActivity)
                        branchListAdaptor.clearArrayList()
                        loadInitItem()
                    }
                    else -> {

                    }
                }
            }
        }
        launchWhenStarted {
            branchViewModel.mDeleteBranchStateFlow.collect {
                when (it) {
                    is ApiState.Loading -> {
                        progressDialog.showProgress(this@BranchListActivity)
                    }

                    is ApiState.Failure -> {
                        progressDialog.dismissProgress()
                        errorHandling(logout = {
                            branchViewModel.setLogout()
                            startActivity<LoginActivity> {  }
                            finish()
                            finishAffinity()
                        },it.isNetworkError, it.msg)
                    }

                    is ApiState.Success -> {
                        progressDialog.dismissProgress()
                        it.data.message.showSuccessToast(this@BranchListActivity)
                        branchListAdaptor.clearArrayList()
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
            finish()
        }

        binding.ivAdd.setOnClickListener {
            mAddUpdateBranchDialog("","","", isUpdate = false)
        }

        branchListAdaptor.onEditClick = { branchResponse, pos ->
            mAddUpdateBranchDialog(branchResponse.id?:"",branchResponse.branchName?:"",branchResponse.branchLocation?:"", isUpdate = true)
        }

        branchListAdaptor.onDeleteClick = { branchResponse, pos ->
            mExitDialog(branchResponse,pos)
        }
    }

    private fun mExitDialog(branchResponse:BranchResponse,pos:Int) {
        val conformationDialog = ConformationDialog.newInstance(
            title = getString(R.string.delete_branch),
            message = getString(R.string.delete_branch_detail)
        )
        conformationDialog.onClick = { it ->
            when (it) {
                ConformationDlgClick.YES -> {
                    branchResponse.id?.let {  branchViewModel.deleteBranch(it) }
                }
                ConformationDlgClick.NO -> {

                }
            }
        }
        conformationDialog.show(supportFragmentManager, CONFORMATION_DIALOG)
    }

    override fun onBackPress() {
       finish()
    }


    private fun loadMoreItems() {
        isPaginationLoading = true
        isInitLoading = false
        CURRENT_PAGES += 1
        branchViewModel.getBranchList(
            CURRENT_PAGES,
            10
        )
    }

    private fun mAddUpdateBranchDialog(branchId:String,branchName:String,branchLocation:String,isUpdate:Boolean) {
        val conformationDialog = BranchAddUpdateDialog.newInstance(
            title = if(isUpdate) "Update Branch" else "Add Branch",
            isUpdate = isUpdate,
            branchId = branchId,
            branchName = branchName,
            branchLocation = branchLocation
        )
        conformationDialog.onClick = { it,id,name,location ->
            when (it) {
                ConformationDlgClick.YES -> {
                    if(isUpdate){
                        val branchRequest = BranchRequest()
                        branchRequest.branchName = name
                        branchRequest.branchLocation = location
                        branchViewModel.updateBranch(id,branchRequest)
                    }else{
                        val branchRequest = BranchRequest()
                        branchRequest.branchName = name
                        branchRequest.branchLocation = location
                        branchViewModel.createBranch(branchRequest)
                    }
                }
                ConformationDlgClick.NO -> {

                }
            }
        }
        conformationDialog.show(supportFragmentManager, CONFORMATION_DIALOG)
    }

    private fun loadInitItem() {
        isPaginationLoading = false
        isInitLoading = true
        CURRENT_PAGES = 1
        branchViewModel.getBranchList(
            CURRENT_PAGES,
            10
        )
    }
}