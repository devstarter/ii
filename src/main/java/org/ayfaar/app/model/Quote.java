package org.ayfaar.app.model;

import lombok.Data;

import javax.persistence.PostLoad;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;

@Data
public class Quote extends UID {

    private String parentUri;
    private int from;
    private int to;

    public Quote() {
    }

    public Quote(String parentUri, int from, int to) {
        this.parentUri = parentUri;
        this.from = from;
        this.to = to;
    }

    @Override
    public String generateUri() {
        return parentUri+"["+from+":"+to+"]";
    }

    @PostLoad
    private void parse() {
        Matcher matcher = Pattern.compile("^(.+)\\[(\\d+):(\\d+)\\]$").matcher(getUri());
        if (matcher.find()) {
            parentUri = matcher.group(1);
            from = parseInt(matcher.group(2));
            to = parseInt(matcher.group(3));
        } else {
            throw new RuntimeException("Quote parse error, uri:"+getUri());
        }
    }
}
