import SwiftUI

@main
struct iOSApp: App {
    var body: some Scene {
        WindowGroup {
            // ignoresSafeArea, because MorphingFab's scrim is drawn in a window-level overlay and
            // is meant to dim the whole screen. Compose applies its own insets inside.
            ComposeView().ignoresSafeArea(.all)
        }
    }
}
