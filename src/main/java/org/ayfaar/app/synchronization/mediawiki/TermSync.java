package org.ayfaar.app.synchronization.mediawiki;

import org.ayfaar.app.controllers.TermController;
import org.ayfaar.app.dao.LinkDao;
import org.ayfaar.app.model.Link;
import org.ayfaar.app.model.Term;
import org.ayfaar.app.model.UID;
import org.ayfaar.app.utils.TermsMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;
import static java.util.regex.Pattern.*;
import static org.ayfaar.app.synchronization.mediawiki.SyncUtils.getArticleName;

@Component
public class TermSync extends EntitySynchronizer<Term> {
    @Autowired
    MediaWikiBotHelper mediaWikiBotHelper;
    @Autowired
    TermController termController;
    @Autowired
    TermsMap termsMap;
    @Autowired
    LinkDao linkDao;
    @Autowired SyncUtils syncUtils;

    private Set<String> alreadySync = new HashSet<String>();

    @Override
    public void synchronize(Term term) throws Exception {

        if (alreadySync.contains(term.getUri())) {
            return;
        }

        // может быть аббравиатурой или сокращением или кодом
        Term prime = (Term) linkDao.getPrimeForAlias(term.getUri());
        if (prime != null) {
            saveRedirect(term, prime);
            scheduleSync(prime);
            return;
        }

        /*Link _link = linkDao.getForAbbreviationOrAliasOrCode(term.getUri());
        if (_link != null && _link.getUid1() instanceof Term) {
            Term mainTerm = (Term) _link.getUid1();
            saveRedirect(term, mainTerm);
            synchronize(mainTerm);
            return;
        }*/

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
            sb.append(format("{{Quote|text=%s|sign=[[%s]]}}\n", text, getArticleName(source.getClass(), source.getUri())));
            syncUtils.scheduleSync(source);
        }

        Set<UID> aliases = new LinkedHashSet<UID>();
        for (Link link : linkDao.getAliases(term.getUri())) {
            aliases.add(link.getUid2());
        }

        Set<UID> related = new LinkedHashSet<UID>();
        for (Link link : linkDao.getRelated(term.getUri())) {
            if (link.getQuote() == null) {
                if (link.getUid1().getUri().equals(term.getUri())) {
                    related.add(link.getUid2());
                } else {
                    related.add(link.getUid1());
                }
            }
        }
        if (aliases.size() > 0) {
            sb.append(format("== Сокращения или синонимы ==\n"));
            for (UID uid : aliases) {
                sb.append(format("[[%s]], ", getArticleName(uid.getClass(), uid.getUri())));
                syncUtils.scheduleSync(uid);
            }
            sb.delete(sb.length()-2, sb.length());
            sb.append("\n");
        }
        if (related.size() > 0) {
            sb.append(format("== Связан с ==\n"));
            for (UID uid : related) {
                sb.append(format("[[%s]], ", getArticleName(uid.getClass(), uid.getUri())));
                syncUtils.scheduleSync(uid);
            }
            sb.delete(sb.length()-2, sb.length());
        }

        mediaWikiBotHelper.saveArticle(term.getName(), sb.length() == 0 ? "Ожидается наполнение" : sb.toString());
        alreadySync.add(term.getUri());
    }

    private void saveRedirect(Term fromTerm, Term toTerm) {
        mediaWikiBotHelper.saveArticle(fromTerm.getName(), format("#redirect [[%s]]", toTerm.getName()));
        alreadySync.add(fromTerm.getUri());
    }

    public String markTerms(String content) {
        String result = content;
        for (Map.Entry<String, TermsMap.TermProvider> entry : termsMap.getAll()) {
            String key = entry.getKey();
            Pattern pattern = compile("(([^A-Za-zА-Яа-я0-9Ёё\\[\\|\\-])|^)(" + key
                    + ")(([^A-Za-zА-Яа-я0-9Ёё\\]\\|])|$)", UNICODE_CHARACTER_CLASS | UNICODE_CASE | CASE_INSENSITIVE);
            Matcher contentMatcher = pattern.matcher(content);
            if (contentMatcher.find()) {
                Matcher matcher = pattern.matcher(result);
                if (matcher.find()) {
                    scheduleSync(entry.getValue().getTerm());
                    String articleName = getArticleName(Term.class, entry.getValue().getUri());
                    String found = contentMatcher.group(3);
                    String charBefore = contentMatcher.group(2) != null ? contentMatcher.group(2) : "";
                    String charAfter = contentMatcher.group(5) != null ? contentMatcher.group(5) : "";
                    String articleReplacer = articleName.equals(found)
                            ? articleName
                            : format("%s|%s", articleName, found);
                    String fullReplacer = format("%s[[%s]]%s",
                            charBefore,
                            articleReplacer,
                            charAfter
                    );
                    result = matcher.replaceAll(fullReplacer);
                }
                content = contentMatcher.replaceAll("");
            }
        }
        return result;
    }
}
