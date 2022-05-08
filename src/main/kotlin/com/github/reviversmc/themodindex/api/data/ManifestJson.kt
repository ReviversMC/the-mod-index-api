package com.github.reviversmc.themodindex.api.data

/**
 * A manifest for a mod. The same mod meant for different mod loaders (e.g. Quilt, Fabric, Forge, etc.) will have different manifests.
 *
 * @param schemaVersion The version of the manifest schema.
 * @param fancyName     The user readable name of the project.
 * @param author        The author/publisher of the mod.
 * @param license       The license of the mod, or a url if custom.
 * @param curseForgeId  The curseforge id of the mod, not the slug.
 * @param modrinthId    The modrinth id of the mod, not the slug.
 * @param links         A list of links related to the mod.
 * @param files         File versions for the mod.
 */
@kotlinx.serialization.Serializable
data class ManifestJson(
    val schemaVersion: String?,
    val fancyName: String?,
    val author: String?,
    val license: String?,
    val curseForgeId: String?,
    val modrinthId: String?,
    val links: ManifestLinks?,
    val files: List<ManifestFile>
) {

    /**
     * A list of links related to the mod.
     *
     * @param issue         A link to the mod's issue tracker.
     * @param sourceControl A link to the mod's source control, no mirrors. Remove endings like ".git".
     * @param others        A list of other links related to the mod.
     */
    @kotlinx.serialization.Serializable
    data class ManifestLinks(val issue: String?, val sourceControl: String?, val others: List<OtherLink>) {

        /**
         * A list of other links related to the mod.
         *
         * @param linkName The type of link, like "discord", "irc", or "GitHub wiki"
         * @param url      The url of the link.
         */
        @kotlinx.serialization.Serializable
        data class OtherLink(val linkName: String?, val url: String?)
    }

    /**
     * File versions for the mod.
     *
     * @param fileName     The name of the file, should not be used for version checking.
     * @param mcVersions   A list of Minecraft versions the file is compatible with.
     * @param sha1Hash     The sha1 hash of the file.
     * @param downloadUrls A list of urls to download the file from.
     * @param curseDownloadAvailable Whether the file is available on Curse. A further api call to CF is required to get the download url.
     */
    @kotlinx.serialization.Serializable
    data class ManifestFile(
        val fileName: String?,
        val mcVersions: List<String>,
        val sha1Hash: String?,
        val downloadUrls: List<String>,
        val curseDownloadAvailable: Boolean?
    )
}
