import SwiftUI
import SharedLogic

@Observable
class SearchViewModel {
    private let repository = KoinDependencies().songRepository
    
    var query: String = ""
    var results: [SearchResult] = []
    var isSearching: Bool = false
    
    func search() {
        guard !query.isEmpty else { return }
        isSearching = true
        
        Task {
            do {
                let result = try await repository.searchGenius(query: query)
                
                // SKIE makes Result handling easier, but let's use the basic way if not sure
                if let data = result as? [SearchResult] {
                    await MainActor.run {
                        self.results = data
                        self.isSearching = false
                    }
                }
            } catch {
                await MainActor.run {
                    self.isSearching = false
                }
                print("Search error: \(error)")
            }
        }
    }
}
