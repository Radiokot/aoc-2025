plugins {
    kotlin("jvm") version "2.2.21"
}

sourceSets {
    main {
        kotlin.srcDir("src")
    }
}

tasks {
    wrapper {
        gradleVersion = "8.7"
        distributionType = Wrapper.DistributionType.BIN
    }
}
