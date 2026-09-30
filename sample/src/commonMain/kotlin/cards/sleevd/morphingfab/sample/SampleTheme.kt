package cards.sleevd.morphingfab.sample

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import cards.sleevd.morphingfab.MorphingFabDefaults

/**
 * The morph's animation spec, hoisted so one switch on the home screen can swap every demo over to
 * `snap()`.
 *
 * This is the pattern `:morphingfab` expects for reduced motion. The library takes an
 * `animationSpec` and has no idea what an accessibility preference is; deciding that "reduced
 * motion" means `snap()` rather than, say, a shorter tween is the app's call, not the library's.
 */
internal val LocalMorphSpec =
    compositionLocalOf<AnimationSpec<Float>> { MorphingFabDefaults.MorphSpec }

@Composable
internal fun SampleTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors,
        content = content,
    )
}

private val Violet = Color(0xFF6C4BF6)
private val VioletLight = Color(0xFFC3B3FF)

private val LightColors =
    lightColorScheme(
        primary = Violet,
        onPrimary = Color.White,
        primaryContainer = Color(0xFFE6DEFF),
        onPrimaryContainer = Color(0xFF1F0061),
        secondary = Color(0xFF00696E),
        surface = Color(0xFFFDFBFF),
        onSurface = Color(0xFF1B1B21),
        surfaceContainerHigh = Color(0xFFEBE7F0),
        surfaceContainerLow = Color(0xFFF6F2FB),
        onSurfaceVariant = Color(0xFF47464F),
        outlineVariant = Color(0xFFC8C5D0),
        error = Color(0xFFBA1A1A),
    )

private val DarkColors =
    darkColorScheme(
        primary = VioletLight,
        onPrimary = Color(0xFF330093),
        primaryContainer = Color(0xFF4A22CE),
        onPrimaryContainer = Color(0xFFE6DEFF),
        secondary = Color(0xFF4CDADF),
        surface = Color(0xFF131318),
        onSurface = Color(0xFFE5E1E9),
        surfaceContainerHigh = Color(0xFF292930),
        surfaceContainerLow = Color(0xFF1B1B21),
        onSurfaceVariant = Color(0xFFC8C5D0),
        outlineVariant = Color(0xFF47464F),
        error = Color(0xFFFFB4AB),
    )
