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
    val repositoryUrlAsString: String

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
     * The manifest.json file for this repository. Always re-downloads the index.json file. Runs asynchronously.
     * @return The manifest.json file for this repository.
     * @author ReviversMC
     * @since 5.1.0
     */
    @Deprecated("This method offers no advantages over #downloadIndexJson. Use that instead. To be removed in next major release (6.0.0)", ReplaceWith("downloadIndexJson()"))
    @ExperimentalSerializationApi
    suspend fun asyncDownloadIndexJson(): IndexJson?

    /**
     * The manifest.json file for this repository. Downloads the index.json file if it has not already been downloaded. Runs synchronously.
     * @return The manifest.json file for this repository.
     * @author ReviversMC
     * @since 1.0.0-2.0.0
     */
    @ExperimentalSerializationApi
    fun getOrDownloadIndexJson(): IndexJson?

    /**
     * The manifest.json file for this repository. Downloads the index.json file if it has not already been downloaded. Runs asynchronously.
     * @return The manifest.json file for this repository.
     * @author ReviversMC
     * @since 5.1.0
     */
    @Deprecated("This method offers no advantages over #getOrDownloadIndexJson. Use that instead. To be removed in next major release (6.0.0)", ReplaceWith("getOrDownloadIndexJson()"))
    @ExperimentalSerializationApi
    suspend fun getOrAsyncDownloadIndexJson(): IndexJson?

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
     * The manifest.json file for this repository. Runs asynchronously.
     * @param genericIdentifier The generic identifier is "modLoader:modName".
     * This is similar to the identifier in [IndexJson.identifiers],
     * but does not contain the version.
     * @return The requested manifest.json file.
     * @author ReviversMC
     * @since 5.1.0
     */
    @Deprecated("This method offers no advantages over #downloadManifestJson. Use that instead. To be removed in next major release (6.0.0)", ReplaceWith("downloadManifestJson(genericIdentifier)"))
    @ExperimentalSerializationApi
    suspend fun asyncDownloadManifestJson(genericIdentifier: String): ManifestJson?

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

    /**
     * A [ManifestJson.ManifestFile] for the given identifier. Runs asynchronously.
     * This is not to be confused with the manifest.json file, which can be obtained with [downloadManifestJson]
     * @param identifier The identifier of the file entry to retrieve.
     * @return The requested manifest.json file.
     * @author ReviversMC
     * @since 5.1.0
     */
    @Deprecated("This method offers no advantages over #downloadManifestFileEntry. Use that instead. To be removed in next major release (6.0.0)", ReplaceWith("downloadManifestFileEntry(identifier)"))
    @ExperimentalSerializationApi
    suspend fun asyncDownloadManifestFileEntry(identifier: String): ManifestJson.ManifestFile?
}