package org.ayfaar.app.vocabulary

import nullOnBlank
import org.ayfaar.app.services.GoogleSpreadsheetService
import java.util.ArrayList

class VocabularyLoader {

    private fun loadData(): MutableList<MutableList<String>> {
        val service = GoogleSpreadsheetService()
        val spreadsheetId = "1W2zWkPVV2PirPDb6UHT-M4b-g0ZFeRDolcOOApg3bNY"
        return service.read(spreadsheetId, "A:Z") as MutableList<MutableList<String>>
    }

    fun getData() = getData(loadData())

    internal fun getData(data: MutableList<MutableList<String>>): List<VocabularyTerm> {
        val titles = data.removeAt(0)
        data.forEachIndexed { index, mutableList ->
            if (mutableList.first().toString().isEmpty()) {
                mutableList[0] = data[index - 1].first()
            }
        }
        val terms = data.groupBy { it.first() }.map { (_, records) ->
            val term = getBasicData(records.first())
            records.forEach { data ->
                setData(data, 10,  term.derivatives, true)
                setData(data, 12, term.derivatives, false)
                setData(data, 14, term.aliases, true)
                setData(data, 17, term.aliases, false)
                setData(data, 19, term.antonyms, true)
                setData(data, 21, term.antonyms, false)
                setData(data, 3, term.inPhrases, true)
            }
            term
        }

        return terms
    }

    private fun setData(data: MutableList<String>, index: Int, list: MutableCollection<VocabularySubTerm>, ii: Boolean) {
        if (data.getOrNull(index)?.isNotBlank() == true) {
            data[index].split(",", ";").forEach {
                list.add(VocabularySubTerm(
                        name = it.trim(),
                        description = data.getOrNull(index + 1).nullOnBlank(),
                        ii = ii))
            }
        }
    }

    private fun getBasicData(data: MutableList<String>) = VocabularyTerm(
            name = data[0],
            description = data[1],
            source = data.getOrNull(2).nullOnBlank(),
            reductions = data.getOrNull(5)?.split(",", ";")?.mapNotNull { it.trim().nullOnBlank() } ?: emptyList(),
            zkk = data.getOrNull(6).nullOnBlank(),
            pleyadyTerm = data.getOrNull(21).equals("да", true),
            inII = data.getOrNull(23).equals("да", true),
            conventional = data.getOrNull(24).equals("да", true),
            indication = parseIndications(data.getOrNull(25))
    )

}

internal fun parseIndications(text: String?)
        = text?.split("\n")?.map { it.split("#") }?.map {
    val type = if (it.size > 1) VocabularyIndicationType.resolve(it[0]) else VocabularyIndicationType.ITALIC
    val text = it.last()
    VocabularyIndication(text, type)
}

data class VocabularyTerm(
        val name: String,
        val source: String?,
        val description: String,
        val reductions: List<String> = ArrayList(),
        val zkk: String?,
        val derivatives: MutableCollection<VocabularySubTerm> = ArrayList(),
        val aliases: MutableCollection<VocabularySubTerm> = ArrayList(),
        val antonyms: MutableCollection<VocabularySubTerm> = ArrayList(),
        val inPhrases: MutableCollection<VocabularySubTerm> = ArrayList(),
        val pleyadyTerm: Boolean = false,
        val inII: Boolean = false,
        val conventional: Boolean = false,
        val indication: Collection<VocabularyIndication>?
)

enum class VocabularyIndicationType {
    ITALIC, BOLD, UNDERSCORE;

    companion object {
        fun resolve(text: String) = when(text) {
            "курсив" -> ITALIC
            "подчёркивание" -> UNDERSCORE
            "жирным" -> BOLD
            else -> throw Exception("Cannot understand $text as indication type")
        }

    }
}
data class VocabularyIndication(
        val text: String,
        val type: VocabularyIndicationType = VocabularyIndicationType.ITALIC
)

data class VocabularySubTerm(
        val name: String,
        val description: String?,
        val ii: Boolean
)
