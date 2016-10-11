package org.ayfaar.app.services.links;

import org.ayfaar.app.model.LightLink;
import org.ayfaar.app.model.Link;
import org.ayfaar.app.model.LinkType;
import org.ayfaar.app.model.UID;
import org.ayfaar.app.utils.UriGenerator;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class LinkProvider {
    private final Class<? extends UID> uid1Class;
    private final Class<? extends UID> uid2Class;
    private LightLink link;
    private Function<LightLink, Link> saver;

    LinkProvider(LightLink link, Function<LightLink, Link> saver) {
        this.link = link;
        this.saver = saver;
        uid1Class = UriGenerator.getClassByUri(link.getUid1());
        uid2Class = UriGenerator.getClassByUri(link.getUid2());
    }

    public String taggedQuote() {
        return link.getTaggedQuote();
    }

    public <T extends UID> Optional<String> get(Class<T> entityClass) {
        if (entityClass == uid1Class) return Optional.of(link.getUid1());
        if (entityClass == uid2Class) return Optional.of(link.getUid2());
        return Optional.empty();
    }

    public LinkType type() {
        return link.getType();
    }

    public String not(String uri) {
        return Objects.equals(link.getUid1(), uri) ? link.getUid2() : link.getUid1();
    }

    public Updater updater() {
        return new Updater();
    }

    public boolean has(Class<?> entityClass) {
        return uid1Class == entityClass || uid2Class == entityClass;
    }

    public class Updater {
        List<Runnable> updates = new LinkedList<>();

        public Updater rate(Float rate) {
            updates.add(() -> link.setRate(rate));
            return this;
        }

        public Updater comment(String value) {
            updates.add(() -> link.setComment(value));
            return this;
        }

        public Updater quote(String value) {
            updates.add(() -> link.setQuote(value));
            return this;
        }

        public void commit() {
            updates.forEach(Runnable::run);
            saver.apply(link);
        }
    }
}
