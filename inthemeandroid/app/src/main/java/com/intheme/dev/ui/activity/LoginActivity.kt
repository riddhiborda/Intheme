package com.intheme.dev.ui.activity

import android.util.Patterns
import androidx.activity.viewModels
import com.intheme.dev.R
import com.intheme.dev.base.BaseActivity
import com.intheme.dev.data.model.request.LoginRequest
import com.intheme.dev.databinding.ActivityLoginBinding
import com.intheme.dev.utils.ApiState
import com.intheme.dev.utils.Constants.Companion.ARG_EMAIL
import com.intheme.dev.utils.startActivity
import com.intheme.dev.utils.errorHandling
import com.intheme.dev.utils.getStr
import com.intheme.dev.utils.gone
import com.intheme.dev.utils.launchWhenStarted
import com.intheme.dev.utils.showSuccessToast
import com.intheme.dev.utils.text
import com.intheme.dev.utils.visible
import com.intheme.dev.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding>(ActivityLoginBinding::inflate) {
    private val authViewModel: AuthViewModel by viewModels()

    enum class VALIDATION_LOGIN {
        EMAIL_ID
    }

    override fun initView() {

    }

    override fun setupObservers() {
        launchWhenStarted {
            authViewModel.mLoginStateFlow.collect {
                when (it) {
                    is ApiState.Loading -> {
                        showProgressApiCall(isStart=true)
                    }

                    is ApiState.Failure -> {
                        showProgressApiCall(isStart=false)
                        errorHandling(logout = {

                        },it.isNetworkError, it.msg)
                    }

                    is ApiState.Success -> {
                        showProgressApiCall(isStart=false)
                        it.data.message.showSuccessToast(this@LoginActivity)
                        startActivity<OTPVerificationActivity>{
                            this.putExtra(ARG_EMAIL,binding.edMobileNo.text.toString())
                        }
                    }
                    else -> {

                    }
                }
            }
        }
    }

    fun showProgressApiCall(isStart:Boolean){
        if(isStart){
            binding.pbLogin.visible()
            binding.btnLogin.text("")
        }else{
            binding.pbLogin.gone()
            binding.btnLogin.text(getStr(R.string.btn_login))
        }

    }

    override fun setOnClicks() {
        binding.btnLogin.setOnClickListener {
            if (binding.edMobileNo.text.toString().isEmpty()) {
                showValidation(VALIDATION_LOGIN.EMAIL_ID,getString(R.string.validation_email_id))
            }else if (!Patterns.EMAIL_ADDRESS.matcher(binding.edMobileNo.text.toString()).matches()) {
                showValidation(VALIDATION_LOGIN.EMAIL_ID,getString(R.string.validation_valid_email_id))
            } else {
                val loginRequest = LoginRequest()
                loginRequest.email = binding.edMobileNo.text.toString()
                binding.tilEmail.isErrorEnabled = false
                binding.tilEmail.errorIconDrawable = null
                authViewModel.postLogin(loginRequest)
            }
        }
    }

    private fun showValidation(validateType: VALIDATION_LOGIN?, msg: String?) {
        binding.tilEmail.isErrorEnabled = false
        binding.tilEmail.errorIconDrawable = null
        if(validateType == VALIDATION_LOGIN.EMAIL_ID){
            binding.tilEmail.isErrorEnabled = true
            binding.tilEmail.error = msg
        }
    }

    override fun onBackPress() {

    }

}