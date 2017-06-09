package com.media.net.NeuralNet;


import com.media.net.Beans.DocBean;
import com.media.net.DataPreparation.ShortFileReader;
import com.media.net.PreprocessingEntities.HuffmanNode;
import com.media.net.Utils.ApplicationProperties;
import com.media.net.Utils.ResultSet;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by vibhor.go on 01/23/17.
 */

public class Doc2VecTrainer extends NeuralNetworkTrainer
{

    public ArrayList<DocBean> documents;
    public HashMap<Long, Integer> docTagtoIndex;
    public ArrayList<double []> doc_syn0;
    public static int doc_vector_size= Integer.parseInt(ApplicationProperties.getProperty("DOC_VECTOR_SIZE"));
    public Doc2VecAlgoType doc2vecAlgo;
    public boolean learnWords;
    public int numDocuments;
    public Doc2VecTrainer(Map<String,HuffmanNode> huffmanNodeMap,ArrayList<String> index2Word,
                                HashMap<String,Integer> word2index,NeuralNetworkConfig networkConfig)
    {
        super(huffmanNodeMap,index2Word,word2index,networkConfig);
        documents= new ArrayList<DocBean>();
        docTagtoIndex = new HashMap<Long, Integer>();
        doc_syn0=new ArrayList<double[]>();
        this.learnWords= networkConfig.learnWords;
        this.doc2vecAlgo= networkConfig.doc2VecAlgoType;
    }

//    protected void initializeWeights()
//    {
//        for(int i=0;i<vocabSize;i++)
//        {
//            generator.nextDouble();
//            for (int j=0;j<layerSize;j++)
//            {
//
//                doctag_syn0[i][j]=(generator.nextDouble()-0.5)/layerSize;
//            }
//        }
//        super.initializeWeights();
//    }

    public void addDocTags(ArrayList<File> listFiles)
    {
        ArrayList<DocBean> docBeans;
        ShortFileReader shortFileReader= new ShortFileReader();
        for(File file:listFiles)
        {
            docBeans= shortFileReader.readDocBeansfromResource(file);
            for(DocBean document: docBeans)
            {
                if (!docTagtoIndex.containsKey(document.getTag()))
                {
                    docTagtoIndex.put(document.getTag(),docTagtoIndex.size());
                    DocBean docBean= new DocBean(document.getTag(), document.getUrl());
                    docBean.setTitle(document.getTitle());
                    docBean.setUrl(document.getUrl());
                    documents.add(docBean);
                    double []randVec= new double[layerSize];
                    for (int j=0;j<layerSize;j++)
                    {

                        randVec[j]=(generator.nextDouble()-0.5)/layerSize;
                    }
                    doc_syn0.add(randVec) ;
                }
            }
        }
    }

    public NeuralNetWorker createWorker(int workerNo,ArrayList<File> listFiles,NeuralNetworkTrainer networkTrainer)
    {
        return new Doc2VecWorker(workerNo,listFiles,this);
    }

    public ArrayList<ResultSet> mostSimilartoDocument(long docTag, int topn) throws Exception
    {
        if(!docTagtoIndex.containsKey(docTag))
            throw new Exception("Doctag:"+docTag+" not present in learned documents");
        else
        {
            return mostSimilarDocumentToVector(doc_syn0.get(docTagtoIndex.get(docTag)),topn);
        }
    }

