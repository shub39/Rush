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
            defer {
                Task { @MainActor in
                    self.isSearching = false
                }
            }
            
            do {
                let result = try await repository.searchGenius(query: query)
                
                if let data = result as? [SearchResult] {
                    await MainActor.run {
                        self.results = data
                    }
                } else if let success = result as? ResultSuccess<NSArray, SourceError> {
                    let data = success.data as? [SearchResult] ?? []
                    await MainActor.run {
                        self.results = data
                    }
                } else if let errorRes = result as? ResultError<NSArray, SourceError> {
                    print("Search error: \(errorRes.message ?? "Unknown error")")
                }
            } catch {
                print("Network/Mapping error: \(error)")
            }
        }
    }
}
