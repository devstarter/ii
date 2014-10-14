package org.ayfaar.app.controllers;

import org.ayfaar.app.utils.contents.CategoryPresentation;
import org.ayfaar.app.utils.contents.ContentsHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("v2/category")
public class ContentsController {
    @Autowired
    private ContentsHelper contentsHelper;

    @RequestMapping("{name}")
    @ResponseBody
    public List<CategoryPresentation> getContents(@PathVariable("name") String categoryName) {
        return contentsHelper.createContents(categoryName);
    }
}
