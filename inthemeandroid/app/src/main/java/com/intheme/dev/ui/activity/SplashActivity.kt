package com.intheme.dev.ui.activity

import android.annotation.SuppressLint
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.intheme.dev.R
import com.intheme.dev.base.BaseActivity
import com.intheme.dev.databinding.ActivitySplashBinding
import com.intheme.dev.utils.launchWhenStarted
import com.intheme.dev.utils.startActivity
import com.intheme.dev.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : BaseActivity<ActivitySplashBinding>(ActivitySplashBinding::inflate) {
    private val authViewModel: AuthViewModel by viewModels()
    override fun initView() {
        Glide.with(this@SplashActivity)
            .load(R.drawable.intheme)
            .placeholder(R.drawable.intheme)
            .into(binding.gifLog)
    }
    override fun setupObservers() {
       launchWhenStarted {
           delay(5000)
           if(authViewModel.isLogin()){
               startActivity<DashboardActivity>()
           }else{
               startActivity<LoginActivity>()
           }

       }
    }
    override fun setOnClicks() {

    }
    override fun onBackPress() {

    }
}