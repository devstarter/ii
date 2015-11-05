package org.ayfaar.app.controllers;

import org.ayfaar.app.annotations.ContentsCache;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.dao.ItemDao;
import org.ayfaar.app.dao.LinkDao;
import org.ayfaar.app.dao.TermDao;
import org.ayfaar.app.model.Category;
import org.ayfaar.app.utils.CategoryService;
import org.ayfaar.app.utils.UriGenerator;
import org.ayfaar.app.utils.contents.ContentsHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.util.Assert.hasLength;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("api/category")
public class CategoryController {
    @Autowired CommonDao commonDao;
    @Autowired TermDao termDao;
    @Autowired LinkDao linkDao;
    @Autowired ItemDao itemDao;
    @Autowired ContentsHelper contentsHelper;
    @Autowired
    CategoryService categoryService;

    @ContentsCache //fixme временно пока идёт активная работа с ним
    @RequestMapping
    @ResponseBody
    public Object getContents(@RequestParam("name") String name) {
        name = UriGenerator.getValueFromUri(Category.class, name);
        return contentsHelper.createContents(name);
    }

    @RequestMapping(value = "add", method = POST)
    @ResponseBody
    public String add(@RequestParam("category") String categoryName,
                    @RequestParam("parent") String parentCategoryName,
                    @RequestParam("from") String fromItem,
                    @RequestParam("to") String toItem) {
        hasLength(categoryName);
//        hasLength(fromItem);
//        hasLength(toItem);
        Category parent = commonDao.get(Category.class, "name", parentCategoryName);
        Category category = new Category(categoryName, parent.getUri());

        category.setStart(itemDao.getByNumber(fromItem).getUri());
        category.setEnd(itemDao.getByNumber(toItem).getUri());

        commonDao.save(category);

        return category.getUri();
    }

    @RequestMapping("autocomplete")
    @ResponseBody
    public List<String> autoComplete(@RequestParam("filter[filters][0][value]") String filter) {
        List<Category> categories = commonDao.getLike(Category.class, "name", filter + "%", 20);
        List<String> names = new ArrayList<String>();
        for (Category category : categories) {
            names.add(category.getName());
        }
        return names;
    }

    @RequestMapping("reload")
    public void reload() {
        categoryService.reload();
    }
}
