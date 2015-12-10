package org.ayfaar.app.model;

import lombok.NoArgsConstructor;
import org.ayfaar.app.annotations.Uri;

import javax.persistence.*;

@Entity
@PrimaryKeyJoinColumn(name="uri")
@NoArgsConstructor
@Uri(nameSpace = "ии:пункт:диапазон:")
public class ItemsRange extends UID {

    public static final Class SEQUENCE = ItemsRangeSeq.class;
//    first: 5.0001
//    last: 5.0002 (определил по следующей строке)
//    code: 5.17.1.1 (том.раздел.глава.параграф)
//    description: "Ииссиидиология не признаётся наукой, которая в свою очередь не может ответить на вопросы о структуре Самосознания. Поэтому представления людей о "своей душе" туманны и надуманы."
//    uri: ии:пункт:диапазон:5.17.1.1

    private String first;
    private String last;
    @Column(unique = true)
    private String code;
    @Column(columnDefinition = "TEXT")
    private String description;
//    private String uri;

    public ItemsRange(String first, String last, String code, String description) {
        this.first = first;
        this.last = last;
        this.code = code;
        this.description = description;
    }
}
