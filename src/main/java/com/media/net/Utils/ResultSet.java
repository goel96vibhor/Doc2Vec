package com.media.net.Utils;

/**
 * Created by vibhor.go on 01/19/17.
 */

public class ResultSet
{
    private String word;
    private Double similarityScore;

    public ResultSet(String word, double similarityScore)
    {
        this.word=word;
        this.similarityScore=similarityScore;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Double getSimilarityScore() {
        return similarityScore;
    }

    public void setSimilarityScore(Double similarityScore) {
        this.similarityScore = similarityScore;
    }

    public void printResultSet()
    {
        System.out.println("Word:"+word+" similarity score:"+similarityScore);
    }
}