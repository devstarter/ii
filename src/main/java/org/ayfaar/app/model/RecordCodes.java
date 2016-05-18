package org.ayfaar.app.model;

import java.util.List;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class RecordCodes {

    String code;
    String name;
    List<String> topicCods;
}
