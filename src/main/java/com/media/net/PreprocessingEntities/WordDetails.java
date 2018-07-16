package com.media.net.PreprocessingEntities;

import com.media.net.Utils.ApplicationProperties;
import org.apache.log4j.Logger;
import org.slf4j.impl.Log4jLoggerAdapter;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;


/**
 * Created by vibhor.go on 01/19/17.
 */

public class WordDetails
{
    public static HashMap<String, Integer> vocabulary=null;
    public static ArrayList<String> index2Word=null;
    public static HashMap<String,Integer> word2index=null;
    public static Map<String,HuffmanNode> huffmanNodeMap=null;
    private static Logger logger = Logger.getLogger(WordDetails.class.getName());
    public static Integer maxWordFreq =0;

    public static void buildVocab(String writeDirpath)
    {
        vocabulary=VocabBuilder.buildVocab(writeDirpath);
        sortVocab();
    }

    public static void updateVocab(String writeDirPath)
    {
        vocabulary=VocabBuilder.updateVocab(vocabulary,writeDirPath);
        if(index2Word!=null){
            HashSet<String> hashSet= new HashSet<String>(index2Word);

            for(String key:vocabulary.keySet())
            {
                if(!hashSet.contains(key)){
                    index2Word.add(key);
                    word2index.put(key,index2Word.size()-1);
                }
            }
        }
        else sortVocab();
    }

    public static void sortVocab()
    {
        index2Word= new ArrayList<String>();
        word2index=new HashMap<String, Integer>();
        Comparator<Entry<String, Integer>> valueComparator = new Comparator<Entry<String,Integer>>() {
            @Override public int compare(Entry<String, Integer> e1, Entry<String, Integer> e2)
            {
                Integer v1 = e1.getValue();
                Integer v2 = e2.getValue();
                return v2.compareTo(v1);
            }
        };
        List<Entry<String, Integer>> listOfEntries = new ArrayList<Entry<String, Integer>>(vocabulary.entrySet());
        Collections.sort(listOfEntries, valueComparator);
        for(Entry<String,Integer> entry:listOfEntries)
        {
           index2Word.add(entry.getKey());
           word2index.put(entry.getKey(),index2Word.size()-1);
        }
        logger.info("Sorted vocabulary and created index2word and word2index maps");
    }

    public static void finalizeVocabDetails(String writeDirPath)
    {
        updateVocab(writeDirPath);
        sortVocab();
        huffmanNodeMap=HuffmanEncoding.encode(vocabulary,index2Word);

    }

    public static void createHuffmanMap()
    {
        huffmanNodeMap=HuffmanEncoding.encode(vocabulary,index2Word);
    }

    public static void serializeWordDetails()
    {
        logger.info("vocabulary size:" + vocabulary.size());
        logger.info("index2word size:"+index2Word.size());
        logger.info("index2word first:"+index2Word.get(0)+", count:"+vocabulary.get(index2Word.get(0)));
        logger.info("index2word last:"+index2Word.get(index2Word.size()-1)+", count:"+vocabulary.get(index2Word.get(index2Word.size()-1)));
        logger.info("word2index first:"+index2Word.get(0)+", has word index:"+word2index.get(index2Word.get(0)));
        logger.info("word2index last:" + index2Word.get(index2Word.size() - 1) + ", has word index:" + word2index.get(index2Word.get(index2Word.size() - 1)));
        logger.info("huffman map size:" + huffmanNodeMap.size());
        logger.info("huffman node for word:" + index2Word.get(0));
        huffmanNodeMap.get(index2Word.get(0)).printHuffmanNode();
        logger.info("huffman node for word:"+index2Word.get(index2Word.size()-1));
        huffmanNodeMap.get(index2Word.get(index2Word.size()-1)).printHuffmanNode();
        try{
            FileOutputStream fos = new FileOutputStream(ApplicationProperties.getProperty("WORD_DETAILS_FILE"));

            ObjectOutputStream outputStream = new ObjectOutputStream(fos);
            outputStream.writeObject(vocabulary);
            outputStream.writeObject(index2Word);
            outputStream.writeObject(word2index);
            outputStream.writeObject(huffmanNodeMap);
            outputStream.close();
        }
        catch (IOException ioex)
        {
            ioex.printStackTrace();
        }
    }

    public static void deserializeWordDetails(String fileName)
    {
        try{
            FileInputStream fis = new FileInputStream(fileName);
            ObjectInputStream inputStream = new ObjectInputStream(fis);
            vocabulary=(HashMap<String, Integer>)inputStream.readObject();
            index2Word=(ArrayList<String>)inputStream.readObject();
            word2index=(HashMap<String, Integer>)inputStream.readObject();
            huffmanNodeMap=(Map<String,HuffmanNode>)inputStream.readObject();
            inputStream.close();
            for(Integer freq : vocabulary.values())
            {
                if(freq>maxWordFreq)maxWordFreq = freq;
            }
            logger.info("vocabulary size:"+vocabulary.size());
            logger.info("index2word size:"+index2Word.size());
            logger.info("index2word first:"+index2Word.get(0)+", count:"+vocabulary.get(index2Word.get(0)));
            logger.info("index2word last:"+index2Word.get(index2Word.size()-1)+", count:"+vocabulary.get(index2Word.get(index2Word.size()-1)));
            logger.info("word2index first:"+index2Word.get(0)+", has word index:"+word2index.get(index2Word.get(0)));
            logger.info("word2index last:"+index2Word.get(index2Word.size()-1)+", has word index:"+word2index.get(index2Word.get(index2Word.size()-1)));
            logger.info("huffman map size:"+huffmanNodeMap.size());
            logger.info("huffman node for word:"+index2Word.get(2));
            huffmanNodeMap.get(index2Word.get(0)).printHuffmanNode();
            logger.info("huffman node for word:"+index2Word.get(index2Word.size()-1));
            huffmanNodeMap.get(index2Word.get(index2Word.size()-1)).printHuffmanNode();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

    public static HashMap<String, Integer> getTermListforContent(String sentence) throws Exception
    {
        ArrayList<String> sentenceTokens= VocabBuilder.tokenize(sentence);
        Integer wordFound = 0;
        HashMap<String, Integer> termList =new HashMap<String, Integer>();
        for(String word:sentenceTokens)
        {
            if(word2index.containsKey(word))
            {
                wordFound++;
                termList.put(word,vocabulary.get(word));
            }
        }
        if ((wordFound==0))throw new Exception("Could not create termlist. None of the words are in the vocabulary.");
        return termList;
    }


}