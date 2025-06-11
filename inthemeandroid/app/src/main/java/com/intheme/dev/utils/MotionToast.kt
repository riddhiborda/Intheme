package com.intheme.dev.utils

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.Typeface
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.intheme.dev.R
import com.intheme.dev.databinding.FullColorToastBinding



@Suppress("DEPRECATION")
class MotionToast {
    companion object {

        const val SHORT_DURATION = 2000L // 2 seconds
        const val GRAVITY_BOTTOM = 80

        private lateinit var layoutInflater: LayoutInflater

        private var successToastColor: Int = R.color.success_color
        private var errorToastColor: Int = R.color.error_color
        private var warningToastColor: Int = R.color.warning_color
        private var infoToastColor: Int = R.color.info_color
        private var deleteToastColor: Int = R.color.delete_color

        // all color toast CTA
        fun createColorToast(
            context: Context,
            title: String? = null,
            message: String,
            style: MotionToastStyle,
            position: Int,
            duration: Long,
            font: Typeface?
        ) {
            layoutInflater = LayoutInflater.from(context)
            val binding = context.inflateBindingView<FullColorToastBinding>(R.layout.full_color_toast)
            val layout = binding.root
            when (style) {
                // Function for Toast Success
                MotionToastStyle.SUCCESS -> {
                    binding.colorToastImage.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.ic_check_green
                        )
                    )
                    DrawableCompat.setTint(
                        DrawableCompat.wrap(binding.colorToastImage.drawable),
                        ContextCompat.getColor(context, successToastColor)
                    )

                    // Pulse Animation for Icon
                    val pulseAnimation = AnimationUtils.loadAnimation(context, R.anim.pulse)
                    binding.colorToastImage.startAnimation(pulseAnimation)

                    // round background color
                    setBackgroundAndFilter(
                        R.drawable.toast_round_background,
                        successToastColor, layout, context
                    )

                    // Setting up the color for title & Message text
                    binding.colorToastText.setTextColor(Color.WHITE)
                    binding.colorToastText.text =
                        if (title.isNullOrBlank()) MotionToastStyle.SUCCESS.getName() else title

                    setDescriptionDetails(
                        font,
                        message,
                        binding.colorToastDescription
                    )

                    // init toast
                    val toast = Toast(context.applicationContext)
                    startTimer(duration, toast)

                    // Setting Toast Gravity
                    setGravity(position, toast)

                    // Setting layout to toast
                    toast.view = layout
                    toast.show()
                }
                // CTA for Toast Error
                MotionToastStyle.ERROR -> {
                    binding.colorToastImage.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.ic_error_
                        )
                    )
                    DrawableCompat.setTint(
                        DrawableCompat.wrap(binding.colorToastImage.drawable),
                        ContextCompat.getColor(context, errorToastColor)
                    )
                    // Pulse Animation for Icon
                    val pulseAnimation = AnimationUtils.loadAnimation(context, R.anim.pulse)
                    binding.colorToastImage.startAnimation(pulseAnimation)

                    // round background color
                    setBackgroundAndFilter(
                        R.drawable.toast_round_background,
                        errorToastColor, layout, context
                    )

                    // Setting up the color for title & Message text
                    binding.colorToastText.setTextColor(Color.WHITE)
                    binding.colorToastText.text =
                        if (title.isNullOrBlank()) MotionToastStyle.ERROR.getName() else title

                    setDescriptionDetails(
                        font,
                        message,
                        binding.colorToastDescription
                    )

                    // init toast
                    val toast = Toast(context.applicationContext)
                    startTimer(duration, toast)

                    // Setting Toast Gravity
                    setGravity(position, toast)

