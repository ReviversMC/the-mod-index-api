import com.github.reviversmc.themodindex.api.data.IndexJson
import com.github.reviversmc.themodindex.api.data.ManifestJson
import com.github.reviversmc.themodindex.api.downloader.DefaultApiDownloader
import okhttp3.OkHttpClient
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class ApiDownloadTest {

    //TODO Replace test endpoint with localhost?

    private val okHttpClient = OkHttpClient()

    @Test
    internal fun shouldReturnDefaultManifestIndex() {
        val infoDownloader = DefaultApiDownloader(okHttpClient) //Use default repo url.
        Assertions.assertEquals(
            "https://raw.githubusercontent.com/ReviversMC/the-mod-index/v1", infoDownloader.repositoryUrlAsString
        )
    }

    @Test
    internal fun shouldNotReturnIndexInfo() {
        //The basis of this test is to the index file is not automatically downloaded without an end user's consent.
        val infoDownloader = DefaultApiDownloader(okHttpClient)
        Assertions.assertNull(infoDownloader.indexJson)
    }

    @Test
    internal fun shouldReturnFakeModInfo() {
        val infoDownloader = DefaultApiDownloader(
            okHttpClient, "https://raw.githubusercontent.com/ReviversMC/the-mod-index-api/main/fakeIndex/"
        )
        Assertions.assertEquals(
            IndexJson(
                "1.0.0", listOf(
                    IndexJson.IndexFile(
                        "bricks:fakeMod:brick-1.18.2+1.2.0", "47a013e660d408619d894b20806b1d5086aab03b"
                    )
                )
            ), infoDownloader.downloadIndexJson()
        )

        Assertions.assertEquals(
            ManifestJson(
                "1.0.0", "Fake Mod", "Fake Author", "AGPL-3.0",
                null, null, ManifestJson.ManifestLinks(
                    null, "https://github.com/ReviversMC/the-mod-index-api/fakeIndex", listOf(
                        ManifestJson.ManifestLinks.OtherLink(
                            "Discord", "https://discord.gg/6bTGYFppfz"
                        )
                    )
                ), listOf(
                    ManifestJson.ManifestFile(
                        "brick-1.18.2+1.2.0", listOf("1.18.2"),
                        "47a013e660d408619d894b20806b1d5086aab03b", emptyList()
                    )
                )
            ), infoDownloader.downloadManifestJson("bricks:fakeMod")
        )

        Assertions.assertEquals(
            ManifestJson.ManifestFile(
                "brick-1.18.2+1.2.0", listOf("1.18.2"),
                "47a013e660d408619d894b20806b1d5086aab03b", emptyList()
            ), infoDownloader.downloadManifestFileEntry("bricks:fakeMod:brick-1.18.2+1.2.0")
        )
    }
}