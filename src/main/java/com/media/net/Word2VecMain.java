package com.media.net;
/**
 * Created by vibhor.go on 01/18/17.
 */

import com.media.net.DataPreparation.*;
import com.media.net.NeuralNet.NeuralNetworkConfig;
import com.media.net.NeuralNet.NeuralNetworkTrainer;
import com.media.net.PreprocessingEntities.WordDetails;
import com.media.net.Utils.ApplicationProperties;
import com.media.net.Utils.ResultSet;
import com.media.net.Utils.StopWordHash;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.*;

public class Word2VecMain
{
    private static Logger logger = Logger.getLogger(Word2VecMain.class.getName());
    static {
        try {
            ApplicationProperties.loadProperties(Word2VecMain.class.getResourceAsStream("/application.properties"), true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String args[])
    {
        String readDirPath=ApplicationProperties.getProperty("READ_DIR_PATH");
        String filePath=ApplicationProperties.getProperty("FILE_PATH");
        String writeDirPath=ApplicationProperties.getProperty("WRITE_DIR_PATH");
        Random generator= new Random();
        File readFile= new File(filePath);
//        FileSplitter.splitFile(readFile.getAbsolutePath(),writeDirPath);
//        File directory= new File(readDirPath);
//        File[] listofFiles= directory.listFiles();
//        int count=0;
//        for(File file:listofFiles){
//            FileSplitter.splitFile(file.getAbsolutePath(),writeDirPath);
//            System.out.println("splitted file "+(count++));
//        }
//        WordDetails.finalizeVocabDetails(writeDirPath);
//        WordDetails.serializeWordDetails();
        WordDetails.deserializeWordDetails(ApplicationProperties.getProperty("WORD_DETAILS_FILE"));
//        long count=0;
//        for(int i=0;i<100;i++)
//        {
//            //if(StopWordHash.contains(WordDetails.index2Word.get(i)))
//            count+=WordDetails.vocabulary.get(WordDetails.index2Word.get(i));
//            System.out.println(WordDetails.vocabulary.get(WordDetails.index2Word.get(i))+" " + WordDetails.index2Word.get(i));
//        }
//        System.out.println();
//        System.out.println(count);
//        count=0;
//        for(int i=0;i<WordDetails.index2Word.size();i++)
//        {
//            count+=WordDetails.vocabulary.get(WordDetails.index2Word.get(i));
//        }
//        System.out.println(WordDetails.index2Word.size());
//        System.out.println(count);
//        NeuralNetworkConfig networkConfig= new NeuralNetworkConfig()
//                .setNumThreads(25)
//                .setIterations(1)
//                .setLayerSize(25)
//                .setWindow(8)
//                .setNegativeSamples(5)
//                .setDownSampleRate(1e-3)
//                .setInitialLearningRate(0.025);
//        NeuralNetworkTrainer neuralNetworkTrainer=new NeuralNetworkTrainer(WordDetails.huffmanNodeMap,
//                                                    WordDetails.index2Word,WordDetails.word2index,networkConfig);
//        try {
//            neuralNetworkTrainer.train(writeDirPath);
//            NeuralNetworkTrainer.serializeNetwork(neuralNetworkTrainer);
//
//
//        }
//        catch (InterruptedException ex)
//        {
//            logger.error(ex.getMessage());
//        }
        NeuralNetworkTrainer networkTrainer= NeuralNetworkTrainer.deserializeNetwork(WordDetails.huffmanNodeMap,
                WordDetails.index2Word,
                WordDetails.word2index);
        Scanner console = new Scanner(System.in);
        boolean loop = true;
        String input,input2,input3;
        String task;
        ArrayList<ResultSet> resultSets= new ArrayList<ResultSet>();
        while(loop) {
            System.out.println("Enter 0 to exit, 1 for word pair similarity,2 for most similar word, 3 for most similar for sentence, 4 for random most dissimilar, else anything for analogy :");
            task = console.nextLine();
            //Add input to a data structure
            if (task.equals("0")) {
                break;
            }
            try {
                if(task.equals("1"))
                {
                    System.out.println("enter word 1");
                    input=console.nextLine();
                    System.out.println("enter word 2");
                    input2=console.nextLine();
                    System.out.println("similarity score " + networkTrainer.wordSimilarity(input, input2));
                }
                else if(task.equals("2")){
                    input=console.nextLine();
                    resultSets= networkTrainer.mostSimilartoWord(input);
                }
                else if (task.equals("3"))
                {
                    input=console.nextLine();
                    resultSets=networkTrainer.probabWordforSentence(input);
                }
                else if(task.equals("4"))
                {
                    String min1=null, min2=null;double minScore=1,score;
                    int posCount=0,negCount=0;
                    for(int i=0;i<WordDetails.index2Word.size();i++)
                    {
                        for(int j=0;j<100;j++)
                        {
                            int k= (int)(Math.random()*WordDetails.index2Word.size());
                            score= networkTrainer.wordSimilarity(WordDetails.index2Word.get(i),WordDetails.index2Word.get(k));
                            if(score<0)negCount++;
                            else posCount++;
                            if(score<minScore)
                            {
                                minScore=score;
                                min1=WordDetails.index2Word.get(i);
                                min2=WordDetails.index2Word.get(k);
                            }
                        }

                    }
                    System.out.println(min1 + " " + min2 + " " + minScore);
                    System.out.println(posCount+" "+negCount);
                }
                else
                {
                    System.out.println("enter positive word 1");
                    input=console.nextLine();
                    System.out.println("enter positive word 2");
                    input2=console.nextLine();
                    System.out.println("enter negative word");
                    input3= console.nextLine();
                    String [] positive ={input,input2};
                    String [] negative={input3};
//                    String [] positive = {"woman","queen"};
//                    String[] negative = {"man"};
                    resultSets=networkTrainer.empiricallySimilar(new ArrayList<String>(Arrays.asList(positive)),new ArrayList<String>(Arrays.asList(negative)));
                }
//
//
//
                if(!task.equals("1"))
                for (ResultSet rs:resultSets)
                    rs.printResultSet();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

    }
}