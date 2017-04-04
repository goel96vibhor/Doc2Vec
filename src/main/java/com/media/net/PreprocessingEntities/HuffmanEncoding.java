package com.media.net.PreprocessingEntities;


import com.google.common.collect.ImmutableMap;
import org.apache.log4j.Logger;

import java.util.*;


/**
 * Created by vibhor.go on 01/19/17.
 */

public class HuffmanEncoding
{
    private static Logger logger = Logger.getLogger(HuffmanEncoding.class.getName());
    public static Map<String,HuffmanNode > encode(HashMap<String, Integer> vocabulary, ArrayList<String> index2Word)
    {
        final int numTokens=vocabulary.size();
        int[] parentNode = new int[numTokens * 2 + 1];
        byte[] binary = new byte[numTokens * 2 + 1];
        long[] count = new long[numTokens * 2 + 1];
        int i = 0;
        for(String word:index2Word)
        {
            count[i]=vocabulary.get(word);
            i++;
        }
        for(i=numTokens;i<count.length;i++)
        {
            count[i]=(long)1e15;
        }
        createHuffmanTree(numTokens,count,parentNode,binary);
        return createHuffmanNodeMap(parentNode,binary,vocabulary,index2Word);
    }

    public static void createHuffmanTree(int numTokens,long [] count,int [] parentNode, byte [] binary)
    {
        int min1index,min2index,pos1,pos2;
        pos1=numTokens-1;pos2=numTokens;
        for(int i=0;i<numTokens;i++)
        {
            if(pos1>=0)
            {
                if(count[pos2]<count[pos1])
                {
                    min1index=pos2;
                    pos2++;
                }
                else
                {
                    min1index=pos1;
                    pos1--;
                }
            }
            else {
                min1index=pos2;pos2++;
            }
            if(pos1>=0)
            {
                if(count[pos2]<count[pos1])
                {
                    min2index=pos2;
                    pos2++;
                }
                else
                {
                    min2index=pos1;
                    pos1--;
                }
            }
            else  {
                min2index=pos2;pos2++;
            }
            int newNodeIndex=numTokens+i;
            count[newNodeIndex]=count[min1index]+count[min2index];
            parentNode[min1index]=newNodeIndex;
            parentNode[min2index]=newNodeIndex;
            binary[min1index]=0;
            binary[min2index]=1;
        }
        System.out.println("created huffman tree");
    }

    public static Map<String,HuffmanNode> createHuffmanNodeMap(int [] parentNode, byte [] binary,HashMap<String, Integer> vocabulary, ArrayList<String> index2Word)
    {
        ArrayList<Byte> codes;
        ArrayList<Integer> points;
        ImmutableMap.Builder<String, HuffmanNode> result = ImmutableMap.builder();
        int numTokens=index2Word.size();
        int currNode;
        int maxDepth=1;
        int currdepth=0;
        for(int i=0;i<index2Word.size();i++)
        {
            codes= new ArrayList<Byte>();
            points= new ArrayList<Integer>();
            currNode=i;
            currdepth=1;
            while(true)
            {
                codes.add(binary[currNode]);
                points.add(parentNode[currNode]);
                currdepth++;
                currNode=parentNode[currNode];
                if(currNode==numTokens*2-2)break;
            }
            int codelen=codes.size();
            if(maxDepth<codelen+1)maxDepth=codelen+1;
            final int count = vocabulary.get(index2Word.get(i));
            final byte[] rawCode = new byte[codelen];
            final int[] rawPoints = new int[codelen + 1];
            for(int j=0;j<codelen;j++)
            {
                rawCode[j]=codes.get(codelen-j-1);
                rawPoints[j]=points.get(codelen-j-1)-numTokens;
            }
            rawPoints[codelen]=i-numTokens;
            result.put(index2Word.get(i),new HuffmanNode(rawCode,rawPoints,i,count));
//            System.out.println(maxDepth);
        }
        logger.info("Created huffman tree with max depth:"+maxDepth+" having node count:"+(numTokens*2-1));
        return result.build();
    }
}