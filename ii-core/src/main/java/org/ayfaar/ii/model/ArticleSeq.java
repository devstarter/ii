package org.ayfaar.ii.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
public class ArticleSeq {
    @Id
    @GeneratedValue
    private Integer seq;
}
