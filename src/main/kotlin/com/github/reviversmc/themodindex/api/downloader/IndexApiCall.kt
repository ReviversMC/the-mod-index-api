package com.github.reviversmc.themodindex.api.downloader

import com.github.reviversmc.themodindex.api.data.IndexJson
import com.github.reviversmc.themodindex.api.data.ManifestJson
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface IndexApiCall {

    //These should NOT be absolute paths. Absolute paths would cause the calls to resolve to github.com instead of github.com/reviversmc/themodindex/

    @GET("v4/mods/index.json")
    fun callIndex(): Call<IndexJson>

    @GET("v4/mods/{modLoader}/{modName}.json")
    fun callManifest(@Path("modLoader") modLoader: String, @Path("modName") modName: String): Call<ManifestJson>

}