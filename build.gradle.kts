import git.formatLog
import git.latestCommitHash
import git.latestCommitMessage

plugins {
    id("io.papermc.hangar-publish-plugin") version "0.1.2"
    id("com.modrinth.minotaur") version "2.+"

    id("com.github.johnrengelman.shadow")

    `root-plugin`
}

val buildNumber: String = System.getenv("NEXT_BUILD_NUMBER") ?: "SNAPSHOT"

val isSnapshot = false

rootProject.version = if (isSnapshot) "3.3-$buildNumber" else "3.3"

val content: String = if (isSnapshot) {
    formatLog(latestCommitHash(), latestCommitMessage(), rootProject.name)
} else {
    rootProject.file("CHANGELOG.md").readText(Charsets.UTF_8)
}

subprojects.filter { it.name != "api" }.forEach {
    it.project.version = rootProject.version
}

modrinth {
    token.set(System.getenv("MODRINTH_TOKEN"))

    projectId.set(rootProject.name.lowercase())

    versionType.set(if (isSnapshot) "beta" else "release")

    versionName.set("${rootProject.name} ${rootProject.version}")
    versionNumber.set(rootProject.version as String)

    changelog.set(content)

    uploadFile.set(rootProject.projectDir.resolve("jars/${rootProject.name}-${rootProject.version}.jar"))

    gameVersions.set(listOf(
        "1.20.4"
    ))

    loaders.add("paper")
    loaders.add("purpur")

    autoAddDependsOn.set(false)
    detectLoaders.set(false)
}

hangarPublish {
    publications.register("plugin") {
        apiKey.set(System.getenv("HANGAR_KEY"))

        id.set(rootProject.name.lowercase())

        version.set(rootProject.version as String)

        channel.set(if (isSnapshot) "Snapshot" else "Release")

        changelog.set(content)

        platforms {
            paper {
                jar.set(rootProject.projectDir.resolve("jars/${rootProject.name}-${rootProject.version}.jar"))

                platformVersions.set(listOf(
                    "1.20.4"
                ))

                dependencies {
                    hangar("PlaceholderAPI") {
                        required = false
                    }

                    url("Oraxen", "https://www.spigotmc.org/resources/%E2%98%84%EF%B8%8F-oraxen-custom-items-blocks-emotes-furniture-resourcepack-and-gui-1-18-1-20-4.72448/") {
                        required = false
                    }
                }
            }
        }
    }
}