package com.github.reviversmc.themodindex.api.downloader

import com.github.reviversmc.themodindex.api.data.IndexJson
import com.github.reviversmc.themodindex.api.data.ManifestJson
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface IndexApiCall {

    /**
     * Calls the index.json path of the mod index
     */
    @GET("mods/index.json")
     fun callIndex(): Call<IndexJson>

    @GET("mods/{modLoader}/{modName}.json")
     fun callManifest(@Path("modLoader") modLoader: String, @Path("modName") modName: String): Call<ManifestJson>

}