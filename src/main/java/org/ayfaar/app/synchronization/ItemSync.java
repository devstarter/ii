package org.ayfaar.app.synchronization;

import net.sourceforge.jwbf.core.contentRep.SimpleArticle;
import org.ayfaar.app.dao.CommonDao;
import org.ayfaar.app.model.Item;
import org.ayfaar.app.model.Term;
import org.ayfaar.app.utils.AliasesMap;
import org.ayfaar.app.utils.UriGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

import static java.lang.String.format;
import static java.util.regex.Pattern.*;
import static org.ayfaar.app.utils.UriGenerator.getValueFromUri;

@Component
public class ItemSync implements EntitySynchronizer<Item> {
    @Autowired AliasesMap aliasesMap;
    @Autowired CommonDao commonDao;
    @Autowired MediaWikiBotHelper mediaWikiBotHelper;
    @Autowired TermSync termSync;

    public Set<String> foundTerms = new HashSet<String>();

    @Override
    public void synchronize(Item item) throws Exception {
        synchronize(item, null);
    }

    public void synchronize(Item item, String paragraph) throws Exception {
        String content = item.getContent();

        for (Map.Entry<String, AliasesMap.Proxy> entry : aliasesMap.entrySet()) {
            String key = entry.getKey();
            Matcher matcher = compile("(([^A-Za-zА-Яа-я0-9Ёё\\[\\|\\-])|^)(" + key
                    + ")(([^A-Za-zА-Яа-я0-9Ёё\\]\\|\\-])|$)", UNICODE_CHARACTER_CLASS|UNICODE_CASE|CASE_INSENSITIVE)
                    .matcher(content);
            if (matcher.find()) {
                content = matcher.replaceAll(format("%s[[%s|%s]]%s",
                        matcher.group(2),
                        getValueFromUri(Term.class, entry.getValue().getUri()),
                        matcher.group(3),
                        matcher.group(5)
                ));
                foundTerms.add(entry.getValue().getUri());
//                termSync.synchronize(entry.getValue().getTerm());
            }
        }

        if (item.getNext() != null || paragraph != null) {
            content += "\n<noinclude>\n";
            if (item.getNext() != null) {
                content += format("[[%s|Следующий пункт]] ", UriGenerator.getValueFromUri(Item.class, item.getNext()));
            }
            if (paragraph != null) {
                content += format("[[%s]]", paragraph);
            }
            content += "\n</noinclude>";
        }

        SimpleArticle article = new SimpleArticle(item.getNumber());
        article.setText(content);

        mediaWikiBotHelper.saveArticle(article);
//        item.setWiki(content);
//        commonDao.save(item);
    }
}
