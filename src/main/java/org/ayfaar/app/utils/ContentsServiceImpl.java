package org.ayfaar.app.utils;


import lombok.Getter;
import lombok.experimental.Accessors;
import one.util.streamex.StreamEx;
import org.ayfaar.app.dao.CategoryDao;
import org.ayfaar.app.model.Category;
import org.ayfaar.app.model.Item;
import org.ayfaar.app.model.ItemsRange;
import org.ayfaar.app.services.itemRange.ItemRangeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.ayfaar.app.utils.UriGenerator.getValueFromUri;

@Component
public class ContentsServiceImpl implements ContentsService {
    private static final Logger logger = LoggerFactory.getLogger(ContentsService.class);

    @Inject CategoryDao categoryDao;
    @Inject ItemRangeService itemRangeService;

    private Map<String, CategoryProvider> categoryMap;
    private Map<String, ? extends ParagraphProvider> paragraphMap;
//    private List<Paragraph> paragraphs;

    @PostConstruct
    public void load() {
        logger.info("Category map loading...");
        categoryMap = new HashMap<>();
        paragraphMap = new HashMap<>();

        List<Category> categories = categoryDao.getAll();
        for(Category category : categories) {
            CategoryProvider provider = new CategoryProviderImpl(category);
            categoryMap.put(category.getName(), provider);
        }
        paragraphMap = StreamEx.of(itemRangeService.getWithCategories())
                .sortedBy(ItemsRange::getFrom)
                .toMap(ItemsRange::getCode, Paragraph::new);
        logger.info("Category map loading finish");
    }

    @Getter @Accessors(chain = true)
    public class Paragraph implements ParagraphProvider {
        private ItemsRange itemsRange;
        private Double start;
        private Double end;

        private Paragraph(ItemsRange itemsRange) {
            this.itemsRange = itemsRange;
            this.start = convertItemNumber(itemsRange.getFrom());
            this.end = convertItemNumber(itemsRange.getTo());
        }

        @Override
        public String description() {
            return name();
        }

        @Override
        public Optional<String> previousUri() {
            return previous().isPresent() ? Optional.of(previous().get().uri()) : Optional.empty();
        }

        @Override
        public Optional<String> nextUri() {
            return next().isPresent() ? Optional.of(next().get().uri()) : Optional.empty();
        }

        @Override
        public String code() {
            return itemsRange.getCode();
        }

        @Override
        public String uri() {
            return itemsRange.getUri();
        }

        @Override
        public String startItemNumber() {
            return from();
        }

        @Override
        public String name() {
            return itemsRange.getDescription();
        }

        @Override
        public Optional<? extends ParagraphProvider> next() {
            final CategoryProvider category = getCategoryByUri(itemsRange.getCategory())
                    .orElseThrow(() -> new RuntimeException("Category "+itemsRange.getCategory()+" not found in cache"));
            final Iterator<? extends ContentsProvider> iterator = category.children().iterator();
            while (iterator.hasNext()) {
                ContentsProvider child = iterator.next();
                if (child == this) return iterator.hasNext() ? Optional.of((Paragraph) iterator.next()) : Optional.empty();
            }
            return Optional.empty();
        }

        @Override
        public Optional<? extends ParagraphProvider> previous() {
            final CategoryProvider category = getCategoryByUri(itemsRange.getCategory())
                    .orElseThrow(() -> new RuntimeException("Category "+itemsRange.getCategory()+" not found in cache"));
            final ListIterator<? extends ContentsProvider> iterator = category.children().listIterator();
            while (iterator.hasPrevious()) {
                ContentsProvider child = iterator.previous();
                if (child == this) return iterator.hasPrevious() ? Optional.of((Paragraph) iterator.previous()) : Optional.empty();
            }
            return Optional.empty();
        }

        @Override
        public Optional<? extends CategoryProvider> parent() {
            return getCategoryByUri(itemsRange.getCategory());
        }

        @Override
        public List<? extends CategoryProvider> parents() {
            if (!parent().isPresent()) return Collections.emptyList();
            List<CategoryProvider> parents = new LinkedList<>();
            parents.add(parent().get());
            parents.addAll(parent().get().parents());
            return parents;
        }

