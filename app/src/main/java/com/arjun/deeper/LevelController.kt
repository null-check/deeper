package com.arjun.deeper

import com.arjun.deeper.interfaces.LevelControllerCallback
import com.arjun.deeper.singletons.GameStateSingleton
import com.arjun.deeper.utils.CommonLib
import com.arjun.deeper.views.customviews.Cell

class LevelController {

    private val LEVELS_PER_STAGE = 10
    private val STAGES_PER_DIFFICULTY = 2

    private var level: Int = 0
    private var stage: Int = 0
    private var difficulty: Int = 0

    private var levelControllerCallback: LevelControllerCallback? = null

    fun reset() {
        level = 0
        stage = 0
        difficulty = 0
        levelControllerCallback?.resetAttributes()
        if (GameStateSingleton.getInstance().gameState != GameStateSingleton.GameState.TUTORIAL) // Tutorial already sets predefined child counts
            generateChildren()
    }

    /**
     * Every time level (same as score) reaches LEVEL_STEPS, stage is incremented
     * Every time stage reaches STAGE_STEPS, difficulty is incremented
     * For every difficulty attained, game behaviour changes
     *
     * Stage 5 is disabled for now as setting to gone makes visible views abruptly expand after remaining views have finished animating (and are set to gone)
     */
    fun increaseLevel() {
        if (++level % LEVELS_PER_STAGE == 0) {
            if (++stage % STAGES_PER_DIFFICULTY == 0) {
                when (++difficulty) {
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
        var possibleDuplicate = false

        for (index in 0..8) {
            val childCount = CommonLib.getRandomIntBetween((1 + stage / 2).coerceAtMost(5), (9 - stage / 2).coerceAtLeast(7))
            distributionArray[index] = childCount
            if (childCount > highestValue) {
                highestValue = childCount
                possibleDuplicate = false
            } else if (childCount == highestValue)
                possibleDuplicate = true
        }
        levelControllerCallback?.setChildren(if (possibleDuplicate) removeDuplicateHighestValues(distributionArray, highestValue) else distributionArray)
    }

    /**
     * Remove duplicate max values by decrementing all but one of them
     * @param array Array of numbers to check
     * @param highestValue Highest value in the array
     */
    private fun removeDuplicateHighestValues(array: IntArray, highestValue: Int): IntArray {
        var valueEncountered = false
        for (index in array.indices) {
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