package org.ayfaar.app.vocabulary

import org.ayfaar.app.utils.RegExpUtils.W
import org.ayfaar.app.vocabulary.VocabularyIndicationType.*
import org.docx4j.Docx4J
import org.docx4j.openpackaging.packages.WordprocessingMLPackage
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart
import org.docx4j.wml.*
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Service
import java.io.File
import java.math.BigInteger
import java.util.regex.Pattern
import javax.inject.Inject

@Service
class VocabularyService(private val helper: VocabularyUpperWordsHelper) {
    private lateinit var styles: VocabularyStyles
    @Inject lateinit var resourceLoader: ResourceLoader

    fun getDoc(fileName: String = "test.docx") = getDoc(VocabularyLoader().getData(), fileName/*, resourceLoader.getResource("classpath:template.docx").file*/)

    internal fun getDoc(data: List<VocabularyTerm>, fileName: String/*, template: File*/): File {

        val wordMLPackage = WordprocessingMLPackage.createPackage()//load(template)
        val mdp = wordMLPackage.mainDocumentPart

        styles = VocabularyStyles()
        styles.init(mdp)

        val groupedByFirstLetter = data.groupBy { if (it.name[0] != '«') it.name[0].toLowerCase() else it.name[1].toLowerCase() }
        var first = true

        groupedByFirstLetter.forEach { (firstLetter, terms) ->
            if (first) {
                first = false
            } else {
                mdp.addObject(P().apply {
                    content.add(R().apply {
                        content.add(Br().apply { type = STBrType.PAGE })
                    })
                })
            }
            mdp.addStyledParagraphOfText(styles.alphabet, firstLetter.toString().toUpperCase())
            drawTerms(mdp, terms)
        }

        val file = File(fileName)
        Docx4J.save(wordMLPackage, file)
        return file
    }

    private fun drawTerms(mdp: MainDocumentPart, terms: List<VocabularyTerm>) {
        terms.forEach { term ->
            drawTermFirstLine(term, mdp)

            var description = term.description.proceed().let { helper.check(it) }

            val haveNextText = listOf(term.inPhrases, term.aliases, term.derivatives, term.antonyms).any { it.isNotEmpty() } || term.zkk != null
            val hasDotInside = description.contains('.')
            if (haveNextText || hasDotInside) {
                description += "."
            }

            var p = P().styled(styles.description)

            if (term.inPleadsCivilisations) p.addContent("в плеядеянских цивилизациях: ") { i = True() }
            if (term.pleadsTerm) p.addContent("плеядианский термин: ") { i = True() }
            if (term.inII) p.addContent("в ииссиидиологии: ") { i = True() }
            if (term.conventional) p.addContent("совпадает с общепринятым значением: ") { i = True() }

            if (description.contains("\n")) {
                description.split("\n", "\r").forEach {
                    p.withContent(it, term.indication)
                    mdp.addObject(p)
                    p = P().styled(styles.description)
                }
            } else {
                p.withContent(description, term.indication)
                mdp.addObject(p)
            }


            drawSubTerm("Синоним", "Синонимы", term.aliases, mdp, term.indication)
            drawSubTerm("Антоним", "Антонимы", term.antonyms, mdp, term.indication)
            drawSubTerm("В словосочетании", "В словосочетаниях", term.inPhrases.map { it.copy(ii = true) }, mdp, term.indication)
            drawSubTerm("Производное", "Производные", term.derivatives, mdp, term.indication)

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
        val termName = term.name.let { helper.check(it) }
//        val tail = if (term.source == null && term.zkk == null && term.reductions.isEmpty()) "—" else ""
        val p = P()
                .styled(styles.term)
                .addContent(termName)
        /*var title = "<w:p xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\">\n" +
                "    <w:pPr>\n" +
                "        <w:pStyle w:val=\"${styles.term}\"/>\n" +
                "    </w:pPr>\n" +
                "    <w:r>\n" +
                "        <w:t xml:space=\"preserve\">$termName ${if (term.source == null && term.zkk == null) "—" else ""}</w:t>\n" +
                "    </w:r>"*/

        if (term.reductions.isNotEmpty()) {
            p.addContent(" (" + term.reductions.joinToString(", ") + ")")
        }
        if (term.source != null) {
            p.addContent(" " + term.source.proceed()) {
                b = False()
                bCs = False()
                i = True()
                iCs = True()
                sz = HpsMeasure().apply { `val` = BigInteger.valueOf(20L) }
                szCs = HpsMeasure().apply { `val` = BigInteger.valueOf(20L) }
            }
        }
        if (term.zkk != null) {
            p.addContent(" — Звуковой Космический Код (ЗКК)", styles.term) {
                b = False()
                i = True()
                sz = HpsMeasure().apply { `val` = BigInteger.valueOf(24L) }
            }
        }

        p.addContent(" —")

        mdp.addObject(p)
    }

    private fun drawSubTerm(singleLabel: String, multyLabel: String, subterms: Collection<VocabularySubTerm>, mdp: MainDocumentPart, indication: Collection<VocabularyIndication>?) {
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

            val withoutDescription = subterms.filter { it.description == null }
            val withDescription = subterms.filterNot { it.description == null }

            if (withoutDescription.isNotEmpty()) {
                withoutDescription.forEach { subTerm ->
                    p.addHead(subTerm.name.proceed(), subTerm.ii)
                    val tail = when {
                        subterms.last() == subTerm && withDescription.isEmpty() -> "."
                        withDescription.isNotEmpty() -> ";"
                        else -> ", "
                    }
                    p.withContent(tail) { i = False() }
                }
                mdp.addObject(p)
                if (withDescription.isNotEmpty()) p = P().styled(styles.subTermLabel)
            }

            if (withDescription.isNotEmpty()) {
                withDescription.forEach { subTerm ->
                    val lastOne = subterms.last() == subTerm
                    p.addHead(subTerm.name.proceed(), subTerm.ii)
                    val tail = if (lastOne) "." else ";"

                    val description = subTerm.description?.proceed()?.let { helper.check(it) }
                    p.withContent(" — $description$tail", indication, styles.description) {
                        i = False()
                    }
                    mdp.addObject(p)
                    if (!lastOne) p = P().styled(styles.description)
                }
            }
        }
    }
}

