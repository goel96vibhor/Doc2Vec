package com.media.net.NeuralNet;

import com.media.net.DataPreparation.ShortFileReader;
import com.media.net.PreprocessingEntities.HuffmanNode;
import com.media.net.Utils.ApplicationProperties;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by vibhor.go on 01/24/17.
 */

public class NeuralNetWorker implements Callable<Void>
{
    protected static final int LEARNING_RATE_UPDATE_FREQUENCY = 10000;
    int workerNo;
    ArrayList<File> listFiles;
    private NeuralNetworkTrainer trainer;
    int wordCount=0;
    int lastWordCount=0;
    int[] wordIndices= new int[Integer.parseInt(ApplicationProperties.getProperty("MAX_LINE_WORDS"))];
    HuffmanNode[] huffmanNodes= new HuffmanNode[Integer.parseInt(ApplicationProperties.getProperty("MAX_LINE_WORDS"))];
    double [] neule;
    public NeuralNetWorker(int workerNo,ArrayList<File> listFiles,NeuralNetworkTrainer networkTrainer)
    {
        this.workerNo=workerNo;
        this.listFiles=listFiles;
        this.trainer=networkTrainer;
        neule= new double[trainer.layerSize];
    }

    public NeuralNetWorker(int workerNo,ArrayList<File> listFiles)
    {
        this.workerNo=workerNo;
        this.listFiles=listFiles;
//        neule= new double[trainer.layerSize];
    }

    public NeuralNetWorker()
    {

    }

    @Override
    public Void call() throws Exception {
        run(trainer);
        return null;
    }

