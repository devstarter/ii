package org.ayfaar.app.contents;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by Nayil on 05.03.2016.
 */
@Data
@NoArgsConstructor
public class ItemBookRes {
    private String categoryNumber;//номер категории - тома, раздела, главы или параграфа
    private String title; //название категории
    private String itemNumber; //номер абзаца, будет не null только для параграфа
    TypeSection type;

    @Override
    public String toString(){
         return("Тип категории " + type + " номер: " + categoryNumber
                 + " название: " + title
                 +  " номер абзаца: " + itemNumber);
    }
}

