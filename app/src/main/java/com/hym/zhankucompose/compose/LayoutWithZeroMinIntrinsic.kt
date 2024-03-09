package com.hym.zhankucompose.compose

import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.IntrinsicMeasurable
import androidx.compose.ui.layout.IntrinsicMeasureScope
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.Constraints

/**
 * @author hehua2008
 * @date 2024/3/9
 */
private val DefaultMeasure: MeasureScope.(Measurable, Constraints) -> MeasureResult =
    { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        layout(placeable.width, placeable.height) {
            placeable.placeRelative(0, 0)
        }
    }

// Modifier factory
fun Modifier.layoutWithZeroMinIntrinsic(
    measure: MeasureScope.(Measurable, Constraints) -> MeasureResult = DefaultMeasure
) = this then LayoutWithZeroMinIntrinsicElement(zeroMinWidth = true, zeroMinHeight = true, measure)

fun Modifier.layoutWithZeroMinIntrinsicWidth(
    measure: MeasureScope.(Measurable, Constraints) -> MeasureResult = DefaultMeasure
) = this then LayoutWithZeroMinIntrinsicElement(zeroMinWidth = true, zeroMinHeight = false, measure)

fun Modifier.layoutWithZeroMinIntrinsicHeight(
    measure: MeasureScope.(Measurable, Constraints) -> MeasureResult = DefaultMeasure
) = this then LayoutWithZeroMinIntrinsicElement(zeroMinWidth = false, zeroMinHeight = true, measure)

// ModifierNodeElement
private data class LayoutWithZeroMinIntrinsicElement(
    val zeroMinWidth: Boolean,
    val zeroMinHeight: Boolean,
    val measure: MeasureScope.(Measurable, Constraints) -> MeasureResult
) : ModifierNodeElement<AbstractLayoutWithZeroMinIntrinsicNode>() {
    override fun create() =
        when {
            zeroMinWidth && zeroMinHeight -> LayoutWithZeroMinIntrinsicNode(measure)
            zeroMinWidth -> LayoutWithZeroMinIntrinsicWidthNode(measure)
            zeroMinHeight -> LayoutWithZeroMinIntrinsicHeightNode(measure)
            else -> throw IllegalArgumentException("Both zeroMinWidth and zeroMinHeight are false!")
        }

    override fun update(node: AbstractLayoutWithZeroMinIntrinsicNode) {
        node.measureBlock = measure
    }

    override fun InspectorInfo.inspectableProperties() {
        name = when {
            zeroMinWidth && zeroMinHeight -> "layoutWithZeroMinIntrinsic"
            zeroMinWidth -> "layoutWithZeroMinIntrinsicWidth"
            zeroMinHeight -> "layoutWithZeroMinIntrinsicHeight"
            else -> "layoutWithZeroMinIntrinsic"
        }
        properties["zeroMinWidth"] = zeroMinWidth
        properties["zeroMinHeight"] = zeroMinHeight
        properties["measure"] = measure
    }
}

// Modifier.Node
private abstract class AbstractLayoutWithZeroMinIntrinsicNode(
    var measureBlock: MeasureScope.(Measurable, Constraints) -> MeasureResult
) : LayoutModifierNode, Modifier.Node() {
    final override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints
    ) = measureBlock(measurable, constraints)
}

private class LayoutWithZeroMinIntrinsicNode(
    measureBlock: MeasureScope.(Measurable, Constraints) -> MeasureResult
) : AbstractLayoutWithZeroMinIntrinsicNode(measureBlock) {
    override fun IntrinsicMeasureScope.minIntrinsicWidth(
        measurable: IntrinsicMeasurable,
        height: Int
    ): Int = 0

    override fun IntrinsicMeasureScope.minIntrinsicHeight(
        measurable: IntrinsicMeasurable,
        width: Int
    ): Int = 0

    override fun toString(): String {
        return "LayoutWithZeroMinIntrinsicNode(measureBlock=$measureBlock)"
    }
}

private class LayoutWithZeroMinIntrinsicWidthNode(
    measureBlock: MeasureScope.(Measurable, Constraints) -> MeasureResult
) : AbstractLayoutWithZeroMinIntrinsicNode(measureBlock) {

    override fun IntrinsicMeasureScope.minIntrinsicWidth(
        measurable: IntrinsicMeasurable,
        height: Int
    ): Int = 0

    override fun toString(): String {
        return "LayoutWithZeroMinIntrinsicWidthModifierNode(measureBlock=$measureBlock)"
    }
}

private class LayoutWithZeroMinIntrinsicHeightNode(
    measureBlock: MeasureScope.(Measurable, Constraints) -> MeasureResult
) : AbstractLayoutWithZeroMinIntrinsicNode(measureBlock) {

    override fun IntrinsicMeasureScope.minIntrinsicHeight(
        measurable: IntrinsicMeasurable,
        width: Int
    ): Int = 0

    override fun toString(): String {
        return "LayoutWithZeroMinIntrinsicHeightModifierNode(measureBlock=$measureBlock)"
    }
}