                    // Setting layout to toast
                    toast.view = layout
                    toast.show()
                }
                // CTA for Toast Warning
                MotionToastStyle.WARNING -> {
                    binding.colorToastImage.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.ic_warning_yellow
                        )
                    )
                    DrawableCompat.setTint(
                        DrawableCompat.wrap(binding.colorToastImage.drawable),
                        ContextCompat.getColor(context, warningToastColor)
                    )
                    // Pulse Animation for Icon
                    val pulseAnimation = AnimationUtils.loadAnimation(context, R.anim.pulse)
                    binding.colorToastImage.startAnimation(pulseAnimation)

                    // round background color
                    setBackgroundAndFilter(
                        R.drawable.toast_round_background,
                        warningToastColor, layout, context
                    )

                    // Setting up the color for title & Message text
                    binding.colorToastText.setTextColor(Color.WHITE)
                    binding.colorToastText.text =
                        if (title.isNullOrBlank()) MotionToastStyle.WARNING.getName() else title

                    setDescriptionDetails(
                        font,
                        message,
                        binding.colorToastDescription
                    )

                    // init toast
                    val toast = Toast(context.applicationContext)
                    startTimer(duration, toast)

                    // Setting Toast Gravity
                    setGravity(position, toast)

                    // Setting layout to toast
                    toast.view = layout
                    toast.show()
                }
                // CTA for Toast Info
                MotionToastStyle.INFO -> {
                    binding.colorToastImage.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.ic_info_blue
                        )
                    )
                    DrawableCompat.setTint(
                        DrawableCompat.wrap(binding.colorToastImage.drawable),
                        ContextCompat.getColor(context, infoToastColor)
                    )
                    // Pulse Animation for Icon
                    val pulseAnimation = AnimationUtils.loadAnimation(context, R.anim.pulse)
                    binding.colorToastImage.startAnimation(pulseAnimation)

                    // round background color
                    setBackgroundAndFilter(
                        R.drawable.toast_round_background,
                        infoToastColor, layout, context
                    )

                    // Setting up the color for title & Message text
                    binding.colorToastText.setTextColor(Color.WHITE)
                    binding.colorToastText.text =
                        if (title.isNullOrBlank()) MotionToastStyle.INFO.getName() else title

                    setDescriptionDetails(
                        font,
                        message,
                        binding.colorToastDescription
                    )

                    // init toast
                    val toast = Toast(context.applicationContext)
                    startTimer(duration, toast)

                    // Setting Toast Gravity
                    setGravity(position, toast)

                    // Setting layout to toast
                    toast.view = layout
                    toast.show()
                }
                // CTA for Toast Delete
                MotionToastStyle.DELETE -> {
                    binding.colorToastImage.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.ic_delete_
                        )
                    )
                    DrawableCompat.setTint(
                        DrawableCompat.wrap(binding.colorToastImage.drawable),
                        ContextCompat.getColor(context, deleteToastColor)
                    )
                    // Pulse Animation for Icon
                    val pulseAnimation = AnimationUtils.loadAnimation(context, R.anim.pulse)
                    binding.colorToastImage.startAnimation(pulseAnimation)

                    // round background color
                    setBackgroundAndFilter(
                        R.drawable.toast_round_background,
                        deleteToastColor, layout, context
                    )

                    // Setting up the color for title & Message text
                    binding.colorToastText.setTextColor(Color.WHITE)
                    binding.colorToastText.text =
                        if (title.isNullOrBlank()) MotionToastStyle.DELETE.getName() else title

                    setDescriptionDetails(
                        font,
                        message,
                        binding.colorToastDescription
                    )

                    // init toast
                    val toast = Toast(context.applicationContext)
                    startTimer(duration, toast)

                    // Setting Toast Gravity
                    setGravity(position, toast)

                    // Setting layout to toast
                    toast.view = layout
                    toast.show()

                }
                // CTA for Toast No Internet
                MotionToastStyle.NO_INTERNET -> {
                    binding.colorToastImage.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.ic_no_internet
                        )
                    )
                    DrawableCompat.setTint(
                        DrawableCompat.wrap(binding.colorToastImage.drawable),
                        ContextCompat.getColor(context, warningToastColor)
                    )
                    // Pulse Animation for Icon
                    val pulseAnimation = AnimationUtils.loadAnimation(context, R.anim.pulse)
                    binding.colorToastImage.startAnimation(pulseAnimation)

                    // round background color
                    setBackgroundAndFilter(
                        R.drawable.toast_round_background,
                        warningToastColor, layout, context
                    )

                    // Setting up the color for title & Message text
                    binding.colorToastText.setTextColor(Color.WHITE)
                    binding.colorToastText.text =
                        if (title.isNullOrBlank()) MotionToastStyle.NO_INTERNET.getName() else title

                    setDescriptionDetails(
                        font,
                        message,
                        binding.colorToastDescription
                    )

                    // init toast
                    val toast = Toast(context.applicationContext)
                    startTimer(duration, toast)

                    // Setting Toast Gravity
                    setGravity(position, toast)

                    // Setting layout to toast
                    toast.view = layout
                    toast.show()
                }
            }
        }

        private fun startTimer(duration: Long, toast: Toast) {
            val timer = object : CountDownTimer(duration, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    // do nothing
                }

                override fun onFinish() {
                    toast.cancel()
                }
            }
            timer.start()
        }


        private fun setDescriptionDetails(
            font: Typeface?,
            message: String,
            layout: TextView
        ) {
            layout.setTextColor(Color.WHITE)
            layout.text = message
            font?.let {
                layout.typeface = font
            }
        }

        private fun setGravity(position: Int, toast: Toast) {
            if (position == GRAVITY_BOTTOM) {
                toast.setGravity(position, 0, 100)
            } else {
                toast.setGravity(position, 0, 0)
            }
        }

        private fun setBackgroundAndFilter(
            @DrawableRes background: Int,
            @ColorRes colorFilter: Int,
            layout: View,
            context: Context
        ) {
            val drawable = ContextCompat.getDrawable(context, background)
            drawable?.colorFilter = PorterDuffColorFilter(
                ContextCompat.getColor(context, colorFilter),
                PorterDuff.Mode.MULTIPLY
            )
            layout.background = drawable
        }
    }
}