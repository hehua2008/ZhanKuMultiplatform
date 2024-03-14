package com.hym.zhankucompose.compose

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ChipColors
import androidx.compose.material3.ChipElevation
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ElevatedSuggestionChip
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableChipColors
import androidx.compose.material3.SelectableChipElevation
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.offset
import androidx.compose.ui.util.fastFirst
import androidx.compose.ui.util.fastFirstOrNull
import com.hym.zhankucompose.compose.SelectableChipColorsHelper.getValue
import java.lang.reflect.Field

/**
 * @author hehua2008
 * @date 2024/3/14
 */

/**
 * <a href="https://m3.material.io/components/chips/overview" class="external" target="_blank">Material Design assist chip</a>.
 *
 * Chips help people enter information, make selections, filter content, or trigger actions. Chips
 * can show multiple interactive elements together in the same area, such as a list of selectable
 * movie times, or a series of email contacts.
 *
 * Assist chips represent smart or automated actions that can span multiple apps, such as opening a
 * calendar event from the home screen. Assist chips function as though the user asked an assistant
 * to complete the action. They should appear dynamically and contextually in a UI.
 *
 * ![Assist chip image](https://developer.android.com/images/reference/androidx/compose/material3/assist-chip.png)
 *
 * This assist chip is applied with a flat style. If you want an elevated style, use the
 * [ElevatedAssistChip].
 *
 * Example of a flat AssistChip:
 * @sample androidx.compose.material3.samples.AssistChipSample
 *
 * @param onClick called when this chip is clicked
 * @param label text label for this chip
 * @param modifier the [Modifier] to be applied to this chip
 * @param enabled controls the enabled state of this chip. When `false`, this component will not
 * respond to user input, and it will appear visually disabled and disabled to accessibility
 * services.
 * @param leadingIcon optional icon at the start of the chip, preceding the [label] text
 * @param trailingIcon optional icon at the end of the chip
 * @param shape defines the shape of this chip's container, border (when [border] is not null), and
 * shadow (when using [elevation])
 * @param colors [ChipColors] that will be used to resolve the colors used for this chip in
 * different states. See [AssistChipDefaults.assistChipColors].
 * @param elevation [ChipElevation] used to resolve the elevation for this chip in different states.
 * This controls the size of the shadow below the chip. Additionally, when the container color is
 * [ColorScheme.surface], this controls the amount of primary color applied as an overlay. See
 * [AssistChipDefaults.assistChipElevation].
 * @param border the border to draw around the container of this chip. Pass `null` for no border.
 * See [AssistChipDefaults.assistChipBorder].
 * @param interactionSource the [MutableInteractionSource] representing the stream of [Interaction]s
 * for this chip. You can create and pass in your own `remember`ed instance to observe
 * [Interaction]s and customize the appearance / behavior of this chip in different states.
 */
@Composable
fun SmallAssistChip(
    onClick: () -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    shape: Shape = AssistChipDefaults.shape,
    colors: ChipColors = AssistChipDefaults.assistChipColors(),
    elevation: ChipElevation? = AssistChipDefaults.assistChipElevation(),
    border: BorderStroke? = AssistChipDefaults.assistChipBorder(enabled),
    minHeight: Dp = AssistChipDefaults.Height,
    paddingValues: PaddingValues = AssistChipPadding,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) = Chip(
    modifier = modifier,
    onClick = onClick,
    enabled = enabled,
    label = label,
    labelTextStyle = MaterialTheme.typography.labelLarge,
    labelColor = if (enabled) colors.labelColor else colors.disabledLabelColor,
    leadingIcon = leadingIcon,
    trailingIcon = trailingIcon,
    shape = shape,
    colors = colors,
    elevation = elevation,
    border = border,
    minHeight = minHeight,
    paddingValues = paddingValues,
    interactionSource = interactionSource
)

/**
 * <a href="https://m3.material.io/components/chips/overview" class="external" target="_blank">Material Design elevated assist chip</a>.
 *
 * Chips help people enter information, make selections, filter content, or trigger actions. Chips
 * can show multiple interactive elements together in the same area, such as a list of selectable
 * movie times, or a series of email contacts.
 *
 * Assist chips represent smart or automated actions that can span multiple apps, such as opening a
 * calendar event from the home screen. Assist chips function as though the user asked an assistant
 * to complete the action. They should appear dynamically and contextually in a UI.
 *
 * ![Assist chip image](https://developer.android.com/images/reference/androidx/compose/material3/elevated-assist-chip.png)
 *
 * This assist chip is applied with an elevated style. If you want a flat style, use the
 * [AssistChip].
 *
 * Example of an elevated AssistChip with a trailing icon:
 * @sample androidx.compose.material3.samples.ElevatedAssistChipSample
 *
 * @param onClick called when this chip is clicked
 * @param label text label for this chip
 * @param modifier the [Modifier] to be applied to this chip
 * @param enabled controls the enabled state of this chip. When `false`, this component will not
 * respond to user input, and it will appear visually disabled and disabled to accessibility
 * services.
 * @param leadingIcon optional icon at the start of the chip, preceding the [label] text
 * @param trailingIcon optional icon at the end of the chip
 * @param shape defines the shape of this chip's container, border (when [border] is not null), and
 * shadow (when using [elevation])
 * @param colors [ChipColors] that will be used to resolve the colors used for this chip in
 * different states. See [AssistChipDefaults.elevatedAssistChipColors].
 * @param elevation [ChipElevation] used to resolve the elevation for this chip in different states.
 * This controls the size of the shadow below the chip. Additionally, when the container color is
 * [ColorScheme.surface], this controls the amount of primary color applied as an overlay. See
 * [AssistChipDefaults.elevatedAssistChipElevation].
 * @param border the border to draw around the container of this chip
 * @param interactionSource the [MutableInteractionSource] representing the stream of [Interaction]s
 * for this chip. You can create and pass in your own `remember`ed instance to observe
 * [Interaction]s and customize the appearance / behavior of this chip in different states.
 */
