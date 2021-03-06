
import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.serialization") version "1.6.10"
    id("org.jetbrains.compose") version "1.1.1"
}

group = "love.forte.simbot"
val subVersion = "0.10"
version = "3.$subVersion"

repositories {
    mavenCentral()
    google()

    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

configurations.all {
    resolutionStrategy {
        cacheChangingModulesFor(1, "seconds")
    }
}

val simbot = "3.0.0.preview.17.0"
val simbotMirai = "3.0.0.0.preview.11.0"


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
    implementation("love.forte.simbot.component:simbot-component-mirai-core:$simbotMirai")
    implementation("love.forte.simbot:simbot-core:$simbot")

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
                TargetFormat.Rpm,
                TargetFormat.Deb,

                //TargetFormat.Pkg,
                TargetFormat.Dmg,

                TargetFormat.Msi,
                TargetFormat.Exe,
            )


            // outputBaseDir
            modules("java.naming")

            licenseFile.set(project.file("_COPYING.MERGE"))
            // description = "simbot???????????????mirai???????????????????????????"
            packageName = "simbotMiraiLoginHelper"
            packageVersion = project.version.toString()
            copyright = "(C) 2022 ForteScarlet. All rights reserved."


            macOS {
                iconFile.set(project.file("icon.icns"))
            }

            windows {
                iconFile.set(project.file("icon.ico"))
                shortcut = true
                menuGroup = "simbot"
            }

            linux {
                iconFile.set(project.file("icon.png"))
                shortcut = true
                debMaintainer = "ForteScarlet@163.com"
                menuGroup = "simbot"
            }


        }
    }
}



tasks.register("packageAndMove") {
    group = "compose desktop"

    dependsOn("package", ) // "createDistributable"

    doLast("packageAndMoveDoLast") {
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

                // ?????????????????????

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

                // val zipFileExtra = when (os) {
                //     "macOS", "windows" -> "zip"
                //     else -> "tar"
                // }
                //
                // val newZipFileName = "$packageName-$os-$packageVersion.$zipFileExtra"
                // val zipFile = newOutputDir.file(newZipFileName).asFile
                // val fileOutputStream = FileOutputStream(zipFile)
                // // copy App and zip?
                // val zipOut = ZipOutputStream(fileOutputStream)
                // GZIPOutputStream(fileOutputStream)
            }

        /*
            Deb
            Dmg
            Exe
         */

    }
}



tasks.create("createChangelog") {
    group = "build"
    doFirst {
        val version = "v${project.version}"
        println("Generate change log for $version ...")
        // configurations.runtimeClasspath
        val changelogDir = project.file(".changelog").also {
            it.mkdirs()
        }
        val file = File(changelogDir, "$version.md")
        if (!file.exists()) {
            file.createNewFile()
            val autoGenerateText = """
                real version: `v0.$subVersion`
                
                simbot-mirai version: `v$simbotMirai`



                ## ????????????
                #### ?????????
                ???????????? `v3.$subVersion` ????????? `v0.$subVersion`, ?????????????????????????????????????????????-3???
                ?????? `macOS`(`dmg` & `pkg`) ???????????????????????????????????????: `MAJOR[.MINOR][.PATCH]` ???:
                - `MAJOR` ?????????0?????????;
                - `MINOR` ??????????????????????????????;
                - `PATCH` ??????????????????????????????;
                ????????????`dmg`???`pkg`????????????????????????????????????????????????0??????????????????????????? `MAJOR` ???????????????????????? `simbot` ????????? `MAJOR` ?????????????????? `3`???

                ????????????????????????????????????????????? [compose-jb/tutorials/Building native distribution/Specifying package version](https://github.com/JetBrains/compose-jb/tree/master/tutorials/Native_distributions_and_local_execution#specifying-package-version)

                <hr>

                ## ????????????
                ??? [release](https://github.com/simple-robot/simbot-mirai-login-helper/releases/tag/v$version) ??? `Assets` ????????????????????????????????????
                
                #### ????????????
                - [linux(deb)](https://github.com/simple-robot/simbot-mirai-login-helper/releases/download/v$version/simbotMiraiLoginHelper-linux-$version.deb)
                - [linux(rpm)](https://github.com/simple-robot/simbot-mirai-login-helper/releases/download/v$version/simbotMiraiLoginHelper-linux-$version.rpm)
                - [macOS](https://github.com/simple-robot/simbot-mirai-login-helper/releases/download/v$version/simbotMiraiLoginHelper-macOS-$version.dmg)
                - [windows(exe)](https://github.com/simple-robot/simbot-mirai-login-helper/releases/download/v$version/simbotMiraiLoginHelper-windows-$version.exe)
                - [windows(msi)](https://github.com/simple-robot/simbot-mirai-login-helper/releases/download/v$version/simbotMiraiLoginHelper-windows-$version.msi)


            """.trimIndent()


            file.writeText(autoGenerateText)
        }


    }
}
