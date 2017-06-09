/*
 * Copyright (C) 2016 Media.net Advertising FZ-LLC All Rights Reserved
 */

package com.media.net.Beans;

import com.autoopt.beans.NGramCollection;
import com.autoopt.utils.UrlCleaner;

import java.net.MalformedURLException;

/**
 * Created by vibhor.go on 15/4/16.
 */
public class UrlBean {

    private String url;
    private String urlCanonicalHash;
    private String crawledContent;
    private String title;
    private String metaContent;
    private String usefulContent;

    public UrlBean(String url) throws MalformedURLException {
        this.url = url;
        this.urlCanonicalHash = UrlCleaner.getCanonicalUrlHash(url);
        this.crawledContent = "";
        this.title = "";
        this.metaContent = "";
        this.usefulContent = "";
    }

    public UrlBean(String url, String urlCanonicalHash) {
        this.url = url;
        this.urlCanonicalHash = urlCanonicalHash;
        this.crawledContent = "";
        this.title = "";
        this.metaContent = "";
        this.usefulContent = "";
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) throws MalformedURLException {
        this.url = url;
        this.urlCanonicalHash = UrlCleaner.getCanonicalUrlHash(url);
    }

    public String getCrawledContent() {
        return crawledContent;
    }

    public void setCrawledContent(String crawledContent) {
        this.crawledContent = crawledContent;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMetaContent() {
        return metaContent;
    }

    public void setMetaContent(String metaContent) {
        this.metaContent = metaContent;
    }

    public String getUsefulContent() {
        return usefulContent;
    }

    public void setUsefulContent(String usefulContent) {
        this.usefulContent = usefulContent;
    }

    public String getUrlCanonicalHash() {
        return urlCanonicalHash;
    }

    public void setUrlCanonicalHash(String urlCanonicalHash) {
        this.urlCanonicalHash = urlCanonicalHash;
    }
}