@Composable
fun SmallElevatedAssistChip(
    onClick: () -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    shape: Shape = AssistChipDefaults.shape,
    colors: ChipColors = AssistChipDefaults.elevatedAssistChipColors(),
    elevation: ChipElevation? = AssistChipDefaults.elevatedAssistChipElevation(),
    border: BorderStroke? = null,
    minHeight: Dp = AssistChipDefaults.Height,
    paddingValues: PaddingValues = AssistChipPadding,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) = Chip(
    modifier = modifier,
    onClick = onClick,
    enabled = enabled,
    label = label,
    labelTextStyle = MaterialTheme.typography.labelLarge,
    labelColor = if (enabled) colors.labelColor else colors.disabledLabelColor,
    leadingIcon = leadingIcon,
    trailingIcon = trailingIcon,
    elevation = elevation,
    colors = colors,
    minHeight = minHeight,
    paddingValues = paddingValues,
    shape = shape,
    border = border,
    interactionSource = interactionSource
)

/**
 * <a href="https://m3.material.io/components/chips/overview" class="external" target="_blank">Material Design filter chip</a>.
 *
 * Chips help people enter information, make selections, filter content, or trigger actions. Chips
 * can show multiple interactive elements together in the same area, such as a list of selectable
 * movie times, or a series of email contacts.
 *
 * Filter chips use tags or descriptive words to filter content. They can be a good alternative to
 * toggle buttons or checkboxes.
 *
 * ![Filter chip image](https://developer.android.com/images/reference/androidx/compose/material3/filter-chip.png)
 *
 * This filter chip is applied with a flat style. If you want an elevated style, use the
 * [ElevatedFilterChip].
 *
 * Tapping on a filter chip toggles its selection state. A selection state [leadingIcon] can be
 * provided (e.g. a checkmark) to be appended at the starting edge of the chip's label.
 *
 * Example of a flat FilterChip with a trailing icon:
 * @sample androidx.compose.material3.samples.FilterChipSample
 *
 * Example of a FilterChip with both a leading icon and a selected icon:
 * @sample androidx.compose.material3.samples.FilterChipWithLeadingIconSample
 *
 * @param selected whether this chip is selected or not
 * @param onClick called when this chip is clicked
 * @param label text label for this chip
 * @param modifier the [Modifier] to be applied to this chip
 * @param enabled controls the enabled state of this chip. When `false`, this component will not
 * respond to user input, and it will appear visually disabled and disabled to accessibility
 * services.
 * @param leadingIcon optional icon at the start of the chip, preceding the [label] text. When
 * [selected] is true, this icon may visually indicate that the chip is selected (for example, via a
 * checkmark icon).
 * @param trailingIcon optional icon at the end of the chip
 * @param shape defines the shape of this chip's container, border (when [border] is not null), and
 * shadow (when using [elevation])
 * @param colors [SelectableChipColors] that will be used to resolve the colors used for this chip
 * in different states. See [FilterChipDefaults.filterChipColors].
 * @param elevation [SelectableChipElevation] used to resolve the elevation for this chip in
 * different states. This controls the size of the shadow below the chip. Additionally, when the
 * container color is [ColorScheme.surface], this controls the amount of primary color applied as an
 * overlay. See [FilterChipDefaults.filterChipElevation].
 * @param border the border to draw around the container of this chip. Pass `null` for no border.
 * See [FilterChipDefaults.filterChipBorder].
 * @param interactionSource the [MutableInteractionSource] representing the stream of [Interaction]s
 * for this chip. You can create and pass in your own `remember`ed instance to observe
 * [Interaction]s and customize the appearance / behavior of this chip in different states.
 */
@Composable
fun SmallFilterChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    shape: Shape = FilterChipDefaults.shape,
    colors: SelectableChipColors = FilterChipDefaults.filterChipColors(),
    elevation: SelectableChipElevation? = FilterChipDefaults.filterChipElevation(),
    border: BorderStroke? = FilterChipDefaults.filterChipBorder(enabled, selected),
    minHeight: Dp = FilterChipDefaults.Height,
    paddingValues: PaddingValues = FilterChipPadding,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) = SelectableChip(
    selected = selected,
    modifier = modifier,
    onClick = onClick,
    enabled = enabled,
    label = label,
    labelTextStyle = MaterialTheme.typography.labelLarge,
    leadingIcon = leadingIcon,
    avatar = null,
    trailingIcon = trailingIcon,
    elevation = elevation,
    colors = colors,
    minHeight = minHeight,
    paddingValues = paddingValues,
    shape = shape,
    border = border,
    interactionSource = interactionSource
)

