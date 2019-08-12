package com.arjun.deeper.views.customviews

import android.content.Context
import android.support.annotation.AttrRes
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import carbon.widget.FrameLayout
import com.arjun.deeper.R
import com.arjun.deeper.utils.UiUtils
import kotlinx.android.synthetic.main.view_button_menu.view.*

class MenuButtonView : FrameLayout {

    private var buttonTextView: TextView? = null

    private var buttonText: String? = null
    private var invertColors: Boolean = false

    constructor (context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, @AttrRes defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        fetchAttributes(attrs)
        initialise()
    }

    private fun fetchAttributes(attrs: AttributeSet?) {

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.MenuButtonView)

        if (attributes != null) {
            buttonText = attributes.getString(R.styleable.MenuButtonView_button_text)
            invertColors = attributes.getBoolean(R.styleable.MenuButtonView_invert_colors, false)
            attributes.recycle()
        }
    }

    private fun initialise() {
        View.inflate(context, R.layout.view_button_menu, this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        buttonTextView = button_text
        buttonText?.let { buttonTextView?.text = it }
        if (invertColors) {
            root_view.background = resources.getDrawable(R.drawable.white_border_rounded_transparent_rectangle)
            buttonTextView?.setTextColor(UiUtils.getColor(R.color.white))
        }
    }

    fun setText(text: String) {
        buttonTextView?.text = text
    }
}