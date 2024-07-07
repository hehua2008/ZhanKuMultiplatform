import SwiftUI
import ComposeApp

@main
struct iOSApp: App {
    init() {
        InitAppKt.doInit()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
