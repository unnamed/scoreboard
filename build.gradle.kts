allprojects {
    repositories {
        mavenLocal()
        maven("https://repo.unnamed.team/repository/unnamed-public/")
        maven("https://repo.codemc.io/repository/nms/")
        maven("https://oss.sonatype.org/content/repositories/snapshots")
        maven("https://oss.sonatype.org/content/repositories/central")
        mavenCentral()
    }
}