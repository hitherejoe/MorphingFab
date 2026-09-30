package cards.sleevd.morphingfab.sample.demos

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import cards.sleevd.morphingfab.MorphingFab
import cards.sleevd.morphingfab.MorphingFabAnchor
import cards.sleevd.morphingfab.MorphingFabPlacement
import cards.sleevd.morphingfab.MorphingFabTransform
import cards.sleevd.morphingfab.sample.DemoScaffold
import cards.sleevd.morphingfab.sample.LocalMorphSpec
import cards.sleevd.morphingfab.sample.SampleIcons
import kotlin.math.hypot
import kotlin.math.max
import kotlinx.coroutines.launch

/**
 * One FAB with two jobs, which is what [MorphingFabTransform] exists for.
 *
 * While there's quota left a tap navigates: the FAB shrinks and rounds into a dot, and a
 * destination circular-reveals out of that same dot. When the quota runs out the identical tap
 * morphs the FAB into a dialog instead. Both readings share one resting surface, so the button
 * never blinks between them.
 *
 * The library owns none of this. It exposes three floats — scale, corner circularity, content alpha
 * — and the reveal, its timing and the decision about which branch to take all live here. A
 * `MorphingFabPlacement.Anchored` is used so the demo knows the FAB's window bounds and can
 * originate the reveal from its centre.
 */
@Composable
internal fun LaunchMorphDemo(onBack: () -> Unit) {
    val scope = rememberCoroutineScope()

    var remaining by remember { mutableIntStateOf(2) }
    var expanded by remember { mutableStateOf(false) }
    var destinationOpen by remember { mutableStateOf(false) }
    var fabBounds by remember { mutableStateOf<Rect?>(null) }
    var rootOrigin by remember { mutableStateOf(Offset.Zero) }
    var rootSize by remember { mutableStateOf(Size.Zero) }

    // 0 is the FAB at rest, 1 is a fully revealed destination. Drives the shrink and the reveal
    // together, which is the whole trick: two effects, one clock.
    val launch = remember { Animatable(0f) }

    val revealCentre =
        fabBounds?.center?.let { Offset(it.x - rootOrigin.x, it.y - rootOrigin.y) } ?: Offset.Zero

    Box(
        modifier =
            Modifier.fillMaxSize().onGloballyPositioned {
                rootOrigin = it.positionInWindow()
                rootSize = it.size.toSize()
            }
    ) {
        DemoScaffold(
            title = "Launch morph",
            explanation =
                "Two taps navigate, the third finds the quota spent and opens the dialog instead.",
            onBack = onBack,
            floatingActionButton = { MorphingFabAnchor(onBoundsChanged = { fabBounds = it }) },
        ) {
            Text(
                text = if (remaining > 0) "$remaining binders left" else "No binders left",
                style = MaterialTheme.typography.displaySmall,
            )
        }

        MorphingFab(
            expanded = expanded,
            onFabClick = {
                if (remaining > 0) {
                    remaining--
                    destinationOpen = true
                    scope.launch { launch.animateTo(1f, tween(400, easing = FastOutSlowInEasing)) }
                } else {
                    expanded = true
                }
            },
            onDismissRequest = { expanded = false },
            title = "Out of binders",
            text =
                "The free tier stops at two. This dialog and the reveal you just saw are the " +
                    "same button, taking different branches on the same tap.",
            confirmLabel = "Reset quota",
            onConfirm = {
                remaining = 2
                expanded = false
            },
            dismissLabel = "Cancel",
            icon = {
                Icon(
                    imageVector = SampleIcons.Lock,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
            },
            placement = MorphingFabPlacement.Anchored(fabBounds),
            animationSpec = LocalMorphSpec.current,
            // Only meaningful while the dialog morph is at 0, which is exactly when a launch runs.
            fabTransform =
                MorphingFabTransform(
                    scale = 1f - 0.7f * launch.value,
                    cornerCircularity = launch.value,
                    contentAlpha = 1f - launch.value * 2f,
                ),
            fabContent = {
                Icon(
                    imageVector = SampleIcons.Add,
                    contentDescription = "New binder",
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
            },
        )

        // Above the FAB, so the growing circle swallows the dot rather than orbiting it.
        if (destinationOpen) {
            val maxRadius = farthestCorner(revealCentre, rootSize)

            Box(
                modifier =
                    Modifier.fillMaxSize()
                        .graphicsLayer {
                            clip = true
                            shape = CircularRevealShape(revealCentre, maxRadius * launch.value)
                        }
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .pointerInput(Unit) { detectTapGestures {} }
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(32.dp).alpha(launch.value),
                    verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "New binder",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                    Text(
                        text =
                            "This screen grew out of the FAB, which had already shrunk to a dot " +
                                "under it. Nothing crossfaded.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        textAlign = TextAlign.Center,
                    )
                    Button(
                        onClick = {
                            scope.launch {
                                launch.animateTo(0f, tween(300, easing = FastOutSlowInEasing))
                                destinationOpen = false
                            }
                        }
                    ) {
                        Text("Back")
                    }
                }
            }
        }
    }
}

/** Distance from [centre] to whichever corner of a [size]-sized box is furthest away. */
private fun farthestCorner(centre: Offset, size: Size): Float =
    max(
        max(hypot(centre.x, centre.y), hypot(size.width - centre.x, centre.y)),
        max(
            hypot(centre.x, size.height - centre.y),
            hypot(size.width - centre.x, size.height - centre.y),
        ),
    )

private class CircularRevealShape(private val centre: Offset, private val radius: Float) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline = Outline.Generic(Path().apply { addOval(Rect(centre, radius)) })
}
