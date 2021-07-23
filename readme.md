# unnamed/scoreboard ![Build Status](https://img.shields.io/github/workflow/status/unnamed/scoreboard/build/main) [![MIT License](https://img.shields.io/badge/license-MIT-blue)](license.txt)
unnamed/scoreboard *(or µboard)* is a very fast and lightweight library for the creation of
Minecraft scoreboards in the Bukkit API. µboard has an extensible API that can be adapted
to every Minecraft server version.
## Download
You can simply download the JAR from GitHub from the Releases section or using 
[Maven](https://maven.apache.org/) or [Gradle](https://gradle.org/) (recommended)
### Repository
**Maven - pom.xml**
```xml
<repository>
    <id>unnamed-public</id>
    <url>https://repo.unnamed.team/repository/unnamed-public/</url>
</repository>
```
**Gradle - build.gradle(.kts)**

build.gradle (Groovy DSL)
```groovy
repositories {
    maven { url 'https://repo.unnamed.team/repository/unnamed-public/' }
}
```

build.gradle.kts (Kotlin DSL)
```kotlin
repositories {
    maven("https://repo.unnamed.team/repository/unnamed-public/")
}
```

### Dependency
- Latest snapshot: not yet!
- Latest release: not yet!

**Maven - pom.xml**
```xml
<dependency>
    <groupId>team.unnamed.scoreboard</groupId>
    <artifactId>scoreboard-api</artifactId>
    <version>VERSION</version>
</dependency>
```
**Gradle - build.gradle(.kts)**

build.gradle (Groovy DSL)
```groovy
dependencies {
    implementation 'team.unnamed.scoreboard:scoreboard-api:VERSION'
}
```

build.gradle.kts (Kotlin DSL)
```kotlin
dependencies {
    implementation("team.unnamed.scoreboard:scoreboard-api:VERSION")
}
```