package com.github.reviversmc.themodindex.api.downloader

import com.github.reviversmc.themodindex.api.data.IndexJson
import com.github.reviversmc.themodindex.api.data.ManifestJson
import com.github.reviversmc.themodindex.api.data.ManifestJsonWithOverrides
import com.github.reviversmc.themodindex.api.data.VersionFile
import java.io.IOException

/**
 * The downloader for accessing [the-mod-index](https://github.com/reviversmc/the-mod-index), or similarly structured indexes.
 * @author ReviversMC
 * @since 1.0.0-2.0.0
 */
interface ApiDownloader {

    /**
     * The base url the current instance of the [ApiDownloader] is targeting.
     * @author ReviversMC
     * @since 6.0.0
     */
    val formattedBaseUrl: String

    /**
     * Retrieves the [IndexJson] file from cache if it has already been downloaded, or null if no cache exists.
     * @author ReviversMC
     * @since 6.1.0
     */
    val cachedIndexJson: IndexJson?

    /**
     * Re-downloads and caches the [IndexJson] file from the remote repository. Null if the download fails.
     * @throws [IOException] if the download fails.
     * @author ReviversMC
     * @since 1.0.0-2.0.0
     */
    @kotlin.jvm.Throws(IOException::class)
    fun downloadIndexJson(): IndexJson?

    /**
     * Retrieves the [IndexJson] file from cache if it has already been downloaded, or attempts to download and cache it from the remote repository if no cache exists
     * @throws [IOException] if the download fails.
     * @author ReviversMC
     * @since 1.0.0-2.0.0
     */
    @kotlin.jvm.Throws(IOException::class)
    fun getOrDownloadIndexJson(): IndexJson?

    /**
     * Retrieves the requested [ManifestJson] file for the given [genericIdentifier].
     * The format for a generic identifier is "modLoader:modName".
     * @throws [IOException] if the download fails.
     * @author ReviversMC
     * @since 1.0.0-2.0.0
     */
    @kotlin.jvm.Throws(IOException::class)
    fun downloadManifestJson(genericIdentifier: String): ManifestJson?

    /**
     * Retrieves the requested [ManifestJsonWithOverrides] file for the given [genericIdentifier].
     * The format for a generic identifier is "modLoader:modName".
     *
     * For most consumers of TMI, [downloadManifestJson] is more than sufficient.
     * Overrides are mostly for internal tools such as the [TMI-Maintainer](https://github.com/reviversmc/the-mod-index-creator)
     * @throws [IOException] if the download fails.
     * @author ReviversMC
     * @since 9.1.0
     */
    @kotlin.jvm.Throws(IOException::class)
    fun downloadManifestJsonWithOverrides(genericIdentifier: String): ManifestJsonWithOverrides?

    /**
     * Retrieves the requested [VersionFile] for the given [identifier].
     * @throws [IOException] if the download fails.
     * @author ReviversMC
     * @since 9.0.0
     */
    @kotlin.jvm.Throws(IOException::class)
    fun downloadManifestFileEntryFromIdentifier(identifier: String): VersionFile?

    /**
     * Retrieves the requested [VersionFile]s for the given sha512 [shortHash].
     * While extremely unlikely, there is a chance that the shortHash is not unique, and thus multiple [VersionFile]s will be returned.
     * Should the [shortHash] be more than 15 characters, it will be truncated to the first 15 characters.
     * @throws [IOException] if the download fails.
     * @author ReviversMC
     * @since 9.0.0
     */
    @kotlin.jvm.Throws(IOException::class)
    fun downloadManifestFileEntryFromHash(shortHash: String): List<VersionFile>
}
