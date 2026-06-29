import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

fun properties(key: String) = project.findProperty(key).toString()

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.4.0"
    id("org.jetbrains.intellij.platform") version "2.16.0"
}

group = properties("pluginGroup")
version = properties("pluginVersion")

// Configure project's dependencies
repositories {
    mavenCentral()

    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        val version = providers.gradleProperty("platformVersion")
        val type = providers.gradleProperty("platformType")
        create(type, version) {
            useInstaller.set(true)
            useCache.set(true)
        }

        compatiblePlugins("com.jetbrains.php")
        bundledPlugins(
            "com.intellij.java",
            "com.intellij.modules.xml",
            "com.intellij.modules.php-capable",
            "com.intellij.modules.json",
        )
        testBundledPlugins(
            "com.intellij.java",
            "com.intellij.modules.xml",
            "com.intellij.modules.php-capable",
            "com.intellij.modules.json",
        )
        bundledModules(
            "com.intellij.modules.ultimate",
            "intellij.spellchecker",
            "intellij.regexp",
        )
        testBundledModules(
            "com.intellij.modules.ultimate",
            "intellij.spellchecker",
            "intellij.regexp",
        )

        testFramework(TestFrameworkType.Platform)
        testFramework(TestFrameworkType.Plugin.Java)
    }

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine:5.11.4")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

intellijPlatform {
    val version = providers.gradleProperty("platformVersion")
    val type = providers.gradleProperty("platformType")

    pluginConfiguration {
        name = properties("pluginName")
    }

    buildSearchableOptions = false

    pluginVerification {
        ides {
            create(type, version) {
                useInstaller.set(true)
                useCache.set(true)
            }
        }
    }
}

tasks {
    // Set the JVM compatibility versions
    properties("javaVersion").let {
        withType<JavaCompile> {
            sourceCompatibility = it
            targetCompatibility = it
        }
        withType<KotlinCompile> {
            compilerOptions {
                jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.fromTarget(it))
            }
        }
    }

    wrapper {
        gradleVersion = properties("gradleVersion")
    }

    patchPluginXml {
        version = properties("pluginVersion")
        sinceBuild.set(properties("pluginSinceBuild"))
        untilBuild.set(properties("pluginUntilBuild"))
        changeNotes.set(file("src/main/resources/META-INF/change-notes.html").readText().replace("<html>", "").replace("</html>", ""))
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
        // pluginVersion is based on the SemVer (https://semver.org) and supports pre-release labels, like 2.1.7-alpha.3
        // Specify pre-release label to publish the plugin in a custom Release Channel automatically. Read more:
        // https://plugins.jetbrains.com/docs/intellij/deployment.html#specifying-a-release-channel
        channels.set(listOf(properties("pluginVersion").split('-').getOrElse(1) { "default" }.split('.').first()))
    }

    test {
        // Support "setUp" like "BasePlatformTestCase::setUp" as valid test structure
        useJUnitPlatform {
            includeEngines("junit-vintage", "junit-jupiter")
        }

        // Disable CDS warning about java.system.class.loader
        jvmArgs("-Xshare:off")

        // Disable SVG rendering to work around JSvg IllegalAccessError in IntelliJ 2025.3.x
        systemProperty("idea.ui.icons.svg.disabled", "true")
        systemProperty("java.awt.headless", "true")
    }
}
