package cards.sleevd.morphingfab

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties

/**
 * Draws [content] in a popup covering the entire window, with (0, 0) at the window's top-left.
 *
 * [content] receives the window size in pixels so it can lay things out in window coordinates — the
 * same space `LayoutCoordinates.positionInWindow()` reports, so a rect captured from the tree can
 * be used here unchanged. Nothing is drawn until that size is known.
 *
 * Mount this only while it's showing something: it's a full-screen window, so while it's up it
 * takes every touch on the screen.
 *
 * The explicit `size` below is deliberate — `fillMaxSize()` won't do, because the popup window is
 * WRAP_CONTENT and so the constraints it hands down describe the available space, not the window.
 */
@Composable
fun WindowOverlay(content: @Composable (windowSize: Size) -> Unit) {
    val windowSize = LocalWindowInfo.current.containerSize
    if (windowSize.width <= 0 || windowSize.height <= 0) return

    Popup(
        popupPositionProvider = remember { WindowOriginPositionProvider },
        properties = remember { windowOverlayPopupProperties() },
    ) {
        with(LocalDensity.current) {
            Box(Modifier.size(windowSize.width.toDp(), windowSize.height.toDp())) {
                content(Size(windowSize.width.toFloat(), windowSize.height.toFloat()))
            }
        }
    }
}

/**
 * Properties for the overlay popup. Constant on the face of it, but it can't be a literal in
 * `commonMain`: the one option that matters — `usePlatformInsets` — only exists on the iOS/skiko
 * `actual` of `PopupProperties`.
 *
 * Every implementation must produce a popup that is not focusable (these overlays own system back
 * through [BackHandler]), has `clippingEnabled = false` (or it's held inside the system bars), and
 * places its content at the **true window origin**, so `IntOffset.Zero` means the same point
 * `positionInWindow()` calls (0, 0). That last one is the load-bearing part — see the iOS `actual`.
 */
internal expect fun windowOverlayPopupProperties(): PopupProperties

/** Pins the popup to the window's top-left rather than to an anchor. */
private object WindowOriginPositionProvider : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize,
    ): IntOffset = IntOffset.Zero
}
