package org.ayfaar.app.controllers;

import org.ayfaar.app.dao.LinkDao;
import org.ayfaar.app.model.Link;
import org.ayfaar.app.spring.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("item")
public class LinkController {
    @Autowired LinkDao linkDao;

    @RequestMapping("{uid1}/{uid2}")
    @Model
    public Link link(@PathVariable String uid1, @PathVariable String uid2) {
        return null;
    }
}
