package com.hym.zhankucompose.compose

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hym.zhankucompose.compose.ButtonHelper.getValue
import com.hym.zhankucompose.compose.ButtonHelper.tonalElevation
import java.lang.reflect.Field

/**
 * @author hehua2008
 * @date 2024/3/13
 */

/**
 * <a href="https://m3.material.io/components/buttons/overview" class="external" target="_blank">Material Design button</a>.
 *
 * Buttons help people initiate actions, from sending an email, to sharing a document, to liking a
 * post.
 *
 * ![Filled button image](https://developer.android.com/images/reference/androidx/compose/material3/filled-button.png)
 *
 * Filled buttons are high-emphasis buttons. Filled buttons have the most visual impact after the
 * [FloatingActionButton], and should be used for important, final actions that complete a flow,
 * like "Save", "Join now", or "Confirm".
 *
 * @sample androidx.compose.material3.samples.ButtonSample
 * @sample androidx.compose.material3.samples.ButtonWithIconSample
 *
 * Choose the best button for an action based on the amount of emphasis it needs. The more important
 * an action is, the higher emphasis its button should be.
 *
 * - See [OutlinedButton] for a medium-emphasis button with a border.
 * - See [ElevatedButton] for an [OutlinedButton] with a shadow.
 * - See [TextButton] for a low-emphasis button with no border.
 * - See [FilledTonalButton] for a middle ground between [OutlinedButton] and [Button].
 *
 * The default text style for internal [Text] components will be set to [Typography.labelLarge].
 *
 * @param onClick called when this button is clicked
 * @param modifier the [Modifier] to be applied to this button
 * @param enabled controls the enabled state of this button. When `false`, this component will not
 * respond to user input, and it will appear visually disabled and disabled to accessibility
 * services.
 * @param shape defines the shape of this button's container, border (when [border] is not null),
 * and shadow (when using [elevation])
 * @param colors [ButtonColors] that will be used to resolve the colors for this button in different
 * states. See [ButtonDefaults.buttonColors].
 * @param elevation [ButtonElevation] used to resolve the elevation for this button in different
 * states. This controls the size of the shadow below the button. Additionally, when the container
 * color is [ColorScheme.surface], this controls the amount of primary color applied as an overlay.
 * See [ButtonElevation.shadowElevation] and [ButtonElevation.tonalElevation].
 * @param border the border to draw around the container of this button
 * @param contentPadding the spacing values to apply internally between the container and the
 * content
 * @param interactionSource the [MutableInteractionSource] representing the stream of [Interaction]s
 * for this button. You can create and pass in your own `remember`ed instance to observe
 * [Interaction]s and customize the appearance / behavior of this button in different states.
 */
/**
 * A Button that can set the minimum width and minimum height.
 */
@Composable
fun SmallButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = ButtonDefaults.shape,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
    border: BorderStroke? = null,
    contentPadding: PaddingValues = SmallButtonPaddingValues, //ButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    minWidth: Dp = 0.dp, //ButtonDefaults.MinWidth,
    minHeight: Dp = 0.dp, //ButtonDefaults.MinHeight,
    content: @Composable RowScope.() -> Unit
) {
    val containerColor = colors.run {
        if (enabled) containerColor else disabledContainerColor
    }
    val contentColor = colors.run {
        if (enabled) contentColor else disabledContentColor
    }
    val shadowElevation = elevation?.shadowElevation(enabled, interactionSource)?.value ?: 0.dp
    val tonalElevation = elevation?.tonalElevation(enabled)?.dp ?: 0.dp
    Surface(
        onClick = onClick,
        modifier = modifier.semantics { role = Role.Button },
        enabled = enabled,
        shape = shape,
        color = containerColor,
        contentColor = contentColor,
        tonalElevation = tonalElevation,
        shadowElevation = shadowElevation,
        border = border,
        interactionSource = interactionSource
    ) {
        val textStyle = MaterialTheme.typography.labelLarge
        val mergedStyle = LocalTextStyle.current.merge(textStyle)
        CompositionLocalProvider(
            LocalContentColor provides contentColor,
            LocalTextStyle provides mergedStyle
        ) {
            Row(
                Modifier
                    .defaultMinSize(
                        minWidth = minWidth,
                        minHeight = minHeight
                    )
                    .padding(contentPadding),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                content = content
            )
        }
    }
}

