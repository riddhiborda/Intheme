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
import com.intheme.dev.databinding.DlgBranchBottomBinding
import com.intheme.dev.ui.activity.LoginActivity
import com.intheme.dev.ui.adaptor.BranchSelectAdaptor
import com.intheme.dev.utils.ApiState
import com.intheme.dev.utils.PaginationScrollListener
import com.intheme.dev.utils.errorHandling
import com.intheme.dev.utils.gone
import com.intheme.dev.utils.launchWhenStarted
import com.intheme.dev.utils.loge
import com.intheme.dev.utils.reqAct
import com.intheme.dev.utils.startActivity
import com.intheme.dev.utils.visible
import com.intheme.dev.viewmodel.BranchViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BranchSelectDialog : BottomSheetDialogFragment() {
    private lateinit var binding: DlgBranchBottomBinding
    private lateinit var branchSelectAdaptor: BranchSelectAdaptor

    var branchSelect :((branch: BranchResponse, pos:Int)->Unit)?=null

    private val branchViewModel: BranchViewModel by viewModels()

    private var isPaginateLoading = false
    private var CURRENT_PAGES = 0
    private var TOTAL_PAGES = 0
    private var IS_LAST_PAGE = false
    private var isPaginationLoading = false

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), theme).apply {
            setOnShowListener { dialog ->
                val bottomSheet = (dialog as BottomSheetDialog).findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
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
        binding = DlgBranchBottomBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initLocationListApiCall()
        setupObservers()
    }

    private fun initLocationListApiCall(){
        CURRENT_PAGES = 1
        IS_LAST_PAGE = false
        isPaginateLoading = false
        if(branchSelectAdaptor.getList().size>0){
            branchSelectAdaptor.clearArrayList()
        }
        branchViewModel.getBranchList(currentPage = CURRENT_PAGES, limit = 10)
    }



    @Suppress("DEPRECATION")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.setOnKeyListener { _, keyCode, _ ->
            keyCode == KeyEvent.KEYCODE_BACK
        }
    }


    private fun initViews() {
        setLocationAdaptor()
        binding.btnBack.setOnClickListener {
            dismiss()
        }
    }

    private fun setLocationAdaptor(){
        branchSelectAdaptor = BranchSelectAdaptor()
        branchSelectAdaptor.citySelect ={ cityItemModel, pos ->
            branchSelect?.invoke(cityItemModel,pos)
            dismiss()
        }
        val layoutManager = LinearLayoutManager(requireActivity())
        binding.rvBranchList.also {
            it.adapter = branchSelectAdaptor
            it.layoutManager = layoutManager
            it.itemAnimator = DefaultItemAnimator()
            it.addOnScrollListener(PaginationScrollListener(layoutManager) {
                binding.rvBranchList.post {
                    CURRENT_PAGES.toString().loge("CurrentPage--> C")
                    TOTAL_PAGES.toString().loge("CurrentPage--> L")
                    if((CURRENT_PAGES <= TOTAL_PAGES) && !isPaginateLoading){
                        isPaginateLoading = true
                        loadMoreItems()
                    }
                }
            })
        }
    }

    private fun loadMoreItems() {
        CURRENT_PAGES += 1
        branchViewModel.getBranchList(currentPage = CURRENT_PAGES, limit = 10)
    }

    private fun setupObservers() {
        launchWhenStarted {
            branchViewModel.mGetBranchListStateFlow.collect {
                when (it) {
                    is ApiState.Loading -> {
                        if(isPaginationLoading){
                            binding.pbBottom.visible()
                            binding.rvBranchList.gone()
                            binding.tvNoDataFound.gone()
                            binding.pbLoader.gone()
                        }else{
                            binding.pbBottom.gone()
                            binding.rvBranchList.gone()
                            binding.tvNoDataFound.gone()
                            binding.pbLoader.visible()
                        }
                    }

                    is ApiState.Failure -> {
                        binding.pbBottom.gone()
                        binding.rvBranchList.gone()
                        binding.tvNoDataFound.visible()
                        binding.pbLoader.gone()
                        isPaginationLoading = false
                        reqAct().errorHandling(logout = {
                            branchViewModel.setLogout()
                            reqAct().startActivity<LoginActivity> {  }
                            reqAct().finish()
                        },it.isNetworkError, it.msg)
                    }

                    is ApiState.Success -> {
                        binding.pbBottom.gone()
                        binding.pbLoader.gone()
                        isPaginationLoading = false
                        TOTAL_PAGES=it.data.data.lastPage?:0
                        if(it.data.data.data.isNullOrEmpty()){
                            binding.tvNoDataFound.visible()
                            binding.rvBranchList.gone()
                        }else{
                            branchSelectAdaptor.addData(it.data.data.data)
                            binding.tvNoDataFound.gone()
                            binding.rvBranchList.visible()
                        }
                    }
                    else -> {

                    }
                }
            }
        }
    }
}