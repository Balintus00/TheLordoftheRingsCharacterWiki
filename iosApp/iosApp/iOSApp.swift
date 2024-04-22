import SwiftUI
import App

@main
struct iOSApp: App {
    
    @UIApplicationDelegateAdaptor(AppDelegate.self)
    var appDelegate: AppDelegate
    
    init() {
        KoinHelperKt.doInitKoin()
    }
    
	var body: some Scene {
		WindowGroup {
            ContentView(component: appDelegate.component)
		}
	}
}
