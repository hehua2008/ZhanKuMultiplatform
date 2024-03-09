package com.hym.zhankucompose.compose

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private const val TAG = "CommonComponents"

private val ZeroPaddingValues = PaddingValues()
private val EmptyOnValueChangeBlock: (String) -> Unit = {}

@Composable
fun ReadOnlyTextField(
    value: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    textStyle: TextStyle = LocalTextStyle.current,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = TextFieldDefaults.shape,
    containerColor: Color = Color.Unspecified,
    indicatorColor: Color = Color.Unspecified,
    colors: TextFieldColors = TextFieldDefaults.colors(),
    contentPadding: PaddingValues = ZeroPaddingValues
) {
    PaddingTextField(
        value,
        onValueChange = EmptyOnValueChangeBlock,
        modifier,
        enabled,
        readOnly = true,
        textStyle,
        label = null,
        placeholder = null,
        leadingIcon,
        trailingIcon,
        prefix,
        suffix,
        supportingText = null,
        isError = false,
        visualTransformation = VisualTransformation.None,
        keyboardOptions,
        keyboardActions,
        singleLine,
        maxLines,
        minLines,
        interactionSource,
        shape,
        colors = remember(colors, containerColor, indicatorColor) {
            colors.copy(
                focusedContainerColor = if (containerColor.isSpecified) containerColor else colors.focusedContainerColor,
                unfocusedContainerColor = if (containerColor.isSpecified) containerColor else colors.unfocusedContainerColor,
                disabledContainerColor = if (containerColor.isSpecified) containerColor else colors.disabledContainerColor,
                errorContainerColor = if (containerColor.isSpecified) containerColor else colors.errorContainerColor,
                focusedIndicatorColor = if (indicatorColor.isSpecified) indicatorColor else colors.focusedIndicatorColor,
                unfocusedIndicatorColor = if (indicatorColor.isSpecified) indicatorColor else colors.unfocusedIndicatorColor,
                disabledIndicatorColor = if (indicatorColor.isSpecified) indicatorColor else colors.disabledIndicatorColor,
                errorIndicatorColor = if (indicatorColor.isSpecified) indicatorColor else colors.errorIndicatorColor
            )
        },
        contentPadding = contentPadding
    )
}

@Composable
fun ReadOnlyOutlinedTextField(
    value: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    textStyle: TextStyle = LocalTextStyle.current,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = OutlinedTextFieldDefaults.shape,
    containerColor: Color = Color.Unspecified,
    indicatorColor: Color = Color.Unspecified,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(),
    contentPadding: PaddingValues = ZeroPaddingValues,
    outlinedTextFieldTopPadding: Dp = 0.dp
) {
    PaddingOutlinedTextField(
        value,
        onValueChange = EmptyOnValueChangeBlock,
        modifier,
        enabled,
        readOnly = true,
        textStyle,
        label = null,
        placeholder = null,
        leadingIcon,
        trailingIcon,
        prefix,
        suffix,
        supportingText = null,
        isError = false,
        visualTransformation = VisualTransformation.None,
        keyboardOptions,
        keyboardActions,
        singleLine,
        maxLines,
        minLines,
        interactionSource,
        shape,
        colors = remember(colors, containerColor, indicatorColor) {
            colors.copy(
                focusedContainerColor = if (containerColor.isSpecified) containerColor else colors.focusedContainerColor,
                unfocusedContainerColor = if (containerColor.isSpecified) containerColor else colors.unfocusedContainerColor,
                disabledContainerColor = if (containerColor.isSpecified) containerColor else colors.disabledContainerColor,
                errorContainerColor = if (containerColor.isSpecified) containerColor else colors.errorContainerColor,
                focusedIndicatorColor = if (indicatorColor.isSpecified) indicatorColor else colors.focusedIndicatorColor,
                unfocusedIndicatorColor = if (indicatorColor.isSpecified) indicatorColor else colors.unfocusedIndicatorColor,
                disabledIndicatorColor = if (indicatorColor.isSpecified) indicatorColor else colors.disabledIndicatorColor,
                errorIndicatorColor = if (indicatorColor.isSpecified) indicatorColor else colors.errorIndicatorColor
            )
        },
        contentPadding = contentPadding,
        outlinedTextFieldTopPadding = outlinedTextFieldTopPadding
    )
}

@ExperimentalMaterial3Api
@Composable
fun RemoveAccessibilityExtraSpace(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false, content)
}

@ExperimentalMaterial3Api
@Composable
fun RestoreAccessibilityExtraSpace(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides true, content)
}
