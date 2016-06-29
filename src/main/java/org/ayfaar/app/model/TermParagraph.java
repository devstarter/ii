package org.ayfaar.app.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

@Entity
@Getter
@Setter
@NoArgsConstructor
@IdClass(TermParagraphKey.class)
public class TermParagraph {
    @Id
    private String term;
    @Id
    private String paragraph;

}
