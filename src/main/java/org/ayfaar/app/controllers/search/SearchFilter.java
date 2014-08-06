package org.ayfaar.app.controllers.search;

import lombok.Data;

@Data
public class SearchFilter {
    private String fromItem;
    // так как мы сортируем пункты по возрастанию, в конечном пункте нет необходимости, как и в этом класе
    // fixme удалить этот клас заменив в NewSearchController на String fromItemNumber
    //private String toItem;
}