private fun P.pageBreak() = this.content.add(Br().apply { type = STBrType.PAGE })

internal fun String.proceed() = this.trim().trim('.').trim().let { s -> s
            .replace("й", "й")
            .replace("ё", "ё")
            .replace(Regex("“(.+?)”")) { "«${it.groupValues[1]}»" }
            .replace(Regex("\"(.+?)\"")) { "«${it.groupValues[1]}»" }
            .replace(Regex("($W)[-–]($W)"), "$1—$2")
}

private fun P.styled(style: String) = this.apply {
    pPr = PPr().apply { pStyle = PPrBase.PStyle().apply { `val` = style } }
    return this
}

private fun R.styled(style: String) = this.apply {
    rPr = RPr().apply { rStyle = RStyle().apply { `val` = style } }
}

private operator fun P.plusAssign(s: String)  { this.addContent(s) }

private fun P.addContent(text: String): P {
    this.content.add(R().also { r -> r.content.add(Text().also { t -> t.space = "preserve"; t.value = text }) })
    return this
}

private fun P.withContent(text: String): P {
    this.addContent(text)
    return this
}

internal fun P.withContent(text: String, indication: Collection<VocabularyIndication>? = null, style: String? = null, block: (RPr.() -> Unit)? = null): P {
    if (indication == null) return this.addContent(text, style, block)

    val matcher = Pattern.compile(indication.joinToString("|") {
        it.text.replace("(", "\\(").replace(")", "\\)")
    }).matcher(text)

    var parts: MutableList<Pair<String, VocabularyIndicationType?>> = ArrayList()
    var start = 0

    while (matcher.find()) {
        parts.add(text.substring(start, matcher.start()) to null)
        parts.add(matcher.group() to indication.find { it.text == matcher.group() }?.type)
        start = matcher.end()
    }
    parts.add(text.substring(start) to null)

    parts = parts.filter { it.first.isNotEmpty() }.toMutableList()

    parts.map { (text, identType) ->
        this.content.add(R().also { r ->
            style?.let { r.styled(it) }
            r.rPr = RPr()
            block?.let { it(r.rPr) }
            when(identType) {
                ITALIC -> r.rPr.apply { i = BooleanDefaultTrue() }
                BOLD -> r.rPr.apply { b = BooleanDefaultTrue() }
                UNDERSCORE -> r.rPr.apply { u = U().apply { `val` = UnderlineEnumeration.SINGLE } }
            }
            r.content.add(Text().also { t ->
                t.space = "preserve"; t.value = text
            })
        })
    }

    return this
}

private fun P.addContent(text: String, style: String? = null, block: (RPr.() -> Unit)? = null): P {
    R().also { r ->
        r.rPr = RPr().apply {
            rStyle?.let { rStyle = RStyle().apply { `val` = style } }
            block?.let { it(this) }
        }
        r.content.add(Text().also { t -> t.space = "preserve"; t.value = text })
    }.also {
        this.content.add(it)
    }
    return this
}

private fun True() = BooleanDefaultTrue()
private fun False() = BooleanDefaultTrue().apply { isVal = false }
