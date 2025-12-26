package at.xa1.architecture.kmp.navigation

interface ScopeContext {
    companion object {
        fun add(context: ScopeContext): Metadata {
            return mapOf(METADATA_ID to context)
        }

        fun from(metadata: Metadata): ScopeContext? {
            return metadata[METADATA_ID] as? ScopeContext
        }

        private const val METADATA_ID = "at.xa1.architecture.kmp.navigation:ScopeContext"
    }
}
