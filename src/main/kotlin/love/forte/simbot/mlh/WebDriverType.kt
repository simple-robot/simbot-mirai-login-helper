package love.forte.simbot.mlh

import io.github.bonigarcia.wdm.WebDriverManager
import kotlinx.serialization.Serializable
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.edge.EdgeDriver
import org.openqa.selenium.edge.EdgeOptions
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxOptions
import org.openqa.selenium.ie.InternetExplorerDriver
import org.openqa.selenium.ie.InternetExplorerOptions
import org.openqa.selenium.opera.OperaDriver
import org.openqa.selenium.opera.OperaOptions
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.safari.SafariDriver
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

    CHROME(true, true, { WebDriverManager.chromedriver() }, { capabilities ->
        ChromeDriver(org.openqa.selenium.chrome.ChromeOptions().merge(capabilities)
            .also { it.setAcceptInsecureCerts(true) })
    }),

    EDGE(true, true, { WebDriverManager.edgedriver() }, { capabilities ->
        EdgeDriver(EdgeOptions().merge(
            capabilities
        ).also { it.setAcceptInsecureCerts(true) })
    }),

    FIREFOX(true, true, { WebDriverManager.firefoxdriver() }, { capabilities ->
        FirefoxDriver(FirefoxOptions().merge(capabilities).also { it.setAcceptInsecureCerts(true) })
    }),

    IE(true, false, { WebDriverManager.iedriver() }, { capabilities ->
        InternetExplorerDriver(InternetExplorerOptions().merge(capabilities).also { it.setAcceptInsecureCerts(true) })
    }),

    SAFARI(false,
        true,
        { WebDriverManager.safaridriver() },
        { capabilities -> SafariDriver(SafariOptions().merge(capabilities).also { it.setAcceptInsecureCerts(true) }) }),

    OPERA(true,
        true,
        { WebDriverManager.operadriver() },
        { capabilities -> OperaDriver(OperaOptions().merge(capabilities).also { it.setAcceptInsecureCerts(true) }) }), ;


    fun driverManager(): WebDriverManager = driverManagerFactory()
    fun driverFactory(capabilities: DesiredCapabilities): () -> WebDriver = { this.driverFactory.invoke(capabilities) }
}