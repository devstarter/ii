package org.ayfaar.app.utils;


import org.ayfaar.app.dao.CategoryDao;
import org.ayfaar.app.model.Category;
import org.ayfaar.app.model.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.ayfaar.app.utils.UriGenerator.getValueFromUri;

@Component
public class CategoryMapImpl implements CategoryMap {
    @Autowired
    private CategoryDao categoryDao;

    private Map<String, CategoryProvider> categoryMap;
    private List<Paragraph> paragraphs;

    @PostConstruct
    public void load() {
        categoryMap = new HashMap<String, CategoryProvider>();
        paragraphs = new ArrayList<Paragraph>();

        List<Category> categories = categoryDao.getAll();
        for(Category category : categories) {
            CategoryProvider provider = new CategoryProviderImpl(category);
            categoryMap.put(category.getName(), provider);
            if(provider.isParagraph()) {
                paragraphs.add(new Paragraph(category.getName(),
                        convertItemNumber(category.getStart()), convertItemNumber(category.getEnd())));
            }
        }
    }

    private class Paragraph {
        private String name;
        private double start;
        private double end;

        private Paragraph(String name, double start, double end) {
            this.name = name;
            this.start = start;
            this.end = end;
        }
    }

    @Override
    public CategoryProvider getProviderForCategoryName(String name) {
        return categoryMap.get(name);
    }

    @Override
    public CategoryProvider getProviderByItemNumber(String number) {
        CategoryProvider provider = null;
        double itemNumber = convertItemNumber(UriGenerator.generate(Item.class, number));

        for(Paragraph p : paragraphs) {
            if(itemNumber >= p.start && itemNumber <= p.end) {
                provider = categoryMap.get(p.name);
            } else if(p.end == 0.0){
                if(itemNumber == p.start) {
                    provider = categoryMap.get(p.name);
                }
            }
        }
        return provider;
    }

    @Override
    public Category getCategory(String name) {
        return getProviderForCategoryName(name).getCategory();
    }

    @Override
    public List<CategoryProvider> descriptionContains(List<String> searchQueries) {
        ListIterator<String> iterator = searchQueries.listIterator();

        String regexp = "";
        while (iterator.hasNext()) {
            String q = iterator.next();
            regexp += "(^" + q + RegExpUtils.W + "+)|(" + RegExpUtils.W + "+" + q + RegExpUtils.W + "+)|(" + RegExpUtils.W + "+" + q + "$)";
            if (iterator.hasNext()) regexp += "|";
        }

        Pattern pattern = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        List<CategoryProvider> foundCategories = new ArrayList<CategoryProvider>();
        for (CategoryProvider provider : categoryMap.values()) {
			if (provider.getDescription() == null || provider.getDescription().isEmpty()) continue;
            Matcher matcher = pattern.matcher(provider.getDescription());
            if (matcher.find()) {
                foundCategories.add(provider);
            }
        }

        return foundCategories;
    }

    public class CategoryProviderImpl implements CategoryProvider {
        private Category category;

        public CategoryProviderImpl(Category category) {
            this.category = category;
        }

        @Override
        public Category getCategory() {
            return category;
        }

        @Override
        public String getUri() {
            return category.getUri();
        }

        @Override
        public String getParentUri() {
            return category.getParent();
        }

        @Override
        public String getDescription() {
            return category.getDescription();
        }

        @Override
        public CategoryProvider getNext() {
            String next = category.getNext();
            return next != null ? categoryMap.get(getValueFromUri(Category.class, next)) : null;
        }

        @Override
        public boolean isParagraph() {
            return category.getName().indexOf(Category.PARAGRAPH_NAME) == 0;
        }

        @Override
        public boolean isTom() {
            return category.getName().indexOf(Category.TOM_NAME) == 0;
        }

        @Override
        public boolean isCikl() {
            return category.getName().equals("БДК") || category.getName().equals("Основы");
        }

		@Override
		public boolean isContentsRoot() {
			return category.getName().equals("Содержание");
		}

		@Override
        public List<CategoryProvider> getChildren() {
            List<CategoryProvider> children = new ArrayList<CategoryProvider>();
            if (category.getStart() != null) {
                CategoryProvider child = categoryMap.get(getValueFromUri(Category.class, category.getStart()));
                if (child != null) {
                    children.add(child);
                    while (child.getNext() != null) {
                        child = child.getNext();
                        if (!child.getParentUri().equals(getUri()))
                            break;
                        children.add(child);
                    }
                }
            }
            return children;
        }

        @Override
        public CategoryProvider getParent() {
            return getParentUri() != null ? categoryMap.get(getValueFromUri(Category.class, getParentUri())) : null;
        }

        @Override
        public List<CategoryProvider> getParents() {
               return getParents(getValueFromUri(Category.class, getUri()));
        }

        private List<CategoryProvider> getParents(String name) {
            List<CategoryProvider> parents = new ArrayList<CategoryProvider>();
            CategoryProvider parent = categoryMap.get(name).getParent();

            if(parent != null) {
                parents.add(parent);
                parents.addAll(getParents(getValueFromUri(Category.class, parent.getUri())));
            }
            return parents;
        }

        @Override
        public String extractCategoryName() {
            String[] split = getUri().split("/|:");
            return split[split.length-1].trim();
        }

		@Override
		public String getPath() {
			String path = "";
			List<CategoryProvider> chain = new ArrayList<CategoryProvider>(getParents());
			chain.add(0, this);
			ListIterator<CategoryProvider> iterator = chain.listIterator(chain.size());
			while (iterator.hasPrevious()) {
				CategoryProvider provider = iterator.previous();
				if (provider.isContentsRoot() || provider.isCikl()) continue;
				path += provider.extractCategoryName();
				if (iterator.hasPrevious()) path += " / ";
			}
			return path;
		}
	}

    private double convertItemNumber(String value) {
        return value != null ? Double.parseDouble(getValueFromUri(Item.class, value)) : 0.0;
    }
}

