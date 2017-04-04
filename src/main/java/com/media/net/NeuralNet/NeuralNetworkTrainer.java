package com.media.net.NeuralNet;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.media.net.DataPreparation.ShortFileReader;
import com.media.net.PreprocessingEntities.HuffmanNode;
import com.media.net.PreprocessingEntities.VocabBuilder;
import com.media.net.Utils.ApplicationProperties;
import com.media.net.Utils.ResultSet;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;


/**
 * Created by vibhor.go on 01/23/17.
 */

public class NeuralNetworkTrainer
{
    private static Logger logger = Logger.getLogger(NeuralNetworkTrainer.class.getName());
    static final int MAX_EXP=6;
    static final int EXP_TABLE_SIZE=1000;
    static final double[] exp_table=new double[EXP_TABLE_SIZE];
    static {
        for(int i=0;i<EXP_TABLE_SIZE;i++)
        {
            exp_table[i]=Math.exp((((double)(i*2.0)/(double)EXP_TABLE_SIZE)-1.0)*MAX_EXP);
            exp_table[i]/=1.0+exp_table[i];
//            if(i%100==0)System.out.println(exp_table[i]);
        }
    }

    static final int TABLE_SIZE=(int)1e8;
    Map<String,HuffmanNode> huffmanNodeMap=null;
    ArrayList<String> index2Word=null;
    HashMap<String,Integer> word2index=null;
    double [][] syn0;
    double [][] syn1;
    double [][] synNeg;
    public int[] table;
    int numThreads;
    int iterations;
    int layerSize;
    int window;
    int negativeSamples;
    double downSampleRate;
    volatile double alpha;
    double initialLearningRate;
    int vocabSize;
    long numTrainedTokens;
    protected AtomicLong actualWordCount;
    protected AtomicInteger fileCount;

    Random generator= new Random(1000);

    public NeuralNetworkTrainer(Map<String,HuffmanNode> huffmanNodeMap,ArrayList<String> index2Word,
                         HashMap<String,Integer> word2index,NeuralNetworkConfig networkConfig)
    {
        this.huffmanNodeMap=huffmanNodeMap;
        this.index2Word=index2Word;
        this.word2index=word2index;
        this.numThreads=networkConfig.numThreads;
        this.iterations=networkConfig.iterations;
        this.layerSize=networkConfig.layerSize;
        this.window=networkConfig.window;
        this.negativeSamples=networkConfig.negativeSamples;
        this.downSampleRate=networkConfig.downSampleRate;
        this.alpha=networkConfig.initialLearningRate;
        this.initialLearningRate=networkConfig.initialLearningRate;
        this.vocabSize=huffmanNodeMap.size();
        this.numTrainedTokens=0;
        this.actualWordCount=new AtomicLong();

        this.syn0 = new double[vocabSize][layerSize];
        this.syn1 = new double[vocabSize][layerSize];
        this.synNeg = new double[vocabSize][layerSize];
        this.table = new int[TABLE_SIZE];
        initializeWeights();
        initializeSampleTable();

        this.numTrainedTokens=0;
        for(HuffmanNode node:huffmanNodeMap.values())
        {
            this.numTrainedTokens+=node.count;
        }
        System.out.println(Arrays.toString(syn0[1000]));
        System.out.println(Arrays.toString(syn1[1000]));
        System.out.println(Arrays.toString(synNeg[1000]));
        System.out.println(exp_table[EXP_TABLE_SIZE- 5]);
        System.out.println(table[TABLE_SIZE- 1000]);
    }

    static long incrementRandom(long r) {
        return r * 25214903917L + 11;
    }


    protected void initializeWeights()
    {

        for(int i=0;i<vocabSize;i++)
        {
            generator.nextDouble();
            for (int j=0;j<layerSize;j++)
            {

                syn0[i][j]=(generator.nextDouble()-0.5)/layerSize;
            }
        }
    }

    private void initializeSampleTable()
    {
        double wordCountSum=0.0;
        double pow=0.75;
        for(HuffmanNode node:huffmanNodeMap.values())
        {
            wordCountSum+=Math.pow(node.count,pow);
        }
        double currSum=0.0;

        int i=0;
        HuffmanNode last= huffmanNodeMap.get(index2Word.get(0));
        currSum+=Math.pow(last.count, pow) / wordCountSum;
        for(int j=0;j<TABLE_SIZE;j++)
        {
            if(j/(double)TABLE_SIZE>currSum)
            {
                i++;
                if(i<index2Word.size())last=huffmanNodeMap.get(index2Word.get(i));
                else i--;
                currSum+=Math.pow(last.count, pow) / wordCountSum;
            }
            table[j]=i;
        }
    }