/**
 * <a href="https://m3.material.io/components/chips/overview" class="external" target="_blank">Material Design elevated filter chip</a>.
 *
 * Chips help people enter information, make selections, filter content, or trigger actions. Chips
 * can show multiple interactive elements together in the same area, such as a list of selectable
 * movie times, or a series of email contacts.
 *
 * Filter chips use tags or descriptive words to filter content. They can be a good alternative to
 * toggle buttons or checkboxes.
 *
 * ![Filter chip image](https://developer.android.com/images/reference/androidx/compose/material3/elevated-filter-chip.png)
 *
 * This filter chip is applied with an elevated style. If you want a flat style, use the
 * [FilterChip].
 *
 * Tapping on a filter chip toggles its selection state. A selection state [leadingIcon] can be
 * provided (e.g. a checkmark) to be appended at the starting edge of the chip's label.
 *
 * Example of an elevated FilterChip with a trailing icon:
 * @sample androidx.compose.material3.samples.ElevatedFilterChipSample
 *
 * @param selected whether this chip is selected or not
 * @param onClick called when this chip is clicked
 * @param label text label for this chip
 * @param modifier the [Modifier] to be applied to this chip
 * @param enabled controls the enabled state of this chip. When `false`, this component will not
 * respond to user input, and it will appear visually disabled and disabled to accessibility
 * services.
 * @param leadingIcon optional icon at the start of the chip, preceding the [label] text. When
 * [selected] is true, this icon may visually indicate that the chip is selected (for example, via a
 * checkmark icon).
 * @param trailingIcon optional icon at the end of the chip
 * @param shape defines the shape of this chip's container, border (when [border] is not null), and
 * shadow (when using [elevation])
 * @param colors [SelectableChipColors] that will be used to resolve the colors used for this chip
 * in different states. See [FilterChipDefaults.elevatedFilterChipColors].
 * @param elevation [SelectableChipElevation] used to resolve the elevation for this chip in
 * different states. This controls the size of the shadow below the chip. Additionally, when the
 * container color is [ColorScheme.surface], this controls the amount of primary color applied as an
 * overlay. See [FilterChipDefaults.filterChipElevation].
 * @param border the border to draw around the container of this chip. Pass `null` for no border.
 * See [FilterChipDefaults.filterChipBorder].
 * @param interactionSource the [MutableInteractionSource] representing the stream of [Interaction]s
 * for this chip. You can create and pass in your own `remember`ed instance to observe
 * [Interaction]s and customize the appearance / behavior of this chip in different states.
 */
@Composable
fun SmallElevatedFilterChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    shape: Shape = FilterChipDefaults.shape,
    colors: SelectableChipColors = FilterChipDefaults.elevatedFilterChipColors(),
    elevation: SelectableChipElevation? = FilterChipDefaults.elevatedFilterChipElevation(),
    border: BorderStroke? = null,
    minHeight: Dp = FilterChipDefaults.Height,
    paddingValues: PaddingValues = FilterChipPadding,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) = SelectableChip(
    selected = selected,
    modifier = modifier,
    onClick = onClick,
    enabled = enabled,
    label = label,
    labelTextStyle = MaterialTheme.typography.labelLarge,
    leadingIcon = leadingIcon,
    avatar = null,
    trailingIcon = trailingIcon,
    elevation = elevation,
    colors = colors,
    minHeight = minHeight,
    paddingValues = paddingValues,
    shape = shape,
    border = border,
    interactionSource = interactionSource
)

/**
 * <a href="https://m3.material.io/components/chips/overview" class="external" target="_blank">Material Design input chip</a>.
 *
 * Chips help people enter information, make selections, filter content, or trigger actions. Chips
 * can show multiple interactive elements together in the same area, such as a list of selectable
 * movie times, or a series of email contacts.
 *
 * Input chips represent discrete pieces of information entered by a user.
 *
 * ![Input chip image](https://developer.android.com/images/reference/androidx/compose/material3/input-chip.png)
 *
 * An Input Chip can have a leading icon or an avatar at its start. In case both are provided, the
 * avatar will take precedence and will be displayed.
 *
 * Example of an InputChip with a trailing icon:
 * @sample androidx.compose.material3.samples.InputChipSample
 *
 * Example of an InputChip with an avatar and a trailing icon:
 * @sample androidx.compose.material3.samples.InputChipWithAvatarSample
 *
 * Input chips should appear in a set and can be horizontally scrollable:
 * @sample androidx.compose.material3.samples.ChipGroupSingleLineSample
 *
 * Alternatively, use [androidx.compose.foundation.layout.FlowRow] to wrap chips to a new line.
 * @sample androidx.compose.material3.samples.ChipGroupReflowSample
 *
 * @param selected whether this chip is selected or not
 * @param onClick called when this chip is clicked
 * @param label text label for this chip
 * @param modifier the [Modifier] to be applied to this chip
 * @param enabled controls the enabled state of this chip. When `false`, this component will not
 * respond to user input, and it will appear visually disabled and disabled to accessibility
 * services.
 * @param leadingIcon optional icon at the start of the chip, preceding the [label] text
 * @param avatar optional avatar at the start of the chip, preceding the [label] text
 * @param trailingIcon optional icon at the end of the chip
 * @param shape defines the shape of this chip's container, border (when [border] is not null), and
 * shadow (when using [elevation])
 * @param colors [ChipColors] that will be used to resolve the colors used for this chip in
 * different states. See [InputChipDefaults.inputChipColors].
 * @param elevation [ChipElevation] used to resolve the elevation for this chip in different states.
 * This controls the size of the shadow below the chip. Additionally, when the container color is
 * [ColorScheme.surface], this controls the amount of primary color applied as an overlay. See
 * [InputChipDefaults.inputChipElevation].
 * @param border the border to draw around the container of this chip. Pass `null` for no border.
 * See [InputChipDefaults.inputChipBorder].
 * @param interactionSource the [MutableInteractionSource] representing the stream of [Interaction]s
 * for this chip. You can create and pass in your own `remember`ed instance to observe
 * [Interaction]s and customize the appearance / behavior of this chip in different states.
 */
