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
    boolean learnWords;
    boolean learnHidden;
    boolean learnVectors;
    boolean cbow_Mean;
    Doc2VecAlgoType doc2VecAlgoType;
    Word2VecAlgoType word2VecAlgoType;

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

    public NeuralNetworkConfig setLearnHidden(boolean learnHidden)
    {
        this.learnHidden=learnHidden;
        return this;
    }

    public NeuralNetworkConfig setLearnWords(boolean learnWords)
    {
        this.learnWords=learnWords;
        return this;
    }

    public NeuralNetworkConfig setLearnVectors(boolean learnVectors)
    {
        this.learnVectors= learnVectors;
        return this;
    }

    public NeuralNetworkConfig setDoc2VecAlgoType(Doc2VecAlgoType doc2VecAlgoType)
    {
        this.doc2VecAlgoType= doc2VecAlgoType;
        return this;
    }

    public NeuralNetworkConfig setWord2VecAlgoType(Word2VecAlgoType word2VecAlgoType)
    {
        this.word2VecAlgoType= word2VecAlgoType;
        return this;
    }

    public NeuralNetworkConfig setCbowMean(boolean cbow_Mean)
    {
        this.cbow_Mean= cbow_Mean;
        return this;
    }
}