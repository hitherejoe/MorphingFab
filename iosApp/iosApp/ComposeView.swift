import SwiftUI
import SampleApp

/// The entire iOS app. `MainViewController()` comes from :sample's iosMain.
struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
