rootProject.name = "scoreboard"

include("api")
project(":api").name = "scoreboard-api"

// platforms
sequenceOf("v1_8_R3", "v1_16_R3", "v1_17_R1").forEach {
    include(":platform:platform-$it")
    project(":platform:platform-$it").name = "scoreboard-platform-$it"
}