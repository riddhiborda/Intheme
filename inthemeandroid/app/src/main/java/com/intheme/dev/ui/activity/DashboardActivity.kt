package com.intheme.dev.ui.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.intheme.dev.R
import com.intheme.dev.base.BaseActivity
import com.intheme.dev.databinding.ActivityDashboardBinding
import com.intheme.dev.databinding.ActivityLoginBinding
import com.intheme.dev.utils.load
import com.intheme.dev.utils.startActivity
import com.intheme.dev.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardActivity : BaseActivity<ActivityDashboardBinding>(ActivityDashboardBinding::inflate) {

    private val userViewModel: UserViewModel by viewModels()


    override fun initView() {

    }

    override fun setupObservers() {

    }

    override fun setOnClicks() {
       binding.cvBranchAdmin.setOnClickListener {
           startActivity<BranchAdminActivity>()
       }

       binding.cvBranch.setOnClickListener {
           startActivity<BranchListActivity>()
       }

       binding.ivUser.setOnClickListener {
           startActivity<UpdateProfileActivity>()
       }

       binding.cvServiceMan.setOnClickListener {
           startActivity<ServiceManActivity>()
       }

       binding.cvCustomer.setOnClickListener {
           startActivity<CustomerListActivity>()
       }
    }

    override fun onBackPress() {

    }

    override fun onResume() {
        super.onResume()
        binding.ivUser.load(this@DashboardActivity,userViewModel.getUserDetail().image?:"")
    }

}