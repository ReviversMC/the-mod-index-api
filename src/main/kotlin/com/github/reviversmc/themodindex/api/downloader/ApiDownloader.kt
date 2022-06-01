package com.github.reviversmc.themodindex.api.downloader

import com.github.reviversmc.themodindex.api.data.IndexJson
import com.github.reviversmc.themodindex.api.data.ManifestJson
import kotlinx.serialization.ExperimentalSerializationApi

/**
 * The downloader for accessing the-mod-index, or similarly structured indexes.
 * @author ReviversMC
 * @since 1.0.0-2.0.0
 */
interface ApiDownloader {

    /**
     * The url of the repository that this downloader is for.
     * @return the url of the repository that this downloader is for.
     * @author ReviversMC
     * @since 1.0.0-2.0.0
     */
    val formattedBaseUrl: String

    /**
     * The index.json file for this repository. Only retrieves the index.json file locally if it has already been downloaded.
     * @return the index.json file for this repository.
     * @author ReviversMC
     * @since 1.0.0-2.0.0
     */
    val indexJson: IndexJson?

    /**
     * The manifest.json file for this repository. Always re-downloads the index.json file. Runs synchronously.
     * @return The manifest.json file for this repository.
     * @author ReviversMC
     * @since 1.0.0-2.0.0
     */
    @ExperimentalSerializationApi
    fun downloadIndexJson(): IndexJson?

    /**
     * The manifest.json file for this repository. Downloads the index.json file if it has not already been downloaded. Runs synchronously.
     * @return The manifest.json file for this repository.
     * @author ReviversMC
     * @since 1.0.0-2.0.0
     */
    @ExperimentalSerializationApi
    fun getOrDownloadIndexJson(): IndexJson?

    /**
     * The manifest.json file for this repository. Runs synchronously.
     * @param genericIdentifier The generic identifier is "modLoader:modName".
     * This is similar to the identifier in [IndexJson.identifiers],
     * but does not contain the version.
     * @return The requested manifest.json file.
     * @author ReviversMC
     * @since 1.0.0-2.0.0
     */
    @ExperimentalSerializationApi
    fun downloadManifestJson(genericIdentifier: String): ManifestJson?


    /**
     * A [ManifestJson.ManifestFile] for the given identifier. Runs synchronously.
     * This is not to be confused with the manifest.json file, which can be obtained with [downloadManifestJson]
     * @param identifier The identifier of the file entry to retrieve.
     * @return The requested manifest.json file.
     * @author ReviversMC
     * @since 1.0.0-2.0.0
     */
    @ExperimentalSerializationApi
    fun downloadManifestFileEntry(identifier: String): ManifestJson.ManifestFile?
}