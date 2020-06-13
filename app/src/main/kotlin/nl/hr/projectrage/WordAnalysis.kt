package nl.hr.projectrage

import android.content.res.Resources
import android.util.Log
import org.json.JSONArray
import java.io.IOException

class WordAnalysis(res: Resources) {
    private val dictionary: List<String>

    init {
        try {
            val rawJson = res.openRawResource(R.raw.dictionary).bufferedReader().use { it.readText() }
            val jsonArray = JSONArray(rawJson)

            val tmpList = ArrayList<String>()
            for (i in 0 until jsonArray.length())
                tmpList.add(jsonArray[i] as? String ?: continue)
            dictionary = tmpList
        } catch (e: IOException) {
            TODO("Well, shit.. Error not handled by prototype")
        }
    }

    private fun divideIntoSyllables(word: String): List<String> {
        val syllables = ArrayList<String>()
        if (!isValidWord(word))
            error("Not a valid word")

        val vowels = """(aa|a|oe|ie|ee|i|e|oo|o|uu|u)"""
        val consonances = """([^$vowels])"""

        val syllablePattern = """$vowels$consonances+$vowels""".toRegex()


        Log.wtf("test", "word:$word,divideIntoWords:${divideIntoWords(word)}")
        divideIntoWords(word).forEach {
            val matchSyllables = syllablePattern.find(it)

            val pattern =
                when {
                    matchSyllables == null -> """.*""".toRegex()
                    matchSyllables.value.length > 3 -> """($consonances*($vowels))(.*)""".toRegex()
                    else -> """$($consonances$vowels$consonances)(.*)""".toRegex()
                }


            val match = pattern.find(word) ?: run {
                //Fixme: Nearly always it takes pattern "/.*/i" that does not manage to match anything
                // Therefore, this error occurs a lot.
                Log.wtf("error", "No match found with pattern: ${pattern.pattern}")
                error("Failed to determine quality")
            }
            syllables.add(match.groupValues[0])
            syllables.add(match.groupValues[3])
        }

        return syllables
    }

    private fun divideIntoWords(word: String) =
        dictionary.filter { word.contains(it) }

    private fun divideIntoPhonetics(syllables: List<String>): List<Klanken> {
        val phonetics = ArrayList<Klanken>()

        val pattern = klankenList.toPhoneticsRegexPattern()
        syllables.forEach { syllable ->
            pattern.find(syllable)!!.groupValues.forEach { match ->
                klankenList.find { it.klank == match }?.let { phonetics.add(it) }
            }
        }
        return phonetics
    }

    private fun calcScore(word: String): Double {
        val syllables = divideIntoSyllables(word)
        val phonetics = divideIntoPhonetics(syllables)
        val totalPoints = phonetics.sumBy { it.score }.toDouble()
        return totalPoints / phonetics.size
    }

    fun getScore(word: String) = calcScore(word).let { score ->
        when {
            score <= 33 -> "bad"
            score <= 66 -> "average"
            else -> "good"
        }
    }

    private fun isValidWord(word: String): Boolean {
        val vowelPattern = """(?<vowels>aa|a|oe|ie|ee|i|e|oo|o|uu|u)""".toRegex()
        return vowelPattern.find(word) != null
    }
}

data class Klanken(
    val name: String,
    val klank: String,
    val score: Int
)

val klankenList = listOf(
    Klanken("p", "p", 30),
    Klanken("b", "b", 40),
    Klanken("t", "t", 72),
    Klanken("d", "d", 80),
    Klanken("c", "c", 50),
    Klanken("k", "k", 100),
    Klanken("g", "g", 85),
    Klanken("q", "q", 81),
    Klanken("m", "m", 74),
    Klanken("n", "n", 85),
    Klanken("r", "r", 78),
    Klanken("f", "f", 30),
    Klanken("v", "v", 70),
    Klanken("s", "s", 50),
    Klanken("j", "j", 80),
    Klanken("h", "h", 86),
    Klanken("X", "ch", 90),
    Klanken("esh", "sch", 87),
    Klanken("ezh", "sj", 74),
    Klanken("stemloze_retroflexe_fricatief", "sj", 74),
    Klanken("Stemhebbende_palatale_fricatief", "j", 80),
    Klanken("i", "i", 40),
    Klanken("y", "y", 69),
    Klanken("u", "u", 75),
    Klanken("e", "e", 85),
    Klanken("o", "o", 71),
    Klanken("a", "a", 86),
    Klanken("Y", "eu", 86),
    Klanken("Open-mid_central_unrounded_vowel", "ui", 76),
    Klanken("Open-mid_back_unrounded_vowel", "ah", 89),
    Klanken("ä", "uh", 72),
    Klanken("Open_back_rounded_vowel", "oh", 50),
    Klanken("ç", "ch", 90),
    Klanken("ɳ", "na", 70)
)

fun List<Klanken>.toPhoneticsRegexPattern() =
    StringBuilder("(").also { pattern ->
        for ((i, it) in this.withIndex())
            pattern.append(it.klank + "|".takeIf { i != klankenList.size - 1 })
        pattern.append(")+")
    }.toString().toRegex()