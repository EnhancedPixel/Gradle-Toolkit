package dev.deftu.gradle.utils

import dev.deftu.gradle.exceptions.LoaderSpecificException
import org.gradle.api.Project

class MCDependencies(
    val mcData: MCData
) {

    inner class Fabric {

        val fabricLoaderVersion: String
            get() {
                if (!mcData.isFabric) {
                    throw LoaderSpecificException(ModLoader.FABRIC)
                }

                val fabricLoaderVersionOverride = mcData.project.propertyOr("fabric.loader.version", "")
                if (fabricLoaderVersionOverride.isNotEmpty()) {
                    return fabricLoaderVersionOverride
                }

                return MinecraftInfo.Fabric.LOADER_VERSION
            }

        val yarnVersion: String
            get() {
                if (!mcData.isFabric) {
                    throw LoaderSpecificException(ModLoader.FABRIC)
                }

                val yarnVersionOverride = mcData.project.propertyOr("fabric.yarn.version", "")
                if (yarnVersionOverride.isNotEmpty()) {
                    return yarnVersionOverride
                }

                return MinecraftInfo.Fabric.getYarnVersion(mcData.version)
            }

        val fabricApiVersion: String
            get() {
                if (!mcData.isFabric) {
                    throw LoaderSpecificException(ModLoader.FABRIC)
                }

                val fabricApiVersionOverride = mcData.project.propertyOr("fabric.api.version", "")
                if (fabricApiVersionOverride.isNotEmpty()) {
                    return fabricApiVersionOverride
                }

                return MinecraftInfo.Fabric.getFabricApiVersion(mcData.version)
            }

        val fabricLanguageKotlinVersion: String
            get() {
                if (!mcData.isFabric) {
                    throw LoaderSpecificException(ModLoader.FABRIC)
                }

                val kotlinVersionOverride = mcData.project.propertyOr("fabric.language.kotlin.version", "")

                return MinecraftInfo.Fabric.KOTLIN_DEP_VERSION
            }

        val modMenuVersion: String
            get() {
                if (!mcData.isFabric) {
                    throw LoaderSpecificException(ModLoader.FABRIC)
                }

                val modMenuVersionOverride = mcData.project.propertyOr("fabric.modmenu.version", "")
                if (modMenuVersionOverride.isNotEmpty()) {
                    return modMenuVersionOverride
                }

                val (_, version) = MinecraftInfo.Fabric.getModMenuDependency(mcData.version)
                return version
            }

        val modMenuDependency: String
            get() {
                if (!mcData.isFabric) {
                    throw LoaderSpecificException(ModLoader.FABRIC)
                }

                val modMenuDependencyOverride = mcData.project.propertyOr("fabric.modmenu.dependency", "")
                if (modMenuDependencyOverride.isNotEmpty()) {
                    return modMenuDependencyOverride
                }

                val (group, version) = MinecraftInfo.Fabric.getModMenuDependency(mcData.version)
                return "$group$version"
            }

    }

    inner class LegacyFabric {

        val legacyYarnVersion: String
            get() {
                if (!mcData.isLegacyFabric) {
                    throw LoaderSpecificException(ModLoader.FABRIC)
                }

                val legacyYarnVersionOverride = mcData.project.propertyOr("fabric.yarn.version", "")
                if (legacyYarnVersionOverride.isNotEmpty()) {
                    return legacyYarnVersionOverride
                }

                return MinecraftInfo.LegacyFabric.getLegacyYarnVersion(mcData.version)
            }

        val legacyFabricApiVersion: String
            get() {
                if (!mcData.isLegacyFabric) {
                    throw LoaderSpecificException(ModLoader.FABRIC)
                }

                val legacyFabricApiVersionOverride = mcData.project.propertyOr("fabric.api.version", "")
                if (legacyFabricApiVersionOverride.isNotEmpty()) {
                    return legacyFabricApiVersionOverride
                }

                return MinecraftInfo.LegacyFabric.getLegacyFabricApiVersion(mcData.version)
            }

    }

    inner class ForgeLike {

        val kotlinForForgeVersion: String
            get() {
                if (!mcData.isForgeLike) {
                    throw LoaderSpecificException(ModLoader.FORGE)
                }

                val kotlinForForgeVersionOverride = mcData.project.propertyOr("forge.kotlin.version", "")
                if (kotlinForForgeVersionOverride.isNotEmpty()) {
                    return kotlinForForgeVersionOverride
                }

                return MinecraftInfo.ForgeLike.getKotlinForForgeVersion(mcData.version)
            }

    }

    inner class Forge {

        val forgeVersion: String
            get() {
                if (!mcData.isForge) {
                    throw LoaderSpecificException(ModLoader.FORGE)
                }

                val forgeVersionOverride = mcData.project.propertyOr("forge.version", "")
                if (forgeVersionOverride.isNotEmpty()) {
                    return forgeVersionOverride
                }

                return MinecraftInfo.Forge.getForgeVersion(mcData.version)
            }

        val mcpDependency: String
            get() {
                if (!mcData.isForge) {
                    throw LoaderSpecificException(ModLoader.FORGE)
                }

                val mcpDependencyOverride = mcData.project.propertyOr("forge.mcp.dependency", "")
                if (mcpDependencyOverride.isNotEmpty()) {
                    return mcpDependencyOverride
                }

                return MinecraftInfo.Forge.getMcpDependency(mcData.version)
            }

    }

    inner class NeoForged {

        val neoForgeVersion: String
            get() {
                if (!mcData.isNeoForge) {
                    throw LoaderSpecificException(ModLoader.NEOFORGE)
                }

                val neoForgeVersionOverride = mcData.project.propertyOr("neoforge.version", "")
                if (neoForgeVersionOverride.isNotEmpty()) {
                    return neoForgeVersionOverride
                }

                return MinecraftInfo.NeoForged.getNeoForgedVersion(mcData.version)
            }

    }

    val fabric = Fabric()
    val legacyFabric = LegacyFabric()
    val forgeLike = ForgeLike()
    val forge = Forge()
    val neoForged = NeoForged()

}

