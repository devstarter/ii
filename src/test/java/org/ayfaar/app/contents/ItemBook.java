package org.ayfaar.app.contents;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class ItemBook {
    private String categoryNumber;//номер категории - тома, раздела, главы или параграфа
    private String title; //название категории
    private String itemNumber; //номер абзаца, будет не null только для параграфа
    SectionType type;
    private ItemBook prev;
    private ItemBook parent;
    private ItemBook start;

    @Override
    public String toString(){
         return("Тип категории " + type + " номер: " + categoryNumber
                 + " название: " + title
                 +  " номер абзаца: " + itemNumber);
    }

    public void setStartIfEmpty(ItemBook item) {
        if (start == null) start = item;
    }
}