@Composable
fun SmallInputChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: @Composable (() -> Unit)? = null,
    avatar: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    shape: Shape = InputChipDefaults.shape,
    colors: SelectableChipColors = InputChipDefaults.inputChipColors(),
    elevation: SelectableChipElevation? = InputChipDefaults.inputChipElevation(),
    border: BorderStroke? = InputChipDefaults.inputChipBorder(enabled, selected),
    minHeight: Dp = InputChipDefaults.Height,
    paddingValues: PaddingValues = inputChipPadding(
        hasAvatar = avatar != null,
        hasLeadingIcon = leadingIcon != null,
        hasTrailingIcon = trailingIcon != null
    ),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    // If given, place the avatar in an InputChipTokens.AvatarShape shape before passing it into the
    // Chip function.
    var shapedAvatar: @Composable (() -> Unit)? = null
    if (avatar != null) {
        val avatarOpacity = if (enabled) 1f else 0.38f
        val avatarShape = CircleShape
        shapedAvatar = @Composable {
            Box(
                modifier = Modifier.graphicsLayer {
                    this.alpha = avatarOpacity
                    this.shape = avatarShape
                    this.clip = true
                },
                contentAlignment = Alignment.Center
            ) {
                avatar()
            }
        }
    }
    SelectableChip(
        selected = selected,
        modifier = modifier,
        onClick = onClick,
        enabled = enabled,
        label = label,
        labelTextStyle = MaterialTheme.typography.labelLarge,
        leadingIcon = leadingIcon,
        avatar = shapedAvatar,
        trailingIcon = trailingIcon,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border,
        minHeight = minHeight,
        paddingValues = paddingValues,
        interactionSource = interactionSource
    )
}

/**
 * <a href="https://m3.material.io/components/chips/overview" class="external" target="_blank">Material Design suggestion chip</a>.
 *
 * Chips help people enter information, make selections, filter content, or trigger actions. Chips
 * can show multiple interactive elements together in the same area, such as a list of selectable
 * movie times, or a series of email contacts.
 *
 * Suggestion chips help narrow a user's intent by presenting dynamically generated suggestions,
 * such as possible responses or search filters.
 *
 * ![Suggestion chip image](https://developer.android.com/images/reference/androidx/compose/material3/suggestion-chip.png)
 *
 * This suggestion chip is applied with a flat style. If you want an elevated style, use the
 * [ElevatedSuggestionChip].
 *
 * Example of a flat SuggestionChip with a trailing icon:
 * @sample androidx.compose.material3.samples.SuggestionChipSample
 *
 * @param onClick called when this chip is clicked
 * @param label text label for this chip
 * @param modifier the [Modifier] to be applied to this chip
 * @param enabled controls the enabled state of this chip. When `false`, this component will not
 * respond to user input, and it will appear visually disabled and disabled to accessibility
 * services.
 * @param icon optional icon at the start of the chip, preceding the [label] text
 * @param shape defines the shape of this chip's container, border (when [border] is not null), and
 * shadow (when using [elevation])
 * @param colors [ChipColors] that will be used to resolve the colors used for this chip in
 * different states. See [SuggestionChipDefaults.suggestionChipColors].
 * @param elevation [ChipElevation] used to resolve the elevation for this chip in different states.
 * This controls the size of the shadow below the chip. Additionally, when the container color is
 * [ColorScheme.surface], this controls the amount of primary color applied as an overlay. See
 * [SuggestionChipDefaults.suggestionChipElevation].
 * @param border the border to draw around the container of this chip. Pass `null` for no border.
 * See [SuggestionChipDefaults.suggestionChipBorder].
 * @param interactionSource the [MutableInteractionSource] representing the stream of [Interaction]s
 * for this chip. You can create and pass in your own `remember`ed instance to observe
 * [Interaction]s and customize the appearance / behavior of this chip in different states.
 */
