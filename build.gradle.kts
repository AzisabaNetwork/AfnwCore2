plugins {
    java
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "net.azisaba.afnw"
version = "1.5.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        name = "papermc-repo"
        url = uri("https://papermc.io/repo/repository/maven-public/")
    }
    maven {
        name = "sonatype"
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }
    maven {
        name = "azisaba"
        url = uri("https://repo.azisaba.net/repository/maven-public/")
    }
    maven {
        name = "essentialsx"
        url = uri("https://repo.essentialsx.net/releases/")
    }
    maven {
        name = "lumine"
        url = uri("https://mvn.lumine.io/repository/maven-public/")
    }
    if (properties["azisabaNmsUsername"] != null && properties["azisabaNmsPassword"] != null) {
        maven {
            name = "azisabaNms"
            credentials(PasswordCredentials::class)
            url = uri("https://repo.azisaba.net/repository/nms/")
        }
    }
    mavenLocal()
}

dependencies {
//    implementation("net.blueberrymc:native-util:2.1.0")
    implementation("xyz.acrylicstyle.java-util:common:2.0.0-SNAPSHOT")
    implementation("xyz.acrylicstyle.java-util:expression:2.0.0-SNAPSHOT")
    compileOnly("io.papermc.paper:paper-api:1.20.2-R0.1-SNAPSHOT")
    compileOnly("org.spigotmc:spigot:1.20.2-R0.1-SNAPSHOT")
    compileOnly("net.azisaba.ballotbox:receiver:1.0.1")
    compileOnly("net.azisaba:ItemStash:1.0.0-SNAPSHOT")
    compileOnly("net.essentialsx:EssentialsX:2.20.1")
    compileOnly("io.lumine:Mythic-Dist:5.4.0")
}

java.toolchain.languageVersion.set(JavaLanguageVersion.of(17))

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
