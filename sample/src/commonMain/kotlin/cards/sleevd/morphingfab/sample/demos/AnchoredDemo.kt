package cards.sleevd.morphingfab.sample.demos

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import cards.sleevd.morphingfab.MorphingFab
import cards.sleevd.morphingfab.MorphingFabAnchor
import cards.sleevd.morphingfab.MorphingFabPlacement
import cards.sleevd.morphingfab.sample.DemoScaffold
import cards.sleevd.morphingfab.sample.LocalMorphSpec
import cards.sleevd.morphingfab.sample.SampleIcons

/**
 * `MorphingFabPlacement.Anchored`, for when something else owns where the FAB sits.
 *
 * `MorphingFab` can't go in `Scaffold`'s `floatingActionButton` slot: it `fillMaxSize()`s, and
 * `Scaffold` measures that slot loosely and then lifts it by its own measured height, which parks
 * the resting FAB somewhere around the middle of the screen. `MorphingFabAnchor` goes in the slot
 * instead — it's an empty box of the right size that reports its window bounds — and `MorphingFab`
 * renders at the root and morphs from there.
 *
 * The bottom bar is here to show it works: Scaffold has already lifted the anchor clear of it, so
 * there's no inset to pass and nothing to keep in sync by hand.
 */
@Composable
internal fun AnchoredDemo(onBack: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableIntStateOf(0) }
    var fabBounds by remember { mutableStateOf<Rect?>(null) }

    Box(Modifier.fillMaxSize()) {
        DemoScaffold(
            title = "Scaffold anchor",
            explanation =
                "The FAB is positioned by Scaffold, not by the library. MorphingFabAnchor sits " +
                    "in the floatingActionButton slot and reports its bounds; the morph starts " +
                    "from exactly there.",
            onBack = onBack,
            bottomBar = {
                NavigationBar {
                    listOf("Home" to SampleIcons.Home, "Search" to SampleIcons.Search)
                        .forEachIndexed { index, (label, icon) ->
                            NavigationBarItem(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                icon = { Icon(icon, contentDescription = null) },
                                label = { Text(label) },
                            )
                        }
                }
            },
            floatingActionButton = { MorphingFabAnchor(onBoundsChanged = { fabBounds = it }) },
        ) {
            Text(
                text = "Anchored to the Scaffold slot",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        MorphingFab(
            expanded = expanded,
            onFabClick = { expanded = true },
            onDismissRequest = { expanded = false },
            title = "New binder",
            text =
                "The morph started from the anchor's bounds, which Scaffold had already lifted " +
                    "above the navigation bar.",
            confirmLabel = "Create",
            onConfirm = { expanded = false },
            dismissLabel = "Cancel",
            placement = MorphingFabPlacement.Anchored(fabBounds),
            animationSpec = LocalMorphSpec.current,
            fabContent = {
                Icon(
                    imageVector = SampleIcons.Add,
                    contentDescription = "New binder",
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
            },
        )
    }
}
