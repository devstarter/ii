package org.ayfaar.app.vocabulary

import nullOnBlank
import org.ayfaar.app.services.GoogleSpreadsheetService
import org.docx4j.Docx4J
import org.docx4j.openpackaging.packages.WordprocessingMLPackage
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Service
import java.io.File
import java.util.ArrayList

@Service
class VocabularyService(private val resourceLoader: ResourceLoader) {
    fun getDoc() = getDoc(getData())

    internal fun getDoc(data: List<VocabularyTerm>): File {

        val template = resourceLoader.getResource("classpath:template.docx").file
        val wordMLPackage = WordprocessingMLPackage.load(template)
        val mdp = wordMLPackage.mainDocumentPart


        data.groupBy { if (it.name[0] != '«') it.name[0] else it.name[1] }.forEach { (firstLetter, terms) ->
            mdp.addStyledParagraphOfText("Heading3", firstLetter.toString().toUpperCase())
            drawTerms(mdp, terms)
        }


        val file = File("test.docx")
        Docx4J.save(wordMLPackage, file)
        return file
    }

    private fun drawTerms(mdp: MainDocumentPart, terms: List<VocabularyTerm>) {
        terms.forEach { term ->
            var title = term.name

            if (term.reductions.isNotEmpty()) {
                title += " ("
                title += term.reductions.joinToString(", ")
                title += ")"
            }
            if (term.zkk != null) {
                title += " - Звуковой Космический Код (ЗКК)"
            }
            if (term.source != null) {
                title += " ${term.source}"
            }
            title += " -"

            mdp.addStyledParagraphOfText("Heading4", title)
            mdp.addStyledParagraphOfText("Определение", term.description)

            drawSubTerm("В словосочетаниях", term.inPhrases, mdp)
            drawSubTerm("Синонимы", term.aliases, mdp)
            drawSubTerm("Производные", term.derivatives, mdp)
            drawSubTerm("Антонимы", term.antonyms, mdp)

            if (term.zkk != null) {
                mdp.addStyledParagraphOfText("?", "Звуковой Космический Код (ЗКК): ${term.zkk}.")
            }
        }
    }

    private fun drawSubTerm(label: String, subterms: MutableCollection<VocabularySubTerm>, mdp: MainDocumentPart) {
        if (subterms.isNotEmpty()) {
            var text = "$label: "
            subterms.forEach { subTerm ->
                text += if (subTerm.ii) "*${subTerm.name}*" else subTerm.name
                text += when {
                    subTerm.description != null -> " - ${subTerm.description}.\n"
                    subterms.size > 1 -> "; "
                    else -> "."
                }
            }
            mdp.addParagraphOfText(text)
        }
    }

    private fun loadData(): MutableList<MutableList<String>> {
        val service = GoogleSpreadsheetService()
        val spreadsheetId = "1Xm_bw6PEHPN8N6aaHia_0kHlad8Hp7LJBELfn2I2mDE"
        return service.read(spreadsheetId, "A:Z") as MutableList<MutableList<String>>
    }

    internal fun getData() = getData(loadData())

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
                setData(data, 8,  term.derivatives, true)
                setData(data, 10, term.derivatives, false)
                setData(data, 12, term.aliases, true)
                setData(data, 14, term.aliases, false)
                setData(data, 16, term.antonyms, true)
                setData(data, 18, term.antonyms, false)
                setData(data, 20, term.inPhrases, true)
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
            name = data.get(0),
            source = data.getOrNull(1).nullOnBlank(),
            description = data.get(2),
            reductions = data.getOrNull(4)?.split(",", ";")?.mapNotNull { it.trim().nullOnBlank() } ?: emptyList(),
            zkk = data.getOrNull(6).nullOnBlank(),
            pleyadyTerm = data.getOrNull(23).equals("да", true),
            inII = data.getOrNull(23).equals("да", true),
            conventional = data.getOrNull(23).equals("да", true)
    )
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
        val conventional: Boolean = false
)

data class VocabularySubTerm(
        val name: String,
        val description: String?,
        val ii: Boolean
)
