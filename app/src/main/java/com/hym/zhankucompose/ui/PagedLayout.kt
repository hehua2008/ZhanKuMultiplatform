package com.hym.zhankucompose.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.hym.zhankucompose.compose.EMPTY_BLOCK
import com.hym.zhankucompose.compose.NON_NUMBER_REGEX
import com.hym.zhankucompose.compose.PaddingOutlinedTextField
import com.hym.zhankucompose.compose.ReadOnlyOutlinedTextField
import com.hym.zhankucompose.compose.RemoveAccessibilityExtraSpace
import com.hym.zhankucompose.compose.SMALL_BUTTON_CONTENT_PADDING
import com.hym.zhankucompose.compose.SimpleExposedDropdownMenuBox
import com.hym.zhankucompose.compose.SmallButton
import com.hym.zhankucompose.compose.rememberMutableState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

/**
 * @author hehua2008
 * @date 2021/12/10
 */
private val HorizontalPadding = 8.dp
private val TextFieldContentPadding = PaddingValues(horizontal = HorizontalPadding)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PagedLayout(
    modifier: Modifier = Modifier,
    activePage: Int,
    lastPage: Int,
    pageSizeList: ImmutableList<Int>,
    pageSizeIndex: Int = 0,
    onPreClick: () -> Unit,
    onNextClick: () -> Unit,
    onJumpAction: (page: Int) -> Unit,
    onPageSizeSelected: (index: Int, item: Int) -> Unit
) {
    val textStyle = LocalTextStyle.current
    val numberFont = FontFamily.Monospace
    val numberTextStyle = remember(textStyle) {
        textStyle.copy(fontFamily = numberFont)
    }

    RemoveAccessibilityExtraSpace {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SmallButton(
                onClick = onPreClick,
                modifier = Modifier.padding(horizontal = HorizontalPadding),
                enabled = activePage > 1,
                shape = ShapeDefaults.ExtraSmall,
                contentPadding = SMALL_BUTTON_CONTENT_PADDING
            ) {
                Text(text = "上页")
            }

            Text(
                text = "$activePage",
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = numberFont
            )

            SmallButton(
                onClick = onNextClick,
                enabled = activePage != lastPage,
                modifier = Modifier.padding(horizontal = HorizontalPadding),
                shape = ShapeDefaults.ExtraSmall,
                contentPadding = SMALL_BUTTON_CONTENT_PADDING
            ) {
                Text(text = "下页")
            }

            val focusManager = LocalFocusManager.current
            val keyboardController = LocalSoftwareKeyboardController.current
            var targetPage by rememberMutableState(activePage) {
                val str = activePage.toString()
                TextFieldValue(str, selection = TextRange(str.length))
            }
            val onJumpClick = remember(activePage, lastPage, focusManager, keyboardController) {
                {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                    val page = targetPage.text.run {
                        if (isBlank()) activePage
                        else toInt().coerceAtLeast(1).coerceAtMost(lastPage)
                    }
                    val str = page.toString()
                    targetPage = TextFieldValue(str, selection = TextRange(str.length))
                    onJumpAction(page)
                }
            }
            PaddingOutlinedTextField(
                value = targetPage,
                onValueChange = { value ->
                    if (value.text === targetPage.text) { // Same string instance
                        targetPage = value
                        return@PaddingOutlinedTextField
                    }
                    val str = NON_NUMBER_REGEX.replace(value.text, "")
                    val differ = value.text.length - str.length
                    if (differ == 0) { // Same string content
                        targetPage = value
                        return@PaddingOutlinedTextField
                    }
                    val newSelectionEnd = value.selection.end - differ
                    val newSelectionStart = value.selection.start.coerceAtMost(newSelectionEnd)
                    targetPage = TextFieldValue(str, TextRange(newSelectionStart, newSelectionEnd))
                },
                modifier = Modifier
                    .wrapContentHeight()
                    .widthIn(20.dp, 60.dp)
                    .width(IntrinsicSize.Min),
                textStyle = numberTextStyle,
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Go
                ),
                keyboardActions = KeyboardActions(onGo = { onJumpClick() }),
                contentPadding = TextFieldContentPadding
            )

            SmallButton(
                onClick = onJumpClick,
                modifier = Modifier.padding(horizontal = HorizontalPadding),
                shape = ShapeDefaults.ExtraSmall,
                contentPadding = SMALL_BUTTON_CONTENT_PADDING
            ) {
                Text(text = "跳转")
            }

            Text(text = "每页", color = MaterialTheme.colorScheme.onSurface)

            val textMeasurer = rememberTextMeasurer()
            val longestText = remember(pageSizeList) {
                StringBuilder().run {
                    repeat(pageSizeList.maxOf { it.toString().length }) {
                        append('8')
                    }
                    toString()
                }
            }
            val density = LocalDensity.current
            val measuredWidth = remember(textMeasurer, longestText, numberTextStyle, density) {
                val textLayoutResult = textMeasurer.measure(
                    text = AnnotatedString(longestText),
                    overflow = TextOverflow.Visible,
                    style = numberTextStyle
                )
                with(density) {
                    textLayoutResult.size.width.toDp()
                }
            }

            SimpleExposedDropdownMenuBox(
                items = pageSizeList,
                modifier = Modifier.padding(horizontal = HorizontalPadding),
                defaultItemIndex = pageSizeIndex,
                selectedContent = { selectedItem, expanded ->
                    ReadOnlyOutlinedTextField(
                        value = "$selectedItem",
                        modifier = Modifier
                            .menuAnchor()
                            .width(measuredWidth + 2 * HorizontalPadding),
                        textStyle = numberTextStyle,
                        singleLine = true,
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        contentPadding = TextFieldContentPadding
                    )
                },
                menuItemContent = { item ->
                    Text(
                        text = "$item",
                        modifier = Modifier.padding(horizontal = HorizontalPadding),
                        maxLines = 1,
                        style = numberTextStyle
                    )
                }
            ) { index, item ->
                onPageSizeSelected(index, item)
            }
        }
    }
}

@Preview
@Composable
private fun PreviewPagedLayout() {
    PagedLayout(
        Modifier.background(Color.White),
        1,
        10,
        listOf(10, 25, 50, 100, 200, 400).toImmutableList(),
        0,
        EMPTY_BLOCK,
        EMPTY_BLOCK,
        {},
        { index, item -> }
    )
}
