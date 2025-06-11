package com.intheme.dev.ui.activity

import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.intheme.dev.R
import com.intheme.dev.base.BaseActivity
import com.intheme.dev.data.model.response.User
import com.intheme.dev.databinding.ActivityBranchAdminBinding
import com.intheme.dev.ui.adaptor.BranchAdminListAdaptor
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
import dagger.hilt.android.AndroidEntryPoint
import kotlin.properties.Delegates

@AndroidEntryPoint
class BranchAdminActivity : BaseActivity<ActivityBranchAdminBinding>(ActivityBranchAdminBinding::inflate) {

    private var CURRENT_PAGES by Delegates.notNull<Int>()
    private var TOTAL_PAGES by Delegates.notNull<Int>()
    private var isPaginationLoading = false
    private var isInitLoading = false
    private val branchAdminViewModel: BranchAdminViewModel by viewModels()

    private val branchAdminListAdaptor: BranchAdminListAdaptor by lazy {
        BranchAdminListAdaptor(this@BranchAdminActivity)
    }

    private val progressDialog: ProgressDialog by lazy {
        ProgressDialog()
    }

    override fun initView() {
        val layoutManager = LinearLayoutManager(this@BranchAdminActivity)
        binding.rvListItem.also {
            it.adapter = branchAdminListAdaptor
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
            branchAdminViewModel.mGetBranchAdminListStateFlow.collect {
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
                            branchAdminViewModel.setLogout()
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
                            branchAdminListAdaptor.addData(it.data.data.data)
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
                    EVENT_TYPE.ADD_BRANCH_ADMIN -> {
                        branchAdminListAdaptor.clearArrayList()
                        loadInitItem()
                    }
                    EVENT_TYPE.UPDATE_BRANCH_ADMIN -> {
                        branchAdminListAdaptor.clearArrayList()
                        loadInitItem()
                    }

                    else -> {}
                }
            }
        }

        launchWhenStarted {
            branchAdminViewModel.mDeleteBranchAdminStateFlow.collect {
                when (it) {
                    is ApiState.Loading -> {
                        progressDialog.showProgress(this@BranchAdminActivity)
                    }

                    is ApiState.Failure -> {
                        progressDialog.dismissProgress()
                        errorHandling(logout = {
                            branchAdminViewModel.setLogout()
                            startActivity<LoginActivity> {  }
                            finish()
                            finishAffinity()
                        },it.isNetworkError, it.msg)
                    }

                    is ApiState.Success -> {
                        progressDialog.dismissProgress()
                        it.data.message.showSuccessToast(this@BranchAdminActivity)
                        branchAdminListAdaptor.clearArrayList()
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
             startActivity<AddBranchAdminActivity>()
        }

        branchAdminListAdaptor.onEditClick = { branchAdminResponse, pos ->
            startActivity<AddBranchAdminActivity>{
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

        branchAdminListAdaptor.onDeleteClick = { user, pos ->
            mDeleteBranchAdminDialog(user)
        }
    }

    override fun onBackPress() {
        finish()
    }

    private fun mDeleteBranchAdminDialog(user: User?) {
        val conformationDialog = ConformationDialog.newInstance(
            title = getString(R.string.delete_branch_admin),
            message = getString(R.string.delete_branch_admin_detail)
        )
        conformationDialog.onClick = { it ->
            when (it) {
                ConformationDlgClick.YES -> {
                    user?.id?.let {  branchAdminViewModel.deleteBranchAdmin(it) }
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
        branchAdminViewModel.getBranchAdminList(
            CURRENT_PAGES,
            10
        )
    }

    private fun loadInitItem() {
        isPaginationLoading = false
        isInitLoading = true
        CURRENT_PAGES = 1
        branchAdminViewModel.getBranchAdminList(
            CURRENT_PAGES,
            10
        )
    }

}