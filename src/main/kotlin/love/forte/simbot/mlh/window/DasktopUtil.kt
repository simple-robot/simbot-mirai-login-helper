package love.forte.simbot.mlh.window

import java.awt.Desktop
import java.awt.Desktop.Action


fun desktop(): Desktop? {
    if (!Desktop.isDesktopSupported()) {
        return null
    }

    return Desktop.getDesktop()
}


fun desktop(vararg actions: Action): Desktop? {
    if (!Desktop.isDesktopSupported()) {
        return null
    }

    return Desktop.getDesktop().takeIf { desktop ->
        actions.all { desktop.isSupported(it) }
    }
}


