package com.github.reviversmc.themodindex.api.data

/**
 * Represents the index.json file found in the mod index.
 *
 * @param indexVersion The schema version of the index.json file.
 * @param identifiers        The identifier of the mod file, in the format "modLoader:modName:sha512hash".
 * @author ReviversMC
 * @since 5.0.0
 */
@kotlinx.serialization.Serializable
data class IndexJson(val indexVersion: String, val identifiers: List<String>)
