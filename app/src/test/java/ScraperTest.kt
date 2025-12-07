import com.shub39.rush.core.data.HttpClientFactory
import com.shub39.rush.core.data.network.GeniusScraper
import com.shub39.rush.core.domain.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import org.junit.Test

class ScraperTest {
    val scraper = GeniusScraper(client = HttpClientFactory.create())

    private fun testIn(title: String, block: suspend CoroutineScope.() -> Unit) = runBlocking {
        println("\n-- $title --")
        block.invoke(this)
        println("\n")
    }

    @Test
    fun testScrape() = testIn("Scrape test") {
        val lyrics = scraper.geniusScrape("https://genius.com/Haddaway-what-is-love-lyrics")

        println(lyrics)

        when (lyrics) {
            is Result.Error -> { println(lyrics.debugMessage) }
            is Result.Success -> { println(lyrics.data) }
        }
    }
}