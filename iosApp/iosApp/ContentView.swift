import UIKit
import SwiftUI
import App

struct ComposeView: UIViewControllerRepresentable {
    
    let component: RootComponent
    
    func makeUIViewController(context: Context) -> UIViewController {
        ViewControllerKt.TheLordOfTheRingsCharacterWikiViewController(
            component: component
        )
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    
    let component: RootComponent
    
    var body: some View {
        ComposeView(component: component)
                .ignoresSafeArea(.keyboard) // Compose has own keyboard handler
    }
}



