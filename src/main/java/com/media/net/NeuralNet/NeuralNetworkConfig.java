package com.media.net.NeuralNet;


import com.media.net.PreprocessingEntities.HuffmanNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by vibhor.go on 01/24/17.
 */

public class NeuralNetworkConfig
{
    int numThreads;
    int iterations;
    int layerSize;
    int window;
    int negativeSamples;
    double downSampleRate;
    double initialLearningRate;

    public NeuralNetworkConfig(int numThreads,
                        int iterations,
                        int layerSize,
                        int windowSize,
                        int negativeSamples,
                        double downSampleRate,
                        double initialLearningRate)
    {
        this.numThreads=numThreads;
        this.iterations=iterations;
        this.layerSize=layerSize;
        this.window=windowSize;
        this.negativeSamples=negativeSamples;
        this.downSampleRate=downSampleRate;
        this.initialLearningRate=initialLearningRate;
    }

    public NeuralNetworkConfig(){

    }

    public NeuralNetworkConfig setNumThreads(int numThreads) {
        this.numThreads = numThreads;
        return this;
    }

    public NeuralNetworkConfig setIterations(int iterations) {
        this.iterations = iterations;
        return this;
    }

    public NeuralNetworkConfig setLayerSize(int layerSize) {
        this.layerSize = layerSize;
        return this;
    }

    public NeuralNetworkConfig setWindow(int window) {
        this.window = window;
        return this;
    }

    public NeuralNetworkConfig setNegativeSamples(int negativeSamples) {
        this.negativeSamples = negativeSamples;
        return this;
    }

    public NeuralNetworkConfig setDownSampleRate(double downSampleRate) {
        this.downSampleRate = downSampleRate;
        return this;
    }

    public NeuralNetworkConfig setInitialLearningRate(double initialLearningRate) {
        this.initialLearningRate = initialLearningRate;
        return this;
    }
}