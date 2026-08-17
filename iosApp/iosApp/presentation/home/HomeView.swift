import SwiftUI
import SharedLogic

struct HomeView: View {
    @State private var viewModel = HomeViewModel()
    @State private var showingSearch = false
    
    // States to trigger navigation. 
    // Parallels: 'selectedItem' state in Compose that triggers a route change.
    @State private var selectedSong: Song?
    @State private var selectedSearchResult: SearchResult?
    
    var body: some View {
        NavigationStack {
            List {
                ForEach(viewModel.songs, id: \.id) { song in
                    // Tapping a row sets the state, which triggers navigationDestination below.
                    Button {
                        selectedSong = song
                    } label: {
                        HStack {
                            AsyncImage(url: URL(string: song.artUrl ?? "")) { image in
                                image.resizable()
                            } placeholder: {
                                Image(systemName: "music.note")
                                    .foregroundColor(.gray)
                            }
                            .frame(width: 50, height: 50)
                            .cornerRadius(8)
                            
                            VStack(alignment: .leading) {
                                Text(song.title)
                                    .font(.headline)
                                    .foregroundColor(.primary)
                                Text(song.artists)
                                    .font(.subheadline)
                                    .foregroundColor(.secondary)
                            }
                        }
                    }
                }
                .onDelete(perform: viewModel.deleteSong)
            }
            .navigationTitle("Saved Lyrics")
            // .navigationDestination(item:) is a modern way to handle programmatic navigation.
            // It watches the binding ($selectedSong) and navigates when it's not nil.
            .navigationDestination(item: $selectedSong) { song in
                LyricsDetailView(song: song)
            }
            .navigationDestination(item: $selectedSearchResult) { result in
                LyricsDetailView(searchResult: result)
            }
            .toolbar {
                ToolbarItem(placement: .primaryAction) {
                    Button(action: { showingSearch = true }) {
                        Image(systemName: "plus")
                    }
                }
            }
            .sheet(isPresented: $showingSearch) {
                SearchView { result in
                    // When the sheet reports a selection, we set our state to trigger navigation.
                    self.selectedSearchResult = result
                }
            }
            .onAppear {
                viewModel.loadSongs()
            }
            .overlay {
                if viewModel.songs.isEmpty {
                    ContentUnavailableView(
                        "No Lyrics Saved",
                        systemImage: "music.note.list",
                        description: Text("Tap the + button to search and save lyrics.")
                    )
                }
            }
        }
    }
}

// Extension to make KMP classes compatible with navigationDestination(item:)
// Parallels: Implementing 'Parcelable' or using a unique key in Compose Navigation.
extension Song: @retroactive Identifiable {}
extension SearchResult: @retroactive Identifiable {}

#Preview {
    HomeView()
}
