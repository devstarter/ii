package org.ayfaar.app.controllers;


import org.ayfaar.app.utils.contents.Contents;
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
    private Contents contents;

    @RequestMapping(value="{name}")
    @ResponseBody
    public List<String> getContents(@PathVariable String categoryName) {
        return contents.createContents(categoryName);
    }
}
