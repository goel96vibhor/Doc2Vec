package com.media.net.PreprocessingEntities;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.media.net.DataPreparation.ShortFileReader;
import com.media.net.Utils.StopWordHash;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Created by vibhor.go on 01/19/17.
 */

public class VocabBuilder
{
    public static Integer maxVocabSize=-1;
    public static Integer minVocabWordCount=5;
    public static Integer numThreads=5;
    public static AtomicInteger fileCount;

    private static Logger logger = Logger.getLogger(VocabBuilder.class.getName());

    public static void setMaxVocabSize(int maxVocabSize) {
        VocabBuilder.maxVocabSize = maxVocabSize;
    }

    public static FileScanWorker createWorker(int workerno, File []listFiles, HashMap<String,Integer> threadVocabulary)
    {
        return new FileScanWorker(workerno,listFiles,threadVocabulary);
    }

    public static HashMap<String, Integer> scanVocab(String writeDirPath)
    {
        ArrayList<HashMap<String, Integer> > threadVocabularies= new ArrayList<HashMap<String, Integer>>();
        HashMap<String, Integer> scanVocabulary= new HashMap<String, Integer>();
        File directory= new File(writeDirPath);
        File[] listofFiles= directory.listFiles();

        fileCount=new AtomicInteger(0);
        ListeningExecutorService ex = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(numThreads));

        try {
            long start= System.currentTimeMillis();
            System.out.println(start);

            List<Callable<Void>> tasks= new ArrayList<Callable<Void>>();
            for(int j=0;j<numThreads;j++)
            {
                threadVocabularies.add(new HashMap<String, Integer>());
                tasks.add(createWorker(j,listofFiles,threadVocabularies.get(j)));
            }
            List<ListenableFuture<?>> future= new ArrayList<ListenableFuture<?>>(tasks.size());
            for(Callable<Void> task:tasks)
            {
                future.add(ex.submit(task));
            }
            try {
                Futures.allAsList(future).get();
            }
            catch (Exception e)
            {
                throw new IllegalStateException("error training neural network"+e.getCause());
            }

            long end=System.currentTimeMillis();

            System.out.println("Time taken for training:"+(end-start)+"ms.");
            ex.shutdown();
        }
        finally {
            ex.shutdownNow();
        }
        for(HashMap<String, Integer> threadVocabulary:threadVocabularies)
        {
            for(String key:threadVocabulary.keySet())
            {
                if(scanVocabulary.containsKey(key))scanVocabulary.put(key,scanVocabulary.get(key)+threadVocabulary.get(key));
                else scanVocabulary.put(key,threadVocabulary.get(key));
            }
        }

        return scanVocabulary;
    }

    public static void pruneVocab(HashMap<String, Integer> vocabulary, Integer minCount)
    {
        Iterator<HashMap.Entry<String,Integer>> iter = vocabulary.entrySet().iterator();
        while (iter.hasNext()) {
            HashMap.Entry<String,Integer> entry=iter.next();
            if(entry.getValue()<minCount)iter.remove();
        }
    }

    public static HashMap<String, Integer> buildVocab(String writeDirPath)
    {
        long starttime= System.currentTimeMillis();

        HashMap<String, Integer> vocabulary= scanVocab(writeDirPath);
        pruneVocab(vocabulary,minVocabWordCount);
        long totalWords=0;
        for(String key:vocabulary.keySet())
        {
            totalWords+=vocabulary.get(key);
        }
        logger.info("Created vocabulary with size "+vocabulary.size()+" with total word count "+totalWords+",each having word count > "
            +minVocabWordCount+" in vocabulary");
        logger.info("time taken for reading vocabulary"+(System.currentTimeMillis()-starttime));
        return vocabulary;
    }

    public static HashMap<String, Integer> updateVocab(HashMap<String, Integer> vocabulary,String writeDirPath)
    {
        if(vocabulary!=null){
            HashMap<String, Integer> scanVocabulary= scanVocab(writeDirPath);
            for(String key:scanVocabulary.keySet())
            {
                if(vocabulary.containsKey(key))vocabulary.put(key,scanVocabulary.get(key)+vocabulary.get(key));
                else vocabulary.put(key,scanVocabulary.get(key));
            }
            pruneVocab(vocabulary,minVocabWordCount);
            if(maxVocabSize!=-1)
            {
                while (vocabulary.size()>maxVocabSize)
                {
                    minVocabWordCount++;
                    pruneVocab(vocabulary,minVocabWordCount);
                }
            }
        }
        else vocabulary=buildVocab(writeDirPath);
        Integer totalWords=0;
        for(String key:vocabulary.keySet())
        {
            totalWords+=vocabulary.get(key);
        }
        logger.info("Updated vocabulary has size "+vocabulary.size()+" with total word count "+totalWords+",each having word count > "
                +minVocabWordCount+" in vocabulary");
        return vocabulary;
    }

    public static ArrayList<String> tokenize(String line)
    {
        String delimiters = "\\s*,\\s*|\\s*\\.\\s*|\\s+";
        String [] tokens=line.split(delimiters);
        ArrayList<String> tokenizedString= new ArrayList<String>();
        for(String token:tokens)
        {
            if(token.matches(".*[a-zA-Z]+.*")&&token.length()>1)
            {
//                if(!StopWordHash.contains(token))
                tokenizedString.add(token);
            }
        }
        return tokenizedString;
    }


}