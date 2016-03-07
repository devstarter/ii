package org.ayfaar.app.controllers;

import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("api/resource")
public class ResourcesController {

    @Autowired CommonDao commonDao;

    @RequestMapping("{id}")
    @ResponseBody
    public Resource get(@PathVariable String id) throws Exception {
        return null;
    }

    @RequestMapping(method = POST)
    @ResponseBody
    public Resource add(@RequestParam String url) throws Exception {
        return null;
    }
}
