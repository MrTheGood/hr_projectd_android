package nl.hr.projectrage

import java.util.*
import kotlin.collections.ArrayList

class WordAnalysis(
    private var localisation: String = Locale.getDefault().displayLanguage
) {
    private val words = ArrayList<String>()

    fun dividIntoSyllables(word: String): List<String> {
        val syllablePattern = """/(?<vowels>aa|a|oe|ie|ee|i|e|oo|o|uu|u)(?<consonances>[^\k<vowels>]|\b)*\k<vowels>/i""".toRegex()
        val matchSyllables = syllablePattern.find(word)!!

        val pattern =
            if (matchSyllables.value.length > 3) """/((?<consonances>[^\k<vowels>])*(?<vowels>aa|a|oe|ie|ee|i|e|oo|o|uu|u))(.*)/i""".toRegex()
            else """/$((?<consonances>[^\k<vowels>])(?<vowels>aa|a|oe|ie|ee|i|e|oo|o|uu|u)\k<consonances>)(.*)/i""".toRegex()

        val match = pattern.find(word)!!
        return listOf(
            match.groupValues[0],
            match.groupValues[3]
        )
    }

    fun devideIntoWords() {
        TODO()
    }
}