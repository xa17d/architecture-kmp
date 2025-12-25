package at.xa1.architecture.kmp.sample

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import at.xa1.architecture.kmp.sample.shared.App

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "architecture-example",
    ) {
        App()
    }
}
