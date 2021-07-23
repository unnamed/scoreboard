plugins {
    `java-library`
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8;
    targetCompatibility = JavaVersion.VERSION_1_8;
}

dependencies {
    api("org.jetbrains:annotations:21.0.1")
    api("team.unnamed.common:commons-validation:2.0.0-SNAPSHOT")
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")

    compileOnly("org.spigotmc:spigot:1.8.8-R0.1-SNAPSHOT")
    compileOnly("org.spigotmc:spigot:1.16.5-R0.1-SNAPSHOT")
}