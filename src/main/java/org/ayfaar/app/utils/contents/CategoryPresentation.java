package org.ayfaar.app.utils.contents;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

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
    private String from;
    private String to;
    private List<CategoryPresentation> parents;
    private List<CategoryPresentation> children;

    public CategoryPresentation(String name, String uri, String description,  List<CategoryPresentation> children){
        this.name = name;
        this.uri = uri;
        this.description = description;
        this.children = children;
    }

    public CategoryPresentation(String name, String uri, String description, Optional<String> previous, Optional<String> next,
                                List<CategoryPresentation> parents, List<CategoryPresentation> children){

        this(name, uri, description, children);
        this.previous = previous.isPresent() ? previous.get() : null;
        this.next= next.isPresent() ? next.get() : null;
        this.parents = parents;
    }

    public CategoryPresentation(String name, String uri, String description, Optional<String> previous, Optional<String> next,
                                List<CategoryPresentation> parents, List<CategoryPresentation> children, String from, String to){

        this(name, uri, description, previous, next, parents, children);
        this.from = from;
        this.to = to;
    }

    public CategoryPresentation(String name, String uri, String description) {
        this(name, uri, description, null);
    }

    public CategoryPresentation(String name, String uri) {
        this(name, uri, null);
    }

    public CategoryPresentation(String paragraphCode, String uri, String description, String from, String to) {
        this(paragraphCode, uri, description, null);
        this.from = from;
        this.to = to;
    }
}
