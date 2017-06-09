package com.media.net.NeuralNet;

import com.media.net.Beans.DocBean;
import com.media.net.DataPreparation.ShortFileReader;
import com.media.net.PreprocessingEntities.HuffmanNode;
import com.media.net.Utils.ApplicationProperties;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by vibhor.go on 03/29/17.
 */

public class Doc2VecWorker extends NeuralNetWorker implements Callable<Void>
{
    ArrayList<DocBean> docBeansinFile;
    private Doc2VecTrainer trainer;

    public Doc2VecWorker(int workerNo,ArrayList<File> listFiles,Doc2VecTrainer networkTrainer)
    {
        super(workerNo,listFiles);
        this.trainer=networkTrainer;
        neule= new double[trainer.layerSize];
    }

    public Doc2VecWorker(Doc2VecTrainer doc2VecTrainer)
    {
        this.trainer= doc2VecTrainer;
        neule= new double[trainer.layerSize];
    }

    @Override
    public final Void call() throws Exception {
        run(trainer);
        return null;
    }

    public void run(Doc2VecTrainer trainer)
    {
        int currentFileNo=workerNo;
        List<ArrayList<String>> fileTokens= null;
        ArrayList<String> filteredSentence=null;
        double wordProbab;
        int docIndex;
//        for(int i=0;i<listFiles.length;i++)
//        System.out.println("filename:"+listFiles[i].getName());
        ShortFileReader shortFileReader= new ShortFileReader();
        while (currentFileNo<listFiles.size())
        {
            docBeansinFile = shortFileReader.readDocBeansfromResource(listFiles.get(currentFileNo));

            for(DocBean document : docBeansinFile)
            {
                if(!trainer.docTagtoIndex.containsKey(document.getTag()))continue;
                filteredSentence=new ArrayList<String>();
                for(String word:document.getContent())
                {
                    if(!trainer.huffmanNodeMap.containsKey(word))
                    {
                        continue;
                    }
                    if(trainer.downSampleRate>0.0d)
                    {
                        HuffmanNode huffmanNode= trainer.huffmanNodeMap.get(word);
                        wordProbab=Math.sqrt(((double)huffmanNode.count/(trainer.downSampleRate*trainer.numTrainedTokens))+1.0);
                        wordProbab*=(trainer.downSampleRate*trainer.numTrainedTokens)/huffmanNode.count;
                       if(wordProbab<trainer.generator.nextDouble())continue;
                    }
                    filteredSentence.add(word);
                    wordCount++;


                }
                docIndex= trainer.docTagtoIndex.get(document.getTag());

                if(trainer.doc2vecAlgo==Doc2VecAlgoType.DBOW)
                    trainDocumentDBOW(trainer.doc_syn0.get(docIndex), filteredSentence, trainer.alpha
                            , trainer.learnHidden, true, trainer.learnWords, trainer);
                else if (trainer.doc2vecAlgo==Doc2VecAlgoType.DM)
                    trainDocumentDM(trainer.doc_syn0.get(docIndex),filteredSentence,trainer.alpha,
                            trainer.learnHidden,true,trainer.learnWords, trainer);
            }
            System.out.println("Read file :"+listFiles.get(currentFileNo).getName()+" currentfileno "+currentFileNo);
            currentFileNo+= trainer.numThreads;
            trainer.fileCount.addAndGet(1);
            trainer.actualWordCount.addAndGet(wordCount);
            updateAlpha(trainer);

        }
//        trainer.actualWordCount.addAndGet(wordCount-lastWordCount);
    }

    public void updateAlpha(Doc2VecTrainer trainer)
    {
        long currentActual=trainer.fileCount.get();
        lastWordCount=wordCount;
        trainer.alpha=trainer.initialLearningRate*Math.max(1.0-((double)currentActual/((double)(trainer.iterations*listFiles.size())))
                ,0.0001);
    }

    public void trainDocumentDBOW(double [] docsyn,ArrayList<String> content,double alpha ,boolean learnHidden, boolean learnVectors, boolean learnWords, Doc2VecTrainer trainer)
    {
        int count=0;
        int max_line_words= Integer.parseInt(ApplicationProperties.getProperty("MAX_LINE_WORDS"));
        if(learnWords)
        {
            while(content.size()>count+max_line_words)
            {
                trainWordsSG(content.subList(count,count+max_line_words),alpha, learnHidden, learnWords, trainer);
                count+=max_line_words;
            }
            trainWordsSG(content.subList(count,content.size()),alpha, learnHidden, learnWords, trainer);
        }
        for(String word: content)
        {
            trainWord(word,docsyn,alpha,learnHidden,learnVectors, trainer);
        }
    }

