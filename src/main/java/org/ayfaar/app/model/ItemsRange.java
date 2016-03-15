package org.ayfaar.app.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.ayfaar.app.annotations.Uri;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

import static org.ayfaar.app.model.ItemsRange.NAME_SPACE;

@Entity
@PrimaryKeyJoinColumn(name="uri")
@NoArgsConstructor
@Uri(nameSpace = NAME_SPACE)
@Data
@EqualsAndHashCode(callSuper = true)
public class ItemsRange extends UID {
    public static final String NAME_SPACE = "ии:пункты:";
    public static final Class SEQUENCE = ItemsRangeSeq.class;
//    from: 5.0001
//    to: 5.0002 (определил по следующей строке)
//    code: 5.17.1.1 (том.раздел.глава.параграф)
//    description: "Ииссиидиология не признаётся наукой, которая в свою очередь не может ответить на вопросы о структуре Самосознания. Поэтому представления людей о "своей душе" туманны и надуманы."
//    uri: ии:пункты:5.17.1.1
    @Column(name = "`from`")
    private String from;
    @Column(name = "`to`")
    private String to;
    @Column(unique = true)
    private String code;
    @Column(columnDefinition = "TEXT")
    private String description;

    @Builder
    public ItemsRange(String from, String to, String code, String description) {
        this.from = from;
        this.to = to;
        this.code = code;
        this.description = description;
        if (this.code == null) this.code = this.from + "-" + this.to;
    }

    @Override
    public String toTitle() {
        return description != null && !description.isEmpty() ? description : code;
    }
}
