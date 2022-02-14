package love.forte.simbot.mlh

import io.github.bonigarcia.wdm.WebDriverManager

/**
 *
 * @author ForteScarlet
 */
enum class DriverSelector(
    private val windowsAble: Boolean,
    private val macAble: Boolean,
    private val driverManagerFactory: () -> WebDriverManager
) {

    CHROME(
        true, true,
        { WebDriverManager.chromedriver() }
    ),

    CHROMIUM(
        true, true,
        { WebDriverManager.chromiumdriver() }
    ),

    EDGE(
        true, true,
        { WebDriverManager.edgedriver() }
    ),
    FIREFOX(
        true, true,
        { WebDriverManager.firefoxdriver() }
    ),

    IE(
        true, false,
        { WebDriverManager.iedriver() }
    ),

    SAFARI(
        false, true,
        { WebDriverManager.safaridriver() }
    ),

    OPERA(
        true, true,
        { WebDriverManager.operadriver() }
    ),


}