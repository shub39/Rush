import SwiftUI
import SharedLogic

struct SongRepositoryTestView: View {
    @State private var logs: [String] = []
    private let repository = KoinDependencies().songRepository
    
    var body: some View {
        NavigationStack {
            VStack {
                ScrollViewReader { proxy in
                    ScrollView {
                        LazyVStack(alignment: .leading) {
                            ForEach(logs.indices, id: \.self) { index in
                                Text(logs[index])
                                    .font(.system(.caption, design: .monospaced))
                                    .padding(.horizontal)
                                    .id(index)
                            }
                        }
                    }
                    .onChange(of: logs.count) { _ in
                        withAnimation {
                            proxy.scrollTo(logs.count - 1, anchor: .bottom)
                        }
                    }
                }
                .frame(maxWidth: .infinity, maxHeight: .infinity)
                .background(Color(.systemGray6))
                
                Divider()
                
                VStack(spacing: 12) {
                    HStack {
                        Button(action: runSearchTest) {
                            Text("Search")
                                .frame(maxWidth: .infinity)
                        }
                        .buttonStyle(.borderedProminent)
                        
                        Button(action: runDbTest) {
                            Text("Database")
                                .frame(maxWidth: .infinity)
                        }
                        .buttonStyle(.borderedProminent)
                    }
                    
                    HStack {
                        Button(action: {
                            logs.removeAll()
                            log("Logs cleared.")
                        }) {
                            Text("Clear Logs")
                                .frame(maxWidth: .infinity)
                        }
                        .buttonStyle(.bordered)
                        
                        Button(action: runFullWorkflow) {
                            Text("Run All")
                                .frame(maxWidth: .infinity)
                        }
                        .buttonStyle(.borderedProminent)
                        .tint(.green)
                    }
                }
                .padding()
            }
            .navigationTitle("SongRepo Test")
            .navigationBarTitleDisplayMode(.inline)
        }
    }
    
    private func log(_ message: String) {
        let timestamp = Date().formatted(.dateTime.hour().minute().second().secondFraction(.fractional(3)))
        logs.append("[\(timestamp)] \(message)")
    }
    
    private func runSearchTest() {
        log("--- SEARCH TEST ---")
        log("Searching for 'Blinding Lights'...")
        Task {
            do {
                let result = try await repository.searchGenius(query: "Blinding Lights")
                handleSearchResult(result)
            } catch {
                log("Search Exception: \(error.localizedDescription)")
            }
        }
    }
    
    private func handleSearchResult(_ result: Any) {
        // Try direct cast first as seen in SearchViewModel
        if let data = result as? [SearchResult] {
            log("DIRECT SUCCESS: Found \(data.count) items")
            for item in data.prefix(2) {
                log(" >> \(item.title) - \(item.artist) (ID: \(item.id))")
            }
            return
        }
        
        // Handling sealed interface Result in Swift
        if let success = result as? ResultSuccess<NSArray, SourceError> {
            let data = success.data as? [SearchResult] ?? []
            log("SUCCESS: Found \(data.count) items")
            for item in data.prefix(2) {
                log(" >> \(item.title) - \(item.artist) (ID: \(item.id))")
            }
        } else if let errorResult = result as? ResultError<NSArray, SourceError> {
            log("ERROR: \(errorResult.message ?? "Unknown error")")
        } else {
            log("UNKNOWN result type: \(type(of: result))")
        }
    }
    
    private func runDbTest() {
        log("--- DATABASE TEST ---")
        Task {
            do {
                let testId: Int64 = 99999
                log("Checking if test song exists...")
                let existing = try await repository.getAllSongs()
                if let song = existing.first(where: { $0.id == testId }) {
                    log("Found existing test song. Deleting it.")
                    try await repository.deleteSong(id: testId)
                }
                
                let newSong = Song(
                    id: testId,
                    title: "KMP Test Song",
                    artists: "Rush Developers",
                    lyrics: "Testing KMP Room implementation on iOS",
                    album: "Rush Debug",
                    sourceUrl: "local://test",
                    artUrl: nil,
                    geniusLyrics: nil,
                    syncedLyrics: nil,
                    ttmlLyrics: nil,
                    dateAdded: Int64(Date().timeIntervalSince1970 * 1000)
                )
                
                log("Inserting song: \(newSong.title)")
                try await repository.insertSong(song: newSong)
                
                log("Verifying insertion...")
                let song = try await repository.getSong(id: testId)
                log("VERIFIED: Retrieved '\(song.title)' from DB")
                
                let all = try await repository.getAllSongs()
                log("Total songs in DB: \(all.count)")
                
            } catch {
                log("DB Exception: \(error.localizedDescription)")
            }
        }
    }
    
    private func runFullWorkflow() {
        log("--- FULL WORKFLOW START ---")
        Task {
            // 1. Search
            log("1. Searching...")
            let searchRes = try await repository.searchGenius(query: "Starboy")
            if let success = searchRes as? ResultSuccess<NSArray, SourceError>,
               let firstResult = (success.data as? [SearchResult])?.first {
                log("   Found: \(firstResult.title)")
                
                // 2. Fetch full song
                log("2. Fetching full song details...")
                let songRes = try await repository.fetchSong(result: firstResult)
                
                if let songSuccess = songRes as? ResultSuccess<Song, SourceError> {
                    let song = songSuccess.data!
                    log("   Fetched: \(song.title) with \(song.lyrics.count) chars of lyrics")
                    
                    // 3. Save to DB
                    log("3. Saving to database...")
                    try await repository.insertSong(song: song)
                    
                    // 4. Verify in DB
                    log("4. Verifying...")
                    let dbSongs = try await repository.getAllSongs()
                    if dbSongs.contains(where: { $0.id == song.id }) {
                        log("   CONFIRMED: Song saved and retrieved.")
                    }
                }
            }
            log("--- FULL WORKFLOW FINISHED ---")
        }
    }
}
