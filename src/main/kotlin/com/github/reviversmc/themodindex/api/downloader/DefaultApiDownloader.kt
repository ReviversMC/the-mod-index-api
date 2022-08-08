package com.github.reviversmc.themodindex.api.downloader

import com.github.reviversmc.themodindex.api.data.IndexJson
import com.github.reviversmc.themodindex.api.data.VersionFile
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.io.IOException

/**
 * The default implementation of [DefaultApiDownloader]
 * @param baseUrl The base URL of the repository to download from. The repository should follow the layout as specified by [the-mod-index](https://github.com/reviversmc/the-mod-index/), Default: https://github.com/reviversmc/the-mod-index/v5/mods/
 * @param okHttpClient The [OkHttpClient] to use for the download. Defaults to a new instance of [OkHttpClient]
 * @param json The [Json] instance to use for serialization. Default options: ignoreUnknownKeys = true, prettyPrint = true
 * @author ReviversMC
 * @since 9.0.0
 */
class DefaultApiDownloader @JvmOverloads constructor(
    baseUrl: String = "https://raw.githubusercontent.com/ReviversMC/the-mod-index/v5/mods/",
    okHttpClient: OkHttpClient = OkHttpClient.Builder().build(),
    json: Json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
    },
) : ApiDownloader {

    constructor(
        okHttpClient: OkHttpClient,
        baseUrl: String,
        json: Json,
    ) : this(baseUrl, okHttpClient, json)

    override var cachedIndexJson: IndexJson? = null
        private set // We don't want consumers to be able to set this directly.

    // We want to ensure that we don't have the "/" at the end of the URL for consistency.
    override val formattedBaseUrl: String = baseUrl + if (baseUrl.endsWith("/")) "" else "/"

    @OptIn(ExperimentalSerializationApi::class)
    private val indexApiCall =
        Retrofit.Builder().addConverterFactory(json.asConverterFactory(MediaType.get("application/json")))
            .baseUrl(formattedBaseUrl).client(okHttpClient).build().create(IndexApiCall::class.java)


    override fun downloadIndexJson(): IndexJson? {
        cachedIndexJson = indexApiCall.index().execute().body()
        return cachedIndexJson
    }

    override fun getOrDownloadIndexJson(): IndexJson? = cachedIndexJson ?: downloadIndexJson()


    /**
     * Does pre-download checks for manifest, and decides whether to download or not.
     * If the manifest should be downloaded, the mod's generic identifier will be returned in a [Pair].
     * @author ReviversMC
     * @since 9.1.0
     */
    private fun preDownloadManifestJson(genericIdentifier: String): Pair<String, String>? {
        getOrDownloadIndexJson() // Try to ensure that we have a valid index.

        // Assumes format of loader:name:hash, where everything is lowercase. Grabs from indexJson.
        val genericIdentifiers = cachedIndexJson?.identifiers?.map { it.substringBeforeLast(":") }?.distinct()
            ?: throw IOException("Could not get generic identifiers from index.json at base url of $formattedBaseUrl")
        val lowerCasedGenericIdentifier = genericIdentifier.lowercase().split(":")

        return if ("${lowerCasedGenericIdentifier[0]}:${lowerCasedGenericIdentifier[1]}" in genericIdentifiers)
            Pair(lowerCasedGenericIdentifier[0], lowerCasedGenericIdentifier[1])
        else null
    }

    override fun downloadManifestJson(genericIdentifier: String) = preDownloadManifestJson(genericIdentifier)?.let {
        indexApiCall.manifest(it.first, it.second).execute().body()
    }

    override fun downloadManifestJsonWithOverrides(genericIdentifier: String) =
        preDownloadManifestJson(genericIdentifier)?.let {
            indexApiCall.manifestWithOverrides(it.first, it.second).execute().body()
        }


    override fun downloadManifestFileEntryFromIdentifier(identifier: String): VersionFile? {
        getOrDownloadIndexJson() // Try to ensure that we have a valid index.

        val lowerCaseIdentifier = identifier.lowercase()
        if (cachedIndexJson?.identifiers?.contains(lowerCaseIdentifier) == true) {
            val manifestJson =
                downloadManifestJson(lowerCaseIdentifier) // We can pass the whole identifier as the hash will be ignored.
                    ?: throw IOException("Could not get generic identifiers from index.json at base url of $formattedBaseUrl")
            return manifestJson.files.firstOrNull {
                it.shortSha512Hash == lowerCaseIdentifier.substringAfterLast(":")
            }
        }
        return null
    }

    override fun downloadManifestFileEntryFromHash(shortHash: String): List<VersionFile> {
        getOrDownloadIndexJson() // Try to ensure that we have a valid index.

        val lowerCasedShortHash = shortHash.lowercase()
        val matches = cachedIndexJson?.identifiers?.filter { shortHash == it.substringAfterLast(":") }
            ?: throw IOException("Could not get generic identifiers from index.json at base url of $formattedBaseUrl")
        return matches.mapNotNull { identifier ->
            downloadManifestJson(identifier)?.files?.first {
                it.shortSha512Hash == lowerCasedShortHash
            }
        }
    }
}
