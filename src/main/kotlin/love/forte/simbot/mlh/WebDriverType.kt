package love.forte.simbot.mlh

import io.github.bonigarcia.wdm.WebDriverManager
import kotlinx.serialization.Serializable
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.edge.EdgeDriver
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.ie.InternetExplorerDriver
import org.openqa.selenium.opera.OperaOptions
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.safari.SafariOptions

/**
 *
 * @author ForteScarlet
 */
@Serializable
enum class WebDriverType(
    val windowsAble: Boolean,
    val macAble: Boolean,
    private val driverManagerFactory: () -> WebDriverManager,
    private val driverFactory: (capabilities: DesiredCapabilities) -> WebDriver
) {

    CHROME(
        true, true,
        { WebDriverManager.chromedriver() },
        { capabilities -> ChromeDriver(org.openqa.selenium.chrome.ChromeOptions().merge(capabilities)) }
    ),

    EDGE(
        true, true,
        { WebDriverManager.edgedriver() },
        { capabilities -> EdgeDriver(org.openqa.selenium.edge.EdgeOptions().merge(capabilities)) }
    ),
    FIREFOX(
        true, true,
        { WebDriverManager.firefoxdriver() },
        { capabilities -> FirefoxDriver(org.openqa.selenium.firefox.FirefoxOptions().merge(capabilities)) }
    ),

    IE(
        true, false,
        { WebDriverManager.iedriver() },
        { capabilities -> InternetExplorerDriver(org.openqa.selenium.ie.InternetExplorerOptions().merge(capabilities)) }
    ),

    SAFARI(
        false, true,
        { WebDriverManager.safaridriver() },
        { capabilities -> org.openqa.selenium.safari.SafariDriver(SafariOptions().merge(capabilities)) }
    ),

    OPERA(
        true, true,
        { WebDriverManager.operadriver() },
        { capabilities -> org.openqa.selenium.opera.OperaDriver(OperaOptions().merge(capabilities)) }
    ),
    ;


    fun driverManager(): WebDriverManager = driverManagerFactory()
    fun driverFactory(capabilities: DesiredCapabilities): () -> WebDriver = { this.driverFactory.invoke(capabilities) }
}