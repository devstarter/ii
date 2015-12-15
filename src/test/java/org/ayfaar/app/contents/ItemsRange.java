package org.ayfaar.app.contents;

import org.ayfaar.app.model.UID;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
public class ItemsRange extends UID {

//    first: 5.0001
//    last: 5.0002 (определил по следующей строке)
//    code: 5.17.1.1 (том.раздел.глава.параграф)
//    description: "Ииссиидиология не признаётся наукой, которая в свою очередь не может ответить на вопросы о структуре Самосознания. Поэтому представления людей о "своей душе" туманны и надуманы."
//    uri: ии:пункт:диапазон:5.17.1.1

    private String first;
    private String last;

    private String code;
    private String description;
    private String uri;

    public ItemsRange(String first, String last, String description, String uri) {
        this.first = first;
        this.last = last;
        this.description = description;
        this.uri = uri;
    }
}
