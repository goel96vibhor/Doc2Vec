package com.media.net.Beans;

import com.media.net.Utils.ResultSet;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: vibhor
 * Date: 16/1/18
 * Time: 6:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class APIResultBean {

    private ArrayList<ResultSet> resultSets = new ArrayList<ResultSet>();
    private ArrayList<ResultSet> titleResultSets = new ArrayList<ResultSet>();
    private String word1;
    private String word2;
    private String negWord;
    private String error ;
    private Double similarity;
    private String title;
    private String url;
    private String content;
    private String sentence1;
    private String sentence2;
    private Long entityId;
    private double vector[]=null;

    public String getSentence1() {
        return sentence1;
    }

    public void setSentence1(String sentence1) {
        this.sentence1 = sentence1;
    }

    public String getSentence2() {
        return sentence2;
    }

    public void setSentence2(String sentence2) {
        this.sentence2 = sentence2;
    }

    public double[] getVector() {
        return vector;
    }

    public void setVector(double[] vector) {
        this.vector = vector;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ArrayList<ResultSet> getTitleResultSets() {
        return titleResultSets;
    }

    public void setTitleResultSets(ArrayList<ResultSet> titleResultSets) {
        this.titleResultSets = titleResultSets;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Double getSimilarity() {
        return similarity;
    }

    public void setSimilarity(Double similarity) {
        this.similarity = similarity;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public ArrayList<ResultSet> getResultSets() {
        return resultSets;
    }

    public void setResultSets(ArrayList<ResultSet> resultSets) {
        this.resultSets = resultSets;
    }

    public String getWord1() {
        return word1;
    }

    public void setWord1(String word1) {
        this.word1 = word1;
    }

    public String getWord2() {
        return word2;
    }

    public void setWord2(String word2) {
        this.word2 = word2;
    }

    public String getNegWord() {
        return negWord;
    }

    public void setNegWord(String negWord) {
        this.negWord = negWord;
    }
}


