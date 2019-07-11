package org.ayfaar.app.vocabulary

import nullOnBlank
import org.ayfaar.app.services.GoogleSpreadsheetService
import org.docx4j.Docx4J
import org.docx4j.openpackaging.packages.WordprocessingMLPackage
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Service
import java.io.File
import java.util.*
import javax.inject.Inject

@Service
class VocabularyService {
    private lateinit var styles: VocabularyStyles
    @Inject lateinit var resourceLoader: ResourceLoader

    fun getDoc() = getDoc(getData()/*, resourceLoader.getResource("classpath:template.docx").file*/)

    internal fun getDoc(data: List<VocabularyTerm>/*, template: File*/): File {

        val wordMLPackage = WordprocessingMLPackage.createPackage()//load(template)
        val mdp = wordMLPackage.mainDocumentPart

        styles = VocabularyStyles()
        styles.init(mdp)

        data.groupBy { if (it.name[0] != '«') it.name[0].toLowerCase() else it.name[1].toLowerCase() }.forEach { (firstLetter, terms) ->
            mdp.addStyledParagraphOfText(styles.alphabet, firstLetter.toString().toUpperCase())
            drawTerms(mdp, terms)
        }


        val file = File("test.docx")
        Docx4J.save(wordMLPackage, file)
        return file
    }

    private fun drawTerms(mdp: MainDocumentPart, terms: List<VocabularyTerm>) {
        terms.forEach { term ->
            drawTermFirstLine(term, mdp)

            var description = term.description.trim('.')

            val haveNextText = listOf(term.inPhrases, term.aliases, term.derivatives, term.antonyms).any { it.isNotEmpty() } || term.zkk != null
            val hasDotInside = description.contains('.')
            if (haveNextText || hasDotInside) {
                description += "."
            }

            mdp.addParagraph("<w:p xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\">\n" +
                    "            <w:pPr>\n" +
                    "                <w:pStyle w:val=\"${styles.description}\"/>\n" +
                    "            </w:pPr>\n" +
                    "            <w:r>\n" +
                    "                <w:t>$description</w:t>\n" +
                    "            </w:r>\n" +
                    "        </w:p>")

            drawSubTerm("В словосочетании:", "В словосочетаниях", term.inPhrases, mdp)
            drawSubTerm("Синоним", "Синонимы", term.aliases, mdp)
            drawSubTerm("Производное", "Производные", term.derivatives, mdp)
            drawSubTerm("Антоним", "Антонимы", term.antonyms, mdp)

            if (term.zkk != null) {
                mdp.addParagraph("<w:p xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\">\n" +
                        "   <w:pPr><w:pStyle w:val=\"${styles.description}\"/></w:pPr> " +
                        "   <w:r>\n" +
                        "     <w:rPr>\n" +
                        "        <w:i/>" +
                        "     </w:rPr>\n" +
                        "        <w:t xml:space=\"preserve\">Звуковой Космический Код (ЗКК): </w:t>\n" +
                        "    </w:r>"  +
                        "    <w:r>\n" +
                        "        <w:t xml:space=\"preserve\">${term.zkk}</w:t>\n" +
                        "    </w:r></w:p>")
            }
        }
    }

    private fun drawTermFirstLine(term: VocabularyTerm, mdp: MainDocumentPart) {
        var title = "<w:p xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\">\n" +
                "    <w:pPr>\n" +
                "        <w:pStyle w:val=\"${styles.term}\"/>\n" +
                "    </w:pPr>\n" +
                "    <w:r>\n" +
                "        <w:t xml:space=\"preserve\">${term.name} ${if (term.source == null && term.zkk == null) "—" else ""}</w:t>\n" +
                "    </w:r>"

        if (term.reductions.isNotEmpty()) {
            title += " ("
            title += term.reductions.joinToString(", ")
            title += ")"
        }
        if (term.zkk != null) {
            title += "  <w:r>\n" +
                    "       <w:rPr>\n" +
            "                    <w:rStyle w:val=\"${styles.term}\"/>\n" +
            "                    <w:b w:val=\"0\"/>\n" +
            "                    <w:i/>\n" +
            "                    <w:sz w:val=\"24\"/>\n" +
            "                </w:rPr>" +
                    "        <w:t xml:space=\"preserve\">— Звуковой Космический Код (ЗКК) —</w:t>\n" +
                    "    </w:r>\n"
        }
        if (term.source != null) {
            title += "  <w:r>\n" +
                    "       <w:rPr>\n" +
                    "            <w:b w:val=\"0\"/>\n" +
                    "            <w:bCs w:val=\"0\"/>\n" +
                    "            <w:i/>\n" +
                    "            <w:iCs/>\n" +
                    "            <w:sz w:val=\"20\"/>\n" +
                    "            <w:szCs w:val=\"20\"/>\n" +
                    "        </w:rPr>" +
                    "        <w:t xml:space=\"preserve\">— ${term.source} —</w:t>\n" +
                    "    </w:r>\n"
        }
        title += "</w:p>"

        mdp.addParagraph(title)
    }

    private fun drawSubTerm(singleLabel: String, multyLabel: String, subterms: MutableCollection<VocabularySubTerm>, mdp: MainDocumentPart) {
        if (subterms.isNotEmpty()) {
            val label = if (subterms.size > 1) multyLabel else singleLabel
            var text = "<w:p xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\" >\n" +
                    "            <w:pPr>\n" +
                    "                <w:pStyle w:val=\"${styles.subTermLabel}\"/>\n" +
                    "            </w:pPr>\n" +
                    "            <w:r>\n" +
                    "                <w:t xml:space=\"preserve\">$label: </w:t>\n" +
                    "            </w:r>"
            subterms.forEach { subTerm ->
                text += if (subTerm.ii) {
                    "<w:r w:rsidRPr=\"008B7EE6\">\n" +
                            "                <w:rPr>\n" +
                            "                    <w:rStyle w:val=\"${styles.description}\"/>\n" +
                            "                    <w:b/>" +
                            "                </w:rPr>\n" +
                            "                <w:t>${subTerm.name}</w:t>\n" +
                            "            </w:r>"
                } else {
                    "<w:r>\n" +
                            "                <w:rPr>\n" +
                            "                    <w:rStyle w:val=\"${styles.description}\"/>\n" +
                            "                </w:rPr>\n" +
                            "                <w:t>${subTerm.name}</w:t>\n" +
                            "            </w:r>"
                }
                text += when {
                    subTerm.description != null -> "<w:r>\n" +
                            "                <w:rPr>\n" +
                            "                    <w:rStyle w:val=\"${styles.description}\"/>\n" +
                            "                </w:rPr>\n" +
                            "                <w:t> – ${subTerm.description}</w:t>\n" +
                            "            </w:r>\n"
                    subterms.size > 1 -> "<w:r><w:t>; </w:t></w:r>"
                    else -> "<w:r><w:t>.</w:t></w:r>"
                }
            }
            text += "</w:p>"
            mdp.addParagraph(text)
        }
    }

    private fun loadData(): MutableList<MutableList<String>> {
        val service = GoogleSpreadsheetService()
        val spreadsheetId = "1W2zWkPVV2PirPDb6UHT-M4b-g0ZFeRDolcOOApg3bNY"
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
            reductions = data.getOrNull(6)?.split(",", ";")?.mapNotNull { it.trim().nullOnBlank() } ?: emptyList(),
            zkk = data.getOrNull(8).nullOnBlank(),
            pleyadyTerm = data.getOrNull(23).equals("да", true),
            inII = data.getOrNull(24).equals("да", true),
            conventional = data.getOrNull(25).equals("да", true)
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
