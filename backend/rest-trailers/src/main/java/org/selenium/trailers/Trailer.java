package org.selenium.trailers;


public class Trailer {
    private String url;

    public Trailer() {
    }

    public Trailer(String title) {
        this.url = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