    public void run(NeuralNetworkTrainer trainer)
    {
        int currentFileNo=workerNo;
        List<ArrayList<String>> fileTokens= null;
        ArrayList<String> filteredSentence=null;
        double wordProbab;
//        for(int i=0;i<listFiles.length;i++)
//        System.out.println("filename:"+listFiles[i].getName());
        ShortFileReader shortFileReader= new ShortFileReader();
        while (currentFileNo<listFiles.size())
        {
            fileTokens= shortFileReader.readTokensfromResource(listFiles.get(currentFileNo));

            for(ArrayList<String> sentence:fileTokens)
            {
                filteredSentence=new ArrayList<String>();
                for(String word:sentence)
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
//                       if(wordProbab<trainer.generator.nextDouble())continue;
                    }
                    filteredSentence.add(word);
                    wordCount++;
                    if(wordCount-lastWordCount>LEARNING_RATE_UPDATE_FREQUENCY)
                    {
                        updateAlpha(trainer);
                        //System.out.println("updating alpha");
                    }

                }
                if(trainer.word2VecAlgo==Word2VecAlgoType.SG)
                    trainWordsSG(filteredSentence,trainer.alpha ,true, true,trainer);
                else
                    trainWordsCBOW(filteredSentence,trainer.alpha,true,true, trainer);
//                System.out.println("syn for 1000th word :" + Arrays.toString(trainer.syn1[510844]));
            }
            System.out.println("Read file :"+listFiles.get(currentFileNo).getName()+" currentfileno "+currentFileNo);
//            System.out.println("syn for 1000th word :" + Arrays.toString(trainer.syn0[0]));
            currentFileNo+= trainer.numThreads;
            trainer.fileCount.addAndGet(1);

        }
        trainer.actualWordCount.addAndGet(wordCount-lastWordCount);
    }

    public void updateAlpha(NeuralNetworkTrainer trainer)
    {
        long currentActual=trainer.actualWordCount.addAndGet(wordCount-lastWordCount);
        lastWordCount=wordCount;
        trainer.alpha=trainer.initialLearningRate*Math.max(1.0-((double)currentActual/((double)(trainer.iterations*trainer.numTrainedTokens)))
                ,0.0001);
    }

    public void trainWordsSG(List<String> sentence,double alpha ,boolean learnHidden, boolean learnVectors,NeuralNetworkTrainer trainer)
    {
        int length=sentence.size();
        String word;
        int currWindow,wordPosinSent;
        double f,g,sum;
        int l1,l2;
        for(int i=0;i<length;i++)
        {
            wordIndices[i]=trainer.word2index.get(sentence.get(i));
            huffmanNodes[i]=trainer.huffmanNodeMap.get(sentence.get(i));
        }
        for(int currPos=0;currPos<length;currPos++)
        {
            currWindow=(trainer.generator.nextInt()%trainer.window+trainer.window)%trainer.window;
            for(int a=currWindow;a<trainer.window*2+1-currWindow;a++)
            {
                if(a==trainer.window)continue;
                wordPosinSent=a-trainer.window+currPos;
                if(wordPosinSent<0||wordPosinSent>=length)continue;
                l1=wordIndices[wordPosinSent];
                for(int i=0;i<trainer.layerSize;i++)
                {
                    neule[i]=0;
                }

                for(int i=0;i<huffmanNodes[currPos].code.length;i++)
                {
                    f=0.0;
                    l2=huffmanNodes[currPos].points[i];
                    for(int j=0;j<trainer.layerSize;j++)
                    {
                        f+=trainer.syn0[l1][j]*trainer.syn1[l2][j];
                    }
                    if(f<=-trainer.MAX_EXP)continue;//f=-(trainer.MAX_EXP-1e-8);
                    if(f>=trainer.MAX_EXP)continue;//f=(trainer.MAX_EXP-1e-8);

                    f=NeuralNetworkTrainer.exp_table
                            [(int)((f+trainer.MAX_EXP)*NeuralNetworkTrainer.EXP_TABLE_SIZE/(2*NeuralNetworkTrainer.MAX_EXP))];
                    g=(1.0-huffmanNodes[currPos].code[i]-f)*alpha;
                    for(int j=0;j<trainer.layerSize;j++)
                    {
                        neule[j]+=g*trainer.syn1[l2][j];
                    }
                    sum=0.0;
                    for(int j=0;j<trainer.layerSize;j++)
                    {
                        if (learnHidden)trainer.syn1[l2][j]+=g*trainer.syn0[l1][j];
                        sum+=trainer.syn1[l2][j]*trainer.syn1[l2][j];
                    }
//                    for(int j=0;j<trainer.layerSize;j++)
//                    {
//                        if(sum!=0.0)trainer.syn1[l2][j]/=Math.sqrt(sum);
//                    }
//                    System.out.println("f:"+f+" g:"+g);
//                    System.out.println("syno l2:"+l2+" word:"+trainer.index2Word.get(l2)+Arrays.toString(trainer.syn1[l2]));
                }
                handleNegativeSampling(huffmanNodes[currPos], trainer.syn0[l1],alpha,learnHidden, trainer);
                sum=0.0;
                for(int j=0;j<trainer.layerSize;j++)
                {
                    if (learnVectors)trainer.syn0[l1][j]+= neule[j];
                    sum+=trainer.syn0[l1][j]*trainer.syn0[l1][j];
                }
//                for(int j=0;j<trainer.layerSize;j++)
//                {
//                    if(sum!=0.0)trainer.syn0[l1][j]/=Math.sqrt(sum);
//                }
//                System.out.println("syno l1:"+l1+" word:"+trainer.index2Word.get(l1)+Arrays.toString(trainer.syn0[l1]));
            }
        }
    }

    public void handleNegativeSampling(HuffmanNode huffmanNode, double [] neul,double alpha ,boolean learnHidden,NeuralNetworkTrainer trainer)
    {
        int target, label;
        int l2;
        double f,g,sum;
        for(int i=0;i<trainer.negativeSamples+1;i++)
        {
            if(i==0)
            {
                target=huffmanNode.idx;
                label=1;
            }
            else
            {
                target=trainer.table[((trainer.generator.nextInt()%trainer.TABLE_SIZE)+trainer.TABLE_SIZE)%trainer.TABLE_SIZE];
                if(target==0)
                {
                    target=trainer.table[((trainer.generator.nextInt()%(trainer.vocabSize-1))+trainer.vocabSize-1)%(trainer.vocabSize-1)]+1;
                }
                if(target==huffmanNode.idx)continue;
                label=0;
            }
            l2=target;

            f=0;
            for(int j=0;j<trainer.layerSize;j++)
            {
                f+=neul[j]*trainer.synNeg[l2][j];
            }
            if(f>trainer.MAX_EXP)g=label-1;
            else if(f<-trainer.MAX_EXP)g=label-0;

            else g=label-trainer.exp_table[(int)((f+trainer.MAX_EXP)*NeuralNetworkTrainer.EXP_TABLE_SIZE/(2*NeuralNetworkTrainer.MAX_EXP))];
            g*=alpha;
            for(int j=0;j<trainer.layerSize;j++)
            {
                neule[j]+=g*trainer.synNeg[l2][j];
            }
            sum=0.0;
            for (int j=0;j<trainer.layerSize;j++)
            {
                if (learnHidden)trainer.synNeg[l2][j]+=g*neul[j];
                sum+=trainer.synNeg[l2][j]*trainer.synNeg[l2][j];
            }
//            for (int j=0;j<trainer.layerSize;j++)
//            {
//                if(sum!=0.0)trainer.synNeg[l2][j]/=Math.sqrt(sum);
//            }
        }

    }

    public void trainWord(String word, double[] vector,double alpha ,boolean learnHidden, boolean learnVectors,NeuralNetworkTrainer trainer)
    {
        int index;
        double f,g,sum;
        int l1,l2;
//        double [] neule = new double[trainer.layerSize];
        if(vector.length==trainer.layerSize && trainer.word2index.containsKey(word))
        {
            HuffmanNode huffmanNode= trainer.huffmanNodeMap.get(word);
            index= trainer.word2index.get(word);
            for(int i=0;i<trainer.layerSize;i++)
            {
                neule[i]=0;
            }
            for(int i=0;i<huffmanNode.code.length;i++)
            {
                f=0.0;
                l2=huffmanNode.points[i];
                for(int j=0;j<trainer.layerSize;j++)
                {
                    f+=vector[j]*trainer.syn1[l2][j];
                }
                if(f<=-trainer.MAX_EXP)continue;//f=-(trainer.MAX_EXP-1e-8);
                if(f>=trainer.MAX_EXP)continue;//f=(trainer.MAX_EXP-1e-8);

                f=NeuralNetworkTrainer.exp_table
                        [(int)((f+trainer.MAX_EXP)*NeuralNetworkTrainer.EXP_TABLE_SIZE/(2*NeuralNetworkTrainer.MAX_EXP))];
                g=(1.0-huffmanNode.code[i]-f)*alpha;
                for(int j=0;j<trainer.layerSize;j++)
                {
                    neule[j]+=g*trainer.syn1[l2][j];
                }
                sum=0.0;
                for(int j=0;j<trainer.layerSize;j++)
                {
                    if (learnHidden)trainer.syn1[l2][j]+=g*vector[j];
                    sum+=trainer.syn1[l2][j]*trainer.syn1[l2][j];
                }
//                    for(int j=0;j<trainer.layerSize;j++)
//                    {
//                        if(sum!=0.0)trainer.syn1[l2][j]/=Math.sqrt(sum);
//                    }
//                    System.out.println("f:"+f+" g:"+g);
//                    System.out.println("syno l2:"+l2+" word:"+trainer.index2Word.get(l2)+Arrays.toString(trainer.syn1[l2]));
            }
            handleNegativeSampling(huffmanNode, vector,alpha,learnHidden, trainer);
            sum=0.0;
            for(int j=0;j<trainer.layerSize;j++)
            {
                if (learnVectors)vector[j]+= neule[j];
                sum+=vector[j]*vector[j];
            }
//                for(int j=0;j<trainer.layerSize;j++)
//                {
//                    if(sum!=0.0)trainer.syn0[l1][j]/=Math.sqrt(sum);
//                }

        }
    }

    public void trainWordsCBOW(ArrayList<String> sentence,double alpha , boolean learnHidden, boolean learnVectors,NeuralNetworkTrainer trainer)
    {
        int length=sentence.size();
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
                l1=trainer.word2index.get(sentence.get(wordPosinSent));

                count++;
                for(int j=0;j<trainer.layerSize;j++)
                {
                    vector[j]+=trainer.syn0[l1][j];
                }
            }
            if(trainer.cbow_Mean && count>0)
            {
                for(int j=0;j<trainer.layerSize;j++)
                {
                    vector[j]/=(double)count;
                }
            }
            trainWord(sentence.get(currPos),vector,alpha,learnHidden,false, trainer);
            if(learnVectors)
            {
                if(!trainer.cbow_Mean && count>0)
                {
                    for(int j=0;j<trainer.layerSize;j++)
                    {
                        neule[j]/=(double)count;
                    }
                }
                for(int a=currWindow;a<trainer.window*2+1-currWindow;a++)
                {
                    if(a==trainer.window)continue;
                    wordPosinSent=a-trainer.window+currPos;
                    if(wordPosinSent<0||wordPosinSent>=length)continue;
                    l1=trainer.word2index.get(sentence.get(wordPosinSent));

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
        }
    }




}