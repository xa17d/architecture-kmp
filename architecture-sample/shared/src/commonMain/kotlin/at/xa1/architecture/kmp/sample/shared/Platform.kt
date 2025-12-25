package at.xa1.architecture.kmp.sample.shared

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
