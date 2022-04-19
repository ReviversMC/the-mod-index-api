package com.github.reviversmc.themodindex.api.downloader;

import com.github.reviversmc.themodindex.api.data.IndexJson;
import com.github.reviversmc.themodindex.api.data.ManifestJson;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Optional;

public class IndexInfoDownloader implements InfoDownloader {

    private final JsonAdapter<IndexJson> indexJsonAdapter;
    private final JsonAdapter<ManifestJson> manifestJsonAdapter;
    private final String repositoryUrlAsString;
    private final OkHttpClient okHttpClient;
    private IndexJson indexJson;

    //The constructors violate DRY for the sake of making the json adapters final.

    /**
     * This creates a downloader for with a default index:
     * <a href="https://github.com/ReviversMC/the-mod-index">https://github.com/ReviversMC/the-mod-index</a>
     */
    public IndexInfoDownloader(Moshi moshi, OkHttpClient okHttpClient) {
        this.repositoryUrlAsString = "https://github.com/ReviversMC/the-mod-index";
        this.okHttpClient = okHttpClient;
        indexJsonAdapter = moshi.adapter(IndexJson.class);
        manifestJsonAdapter = moshi.adapter(ManifestJson.class);
    }

    /**
     * This creates a downloader with a custom index.
     *
     * @param repositoryUrlAsString The root url of the repository.
     */
    public IndexInfoDownloader(Moshi moshi, OkHttpClient okHttpClient, String repositoryUrlAsString) {
        if (repositoryUrlAsString.endsWith("/"))
            this.repositoryUrlAsString = repositoryUrlAsString.substring(0, repositoryUrlAsString.length() - 1);
        else this.repositoryUrlAsString = repositoryUrlAsString;

        this.okHttpClient = okHttpClient;
        indexJsonAdapter = moshi.adapter(IndexJson.class);
        manifestJsonAdapter = moshi.adapter(ManifestJson.class);
    }

    @Override
    public String getRepositoryUrlAsString() {
        return repositoryUrlAsString;
    }

    @Override
    public Optional<IndexJson> getIndexJson() {
        return Optional.ofNullable(indexJson);
    }

    @Override
    public Optional<IndexJson> downloadIndexJson() throws IOException {
        Response downloadResponse = okHttpClient.newCall(
                new Request.Builder()
                        .url(repositoryUrlAsString + "/mods/index.json")
                        .build()
        ).execute();

        downloadResponse.close();

        if (downloadResponse.isSuccessful()) {
            if (downloadResponse.body() != null) {
                indexJson = indexJsonAdapter.fromJson(downloadResponse.body().string());
                return getIndexJson();
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<IndexJson> getOrDownloadIndexJson() throws IOException {
        return indexJson == null ? downloadIndexJson() : getIndexJson();
    }

    @Override
    public Optional<ManifestJson> downloadManifestJson(String genericIdentifier) throws IOException {
        getOrDownloadIndexJson(); //Ensure that the index is downloaded.

        for (IndexJson.IndexFile indexFile : indexJson.files()) {
            if (indexFile.identifier().orElse("").startsWith(genericIdentifier)) {
                Response downloadResponse = okHttpClient.newCall(
                        new Request.Builder()
                                .url(repositoryUrlAsString + "/mods/" +
                                        genericIdentifier.split(":")[0] + genericIdentifier.split(":")[1])
                                .build()
                ).execute();

                downloadResponse.close();

                if (downloadResponse.isSuccessful()) {
                    if (downloadResponse.body() != null) {
                        ManifestJson manifestJson = manifestJsonAdapter.fromJson(downloadResponse.body().string());
                        return Optional.ofNullable(manifestJson);
                    }
                }
            }
        }
        return Optional.empty();
    }
}
