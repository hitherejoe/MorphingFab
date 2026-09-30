package cards.sleevd.morphingfab

import androidx.compose.runtime.Composable

/**
 * Intercepts the system back button/gesture while [enabled], invoking [onBack] instead.
 *
 * [MorphingFab] draws its dialog in a [WindowOverlay] rather than a platform `Dialog`, so it does
 * not get back-dismissal for free. Declared here rather than taken from a multiplatform back
 * library, to keep the dependency list to Compose alone; being `internal`, it can't clash with
 * whatever the host app already has.
 */
@Composable internal expect fun BackHandler(enabled: Boolean, onBack: () -> Unit)
