package nl.hr.projectrage

import java.util.*
import kotlin.collections.ArrayList

class WordAnalysis(
    private var localisation: String = Locale.getDefault().displayLanguage
) {
    private val dictionary = listOf<String>(

    )

    private val words = ArrayList<String>()

    fun dividIntoSyllables(word: String): List<String> {
        if(isValidWord(word)){
            val syllablePattern = """/(?<vowels>aa|a|oe|ie|ee|i|e|oo|o|uu|u)(?<consonances>[^\k<vowels>]|\b)*\k<vowels>/i""".toRegex()
            val matchSyllables = syllablePattern.find(word)

            val pattern =
                if (matchSyllables == null)"""/.*/i""".toRegex()
                else if(matchSyllables.value.length > 3) """/((?<consonances>[^\k<vowels>])*(?<vowels>aa|a|oe|ie|ee|i|e|oo|o|uu|u))(.*)/i""".toRegex()
                else """/$((?<consonances>[^\k<vowels>])(?<vowels>aa|a|oe|ie|ee|i|e|oo|o|uu|u)\k<consonances>)(.*)/i""".toRegex()


            val match = pattern.find(word)!!
            return listOf(
                match.groupValues[0],
                match.groupValues[3]
            )
        }else{
            throw Error("not a valid word")
        }
    }

    fun devideIntoWords() {
        TODO()
    }

    fun divideIntoPhonetics(){

    }

    private fun isValidWord(word: String):Boolean{
        val vowelPattern = """(?<vowels>aa|a|oe|ie|ee|i|e|oo|o|uu|u)""".toRegex()
        val match = vowelPattern.find(word)
        if(match != null){
            return true
        }
        return false
    }
}

data class Klanken(
    val name: String,
    val klank: String
)

val klankerList = listOf(
    Klanken("p", "p"),
    Klanken("b", "b"),
    Klanken("t", "t"),
    Klanken("d", "d"),
    Klanken("c", "c"),
    Klanken("k", "k"),
    Klanken("g", "g"),
    Klanken("q", "q"),
    Klanken("m", "m"),
    Klanken("n", "n"),
    Klanken("r", "r"),
    Klanken("f", "f"),
    Klanken("v", "v"),
    Klanken("s", "s"),
    Klanken("j", "j"),
    Klanken("h", "h"),
    Klanken("X", "ch"),
    Klanken("esh", "sch"),
    Klanken("ezh", "sj"),
    Klanken("stemloze_retroflexe_fricatief", "sj"),
    Klanken("Stemhebbende_palatale_fricatief", "j"),
    Klanken("i", "i"),
    Klanken("y", "y"),
    Klanken("u", "u"),
    Klanken("e", "e"),
    Klanken("o", "o"),
    Klanken("a", "a"),
    Klanken("Y", "eu"),
    Klanken("Open-mid_central_unrounded_vowel", "ui"),
    Klanken("Open-mid_back_unrounded_vowel", "ah"),
    Klanken("ä", "uh"),
    Klanken("Open_back_rounded_vowel", "oh"),
    Klanken("ç", "ch"),
    Klanken("ɳ", "na")
)