data class MCData(
    val project: Project,
    val isPresent: Boolean,
    val version: MinecraftVersion,
    val loader: ModLoader
) {

    val isFabric: Boolean
        get() = loader == ModLoader.FABRIC

    val isLegacyFabric: Boolean
        get() = loader == ModLoader.FABRIC && version < MinecraftVersion.VERSION_1_13_2

    val isForge: Boolean
        get() = loader == ModLoader.FORGE

    val isNeoForge: Boolean
        get() = loader == ModLoader.NEOFORGE

    val isForgeLike: Boolean
        get() = isForge || isNeoForge

    val isModLauncher: Boolean
        get() = loader == ModLoader.FORGE && version >= MinecraftVersion.VERSION_1_14

    val isLegacyForge: Boolean
        get() = loader == ModLoader.FORGE && version < MinecraftVersion.VERSION_1_14

    val dependencies = MCDependencies(this)

    override fun toString(): String {
        return "$version-$loader"
    }

    companion object {

        @JvmStatic
        val versionRegex = "(?<major>\\d+).(?<minor>\\d+).?(?<patch>\\d+)?".toRegex()

        /**
         * Gets the project's Minecraft version, either by the property or by inferring it from the project's name.
         */
        private val Project.minecraftVersion: String
            get() = propertyOr("minecraft.version", name, prefix = "")

        private val Project.modLoader: ModLoader
            get() = ModLoader.from(propertyOr(
                "loom.platform",
                name,
                prefix = ""
            ))

        @JvmStatic
        fun from(project: Project): MCData {
            val extension = project.extensions.findByName("mcData") as MCData?
            if (extension != null) return extension

            val isValidProject = project.hasProperty("minecraft.version") || project.isMultiversionProject()
            if (!isValidProject) return MCData(project, false, MinecraftVersion.UNKNOWN, ModLoader.OTHER)

            val (major, minor, patch) = match(project.minecraftVersion)
            val data = MCData(project, true, MinecraftVersion.from(major, minor, patch), project.modLoader)
            project.extensions.add("mcData", data)
            return data
        }

        private fun match(version: String): Triple<Int, Int, Int> {
            val match = versionRegex.find(version) ?: throw IllegalArgumentException("Invalid version format: $version")
            val groups = match.groups

            val major = groups["major"]?.value?.toInt() ?: throw IllegalArgumentException("Invalid version format: $version")
            val minor = groups["minor"]?.value?.toInt() ?: throw IllegalArgumentException("Invalid version format: $version")
            val patch = groups["patch"]?.value?.toInt() ?: 0
            return Triple(major, minor, patch)
        }

    }
}