package love.forte.simbot.mlh

import net.lightbody.bmp.mitm.KeyStoreFileCertificateSource
import net.lightbody.bmp.mitm.manager.ImpersonatingMitmManager
import org.littleshoot.proxy.HttpProxyServerBootstrap
import org.littleshoot.proxy.impl.DefaultHttpProxyServer
import java.io.File


/**
 *
 * @author ForteScarlet
 */
object CAKeyStoreConfig {
    fun create(): HttpProxyServerBootstrap {
        // load the root certificate and private key from an existing KeyStore
        val fileCertificateSource = KeyStoreFileCertificateSource(
            "PKCS12",  // KeyStore type. for .jks files (Java KeyStore), use "JKS"
            File("./key/keystore.p12"),
            "simbot-key",  // alias of the private key in the KeyStore; if you did not specify an alias when creating it, use "1"
            "simbot-key-password"
        )


        // tell the MitmManager to use the custom certificate and private key
        val mitmManager = ImpersonatingMitmManager.builder()
            .rootCertificateSource(fileCertificateSource)
            .build()

        // tell the HttpProxyServerBootstrap to use the new MitmManager
        // proxy
        return DefaultHttpProxyServer.bootstrap()
            .withManInTheMiddle(mitmManager)
    }
}