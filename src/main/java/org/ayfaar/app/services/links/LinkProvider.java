package org.ayfaar.app.services.links;

import org.ayfaar.app.model.Link;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

public class LinkProvider {
    private Link link;
    private Function<Link, Link> saver;

    LinkProvider(Link link, Function<Link, Link> saver) {
        this.link = link;
        this.saver = saver;
    }

    public Updater updater() {
        return new Updater();
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
