import SwiftUI
import SharedLogic

@Observable
class LyricsDetailViewModel {
    private let repository = KoinDependencies().songRepository
    
    var song: Song? = nil
    var isLoading: Bool = false
    var error: String? = nil
    
    // In Compose, you might pass initial data to a ViewModel via a Factory or Hilt.
    // In SwiftUI, we can just call an init function or a load method.
    func load(initialSong: Song? = nil, searchResult: SearchResult? = nil) {
        if let initialSong = initialSong {
            self.song = initialSong
            return
        }
        
        guard let searchResult = searchResult else { return }
        
        isLoading = true
        Task {
            defer {
                Task { @MainActor in self.isLoading = false }
            }
            
            do {
                let result = try await repository.fetchSong(result: searchResult)
                
                if let success = result as? ResultSuccess<Song, SourceError>,
                   let fetchedSong = success.data {
                    
                    // Auto-save the song when viewed from search
                    try await repository.insertSong(song: fetchedSong)
                    
                    await MainActor.run {
                        self.song = fetchedSong
                    }
                } else if let errorRes = result as? ResultError<Song, SourceError> {
                    await MainActor.run {
                        self.error = errorRes.message ?? "Failed to fetch lyrics"
                    }
                }
            } catch {
                await MainActor.run {
                    self.error = error.localizedDescription
                }
            }
        }
    }
}
