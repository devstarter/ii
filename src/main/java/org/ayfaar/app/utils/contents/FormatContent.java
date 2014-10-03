package org.ayfaar.app.utils.contents;

import org.ayfaar.app.model.Category;
import org.ayfaar.app.model.Item;
import org.ayfaar.app.model.Term;


public class FormatContent {

    public Category formatCategory(Category category) {

        if(category.isParagraph()) {
            //decorate children name and description
            formatParagraph(category);
        }
        else if(category.isTom()) {
            //decorate
            formatTom(category);
        }


        return  new Category();
    }


    public Category formatParagraph(Category category) {
        return new Category();
    }

    public Category formatTom(Category category) {
        return new Category();
    }

    public Item formatItemContent(Item item) {
        //item.getNumber() делаем ссылкой

        //formatTerm();

        return new Item();
    }

    public Term formatTerm(Term term) {
        //term делаем ссылкой
        return new Term();
    }
}
