package cards.sleevd.morphingfab.sample

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

/** Entry point for `iosApp/`. The Swift side does nothing but host what this returns. */
fun MainViewController(): UIViewController = ComposeUIViewController { SampleApp() }
