package cards.sleevd.morphingfab.sample.demos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import cards.sleevd.morphingfab.MorphingFab
import cards.sleevd.morphingfab.MorphingFabDefaults
import cards.sleevd.morphingfab.MorphingFabPlacement
import cards.sleevd.morphingfab.MorphingFabStyle
import cards.sleevd.morphingfab.sample.DemoScaffold
import cards.sleevd.morphingfab.sample.LocalMorphSpec
import cards.sleevd.morphingfab.sample.SampleIcons
import kotlin.math.roundToInt

/**
 * `MorphingFabStyle` and `MorphingFabColors` wired to live controls, so the effect of each knob is
 * visible on the next tap.
 *
 * Both are plain data classes read during composition, so changing one mid-morph takes effect on
 * the following frame — nothing is captured when the animation starts except the `animationSpec`.
 *
 * The two most interesting sliders are the crossfade thresholds. Drag `dialogContentFadeInFrom`
 * down to 0 and the dialog's text renders inside a FAB-sized box for the first half of the morph,
 * which is precisely the artefact the default of 0.55 exists to avoid.
 */
@Composable
internal fun ThemingDemo(onBack: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var palette by remember { mutableStateOf(Palette.Primary) }
    var fabCorner by remember { mutableFloatStateOf(16f) }
    var scrimAlpha by remember { mutableFloatStateOf(0.32f) }
    var fadeInFrom by remember { mutableFloatStateOf(0.55f) }
    var tall by remember { mutableStateOf(false) }

    val colors =
        when (palette) {
            Palette.Primary -> MorphingFabDefaults.colors()
            Palette.Tertiary ->
                MorphingFabDefaults.colors(
                    fabContainer = MaterialTheme.colorScheme.tertiary,
                    dialogContainer = MaterialTheme.colorScheme.tertiaryContainer,
                )
            Palette.Error ->
                MorphingFabDefaults.colors(
                    fabContainer = MaterialTheme.colorScheme.error,
                    dialogContainer = MaterialTheme.colorScheme.errorContainer,
                    titleContent = MaterialTheme.colorScheme.onErrorContainer,
                    textContent = MaterialTheme.colorScheme.onErrorContainer,
                )
        }

    Box(Modifier.fillMaxSize()) {
        DemoScaffold(
            title = "Style and colours",
            explanation =
                "Every value below is read while the morph runs, so a change lands on the next " +
                    "frame rather than the next tap.",
            onBack = onBack,
        ) {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Palette.entries.forEach { option ->
                        Pill(
                            label = option.label,
                            selected = option == palette,
                            onClick = { palette = option },
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Pill(label = "320 x 280", selected = !tall, onClick = { tall = false })
                    Pill(label = "320 x 420", selected = tall, onClick = { tall = true })
                }

                Knob(
                    label = "fabCorner",
                    value = "${fabCorner.roundToInt()}dp",
                    sliderValue = fabCorner,
                    onValueChange = { fabCorner = it },
                    range = 0f..28f,
                )

                Knob(
                    label = "scrimAlpha",
                    value = scrimAlpha.format(),
                    sliderValue = scrimAlpha,
                    onValueChange = { scrimAlpha = it },
                    range = 0f..1f,
                )

                Knob(
                    label = "dialogContentFadeInFrom",
                    value = fadeInFrom.format(),
                    sliderValue = fadeInFrom,
                    onValueChange = { fadeInFrom = it },
                    range = 0f..0.9f,
                )
            }
        }

        MorphingFab(
            expanded = expanded,
            onFabClick = { expanded = true },
            onDismissRequest = { expanded = false },
            title = "Styled to taste",
            text =
                "The surface interpolates between the two container colours as it grows, so the " +
                    "further apart they are the more the morph reads as one object changing role.",
            confirmLabel = "Nice",
            onConfirm = { expanded = false },
            dismissLabel = "Close",
            placement = MorphingFabPlacement.BottomEnd(),
            style =
                MorphingFabStyle(
                    fabCorner = fabCorner.dp,
                    scrimAlpha = scrimAlpha,
                    dialogContentFadeInFrom = fadeInFrom,
                    dialogSize = if (tall) DpSize(320.dp, 420.dp) else DpSize(320.dp, 280.dp),
                ),
            colors = colors,
            animationSpec = LocalMorphSpec.current,
            fabContent = {
                Icon(
                    imageVector = SampleIcons.Tune,
                    contentDescription = "Open styled dialog",
                    tint = MaterialTheme.colorScheme.surface,
                )
            },
        )
    }
}

@Composable
private fun Knob(
    label: String,
    value: String,
    sliderValue: Float,
    onValueChange: (Float) -> Unit,
    range: ClosedFloatingPointRange<Float>,
) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.labelLarge)
            Text(
                text = value,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        Slider(value = sliderValue, onValueChange = onValueChange, valueRange = range)
    }
}

@Composable
private fun Pill(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color =
            if (selected) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = Modifier.selectable(selected = selected, onClick = onClick),
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(label, style = MaterialTheme.typography.labelLarge)
        }
    }
}

private fun Float.format(): String = ((this * 100).roundToInt() / 100f).toString()

private enum class Palette(val label: String) {
    Primary("Primary"),
    Tertiary("Tertiary"),
    Error("Error"),
}
