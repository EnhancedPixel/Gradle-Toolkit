package xyz.unifycraft.gradle

apply(plugin = "xyz.unifycraft.gradle.snippets.repo")

pluginManager.withPlugin("java") {
    apply(plugin = "xyz.unifycraft.gradle.snippets.java")
    apply(plugin = "xyz.unifycraft.gradle.snippets.resources")
}
pluginManager.withPlugin("gg.essential.loom") {
    apply(plugin = "xyz.unifycraft.gradle.snippets.loom")
}
