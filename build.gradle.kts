import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("plugin.lombok") version "2.1.20"
    id("io.freefair.lombok") version "8.10"
    id("java-library")
    id("java")
    id("org.jetbrains.dokka") version "1.9.20"
    kotlin("jvm") version "2.1.20"
    id("com.palantir.git-version") version "0.12.3"
    alias(libs.plugins.shadow)
}

val gitVersion: groovy.lang.Closure<String> by extra
val gitVersionString = gitVersion()
extra["gitVersionString"] = gitVersionString
println("ThePitUltimate version: $gitVersionString")

group = "me.huanmeng"
version = "4.5.0"

repositories {
    maven("https://maven.cleanroommc.com")
    maven("https://maven.aliyun.com/repository/public/")
    mavenCentral()
    maven("https://repo.crazycrew.us/releases")
    maven("https://repo.codemc.io/repository/nms/")
    maven("https://repo.codemc.io/repository/maven-public/")
    maven("https://repo.dmulloy2.net/repository/public/")
    maven("https://repo.inventivetalent.org/content/groups/public/")
    maven("https://jitpack.io")
    maven("https://repo.rosewooddev.io/repository/public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.panda-lang.org/releases")
}

dependencies {
    compileOnly(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(fileTree(mapOf("dir" to "packLib", "include" to listOf("*.jar"))))
    api(libs.reflectionhelper)
    api(libs.hutool.crypto)
    api(libs.book)
    api(libs.slf4j)
    api(libs.litecommands)
    api(libs.adventure.bukkit)
    api(libs.kotlin)
    implementation(kotlin("reflect"))
    implementation("zone.rong:imaginebreaker:2.1")
    compileOnly("com.caoccao.javet:javet:3.1.4")
    compileOnly("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.projectlombok:lombok:1.18.32")
    compileOnly("com.github.ben-manes.caffeine:caffeine:3.1.8")
    compileOnly("com.github.f4b6a3:uuid-creator:6.0.0")
    compileOnly("org.mongojack:mongojack:5.0.1")
    compileOnly("org.mongodb:mongodb-driver-sync:5.2.0")
    compileOnly("us.crazycrew.crazycrates:api:0.7")
    compileOnly(libs.protocollib)
    compileOnly(libs.luckperms)
    compileOnly(libs.papi)
    compileOnly(libs.narshorn)
    compileOnly(libs.jedis)
    compileOnly(libs.fastutil)
    compileOnly(libs.spigot.get8())
    compileOnly(libs.playerpoints)
    compileOnly(libs.decentholograms)
}

kotlin {
    jvmToolchain(17)
}

val injectGitVersion by tasks.registering {
    group = "versioning"
    description = "Injects Git version into source code before compilation."

    val inputDirs = listOf("src/main/kotlin", "src/main/java")
    val outputDir = file("build/generated/gitProcessed")

    outputs.dir(outputDir)

    doLast {
        delete(outputDir)
        inputDirs.forEach { srcDir ->
            fileTree(srcDir).matching {
                include("**/*.kt")
            }.forEach { srcFile ->
                val relativePath = srcFile.relativeTo(file(srcDir))
                val targetFile = outputDir.resolve(relativePath)
                targetFile.parentFile.mkdirs()

                val content = srcFile.readText()
                val replaced = content.replace("%git_version%", gitVersionString)
                targetFile.writeText(replaced)
            }
        }
        println("🔄 Git version injected into generated sources.")
    }
}

val lastFin by tasks.registering {
    group = "versioning"
    description = "Injects Git version into source code before compilation."

    val inputDirs = listOf("src/main/kotlin", "src/main/java")
    val outputDir = file("build/generated/gitProcessed")

    outputs.dir(outputDir)

    doLast {
        delete(outputDir)
        inputDirs.forEach { srcDir ->
            fileTree(srcDir).matching {
                include("**/*.kt")
            }.forEach { srcFile ->
                val relativePath = srcFile.relativeTo(file(srcDir))
                val targetFile = outputDir.resolve(relativePath)
                targetFile.parentFile.mkdirs()

                val content = srcFile.readText()
                val replaced = content.replace(gitVersionString, "%git_version%")
                targetFile.writeText(replaced)
            }
        }
        println("🔄 Git version restored into generated sources.")
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    dependsOn(injectGitVersion)
    finalizedBy(lastFin)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    dependsOn(injectGitVersion)
    finalizedBy(lastFin)
}

tasks.named<ShadowJar>("shadowJar") {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    archiveFileName.set("ThePitUltimate-$version.jar")
    exclude("META-INF/**")
    relocate("pku.yim.license", "cn.charlotte.pit.license")
    relocate("panda", "cn.charlotte.pit.libs")
    relocate("dev.rollczi", "cn.charlotte.pit.libs")
    relocate("cn.hutool", "cn.charlotte.pit.libs")
    relocate("net.kyori", "cn.charlotte.pit.libs")
    relocate("net.jodah", "cn.charlotte.pit.libs")
    relocate("net.jitse", "cn.charlotte.pit.libs")
    relocate("xyz.upperlevel.spigot", "cn.charlotte.pit.libs")
    exclude("kotlin/**", "junit/**", "org/junit/**")
    from("build/tmp/processed-resources")
    mergeServiceFiles()
}

tasks.register<Jar>("dokkaHtmlJar") {
    dependsOn(tasks.dokkaHtml)
    archiveClassifier.set("javadoc")
    from(tasks.dokkaHtml)
}

tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
    source = sourceSets["main"].allJava
    classpath = files()
}

tasks.build {
    dependsOn(tasks.shadowJar)
}