        @Override
        public String path() {
            return parent().isPresent() ? parent().get().path() + " / " + code() : code();
        }

        @Override
        public String from() {
            return itemsRange.getFrom();
        }

        @Override
        public String to() {
            return itemsRange.getTo();
        }
    }

    @Override
    public Optional<ContentsProvider> get(String name) {
        return Optional.ofNullable(categoryMap.containsKey(name) ? categoryMap.get(name) : paragraphMap.get(name));
    }

    @Override
    public Optional<? extends CategoryProvider> getCategory(String name) {
        return Optional.ofNullable(categoryMap.get(name));
    }

    @Override
    public Optional<? extends ParagraphProvider> getParagraph(String code) {
        return Optional.ofNullable(paragraphMap.get(code));
    }

    @Override
    public Optional<? extends ParagraphProvider> getByItemNumber(String number) {
        double itemNumber = convertItemNumber(UriGenerator.generate(Item.class, number));
        return StreamEx.of(paragraphMap.values()).findFirst(o -> {
            Paragraph p = (Paragraph) o;
            if(itemNumber >= p.start && itemNumber <= p.end) {
                return true;
            } else if(p.end == 0.0 && itemNumber == p.start){
                return true;
            }
            return false;
        });
    }

    @Override
    public List<ContentsProvider> descriptionContains(List<String> searchQueries) {
        ListIterator<String> iterator = searchQueries.listIterator();

        String regexp = "";
        while (iterator.hasNext()) {
            String q = iterator.next();
            regexp += RegExpUtils.buildWordContainsRegExp(q);
            if (iterator.hasNext()) regexp += "|";
        }

//        final Map<CategoryProvider, Integer> rateMap = new HashMap<CategoryProvider, Integer>();

        Pattern pattern = Pattern.compile(regexp, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
        List<ContentsProvider> foundCategories = new ArrayList<>();
        for (CategoryProvider provider : categoryMap.values()) {
            if (provider.description() == null || provider.description().isEmpty()) continue;
            Matcher matcher = pattern.matcher(provider.description());
            if (matcher.find()) {
                foundCategories.add(provider);
//                rateMap.put(provider, matcher.start());
            }
        }


        /*
        final List<Paragraph> foundParagraphs = paragraphs()
                .filter(p -> pattern.matcher(p.itemsRange.getDescription()).find())
                .toList();

        foundParagraphs.sort((o1, o2) -> o1.start.compareTo(o2.start));
        foundCategories.addAll(foundParagraphs);
        */

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
        public String uri() {
            return category.getUri();
        }

        @Override
        public String name() {
            return category.getName();
        }

        @Override
        public String getParentUri() {
            return category.getParent();
        }

        @Override
        public String description() {
            return category.getDescription();
        }

        @Override
        public Optional<? extends CategoryProvider> next() {
            return nextUri().isPresent() ? getCategoryByUri(nextUri().get()) : Optional.empty();
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
        public List<ContentsProvider> children() {
            List<ContentsProvider> children = new ArrayList<>();
            if (category.getStart() != null) {
                CategoryProvider child = categoryMap.get(getValueFromUri(Category.class, category.getStart()));
                if (child != null) {
                    children.add(child);
                    while (child.next().isPresent()) {
                        child = child.next().get();
                        if (!child.getParentUri().equals(uri()))
                            break;
                        children.add(child);
                    }
                }
            }
            final List<Paragraph> paragraphs = paragraphs()
                    .filter(p -> p.itemsRange.getCategory().equals(uri()))
                    .sortedByDouble(Paragraph::getStart)
                    .toList();
            children.addAll(paragraphs);
            return children;
        }

        @Override
        public CategoryProvider getParent() {
            return getParentUri() != null ? categoryMap.get(getValueFromUri(Category.class, getParentUri())) : null;
        }

        @Override
        public List<CategoryProvider> parents() {
               return getParents(getValueFromUri(Category.class, uri()));
        }

        private List<CategoryProvider> getParents(String name) {
            List<CategoryProvider> parents = new ArrayList<CategoryProvider>();
            CategoryProvider parent = categoryMap.get(name).getParent();

            if(parent != null) {
                parents.add(parent);
                parents.addAll(getParents(getValueFromUri(Category.class, parent.uri())));
            }
            return parents;
        }

        @Override
        public String extractCategoryName() {
            String[] split = uri().split("/|:");
            return split[split.length-1].trim();
        }

               @Override
               public String path() {
                       String path = "";
                       List<CategoryProvider> chain = new ArrayList<>(parents());
                       chain.add(0, this);
                       ListIterator<CategoryProvider> iterator = chain.listIterator(chain.size());
                       while (iterator.hasPrevious()) {
                               CategoryProvider provider = iterator.previous();
                               if (provider.isContentsRoot() || provider.isCikl()) continue;
//			                   if (provider.isParagraph()) path += "§";
                               path += provider.extractCategoryName();
                               if (iterator.hasPrevious()) path += " / ";
                       }
                       return path;
               }

               @Override
               public String code() {
                       return category.getName();
               }

               @Override
               public Optional<String> previousUri() {
                       for (CategoryProvider provider : categoryMap.values())
                               if (category.getUri().equals(provider.getCategory().getNext())) return Optional.of(provider.uri());
                       return Optional.empty();
               }

               @Override
               public Optional<String> nextUri() {
                       return Optional.ofNullable(category.getNext());
               }

               @Override
               public String startItemNumber() {
                       return getStartItemNumberOfChildren(children());
               }

               private String getStartItemNumberOfChildren(List<? extends ContentsProvider> categories) {
                       if (categories.isEmpty()) return null;
                       ContentsProvider firstCat = categories.get(0);
                       return firstCat instanceof CategoryProvider
                    ? getStartItemNumberOfChildren(((CategoryProvider) firstCat).children())
                    : null;
               }

               @Override
               public Optional<? extends ContentsProvider> getPrevious() {
                       return previousUri().isPresent() ? getByUri(previousUri().get()) : Optional.empty();
               }
       }

    private StreamEx<Paragraph> paragraphs() {
        return StreamEx.of(paragraphMap.values()).map(p -> (Paragraph) p);
    }

    @Override
       public Optional<? extends ContentsProvider> getByUri(String uri) {
               if (uri == null) return Optional.empty();
        final Optional<? extends CategoryProvider> catOpt = getCategoryByUri(uri);
        return catOpt.isPresent() ? catOpt : getParagraphByUri(uri);
       }

    public Optional<? extends CategoryProvider> getCategoryByUri(String uri) {
        final CategoryProvider categoryProvider = categoryMap.get(UriGenerator.getValueFromUri(Category.class, uri));
        return Optional.ofNullable(categoryProvider);
       }

    public Optional<? extends ParagraphProvider> getParagraphByUri(String uri) {
        final ParagraphProvider paragraphProvider = paragraphMap.get(UriGenerator.getValueFromUri(ItemsRange.class, uri));
        return Optional.ofNullable(paragraphProvider);
       }

       private static Double convertItemNumber(String value) {
        return value != null ? Double.parseDouble(getValueFromUri(Item.class, value)) : 0.0;
    }

    @Override
    public StreamEx<? extends CategoryProvider> getAllCategories(){
        return StreamEx.of(categoryMap.values());
    }

    @Override
    public StreamEx<? extends ParagraphProvider> getAllParagraphs() {
        return paragraphs();
    }

    @Override
    public Map<String, String> getAllUriNames(){
        final Map<String, String> map = categoryMap.values().stream()
                .collect(Collectors.toMap(categoryProvider -> categoryProvider.getCategory().getUri(),
                        categoryProvider -> categoryProvider.getCategory().getName()));

        paragraphMap.values().forEach(p -> map.put(p.uri(), p.name()));
        return map;
    }

    @Override
    public Map<String, String> getAllUriDescription() {
        final Map<String, String> map = categoryMap.values().stream()
                .filter(categoryProvider -> categoryProvider.getCategory().getDescription() != null)
                .collect(Collectors.toMap(categoryProvider -> categoryProvider.getCategory().getUri(),
                        categoryProvider -> categoryProvider.getCategory().getDescription()));

        return map;
    }
}

