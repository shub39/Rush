import SwiftUI
import SharedLogic

@main
struct RushApp: App {
    // init() is where we initialize dependencies.
    // Similar to onCreate in an Android Application class.
    init() {
        KoinKt.doInitKoin()
    }
    
    var body: some Scene {
        WindowGroup {
            // The starting point of our UI.
            HomeView()
        }
    }
}
