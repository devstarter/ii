package org.ayfaar.app.controllers;

import org.ayfaar.app.model.Category;
import org.ayfaar.app.utils.contents.Contents;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("v2/contents")
public class ContentsController {
    @Autowired
    private Contents contents;

    @RequestMapping(value="{category}")
    @ResponseBody
    public List<String> getContents(@PathVariable Category category) {
        return contents.createContents(category);
    }
}