/**
 * Represents the shadow elevation used in a button, depending on its [enabled] state and
 * [interactionSource].
 *
 * Shadow elevation is used to apply a shadow around the button to give it higher emphasis.
 *
 * See [tonalElevation] which controls the elevation with a color shift to the surface.
 *
 * @param enabled whether the button is enabled
 * @param interactionSource the [InteractionSource] for this button
 */
@Composable
private fun ButtonElevation.shadowElevation(
    enabled: Boolean,
    interactionSource: InteractionSource
): State<Dp> {
    return animateElevation(enabled = enabled, interactionSource = interactionSource)
}

@Composable
private fun ButtonElevation.animateElevation(
    enabled: Boolean,
    interactionSource: InteractionSource
): State<Dp> {
    val interactions = remember { mutableStateListOf<Interaction>() }
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
            }
        }
    }

    val interaction = interactions.lastOrNull()

    val target =
        if (!enabled) {
            getValue(ButtonHelper.disabledElevation)
        } else {
            when (interaction) {
                is PressInteraction.Press -> getValue(ButtonHelper.pressedElevation)
                is HoverInteraction.Enter -> getValue(ButtonHelper.hoveredElevation)
                is FocusInteraction.Focus -> getValue(ButtonHelper.focusedElevation)
                else -> getValue(ButtonHelper.defaultElevation)
            }
        }

    val animatable = remember { Animatable(target, Dp.VectorConverter) }

    LaunchedEffect(target) {
        if (animatable.targetValue != target) {
            if (!enabled) {
                // No transition when moving to a disabled state
                animatable.snapTo(target)
            } else {
                val lastInteraction = when (animatable.targetValue) {
                    getValue(ButtonHelper.pressedElevation) -> PressInteraction.Press(Offset.Zero)
                    getValue(ButtonHelper.hoveredElevation) -> HoverInteraction.Enter()
                    getValue(ButtonHelper.focusedElevation) -> FocusInteraction.Focus()
                    else -> null
                }
                animatable.animateElevation(
                    from = lastInteraction,
                    to = interaction,
                    target = target
                )
            }
        }
    }

    return animatable.asState()
}

private object ButtonHelper {
    /**
     * private final float defaultElevation;
     * private final float disabledElevation;
     * private final float focusedElevation;
     * private final float hoveredElevation;
     * private final float pressedElevation;
     */
    val defaultElevation: Field = ButtonElevation::class.java.getDeclaredField("defaultElevation")
        .apply { isAccessible = true }

    val pressedElevation: Field = ButtonElevation::class.java.getDeclaredField("pressedElevation")
        .apply { isAccessible = true }

    val focusedElevation: Field = ButtonElevation::class.java.getDeclaredField("focusedElevation")
        .apply { isAccessible = true }

    val hoveredElevation: Field = ButtonElevation::class.java.getDeclaredField("hoveredElevation")
        .apply { isAccessible = true }

    val disabledElevation: Field = ButtonElevation::class.java.getDeclaredField("disabledElevation")
        .apply { isAccessible = true }

    fun ButtonElevation.getValue(field: Field): Dp {
        return try {
            field.getFloat(this).dp
        } catch (e: ReflectiveOperationException) {
            e.printStackTrace()
            throw e
        }
    }

    /**
     * public final float m1622tonalElevationu2uoSUM$material3_release(boolean enabled) {
     * return enabled ? this.defaultElevation : this.disabledElevation;
     * }
     */
    fun ButtonElevation.tonalElevation(enabled: Boolean): Float {
        return try {
            if (enabled) defaultElevation.getFloat(this) else disabledElevation.getFloat(this)
        } catch (e: ReflectiveOperationException) {
            e.printStackTrace()
            0f
        }
    }
}

val SmallButtonPaddingValues = PaddingValues(8.dp)
