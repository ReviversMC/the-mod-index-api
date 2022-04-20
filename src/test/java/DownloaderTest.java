import com.github.reviversmc.themodindex.api.data.IndexJson;
import com.github.reviversmc.themodindex.api.data.ManifestJson;
import com.github.reviversmc.themodindex.api.downloader.IndexInfoDownloader;
import com.github.reviversmc.themodindex.api.downloader.InfoDownloader;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class DownloaderTest {

    @Test
    void shouldReturnFakeModInfo() throws IOException {
        InfoDownloader infoDownloader = new IndexInfoDownloader(
                new OkHttpClient.Builder().build(),
                "https://raw.githubusercontent.com/ReviversMC/the-mod-index-api/main/fakeIndex/"
        );

        Assertions.assertEquals(
                new IndexJson(Optional.of("1.0.0"),
                        List.of(
                                new IndexJson.IndexFile(
                                        Optional.of("bricks:fakeMod:brick-1.18.2+1.2.0"),
                                        Optional.of("47a013e660d408619d894b20806b1d5086aab03b")
                                )
                        )
                ), infoDownloader.downloadIndexJson().orElse(new IndexJson(Optional.empty(), List.of()))
        );

        Assertions.assertEquals(
                new ManifestJson(
                        Optional.of("1.0.0"), Optional.of("Fake Mod"),
                        Optional.of("Fake Author"), Optional.of("AGPL-3.0"),
                        Optional.empty(), Optional.empty(),
                        Optional.of(
                                new ManifestJson.ManifestLinks(
                                        Optional.empty(),
                                        Optional.of("https://github.com/ReviversMC/the-mod-index-api/fakeIndex"),
                                        List.of(
                                                new ManifestJson.ManifestLinks.OtherLink(
                                                        Optional.of("Discord"),
                                                        Optional.of("https://discord.gg/6bTGYFppfz")
                                                )
                                        )
                                )
                        ),
                        List.of(
                                new ManifestJson.ManifestFile(
                                        Optional.of("brick-1.18.2+1.2.0"),
                                        List.of("1.18.2"),
                                        Optional.of("47a013e660d408619d894b20806b1d5086aab03b"),
                                        List.of()
                                )
                        )
                ),
                infoDownloader.downloadManifestJson("bricks:fakeMod").orElse(
                        new ManifestJson(
                                Optional.empty(), Optional.empty(),
                                Optional.empty(), Optional.empty(),
                                Optional.empty(), Optional.empty(),
                                Optional.empty(), List.of()
                        )
                )
        );
    }

}
