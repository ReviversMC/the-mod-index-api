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
 * @param repositoryUrlAsString The URL of the repository to download from. Default: "https://raw.githubusercontent.com/ReviversMC/the-mod-index/v1"
 * @param json The [Json] instance to use for serialization. Default options: ignoreUnknownKeys = true, prettyPrint = true
 * @author ReviversMC
 * @since 1.0.0-1.2.0
 * */
class DefaultApiDownloader(
    private val okHttpClient: OkHttpClient,
    override val repositoryUrlAsString: String = "https://raw.githubusercontent.com/ReviversMC/the-mod-index/v1".let {
        if (it.endsWith("/")) it.substring(0, it.length - 1) else it
    },
    private val json: Json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
    }
) : ApiDownloader {

    override var indexJson: IndexJson? = null
        private set //We don't want consumers to be able to set this directly.

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

        for (indexFile in indexJson?.files ?: return null) {
            if (indexFile.identifier?.startsWith(genericIdentifier) == true) {

                val downloadResponse = okHttpClient.newCall(
                    Request.Builder().url(
                        "$repositoryUrlAsString/mods/${
                            genericIdentifier.split(":")[0]
                        }/${
                            genericIdentifier.split(":")[1]
                        }.json"
                    ).build()
                ).execute()

                val manifestJson = downloadResponse.body?.string()?.let { json.decodeFromString<ManifestJson>(it) }
                downloadResponse.close()
                return manifestJson ?: continue
            }
        }
        return null
    }

    override fun downloadManifestFileEntry(identifier: String): ManifestJson.ManifestFile? {
        indexJson ?: getOrDownloadIndexJson() //Ensure that we have a valid index.

        for (indexFile in indexJson?.files ?: return null) {
            if (indexFile.identifier.equals(identifier, false)) { //"==" calls .equals anyway
                val manifestJson = downloadManifestJson(identifier)
                    ?: return null //We can pass the whole identifier as the version will be ignored.
                return manifestJson.files.firstOrNull {
                    it.fileName.equals(identifier.split(":").last(), true)
                }
            }
        }
        return null
    }
}
