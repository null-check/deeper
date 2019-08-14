package com.arjun.deeper

import com.arjun.deeper.interfaces.LevelControllerCallback
import com.arjun.deeper.singletons.GameStateSingleton
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
        if (GameStateSingleton.getInstance().gameState != GameStateSingleton.GameState.TUTORIAL) // Tutorial already sets predefined child counts
            generateChildren()
    }

    /**
     * Everytime level (same as score) reaches LEVEL_STEPS, difficulty is incremented
     * Everytime difficulty reaches DIFFICULTY_STEPS, stage is incremented
     * For every stage attained, game behaviour changes (gets more difficult)
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
        val distributionArray = IntArray(9)
        var highestValue = 0

        for (index in 0..8) {
            val childCount = CommonLib.getRandomIntBetween(Math.min(1 + difficulty / 2, 5), Math.max(7, 9 - difficulty / 2))
            distributionArray[index] = childCount
            if (childCount > highestValue)
                highestValue = childCount
        }
        levelControllerCallback?.setChildren(removeDuplicateHighestValues(distributionArray, highestValue))
    }

    // Cheap way to avoid duplicate correct choices for now
    private fun removeDuplicateHighestValues(array: IntArray, highestValue: Int): IntArray {
        var valueEncountered = false
        for (index in 0 until array.size) {
            val value = array[index]
            if (value == highestValue) {
                if (valueEncountered)
                    array[index] = value - 1
                else
                    valueEncountered = true
            }
        }
        return array
    }

    fun attachView(levelControllerCallback: LevelControllerCallback) {
        this.levelControllerCallback = levelControllerCallback
    }
}