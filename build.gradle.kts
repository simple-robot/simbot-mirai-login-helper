import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.serialization") version "1.6.10"
    id("org.jetbrains.compose") version "1.0.1"
}

group = "love.forte.simbot"
version = "3.0.1"

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
    val simbotMirai = "3.0.0.preview.3.0-292.0.1-SNAPSHOT"
    implementation("love.forte.simbot.component:simbot-component-mirai-core:$simbotMirai")
    //implementation("love.forte.simbot.component:simbot-component-mirai-boot:3.0.0.preview.3.0-292.0.1")

    // log4j2
    implementation("org.apache.logging.log4j:log4j-api:2.17.1")
    implementation("org.apache.logging.log4j:log4j-core:2.17.1")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.17.1")

    //testImplementation(kotlin("test"))
    //implementation(compose.desktop.common)
    implementation(compose.desktop.currentOs)
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "11"
        javaParameters = true
        // -opt-in=kotlin.RequiresOptIn
        // -Xopt-in=kotlin.RequiresOptIn
        freeCompilerArgs = freeCompilerArgs + listOf("-Xopt-in=kotlin.RequiresOptIn")
    }
}

compose.desktop {
    application {
        mainClass = "love.forte.simbot.mlh.MainKt"
        jvmArgs += listOf(
            "-XX:ErrorFile=.log/hs_err.log",
            "-XX:-HeapDumpOnOutOfMemoryError",
            "-XX:HeapDumpPath=.log/dump.hprof",
        )
        nativeDistributions {
            targetFormats(
                TargetFormat.Deb,
                TargetFormat.Dmg,
                TargetFormat.Exe,
            )


            // outputBaseDir
            //modules("javax.naming")

            licenseFile.set(project.file("_COPYING.MERGE"))
            // description = "simbot下用于进行mirai登录验证的辅助工具"
            packageName = "simbotMiraiLoginHelper"
            packageVersion = project.version.toString()
            copyright = "(C) 2022 ForteScarlet. All rights reserved."


            macOS {
                this.iconFile.set(project.file("icon.icns"))
            }

            windows {
                this.iconFile.set(project.file("icon.ico"))
                shortcut = true
                menuGroup = "simbot"
            }

            linux {
                this.iconFile.set(project.file("icon.png"))
                shortcut = true
                debMaintainer = "ForteScarlet@163.com"
                menuGroup = "simbot"
            }


        }
    }
}

// internal val outputs =

tasks.register("packageAndCopy") {
    group = "compose desktop"
    dependsOn("package")
    doLast("packageAndCopyDoLast") {
        val nativeDistributions = compose.desktop.application.nativeDistributions

        val outputBaseDir = nativeDistributions.outputBaseDir
        val outputMain = outputBaseDir.dir("main").orNull
            ?: throw NullPointerException("No outputDir: $outputBaseDir/main")

        val newOutputDir = outputMain.dir("distributions")
        newOutputDir.asFile.mkdirs()

        println("outputMain: $outputMain")

        nativeDistributions.targetFormats
            .forEach { targetOsExtension ->
                val targetOsExtensionName = targetOsExtension.name.toLowerCase()
                val currentFile = outputMain.files(targetOsExtensionName)
                    .asFileTree
                    .filter { it.extension == targetOsExtensionName }
                    .firstOrNull()
                    ?.takeIf { it.exists() }
                    ?: return@forEach

                // 应该就只有一个

                val packageName = nativeDistributions.packageName
                val packageVersion = nativeDistributions.packageVersion ?: project.version

                val os = when (targetOsExtension) {
                    TargetFormat.Deb -> "linux"
                    TargetFormat.Rpm -> "linux"
                    TargetFormat.Dmg -> "macOS"
                    TargetFormat.Pkg -> "macOS"
                    TargetFormat.Exe -> "windows"
                    TargetFormat.Msi -> "windows"
                    else -> "unknown"
                }

                val newFileName = "$packageName-$os-$packageVersion.$targetOsExtensionName"
                val newFile = newOutputDir.file(newFileName).asFile
                newFile.createNewFile()

                currentFile.copyTo(target = newFile, overwrite = true)
            }

        /*
            Deb
            Dmg
            Exe
         */

    }
}

