package cards.sleevd.morphingfab.sample

import androidx.compose.animation.core.snap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import cards.sleevd.morphingfab.MorphingFabDefaults
import cards.sleevd.morphingfab.sample.demos.AnchoredDemo
import cards.sleevd.morphingfab.sample.demos.BasicDemo
import cards.sleevd.morphingfab.sample.demos.CustomContentDemo
import cards.sleevd.morphingfab.sample.demos.LaunchMorphDemo
import cards.sleevd.morphingfab.sample.demos.ThemingDemo

/** Root of the sample. The only thing `:androidApp` and `iosApp/` each call. */
@Composable
fun SampleApp() {
    var current by remember { mutableStateOf<Demo?>(null) }
    var reducedMotion by remember { mutableStateOf(false) }

    SampleTheme {
        CompositionLocalProvider(
            LocalMorphSpec provides if (reducedMotion) snap() else MorphingFabDefaults.MorphSpec
        ) {
            when (val demo = current) {
                null ->
                    HomeScreen(
                        reducedMotion = reducedMotion,
                        onReducedMotionChange = { reducedMotion = it },
                        onOpen = { current = it },
                    )

                else -> {
                    val onBack = { current = null }
                    when (demo) {
                        Demo.Basic -> BasicDemo(onBack)
                        Demo.Anchored -> AnchoredDemo(onBack)
                        Demo.CustomContent -> CustomContentDemo(onBack)
                        Demo.Theming -> ThemingDemo(onBack)
                        Demo.LaunchMorph -> LaunchMorphDemo(onBack)
                    }
                }
            }
        }
    }
}

internal enum class Demo(val title: String, val summary: String, val icon: ImageVector) {
    Basic(
        title = "Basics",
        summary = "The convenience overload, resting in the bottom-end corner.",
        icon = SampleIcons.Delete,
    ),
    Anchored(
        title = "Scaffold anchor",
        summary = "Positioned by Scaffold's FAB slot, with a bottom bar to clear.",
        icon = SampleIcons.Home,
    ),
    CustomContent(
        title = "Custom dialog body",
        summary = "The slot overload, with a list and a weighted action row.",
        icon = SampleIcons.Star,
    ),
    Theming(
        title = "Style and colours",
        summary = "Live controls over geometry, colour and the content crossfade.",
        icon = SampleIcons.Tune,
    ),
    LaunchMorph(
        title = "Launch morph",
        summary = "One FAB that either opens the dialog or becomes a circular reveal.",
        icon = SampleIcons.Bolt,
    ),
}
