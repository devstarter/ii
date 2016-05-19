package org.ayfaar.app.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.ayfaar.app.annotations.Uri;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@PrimaryKeyJoinColumn(name="uri")
@Uri(nameSpace = "record:", field = "code")
public class Record extends UID{

    private String code;
    private String name;
    private String recorderAt;
    private Date createdAt;

    @Override
    public String toTitle() {
        return name;
    }
}
