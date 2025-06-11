package com.intheme.dev.ui.activity

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.os.CountDownTimer
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.KeyEvent
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.doOnTextChanged
import com.google.firebase.messaging.FirebaseMessaging
import com.intheme.dev.R
import com.intheme.dev.base.BaseActivity
import com.intheme.dev.data.model.request.LoginRequest
import com.intheme.dev.data.model.request.OtpVerificationRequest
import com.intheme.dev.databinding.ActivityOtpverificationBinding
import com.intheme.dev.utils.ApiState
import com.intheme.dev.utils.Constants
import com.intheme.dev.utils.disable
import com.intheme.dev.utils.enable
import com.intheme.dev.utils.errorHandling
import com.intheme.dev.utils.getAndroidId
import com.intheme.dev.utils.getStr
import com.intheme.dev.utils.gone
import com.intheme.dev.utils.launchWhenStarted
import com.intheme.dev.utils.showSuccessToast
import com.intheme.dev.utils.startActivity
import com.intheme.dev.utils.text
import com.intheme.dev.utils.visible
import com.intheme.dev.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OTPVerificationActivity : BaseActivity<ActivityOtpverificationBinding>(ActivityOtpverificationBinding::inflate) {
    private val authViewModel: AuthViewModel by viewModels()
    private  var countDownTimer: CountDownTimer ?=null
    private var strToken =""
    enum class VALIDATION_OTP {
        OTP,API_CALL
    }
    override fun initView() {
        setupOtpInputs()
        startOTPTimer(isStop = false)
        FirebaseMessaging.getInstance().token.addOnCompleteListener { token ->
            if (!token.isSuccessful) {
                return@addOnCompleteListener
            }
            strToken = token.result
        }
    }

    override fun setupObservers() {
        launchWhenStarted {
            authViewModel.mResendOTPStateFlow.collect {
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
                        it.data.message.showSuccessToast(this@OTPVerificationActivity)
                        startOTPTimer(isStop = false)
                    }
                    else -> {

                    }
                }
            }
        }
        launchWhenStarted  {
            authViewModel.mVerificationOTPStateFlow.collect {
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
                        it.data.message.showSuccessToast(this@OTPVerificationActivity)
                        it.data.data.user?.let {
                            authViewModel.setUserDetail(it)
                        }
                        it.data.data.tokens?.let {
                            authViewModel.setToken(it)
                        }
                        authViewModel.setIsLogin(true)
                        startActivity<DashboardActivity>()
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
        binding.btnVerify.setOnClickListener{
            if(getVerificationCode().isEmpty()){
                showValidation(VALIDATION_OTP.OTP,getString(R.string.enter_valid_otp))
            }else{
                showValidation(VALIDATION_OTP.API_CALL,null)
            }
        }
    }

    override fun onBackPress() {
        finish()
    }

    private fun showValidation(validateType: VALIDATION_OTP?, msg: String?) {
        binding.tvValidationOTp.visibility = View.GONE
        when (validateType) {
            VALIDATION_OTP.OTP-> {
                binding.tvValidationOTp.text = msg
                binding.tvValidationOTp.visibility = View.VISIBLE
            }
            else->{
                val request = OtpVerificationRequest()
                request.email = intent.getStringExtra(Constants.ARG_EMAIL)
                request.otp = getVerificationCode()
                request.token = strToken
                request.deviceId = getAndroidId()
                authViewModel.postVerificationOTP(request)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setupOtpInputs() {
        val otpInputs = listOf(
            binding.edOtp1,
            binding.edOtp2,
            binding.edOtp3,
            binding.edOtp4,
            binding.edOtp5,
            binding.edOtp6
        )
        for (i in otpInputs.indices) {
            otpInputs[i].doOnTextChanged { text, _, _, _ ->
                if (text?.length == 1 && text.isNotEmpty()) {
                    if (i != otpInputs.size - 1) {
                        otpInputs[i + 1].requestFocus()
                    }
                }
            }
            otpInputs[i].setOnKeyListener { _, keyCode, event ->
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL) {
                    if ((otpInputs[i].text?.isEmpty() == true|| otpInputs[i].text?.isNotEmpty() == true) && i != 0) {
                        otpInputs[i].setText("")
                        otpInputs[i-1].requestFocus()
                    }
                }
                false
            }
        }

    }

    fun showProgressApiCall(isStart:Boolean){
        if(isStart){
            binding.pbLogin.visible()
            binding.btnVerify.text("")
            binding.btnVerify.disable()
            binding.tvOTPReceive.disable()
        }else{
            binding.pbLogin.gone()
            binding.tvOTPReceive.enable()
            binding.btnVerify.enable()
            binding.btnVerify.text(getStr(R.string.btn_login))
        }
    }

    private fun getVerificationCode(): String {
        val otp1 = binding.edOtp1.text.toString()
        val otp2 = binding.edOtp2.text.toString()
        val otp3 = binding.edOtp3.text.toString()
        val otp4 = binding.edOtp4.text.toString()
        val otp5 = binding.edOtp5.text.toString()
        val otp6 = binding.edOtp6.text.toString()
        val otp = otp1 + otp2 + otp3 + otp4 + otp5 + otp6
        return otp
    }

    private fun startOTPTimer(isStop:Boolean) {
        val timerDuration = 60000L // 1 minute in milliseconds
        val timerInterval = 1000L  // 1 second in milliseconds
        if(isStop){
            countDownTimer?.cancel()
            countDownTimer = null
        }else{
            countDownTimer?.cancel()
            countDownTimer = null
            countDownTimer =  object : CountDownTimer(timerDuration, timerInterval) {
                @SuppressLint("DefaultLocale")
                override fun onTick(millis: Long) {
                    val secondsRemaining = millis / 1000
                    val timer=String.format("%02d:%02d",secondsRemaining / 60, secondsRemaining % 60)
                    setRegisterText(true, timer)
                }
                override fun onFinish() {
                    setRegisterText(false, "")
                }
            }.start()
        }

    }

    @SuppressLint("SetTextI18n")
    fun setRegisterText(isTimer: Boolean, strTime: String) {
        if (isTimer) {
            binding.tvOTPReceive.text = "${getString(R.string.lbl_resend_code_in)} $strTime"
        } else {
            // Create the full text with placeholders
            val fullText = getString(R.string.lbl_didnt_get_code_resend_code)

            // Create a SpannableString for the whole text
            val spannableString = SpannableString(fullText)

            // Set color for "Don’t have account?"
            val grayColorSpan =
                ForegroundColorSpan(ResourcesCompat.getColor(resources, R.color.black, null))
            spannableString.setSpan(
                grayColorSpan,
                0,
                fullText.indexOf("Resend Code") - 1,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            // Set color and click ability for "Register"
            val redColorSpan =
                ForegroundColorSpan(ResourcesCompat.getColor(resources, R.color.app_primary, null))
            spannableString.setSpan(
                redColorSpan,
                fullText.indexOf("Resend Code"),
                fullText.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            // Set bold style for "Register"
            val boldSpan = StyleSpan(Typeface.BOLD)
            spannableString.setSpan(
                boldSpan,
                fullText.indexOf("Resend Code"),
                fullText.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            val clickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    val loginRequest = LoginRequest()
                    loginRequest.email = intent.getStringExtra(Constants.ARG_EMAIL)
                    authViewModel.postResendOTP(loginRequest)
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.color = Color.RED // Ensure color is applied
                    ds.isUnderlineText = false // Remove underline
                }
            }
            spannableString.setSpan(
                clickableSpan,
                fullText.indexOf("Resend Code"),
                fullText.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            // Set the SpannableString to the TextView
            binding.tvOTPReceive.text = spannableString
            binding.tvOTPReceive.movementMethod =
                android.text.method.LinkMovementMethod.getInstance()
            binding.tvOTPReceive.background = null
        }
    }

    override fun onStop() {
        super.onStop()
        startOTPTimer(isStop = true)
    }

}