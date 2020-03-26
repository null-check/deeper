package com.arjun.deeper.views.customviews

import android.content.Context
import androidx.annotation.AttrRes
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import com.arjun.deeper.utils.AnimationUtils
import com.arjun.deeper.utils.UiUtils

import kotlinx.android.synthetic.main.view_progress_bar.view.*

class ProgressBarView : FrameLayout {

    private lateinit var progressContainer: FrameLayout
    private lateinit var progressBarProgress: carbon.widget.FrameLayout
    private lateinit var progressBarBg: carbon.widget.FrameLayout

    private var progressAnimation: Animation? = null

    private var progressWidth: Int = 0

    constructor (context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, @AttrRes defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onFinishInflate() {
        super.onFinishInflate()
        progressContainer = progress_container
        progressBarProgress = progress_bar_progress
        progressBarBg = progress_bar_bg
    }

    fun setupView(percent: Float, animate: Boolean, containerColor: Int, progressColor: Int) {
        setProgress(percent, animate)
        setupColor(containerColor, progressColor)
    }

    private fun setupColor(containerColor: Int, progressColor: Int) {
        progressBarProgress.setBackgroundColor(progressColor)
        progressBarBg.setBackgroundColor(containerColor)
    }

    fun setProgress(percent: Float, animate: Boolean) {
        progressContainer.post {
            val maxWidth = progressContainer.width
            progressWidth = (maxWidth * percent / 100).toInt()

            if (animate) {
                if (progressAnimation != null) {
                    progressAnimation!!.cancel()
                }
                progressAnimation = AnimationUtils.expandWidth(progressBarProgress, 0, progressWidth, AnimationUtils.D_1500, LinearInterpolator(), object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation) {
                        progressBarProgress.visibility = View.VISIBLE
                    }

                    override fun onAnimationEnd(animation: Animation) {

                    }

                    override fun onAnimationRepeat(animation: Animation) {

                    }
                })
            } else if (progressBarProgress.layoutParams.width != progressWidth) {
                progressBarProgress.visibility = View.VISIBLE
                progressBarProgress.layoutParams.width = progressWidth
                progressBarProgress.requestLayout()
            }
        }
    }

    fun resetCurvedCorners() {
        progressBarProgress.setCornerRadius(UiUtils.convertDpToPx(3F))
        progressBarBg.setCornerRadius(UiUtils.convertDpToPx(3F))
    }

    fun setProgressBarColor(progressColor: Int) {
        progressBarProgress.setBackgroundColor(progressColor)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (progressAnimation != null) {
            progressAnimation!!.cancel()
            progressAnimation = null
            progressBarProgress.layoutParams.width = progressWidth
        }
    }
}
