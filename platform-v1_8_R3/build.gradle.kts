plugins {
    `java-library`
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8;
    targetCompatibility = JavaVersion.VERSION_1_8;
}

dependencies {
    api(project(":scoreboard-api"))
    compileOnly("org.spigotmc:spigot:1.8.8-R0.1-SNAPSHOT")
}
