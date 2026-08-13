//
//
//  Created by Shubham Gorai on 12/08/26.
//

import SwiftUI
import SharedLogic

@main
struct RushApp: App {
    init() {
        DataModulesKt.doInitKoin()
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
