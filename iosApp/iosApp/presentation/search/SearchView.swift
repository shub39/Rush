import SwiftUI
import SharedLogic

struct SearchView: View {
    @State private var viewModel = SearchViewModel()
    @Environment(\.dismiss) var dismiss
    
    var body: some View {
        NavigationStack {
            List(viewModel.results, id: \.id) { result in
                HStack {
                    AsyncImage(url: URL(string: result.artUrl)) { image in
                            image.resizable()
                        } placeholder: {
                            Color.gray
                        }
                        .frame(width: 50, height: 50)
                        .cornerRadius(8)
                    
                    VStack(alignment: .leading) {
                        Text(result.title)
                            .font(.headline)
                        Text(result.artist)
                            .font(.subheadline)
                            .foregroundColor(.secondary)
                    }
                }
                .onTapGesture {
                    // Handle selection
                    print("Selected: \(result.title)")
                }
            }
            .navigationTitle("Search Lyrics")
            .navigationBarTitleDisplayMode(.inline)
            .searchable(text: $viewModel.query)
            .onSubmit(of: .search) {
                viewModel.search()
            }
            .toolbar {
                ToolbarItem(placement: .topBarTrailing) {
                    Button("Done") {
                        dismiss()
                    }
                }
            }
            .overlay {
                if viewModel.isSearching {
                    ProgressView()
                } else if viewModel.results.isEmpty && !viewModel.query.isEmpty {
                    ContentUnavailableView.search
                }
            }
        }
    }
}