    public void train(String writeDirPath) throws InterruptedException
    {

        File directory= new File(writeDirPath);
        File[] listFiles= directory.listFiles();
        fileCount=new AtomicInteger();
        ListeningExecutorService ex = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(numThreads));

        try {
            long start= System.currentTimeMillis();
            System.out.println(start);
            for(int i=0;i<iterations;i++)
            {
                List<Callable<Void>> tasks= new ArrayList<Callable<Void>>();
                for(int j=0;j<numThreads;j++)
                {
                    tasks.add(createWorker(j,listFiles,this));
                }
                List<ListenableFuture<?>> future= new ArrayList<ListenableFuture<?>>(tasks.size());
                for(Callable<Void> task:tasks)
                {
                    future.add(ex.submit(task));
                }
                try {
                    Futures.allAsList(future).get();
                }
                catch (ExecutionException e)
                {
                    throw new IllegalStateException("error training neural network"+e.getCause());
                }
            }
            long end=System.currentTimeMillis();

            System.out.println("Time taken for training:"+(end-start)+"ms.");
            System.out.println("total words trained:"+actualWordCount);
            ex.shutdown();
        }
        finally {
            ex.shutdownNow();
        }


    }

    public NeuralNetWorker createWorker(int workerNo,File[] listFiles,NeuralNetworkTrainer networkTrainer)
    {
        return new NeuralNetWorker(workerNo,listFiles,this);
    }

    public ArrayList<ResultSet> mostSimilartoWord(String word, int topn) throws Exception
    {
        String similarWord;
        if(!word2index.containsKey(word))  throw new Exception("Word "+word+" is not present in vocabulary.");
        else
        {
            ArrayList<ResultSet> similarWords = mostSimilartoVector(syn0[word2index.get(word)],topn+1);
            for(Iterator<ResultSet> iterator=similarWords.iterator();iterator.hasNext();)
            {
                similarWord= iterator.next().getWord();
                if (similarWord.equals(word))
                {
                    iterator.remove();
                }

            }
            if (similarWords.size()>topn)similarWords.remove(similarWords.size()-1);
            return similarWords;
        }
    }

    public ArrayList<ResultSet> mostSimilartoWord(String word) throws Exception
    {
        return mostSimilartoWord(word,10);
    }

    public ArrayList<ResultSet> empiricallySimilar(ArrayList<String> positive, ArrayList<String> negative, int topn) throws Exception
    {
        double [] resultVector= new double[layerSize];
        int wordFound =0;
        for(String word:positive)
        {
            if(!word2index.containsKey(word)) continue;
            wordFound++;
            for(int i=0;i<layerSize;i++)
            {
                resultVector[i]+=syn0[word2index.get(word)][i];
            }
        }
        for(String word:negative)
        {
            if(!word2index.containsKey(word)) continue;
            wordFound++;
            for(int i=0;i<layerSize;i++)
            {
                resultVector[i]-=syn0[word2index.get(word)][i];
            }
        }
        if(wordFound==0)throw new Exception("none of the passed words are in the vocabulary.");
        ArrayList<ResultSet> extendedWords=mostSimilartoVector(resultVector,topn+wordFound);
        ArrayList<ResultSet> similarWords= new ArrayList<ResultSet>();
        int curr=0;
        while(similarWords.size()<topn)
        {
            if(positive.contains(extendedWords.get(curr).getWord())||negative.contains(extendedWords.get(curr).getWord()));
            else similarWords.add(extendedWords.get(curr));
            curr++;
        }
        return similarWords;
    }

    public ArrayList<ResultSet> empiricallySimilar(ArrayList<String> positive, ArrayList<String> negative) throws Exception
    {
        return empiricallySimilar(positive,negative,10);
    }

    public ArrayList<ResultSet> mostSimilartoVector(double[] vector, int topn)  throws Exception
    {
        HashMap<Integer, Double> wordScores= new HashMap<Integer, Double>();
        ArrayList<ResultSet> similarWords=new ArrayList<ResultSet>();
        Comparator<Map.Entry<Integer, Double>> valueComparator = new Comparator<Map.Entry<Integer,Double>>() {
            @Override public int compare(Map.Entry<Integer,Double> e1, Map.Entry<Integer,Double> e2)
            {
                Double v1 = e1.getValue();
                Double v2 = e2.getValue();
                return v2.compareTo(v1);
            }
        };

         if(vector.length!=layerSize)
         {
             throw new Exception("vectorSize not equal to word vectors dimension");
         }
         else
         {
             for(int i=0;i<index2Word.size();i++)
             {
                 wordScores.put(i,matrixDot(vector,syn0[i]));
             }
             List<Map.Entry<Integer,Double>> listOfEntries = new ArrayList<Map.Entry<Integer,Double>>(wordScores.entrySet());
             Collections.sort(listOfEntries, valueComparator);

             for(int i=0;i<topn;i++)
             {
                 similarWords.add(new ResultSet(index2Word.get(listOfEntries.get(i).getKey()),listOfEntries.get(i).getValue()));
             }
             return similarWords;
         }
    }

    public ArrayList<ResultSet> mostSimilartoVector(double[] vector)  throws Exception
    {
        return mostSimilartoVector(vector,10);
    }

    public Double wordSimilarity(String word1, String word2) throws Exception
    {
        if(word2index.containsKey(word1)&&word2index.containsKey(word2))
        {
             return matrixDot(syn0[word2index.get(word1)],syn0[word2index.get(word2)]);
        }
        else throw new  Exception("both the words should be present in the vocabulary.");
    }

    public ArrayList<ResultSet> probabWordforSentence(String sentence, int topn) throws Exception
    {
        Double score=0.0;
        int wordFound=0;
        ArrayList<String> sentenceTokens= VocabBuilder.tokenize(sentence);
        HashMap<Integer, Double> wordScores= new HashMap<Integer, Double>();
        ArrayList<ResultSet> similarWords=new ArrayList<ResultSet>();
        Comparator<Map.Entry<Integer, Double>> valueComparator = new Comparator<Map.Entry<Integer,Double>>() {
            @Override public int compare(Map.Entry<Integer,Double> e1, Map.Entry<Integer,Double> e2)
            {
                Double v1 = e1.getValue();
                Double v2 = e2.getValue();
                return v2.compareTo(v1);
            }
        };
        for(String word:sentenceTokens)
        {
            if(word2index.containsKey(word))
            {
                wordFound++;
            }
        }
        if ((wordFound==0))throw new Exception("none of the words in the sentence are in the vocabulary.");
        for(int i=0;i<index2Word.size();i++)
        {
            score=1.0;
            for(String word:sentenceTokens)
            {
                if(word2index.containsKey(word))
                {
                    score*=  matrixDot(syn0[i],syn0[word2index.get(word)]);
//                    score*=1.0/(1.0+Math.exp(-matrixDot(syn0[i],syn0[word2index.get(word)])));
                }
            }
            wordScores.put(i,score);
        }

        List<Map.Entry<Integer,Double>> listOfEntries = new ArrayList<Map.Entry<Integer,Double>>(wordScores.entrySet());
        Collections.sort(listOfEntries, valueComparator);

        int count=0;int index=0;
        while (count<topn)
        {
            if(!sentenceTokens.contains(index2Word.get(listOfEntries.get(index).getKey())))
            {
                similarWords.add(new ResultSet(index2Word.get(listOfEntries.get(index).getKey()),listOfEntries.get(index).getValue()));
                count++;
            }
            index++;
        }
        return similarWords;
    }

    public ArrayList<ResultSet> probabWordforSentence(String sentence) throws Exception
    {
        return probabWordforSentence(sentence,10);
    }

    public Double matrixDot(double [] vector1,double [] vector2) throws Exception
    {
        if(vector1.length!=vector2.length)throw new Exception("vectors to be dot multiplied have unequal dimensions");
        double norm1=0d,norm2=0d,score=0d;
        for(int i=0;i<vector1.length;i++)
        {
            score+=vector1[i]*vector2[i];
            norm1+=vector1[i]*vector1[i];
            norm2+=vector2[i]*vector2[i];
        }
        score/=Math.sqrt(norm1*norm2);
        return score;
    }

    public static void serializeNetwork(NeuralNetworkTrainer trainer)
    {
        System.out.println("Network details:");
        System.out.println(trainer.numThreads);
        System.out.println(trainer.iterations);
        System.out.println(trainer.layerSize);
        System.out.println(trainer.window);
        System.out.println(trainer.negativeSamples);
        System.out.println(trainer.downSampleRate);
        System.out.println(trainer.alpha);
        System.out.println(trainer.initialLearningRate);
        System.out.println(trainer.numTrainedTokens);
        System.out.println(trainer.actualWordCount.get());
        System.out.println(Arrays.toString(trainer.syn0[0]));
        System.out.println(Arrays.toString(trainer.syn1[510844]));
        System.out.println(Arrays.toString(trainer.synNeg[0]));
        try{
            FileOutputStream fos = new FileOutputStream(ApplicationProperties.getProperty("NETWORK_FILE"));

            ObjectOutputStream outputStream = new ObjectOutputStream(fos);
            outputStream.writeInt(trainer.numThreads);
            outputStream.writeInt(trainer.iterations);
            outputStream.writeInt(trainer.layerSize);
            outputStream.writeInt(trainer.window);
            outputStream.writeInt(trainer.negativeSamples);
            outputStream.writeDouble(trainer.downSampleRate);
            outputStream.writeDouble(trainer.alpha);
            outputStream.writeDouble(trainer.initialLearningRate);
            outputStream.writeLong(trainer.numTrainedTokens);
            outputStream.writeLong(trainer.actualWordCount.get());
            outputStream.writeObject(trainer.syn0);
            outputStream.writeObject(trainer.syn1);
            outputStream.writeObject(trainer.synNeg);

            outputStream.close();
        }
        catch (IOException ioex)
        {
            ioex.printStackTrace();
        }
    }

    public static NeuralNetworkTrainer deserializeNetwork(Map<String,HuffmanNode> huffmanNodeMap,ArrayList<String> index2Word,
                                                          HashMap<String,Integer> word2index)
    {
        NeuralNetworkConfig networkConfig;
        NeuralNetworkTrainer trainer=null;
        try{
            FileInputStream fis = new FileInputStream(ApplicationProperties.getProperty("NETWORK_FILE"));
            ObjectInputStream inputStream = new ObjectInputStream(fis);
            int numThreads=inputStream.readInt();
            int iterations=inputStream.readInt();
            int layerSize=inputStream.readInt();
            int window=inputStream.readInt();
            int negativeSamples=inputStream.readInt();
            double downSampleRate=inputStream.readDouble();
            double alpha=inputStream.readDouble();
            double initialLearningRate=inputStream.readDouble();
            Long numTrainedTokens=inputStream.readLong();
            Long actualWordCount=inputStream.readLong();
            double syn0[][]=(double [][])inputStream.readObject();
            double syn1[][]=(double [][])inputStream.readObject();
            double synNeg[][]=(double [][])inputStream.readObject();
            inputStream.close();
            networkConfig = new NeuralNetworkConfig().setNumThreads(numThreads)
                                    .setIterations(iterations)
                                    .setLayerSize(layerSize)
                                    .setWindow(window)
                                    .setNegativeSamples(negativeSamples)
                                    .setDownSampleRate(downSampleRate)
                                    .setInitialLearningRate(initialLearningRate);
            trainer= new NeuralNetworkTrainer(huffmanNodeMap,index2Word,word2index,networkConfig);
            trainer.alpha=alpha;
            trainer.numTrainedTokens=numTrainedTokens;
            trainer.actualWordCount=new AtomicLong(actualWordCount);
            trainer.syn0=syn0;
            trainer.syn1=syn1;
            trainer.synNeg=synNeg;
            System.out.println("Network details:");
            System.out.println(trainer.numThreads);
            System.out.println(trainer.iterations);
            System.out.println(trainer.layerSize);
            System.out.println(trainer.window);
            System.out.println(trainer.negativeSamples);
            System.out.println(trainer.downSampleRate);
            System.out.println(trainer.alpha);
            System.out.println(trainer.initialLearningRate);
            System.out.println(trainer.numTrainedTokens);
            System.out.println(trainer.actualWordCount.get());
            System.out.println(Arrays.toString(trainer.syn0[0]));
            System.out.println(Arrays.toString(trainer.syn1[0]));
            System.out.println(Arrays.toString(trainer.synNeg[0]));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally {
            return trainer;
        }
    }

}
