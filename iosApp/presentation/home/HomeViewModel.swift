//
//  HomeViewModel.swift
//  iosApp
//
//  Created by Shubham Gorai on 13/08/26.
//

import SwiftUI
import SharedLogic

@Observable
class HomeViewModel {
    init() {
        let songs = RushRepository.getAllSongs()
    }
    
    var songs: [Song] = []
    
}
