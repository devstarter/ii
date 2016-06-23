package org.ayfaar.app.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.IdClass;

@Entity
@Getter
@Setter
@NoArgsConstructor
@IdClass(TermRecordKey.class)
public class TermRecordFrequency {

    @Id
    private String term;
    @Id
    private String record;
    private int frequency;
}
