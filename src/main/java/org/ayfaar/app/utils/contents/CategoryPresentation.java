package org.ayfaar.app.utils.contents;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Data
public class CategoryPresentation {
    private String name;
    private String description;
    private List<CategoryPresentation> children;

    public CategoryPresentation(){}

    public CategoryPresentation(String name, String description, List<CategoryPresentation> children){
        this.name = name;
        this.description = description;
        this.children = children;
    }
}
