package org.ayfaar.app.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Data
public class SyncStatus {
    @Id @GeneratedValue
    private Integer id;
    @Column(unique = true, nullable = false)
    private String articleName;
    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String articleContent;
    private Boolean synchronised;
    private Date syncDate;
}
