package org.ayfaar.app.vocabulary

import org.docx4j.jaxb.Context
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart
import org.docx4j.openpackaging.parts.WordprocessingML.StyleDefinitionsPart
import org.docx4j.wml.*
import java.math.BigInteger

class VocabularyStyles {

    val alphabet: String = "alphabet"
    val term: String = "term"
    val description: String = "description"
    val subTermLabel: String = "subTermLabel"

    fun init(mdp: MainDocumentPart) {
        val sdp = mdp.styleDefinitionsPart

        initAlphabet(sdp)
        initTerm(sdp)
        initDescription(sdp)
        initSubTermLabel(sdp)
    }

    private fun initDescription(sdp: StyleDefinitionsPart) {
        val style = Context.getWmlObjectFactory().createStyle()
        style.type = "paragraph"
        style.name = createName(description)
        style.basedOn = createBasedOn("DefaultParagraphFont")
        style.styleId = description
        style.rPr = Context.getWmlObjectFactory().createRPr().also {
            it.sz = HpsMeasure().also { it.`val` = BigInteger.valueOf(24L) }
            it.szCs = HpsMeasure().also { it.`val` = BigInteger.valueOf(24L) }
            it.rFonts = schoolBookFonts()
            it.color = Color().also { it.`val` = "000000" }
            it.lang = CTLanguage().also { it.eastAsia="ru-RU" }
        }
        style.pPr = PPr().also { it.ind = PPrBase.Ind().also { it.left = BigInteger.valueOf(720L) } }
        style.isCustomStyle = true

        sdp.jaxbElement.style.add(style)
    }

    private fun initTerm(sdp: StyleDefinitionsPart) {
        val style = Context.getWmlObjectFactory().createStyle()
        style.type = "paragraph"
        style.name = createName(term)
        style.basedOn = createBasedOn("Heading4")
        style.styleId = term
        style.rPr = Context.getWmlObjectFactory().createRPr().also {
            it.b = BooleanDefaultTrue()
            it.i = BooleanDefaultTrue().also { it.isVal = false }
            it.sz = HpsMeasure().also { it.`val` = BigInteger.valueOf(28L) }
            it.rFonts = schoolBookFonts()
            it.color = Color().also { it.`val` = "000000" }
            it.lang = CTLanguage().also { it.eastAsia="ru-RU" }
        }
        style.isCustomStyle = true

        sdp.jaxbElement.style.add(style)
    }

    private fun initSubTermLabel(sdp: StyleDefinitionsPart) {
        val style = Context.getWmlObjectFactory().createStyle()
        style.type = "paragraph"
        style.name = createName(subTermLabel)
        style.basedOn = createBasedOn(description)
        style.styleId = subTermLabel
        style.rPr = Context.getWmlObjectFactory().createRPr().also {
            it.i = BooleanDefaultTrue()
        }
        style.isCustomStyle = true

        sdp.jaxbElement.style.add(style)
    }

    private fun schoolBookFonts(): RFonts {
        return RFonts().also {
            it.ascii = "SchoolBook"
            it.eastAsia = "Times New Roman"
            it.hAnsi = "SchoolBook"
            it.cs = "Calibri"
        }
    }

    private fun initAlphabet(sdp: StyleDefinitionsPart) {
        val style = Context.getWmlObjectFactory().createStyle()
        style.type = "paragraph"
        style.name = createName(alphabet /*+ " Char"*/)
        style.basedOn = createBasedOn("Heading3")
        style.styleId = alphabet
        style.rPr = Context.getWmlObjectFactory().createRPr().also {
            it.b = BooleanDefaultTrue()
            it.sz = HpsMeasure().also { it.`val` = BigInteger.valueOf(36L) }
            it.szCs = HpsMeasure().also { it.`val` = BigInteger.valueOf(28L) }
            it.rFonts = RFonts().also {
                it.ascii = "Academy Condensed"
                it.eastAsia="Times New Roman"
                it.hAnsi="Academy Condensed"
                it.cs="Calibri"
            }
            it.color = Color().also { it.`val` = "000000" }
            it.lang = CTLanguage().also { it.eastAsia="ru-RU" }
        }
        style.isCustomStyle = true

        sdp.jaxbElement.style.add(style)
    }

    private fun createLink(name: String) = Context.getWmlObjectFactory().createStyleLink().also { it.`val` = name }

    private fun createBasedOn(name: String) = Context.getWmlObjectFactory().createStyleBasedOn().also { it.`val` = name }

    private fun createName(name: String) = Context.getWmlObjectFactory().createStyleName().also { it.`val` = name }
}
