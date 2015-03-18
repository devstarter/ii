package org.ayfaar.app.controllers;

import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.dao.ItemDao;
import org.ayfaar.app.dao.TermDao;
import org.ayfaar.app.model.Item;
import org.ayfaar.app.model.Link;
import org.ayfaar.app.model.Term;
import org.ayfaar.app.spring.Model;
import org.ayfaar.app.utils.CategoryMap;
import org.ayfaar.app.utils.TermsMapImpl;
import org.ayfaar.app.utils.TermsMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;


import static org.ayfaar.app.utils.ValueObjectUtils.getModelMap;
import static org.springframework.util.Assert.notNull;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("api/item")
public class ItemController {

    @Autowired CommonDao commonDao;
    @Autowired ItemDao itemDao;
    @Autowired TermDao termDao;
    @Autowired TermController termController;
    @Autowired TermsMap termsMap;
    @Autowired TermsMapImpl aliasesMap;
    @Autowired CategoryMap categoryMap;

    @RequestMapping(value = "{number}", method = POST)
    @Model
    public Item add(@PathVariable String number, @RequestBody String content) {
        Item item = itemDao.getByNumber(number);
        if (item == null) {
            item = new Item(number);
        }
        item.setContent(content);
        itemDao.save(item);
        return item;
    }

    @RequestMapping("{number}")
    @Model
    public ModelMap get(@PathVariable String number) {
        Item item = itemDao.getByNumber(number);
        notNull(item, "Item not found");
        ModelMap modelMap = (ModelMap) getModelMap(item);
//        modelMap.put("linkedTerms", getLinkedTerms(item));
        Item next = itemDao.get(item.getNext());
        if (next !=  null) {
            ModelMap nextMap = new ModelMap();
            nextMap.put("uri", next.getUri());
            nextMap.put("number", next.getNumber());
            modelMap.put("next", nextMap);
        }

        Item prev = itemDao.getByNumber(getPrev(item.getNumber()));
        if (prev != null) {
            ModelMap prevMap = new ModelMap();
            prevMap.put("uri", prev.getUri());
            prevMap.put("number", prev.getNumber());
            modelMap.put("prev", prevMap);
        }
        return modelMap;
    }

    public static String getPrev(String number) {
        String[] split = number.split("\\.");
        return split[0] + "." + formatNumber(Integer.valueOf(split[1]) - 1, split[1].length());
    }

    public static String getNext(String number) {
        String[] split = number.split("\\.");
        return split[0] + "." + formatNumber(Integer.valueOf(split[1]) + 1, split[1].length());
    }

    public static String formatNumber(int number, int length) {
        String formattedNumber = String.valueOf(number);
        while (length > formattedNumber.length()) {
            formattedNumber = "0"+formattedNumber;
        }
        return formattedNumber;
    }

    @RequestMapping("{number}/linked-terms")
    @Model
    @ResponseBody
    public Object getLinkedTerms(@PathVariable String number) {
        Item item = itemDao.getByNumber(number);
        notNull(item, "Пункт не найден");

        return aliasesMap.findTermsInside(item.getContent());
    }

    @RequestMapping("{number}/{term}")
    @Model
    public Link assignToTerm(@PathVariable String number, @PathVariable String term) {
        return assignToTermWithWeight(number, term, null);
    }

    @RequestMapping("{number}/{term}/{weight}")
    @Model
    public Link assignToTermWithWeight(@PathVariable String number,
                                       @PathVariable String term,
                                       @PathVariable Byte weight) {
        Item item = commonDao.get(Item.class, "number", number);
        if (item == null) {
            item = commonDao.save(new Item(number));
        }

        Term termObj = termDao.getByName(term);
        if (termObj == null) {
            termObj = termController.add(term, null);
        }

        return commonDao.save(new Link(item, termObj, weight));
    }
}
