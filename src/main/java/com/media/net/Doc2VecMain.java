package com.media.net;
/**
 * Created by vibhor.go on 01/18/17.
 */

import com.media.net.Beans.DocBean;
import com.media.net.NeuralNet.*;
import com.media.net.PreprocessingEntities.WordDetails;
import com.media.net.Utils.ApplicationProperties;
import com.media.net.Utils.ResultSet;
import com.media.net.Utils.UrlDataExtractor;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.*;


public class Doc2VecMain
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
    {String readDirPath=ApplicationProperties.getProperty("READ_DIR_PATH");
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
        long count=0;
//        for(int i=0;i<100;i++)
//        {
//            //if(StopWordHash.contains(WordDetails.index2Word.get(i)))
//            count+=WordDetails.vocabulary.get(WordDetails.index2Word.get(i));
//            System.out.println(WordDetails.vocabulary.get(WordDetails.index2Word.get(i))+" " + WordDetails.index2Word.get(i));
//        }
//        System.out.println();
//        System.out.println(count);
        count=0;
        for(int i=0;i<WordDetails.index2Word.size();i++)
        {
            count+=WordDetails.vocabulary.get(WordDetails.index2Word.get(i));
        }
        System.out.println(WordDetails.index2Word.size());
        System.out.println(count);
        NeuralNetworkConfig networkConfig= new NeuralNetworkConfig()
                .setNumThreads(25)
                .setIterations(1)
                .setLayerSize(25)
                .setWindow(8)
                .setNegativeSamples(5)
                .setDownSampleRate(1e-3)
                .setInitialLearningRate(0.025);
//        NeuralNetworkTrainer neuralNetworkTrainer=new NeuralNetworkTrainer(WordDetails.huffmanNodeMap,
//                                                    WordDetails.index2Word,WordDetails.word2index,networkConfig);
//        try {
//            neuralNetworkTrainer.train(writeDirPath);
////            NeuralNetworkTrainer.serializeNetwork(neuralNetworkTrainer);
//
//
//        }
//        catch (InterruptedException ex)
//        {
//            logger.error(ex.getMessage());
//        }
//        Doc2VecTrainer networkTrainer= Doc2VecTrainer.deserializeNetwork(WordDetails.huffmanNodeMap,
//                WordDetails.index2Word,
//                WordDetails.word2index);
//        Scanner console = new Scanner(System.in);
//        boolean loop = true;
//        String input,input2,input3;
//        String task;
//        String url;
//        UrlDataExtractor urlDataExtractor= new UrlDataExtractor();
//        Doc2VecWorker doc2VecWorker= new Doc2VecWorker(networkTrainer);
//        ArrayList<ResultSet> resultSets= new ArrayList<ResultSet>();
//        while(loop) {
//            System.out.println("Enter 0 to exit, 1 for word pair similarity,2 for most similar word, 3 for most similar for sentence, 4 for random most dissimilar" +
//                    ", 5 for getting topics for url, 6 to get word for url, else anything for analogy :");
//            task = console.nextLine();
//            //Add input to a data structure
//            if (task.equals("0")) {
//                break;
//            }
//            try {
//                if(task.equals("1"))
//                {
//                    System.out.println("enter word 1");
//                    input=console.nextLine();
//                    System.out.println("enter word 2");
//                    input2=console.nextLine();
//                    System.out.println("similarity score " + networkTrainer.wordSimilarity(input, input2));
//                }
//                else if(task.equals("2")){
//                    input=console.nextLine();
//                    resultSets= networkTrainer.mostSimilartoWord(input);
//                }
//                else if (task.equals("3"))
//                {
//                    input=console.nextLine();
//                    resultSets=networkTrainer.probabWordforSentence(input);
//                }
//                else if(task.equals("4"))
//                {
//                    String min1=null, min2=null;double minScore=1,score;
//                    int posCount=0,negCount=0;
//                    for(int i=0;i<WordDetails.index2Word.size();i++)
//                    {
//                        for(int j=0;j<100;j++)
//                        {
//                            int k= (int)(Math.random()*WordDetails.index2Word.size());
//                            score= networkTrainer.wordSimilarity(WordDetails.index2Word.get(i),WordDetails.index2Word.get(k));
//                            if(score<0)negCount++;
//                            else posCount++;
//                            if(score<minScore)
//                            {
//                                minScore=score;
//                                min1=WordDetails.index2Word.get(i);
//                                min2=WordDetails.index2Word.get(k);
//                            }
//                        }
//
//                    }
//                    System.out.println(min1 + " " + min2 + " " + minScore);
//                    System.out.println(posCount+" "+negCount);
//                }
//                else if(task.equals("5"))
//                {
//                    double alpha=0.1;
//                    double minAlpha= 0.00001;
//                    System.out.println("Enter url :");
//                    url=console.nextLine();
//                    DocBean docBean= urlDataExtractor.getDocBeanforUrl(url,-1);
//                    System.out.println(docBean.getTag());
//                    System.out.println(docBean.getContent().toString().replaceAll(","," "));
//                    System.out.println(docBean.getTitle().toString().replaceAll(","," "));
//                    double []vector= doc2VecWorker.inferDocument(docBean,alpha,minAlpha,20,networkTrainer);
//                    resultSets= networkTrainer.mostSimilarDocumentToVector(vector,10);
//                    System.out.println();
//                    System.out.println("Content Topics ~ ");
//                    for(ResultSet result : resultSets)
//                    {
//                        System.out.println(result.getWord()+":"+result.getSimilarityScore()+"  ");
//                    }
//                    System.out.println();
//                    alpha=0.1;
//                    DocBean titleDocBean= new DocBean(-1l,url);
//                    titleDocBean.setContent(docBean.getTitle());
//                    titleDocBean.setTitle(docBean.getTitle());
//                    vector= doc2VecWorker.inferDocument(titleDocBean,alpha,minAlpha,20,networkTrainer);
//                    resultSets= networkTrainer.mostSimilarDocumentToVector(vector,10);
//                    System.out.println("Title Topics ~ ");
//                    for(ResultSet result : resultSets)
//                    {
//                        System.out.println(result.getWord()+":"+result.getSimilarityScore()+"  ");
//                    }
//                    System.out.println();
//                }
//                else if(task.equals("6"))
//                {
//                    double alpha=0.1;
//                    double minAlpha= 0.00001;
//                    System.out.println("Enter url :");
//                    url=console.nextLine();
//                    DocBean docBean= urlDataExtractor.getDocBeanforUrl(url,-1);
//                    System.out.println(docBean.getTag());
//                    System.out.println(docBean.getContent().toString().replaceAll(","," "));
//                    System.out.println(docBean.getTitle().toString().replaceAll(","," "));
//                    double []vector= doc2VecWorker.inferDocument(docBean,alpha,minAlpha,20,networkTrainer);
//                    resultSets= networkTrainer.mostSimilartoVector(vector,10);
//                    System.out.println();
//                    System.out.println("Content Words ~ ");
//                    for(ResultSet result : resultSets)
//                    {
//                        System.out.println(result.getWord()+":"+result.getSimilarityScore()+"  ");
//                    }
//                    System.out.println();
//                    alpha=0.1;
//                    DocBean titleDocBean= new DocBean(-1l,url);
//                    titleDocBean.setContent(docBean.getTitle());
//                    titleDocBean.setTitle(docBean.getTitle());
//                    vector= doc2VecWorker.inferDocument(titleDocBean,alpha,minAlpha,20,networkTrainer);
//                    resultSets= networkTrainer.mostSimilartoVector(vector,10);
//                    System.out.println("Title words ~ ");
//                    for(ResultSet result : resultSets)
//                    {
//                        System.out.println(result.getWord()+":"+result.getSimilarityScore()+"  ");
//                    }
//                    System.out.println();
//                }
//                else
//                {
//                    System.out.println("enter positive word 1");
//                    input=console.nextLine();
//                    System.out.println("enter positive word 2");
//                    input2=console.nextLine();
//                    System.out.println("enter negative word");
//                    input3= console.nextLine();
//                    String [] positive ={input,input2};
//                    String [] negative={input3};
////                    String [] positive = {"woman","queen"};
////                    String[] negative = {"man"};
//                    resultSets=networkTrainer.empiricallySimilar(new ArrayList<String>(Arrays.asList(positive)),new ArrayList<String>(Arrays.asList(negative)));
//                }
////
////
////
//                if(!task.equals("1") && (!task.equals("5")) && (!task.equals("6")))
//                for (ResultSet rs:resultSets)
//                    rs.printResultSet();
//            }
//            catch (Exception ex)
//            {
//                ex.printStackTrace();
//            }
//        }
//
//        NeuralNetworkTrainer networkTrainer= NeuralNetworkTrainer.deserializeNetwork(WordDetails.huffmanNodeMap,
//                WordDetails.index2Word,
//                WordDetails.word2index);


//        try{
////            File fos = new File(ApplicationProperties.getProperty("SYNNEG_FILE"));
////
////            BufferedWriter bufferedWriter= new BufferedWriter(new FileWriter(fos));
////            for(int i=0;i<WordDetails.index2Word.size();i++)
////            {
//////                if(i%9500==0)System.out.println(WordDetails.index2Word.get(i));
////                for(int j=0;j<25;j++)
////                {
////                    bufferedWriter.write(networkTrainer.getSynNeg()[i][j]+" ");
////                }
////                bufferedWriter.write('\n');
////            }
////
////            bufferedWriter.close();
////
////            fos = new File(ApplicationProperties.getProperty("SYN0_FILE"));
////
////            bufferedWriter= new BufferedWriter(new FileWriter(fos));
////            for(int i=0;i<WordDetails.index2Word.size();i++)
////            {
////                for(int j=0;j<25;j++)
////                {
////                    bufferedWriter.write(networkTrainer.getSyn0()[i][j]+" ");
////                }
////                bufferedWriter.write('\n');
////            }
////
////            bufferedWriter.close();
//
//            File fos = new File(ApplicationProperties.getProperty("VOCAB_FILE"));
//
//            BufferedWriter bufferedWriter= new BufferedWriter(new FileWriter(fos));
//            for(int i=0;i<WordDetails.index2Word.size();i++)
//            {
////                if(i%9500==0)System.out.println(WordDetails.index2Word.get(i));
//                bufferedWriter.write(WordDetails.index2Word.get(i));
//                bufferedWriter.write('\n');
//            }
//
//            bufferedWriter.close();
//
////            HuffmanNode huffmanNode;
////
////            fos = new File(ApplicationProperties.getProperty("CODE_FILE"));
////            bufferedWriter= new BufferedWriter(new FileWriter(fos));
////            for(int i=0;i<WordDetails.index2Word.size();i++)
////            {
//////                if(i%9500==0)System.out.println(WordDetails.index2Word.get(i));
////                huffmanNode=WordDetails.huffmanNodeMap.get(WordDetails.index2Word.get(i));
////                for(int j=0;j<huffmanNode.code.length;j++)
////                bufferedWriter.write(huffmanNode.code[j]+" ");
////                bufferedWriter.write('\n');
////            }
////            bufferedWriter.close();
////
////            fos = new File(ApplicationProperties.getProperty("POINTS_FILE"));
////            bufferedWriter= new BufferedWriter(new FileWriter(fos));
////            for(int i=0;i<WordDetails.index2Word.size();i++)
////            {
//////                if(i%9500==0)System.out.println(WordDetails.index2Word.get(i));
////                huffmanNode=WordDetails.huffmanNodeMap.get(WordDetails.index2Word.get(i));
////                for(int j=0;j<huffmanNode.points.length;j++)
////                    bufferedWriter.write(huffmanNode.points[j]+" ");
////                bufferedWriter.write('\n');
////            }
////            bufferedWriter.close();
//
//
//        }
//        catch (Exception ex)
//        {
//            ex.printStackTrace();
//        }


    }
}