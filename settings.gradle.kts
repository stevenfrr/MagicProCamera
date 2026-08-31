# Project-wide Gradle settings.
# IDE (e.g. Android Studio) users:
# Gradle settings configured through the IDE are ignored when building from the command line, check
# build.gradle files instead.

# For more details on how to configure your build environment visit
# http://www.gradle.org/guide/gradle_and_build_environment/settings.gradle

plugins {
    id "com.gradle.enterprise" version "3.13.4"
}

gradleEnterprise {
    buildScan {
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
    }
}

rootProject.name = "MagicProCamera"

include(":app")
