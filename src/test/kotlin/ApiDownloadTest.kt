import com.github.reviversmc.themodindex.api.data.IndexJson
import com.github.reviversmc.themodindex.api.data.ManifestJson
import com.github.reviversmc.themodindex.api.downloader.DefaultApiDownloader
import okhttp3.OkHttpClient
import okhttp3.mock.*
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ApiDownloadTest {

    private val endpoint = "https://fakelocalhost/fakeindex"
    private val identifier = "bricks:fakemod:1.2.0+bricks-1.18.2"
    private val schemaVersion = "1.1.0"

    //NOTE: The interceptor and all url calls must end with a '/'!
    private val interceptor = MockInterceptor()
    private val okHttpClient = OkHttpClient.Builder().addInterceptor(interceptor).build()

    @Test
    fun `should return default manifest index`() {
        val infoDownloader = DefaultApiDownloader(okHttpClient) //Use default repo url.
        assertEquals(
            "https://raw.githubusercontent.com/ReviversMC/the-mod-index/v1", infoDownloader.repositoryUrlAsString
        )
    }

    @Test
    fun `should not return index info`() {
        //The basis of this test is to the index file is not automatically downloaded without an end user's consent.
        val infoDownloader = DefaultApiDownloader(okHttpClient)
        assertNull(infoDownloader.indexJson)
    }

    @Test
    fun `should return fake mod info`() {
        val fakeIndexText = this.javaClass.getResource("/fakeIndex/mods/index.json")?.readText()
        val fakeManifestText = this.javaClass.getResource("/fakeIndex/mods/bricks/fakemod.json")?.readText()

        interceptor.rule(get, url eq "${endpoint}/mods/index.json") {
            fakeIndexText?.let { respond(it).code(200) } ?: respond(500)
        }

        repeat(2) { //Repeat twice as this is called twice.
            interceptor.rule(
                get,
                url eq "${endpoint}/mods/${identifier.split(":")[0]}/${identifier.split(":")[1]}.json"
            ) {
                fakeManifestText?.let { respond(it).code(200) } ?: respond(500)
            }
        }

        val infoDownloader = DefaultApiDownloader(
            okHttpClient, endpoint
        )
        assertEquals(
            IndexJson(
                schemaVersion, listOf(
                    IndexJson.IndexFile(
                        identifier, "47a013e660d408619d894b20806b1d5086aab03b"
                    )
                )
            ), infoDownloader.downloadIndexJson()
        )

        assertEquals(
            ManifestJson(
                schemaVersion, "Fake Mod", "Fake Author", "AGPL-3.0", null, null, ManifestJson.ManifestLinks(
                    null, "https://github.com/ReviversMC/the-mod-index-api/fakeIndex", listOf(
                        ManifestJson.ManifestLinks.OtherLink(
                            "Discord", "https://discord.gg/6bTGYFppfz"
                        )
                    )
                ), listOf(
                    ManifestJson.ManifestFile(
                        identifier.split(":")[2],
                        listOf("1.18.2"),
                        "47a013e660d408619d894b20806b1d5086aab03b",
                        emptyList(),
                        false
                    )
                )
            ), infoDownloader.downloadManifestJson(identifier) //We can pass an identifier as a generic identefier
        )

        assertEquals(
            ManifestJson.ManifestFile(
                identifier.split(":")[2],
                listOf("1.18.2"),
                "47a013e660d408619d894b20806b1d5086aab03b",
                emptyList(),
                false
            ), infoDownloader.downloadManifestFileEntry(identifier)
        )
    }
}