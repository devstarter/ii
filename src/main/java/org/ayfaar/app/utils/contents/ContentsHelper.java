package org.ayfaar.app.utils.contents;

import org.ayfaar.app.controllers.ItemController;
import org.ayfaar.app.dao.ItemDao;
import org.ayfaar.app.model.Category;
import org.ayfaar.app.model.Item;
import org.ayfaar.app.utils.CategoryService;
import org.ayfaar.app.utils.TermsMarker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static org.ayfaar.app.utils.StringUtils.trim;

@Component
public class ContentsHelper {
    @Autowired ItemDao itemDao;
    @Autowired TermsMarker marker;
    @Autowired CategoryService categoryService;

    private final int SUBCATEGORY_COUNT = 2;

    public CategoryPresentation createContents(String categoryName) {
        CategoryService.CategoryProvider provider = categoryService.getByName(categoryName);
        if (provider == null) {
            throw new RuntimeException(format("Category `%s` not found", categoryName));
        }

        if(provider.isParagraph()) {
            return new CategoryPresentation(provider.getCode().replace(Category.PARAGRAPH_NAME, ""), provider.getUri(),
                    marker.mark(trim(provider.getDescription())), provider.getPreviousUri(), provider.getNextUri(),
                    createParentPresentation(provider.getParents()),
                    getParagraphSubCategory(getItems(provider.getCategory()), 1));
        } else {
            return new CategoryPresentation(extractCategoryName(provider.getCode()), provider.getUri(),
                    marker.mark(trim(provider.getDescription())), provider.getPreviousUri(), provider.getNextUri(),
                    createParentPresentation(provider.getParents()),
                    createChildrenPresentation(provider.getChildren(), 0));
        }
    }

    private List<CategoryPresentation> createChildrenPresentation(List<CategoryService.CategoryProvider> categories, int count) {
        if(count >= SUBCATEGORY_COUNT) return null;

        List<CategoryPresentation> childrenPresentations = new ArrayList<CategoryPresentation>();

        count++;

        for (CategoryService.CategoryProvider category : categories) {
            if (!category.isParagraph()) {
                childrenPresentations.add(new CategoryPresentation(
                        extractCategoryName(category.getCode()), category.getUri(), category.getDescription(),
                        createChildrenPresentation(category.getChildren(), count)));

            } else {
                childrenPresentations.add(new CategoryPresentation(category.getCode().replace(Category.PARAGRAPH_NAME, ""),
                        category.getUri(), trim(category.getDescription()), null));
            }
        }

        return childrenPresentations;
    }

    private List<CategoryPresentation> getParagraphSubCategory(List<Item> items, int count) {
        if(count >= SUBCATEGORY_COUNT) return null;
        List<CategoryPresentation> listPresentations = new ArrayList<CategoryPresentation>();
        for (Item item : items) {
            CategoryPresentation presentation = new CategoryPresentation(item.getNumber(), item.getUri());
            presentation.setContent(item.getTaggedContent());

            listPresentations.add(presentation);
        }
        return listPresentations;
    }

    List<Item> getItems(Category category) {
        List<Item> items = new ArrayList<Item>();
        Item currentItem = itemDao.get(category.getStart());

        String itemNumber = currentItem.getNumber();
        String endNumber = category.getEnd() != null ? itemDao.get(category.getEnd()).getNumber() : itemNumber;

        items.add(currentItem);
        while(!itemNumber.equals(endNumber)) {
            itemNumber = ItemController.getNext(itemNumber);
            currentItem = itemDao.getByNumber(itemNumber);
            items.add(currentItem);
        }
		if (itemNumber.startsWith("1.") || itemNumber.startsWith("2.") || itemNumber.startsWith("3.")) items.remove(items.size()-1);
        return items;
    }

    String extractCategoryName(String categoryName) {
        String[] names = categoryName.split("/");
        return names[names.length-1].trim();
    }

    /**
     * For parents presentations we need only name, uri and description. children and parents should be null
     *
     * @param categories
     * @return
     */
    List<CategoryPresentation> createParentPresentation(List<CategoryService.CategoryProvider> categories) {
        List<CategoryPresentation> presentations = new ArrayList<CategoryPresentation>();

        for(CategoryService.CategoryProvider category : categories) {
            presentations.add(new CategoryPresentation(extractCategoryName(category.getCode()),
                    category.getUri(), trim(category.getDescription())));
        }
        return presentations;
    }
}
