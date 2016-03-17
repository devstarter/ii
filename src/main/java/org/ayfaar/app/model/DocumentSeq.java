package org.ayfaar.app.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
public class DocumentSeq implements Sequence {
    @Id
    @GeneratedValue
    private Integer seq;
}
