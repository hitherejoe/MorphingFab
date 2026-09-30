/**
 * A floating action button that morphs into an alert dialog: one `Surface` is both states, with a
 * single progress value driving its position, size, corner radius, colour and content alpha.
 *
 * The expanded state draws in a [WindowOverlay] so the scrim covers the window; the resting FAB
 * draws in the composable tree, because a full-screen popup would swallow every touch on the
 * screen. The two therefore use different coordinate spaces and the seam is only invisible if both
 * land on the same pixel — a constant error shows up as a snap on the way back, not on the way out.
 * See `README.md` and the iOS `actual` of [windowOverlayPopupProperties].
 */
package cards.sleevd.morphingfab

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * An invisible placeholder that reports where the FAB should be drawn, for use with
 * [MorphingFabPlacement.Anchored].
 *
 * [MorphingFab] itself can't go in a `Scaffold`'s `floatingActionButton` slot — it
 * `fillMaxSize()`s, and `Scaffold` measures that slot loosely then lifts it by its own measured
 * height, stranding the resting FAB near the middle of the screen. Put this in the slot instead.
 *
 * ```kotlin
 * var fabBounds by remember { mutableStateOf<Rect?>(null) }
 *
 * Scaffold(floatingActionButton = { MorphingFabAnchor(onBoundsChanged = { fabBounds = it }) }) { … }
 *
 * MorphingFab(placement = MorphingFabPlacement.Anchored(fabBounds), …)
 * ```
 *
 * @param onBoundsChanged Receives the anchor's bounds in **window** coordinates.
 */
@Composable
fun MorphingFabAnchor(
    onBoundsChanged: (Rect) -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = MorphingFabDefaults.FabSize,
) {
    Box(modifier.size(size).onGloballyPositioned { onBoundsChanged(it.boundsInWindow()) })
}

/**
 * A FAB that morphs into an alert dialog, with the dialog's body supplied as a slot.
 *
 * Place this at the **root of the screen**, as a sibling of the screen's content, so the morph can
 * travel anywhere on screen. While collapsed only a FAB-sized surface is drawn and the rest of the
 * component is touch-transparent.
 *
 * @param expanded Whether the dialog is showing. Hoisted — [MorphingFab] never flips it itself, so
 *   the caller stays free to run a tier check, a quota check or anything else between the tap and
 *   the dialog.
 * @param onFabClick Called when the collapsed FAB is tapped.
 * @param onDismissRequest Called on scrim tap and system back. Note taps on the expanded surface
 *   are swallowed rather than dismissing.
 * @param placement Where the collapsed FAB rests. See [MorphingFabPlacement].
 * @param animationSpec Timing of the morph. Pass `snap()` to honour a reduced-motion preference.
 * @param fabTransform A secondary transform on the collapsed FAB only. See [MorphingFabTransform].
 * @param fabContent Content of the collapsed FAB, centred. Usually a single `Icon`; tint it
 *   yourself, since the library has no opinion on what goes in a FAB and deliberately ships no icon
 *   dependency.
 * @param dialogContent Body of the expanded dialog, laid out in a centre-aligned [Column] with
 *   [MorphingFabStyle.dialogPadding] around it and [MorphingFabStyle.dialogSpacing] between items.
 *   `Spacer(Modifier.weight(1f))` works as you'd expect — use it to push an action row to the
 *   bottom.
 */
