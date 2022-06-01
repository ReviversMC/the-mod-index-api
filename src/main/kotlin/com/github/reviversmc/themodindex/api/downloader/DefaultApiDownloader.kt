package com.github.reviversmc.themodindex.api.downloader

import com.github.reviversmc.themodindex.api.data.IndexJson
import com.github.reviversmc.themodindex.api.data.ManifestJson
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit

/**
 * The default implementation of [DefaultApiDownloader]
 * @param okHttpClient The [OkHttpClient] to use for the download. Defaults to a new instance of [OkHttpClient]
 * @param baseUrl The base URL of the repository to download from. The repository should follow the layout as specified by [the-mod-index](https://github.com/reviversmc/the-mod-index/), Default: https://github.com/reviversmc/the-mod-index/
 * @param json The [Json] instance to use for serialization. Default options: ignoreUnknownKeys = true, prettyPrint = true
 * @author ReviversMC
 * @since 6.0.0
 */
class DefaultApiDownloader(
    okHttpClient: OkHttpClient = OkHttpClient.Builder().build(),
    baseUrl: String = "https://raw.githubusercontent.com/ReviversMC/the-mod-index/",
    json: Json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
    }
) : ApiDownloader {

    override var indexJson: IndexJson? = null
        private set //We don't want consumers to be able to set this directly.

    //We want to ensure that we don't have the "/" at the end of the URL for consistency.
    override val formattedBaseUrl: String =
        baseUrl + if (baseUrl.endsWith("/")) "" else "/"

    @ExperimentalSerializationApi
    private val indexApiCall =
        Retrofit.Builder().addConverterFactory(json.asConverterFactory(MediaType.get("application/json")))
            .baseUrl(formattedBaseUrl).client(okHttpClient).build().create(IndexApiCall::class.java)


    @ExperimentalSerializationApi
    override fun downloadIndexJson(): IndexJson? {
        indexJson = indexApiCall.callIndex().execute().body()
        return indexJson
    }

    @ExperimentalSerializationApi
    override fun getOrDownloadIndexJson(): IndexJson? = indexJson ?: downloadIndexJson()


    @ExperimentalSerializationApi
    override fun downloadManifestJson(genericIdentifier: String): ManifestJson? {
        indexJson ?: getOrDownloadIndexJson() //Ensure that we have a valid index.

        //Assumes format of loader:name:hash, where everything is lowercase. Grabs from indexJson.
        val genericIdentifiers = indexJson?.identifiers?.map { it.substringBeforeLast(":") }?.distinct() ?: return null
        val lowerCasedGenericIdentifier = genericIdentifier.lowercase().split(":")

        if (genericIdentifiers.contains("${lowerCasedGenericIdentifier[0]}:${lowerCasedGenericIdentifier[1]}")) {
            return indexApiCall.callManifest(lowerCasedGenericIdentifier[0], lowerCasedGenericIdentifier[1]).execute()
                .body()
        }

        return null
    }

    @ExperimentalSerializationApi
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