    public ArrayList<ResultSet> mostSimilarDocumentToVector(double vector[], int topn) throws Exception
    {
        HashMap<Integer, Double> documentScores= new HashMap<Integer, Double>();
        ArrayList<ResultSet> similarDocuments=new ArrayList<ResultSet>();
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
            for(int i=0;i<doc_syn0.size();i++)
            {
                documentScores.put(i, matrixDot(vector, doc_syn0.get(i)));
            }
            List<Map.Entry<Integer,Double>> listOfEntries = new ArrayList<Map.Entry<Integer,Double>>(documentScores.entrySet());
            Collections.sort(listOfEntries, valueComparator);

            for(int i=0;i<topn;i++)
            {
                similarDocuments.add(new ResultSet(documents.get(listOfEntries.get(i).getKey()).getTitle().toString() ,listOfEntries.get(i).getValue()));
            }
            return similarDocuments;
        }
    }

    public static void serializeNetwork(Doc2VecTrainer trainer)
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
        System.out.println(trainer.documents.size());
        System.out.println(trainer.docTagtoIndex.size());
        System.out.println(trainer.doc_syn0.size());
        System.out.println(trainer.doc_syn0.get(10).toString());
        System.out.println(trainer.doc2vecAlgo+" "+trainer.word2VecAlgo);
        System.out.println("learnWords:"+trainer.learnWords+" learnHidden:"+trainer.learnHidden+" learnVectors:"+trainer.learnVectors);
        System.out.println("cbowMean:"+trainer.cbow_Mean);
        try{
            FileOutputStream fos = new FileOutputStream(ApplicationProperties.getProperty("DOC_NETWORK_FILE"));

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
            outputStream.writeObject(trainer.documents);
            outputStream.writeObject(trainer.docTagtoIndex);
            outputStream.writeObject(trainer.doc_syn0);
            outputStream.writeObject(trainer.doc2vecAlgo);
            outputStream.writeObject(trainer.word2VecAlgo);
            outputStream.writeBoolean(trainer.learnWords);
            outputStream.writeBoolean(trainer.learnHidden);
            outputStream.writeBoolean(trainer.learnVectors);
            outputStream.writeBoolean(trainer.cbow_Mean);


            outputStream.close();
        }
        catch (IOException ioex)
        {
            ioex.printStackTrace();
        }
    }

    public static Doc2VecTrainer deserializeNetwork(Map<String,HuffmanNode> huffmanNodeMap,ArrayList<String> index2Word,
                                                          HashMap<String,Integer> word2index)
    {
        NeuralNetworkConfig networkConfig;
        Doc2VecTrainer trainer=null;
        try{
            FileInputStream fis = new FileInputStream(ApplicationProperties.getProperty("DOC_NETWORK_FILE"));
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
            ArrayList<DocBean> documents= (ArrayList<DocBean>)inputStream.readObject();
            HashMap<Long, Integer> docTagtoIndex= (HashMap<Long, Integer> )inputStream.readObject() ;
            ArrayList<double []> doc_syn0=(ArrayList<double []>)inputStream.readObject();
            Doc2VecAlgoType doc2VecAlgoType= (Doc2VecAlgoType)inputStream.readObject();
            Word2VecAlgoType word2VecAlgoType= (Word2VecAlgoType)inputStream.readObject();
            boolean learnWords= inputStream.readBoolean();
            boolean learnHidden= inputStream.readBoolean();
            boolean learnVectors= inputStream.readBoolean();
            boolean cbow_Mean= inputStream.readBoolean();
            inputStream.close();
            networkConfig = new NeuralNetworkConfig().setNumThreads(numThreads)
                    .setIterations(iterations)
                    .setLayerSize(layerSize)
                    .setWindow(window)
                    .setNegativeSamples(negativeSamples)
                    .setDownSampleRate(downSampleRate)
                    .setInitialLearningRate(initialLearningRate)
                    .setLearnHidden(learnHidden)
                    .setLearnVectors(learnVectors)
                    .setLearnWords(learnWords)
                    .setWord2VecAlgoType(word2VecAlgoType)
                    .setDoc2VecAlgoType(doc2VecAlgoType)
                    .setCbowMean(cbow_Mean);
            trainer= new Doc2VecTrainer(huffmanNodeMap,index2Word,word2index,networkConfig);
            trainer.alpha=alpha;
            trainer.numTrainedTokens=numTrainedTokens;
            trainer.actualWordCount=new AtomicLong(actualWordCount);
            trainer.syn0=syn0;
            trainer.syn1=syn1;
            trainer.synNeg=synNeg;
            trainer.documents=documents;
            trainer.doc_syn0=doc_syn0;
            trainer.docTagtoIndex= docTagtoIndex;
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
            System.out.println(trainer.documents.size());
            System.out.println(trainer.docTagtoIndex.size());
            System.out.println(trainer.doc_syn0.size());
            System.out.println(trainer.doc_syn0.get(10).toString());
            System.out.println(trainer.doc2vecAlgo+" "+trainer.word2VecAlgo);
            System.out.println("learnWords:"+trainer.learnWords+" learnHidden:"+trainer.learnHidden+" learnVectors:"+trainer.learnVectors);
            System.out.println("cbowMean:"+trainer.cbow_Mean);
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
        finally {
            return trainer;
        }
    }

}