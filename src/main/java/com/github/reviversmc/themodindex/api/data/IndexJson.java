package com.github.reviversmc.themodindex.api.data;

import java.util.List;
import java.util.Optional;

/**
 * Represents the index.json file found in the mod index.
 *
 * @param schemaVersion The schema version of the index.json file.
 * @param files         The {@link IndexFile} entries found in the index.json file.
 */
public record IndexJson(Optional<String> schemaVersion, List<IndexFile> files) {
    /**
     * An file entry found in the index.json file.
     * @param identifier The identifier of the mod file, in the format "modLoader:modName:version".
     * @param sha1Hash  The SHA-1 hash of the mod file.
     */
    public record IndexFile(Optional<String> identifier, Optional<String> sha1Hash) {
    }
}
