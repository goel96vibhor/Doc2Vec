package com.media.net.Utils;

/**
 * Created by vibhor.go on 01/19/17.
 */

public class ResultSet
{
    private String entity;
    private Double similarityScore;

    public ResultSet(String word, double similarityScore)
    {
        this.entity =word;
        this.similarityScore=similarityScore;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public Double getSimilarityScore() {
        return similarityScore;
    }

    public void setSimilarityScore(Double similarityScore) {
        this.similarityScore = similarityScore;
    }

    public void printResultSet()
    {
        System.out.println("Word:"+ entity +" similarity score:"+similarityScore);
    }
}