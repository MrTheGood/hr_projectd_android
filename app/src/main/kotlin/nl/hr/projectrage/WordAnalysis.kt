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
        val words = divideIntoWords(word)
        if (words.isEmpty()) error("Not a valid word")
        words.forEach {
            val matchSyllables = syllablePattern.find(it)
            val fullMatch = matchSyllables?.groupValues?.firstOrNull()

            val pattern =
                when {
                    fullMatch == null -> """.*""".toRegex()
                    fullMatch.length == 3 -> """($consonances*($vowels))(.*)""".toRegex()
                    else -> """$($consonances$vowels$consonances)(.*)""".toRegex()
                }


            val match = pattern.find(word) ?: run {
                //Fixme: Nearly always it takes pattern "/.*/i" that does not manage to match anything
                // Therefore, this error occurs a lot.
                Log.wtf("error", "No match found with pattern: ${pattern.pattern}")
                error("Failed to determine quality")
            }

            if (match.groupValues.size == 1)
                syllables.add(match.groupValues[0])
            else {
                syllables.add(match.groupValues[1])
                syllables.add(match.groupValues[4])
            }
        }

        return syllables
    }

    private fun divideIntoWords(word: String): List<String> {
        var shortList = dictionary.filter {
            word.contains(it)
        }
        if (shortList.size > 1){
            shortList = shortList.filter { !it.equals(word) }
            shortList = shortList.filter { og ->
                for (it in shortList) {
                    if(!og.equals(it) && og.contains(it)){
                        return@filter true;
                    }
                }
                return@filter false;
            }
        }
        if(shortList.isEmpty()){
            return listOf(word)
        }
        return shortList;
    }

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

    fun calcScore(word: String): Double {
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
        val vowelPattern = """(aa|a|oe|ie|ee|i|e|oo|o|uu|u)""".toRegex()
        return vowelPattern.find(word) != null
    }
}

data class Klanken(
    val name: String,
    val klank: String,
    val score: Int
)

val klankenList = listOf(
    Klanken("p", "p", 76),
    Klanken("b", "b", 45),
    Klanken("t", "t", 53),
    Klanken("d", "d", 72),
    Klanken("c", "c", 73),
    Klanken("k", "k", 63),
    Klanken("g", "g", 54),
    Klanken("q", "q", 8),
    Klanken("m", "m", 67),
    Klanken("n", "n", 79),
    Klanken("r", "r", 76),
    Klanken("f", "f", 73),
    Klanken("v", "v", 62),
    Klanken("s", "s", 66),
    Klanken("j", "j", 88),
    Klanken("h", "h", 62),
    Klanken("X", "x", 10),
    Klanken("esh", "sch", 69),
    Klanken("ezh", "sj", 53),
    Klanken("i", "i", 97),
    Klanken("y", "y", 63),
    Klanken("u", "u", 77),
    Klanken("e", "e", 82),
    Klanken("o", "o", 87),
    Klanken("a", "a", 76),
    Klanken("Y", "eu", 89),
    Klanken("Open-mid_central_unrounded_vowel", "ui", 61),
    Klanken("ä", "uh", 93),
    Klanken("Open_back_rounded_vowel", "oh", 83),
    Klanken("ç", "ch", 86),
    Klanken("ɳ", "na", 85)
)

fun List<Klanken>.toPhoneticsRegexPattern() =
    StringBuilder("(").also { pattern ->
        for ((i, it) in this.withIndex())
            pattern.append(it.klank + "|".takeIf { i != klankenList.size - 1 })
        pattern.append(")+")
    }.toString().toRegex()