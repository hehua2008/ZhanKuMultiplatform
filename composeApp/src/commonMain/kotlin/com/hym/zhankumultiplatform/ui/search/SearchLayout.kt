package com.hym.zhankumultiplatform.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import com.hym.zhankumultiplatform.compose.MULTIPLE_SPACE
import com.hym.zhankumultiplatform.compose.PaddingOutlinedTextField
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import zhankumultiplatform.composeapp.generated.resources.Res
import zhankumultiplatform.composeapp.generated.resources.vector_cancel
import zhankumultiplatform.composeapp.generated.resources.vector_search

/**
 * @author hehua2008
 * @date 2024/3/19
 */
private val EmptyTextField = TextFieldValue()

@Composable
fun SearchLayout(
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null,
    onCancel: (() -> Unit)? = null,
    onSearch: (keyword: String) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    var textFieldValue by remember {
        mutableStateOf(EmptyTextField)
    }

    PaddingOutlinedTextField(
        value = textFieldValue,
        onValueChange = { value ->
            if (value.text === textFieldValue.text) { // Same string instance
                textFieldValue = value
                return@PaddingOutlinedTextField
            }
            val str = MULTIPLE_SPACE.replace(value.text, " ")
            val differ = value.text.length - str.length
            if (differ == 0) { // Same string content
                textFieldValue = value
                return@PaddingOutlinedTextField
            }
            val newSelectionEnd = value.selection.end - differ
            val newSelectionStart = value.selection.start.coerceAtMost(newSelectionEnd)
            textFieldValue = TextFieldValue(str, TextRange(newSelectionStart, newSelectionEnd))
        },
        modifier = modifier,
        label = label,
        leadingIcon = { Icon(vectorResource(Res.drawable.vector_search), "") },
        trailingIcon = {
            Icon(
                imageVector = vectorResource(Res.drawable.vector_cancel),
                contentDescription = null,
                modifier = Modifier.clickable {
                    textFieldValue = EmptyTextField
                    onCancel?.invoke()
                }
            )
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = {
            focusManager.clearFocus()
            keyboardController?.hide()
            onSearch(textFieldValue.text)
        }),
        singleLine = true
    )
}

@Preview
@Composable
private fun PreviewSearchLayout() {
    SearchLayout(
        modifier = Modifier.background(Color.White),
        label = { Text("Input keyword") },
        onCancel = {}
    ) {}
}
