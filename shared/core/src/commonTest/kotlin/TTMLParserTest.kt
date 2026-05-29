package com.shub39.rush.shared.core.util

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TTMLParserTest {

    private val sampleTTML = """
        <tt xmlns="http://www.w3.org/ns/ttml" xmlns:ttm="http://www.w3.org/ns/ttml#metadata">
          <body>
            <div>
              <p begin="00:00:01.000" end="00:00:04.000" ttm:agent="v1">
                <span begin="00:00:01.000" end="00:00:01.500">Hello</span>
                <span begin="00:00:01.600" end="00:00:02.000">world</span>
              </p>
              <p begin="00:00:05.000" end="00:00:10.000">
                <span begin="00:00:05.000" end="00:00:06.000">Testing</span>
                <span role="x-bg" begin="00:00:07.000">
                   <span begin="00:00:07.000" end="00:00:08.000">background</span>
                </span>
              </p>
              <p begin="00:00:20.000" end="00:00:22.000">Long gap</p>
            </div>
          </body>
        </tt>
    """.trimIndent()

    @Test
    fun testIsValidTTML() {
        assertTrue(TTMLParser.isValidTTML(sampleTTML))
        assertFalse(TTMLParser.isValidTTML("<tt></tt>"))
        assertFalse(TTMLParser.isValidTTML(""))
    }

    @Test
    fun testParseTTML() {
        val lines = TTMLParser.parseTTML(sampleTTML)
        
        // Check if leading empty line was added (since first line starts at 1.0s, it shouldn't be added as it's < 5.0s)
        // Wait, the logic in TTMLParser.kt:
        // if (lines.firstOrNull()?.let { it.startTime > 5.0 } == true) { add(ParsedLine(text = "", startTime = 0.0, words = emptyList())) }
        
        assertEquals(4, lines.size) // 3 original lines + 1 empty line for the gap between 10s and 20s

        // First line
        val firstLine = lines[0]
        assertEquals("Hello world", firstLine.text)
        assertEquals(1.0, firstLine.startTime)
        assertEquals("v1", firstLine.agent)
        assertEquals(2, firstLine.words.size)
        assertEquals("Hello", firstLine.words[0].text)
        assertEquals(1.0, firstLine.words[0].startTime)
        assertEquals(1.5, firstLine.words[0].endTime)

        // Second line with background
        val secondLine = lines[1]
        assertEquals("Testing", secondLine.text)
        assertEquals(5.0, secondLine.startTime)
        assertEquals(1, secondLine.backgroundLines.size)
        assertEquals("background", secondLine.backgroundLines[0].text)
        assertEquals(7.0, secondLine.backgroundLines[0].startTime)
        assertTrue(secondLine.backgroundLines[0].isBackground)

        // Empty line for gap (> 5s between 10s and 20s)
        val gapLine = lines[2]
        assertEquals("", gapLine.text)
        // CurrentLineEnd = line.words.lastOrNull()?.endTime ?: line.startTime
        // For secondLine, words.last().endTime is 6.0
        assertEquals(6.0, gapLine.startTime)

        // Last line
        val lastLine = lines[3]
        assertEquals("Long gap", lastLine.text)
        assertEquals(20.0, lastLine.startTime)
    }

    @Test
    fun testToLRC() {
        val lrc = TTMLParser.toLRC(sampleTTML)
        
        assertTrue(lrc.contains("[00:01.00]Hello world"))
        assertTrue(lrc.contains("<Hello:1.0:1.5|world:1.6:2.0>"))
        assertTrue(lrc.contains("[00:07.00]{bg}background"))
        assertTrue(lrc.contains("[00:20.00]Long gap"))
    }

    @Test
    fun testParseTime() {
        // Since parseTime is private, we test it through parseTTML or toLRC
        // But we can check various time formats in a sample TTML
        val ttml = """
            <tt><body><div>
                <p begin="1:02:03.45">H</p>
                <p begin="02:03.45">M</p>
                <p begin="3.45">S</p>
            </div></body></tt>
        """.trimIndent()
        
        val lines = TTMLParser.parseTTML(ttml)
        // Since 3723.45 > 5.0, a leading empty line is added at 0.0s
        assertEquals(4, lines.size)
        assertEquals(0.0, lines[0].startTime)
        assertEquals("", lines[0].text)

        assertEquals(3723.45, lines[1].startTime) // 3600 + 120 + 3.45
        assertEquals(123.45, lines[2].startTime)   // 120 + 3.45
        assertEquals(3.45, lines[3].startTime)
    }
}
