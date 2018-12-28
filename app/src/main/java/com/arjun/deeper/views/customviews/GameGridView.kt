package com.arjun.deeper.views.customviews

import android.content.Context
import android.support.annotation.AttrRes
import android.util.AttributeSet
import android.view.View
import carbon.widget.FrameLayout
import android.widget.TextView
import com.arjun.deeper.R
import com.arjun.deeper.interfaces.GameGridCallback
import com.arjun.deeper.utils.CommonLib
import kotlinx.android.synthetic.main.view_game_grid.view.*
import java.util.*

class GameGridView : FrameLayout {

    private lateinit var children: List<Cell>
    private lateinit var cellButton: TextView

    private var enableRotatedCells: Boolean = false
    private var maxCount: Int = 0

    constructor (context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, @AttrRes defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initialise()
    }

    private fun initialise() {
        View.inflate(context, R.layout.view_game_grid, this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        children = ArrayList(Arrays.asList<Cell>(cell_1, cell_2, cell_3, cell_4, cell_5, cell_6, cell_7, cell_8, cell_9))
        cellButton = cell_button
    }

    fun setGameGridCallback(gameGridCallback: GameGridCallback) {

        var position = 0
        for (child in children) {
            child.setOnClickListener {
                gameGridCallback.cellClicked(child.visibleChildCount, maxCount, position)
                position++
            }
        }

        cellButton.setOnClickListener { gameGridCallback.cellButtonClicked() }
    }

    fun randomizeViews(difficulty: Int) {
        maxCount = 0

        for (child in children) {
            val childCount = CommonLib.getRandomIntBetween(Math.min(1 + difficulty / 2, 5), Math.max(7, 9 - difficulty / 2))
//            val childCount = CommonLib.getRandomIntBetween(1, 10)
            if (childCount > maxCount) {
                maxCount = childCount
            }
            child.showChildren(childCount)
            if (enableRotatedCells) child.rotation = (if (CommonLib.getRandomBoolean()) 0 else 90).toFloat()
        }
    }

    fun setChooseRandomSubcell(flag: Boolean) {
        for (child in children) {
            child.setChooseRandomSubcell(flag)
        }
    }

    fun setSubcellShape(shape: Cell.SubcellShape) {
        for (child in children) {
            child.setSubcellShape(shape)
        }
    }

    fun resetAttributes() {
        for (child in children) {
            child.rotation = 0f
            child.resetAttributes()
        }
    }

    fun setCellButtonVisibility(visibility: Int) {
        cellButton.visibility = visibility
    }

    fun setCellButtonText(text: String) {
        cellButton.text = text
    }
}