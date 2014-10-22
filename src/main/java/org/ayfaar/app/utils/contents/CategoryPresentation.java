package org.ayfaar.app.utils.contents;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Data
@NoArgsConstructor
public class CategoryPresentation {
    private String name;
    private String uri;
    private String description;
    private List<CategoryPresentation> parents;
    private List<CategoryPresentation> children;

    public CategoryPresentation(String name, String uri, String description,  List<CategoryPresentation> children){
        this.name = name;
        this.uri = uri;
        this.description = description;
        this.children = children;
    }

    public CategoryPresentation(String name, String uri, String description, List<CategoryPresentation> parents,
                                                         List<CategoryPresentation> children){
        this(name, uri, description, children);
        this.parents = parents;
    }
}