@Composable
fun SmallSuggestionChip(
    onClick: () -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: @Composable (() -> Unit)? = null,
    shape: Shape = SuggestionChipDefaults.shape,
    colors: ChipColors = SuggestionChipDefaults.suggestionChipColors(),
    elevation: ChipElevation? = SuggestionChipDefaults.suggestionChipElevation(),
    border: BorderStroke? = SuggestionChipDefaults.suggestionChipBorder(enabled),
    minHeight: Dp = SuggestionChipDefaults.Height,
    paddingValues: PaddingValues = SuggestionChipPadding,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) = Chip(
    modifier = modifier,
    onClick = onClick,
    enabled = enabled,
    label = label,
    labelTextStyle = MaterialTheme.typography.labelLarge,
    labelColor = if (enabled) colors.labelColor else colors.disabledLabelColor,
    leadingIcon = icon,
    trailingIcon = null,
    shape = shape,
    colors = colors,
    elevation = elevation,
    border = border,
    minHeight = minHeight,
    paddingValues = paddingValues,
    interactionSource = interactionSource
)

/**
 * <a href="https://m3.material.io/components/chips/overview" class="external" target="_blank">Material Design elevated suggestion chip</a>.
 *
 * Chips help people enter information, make selections, filter content, or trigger actions. Chips
 * can show multiple interactive elements together in the same area, such as a list of selectable
 * movie times, or a series of email contacts.
 *
 * Suggestion chips help narrow a user's intent by presenting dynamically generated suggestions,
 * such as possible responses or search filters.
 *
 * ![Suggestion chip image](https://developer.android.com/images/reference/androidx/compose/material3/elevated-suggestion-chip.png)
 *
 * This suggestion chip is applied with an elevated style. If you want a flat style, use the
 * [SuggestionChip].
 *
 * Example of an elevated SuggestionChip with a trailing icon:
 * @sample androidx.compose.material3.samples.ElevatedSuggestionChipSample
 *
 * @param onClick called when this chip is clicked
 * @param label text label for this chip
 * @param modifier the [Modifier] to be applied to this chip
 * @param enabled controls the enabled state of this chip. When `false`, this component will not
 * respond to user input, and it will appear visually disabled and disabled to accessibility
 * services.
 * @param icon optional icon at the start of the chip, preceding the [label] text
 * @param shape defines the shape of this chip's container, border (when [border] is not null), and
 * shadow (when using [elevation])
 * @param colors [ChipColors] that will be used to resolve the colors used for this chip in
 * @param elevation [ChipElevation] used to resolve the elevation for this chip in different states.
 * This controls the size of the shadow below the chip. Additionally, when the container color is
 * [ColorScheme.surface], this controls the amount of primary color applied as an overlay. See
 * [Surface] and [SuggestionChipDefaults.elevatedSuggestionChipElevation].
 * @param border the border to draw around the container of this chip
 * different states. See [SuggestionChipDefaults.elevatedSuggestionChipColors].
 * @param interactionSource the [MutableInteractionSource] representing the stream of [Interaction]s
 * for this chip. You can create and pass in your own `remember`ed instance to observe
 * [Interaction]s and customize the appearance / behavior of this chip in different states.
 */
@Composable
fun SmallElevatedSuggestionChip(
    onClick: () -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: @Composable (() -> Unit)? = null,
    shape: Shape = SuggestionChipDefaults.shape,
    colors: ChipColors = SuggestionChipDefaults.elevatedSuggestionChipColors(),
    elevation: ChipElevation? = SuggestionChipDefaults.elevatedSuggestionChipElevation(),
    border: BorderStroke? = null,
    minHeight: Dp = SuggestionChipDefaults.Height,
    paddingValues: PaddingValues = SuggestionChipPadding,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) = Chip(
    modifier = modifier,
    onClick = onClick,
    enabled = enabled,
    label = label,
    labelTextStyle = MaterialTheme.typography.labelLarge,
    labelColor = if (enabled) colors.labelColor else colors.disabledLabelColor,
    leadingIcon = icon,
    trailingIcon = null,
    elevation = elevation,
    colors = colors,
    minHeight = minHeight,
    paddingValues = paddingValues,
    shape = shape,
    border = border,
    interactionSource = interactionSource
)

@Composable
private fun Chip(
    modifier: Modifier,
    onClick: () -> Unit,
    enabled: Boolean,
    label: @Composable () -> Unit,
    labelTextStyle: TextStyle,
    labelColor: Color,
    leadingIcon: @Composable (() -> Unit)?,
    trailingIcon: @Composable (() -> Unit)?,
    shape: Shape,
    colors: ChipColors,
    elevation: ChipElevation?,
    border: BorderStroke?,
    minHeight: Dp,
    paddingValues: PaddingValues,
    interactionSource: MutableInteractionSource,
) {
    Surface(
        onClick = onClick,
        modifier = modifier.semantics { role = Role.Button },
        enabled = enabled,
        shape = shape,
        color = if (enabled) colors.containerColor else colors.disabledContainerColor,
        tonalElevation = elevation?.run { if (enabled) this.elevation else this.disabledElevation }
            ?: 0.dp,
        shadowElevation = elevation?.shadowElevation(enabled, interactionSource)?.value ?: 0.dp,
        border = border,
        interactionSource = interactionSource,
    ) {
        ChipContent(
            label = label,
            labelTextStyle = labelTextStyle,
            labelColor = labelColor,
            leadingIcon = leadingIcon,
            avatar = null,
            trailingIcon = trailingIcon,
            leadingIconColor = if (enabled) colors.leadingIconContentColor else colors.disabledLeadingIconContentColor,
            trailingIconColor = if (enabled) colors.trailingIconContentColor else colors.disabledTrailingIconContentColor,
            minHeight = minHeight,
            paddingValues = paddingValues
        )
    }
}

