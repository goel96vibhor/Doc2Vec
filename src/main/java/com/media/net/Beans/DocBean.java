package com.media.net.Beans;

/*
  * Created by vibhor.go on 03/16/17.
*/

import java.io.Serializable;
import java.util.List;

public class DocBean implements Serializable
{
    private List<String> content;
    private Long tag;
    private List<String> title;
    private String url;
    public double doctag_syn0;

    public DocBean(Long tag, String url)
    {
        this.tag=tag;
        this.url=url;
        this.title=null;
        this.content=null;
    }

    public List<String> getContent() {
        return content;
    }

    public void setContent(List<String> content) {
        this.content = content;
    }

    public Long getTag() {
        return tag;
    }

    public void setTag(Long tag) {
        this.tag = tag;
    }

    public List<String> getTitle() {
        return title;
    }

    public void setTitle(List<String> title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}