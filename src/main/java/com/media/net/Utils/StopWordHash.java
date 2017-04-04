package com.media.net.Utils;
import java.util.HashSet;


/**
 * Created by vibhor.go on 10/14/16.
 */

public class StopWordHash{

    public static HashSet<String> stopWordHash;

    static
    {
        String stopWords= ApplicationProperties.getProperty("STOP_WORDS");
        String stopWordList[]= stopWords.split("~");
        stopWordHash= new HashSet<String>();
        for(String word:stopWordList)
        {
            stopWordHash.add((word));
        }
    }

    public static boolean contains(String word)
    {
        return stopWordHash.contains(word);
    }

}