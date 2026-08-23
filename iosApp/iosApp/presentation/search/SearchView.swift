import SwiftUI
import SharedLogic

struct SearchView: View {
    @State private var viewModel = SearchViewModel()
    @Environment(\.dismiss) var dismiss
    
    // Callback to notify the parent (HomeView) of the selection.
    var onSelect: (SearchResult) -> Void
    
    var body: some View {
        NavigationStack {
            List(viewModel.results, id: \.id) { result in
                Button {
                    // 1. Notify the parent of the selection
                    onSelect(result)
                    // 2. Dismiss the search sheet
                    dismiss()
                } label: {
                    HStack {
                        AsyncImage(url: URL(string: result.artUrl)) { image in
                            image.resizable()
                        } placeholder: {
                            Color.gray.opacity(0.3)
                        }
                        .frame(width: 50, height: 50)
                        .cornerRadius(8)
                        
                        VStack(alignment: .leading) {
                            Text(result.title)
                                .font(.headline)
                                .foregroundColor(.primary)
                            Text(result.artist)
                                .font(.subheadline)
                                .foregroundColor(.secondary)
                        }
                    }
                }
            }
            .navigationTitle("Search Lyrics")
            .navigationBarTitleDisplayMode(.inline)
            .searchable(text: $viewModel.query)
            .onSubmit(of: .search) {
                viewModel.search()
            }
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Close") {
                        dismiss()
                    }
                }
            }
            .overlay {
                if viewModel.isSearching {
                    ProgressView("Searching...")
                        .padding()
                        .background(.ultraThinMaterial)
                        .cornerRadius(10)
                } else if viewModel.results.isEmpty && !viewModel.query.isEmpty {
                    ContentUnavailableView.search
                }
            }
        }
    }
}

#Preview {
    SearchView { _ in }
}
