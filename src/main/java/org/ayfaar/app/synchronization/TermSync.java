package org.ayfaar.app.synchronization;

import net.sourceforge.jwbf.core.contentRep.SimpleArticle;
import org.ayfaar.app.controllers.TermController;
import org.ayfaar.app.dao.LinkDao;
import org.ayfaar.app.model.Link;
import org.ayfaar.app.model.Term;
import org.ayfaar.app.model.UID;
import org.ayfaar.app.utils.AliasesMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

import static java.lang.String.format;
import static java.util.regex.Pattern.*;
import static org.ayfaar.app.utils.UriGenerator.getValueFromUri;

@Component
public class TermSync extends EntitySynchronizer<Term> {
    @Autowired
    MediaWikiBotHelper mediaWikiBotHelper;
    @Autowired
    TermController termController;
    @Autowired
    AliasesMap aliasesMap;
    @Autowired
    LinkDao linkDao;

    private Set<String> alreadySync = new HashSet<String>();

    @Override
    public void synchronize(Term term) throws Exception {

        if (alreadySync.contains(term.getUri())) {
            return;
        }

        String termName = term.getName();
        Term alias = null;
        if (!aliasesMap.get(termName).getTerm().getUri().equals(term.getUri())) {
            Term mainTerm = aliasesMap.get(termName).getTerm();
            saveRedirect(term, mainTerm);
            synchronize(mainTerm);
            return;
        }

        // может быть аббравиатурой или сокращением
        Link _link = linkDao.getForAbbreviation(term.getUri());
        if (_link != null && _link.getUid1() instanceof Term) {
            Term mainTerm = (Term) _link.getUid1();
            saveRedirect(term, mainTerm);
            synchronize(mainTerm);
            return;
        }

        StringBuilder sb = new StringBuilder();

        if (term.getDescription() != null && !term.getDescription().isEmpty()) {
            sb.append(format("=== %s ===\n", term.getDescription()));
        }

        // QUOTES
        for (Link link : linkDao.getRelatedWithQuote(term.getUri())) {
            ModelMap map = new ModelMap();
            UID source = link.getUid1().getUri().equals(term.getUri())
                    ? link.getUid2()
                    : link.getUid1();
            String text = link.getQuote();
            text = markTerms(text);
            sb.append(format("{{Quote|text=%s|sign=[[%s]]}}\n", text, getValueFromUri(source.getClass(), source.getUri())));
        }

        Set<UID> related = new LinkedHashSet<UID>();
        for (Link link : linkDao.getRelated(term.getUri())) {
            if (!Link.ABBREVIATION.equals(link.getType()) && link.getQuote() == null) {
                if (link.getUid1().getUri().equals(term.getUri())) {
                    related.add(link.getUid2());
                } else {
                    related.add(link.getUid1());
                }
            }
        }
        if (related.size() > 0) {
            sb.append(format("== Связан с ==\n"));
            for (UID uid : related) {
                sb.append(format("[[%s]] ", getValueFromUri(uid.getClass(), uid.getUri())));
            }
        }

        SimpleArticle article = new SimpleArticle(term.getName());
        article.setText(sb.length() == 0 ? "Ожидается наполнение" : sb.toString());
        mediaWikiBotHelper.saveArticle(article);
        alreadySync.add(term.getUri());
    }

    private void saveRedirect(Term fromTerm, Term toTerm) {
        SimpleArticle article = new SimpleArticle(fromTerm.getName());
        article.setText(format("#redirect [[%s]]", toTerm.getName()));
        mediaWikiBotHelper.saveArticle(article);
        alreadySync.add(fromTerm.getUri());
    }

    public String markTerms(String content) {
        for (Map.Entry<String, AliasesMap.Proxy> entry : aliasesMap.entrySet()) {
            String key = entry.getKey();
            Matcher matcher = compile("(([^A-Za-zА-Яа-я0-9Ёё\\[\\|\\-])|^)(" + key
                    + ")(([^A-Za-zА-Яа-я0-9Ёё\\]\\|])|$)", UNICODE_CHARACTER_CLASS|UNICODE_CASE|CASE_INSENSITIVE)
                    .matcher(content);
            if (matcher.find()) {
                content = matcher.replaceAll(format("%s[[%s|%s]]%s",
                        matcher.group(2) != null ? matcher.group(2) : "",
                        getValueFromUri(Term.class, entry.getValue().getUri()),
                        matcher.group(3),
                        matcher.group(5) != null ? matcher.group(5) : ""
                ));
                scheduleSync(entry.getValue().getTerm());
            }
        }
        return content;
    }


}
