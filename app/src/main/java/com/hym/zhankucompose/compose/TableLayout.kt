package com.hym.zhankucompose.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import kotlinx.collections.immutable.ImmutableList

/**
 * @author hehua2008
 * @date 2024/3/9
 */
@Composable
fun <T> TableLayout(
    rowColumnList: ImmutableList<ImmutableList<T?>>,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    itemContent: @Composable (T?, Modifier) -> Unit
) {
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current

    Layout(
        content = {
            rowColumnList.forEachIndexed { rowIndex, columnList ->
                columnList.forEachIndexed { columnIndex, t ->
                    itemContent(t, Modifier.layoutId(rowIndex to columnIndex))
                }
            }
        },
        modifier = modifier
    ) { measurables, constraints ->
        val rowSize = rowColumnList.size
        val rowWidths = IntArray(rowSize)
        val columnSize = rowColumnList.maxOf { columnList -> columnList.size }
        val columnHeights = IntArray(columnSize)
        val rowColumnPlaceableArray = Array<Array<Placeable?>>(rowSize) {
            arrayOfNulls(rowColumnList[it].size)
        }
        // To avoid placeable.width = constraints.FixedWidth or placeable.height = constraints.FixedHeight
        val measureConstraints = constraints.copy(minWidth = 0, minHeight = 0)
        measurables.forEach {
            val (rowIndex, columnIndex) = it.layoutId as Pair<Int, Int>
            val placeable = it.measure(measureConstraints)
            rowColumnPlaceableArray[rowIndex][columnIndex] = placeable
            if (rowWidths[rowIndex] < placeable.width) {
                rowWidths[rowIndex] = placeable.width
            }
            if (columnHeights[columnIndex] < placeable.height) {
                columnHeights[columnIndex] = placeable.height
            }
        }

        val rowOutPositions = IntArray(rowSize)
        val layoutWidth = constraints.constrainWidth(
            rowWidths.sum() + horizontalArrangement.spacing.roundToPx() * (rowSize - 1)
        )
        with(horizontalArrangement) {
            density.arrange(
                layoutWidth,
                rowWidths,
                layoutDirection,
                rowOutPositions
            )
        }

        val columnOutPositions = IntArray(columnSize)
        val layoutHeight = constraints.constrainHeight(
            columnHeights.sum() + verticalArrangement.spacing.roundToPx() * (columnSize - 1)
        )
        with(verticalArrangement) {
            density.arrange(
                layoutHeight,
                columnHeights,
                columnOutPositions
            )
        }

        layout(layoutWidth, layoutHeight) {
            rowColumnPlaceableArray.forEachIndexed { rowIndex, placeables ->
                placeables.forEachIndexed { columnIndex, placeable ->
                    placeable?.placeRelative(
                        rowOutPositions[rowIndex],
                        columnOutPositions[columnIndex]
                    )
                }
            }
        }
    }
}
