package org.ayfaar.app.vocabulary

import org.docx4j.Docx4J
import org.docx4j.openpackaging.packages.WordprocessingMLPackage
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart
import org.docx4j.wml.*
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Service
import java.io.File
import javax.inject.Inject

@Service
class VocabularyService {
    private lateinit var styles: VocabularyStyles
    @Inject lateinit var resourceLoader: ResourceLoader

    fun getDoc() = getDoc(VocabularyLoader().getData()/*, resourceLoader.getResource("classpath:template.docx").file*/)

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

            var description = term.description.trim().trim('.')

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

            drawSubTerm("В словосочетании", "В словосочетаниях", term.inPhrases.map { it.copy(ii = false) }, mdp)
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

    private fun drawSubTerm(singleLabel: String, multyLabel: String, subterms: Collection<VocabularySubTerm>, mdp: MainDocumentPart) {
        if (subterms.isNotEmpty()) {
            val label = if (subterms.size > 1) multyLabel else singleLabel

            var p = P().styled(styles.subTermLabel)

            p += "$label: "

            fun P.addHead(name: String, ii: Boolean) = this.addContent(name, styles.description) {
                if (ii) {
                    b = BooleanDefaultTrue()
                }
                i = False()
            }

            if (subterms.first().description == null) {
                subterms.forEach { subTerm ->
                    p.addHead(subTerm.name, subTerm.ii)
                    p.addContent(if (subterms.last() == subTerm) "." else ", ")
                }
                mdp.addObject(p)
            } else {
                subterms.forEach { subTerm ->
                    val lastOne = subterms.last() == subTerm
                    p.addHead(subTerm.name, subTerm.ii)
                    val tail = if (lastOne) "." else ";"
                    p.addContent(" – ${subTerm.description}$tail", styles.description) {
                        i = False()
                    }
                    mdp.addObject(p)
                    if (!lastOne) p = P().styled(styles.description)
                }
            }
        }
    }
}

private fun P.styled(style: String) = this.apply {
    pPr = PPr().apply { pStyle = PPrBase.PStyle().apply { `val` = style } }
}

private operator fun P.plusAssign(s: String) = this.addContent(s)

private fun P.addContent(text: String) {
    this.content.add(R().also { r -> r.content.add(Text().also { t -> t.space = "preserve"; t.value = text }) })
}

private fun P.addContent(text: String, style: String, block: RPr.() -> Unit) {
    R().also { r ->
        r.rPr = RPr().apply {
            rStyle = RStyle().apply { `val` = style }
            block(this)
        }
        r.content.add(Text().also { t -> t.space = "preserve"; t.value = text })
    }.also {
        this.content.add(it)
    }
}

private fun False() = BooleanDefaultTrue().apply { isVal = false }
