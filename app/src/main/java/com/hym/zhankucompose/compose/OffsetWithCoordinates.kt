package com.hym.zhankucompose.compose

import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.node.GlobalPositionAwareModifierNode
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset

/**
 * @author hehua2008
 * @date 2024/3/20
 */
// Modifier factory
fun Modifier.offsetWithCoordinates(offset: Density.(coordinates: LayoutCoordinates?) -> IntOffset) =
    this then OffsetWithSizeElement(offset)

// ModifierNodeElement
private data class OffsetWithSizeElement(
    val offset: Density.(coordinates: LayoutCoordinates?) -> IntOffset
) : ModifierNodeElement<OffsetWithSizeNode>() {
    override fun create() = OffsetWithSizeNode(offset)

    override fun update(node: OffsetWithSizeNode) {
        node.offset = offset
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "OffsetWithSizeElement"
        properties["offset"] = offset
    }
}

// Modifier.Node
private class OffsetWithSizeNode(
    var offset: Density.(coordinates: LayoutCoordinates?) -> IntOffset
) : GlobalPositionAwareModifierNode, LayoutModifierNode, Modifier.Node() {
    var coordinates: LayoutCoordinates? = null

    override fun onGloballyPositioned(coordinates: LayoutCoordinates) {
        this.coordinates = coordinates
    }

    override fun MeasureScope.measure(
        measurable: Measurable, constraints: Constraints
    ): MeasureResult {
        val placeable = measurable.measure(constraints)
        return layout(placeable.width, placeable.height) {
            val offsetValue = offset(coordinates)
            placeable.placeRelativeWithLayer(offsetValue.x, offsetValue.y)
        }
    }

    override fun toString(): String {
        return "OffsetWithSizeNode(offset=$offset)"
    }
}
