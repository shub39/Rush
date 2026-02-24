import com.shub39.rush.data.network.LrcLibApi
import com.shub39.rush.domain.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import org.junit.Test

class LrcLibApiTest {
    private val lrcLibApi = LrcLibApi()

    private fun testIn(title: String, block: suspend CoroutineScope.() -> Unit) = runBlocking {
        println("\n-- $title --")
        block.invoke(this)
        println("\n")
    }

    @Test
    fun testSearch() = testIn("Test Search") {
        when (val result = lrcLibApi.searchLrcLyrics("DtmF", "Bad Bunny")) {
            is Result.Error -> throw Exception("${result.error} : ${result.message}")
            is Result.Success -> println(result.data)
        }
    }
}