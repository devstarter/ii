package org.ayfaar.ii.utils;

public class Content {
    private String uri;
    private String name;
    private String content;

    public Content() {
    }

    public Content(String uri, String name, String content) {
        this.uri = uri;
        this.name = name;
        this.content = content;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
