package com.github.reviversmc.themodindex.api.downloader;

import com.github.reviversmc.themodindex.api.data.IndexJson;
import com.github.reviversmc.themodindex.api.data.ManifestJson;

import java.io.IOException;
import java.util.Optional;

/**
 * The downloader for accessing the-mod-index, or similarly structured indexes.
 */
public interface InfoDownloader {

    /**
     * The url of the repository that this downloader is for.
     * @return The url of the repository that this downloader is for, as a String.
     */
    String getRepositoryUrlAsString();

    /**
     * The index.json file for this repository. Only retrieves the index.json file locally if it has already been downloaded.
     * @return The index.json file for this repository.
     */
    Optional<IndexJson> getIndexJson();

    /**
     * The manifest.json file for this repository. Always re-downloads the index.json file.
     * @return The manifest.json file for this repository.
     * @throws IOException If there was an error downloading the manifest.json file.
     */
    Optional<IndexJson> downloadIndexJson() throws IOException;

    /**
     * The manifest.json file for this repository. Downloads the index.json file if it has not already been downloaded.
     * @return The manifest.json file for this repository.
     * @throws IOException If there was an error downloading the manifest.json file.
     */
    Optional<IndexJson> getOrDownloadIndexJson() throws IOException;

    /**
     * The manifest.json file for this repository.
     * @param genericIdentifier The generic identifier is "modLoader:modName".
     *                          This is similar to the identifier in {@link com.github.reviversmc.themodindex.api.data.IndexJson.IndexFile},
     *                          but does not contain the version.
     * @return The requested manifest.json file.
     * @throws IOException If there was an error downloading the manifest.json file.
     */
    Optional<ManifestJson> downloadManifestJson(String genericIdentifier) throws IOException;

    /**
     * A {@link ManifestJson.ManifestFile} for the given identifier.
     * This is not to be confused with the manifest.json file, which can be obtained with {@link #downloadManifestJson(String)}
     * @param identifier The identifier of the file entry to retrieve.
     * @return The requested manifest.json file.
     * @throws IOException If there was an error downloading the manifest.json file.
     */
    Optional<ManifestJson.ManifestFile> downloadManifestFileEntry(String identifier) throws IOException;
}
