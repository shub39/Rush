import SwiftUI
import SharedLogic

// @Observable (Swift 5.9+) is similar to a ViewModel holding StateFlows in Kotlin.
// It automatically notifies observers (Views) when any property changes.
@Observable
class HomeViewModel {
    private let repository = KoinDependencies().songRepository
    
    var songs: [Song] = []
    
    // In Kotlin/Compose, you might use viewModelScope.launch. 
    // In Swift, we use Task { ... } to run asynchronous code.
    func loadSongs() {
        Task {
            do {
                let fetchedSongs = try await repository.getAllSongs()
                // MainActor.run ensures UI updates happen on the main thread, 
                // similar to withContext(Dispatchers.Main) in Kotlin.
                await MainActor.run {
                    self.songs = fetchedSongs
                }
            } catch {
                print("Failed to load songs: \(error)")
            }
        }
    }
    
    // In Compose, you'd typically pass a command to the ViewModel to remove an item.
    // SwiftUI's .onDelete provides an IndexSet of the items to remove.
    func deleteSong(at offsets: IndexSet) {
        let songsToRemove = offsets.map { songs[$0] }
        
        // Optimistic UI update: remove from local list immediately.
        // Similar to updating a MutableStateFlow in Kotlin.
        songs.remove(atOffsets: offsets)
        
        Task {
            do {
                for song in songsToRemove {
                    try await repository.deleteSong(id: song.id)
                }
            } catch {
                print("Failed to delete song: \(error)")
                // If deletion fails, we re-load to ensure UI is in sync with DB.
                await loadSongs()
            }
        }
    }
}
