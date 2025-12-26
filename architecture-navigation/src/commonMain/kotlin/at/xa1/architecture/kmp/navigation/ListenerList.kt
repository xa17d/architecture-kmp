package at.xa1.architecture.kmp.navigation

class ListenerList<T> {
    private val _listeners = mutableSetOf<(T) -> Unit>()

    fun add(listener: (T) -> Unit) {
        _listeners.add(listener)
    }

    fun remove(listener: (T) -> Unit) {
        val wasRemoved = _listeners.remove(listener)
        if (!wasRemoved) {
            error("Listener not found: $listener")
        }
    }

    fun notifyAll(target: T) {
        _listeners.forEach { listener ->
            listener(target)
        }
    }
}
