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
 * Created by vibhor.go on 03/29/17.
 */

public class Doc2VecWorker extends NeuralNetWorker implements Callable<Void>
{

    public Doc2VecWorker(int workerNo,File[] listFiles,NeuralNetworkTrainer networkTrainer)
    {
        super(workerNo,listFiles,networkTrainer);
        neule= new double[trainer.layerSize];
    }


    public void run()
    {

    }

}