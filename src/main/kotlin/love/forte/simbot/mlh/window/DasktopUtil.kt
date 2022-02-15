package love.forte.simbot.mlh.window

import java.awt.Desktop
import java.awt.Desktop.Action

@PublishedApi
internal object DesktopNotSupport

@JvmInline
value class DesktopResult<T>(val result: Any?) {

    @Suppress("UNCHECKED_CAST")
    inline fun or(action: () -> T): T {
        return if (result === DesktopNotSupport) action() else result as T
    }

    @Suppress("UNCHECKED_CAST")
    inline fun orDo(action: () -> Unit): T? {
        if (result === DesktopNotSupport) {
            action()
            return null
        }
        return result as T
    }

    @Suppress("NOTHING_TO_INLINE", "UNCHECKED_CAST")
    inline fun orNull(): T? = if (result === DesktopNotSupport) null else result as T
}


inline fun <T> desktop(action: (desktop: Desktop) -> T): DesktopResult<T> {
    if (!Desktop.isDesktopSupported()) {
        return DesktopResult(DesktopNotSupport)
    }

    return Desktop.getDesktop()?.let { DesktopResult(action(it)) } ?: DesktopResult(DesktopNotSupport)
}


inline fun <T> desktop(vararg actions: Action, action: (desktop: Desktop) -> T): DesktopResult<T> {
    if (!Desktop.isDesktopSupported()) {
        return DesktopResult(DesktopNotSupport)
    }

    return Desktop.getDesktop().takeIf { desktop ->
        actions.all { desktop.isSupported(it) }
    }?.let { DesktopResult(action(it)) } ?: DesktopResult(DesktopNotSupport)
}


