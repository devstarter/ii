package org.ayfaar.app.contents;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *  Этот класс - базовый для классов, которыми заполняем БД.
 *  Это набросок для того, что будет сделано в дальнейшем.
 */
@Data
@NoArgsConstructor
public class ItemBook {
    private String description;
    private String name;
    private String code;
    private String uri;

    TypeSection type;
    private boolean cikl;

//    том.раздел.глава.параграф
    private String tom;
    private String razdel;
    private String glava;
    private String paragraf;

    public ItemBook(String description) {
        this.description = description;
    }

    public enum TypeSection {
        Chapter, Paragraph, Section, Root
    }
}

