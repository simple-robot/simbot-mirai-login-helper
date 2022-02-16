import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.31"
    kotlin("plugin.serialization") version "1.5.31"
    id("org.jetbrains.compose") version "1.0.0"
}

group = "love.forte.simbot"
version = "3.0.0"

repositories {
    google()
    mavenLocal()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    implementation(kotlin("stdlib"))
    // selenium https://www.selenium.dev/zh-cn/documentation/webdriver/getting_started/open_browser/
    implementation("org.seleniumhq.selenium:selenium-java:4.1.2")

    // https://www.selenium.dev/zh-cn/documentation/webdriver/getting_started/install_drivers/
    // https://github.com/bonigarcia/webdrivermanager
    // https://bonigarcia.dev/webdrivermanager/
    implementation("io.github.bonigarcia:webdrivermanager:5.0.3")

    // https://testerhome.com/topics/19895
    // https://stackoverflow.com/questions/25431380/capturing-browser-logs-with-selenium-webdriver-using-java
    // https://github.com/lightbody/browsermob-proxy#using-with-selenium
    implementation("net.lightbody.bmp:browsermob-core:2.1.5")

    // https://github.com/lightbody/browsermob-proxy/blob/master/mitm/README.md
    // implementation("org.littleshoot:littleproxy:1.1.2")
    // implementation("net.lightbody.bmp:mitm:2.1.5")

    // qrcode
    implementation("com.google.zxing:core:3.4.1")
    implementation("com.google.zxing:javase:3.4.1")
    // simbot3-mirai
    implementation("love.forte.simbot.component:simbot-component-mirai-core:3.0.0.preview.3.0-292.0.1")
    implementation("love.forte.simbot.component:simbot-component-mirai-boot:3.0.0.preview.3.0-292.0.1")

    // log4j2
    implementation("org.apache.logging.log4j:log4j-api:2.17.1")
    implementation("org.apache.logging.log4j:log4j-core:2.17.1")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.17.1")

    testImplementation(kotlin("test"))
    implementation(compose.desktop.common)
    implementation(compose.desktop.currentOs)
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "11"
        javaParameters = true // -opt-in=kotlin.RequiresOptIn
        // freeCompilerArgs = freeCompilerArgs + listOf("-opt-in=kotlin.RequiresOptIn")
    }
}


compose.desktop {
    application {
        mainClass = "love.forte.simbot.mlh.window.WindowMainKt"
        //jvmArgs += listOf("-Xmx2G")
        nativeDistributions {
            targetFormats(
                TargetFormat.Dmg,
                TargetFormat.Deb,
                TargetFormat.Exe
            )



            description = "simbot下mirai组件首次设备登录辅助工具"
            packageName = "simbot-mirai-login-helper"
            packageVersion = "1.0.0"

            macOS {
                this.iconFile.set(project.file("icon.icns"))
                notarization {
                    this.ascProvider
                }
            }
            linux {
                this.iconFile.set(project.file("icon.png"))
                shortcut = true
            }
            windows {
                this.iconFile.set(project.file("icon.ico"))
                shortcut = true
            }


        }
    }
}