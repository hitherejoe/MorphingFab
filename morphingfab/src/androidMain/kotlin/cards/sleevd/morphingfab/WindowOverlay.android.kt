package cards.sleevd.morphingfab

import androidx.compose.ui.window.PopupProperties

/**
 * `clippingEnabled = false` maps to `FLAG_LAYOUT_NO_LIMITS`, which lets the popup extend past the
 * system bars and puts its top-left at the host window's top-left — so `IntOffset.Zero` lands where
 * the overlay expects with no adjustment.
 */
internal actual fun windowOverlayPopupProperties(): PopupProperties =
    PopupProperties(focusable = false, clippingEnabled = false)
