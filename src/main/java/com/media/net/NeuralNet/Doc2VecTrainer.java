package com.media.net.NeuralNet;


import com.media.net.PreprocessingEntities.HuffmanNode;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vibhor.go on 01/23/17.
 */

public class Doc2VecTrainer extends NeuralNetworkTrainer
{
    double [][] doctag_syn0;
    public Doc2VecTrainer(Map<String,HuffmanNode> huffmanNodeMap,ArrayList<String> index2Word,
                                HashMap<String,Integer> word2index,NeuralNetworkConfig networkConfig)
    {
        super(huffmanNodeMap,index2Word,word2index,networkConfig);

    }

    protected void initializeWeights()
    {
        for(int i=0;i<vocabSize;i++)
        {
            generator.nextDouble();
            for (int j=0;j<layerSize;j++)
            {

                doctag_syn0[i][j]=(generator.nextDouble()-0.5)/layerSize;
            }
        }
        super.initializeWeights();
    }


    public NeuralNetWorker createWorker(int workerNo,File[] listFiles,NeuralNetworkTrainer networkTrainer)
    {
        return new Doc2VecWorker(workerNo,listFiles,this);
    }

}