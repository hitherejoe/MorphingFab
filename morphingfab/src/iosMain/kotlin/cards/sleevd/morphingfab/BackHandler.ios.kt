package cards.sleevd.morphingfab

import androidx.compose.runtime.Composable

/** No-op: Compose Multiplatform has no global back-gesture interception on iOS today. */
@Composable internal actual fun BackHandler(enabled: Boolean, onBack: () -> Unit) {}
