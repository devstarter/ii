package org.ayfaar.app.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class TermRecordFrequency {
    @Id
    @GeneratedValue
    private Integer id;
    private String term;
    private String record;
    private int frequency;
}
