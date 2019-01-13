package com.arjun.deeper.views.customviews

import android.content.Context
import android.support.annotation.AttrRes
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import carbon.widget.FrameLayout
import com.arjun.deeper.LevelController
import com.arjun.deeper.R
import com.arjun.deeper.interfaces.GameGridCallback
import com.arjun.deeper.interfaces.LevelControllerCallback
import com.arjun.deeper.utils.CommonLib
import kotlinx.android.synthetic.main.view_game_grid.view.*
import java.util.*

class GameGridView : FrameLayout, LevelControllerCallback {

    private lateinit var children: List<Cell>
    private lateinit var cellButton: TextView

    private var enableRotatedCells: Boolean = false
    private var maxCount: Int = 0

    private lateinit var levelController: LevelController

    constructor (context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, @AttrRes defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initialise()
    }

    private fun initialise() {
        View.inflate(context, R.layout.view_game_grid, this)
        levelController = LevelController()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        children = ArrayList(Arrays.asList<Cell>(cell_1, cell_2, cell_3, cell_4, cell_5, cell_6, cell_7, cell_8, cell_9))
        cellButton = cell_button
        levelController.attachView(this)
    }

    fun setGameGridCallback(gameGridCallback: GameGridCallback) {

        var position = 0
        for (child in children) {
            child.setOnClickListener {
                gameGridCallback.cellClicked(child.childCellCount, maxCount, position)
                position++
            }
        }

        cellButton.setOnClickListener { gameGridCallback.cellButtonClicked() }
    }

    override fun setChildren(childCounts: IntArray) {

        maxCount = 0
        for (i in 0 until children.size) {
            children[i].showChildren(childCounts[i])
            if (childCounts[i] > maxCount) {
                maxCount = childCounts[i]
            }
            if (enableRotatedCells) children[i].rotation = (if (CommonLib.getRandomBoolean()) 0 else 90).toFloat()
        }
    }

    override fun setRotatedCells(enable: Boolean) {
        enableRotatedCells = enable
    }

    override fun setChooseRandomSubcell(flag: Boolean) {
        for (child in children) {
            child.setChooseRandomSubcell(flag)
        }
    }

    override fun setSubcellShape(shape: Cell.SubcellShape) {
        for (child in children) {
            child.setSubcellShape(shape)
        }
    }

    override fun setSubcellHideMode(subcellHideMode: Int) {
        for (child in children) {
            child.setSubcellHideMode(subcellHideMode)
        }
    }

    override fun resetAttributes() {
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

    fun getLevelController(): LevelController {
        return levelController
    }

    fun setShowCount(showCount: Boolean) {
        for (child in children)
            child.setShowCount(showCount)
    }
}