@Composable
private fun SelectableChip(
    selected: Boolean,
    modifier: Modifier,
    onClick: () -> Unit,
    enabled: Boolean,
    label: @Composable () -> Unit,
    labelTextStyle: TextStyle,
    leadingIcon: @Composable (() -> Unit)?,
    avatar: @Composable (() -> Unit)?,
    trailingIcon: @Composable (() -> Unit)?,
    shape: Shape,
    colors: SelectableChipColors,
    elevation: SelectableChipElevation?,
    border: BorderStroke?,
    minHeight: Dp,
    paddingValues: PaddingValues,
    interactionSource: MutableInteractionSource
) {
    // TODO(b/229794614): Animate transition between unselected and selected.
    Surface(
        selected = selected,
        onClick = onClick,
        modifier = modifier.semantics { role = Role.Checkbox },
        enabled = enabled,
        shape = shape,
        color = with(colors) {
            val target = when {
                !enabled -> if (selected) getValue(SelectableChipColorsHelper.disabledSelectedContainerColor) else getValue(
                    SelectableChipColorsHelper.disabledContainerColor
                )

                !selected -> getValue(SelectableChipColorsHelper.containerColor)
                else -> getValue(SelectableChipColorsHelper.selectedContainerColor)
            }
            rememberUpdatedState(target)
        }.value,
        tonalElevation = elevation?.run { if (enabled) this.elevation else this.disabledElevation }
            ?: 0.dp,
        shadowElevation = elevation?.shadowElevation(enabled, interactionSource)?.value ?: 0.dp,
        border = border,
        interactionSource = interactionSource,
    ) {
        ChipContent(
            label = label,
            labelTextStyle = labelTextStyle,
            leadingIcon = leadingIcon,
            avatar = avatar,
            labelColor = with(colors) {
                when {
                    !enabled -> getValue(SelectableChipColorsHelper.disabledLabelColor)
                    !selected -> getValue(SelectableChipColorsHelper.labelColor)
                    else -> getValue(SelectableChipColorsHelper.selectedLabelColor)
                }
            },
            trailingIcon = trailingIcon,
            leadingIconColor = with(colors) {
                when {
                    !enabled -> getValue(SelectableChipColorsHelper.disabledLeadingIconColor)
                    !selected -> getValue(SelectableChipColorsHelper.leadingIconColor)
                    else -> getValue(SelectableChipColorsHelper.selectedLeadingIconColor)
                }
            },
            trailingIconColor = with(colors) {
                when {
                    !enabled -> getValue(SelectableChipColorsHelper.disabledTrailingIconColor)
                    !selected -> getValue(SelectableChipColorsHelper.trailingIconColor)
                    else -> getValue(SelectableChipColorsHelper.selectedTrailingIconColor)
                }
            },
            minHeight = minHeight,
            paddingValues = paddingValues
        )
    }
}

