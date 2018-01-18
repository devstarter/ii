package org.ayfaar.app.model;

import lombok.Getter;
import lombok.Setter;
import org.ayfaar.app.annotations.Uri;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import java.util.Date;

import static java.lang.Float.parseFloat;
import static org.ayfaar.app.model.Item.NS;

@Entity
@PrimaryKeyJoinColumn(name="uri")
@Setter @Getter
@Uri(nameSpace = NS, field = "number")
public class Item extends UID {
    public static final String NS = "ии:пункт:";

    @Column(unique = true, nullable = false)
    private String number;
    @Column(columnDefinition = "TEXT")
    private String content;
    @Column(columnDefinition = "MEDIUMTEXT")
    private String taggedContent;
//    @Column(columnDefinition = "TEXT")
//    private String wiki;
    private String next; // uri
    // field for optimization order operation on database
    private Float orderIndex;
    private Date updatesAt;

    public Item(String number, String content) {
        this(number);
        this.content = content;
    }

    public Item(String number) {
        this.number = number;
        orderIndex = parseFloat(number);
    }

    public Item() {
    }

    public static boolean isItemNumber(String s) {
        return s.matches("^\\d\\d?\\.\\d{4}\\d?$");
    }

    @Override
    public String toTitle() {
        return number;
    }

    /*
    order index sql:
    ALTER TABLE `item` ADD COLUMN `order_index` DECIMAL(10,5) NULL DEFAULT NULL AFTER `next`;
	update item set order_index = cast(number as decimal(10, 5));
	ALTER TABLE `item` ADD INDEX `order_index` (`order_index`);
     */
}
