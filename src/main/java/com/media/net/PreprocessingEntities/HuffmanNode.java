package com.media.net.PreprocessingEntities;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


/**
 * Created by vibhor.go on 01/19/17.
 */

public class HuffmanNode implements Serializable
{
    public final byte[] code;
    public final int[] points;
    public final int idx;
    public final int count;

    protected HuffmanNode(byte[] code, int[] points, int idx, int count) {
        this.code = code;
        this.points = points;
        this.idx = idx;
        this.count = count;
    }

    protected void printHuffmanNode()
    {
        System.out.println("code: "+ Arrays.toString(code));
        System.out.println("points: " + Arrays.toString(points));
        System.out.println("index: "+idx);
        System.out.println("count: "+count);
    }

}