@Composable
private fun ChipContent(
    label: @Composable () -> Unit,
    labelTextStyle: TextStyle,
    labelColor: Color,
    leadingIcon: @Composable (() -> Unit)?,
    avatar: @Composable (() -> Unit)?,
    trailingIcon: @Composable (() -> Unit)?,
    leadingIconColor: Color,
    trailingIconColor: Color,
    minHeight: Dp,
    paddingValues: PaddingValues
) {
    CompositionLocalProvider(
        LocalContentColor provides labelColor,
        LocalTextStyle provides labelTextStyle
    ) {
        Layout(
            modifier = Modifier
                .defaultMinSize(minHeight = minHeight)
                .padding(paddingValues),
            content = {
                if (avatar != null || leadingIcon != null) {
                    Box(
                        modifier = Modifier
                            .layoutId(LeadingIconLayoutId),
                        contentAlignment = Alignment.Center,
                        content = {
                            if (avatar != null) {
                                avatar()
                            } else if (leadingIcon != null) {
                                CompositionLocalProvider(
                                    LocalContentColor provides leadingIconColor,
                                    content = leadingIcon
                                )
                            }
                        }
                    )
                }
                Row(
                    modifier = Modifier
                        .layoutId(LabelLayoutId)
                        .padding(HorizontalElementsPadding, 0.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    content = { label() }
                )
                if (trailingIcon != null) {
                    Box(
                        modifier = Modifier
                            .layoutId(TrailingIconLayoutId),
                        contentAlignment = Alignment.Center,
                        content = {
                            CompositionLocalProvider(
                                LocalContentColor provides trailingIconColor,
                                content = trailingIcon
                            )
                        }
                    )
                }
            }
        ) { measurables, constraints ->
            val leadingIconPlaceable: Placeable? =
                measurables.fastFirstOrNull { it.layoutId == LeadingIconLayoutId }
                    ?.measure(constraints.copy(minWidth = 0, minHeight = 0))
            val leadingIconWidth = leadingIconPlaceable?.width ?: 0
            val leadingIconHeight = leadingIconPlaceable?.height ?: 0

            val trailingIconPlaceable: Placeable? =
                measurables.fastFirstOrNull { it.layoutId == TrailingIconLayoutId }
                    ?.measure(constraints.copy(minWidth = 0, minHeight = 0))
            val trailingIconWidth = trailingIconPlaceable?.width ?: 0
            val trailingIconHeight = trailingIconPlaceable?.height ?: 0

            val labelPlaceable = measurables.fastFirst { it.layoutId == LabelLayoutId }
                .measure(
                    constraints.offset(horizontal = -(leadingIconWidth + trailingIconWidth))
                )

            val width = leadingIconWidth + labelPlaceable.width + trailingIconWidth
            val height = maxOf(leadingIconHeight, labelPlaceable.height, trailingIconHeight)

            layout(width, height) {
                leadingIconPlaceable?.placeRelative(
                    0,
                    Alignment.CenterVertically.align(leadingIconHeight, height)
                )
                labelPlaceable.placeRelative(leadingIconWidth, 0)
                trailingIconPlaceable?.placeRelative(
                    leadingIconWidth + labelPlaceable.width,
                    Alignment.CenterVertically.align(trailingIconHeight, height)
                )
            }
        }
    }
}

private object SelectableChipColorsHelper {
    /**
     *     private final long containerColor;
     *     private final long labelColor;
     *     private final long leadingIconColor;
     *     private final long trailingIconColor;
     *     private final long disabledContainerColor;
     *     private final long disabledLabelColor;
     *     private final long disabledLeadingIconColor;
     *     private final long disabledTrailingIconColor;
     *     private final long selectedContainerColor;
     *     private final long disabledSelectedContainerColor;
     *     private final long selectedLabelColor;
     *     private final long selectedLeadingIconColor;
     *     private final long selectedTrailingIconColor;
     */
    val containerColor: Field = SelectableChipColors::class.java.getDeclaredField("containerColor")
        .apply { isAccessible = true }

    val labelColor: Field = SelectableChipColors::class.java.getDeclaredField("labelColor")
        .apply { isAccessible = true }

    val leadingIconColor: Field = SelectableChipColors::class.java.getDeclaredField("leadingIconColor")
        .apply { isAccessible = true }

    val trailingIconColor: Field = SelectableChipColors::class.java.getDeclaredField("trailingIconColor")
        .apply { isAccessible = true }

    val disabledContainerColor: Field = SelectableChipColors::class.java.getDeclaredField("disabledContainerColor")
        .apply { isAccessible = true }

    val disabledLabelColor: Field = SelectableChipColors::class.java.getDeclaredField("disabledLabelColor")
        .apply { isAccessible = true }

    val disabledLeadingIconColor: Field = SelectableChipColors::class.java.getDeclaredField("disabledLeadingIconColor")
        .apply { isAccessible = true }

    val disabledTrailingIconColor: Field = SelectableChipColors::class.java.getDeclaredField("disabledTrailingIconColor")
        .apply { isAccessible = true }

    val selectedContainerColor: Field = SelectableChipColors::class.java.getDeclaredField("selectedContainerColor")
        .apply { isAccessible = true }

    val disabledSelectedContainerColor: Field = SelectableChipColors::class.java.getDeclaredField("disabledSelectedContainerColor")
        .apply { isAccessible = true }

    val selectedLabelColor: Field = SelectableChipColors::class.java.getDeclaredField("selectedLabelColor")
        .apply { isAccessible = true }

    val selectedLeadingIconColor: Field = SelectableChipColors::class.java.getDeclaredField("selectedLeadingIconColor")
        .apply { isAccessible = true }

    val selectedTrailingIconColor: Field = SelectableChipColors::class.java.getDeclaredField("selectedTrailingIconColor")
        .apply { isAccessible = true }

    fun SelectableChipColors.getValue(field: Field): Color {
        return try {
            Color(field.getLong(this).toULong())
        } catch (e: ReflectiveOperationException) {
            e.printStackTrace()
            throw e
        }
    }
}

/**
 * Returns the [PaddingValues] for the input chip.
 */
private fun inputChipPadding(
    hasAvatar: Boolean = false,
    hasLeadingIcon: Boolean = false,
    hasTrailingIcon: Boolean = false
): PaddingValues {
    val start = if (hasAvatar || !hasLeadingIcon) 4.dp else 8.dp
    val end = if (hasTrailingIcon) 8.dp else 4.dp
    return PaddingValues(start = start, end = end)
}

/**
 * Represents the shadow elevation used in a chip, depending on its [enabled] state and
 * [interactionSource].
 *
 * Shadow elevation is used to apply a shadow around the chip to give it higher emphasis.
 *
 * See [tonalElevation] which controls the elevation with a color shift to the surface.
 *
 * @param enabled whether the chip is enabled
 * @param interactionSource the [InteractionSource] for this chip
 */
@Composable
private fun ChipElevation.shadowElevation(
    enabled: Boolean,
    interactionSource: InteractionSource
): State<Dp> {
    return animateElevation(enabled = enabled, interactionSource = interactionSource)
}

@Composable
private fun ChipElevation.animateElevation(
    enabled: Boolean,
    interactionSource: InteractionSource
): State<Dp> {
    val interactions = remember { mutableStateListOf<Interaction>() }
    var lastInteraction by remember { mutableStateOf<Interaction?>(null) }
    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is HoverInteraction.Enter -> {
                    interactions.add(interaction)
                }

                is HoverInteraction.Exit -> {
                    interactions.remove(interaction.enter)
                }

                is FocusInteraction.Focus -> {
                    interactions.add(interaction)
                }

                is FocusInteraction.Unfocus -> {
                    interactions.remove(interaction.focus)
                }

                is PressInteraction.Press -> {
                    interactions.add(interaction)
                }

                is PressInteraction.Release -> {
                    interactions.remove(interaction.press)
                }

                is PressInteraction.Cancel -> {
                    interactions.remove(interaction.press)
                }

                is DragInteraction.Start -> {
                    interactions.add(interaction)
                }

                is DragInteraction.Stop -> {
                    interactions.remove(interaction.start)
                }

                is DragInteraction.Cancel -> {
                    interactions.remove(interaction.start)
                }
            }
        }
    }

    val interaction = interactions.lastOrNull()

    val target = if (!enabled) {
        disabledElevation
    } else {
        when (interaction) {
            is PressInteraction.Press -> pressedElevation
            is HoverInteraction.Enter -> hoveredElevation
            is FocusInteraction.Focus -> focusedElevation
            is DragInteraction.Start -> draggedElevation
            else -> elevation
        }
    }

    val animatable = remember { Animatable(target, Dp.VectorConverter) }

    LaunchedEffect(target) {
        if (animatable.targetValue != target) {
            if (!enabled) {
                // No transition when moving to a disabled state
                animatable.snapTo(target)
            } else {
                animatable.animateElevation(
                    from = lastInteraction, to = interaction, target = target
                )
            }
            lastInteraction = interaction
        }
    }

    return animatable.asState()
}