@Composable
fun MorphingFab(
    expanded: Boolean,
    onFabClick: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    placement: MorphingFabPlacement = MorphingFabPlacement.BottomEnd(),
    style: MorphingFabStyle = MorphingFabStyle(),
    colors: MorphingFabColors = MorphingFabDefaults.colors(),
    animationSpec: AnimationSpec<Float> = MorphingFabDefaults.MorphSpec,
    fabTransform: MorphingFabTransform = MorphingFabTransform.None,
    fabContent: @Composable () -> Unit,
    dialogContent: @Composable ColumnScope.() -> Unit,
) {
    BackHandler(enabled = expanded, onBack = onDismissRequest)

    // Keyed on `expanded` alone: animationSpec is read when the animation starts, which is exactly
    // when a change to it should take effect.
    val progress = remember { Animatable(0f) }
    LaunchedEffect(expanded) {
        progress.animateTo(targetValue = if (expanded) 1f else 0f, animationSpec = animationSpec)
    }
    val p = progress.value

    val density = LocalDensity.current
    val navigationBarInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    // Tracked so rects can be converted between window space (the overlay) and local space (tree).
    var origin by remember { mutableStateOf(Offset.Zero) }
    var boxSize by remember { mutableStateOf(Offset.Zero) }
    val measured = boxSize.x > 0f && boxSize.y > 0f

    // The FAB's resting rect, in WINDOW coordinates.
    val fabWindowRect: Rect? =
        when (placement) {
            is MorphingFabPlacement.Anchored -> placement.bounds?.takeIf { measured }
            is MorphingFabPlacement.BottomEnd ->
                if (!measured) {
                    null
                } else {
                    with(density) {
                        val fabSize = style.fabSize.toPx()
                        val edgePadding = placement.edgePadding.toPx()
                        // Whichever of the nav bar or the caller's inset reaches further up.
                        val bottomInset = maxOf(navigationBarInset, placement.bottomInset).toPx()
                        val left = origin.x + boxSize.x - edgePadding - fabSize
                        val top = origin.y + boxSize.y - bottomInset - edgePadding - fabSize
                        Rect(left, top, left + fabSize, top + fabSize)
                    }
                }
        }

    // Expanded, taps are swallowed so they don't fall through to the scrim and dismiss.
    val onSurfaceTap: () -> Unit = { if (!expanded) onFabClick() }

    Box(
        modifier =
            modifier.fillMaxSize().onGloballyPositioned {
                origin = it.positionInWindow()
                boxSize = Offset(it.size.width.toFloat(), it.size.height.toFloat())
            }
    ) {
        if (p == 0f && fabWindowRect != null) {
            // Into local space: the surface is a child of this box, not of the window.
            val localFabRect = fabWindowRect.translate(-origin.x, -origin.y)
            MorphingSurface(
                p = 0f,
                fabTransform = fabTransform,
                startRect = localFabRect,
                targetRect = localFabRect,
                expanded = false,
                style = style,
                colors = colors,
                onTap = onSurfaceTap,
                fabContent = fabContent,
                dialogContent = dialogContent,
            )
        }
    }

    if (p > 0f && fabWindowRect != null) {
        WindowOverlay { windowSize ->
            // Scrim: covers the window, so toolbars and bottom bars dim with it.
            Box(
                modifier =
                    Modifier.fillMaxSize()
                        .background(colors.scrim.copy(alpha = style.scrimAlpha * p))
                        .pointerInput(Unit) { detectTapGestures { onDismissRequest() } }
            )

            MorphingSurface(
                p = p,
                // A secondary transform only runs while collapsed, so it can't be mid-flight here.
                fabTransform = MorphingFabTransform.None,
                // Already window-space, so no conversion needed inside the overlay.
                startRect = fabWindowRect,
                targetRect = dialogRect(windowSize, style),
                expanded = expanded,
                style = style,
                colors = colors,
                onTap = onSurfaceTap,
                fabContent = fabContent,
                dialogContent = dialogContent,
            )
        }
    }
}

/**
 * A FAB that morphs into a standard alert dialog: optional icon, title, supporting text, and a
 * dismiss/confirm action row.
 *
 * The layout matches `AlertDialog`'s conventions. Reach for the slot overload above only when you
 * need something that shape won't hold.
 *
 * @param confirmLabel Label for the trailing (affirmative) button.
 * @param onConfirm Called when the confirm button is tapped. Note this does **not** dismiss — call
 *   [onDismissRequest] yourself first if you want the morph to play back out.
 * @param dismissLabel Label for the leading (dismissive) button.
 * @param onDismiss Called when the dismiss button is tapped. Defaults to [onDismissRequest].
 * @param icon Optional icon above the title.
 */
