rootProject.name = "scoreboard"

include("api")
project(":api").name = "scoreboard-api"

// platforms
sequenceOf("v1_8_R3").forEach {
    include("platform-$it")
    project(":platform-$it").name = "scoreboard-platform-$it"
}
