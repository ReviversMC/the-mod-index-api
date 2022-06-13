import com.github.reviversmc.themodindex.api.data.IndexJson
import com.github.reviversmc.themodindex.api.data.ManifestJson
import com.github.reviversmc.themodindex.api.downloader.DefaultApiDownloader
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull


class ApiDownloadTest {

    private val endpoint = "http://localhost" //Ensure not https!

    private val identifier =
        "bricks:fakemod:1c88ae7e3799f75d73d34c1be40dec8cabbd0f6142b39cb5bdfb32803015a7eea113c38e975c1dd4aaae59f9c3be65eebeb955868b1a10ffca0b6a6b91f8cac9"
    private val schemaMajor = "v4"

    private val indexJsonText = this.javaClass.getResource("/fakeIndex/mods/index.json")?.readText()
    private val manifestJsonText = this.javaClass.getResource("/fakeIndex/mods/bricks/fakemod.json")?.readText()

    private val server = MockWebServer().apply {
        dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                when (request.path) {
                    "/$schemaMajor/mods/index.json" -> indexJsonText?.let {
                        return MockResponse().setResponseCode(200).setBody(it)
                    } ?: return MockResponse().setResponseCode(500)
                    "/$schemaMajor/mods/bricks/fakemod.json" -> {
                        manifestJsonText?.let {
                            return MockResponse().setResponseCode(200).setBody(it)
                        } ?: return MockResponse().setResponseCode(500)
                    }
                    else -> return MockResponse().setResponseCode(404)
                }
            }
        }
    }

    @BeforeEach
    fun `before each`() {
        server.start()
    }

    @AfterEach
    fun `after each`() {
        server.shutdown()
    }

    private val okHttpClient = OkHttpClient.Builder().build()

    @Test
    fun `should return default endpoint`() {
        val apiDownloader = DefaultApiDownloader(okHttpClient) //Use default repo url.
        assertEquals(
            "https://raw.githubusercontent.com/ReviversMC/the-mod-index/", apiDownloader.formattedBaseUrl
        )
    }

    @ExperimentalSerializationApi
    @Test
    fun `index default endpoint test`() {
        val apiDownloader = DefaultApiDownloader(okHttpClient)

        val indexJson = apiDownloader.getOrDownloadIndexJson()
        assertNotNull(indexJson)

        /*
        Check that we actually remembered to bump the api endpoint.
        If this is true, we can assume that value in the manifests are/will be correct.

        Imagine if we forgot to do it on both the api and the index :grimacing:
         */

        assertEquals(schemaMajor, "v${indexJson.indexVersion.substringBefore(".")}")

        val manualRequest = okHttpClient.newCall(
            Request.Builder().url("${apiDownloader.formattedBaseUrl}HEAD/mods/index.json").build()
        ).execute()

        assertEquals(
            schemaMajor,
            "v${Json.decodeFromString<IndexJson>(manualRequest.body!!.string()).indexVersion.substringBefore(".")}"
        )
    }

    @Test
    fun `should not return index info`() {
        //The basis of this test is to the index file is not automatically downloaded without an end user's consent.
        val apiDownloader = DefaultApiDownloader(okHttpClient, "$endpoint:${server.port}")
        assertNull(apiDownloader.cachedIndexJson)
    }

    @ExperimentalSerializationApi
    @Test
    fun `should return index info`() {
        val apiDownloader = DefaultApiDownloader(okHttpClient, "$endpoint:${server.port}")
        assertEquals(Json.decodeFromString<IndexJson>(indexJsonText!!), apiDownloader.getOrDownloadIndexJson())
        assertEquals(Json.decodeFromString<IndexJson>(indexJsonText), apiDownloader.downloadIndexJson())

        //Test for retaining of index info.
        assertEquals(Json.decodeFromString<IndexJson>(indexJsonText), apiDownloader.cachedIndexJson)
    }

    @ExperimentalSerializationApi
    @Test
    fun `should return manifest info`() {
        val apiDownloader = DefaultApiDownloader(okHttpClient, "$endpoint:${server.port}")

        assertEquals(
            Json.decodeFromString<ManifestJson>(manifestJsonText!!), apiDownloader.downloadManifestJson(identifier)
        )
    }

    @ExperimentalSerializationApi
    @Test
    fun `should return file info`() {
        val apiDownloader = DefaultApiDownloader(okHttpClient, "$endpoint:${server.port}")

        val fileInfo = Json.decodeFromString<ManifestJson>(manifestJsonText!!).files.first()

        assertEquals(fileInfo, apiDownloader.downloadManifestFileEntry(identifier))
    }
}