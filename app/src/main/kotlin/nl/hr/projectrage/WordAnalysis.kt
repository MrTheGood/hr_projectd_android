package nl.hr.projectrage

import java.util.*
import kotlin.collections.ArrayList

class WordAnalysis {
    private var word: String
        get() {
            return this.word
        }
        set(word: String) {
            this.word = word
        }
    private var localisation: String
        get() {
            return this.localisation
        }
        set(localisation: String) {
            this.localisation = localisation
        }
    private val words:List<String> = ArrayList<String>()
    private val syllables:List<String> = ArrayList<String>()

    constructor(word: String){
        WordAnalysis(word, Locale.getDefault().displayLanguage)
    }

    constructor(word: String, localisation: String){
        this.word = word
        this.localisation = localisation
    }

    fun dividIntoSyllables(){
    //    todo(slit in to words)
        val syllablePattern = "/(?<vowels>aa|a|oe|ie|ee|i|e|oo|o|uu|u)(?<consonances>[^\\k<vowels>]|\b)*\\k<vowels>/i".toRegex()
        val matchSyllables = syllablePattern.find(this.word)
        if (matchSyllables != null) {
            if (matchSyllables.value.length > 3){
                val pattern = "/((?<consonances>[^\\k<vowels>])*(?<vowels>aa|a|oe|ie|ee|i|e|oo|o|uu|u))(.*)/i".toRegex()
                val match = pattern.find(this.word)
                if(match != null){
                    syllables.add(match.groupValues.get(0))
                    syllables.add(match.groupValues.get(3))
                }else{
                    throw error("not a valid word")
                }
            }else {
                val pattern = "/$((?<consonances>[^\\k<vowels>])(?<vowels>aa|a|oe|ie|ee|i|e|oo|o|uu|u)\\k<consonances>)(.*)/i".toRegex()
                val match = pattern.find(this.word)
                if(match != null){
                    syllables.add(match.groupValues.get(0))
                    syllables.add(match.groupValues.get(3))
                }else{
                    throw error("not a valid word")
                }
            }
        }else{
            throw error("not a valid word")
        }
    }

    inline fun devideIntoWords(){
        return
    }
}