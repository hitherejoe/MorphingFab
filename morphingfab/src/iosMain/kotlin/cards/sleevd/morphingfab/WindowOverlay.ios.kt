package cards.sleevd.morphingfab

import androidx.compose.ui.window.PopupProperties

/**
 * `usePlatformInsets = false` is the load-bearing argument here. Do not drop it.
 *
 * With it on (the default), skiko's popup runs the position provider in a space that has the
 * safe-area insets subtracted and then adds `platformInsets.top`/`left` back, so `IntOffset.Zero`
 * stops meaning "window origin" and starts meaning "below the status bar". The whole overlay shifts
 * down, and [MorphingFab] — which draws the resting FAB in the tree and the morph in a
 * [WindowOverlay] from one window-space rect — appears to snap as the two hand over at progress 0.
 */
internal actual fun windowOverlayPopupProperties(): PopupProperties =
    PopupProperties(focusable = false, clippingEnabled = false, usePlatformInsets = false)
