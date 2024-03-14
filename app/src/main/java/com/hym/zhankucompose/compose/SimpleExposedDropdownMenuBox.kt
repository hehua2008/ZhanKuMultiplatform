package com.hym.zhankucompose.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuBoxScope
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.autoSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

private val ZeroPaddingValues = PaddingValues()

/**
 * @author hehua2008
 * @date 2024/3/14
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> SimpleExposedDropdownMenuBox(
    items: ImmutableList<T>,
    modifier: Modifier = Modifier,
    defaultItemIndex: Int = 0,
    selectedContent: @Composable ExposedDropdownMenuBoxScope.(selectedItem: T, expanded: Boolean) -> Unit,
    menuItemContent: @Composable (menuItem: T) -> Unit,
    onItemSelected: (index: Int, item: T) -> Unit
) {
    var expanded by rememberSaveable(stateSaver = autoSaver()) { mutableStateOf(false) }
    var selectedIndex by rememberSaveable(items, defaultItemIndex, stateSaver = autoSaver()) {
        mutableIntStateOf(defaultItemIndex)
    }
    // We want to react on tap/press on TextField to show menu
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier,
    ) {
        val selectedItem = items[selectedIndex]

        selectedContent(selectedItem, expanded)

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.exposedDropdownSize(true)
        ) {
            items.forEachIndexed { index, item ->
                DropdownMenuItem(
                    text = { menuItemContent(item) },
                    onClick = {
                        selectedIndex = index
                        expanded = false
                        onItemSelected(index, item)
                    },
                    contentPadding = ZeroPaddingValues
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun PreviewSimpleExposedDropdownMenuBox() {
    val textMeasurer = rememberTextMeasurer()
    val textStyle = LocalTextStyle.current
    val density = LocalDensity.current
    val measuredWidth = remember(textMeasurer, textStyle, density) {
        val textLayoutResult = textMeasurer.measure(
            text = AnnotatedString("888"),
            overflow = TextOverflow.Visible,
            style = textStyle
        )
        with(density) {
            textLayoutResult.size.width.toDp()
        }
    }
    SimpleExposedDropdownMenuBox(
        items = remember { listOf(25, 50, 100, 200, 400, 800).toImmutableList() },
        selectedContent = { selectedItem, expanded ->
            ReadOnlyOutlinedTextField(
                value = "$selectedItem",
                modifier = Modifier
                    .menuAnchor()
                    .width(measuredWidth),
                singleLine = true,
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
            )
        },
        menuItemContent = { item ->
            Text(
                text = "$item",
                maxLines = 1
            )
        },
        modifier = Modifier.background(Color.White)
    ) { index, item ->
    }
}
