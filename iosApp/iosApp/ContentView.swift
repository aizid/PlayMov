import SwiftUI
import Shared

struct ContentView: View {
    var body: some View {
        ComposeView()
            .ignoresSafeArea()
            .preferredColorScheme(.dark)
    }
}

private struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
