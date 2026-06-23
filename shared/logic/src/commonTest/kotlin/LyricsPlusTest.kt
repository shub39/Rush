import com.shub39.rush.shared.logic.network.LyricsPlusApi
import kotlinx.coroutines.runBlocking
import kotlin.test.Test

class LyricsPlusTest {
    val api = LyricsPlusApi()

    @Test
    fun testApi() = runBlocking {
        val result = api.fetchTTML("DANCE...", "Slayyyter")
        println(result)
    }
}