package org.ayfaar.app.utils;


import org.ayfaar.app.dao.CategoryDao;
import org.ayfaar.app.model.Category;
import org.ayfaar.app.model.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.ayfaar.app.utils.UriGenerator.getValueFromUri;

@Component
public class CategoryServiceImpl implements CategoryService {
    private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);

    @Autowired
    private CategoryDao categoryDao;

    private Map<String, CategoryProvider> categoryMap;
    private List<Paragraph> paragraphs;

    @PostConstruct
    public void load() {
        logger.info("Category map loading...");
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
        logger.info("Category map loading finish");
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
    public CategoryProvider getByName(String name) {
        return categoryMap.get(name);
    }

    @Override
    public CategoryProvider getByItemNumber(String number) {
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
    public List<CategoryProvider> descriptionContains(List<String> searchQueries) {
        ListIterator<String> iterator = searchQueries.listIterator();

        String regexp = "";
        while (iterator.hasNext()) {
            String q = iterator.next();
            regexp += RegExpUtils.buildWordContainsRegExp(q);
            if (iterator.hasNext()) regexp += "|";
        }

//        final Map<CategoryProvider, Integer> rateMap = new HashMap<CategoryProvider, Integer>();

        Pattern pattern = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        List<CategoryProvider> foundCategories = new ArrayList<CategoryProvider>();
        for (CategoryProvider provider : categoryMap.values()) {
			if (provider.getDescription() == null || provider.getDescription().isEmpty()) continue;
            Matcher matcher = pattern.matcher(provider.getDescription());
            if (matcher.find()) {
                foundCategories.add(provider);
//                rateMap.put(provider, matcher.start());
            }
        }

        Collections.sort(foundCategories, new Comparator<CategoryProvider>() {
            @Override
            public int compare(CategoryProvider p1, CategoryProvider p2) {
				if (p1.getStartItemNumber() == null || p2.getStartItemNumber() == null) return 0;
				return Double.compare(Double.valueOf(p1.getStartItemNumber()), Double.valueOf(p2.getStartItemNumber()));
//                return rateMap.get(p1).compareTo(rateMap.get(p2));
            }
        });

        return foundCategories;
    }

	@Override
	public void reload() {
		load();
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
            String nextUri = getNextUri();
            return nextUri != null ? getByUri(nextUri) : null;
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
				if (provider.isParagraph()) path += "§";
				path += provider.extractCategoryName();
				if (iterator.hasPrevious()) path += " / ";
			}
			return path;
		}

		@Override
		public String getCode() {
			return category.getName();
		}

		@Override
		public String getPreviousUri() {
			for (CategoryProvider provider : categoryMap.values())
				if (category.getUri().equals(provider.getCategory().getNext())) return provider.getUri();
			return null;
		}

		@Override
		public String getNextUri() {
			return category.getNext();
		}

		@Override
		public String getStartItemNumber() {
			if (category.isParagraph()) return UriGenerator.getValueFromUri(Item.class, category.getStart());
			return getStartItemNumberOfChildren(getChildren());
		}

		private String getStartItemNumberOfChildren(List<CategoryProvider> categories) {
			if (categories.isEmpty()) return null;
			CategoryProvider firstCat = categories.get(0);
			if (firstCat.isParagraph()) return firstCat.getStartItemNumber();
			return getStartItemNumberOfChildren(firstCat.getChildren());
		}

		@Override
		public CategoryProvider getPrevious() {
			return getByUri(getPreviousUri());
		}
	}

	@Override
	public CategoryProvider getByUri(String uri) {
		if (uri == null) return null;
		return categoryMap.get(UriGenerator.getValueFromUri(Category.class, uri));
	}

	private double convertItemNumber(String value) {
        return value != null ? Double.parseDouble(getValueFromUri(Item.class, value)) : 0.0;
    }

    @Override
    public Map<String, CategoryProvider> getAll(){
        return categoryMap;
    }
}

