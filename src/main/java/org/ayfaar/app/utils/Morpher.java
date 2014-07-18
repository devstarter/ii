package org.ayfaar.app.utils;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author leonid
 */
public class Morpher {
    private Document doc = null;
    private String word;

    public Morpher(String word) throws IOException, ParserConfigurationException, SAXException {
        this.word = word;
    }

    public boolean getData() throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        doc = builder.parse("http://morpher.ru/WebService.asmx/GetXml?s="+word.replace(" ", "%20"));
        Element root = doc.getDocumentElement();
        return !root.getNodeName().equals("error");
    }

    public Set<Morph> getAllMorph() {
        Set<Morph> list = new HashSet<Morph>();
        list.add(getMorph("Р"));
        list.add(getMorph("Д"));
        list.add(getMorph("В"));
        list.add(getMorph("Т"));
        list.add(getMorph("П"));
        list.add(getMorph("И", true));
        list.add(getMorph("Р", true));
        list.add(getMorph("Д", true));
        list.add(getMorph("В", true));
        list.add(getMorph("Т", true));
        list.add(getMorph("П", true));
        return list;
    }
    public Morph getMorph(String p) {
        return getMorph(p, false);
    }
    public Morph getMorph(String p, boolean multiple) {
        String result = "";
        if (null != doc) {
            Element root = doc.getDocumentElement();
            if (root.getNodeName().equals("error")) return null;
            NodeList nodes = root.getChildNodes();
            if (multiple) {
                NodeList multipleNode = root.getElementsByTagName("множественное");
                if (multipleNode.getLength() > 0) {
                    nodes = multipleNode.item(0).getChildNodes();
                } else {
                    return null;
                }
            }
            for (int x = 0; x < nodes.getLength(); x++) {
                Node item = nodes.item(x);
                if (item instanceof Element) {
                    Element el = ((Element)item);
                    if (el.getTagName().equals(p)) {
                        result = ((Text)el.getFirstChild()).getData().trim();
                    }
                }
            }
        }
        return new Morph(result, p, multiple);
    }

    public class Morph {
        public final String text;
        public final String mode;
        public final boolean multiple;

        public Morph(String text, String mode, boolean multiple) {
            this.text = text;
            this.mode = mode;
            this.multiple = multiple;
        }
    }
}