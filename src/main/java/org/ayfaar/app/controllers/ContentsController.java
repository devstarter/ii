package org.ayfaar.app.controllers;

import org.ayfaar.app.model.Category;
import org.ayfaar.app.utils.UriGenerator;
import org.ayfaar.app.utils.contents.CategoryPresentation;
import org.ayfaar.app.utils.contents.ContentsHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("category")
public class ContentsController {
    @Autowired
    private ContentsHelper contentsHelper;

    @RequestMapping("{name}")
    @ResponseBody
    public List<CategoryPresentation> getContents(@PathVariable String name) {
        name = UriGenerator.getValueFromUri(Category.class, name); // на случай если будет передан uri вместо имени
        return contentsHelper.createContents(name);
    }
}
