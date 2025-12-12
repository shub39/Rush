import com.shub39.rush.core.data.HttpClientFactory
import com.shub39.rush.core.data.network.GeniusApi
import com.shub39.rush.core.domain.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import org.junit.Test

class GeniusApiTest {
    val geniusApi = GeniusApi(client = HttpClientFactory.create())

    private fun testIn(title: String, block: suspend CoroutineScope.() -> Unit) = runBlocking {
        println("\n-- $title --")
        block.invoke(this)
        println("\n")
    }

    @Test
    fun testSearch() = testIn("Test Search") {
        when (val search = geniusApi.geniusSearch("Satan in the wait")) {
            is Result.Error -> {
                println(search.debugMessage)
            }
            is Result.Success -> { println(search.data) }
        }
    }
}