import com.shub39.rush.core.data.HttpClientFactory
import com.shub39.rush.core.data.network.GeniusScraper
import kotlinx.coroutines.runBlocking
import org.junit.Test

class ScraperTest {
    val scraper = GeniusScraper(client = HttpClientFactory.create())

    @Test
    fun testScrape() = runBlocking {
        println(scraper.scrapeLyrics("https://genius.com/Daughters-the-flammable-man-lyrics"))
    }
}