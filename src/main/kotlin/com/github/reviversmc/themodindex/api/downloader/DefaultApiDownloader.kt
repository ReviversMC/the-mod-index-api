package com.github.reviversmc.themodindex.api.downloader

import com.github.reviversmc.themodindex.api.data.IndexJson
import com.github.reviversmc.themodindex.api.data.ManifestJson
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
 * @param okHttpClient The [OkHttpClient] to use for the download. Defaults to a new instance of [OkHttpClient]
 * @param baseUrl The base URL of the repository to download from. The repository should follow the layout as specified by [the-mod-index](https://github.com/reviversmc/the-mod-index/), Default: https://github.com/reviversmc/the-mod-index/v4/mods/
 * @param json The [Json] instance to use for serialization. Default options: ignoreUnknownKeys = true, prettyPrint = true
 * @author ReviversMC
 * @since 8.0.0
 */
class DefaultApiDownloader(
    okHttpClient: OkHttpClient = OkHttpClient.Builder().build(),
    baseUrl: String = "https://raw.githubusercontent.com/ReviversMC/the-mod-index/v4/mods/",
    json: Json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
    }
) : ApiDownloader {

    override var cachedIndexJson: IndexJson? = null
        private set // We don't want consumers to be able to set this directly.

    // We want to ensure that we don't have the "/" at the end of the URL for consistency.
    override val formattedBaseUrl: String =
        baseUrl + if (baseUrl.endsWith("/")) "" else "/"

    @OptIn(ExperimentalSerializationApi::class)
    private val indexApiCall =
        Retrofit.Builder().addConverterFactory(json.asConverterFactory(MediaType.get("application/json")))
            .baseUrl(formattedBaseUrl).client(okHttpClient).build().create(IndexApiCall::class.java)


    override fun downloadIndexJson(): IndexJson? {
        cachedIndexJson = indexApiCall.index().execute().body()
        return cachedIndexJson
    }

    override fun getOrDownloadIndexJson(): IndexJson? = cachedIndexJson ?: downloadIndexJson()


    override fun downloadManifestJson(genericIdentifier: String): ManifestJson? {
        getOrDownloadIndexJson() // Try to ensure that we have a valid index.

        // Assumes format of loader:name:hash, where everything is lowercase. Grabs from indexJson.
        val genericIdentifiers = cachedIndexJson?.identifiers?.map { it.substringBeforeLast(":") }?.distinct() ?: throw IOException("Could not get generic identifiers from index.json at base url of $formattedBaseUrl")
        val lowerCasedGenericIdentifier = genericIdentifier.lowercase().split(":")

        if (genericIdentifiers.contains("${lowerCasedGenericIdentifier[0]}:${lowerCasedGenericIdentifier[1]}")) {
            return indexApiCall.manifest(lowerCasedGenericIdentifier[0], lowerCasedGenericIdentifier[1]).execute()
                .body()
        }

        return null
    }

    override fun downloadManifestFileEntry(identifier: String): VersionFile? {
        getOrDownloadIndexJson() // Try to ensure that we have a valid index.

        val lowerCaseIdentifier = identifier.lowercase()
        if (cachedIndexJson?.identifiers?.contains(lowerCaseIdentifier) == true) {
            val manifestJson = downloadManifestJson(lowerCaseIdentifier) // We can pass the whole identifier as the hash will be ignored.
                ?: throw IOException("Could not get generic identifiers from index.json at base url of $formattedBaseUrl")
            return manifestJson.files.firstOrNull {
                it.sha512Hash == lowerCaseIdentifier.split(":").last()
            }
        }
        return null
    }
}
