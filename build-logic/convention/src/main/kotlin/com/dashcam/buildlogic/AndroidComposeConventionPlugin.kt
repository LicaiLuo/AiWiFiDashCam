package com.dashcam.buildlogic

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidComposeConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.plugins.apply("org.jetbrains.kotlin.plugin.compose")

        target.plugins.withId("com.android.application") {
            target.extensions.configure<ApplicationExtension> {
                buildFeatures {
                    compose = true
                }
            }
        }

        target.plugins.withId("com.android.library") {
            target.extensions.configure<LibraryExtension> {
                buildFeatures {
                    compose = true
                }
            }
        }

        target.addImplementationPlatform("androidx-compose-bom")
        target.addImplementation("androidx-activity-compose")
        target.addImplementation("androidx-compose-ui")
        target.addImplementation("androidx-compose-ui-graphics")
        target.addImplementation("androidx-compose-ui-tooling-preview")
        target.addImplementation("androidx-compose-material3")
        target.addDebugImplementation("androidx-compose-ui-tooling")
        target.addDebugImplementation("androidx-compose-ui-test-manifest")
        target.addAndroidTestImplementationPlatform("androidx-compose-bom")
        target.addAndroidTestImplementation("androidx-compose-ui-test-junit4")
    }
}
