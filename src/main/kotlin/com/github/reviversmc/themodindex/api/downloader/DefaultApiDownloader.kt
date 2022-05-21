package com.github.reviversmc.themodindex.api.downloader

import com.github.reviversmc.themodindex.api.data.IndexJson
import com.github.reviversmc.themodindex.api.data.ManifestJson
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * The default implementation of [DefaultApiDownloader]
 * @param okHttpClient The [OkHttpClient] to use for the download
 * @param unEditedRepositoryUrlAsString The URL of the repository to download from. Default: [https://raw.githubusercontent.com/ReviversMC/the-mod-index/v1](https://raw.githubusercontent.com/ReviversMC/the-mod-index/v1)".
 * The url will be processed to remove the "/" at the end of the url, if found.
 * @param json The [Json] instance to use for serialization. Default options: ignoreUnknownKeys = true, prettyPrint = true
 * @author ReviversMC
 * @since 1.0.0-2.0.0
 * */
class DefaultApiDownloader(
    private val okHttpClient: OkHttpClient,
    unEditedRepositoryUrlAsString: String = "https://raw.githubusercontent.com/ReviversMC/the-mod-index/v4",
    private val json: Json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
    }
) : ApiDownloader {

    override var indexJson: IndexJson? = null
        private set //We don't want consumers to be able to set this directly.

    //We want to ensure that we don't have the "/" at the end of the URL for consistency.
    override val repositoryUrlAsString: String =
        if (unEditedRepositoryUrlAsString.endsWith("/")) unEditedRepositoryUrlAsString.substring(
            0, unEditedRepositoryUrlAsString.length - 1
        )
        else unEditedRepositoryUrlAsString

    override fun downloadIndexJson(): IndexJson? {
        val downloadResponse = okHttpClient.newCall(
            Request.Builder().url("$repositoryUrlAsString/mods/index.json").build()
        ).execute()

        indexJson = downloadResponse.body?.string()?.let { json.decodeFromString(it) }

        downloadResponse.close()
        return indexJson
    }

    override fun getOrDownloadIndexJson(): IndexJson? = indexJson ?: downloadIndexJson()

    override fun downloadManifestJson(genericIdentifier: String): ManifestJson? {
        indexJson ?: getOrDownloadIndexJson() //Ensure that we have a valid index.

        //Assumes format of loader:name:hash, where everything is lowercase
        val genericIdentifiers = indexJson?.identifiers?.map { it.substringBeforeLast(":") }?.distinct() ?: return null

        val lowerCasedGenericIdentifier = genericIdentifier.lowercase().split(":")

        if (genericIdentifiers.contains("${lowerCasedGenericIdentifier[0]}:${lowerCasedGenericIdentifier[1]}")) {
            val downloadResponse = okHttpClient.newCall(
                Request.Builder().url(
                    "$repositoryUrlAsString/mods/${
                        lowerCasedGenericIdentifier[0]
                    }/${
                        lowerCasedGenericIdentifier[1]
                    }.json"
                ).build()
            ).execute()

            val manifestJson = downloadResponse.body?.string()?.let { json.decodeFromString<ManifestJson>(it) }
            downloadResponse.close()
            return manifestJson
        }

        return null
    }

    override fun downloadManifestFileEntry(identifier: String): ManifestJson.ManifestFile? {
        indexJson ?: getOrDownloadIndexJson() //Ensure that we have a valid index.

        val lowerCaseIdentifier = identifier.lowercase()
        if (indexJson?.identifiers?.contains(lowerCaseIdentifier) == true) {
            val manifestJson = downloadManifestJson(lowerCaseIdentifier)
                ?: return null //We can pass the whole identifier as the version will be ignored.
            return manifestJson.files.firstOrNull {
                it.sha512Hash == lowerCaseIdentifier.split(":").last()
            }
        }
        return null
    }
}
