
import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.serialization") version "1.6.10"
    id("org.jetbrains.compose") version "1.0.1"
}

group = "love.forte.simbot"
val subVersion = "0.7"
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

val simbotMirai = "3.0.0.preview.3.0-292.0.1-SNAPSHOT"


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


tasks.register("packageAndMove") {
    group = "compose desktop"
    dependsOn("package")
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

                //val newZipFileName = "$packageName-$os-$packageVersion.zip"
                //val zipFile = newOutputDir.file(newZipFileName).asFile
                //val fileOutputStream = FileOutputStream(zipFile)
                //// copy App and zip?
                //val zipOut = ZipOutputStream(fileOutputStream)
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



                ## 其他说明
                #### 版本号
                目前版本 `v3.$subVersion` 等同于 `v0.$subVersion`, 请在脑海中自动将版本最前的数字-3。
                由于 `macOS`(`dmg` & `pkg`) 打包必须保证版本号符合规则: `MAJOR[.MINOR][.PATCH]` 且:
                - `MAJOR` 是大于0的数字;
                - `MINOR` 是一个可选的非负整数;
                - `PATCH` 是一个可选的非负整数;
                因此对于`dmg`和`pkg`文件来说，不能使用最大版本号小于0的版本。因此选择将 `MAJOR` 数字与当前环境下 `simbot` 对应的 `MAJOR` 一致，也就是 `3`。

                有关于其他文件的版本说明请参考 [compose-jb/tutorials/Building native distribution/Specifying package version](https://github.com/JetBrains/compose-jb/tree/master/tutorials/Native_distributions_and_local_execution#specifying-package-version)

                <hr>



            """.trimIndent()


            file.writeText(autoGenerateText)
        }


    }
}