/**
 * Represents the shadow elevation used in a chip, depending on [enabled] and
 * [interactionSource].
 *
 * Shadow elevation is used to apply a shadow around the surface to give it higher emphasis.
 *
 * See [tonalElevation] which controls the elevation with a color shift to the surface.
 *
 * @param enabled whether the chip is enabled
 * @param interactionSource the [InteractionSource] for this chip
 */
@Composable
private fun SelectableChipElevation.shadowElevation(
    enabled: Boolean,
    interactionSource: InteractionSource
): State<Dp> {
    return animateElevation(enabled = enabled, interactionSource = interactionSource)
}

@Composable
private fun SelectableChipElevation.animateElevation(
    enabled: Boolean,
    interactionSource: InteractionSource
): State<Dp> {
    val interactions = remember { mutableStateListOf<Interaction>() }
    var lastInteraction by remember { mutableStateOf<Interaction?>(null) }
    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is HoverInteraction.Enter -> {
                    interactions.add(interaction)
                }

                is HoverInteraction.Exit -> {
                    interactions.remove(interaction.enter)
                }

                is FocusInteraction.Focus -> {
                    interactions.add(interaction)
                }

                is FocusInteraction.Unfocus -> {
                    interactions.remove(interaction.focus)
                }

                is PressInteraction.Press -> {
                    interactions.add(interaction)
                }

                is PressInteraction.Release -> {
                    interactions.remove(interaction.press)
                }

                is PressInteraction.Cancel -> {
                    interactions.remove(interaction.press)
                }

                is DragInteraction.Start -> {
                    interactions.add(interaction)
                }

                is DragInteraction.Stop -> {
                    interactions.remove(interaction.start)
                }

                is DragInteraction.Cancel -> {
                    interactions.remove(interaction.start)
                }
            }
        }
    }

    val interaction = interactions.lastOrNull()

    val target = if (!enabled) {
        disabledElevation
    } else {
        when (interaction) {
            is PressInteraction.Press -> pressedElevation
            is HoverInteraction.Enter -> hoveredElevation
            is FocusInteraction.Focus -> focusedElevation
            is DragInteraction.Start -> draggedElevation
            else -> elevation
        }
    }

    val animatable = remember { Animatable(target, Dp.VectorConverter) }

    LaunchedEffect(target) {
        if (animatable.targetValue != target) {
            if (!enabled) {
                // No transition when moving to a disabled state
                animatable.snapTo(target)
            } else {
                animatable.animateElevation(
                    from = lastInteraction, to = interaction, target = target
                )
            }
            lastInteraction = interaction
        }
    }

    return animatable.asState()
}

/**
 * The padding between the elements in the chip.
 */
private val HorizontalElementsPadding = 8.dp

/**
 * Returns the [PaddingValues] for the assist chip.
 */
private val AssistChipPadding = PaddingValues(horizontal = HorizontalElementsPadding)

/**
 * [PaddingValues] for the filter chip.
 */
private val FilterChipPadding = PaddingValues(horizontal = HorizontalElementsPadding)

/**
 * Returns the [PaddingValues] for the suggestion chip.
 */
private val SuggestionChipPadding = PaddingValues(horizontal = HorizontalElementsPadding)

private const val LeadingIconLayoutId = "leadingIcon"
private const val LabelLayoutId = "label"
private const val TrailingIconLayoutId = "trailingIcon"
