package xyz.deftu.gradle.tools

import gradle.kotlin.dsl.accessors._89edf7476531f98eff64d602eb448354.publishing
import gradle.kotlin.dsl.accessors._89edf7476531f98eff64d602eb448354.signing
import gradle.kotlin.dsl.accessors._ca59d7b33a587bae1dcf00e1f22a5064.java
import xyz.deftu.gradle.MCData
import xyz.deftu.gradle.ModData
import xyz.deftu.gradle.ProjectData
import xyz.deftu.gradle.utils.isMultiversionProject

plugins {
    `maven-publish`
    signing
    java
}

val mcData = MCData.from(project)
val modData = ModData.from(project)
val projectData = ProjectData.from(project)

pluginManager.withPlugin("java") {
    java {
        withSourcesJar()
    }
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("mavenJava") {
                if (modData.present) {
                    artifactId = if (isMultiversionProject()) "${modData.name}-${mcData.versionStr}" else modData.name
                    groupId = modData.group
                    version = modData.version
                } else if (projectData.present) {
                    artifactId = projectData.name
                    groupId = projectData.group
                    version = projectData.version
                }

                if (pluginManager.hasPlugin("xyz.deftu.gradle.tools.shadow")) {
                    val fatJar by tasks.getting
                    artifact(fatJar) {
                        classifier = null
                    }
                    val sourcesJar by tasks.getting
                    artifact(sourcesJar)

                    pluginManager.withPlugin("java") {
                        val javadocJar = project.tasks.findByName("javadocJar") as Jar?
                        if (javadocJar != null && javadocJar.enabled) {
                            artifact(javadocJar)
                        }
                    }
                } else from(components["java"])
            }
        }

        repositories {
            if (project.hasProperty("deftu.publishing.username") && project.hasProperty("deftu.publishing.password")) {
                fun MavenArtifactRepository.applyCredentials() {
                    authentication.create<BasicAuthentication>("basic")
                    credentials {
                        username = property("deftu.publishing.username")?.toString()
                        password = property("deftu.publishing.password")?.toString()
                    }
                }

                maven {
                    name = "DeftuReleases"
                    url = uri("https://maven.deftu.xyz/releases")
                    applyCredentials()
                }

                maven {
                    name = "DeftuSnapshots"
                    url = uri("https://maven.deftu.xyz/snapshots")
                    applyCredentials()
                }
            }
        }
    }

    if (project.hasProperty("signing.password")) {
        signing {
            publishing.publications.forEach(::sign)
        }
    }
}
