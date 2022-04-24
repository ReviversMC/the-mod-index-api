package com.github.reviversmc.themodindex.api.data

/**
 * Represents the index.json file found in the mod index.
 *
 * @param schemaVersion The schema version of the index.json file.
 * @param files         The [IndexFile] entries found in the index.json file.
 * @author ReviversMC
 * @since 1.0.0-1.2.0
 */
@kotlinx.serialization.Serializable
data class IndexJson(val schemaVersion: String?, val files: List<IndexFile>) {

    /**
     * A file entry found in the index.json file.
     *
     * @param identifier The identifier of the mod file, in the format "modLoader:modName:version".
     * @param sha1Hash  The SHA-1 hash of the mod file.
     * @author ReviversMC
     * @since 1.0.0-1.2.0
     */
    @kotlinx.serialization.Serializable
    data class IndexFile(val identifier: String?, val sha1Hash: String?)
}
