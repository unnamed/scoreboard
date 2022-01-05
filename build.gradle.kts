plugins {
    `java-library`
    `maven-publish`
}

subprojects {
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")

    repositories {
        mavenLocal()
        maven("https://repo.unnamed.team/repository/unnamed-public/")
        maven("https://repo.codemc.io/repository/nms/")
        maven("https://oss.sonatype.org/content/repositories/snapshots")
        maven("https://oss.sonatype.org/content/repositories/central")
        mavenCentral()
    }

    publishing {
        publications {
            create<MavenPublication>("maven") {
                groupId = project.group as String
                artifactId = project.name
                version = project.version as String

                from(components["java"])
            }
        }
    }
}