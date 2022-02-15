package love.forte.simbot.mlh.window

import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.loadImageBitmap
import java.net.URL

@Suppress("MemberVisibilityCanBePrivate")
object Logo {
    private val logoResource: URL = Logo::class.java.classLoader.getResource("logo.png")!!
    val logoBitmap = logoResource.openStream().use(::loadImageBitmap)
    val painter = BitmapPainter(logoBitmap)
}
