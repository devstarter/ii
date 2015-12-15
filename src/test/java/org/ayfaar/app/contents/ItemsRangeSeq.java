package org.ayfaar.app.contents;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by RuAV on 16.12.2015.
 */
@Entity
@Data
public class ItemsRangeSeq {
    @Id
    @GeneratedValue
    private String seq;
}
