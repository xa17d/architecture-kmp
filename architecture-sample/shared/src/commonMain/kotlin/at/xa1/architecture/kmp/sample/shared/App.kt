package at.xa1.architecture.kmp.sample.shared

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import at.xa1.architecture.kmp.navigation.NavDisplay
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        NavDisplay()
    }
}
