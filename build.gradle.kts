plugins {
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.19"
    java
    id("com.gradleup.shadow") version "9.3.0"
}

group = "net.azisaba.afnw"
version = "2.1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://repo.azisaba.net/repository/maven-public/")
    maven("https://repo.essentialsx.net/releases/")
    maven("https://mvn.lumine.io/repository/maven-public/")
    maven("https://jitpack.io")
}

dependencies {
//    implementation("net.blueberrymc:native-util:2.1.0")
    implementation("xyz.acrylicstyle.java-util:common:2.1.1")
    implementation("xyz.acrylicstyle.java-util:expression:2.1.1")
    compileOnly("net.azisaba.ballotbox:receiver:1.0.1")
    compileOnly("net.azisaba:ItemStash:1.0.0-SNAPSHOT")
    compileOnly("net.essentialsx:EssentialsX:2.20.1") {
        exclude("org.spigotmc", "spigot-api")
    }
    compileOnly("io.lumine:Mythic-Dist:5.4.0")
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
    compileOnly("net.azisaba:TAB-BukkitBridge:3.1.0")
    compileOnly("org.jetbrains:annotations:26.0.1")
    paperweight.paperDevBundle("1.21.11-R0.1-SNAPSHOT")
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(21))

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }

    processResources {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE

        from(sourceSets.main.get().resources.srcDirs) {
            filter(org.apache.tools.ant.filters.ReplaceTokens::class, mapOf("tokens" to mapOf("version" to project.version.toString())))
            filteringCharset = "UTF-8"
        }
    }

    shadowJar {
        relocate("xyz.acrylicstyle.util", "net.azisaba.afnwcore2.lib.xyz.acrylicstyle.util")
    }
}
