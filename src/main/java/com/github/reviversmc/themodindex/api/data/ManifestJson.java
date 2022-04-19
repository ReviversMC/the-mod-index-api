package com.github.reviversmc.themodindex.api.data;

import java.util.List;
import java.util.Optional;

/**
 * A manifest for a mod. The same mod meant for different mod loaders (e.g. Fabric, Forge, etc.) will have different manifests.
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
public record ManifestJson(Optional<String> schemaVersion, Optional<String> fancyName, Optional<String> author,
                           Optional<String> license, Optional<String> curseForgeId, Optional<String> modrinthId,
                           Optional<ManifestLinks> links, List<ManifestFile> files) {
    /**
     * A list of links related to the mod.
     *
     * @param issue         A link to the mod's issue tracker.
     * @param sourceControl A link to the mod's source control, no mirrors. Remove endings like ".git".
     * @param others        A list of other links related to the mod.
     */
    public record ManifestLinks(Optional<String> issue, Optional<String> sourceControl, List<OtherLink> others) {
        /**
         * A list of other links related to the mod.
         *
         * @param linkName The type of link, like "discord", "irc", or "GitHub wiki"
         * @param url      The url of the link.
         */
        public record OtherLink(Optional<String> linkName, Optional<String> url) {
        }
    }

    /**
     * File versions for the mod.
     *
     * @param fileName     The name of the file, should not be used for version checking.
     * @param mcVersions   A list of Minecraft versions the file is compatible with.
     * @param sha1Hash     The sha1 hash of the file.
     * @param downloadUrls A list of urls to download the file from.
     */
    public record ManifestFile(Optional<String> fileName, List<String> mcVersions, Optional<String> sha1Hash,
                               List<String> downloadUrls) {
    }

}
