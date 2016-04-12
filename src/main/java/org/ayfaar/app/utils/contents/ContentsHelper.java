package org.ayfaar.app.utils.contents;

import org.ayfaar.app.controllers.ItemController;
import org.ayfaar.app.dao.ItemDao;
import org.ayfaar.app.model.Item;
import org.ayfaar.app.utils.ContentsService;
import org.ayfaar.app.utils.ContentsService.CategoryProvider;
import org.ayfaar.app.utils.ContentsService.ParagraphProvider;
import org.ayfaar.app.utils.TermsMarker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.lang.String.format;
import static org.ayfaar.app.utils.StringUtils.trim;

@Component
public class ContentsHelper {
    @Autowired ItemDao itemDao;
    @Autowired TermsMarker marker;
    @Autowired
    ContentsService contentsService;

    private final int SUBCATEGORY_COUNT = 2;

    public CategoryPresentation createContents(String categoryName) {
        Optional<? extends ContentsService.ContentsProvider> providerOpt = contentsService.get(categoryName);
        if (!providerOpt.isPresent()) {
            throw new RuntimeException(format("Category `%s` not found", categoryName));
        }

        if(providerOpt.get() instanceof ParagraphProvider) {
            ParagraphProvider p = (ParagraphProvider) providerOpt.get();
            return new CategoryPresentation(p.code(), p.uri(),
                    marker.mark(trim(p.name())), p.previousUri(), p.nextUri(),
                    createParentPresentation(p.parents()),
                    getParagraphSubCategory(getItems(p), 1));
        } else {
            CategoryProvider c = (CategoryProvider) providerOpt.get();
            return new CategoryPresentation(extractCategoryName(c.code()), c.uri(),
                    marker.mark(trim(c.description())), c.previousUri(), c.nextUri(),
                    createParentPresentation(c.parents()),
                    createChildrenPresentation(c.getChildren(), 0));
        }
    }

    private List<CategoryPresentation> createChildrenPresentation(List<? extends ContentsService.ContentsProvider> categories, int count) {
        if(count >= SUBCATEGORY_COUNT) return null;

        List<CategoryPresentation> childrenPresentations = new ArrayList<CategoryPresentation>();

        count++;

        for (ContentsService.ContentsProvider category : categories) {
            if (category instanceof CategoryProvider) {
                childrenPresentations.add(new CategoryPresentation(
                        extractCategoryName(category.code()), category.uri(), category.description(),
                        createChildrenPresentation(((CategoryProvider) category).getChildren(), count)));

            } else if (count < 2) {
                childrenPresentations.add(new CategoryPresentation(category.code(),
                        category.uri(), trim(category.name()), null));
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

    List<Item> getItems(ParagraphProvider p) {
        List<Item> items = new ArrayList<>();
        Item currentItem = itemDao.getByNumber(p.from());

        String itemNumber = currentItem.getNumber();
        String endNumber = p.to();

        items.add(currentItem);
        while(!itemNumber.equals(endNumber)) {
            itemNumber = ItemController.getNext(itemNumber);
            currentItem = itemDao.getByNumber(itemNumber);
            items.add(currentItem);
        }
//		if (itemNumber.startsWith("1.") || itemNumber.startsWith("2.") || itemNumber.startsWith("3.")) items.remove(items.size()-1);
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
    List<CategoryPresentation> createParentPresentation(List<? extends CategoryProvider> categories) {
        List<CategoryPresentation> presentations = new ArrayList<CategoryPresentation>();

        for(CategoryProvider category : categories) {
            presentations.add(new CategoryPresentation(extractCategoryName(category.code()),
                    category.uri(), trim(category.description())));
        }
        return presentations;
    }
}
