package cards.sleevd.morphingfab

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

/** Where [MorphingFab] should draw its collapsed FAB. */
@Immutable
sealed interface MorphingFabPlacement {

    /**
     * The FAB rests wherever [MorphingFabAnchor] was placed — use this when something else owns the
     * position, most commonly `Scaffold`'s `floatingActionButton` slot.
     *
     * [bounds] must be in **window** coordinates, i.e. straight from `boundsInWindow()`, which is
     * what [MorphingFabAnchor] reports. Nothing is drawn while it is `null`.
     */
    @Immutable data class Anchored(val bounds: Rect?) : MorphingFabPlacement

    /**
     * The FAB rests in the bottom-end corner of the space [MorphingFab] occupies, inset by
     * [edgePadding] and clear of the system navigation bar.
     *
     * [MorphingFab] `fillMaxSize()`s to find that corner, **so it must never go in `Scaffold`'s
     * `floatingActionButton` slot with this placement** — use [Anchored] there.
     *
     * @param edgePadding Gap between the FAB and the end/bottom edges.
     * @param bottomInset Clearance for a floating bottom bar. Taken as the *larger* of this and the
     *   navigation bar inset rather than added to it, since such a bar normally applies its own
     *   `navigationBarsPadding()` and already subsumes it.
     */
    @Immutable
    data class BottomEnd(val edgePadding: Dp = 16.dp, val bottomInset: Dp = 0.dp) :
        MorphingFabPlacement
}

/**
 * The geometry of the morph: what the FAB and the dialog each measure, and when content crosses
 * over between them.
 *
 * The two alpha thresholds are why the morph doesn't look like a box with text in it — the FAB's
 * icon is gone within the first third and the dialog's content doesn't appear until the surface is
 * more than half grown, so text is never rendered inside a FAB-sized box.
 */
@Immutable
data class MorphingFabStyle(
    /** Side length of the collapsed FAB. Only used by [MorphingFabPlacement.BottomEnd]. */
    val fabSize: Dp = 56.dp,
    /** Corner radius of the collapsed FAB. */
    val fabCorner: Dp = 16.dp,
    /** Corner radius of the expanded dialog. */
    val dialogCorner: Dp = 28.dp,
    /** Size of the expanded dialog, clamped to the window less [dialogMinMargin] on each side. */
    val dialogSize: DpSize = DpSize(320.dp, 280.dp),
    /** Minimum gap between the expanded dialog and the window edges. */
    val dialogMinMargin: Dp = 24.dp,
    /** Padding inside the expanded dialog. */
    val dialogPadding: Dp = 24.dp,
    /** Vertical gap between items in the expanded dialog's column. */
    val dialogSpacing: Dp = 16.dp,
    /** `tonalElevation` of the morphing surface, in both states. */
    val tonalElevation: Dp = 6.dp,
    /** Peak opacity of the scrim, reached at full expansion. */
    val scrimAlpha: Float = 0.32f,
    /** Progress at which the collapsed FAB's content has finished fading out. */
    val fabContentFadeOutBy: Float = 1f / 3f,
    /** Progress at which the expanded dialog's content starts fading in. */
    val dialogContentFadeInFrom: Float = 0.55f,
)

/** Colours for [MorphingFab]. */
@Immutable
data class MorphingFabColors(
    /** Container colour at rest. Interpolated toward [dialogContainer] as the dialog grows. */
    val fabContainer: Color,
    /** Container colour when fully expanded. */
    val dialogContainer: Color,
    /**
     * Scrim behind the expanded dialog, at full opacity — [MorphingFabStyle.scrimAlpha] is applied
     * on top of whatever alpha this already carries.
     */
    val scrim: Color,
    /** Title colour, used by the convenience [MorphingFab] overload. */
    val titleContent: Color,
    /** Body text colour, used by the convenience [MorphingFab] overload. */
    val textContent: Color,
)

/**
 * A secondary transform applied to the **collapsed** FAB only, on top of the dialog morph.
 *
 * The motivating case is a **launch morph**: the FAB doubles as a shared element, and on the taps
 * that navigate rather than open the dialog it shrinks and rounds into a small circle that the
 * destination's circular reveal explodes from — reading as shrink → dot → explode with no seam.
 *
 * Three plain floats rather than a feature of [MorphingFab], because the library has no business
 * knowing when you navigate. Drive them from one `Animatable` of your own:
 * ```kotlin
 * val launch = remember { Animatable(0f) }
 *
 * MorphingFab(
 *     onFabClick = {
 *         navigateToDestination()
 *         scope.launch { launch.animateTo(1f, tween(300, easing = FastOutSlowInEasing)) }
 *     },
 *     fabTransform =
 *         MorphingFabTransform(
 *             scale = 1f - 0.7f * launch.value,
 *             cornerCircularity = launch.value,
 *             contentAlpha = 1f - launch.value * 2f,
 *         ),
 *     …
 * )
 * ```
 *
 * All three only apply while the dialog morph is at 0, which is why there is no colour term: the
 * surface is already [MorphingFabColors.fabContainer] at that point.
 *
 * @param scale Scales the surface about its own centre. `1f` leaves it alone.
 * @param cornerCircularity Rounds the corners toward a full circle (half the current height). `0f`
 *   leaves [MorphingFabStyle.fabCorner] alone, `1f` is a circle.
 * @param contentAlpha Multiplies the alpha of the `fabContent` slot. `1f` leaves it alone.
 */
@Immutable
data class MorphingFabTransform(
    val scale: Float = 1f,
    val cornerCircularity: Float = 0f,
    val contentAlpha: Float = 1f,
) {
    companion object {
        /** No secondary transform — the FAB is just a FAB. */
        val None: MorphingFabTransform = MorphingFabTransform()
    }
}

/** Defaults for [MorphingFab]. */
object MorphingFabDefaults {

    /** Side length of a standard FAB, and of [MorphingFabAnchor]. */
    val FabSize: Dp = 56.dp

    /**
     * 350ms is long enough to read as one object travelling rather than a crossfade, and
     * [FastOutSlowInEasing] stops the surface appearing to overshoot as it grows ~6x in each axis.
     */
    val MorphSpec: AnimationSpec<Float> = tween(durationMillis = 350, easing = FastOutSlowInEasing)

    /**
     * Material 3 colours, with the FAB on `primary` and the dialog on `surfaceContainerHigh`.
     *
     * Not `primaryContainer` for the FAB: the morph is spending the contrast between the two, so it
     * looks best when the collapsed state is the strongest accent on the screen.
     */
    @Composable
    @ReadOnlyComposable
    fun colors(
        fabContainer: Color = MaterialTheme.colorScheme.primary,
        dialogContainer: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
        scrim: Color = MaterialTheme.colorScheme.scrim,
        titleContent: Color = MaterialTheme.colorScheme.onSurface,
        textContent: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    ): MorphingFabColors =
        MorphingFabColors(
            fabContainer = fabContainer,
            dialogContainer = dialogContainer,
            scrim = scrim,
            titleContent = titleContent,
            textContent = textContent,
        )
}