    public void trainDocumentDM(double [] docsyn, ArrayList<String> content,double alpha , boolean learnHidden, boolean learnVectors, boolean learnWords, Doc2VecTrainer trainer)
    {

        int length=content.size();
        String word;
        int currWindow,wordPosinSent;
        double f,g,sum;
        int l1,l2;
//        for(int i=0;i<length;i++)
//        {
//            wordIndices[i]=trainer.word2index.get(sentence.get(i));
//            huffmanNodes[i]=trainer.huffmanNodeMap.get(sentence.get(i));
//        }
        double [] vector = new double[trainer.layerSize];
        int count;
        for(int currPos=0;currPos<length;currPos++)
        {
            currWindow=(trainer.generator.nextInt()%trainer.window+trainer.window)%trainer.window;
            for(int i=0;i<trainer.layerSize;i++)vector[i]=0.0d;
            count=0;
            for(int a=currWindow;a<trainer.window*2+1-currWindow;a++)
            {
                if(a==trainer.window)continue;
                wordPosinSent=a-trainer.window+currPos;
                if(wordPosinSent<0||wordPosinSent>=length)continue;
                l1=trainer.word2index.get(content.get(wordPosinSent));

                count++;
                for(int j=0;j<trainer.layerSize;j++)
                {
                    vector[j]+=trainer.syn0[l1][j];
                }
            }
            for(int j=0;j<trainer.layerSize;j++)
            {
                vector[j]+=docsyn[j];
            }
            count++;
            if(trainer.cbow_Mean && count>0)
            {
                for(int j=0;j<trainer.layerSize;j++)
                {
                    vector[j]/=(double)count;
                }
            }
            trainWord(content.get(currPos),vector,alpha,learnHidden,false, trainer);
            if(!trainer.cbow_Mean && count>0)
            {
                for(int j=0;j<trainer.layerSize;j++)
                {
                    neule[j]/=(double)count;
                }
            }
            if(learnWords)
            {

                for(int a=currWindow;a<trainer.window*2+1-currWindow;a++)
                {
                    if(a==trainer.window)continue;
                    wordPosinSent=a-trainer.window+currPos;
                    if(wordPosinSent<0||wordPosinSent>=length)continue;
                    l1=trainer.word2index.get(content.get(wordPosinSent));

                    sum=0.0;
                    for(int j=0;j<trainer.layerSize;j++)
                    {
                        trainer.syn0[l1][j]+= neule[j];
                        sum+=trainer.syn0[l1][j]*trainer.syn0[l1][j];
                    }
                    //                for(int j=0;j<trainer.layerSize;j++)
                    //                {
                    //                    if(sum!=0.0)trainer.syn0[l1][j]/=Math.sqrt(sum);
                    //                }
                    //                System.out.println("syno l1:"+l1+" word:"+trainer.index2Word.get(l1)+Arrays.toString(trainer.syn0[l1]));
                }

            }
            if(learnVectors)
            {
                for(int j=0;j<trainer.layerSize;j++)
                {
                    docsyn[j]+=neule[j];
                }
            }
        }
    }

    public double[] inferDocument(DocBean docBean, double alpha, double minAlpha, int steps, Doc2VecTrainer trainer)
    {
        double []docVector= new double[trainer.layerSize];
        ArrayList<String> filteredSentence;
        double wordProbab;
        for(int i=0;i<steps;i++)
        {
            filteredSentence= new ArrayList<String>();
            for(String word:docBean.getContent())
            {
                if(!trainer.huffmanNodeMap.containsKey(word))
                {
                    continue;
                }
                if(trainer.downSampleRate>0.0d)
                {
                    HuffmanNode huffmanNode= trainer.huffmanNodeMap.get(word);
                    wordProbab=Math.sqrt(((double)huffmanNode.count/(trainer.downSampleRate*trainer.numTrainedTokens))+1.0);
                    wordProbab*=(trainer.downSampleRate*trainer.numTrainedTokens)/huffmanNode.count;
                    if(wordProbab<trainer.generator.nextDouble())continue;
                }
                filteredSentence.add(word);


            }

            if(trainer.doc2vecAlgo==Doc2VecAlgoType.DBOW)
                trainDocumentDBOW(docVector,filteredSentence,alpha,false,true,false, trainer);
            else if(trainer.doc2vecAlgo==Doc2VecAlgoType.DM)
                trainDocumentDM(docVector,filteredSentence,alpha,false,true,false, trainer);

            alpha= ((alpha-minAlpha)/(double)(steps-i) )+minAlpha;
        }
        return docVector;
    }
}