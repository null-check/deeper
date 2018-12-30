package com.arjun.deeper

import com.arjun.deeper.interfaces.LevelControllerCallback
import com.arjun.deeper.utils.CommonLib
import com.arjun.deeper.views.customviews.Cell

class LevelController {

    private val LEVEL_STEPS = 3
    private val DIFFICULTY_STEPS = 3

    private var level: Int = 0
    private var difficulty: Int = 0
    private var stage: Int = 0

    private var levelControllerCallback: LevelControllerCallback? = null

    fun reset() {
        level = 0
        difficulty = 0
        stage = 0
        levelControllerCallback?.resetAttributes()
        generateChildren()
    }

    /**
     * Everytime level reaches LEVEL_STEPS, difficulty is incremented
     * Everytime difficulty reaches DIFFICULTY_STEPS, stage is incremented
     * For every stage attainted, game behaviour changes (gets more difficult)
     *
     * Stage 5 is disabled as setting to gone makes visible views abruptly expand after remaining views have finished animating (and are set to gone)
     */
    fun increaseLevel() {
        if (++level % LEVEL_STEPS == 0) {
            if (++difficulty % DIFFICULTY_STEPS == 0) {
                when (++stage) {
                    2 -> levelControllerCallback?.setRotatedCells(true)
                    3 -> levelControllerCallback?.setChooseRandomSubcell(true)
                    4 -> levelControllerCallback?.setSubcellShape(Cell.SubcellShape.RANDOM)
//                    5 -> levelControllerCallback?.setSubcellHideMode(View.GONE)
                }
            }
        }
        generateChildren()
    }

    private fun generateChildren() {
        val childCounts = IntArray(9)
        for (i in 0..8) {
            val childCount = CommonLib.getRandomIntBetween(Math.min(1 + difficulty / 2, 5), Math.max(7, 9 - difficulty / 2))
            childCounts[i] = childCount
        }
        levelControllerCallback?.setChildren(childCounts)
    }

    fun attachView(levelControllerCallback: LevelControllerCallback) {
        this.levelControllerCallback = levelControllerCallback
    }
}