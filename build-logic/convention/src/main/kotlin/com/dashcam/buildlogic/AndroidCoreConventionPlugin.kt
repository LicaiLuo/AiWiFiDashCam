package com.dashcam.buildlogic

import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidCoreConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.plugins.apply(AndroidLibraryConventionPlugin::class.java)
        target.addImplementation("androidx-core-ktx")
    }
}
