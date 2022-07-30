package com.github.reviversmc.themodindex.api.data

/**
 * A manifest for a mod. The same mod meant for different mod loaders (e.g. Quilt, Fabric, Forge, etc.) will have different manifests.
 *
 * @param indexVersion The version of the manifest schema.
 * @param genericIdentifier The generic identifier of the manifest (i.e. "{mod loader}:{mod name}")
 * @param fancyName     A user readable name of the project.
 * @param author        The author/publisher of the mod.
 * @param license       The license of the mod, or a url if custom.
 * @param curseForgeId  The curseforge id of the mod.
 * @param modrinthId    The modrinth id of the mod, not the slug.
 * @param links         A list of links related to the mod.
 * @param files         File versions for the mod.
 * @author ReviversMC
 * @since 7.2.1
 */
@kotlinx.serialization.Serializable
data class ManifestJson(
    val indexVersion: String,
    val genericIdentifier: String,
    val fancyName: String,
    val author: String,
    val license: String?,
    val curseForgeId: Int?,
    val modrinthId: String?,
    val links: ManifestLinks,
    val files: List<VersionFile>,
)

/**
 * A couple of links related to the mod.
 *
 * @param issue         A link to the mod's issue tracker.
 * @param sourceControl A link to the mod's source control, no mirrors. Remove endings like ".git".
 * @param others        A list of other links related to the mod.
 * @author ReviversMC
 * @since 6.1.0
 */
@kotlinx.serialization.Serializable
data class ManifestLinks(val issue: String?, val sourceControl: String?, val others: List<OtherLink>) {

    /**
     * A list of other links related to the mod.
     *
     * @param linkName The type of link, like "discord", "irc", or "GitHub wiki"
     * @param url      The url of the link.
     * @author ReviversMC
     * @since 6.1.0
     */
    @kotlinx.serialization.Serializable
    data class OtherLink(val linkName: String, val url: String)
}

/**
 * File versions for the mod.
 *
 * @param fileName     The name of the file, should not be used for version checking.
 * @param mcVersions   A list of Minecraft versions the file is compatible with.
 * @param shortSha512Hash The short SHA512 hash of the file, consisting of only 15 characters.
 * @param downloadUrls A list of urls to download the file from.
 * @param curseDownloadAvailable Whether the file is available on Curse. A further api call to CF is required to get the download url.
 * @param relationsToOtherMods The relations (i.e. dependencies/conflicts) to other mods.
 * @author ReviversMC
 * @since 9.0.0
 */
@kotlinx.serialization.Serializable
data class VersionFile(
    val fileName: String,
    val mcVersions: List<String>,
    val shortSha512Hash: String,
    val downloadUrls: List<String>,
    val curseDownloadAvailable: Boolean,
    val relationsToOtherMods: RelationsToOtherMods,
)

/**
 * Relations (i.e. dependencies/conflicts) to other mods, in the form of generic identifiers (i.e. "{mod loader}:{mod name}").
 *
 * @param required The generic identifiers of the mods required by this mod.
 * @param incompatible The generic identifiers of the mods incompatible with this mod.
 * @author ReviversMC
 * @since 7.2.0
 */
@kotlinx.serialization.Serializable
data class RelationsToOtherMods(val required: List<String>, val incompatible: List<String>)

