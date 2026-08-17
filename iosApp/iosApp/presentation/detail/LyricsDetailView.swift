import SwiftUI
import SharedLogic

struct LyricsDetailView: View {
    // We can pass these in to initialize the view
    let initialSong: Song?
    let searchResult: SearchResult?
    
    @State private var viewModel = LyricsDetailViewModel()
    
    init(song: Song) {
        self.initialSong = song
        self.searchResult = nil
    }
    
    init(searchResult: SearchResult) {
        self.initialSong = nil
        self.searchResult = searchResult
    }
    
    var body: some View {
        Group {
            if viewModel.isLoading {
                // Loading state: Similar to a Box with CircularProgressIndicator in Compose
                VStack(spacing: 20) {
                    ProgressView()
                        .scaleEffect(1.5)
                    Text("Fetching lyrics...")
                        .foregroundColor(.secondary)
                }
            } else if let song = viewModel.song {
                // Content state
                ScrollView {
                    VStack(alignment: .leading, spacing: 20) {
                        // Header section
                        HStack(spacing: 16) {
                            AsyncImage(url: URL(string: song.artUrl ?? "")) { image in
                                image.resizable()
                            } placeholder: {
                                Rectangle().fill(Color.gray.opacity(0.3))
                            }
                            .frame(width: 100, height: 100)
                            .cornerRadius(12)
                            
                            VStack(alignment: .leading) {
                                Text(song.title)
                                    .font(.title2.bold())
                                Text(song.artists)
                                    .font(.headline)
                                    .foregroundColor(.secondary)
                            }
                        }
                        .padding(.horizontal)
                        
                        Divider()
                        
                        // Lyrics section
                        Text(song.lyrics)
                            .font(.body)
                            .lineSpacing(8)
                            .padding(.horizontal)
                            .textSelection(.enabled)
                    }
                    .padding(.vertical)
                }
            } else if let error = viewModel.error {
                // Error state: Similar to a custom Error component in Compose
                ContentUnavailableView(
                    "Error",
                    systemImage: "exclamationmark.triangle",
                    description: Text(error)
                )
            }
        }
        .navigationTitle("Lyrics")
        .navigationBarTitleDisplayMode(.inline)
        .onAppear {
            // Parallels: LaunchedEffect(Unit) in Compose
            viewModel.load(initialSong: initialSong, searchResult: searchResult)
        }
    }
}
