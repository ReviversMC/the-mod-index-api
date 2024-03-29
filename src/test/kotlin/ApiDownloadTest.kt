import com.github.reviversmc.themodindex.api.data.IndexJson
import com.github.reviversmc.themodindex.api.data.ManifestJson
import com.github.reviversmc.themodindex.api.downloader.DefaultApiDownloader
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

    private val baseUrl get() = "http://localhost:${server.port}/$schemaMajor/mods/" // Ensure not https

    private val identifier =
        "bricks:fake-mod:1c88ae7e3799f75"
    private val schemaMajor = "v5"

    private val indexJsonText = javaClass.getResource("/fakeIndex/mods/index.json")?.readText()
    private val manifestJsonText = javaClass.getResource("/fakeIndex/mods/bricks/fake-mod.json")?.readText()

    private val server = MockWebServer().apply {
        dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                when (request.path) {
                    "/$schemaMajor/mods/index.json" -> indexJsonText?.let {
                        return MockResponse().setResponseCode(200).setBody(it)
                    } ?: return MockResponse().setResponseCode(500)

                    "/$schemaMajor/mods/bricks/fake-mod.json" -> {
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

    @Test
    fun `should return default baseUrl`() {
        val apiDownloader = DefaultApiDownloader() // Use default repo url.
        assertEquals(
            "https://raw.githubusercontent.com/ReviversMC/the-mod-index/$schemaMajor/mods/",
            apiDownloader.formattedBaseUrl
        )
    }

    @Test
    fun `index default baseUrl test`() {
        val apiDownloader = DefaultApiDownloader()

        val indexJson = apiDownloader.getOrDownloadIndexJson()
        assertNotNull(indexJson)

        /*
        Check that we actually remembered to bump the api baseUrl.
        If this is true, we can assume that value in the manifests are/will be correct.

        Imagine if we forgot to do it on both the api and the index :grimacing:
         */

        assertEquals(schemaMajor, "v${indexJson.indexVersion.substringBefore(".")}")

        val manualRequest = OkHttpClient.Builder().build().newCall(
            Request.Builder().url("${apiDownloader.formattedBaseUrl}index.json").build()
        ).execute()

        assertEquals(
            schemaMajor,
            "v${Json.decodeFromString<IndexJson>(manualRequest.body!!.string()).indexVersion.substringBefore(".")}"
        )
    }

    @Test
    fun `should not return index info`() {
        // The basis of this test is to the index file is not automatically downloaded without an end user's consent.
        val apiDownloader = DefaultApiDownloader(baseUrl = baseUrl)
        assertNull(apiDownloader.cachedIndexJson)
    }

    @Test
    fun `should return index info`() {
        val apiDownloader = DefaultApiDownloader(baseUrl = baseUrl)
        assertEquals(Json.decodeFromString<IndexJson>(indexJsonText!!), apiDownloader.getOrDownloadIndexJson())
        assertEquals(Json.decodeFromString<IndexJson>(indexJsonText), apiDownloader.downloadIndexJson())

        // Test for retaining of index info.
        assertEquals(Json.decodeFromString<IndexJson>(indexJsonText), apiDownloader.cachedIndexJson)
    }

    @Test
    fun `should return manifest info`() {
        val apiDownloader = DefaultApiDownloader(baseUrl = baseUrl)

        assertEquals(
            Json.decodeFromString<ManifestJson>(manifestJsonText!!), apiDownloader.downloadManifestJson(identifier)
        )
    }

    @Test
    fun `should return file info`() {
        val apiDownloader = DefaultApiDownloader(baseUrl = baseUrl)

        val fileInfo = Json.decodeFromString<ManifestJson>(manifestJsonText!!).files.first()

        assertEquals(fileInfo, apiDownloader.downloadManifestFileEntryFromIdentifier(identifier))

        val shortHash = identifier.substringAfterLast(":")
        assertEquals(listOf(fileInfo), apiDownloader.downloadManifestFileEntryFromHash(shortHash))
    }
}