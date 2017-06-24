package org.ayfaar.app.importing;

/*import org.ayfaar.app.controllers.ItemController;
import org.ayfaar.app.model.Item;
import org.docx4j.model.listnumbering.AbstractListNumberingDefinition;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.openpackaging.parts.WordprocessingML.NumberingDefinitionsPart;
import org.docx4j.wml.P;
import org.docx4j.wml.R;
import org.docx4j.wml.Text;

import javax.xml.bind.JAXBElement;
import java.util.HashMap;
import java.util.regex.Matcher;

import static java.util.regex.Pattern.compile;*/

public class ItemsImporterDocx {
    /*private static Item currentItem;

    public static void main(String[] args) throws Docx4JException {
        WordprocessingMLPackage wordMLPackage =
                WordprocessingMLPackage.load(new java.io.File("D:\\projects\\ayfaar\\II\\ayfaar\\Том 10 - оконч с содержанием.docx"));

        MainDocumentPart documentPart = wordMLPackage.getMainDocumentPart();

        currentItem = null;

        NumberingDefinitionsPart numberingDefinitionsPart = documentPart.getNumberingDefinitionsPart();
        HashMap<String,AbstractListNumberingDefinition> listDefinitions = numberingDefinitionsPart.getAbstractListDefinitions();

        for (Object o : documentPart.getContent()) {
            System.out.println(o.toString());

            if (o instanceof P) {
                if (currentItem != null) {
                    createNextItem(ItemController.next(currentItem.getNumber()));
                }
                for (Object r : ((P) o).getContent()) {
                    if (r instanceof R) {
                        for (Object e : ((R) r).getContent()) {
                            if (e instanceof JAXBElement) {
                                Object t = ((JAXBElement) e).getValue();
                                if (t instanceof Text) {
                                    String value = ((Text)t).getValue();
                                    Matcher matcher = compile("(\\d+\\.\\d+)\\.").matcher(value);
                                    if (matcher.find()) {
                                        if (currentItem != null) {
                                            saveItem();
                                        }
                                        createNextItem(matcher.group(1));
                                    } else if (currentItem != null) {
                                        currentItem.setContent(currentItem.getContent() + value);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static void saveItem() {
        currentItem.setContent(currentItem.getContent().trim());
        System.out.print(currentItem.getNumber() + ": ");
        System.out.println(currentItem.getContent());
        // todo: save current Item
    }

    private static void createNextItem(String number) {
        currentItem = new Item(number, "");
    }*/
}

