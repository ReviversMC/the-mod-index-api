package com.github.reviversmc.themodindex.api.downloader

import com.github.reviversmc.themodindex.api.data.IndexJson
import com.github.reviversmc.themodindex.api.data.ManifestJson
import com.github.reviversmc.themodindex.api.data.ManifestJsonWithOverrides
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface IndexApiCall {

    // These should NOT be absolute paths. Absolute paths would cause the calls to resolve to github.com instead of github.com/reviversmc/themodindex/.../

    @GET("index.json")
    fun index(): Call<IndexJson>

    @GET("{modLoader}/{modName}.json")
    fun manifest(@Path("modLoader") modLoader: String, @Path("modName") modName: String): Call<ManifestJson>

    @GET("{modLoader}/{modName}.json")
    fun manifestWithOverrides(@Path("modLoader") modLoader: String, @Path("modName") modName: String): Call<ManifestJsonWithOverrides>



}