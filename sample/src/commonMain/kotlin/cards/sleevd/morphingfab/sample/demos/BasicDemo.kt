package cards.sleevd.morphingfab.sample.demos

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cards.sleevd.morphingfab.MorphingFab
import cards.sleevd.morphingfab.MorphingFabPlacement
import cards.sleevd.morphingfab.sample.DemoScaffold
import cards.sleevd.morphingfab.sample.LocalMorphSpec
import cards.sleevd.morphingfab.sample.SampleIcons

/**
 * The shortest thing that can be written with this library: the convenience overload, resting in
 * the bottom-end corner.
 *
 * Note `MorphingFab` is a **sibling** of the `Scaffold`, not a child — it `fillMaxSize()`s so the
 * morph can travel anywhere on screen, and a full-screen composable in `Scaffold`'s
 * `floatingActionButton` slot ends up stranded in the middle of the screen. The `Anchored` demo
 * shows the way round that.
 */
@Composable
internal fun BasicDemo(onBack: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var remaining by remember { mutableIntStateOf(12) }

    Box(Modifier.fillMaxSize()) {
        DemoScaffold(
            title = "Basics",
            explanation =
                "Tap the button. One Surface travels from the corner to the centre, growing, " +
                    "rounding off and shifting from primary to surfaceContainerHigh as it goes. " +
                    "Dismiss with the scrim, the system back gesture, or either action.",
            onBack = onBack,
        ) {
            Text(
                text = if (remaining > 0) "$remaining cards" else "Nothing left",
                style = MaterialTheme.typography.displaySmall,
            )
        }

        MorphingFab(
            expanded = expanded,
            onFabClick = { expanded = true },
            onDismissRequest = { expanded = false },
            title = "Delete $remaining cards?",
            text =
                "They'll be removed from this binder. Anything you've marked as a grail stays " +
                    "in your collection.",
            confirmLabel = "Delete",
            onConfirm = {
                remaining = 0
                expanded = false
            },
            dismissLabel = "Cancel",
            icon = {
                Icon(
                    imageVector = SampleIcons.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                )
            },
            placement = MorphingFabPlacement.BottomEnd(),
            animationSpec = LocalMorphSpec.current,
            fabContent = {
                Icon(
                    imageVector = SampleIcons.Delete,
                    contentDescription = "Delete cards",
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
            },
        )
    }
}