@Composable
fun MorphingFab(
    expanded: Boolean,
    onFabClick: () -> Unit,
    onDismissRequest: () -> Unit,
    title: String,
    text: String,
    confirmLabel: String,
    onConfirm: () -> Unit,
    dismissLabel: String,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = onDismissRequest,
    icon: (@Composable () -> Unit)? = null,
    placement: MorphingFabPlacement = MorphingFabPlacement.BottomEnd(),
    style: MorphingFabStyle = MorphingFabStyle(),
    colors: MorphingFabColors = MorphingFabDefaults.colors(),
    animationSpec: AnimationSpec<Float> = MorphingFabDefaults.MorphSpec,
    fabTransform: MorphingFabTransform = MorphingFabTransform.None,
    fabContent: @Composable () -> Unit,
) {
    MorphingFab(
        expanded = expanded,
        onFabClick = onFabClick,
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        placement = placement,
        style = style,
        colors = colors,
        animationSpec = animationSpec,
        fabTransform = fabTransform,
        fabContent = fabContent,
    ) {
        icon?.invoke()

        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = colors.titleContent,
            textAlign = TextAlign.Center,
        )

        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textContent,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(onClick = onDismiss) { Text(dismissLabel) }

            Spacer(Modifier.width(8.dp))

            TextButton(onClick = onConfirm) { Text(confirmLabel) }
        }
    }
}

/** The expanded dialog's rect: [MorphingFabStyle.dialogSize], clamped to the window, centred. */
@Composable
private fun dialogRect(windowSize: Size, style: MorphingFabStyle): Rect =
    with(LocalDensity.current) {
        val width =
            min(style.dialogSize.width.toPx(), windowSize.width - style.dialogMinMargin.toPx() * 2f)
        val height = style.dialogSize.height.toPx()
        val left = (windowSize.width - width) / 2f
        val top = (windowSize.height - height) / 2f
        Rect(left, top, left + width, top + height)
    }

/**
 * The single Surface that is both the FAB and the dialog, placed in whatever coordinate space its
 * parent defines — [startRect] and [targetRect] must be expressed in that same space.
 *
 * @param p Morph progress: 0 is the FAB, 1 is the dialog.
 */
@Composable
private fun MorphingSurface(
    p: Float,
    fabTransform: MorphingFabTransform,
    startRect: Rect,
    targetRect: Rect,
    expanded: Boolean,
    style: MorphingFabStyle,
    colors: MorphingFabColors,
    onTap: () -> Unit,
    fabContent: @Composable () -> Unit,
    dialogContent: @Composable ColumnScope.() -> Unit,
) {
    with(LocalDensity.current) {
        fun lerpF(a: Float, b: Float) = a + (b - a) * p

        // The morph proper, then the secondary transform scaling it about its own centre.
        val fullW = lerpF(startRect.width, targetRect.width)
        val fullH = lerpF(startRect.height, targetRect.height)
        val w = fullW * fabTransform.scale
        val h = fullH * fabTransform.scale
        val left = lerpF(startRect.left, targetRect.left) + (fullW - w) / 2f
        val top = lerpF(startRect.top, targetRect.top) + (fullH - h) / 2f

        // fabCorner → dialogCorner, then tightened toward a full circle (half the current height).
        val baseCorner = lerpF(style.fabCorner.toPx(), style.dialogCorner.toPx())
        val corner = baseCorner + (h / 2f - baseCorner) * fabTransform.cornerCircularity

        val surfaceColor = lerp(colors.fabContainer, colors.dialogContainer, p)

        // Dialog content fades in late, so text never renders inside a tiny FAB-sized box.
        val fabContentAlpha =
            ((1f - p / style.fabContentFadeOutBy) * fabTransform.contentAlpha).coerceIn(0f, 1f)
        val dialogAlpha =
            ((p - style.dialogContentFadeInFrom) / (1f - style.dialogContentFadeInFrom)).coerceIn(
                0f,
                1f,
            )

        Surface(
            shape = RoundedCornerShape(corner.toDp()),
            color = surfaceColor,
            tonalElevation = style.tonalElevation,
            modifier =
                Modifier.offset { IntOffset(left.roundToInt(), top.roundToInt()) }
                    .size(w.toDp(), h.toDp())
                    .pointerInput(expanded) { detectTapGestures { onTap() } },
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                if (fabContentAlpha > 0f) {
                    Box(Modifier.align(Alignment.Center).alpha(fabContentAlpha)) { fabContent() }
                }

                if (dialogAlpha > 0f) {
                    // requiredSize lays content out at the dialog's FINAL dimensions while the
                    // surface is still growing, so text wraps once and simply fades in rather than
                    // re-wrapping every frame. The surface's shape clips the overflow.
                    Column(
                        modifier =
                            Modifier.requiredSize(targetRect.width.toDp(), targetRect.height.toDp())
                                .padding(style.dialogPadding)
                                .alpha(dialogAlpha),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(style.dialogSpacing),
                        content = dialogContent,
                    )
                }
            }
        }
    }
}
