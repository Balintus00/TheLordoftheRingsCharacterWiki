//
//  AppDelegate.swift
//  iosApp
//
//  Created by user on 2024. 04. 22..
//  Copyright © 2024. orgName. All rights reserved.
//

import SwiftUI
import App

class AppDelegate : NSObject, UIApplicationDelegate {
    
    let component: RootComponent = DefaultRootComponent(
            componentContext: DefaultComponentContext(
                lifecycle: ApplicationLifecycle()
            ),
            storeFactory: LoggingStoreFactory(
                delegate: DefaultStoreFactory()
            )
        )
}
