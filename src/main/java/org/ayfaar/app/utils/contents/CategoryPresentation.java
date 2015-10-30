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
    private String content;
    private String previous;
    private String next;
    private List<CategoryPresentation> parents;
    private List<CategoryPresentation> children;

    public CategoryPresentation(String name, String uri, String description,  List<CategoryPresentation> children){
        this.name = name;
        this.uri = uri;
        this.description = description;
        this.children = children;
    }

    public CategoryPresentation(String name, String uri, String description, String previous, String next,
                                 List<CategoryPresentation> parents, List<CategoryPresentation> children){

        this(name, uri, description, children);
        this.previous = previous;
        this.next= next;
        this.parents = parents;
    }

    public CategoryPresentation(String name, String uri, String description) {
        this(name, uri, description, null);
    }

    public CategoryPresentation(String name, String uri) {
        this(name, uri, null);
    }
}
