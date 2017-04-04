package com.media.net.PreprocessingEntities;

import com.media.net.DataPreparation.ShortFileReader;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by vibhor.go on 01/24/17.
 */

public class FileScanWorker implements Callable<Void>
{
    private int workerNo;
    File[] listofFiles;
    HashMap<String, Integer> threadVocabulary;
    public FileScanWorker(int workerNo,File[] listFiles,HashMap<String, Integer> threadVocabulary)
    {
        this.workerNo=workerNo;
        this.listofFiles=listFiles;
        this.threadVocabulary=threadVocabulary;
    }


    @Override
    public final Void call() throws Exception {
        run();
        return null;
    }

    public void run()
    {
        List<String> tokenizedString;
        Integer pruneCount=1;
        int currentFileNo=workerNo;
        ShortFileReader shortFileReader= new ShortFileReader();
        File file;
        while (currentFileNo<listofFiles.length)
        {
            file= listofFiles[currentFileNo];
            List<String> fileContent= shortFileReader.readResource(file);
            for(String line:fileContent)
            {
                tokenizedString=VocabBuilder.tokenize(line);
                for(String token:tokenizedString)
                {
                    if(threadVocabulary.containsKey(token))threadVocabulary.put(token,threadVocabulary.get(token)+1);
                    else
                    {
                        if(VocabBuilder.maxVocabSize!=-1&&threadVocabulary.size()>=VocabBuilder.maxVocabSize)
                        {
                            pruneCount++;
                            VocabBuilder.pruneVocab(threadVocabulary,pruneCount);
                        }
                        threadVocabulary.put(token,1);
                    }
                }
            }
            currentFileNo+=VocabBuilder.numThreads;
            VocabBuilder.fileCount.addAndGet(1);
            System.out.println("Scanned file "+file.getName()+", total files read:"+VocabBuilder.fileCount);
        }
    }
}
