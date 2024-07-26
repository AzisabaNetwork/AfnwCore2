# AfnwCore2

あの世界を、もう一度。

夏季・冬季限定開放 [A Fall New World](https://www.azisaba.net/server-intro/a-fall-new-world/) で使用するPaper Pluginです。

![afnw](https://media.discordapp.net/attachments/911757060083970058/912641570468155402/unknown.png?width=1166&height=656)

## 開発環境

- [Intellij IDEA Ultimate](https://www.jetbrains.com/idea/)
- [Java SE Development Kit 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
- Java 17
- Gradle 8.9
- Paper 1.20.2

## 前提プラグイン

AfnwCore2を導入したPaperを起動するには以下のプラグインが `plugins/` に導入されている必要があります。

- [EssentialsX](https://essentialsx.net/downloads.html)
- Votifier
- [ItemStash](https://github.com/AzisabaNetwork/ItemStash)

## 使用API

- [paper-api:1.20.2-R0.1-SNAPSHOT](https://papermc.io/repo/repository/maven-public/)
  - [JavaDoc](https://papermc.io/repo/repository/maven-public/)
- [EssentialsX API](https://repo.essentialsx.net/releases/)

## Build

```sh 
git clone https://github.com/AfnwTeam/AfnwCore2.git
gradle build
```

なお、Intellij IDEAを使用している場合はすべてGUI上で使用可能です。
