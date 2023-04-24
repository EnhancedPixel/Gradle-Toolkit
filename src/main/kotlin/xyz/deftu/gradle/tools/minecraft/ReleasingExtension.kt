package xyz.deftu.gradle.tools.minecraft

import com.modrinth.minotaur.dependencies.Dependency
import net.darkhax.curseforgegradle.UploadArtifact
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.bundling.Zip
import xyz.deftu.gradle.MCData
import java.io.File

abstract class ReleasingExtension(
    project: Project
) {
    abstract val version: Property<String>
    abstract val versionType: Property<VersionType>
    abstract val gameVersions: ListProperty<String>
    abstract val loaders: ListProperty<String>
    abstract val file: Property<Zip>

    abstract val describeFabricWithQuilt: Property<Boolean>
    abstract val releaseName: Property<String>

    abstract val useSourcesJar: Property<Boolean>
    abstract val sourcesJar: Property<Zip>
    abstract val useJavadocJar: Property<Boolean>
    abstract val javadocJar: Property<Zip>

    // Changelog
    abstract val changelog: Property<String>
    abstract val changelogFile: Property<File>

    // Platform
    abstract val modrinth: PublishingModrinthExtension
        @Nested get
    abstract val curseforge: PublishingCurseForgeExtension
        @Nested get
    abstract val github: PublishingGitHubExtension
        @Nested get

    init {
        val mcData = MCData.from(project)
        versionType.convention(VersionType.RELEASE)
        gameVersions.convention(listOf(mcData.versionStr))
        loaders.convention(listOf(mcData.loader.name))
        describeFabricWithQuilt.convention(false)
        useSourcesJar.convention(false)
        useJavadocJar.convention(false)
        changelog.convention("No changelog provided.")
    }

    // Platform specific
    fun modrinth(action: Action<PublishingModrinthExtension>) = action.execute(modrinth)
    fun curseforge(action: Action<PublishingCurseForgeExtension>) = action.execute(curseforge)
    fun github(action: Action<PublishingGitHubExtension>) = action.execute(github)
}

abstract class PublishingModrinthExtension {
    abstract val projectId: Property<String>
    abstract val dependencies: ListProperty<Dependency>
    abstract val debug: Property<Boolean>

    init {
        projectId.convention("")
        dependencies.convention(listOf())
        debug.convention(false)
    }
}

abstract class PublishingCurseForgeExtension {
    abstract val projectId: Property<String>
    abstract val relations: ListProperty<CurseRelation>
    abstract val changelogType: Property<String>
    init {
        changelogType.convention("text")
    }
}

enum class CurseRelationType {
    INCOMPATIBLE,
    REQUIRED,
    EMBEDDED,
    TOOL,
    OPTIONAL
}

data class CurseRelation(
    val name: String,
    val type: CurseRelationType
) {
    fun applyTo(task: UploadArtifact) {
        when (type) {
            CurseRelationType.INCOMPATIBLE -> task.addIncompatibility(name)
            CurseRelationType.REQUIRED -> task.addRequirement(name)
            CurseRelationType.EMBEDDED -> task.addEmbedded(name)
            CurseRelationType.TOOL -> task.addTool(name)
            CurseRelationType.OPTIONAL -> task.addOptional(name)
        }
    }
}

abstract class PublishingGitHubExtension(
    project: Project
) {
    abstract val owner: Property<String>
    abstract val repository: Property<String>
    abstract val targetCommitish: Property<String>
    abstract val draft: Property<Boolean>
    abstract val autogenerateReleaseNotes: Property<Boolean>

    init {
        owner.convention(project.rootProject.group.toString().substringAfterLast("."))
        repository.convention(project.rootProject.name)
        targetCommitish.convention("main")
        draft.convention(false)
        autogenerateReleaseNotes.convention(false)
    }
}
