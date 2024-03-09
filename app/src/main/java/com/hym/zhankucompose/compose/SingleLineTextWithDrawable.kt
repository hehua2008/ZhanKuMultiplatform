package com.hym.zhankucompose.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hym.zhankucompose.R

/**
 * @author hehua2008
 * @date 2024/3/9
 */
@Composable
fun SingleLineTextWithDrawable(
    text: String,
    modifier: Modifier = Modifier,
    prefix: (@Composable () -> Unit)? = null,
    suffix: (@Composable () -> Unit)? = null,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    softWrap: Boolean = true,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null,
    style: TextStyle = LocalTextStyle.current
) {
    val content: @Composable () -> Unit = {
        Box {
            prefix?.invoke()
        }

        Text(
            text = text,
            modifier = Modifier.wrapContentSize(),
            color = color,
            fontSize = fontSize,
            fontStyle = fontStyle,
            fontWeight = fontWeight,
            fontFamily = fontFamily,
            letterSpacing = letterSpacing,
            textDecoration = textDecoration,
            textAlign = textAlign,
            lineHeight = lineHeight,
            overflow = overflow,
            softWrap = softWrap,
            maxLines = 1,
            onTextLayout = onTextLayout,
            style = style
        )

        Box {
            suffix?.invoke()
        }
    }

    Layout(content = content, modifier = modifier) { measurables, constraints ->
        val prefixMeasurable = measurables[0]
        val textMeasurable = measurables[1]
        val suffixLMeasurable = measurables[2]

        // To avoid placeable.width = constraints.FixedWidth or placeable.height = constraints.FixedHeight
        val initialConstraints = constraints.copy(minWidth = 0, minHeight = 0)
        val singleLineTextHeight = textMeasurable.minIntrinsicHeight(Constraints.Infinity)
        val layoutHeight = constraints.constrainHeight(singleLineTextHeight)

        // Measure prefix
        val prefixConstraints = initialConstraints.copy(
            maxHeight = layoutHeight,
            minHeight = initialConstraints.minHeight.coerceAtMost(layoutHeight)
        )
        val prefixPlaceable = prefixMeasurable.measure(prefixConstraints)

        // Measure suffix
        val suffixMaxWidth = prefixConstraints.maxWidth - prefixPlaceable.width
        val suffixConstraints = prefixConstraints.copy(
            maxWidth = suffixMaxWidth,
            minWidth = prefixConstraints.minWidth.coerceAtMost(suffixMaxWidth)
        )
        val suffixPlaceable = suffixLMeasurable.measure(suffixConstraints)

        // Measure text
        val textMaxWidth = suffixConstraints.maxWidth - suffixPlaceable.width
        val textConstraints = suffixConstraints.copy(
            maxWidth = textMaxWidth,
            minWidth = suffixConstraints.minWidth.coerceAtMost(textMaxWidth)
        )
        val textPlaceable = textMeasurable.measure(textConstraints)

        // Calculate layout width
        val layoutWidth = constraints.constrainWidth(
            prefixPlaceable.width + textPlaceable.width + suffixPlaceable.width
        )

        layout(layoutWidth, layoutHeight) {
            prefixPlaceable.placeRelative(
                0,
                verticalAlignment.align(prefixPlaceable.height, layoutHeight)
            )
            textPlaceable.placeRelative(
                prefixPlaceable.width,
                verticalAlignment.align(textPlaceable.height, layoutHeight)
            )
            suffixPlaceable.placeRelative(
                prefixPlaceable.width + textPlaceable.width,
                verticalAlignment.align(suffixPlaceable.height, layoutHeight)
            )
        }
    }
}

@Preview
@Composable
private fun PreviewSingleLineTextWithDrawable() {
    SingleLineTextWithDrawable(
        text = "SingleLineTextWithDrawable",
        modifier = Modifier
            .background(Color.White)
            .wrapContentSize()
            .padding(20.dp),
        prefix = {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.vector_eye),
                contentDescription = null
            )
        },
        suffix = {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.vector_comment),
                contentDescription = null
            )
        },
        fontSize = 30.sp
    )
}
