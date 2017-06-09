package com.media.net.DataPreparation;

import com.media.net.Beans.DocBean;
import com.media.net.PreprocessingEntities.VocabBuilder;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by vibhor.go on 01/18/17.
*/

public class ShortFileReader
{
    private static Logger logger = Logger.getLogger(ShortFileReader.class.getName());
    public List<String> readResource(File filename)
    {
        BufferedReader reader;
        List<String> sentences = null;
        try {
            reader= new BufferedReader(new FileReader(filename));
            sentences= new ArrayList<String>();
            String line=null;
            while((line=reader.readLine())!=null)
            {
                sentences.add(line);
            }
            reader.close();
            sentences= Collections.unmodifiableList(sentences);
        }
        catch (IOException ex)
        {

            ex.printStackTrace();
        }
        finally {
            logger.info("read file: "+filename.getName()+" having "+sentences.size()+" sentences.");
            return sentences;

        }

    }

    public List<ArrayList<String>> readTokensfromResource(File fileName)
    {
        List<String> sentences= readResource(fileName);
        List<ArrayList<String>> tokens= new ArrayList<ArrayList<String>>();
        for(String sentence:sentences)
        {
            tokens.add(VocabBuilder.tokenize(sentence));
        }
        return tokens;
    }

    public ArrayList<DocBean> readDocBeansfromResource(File fileName)
    {
        ArrayList<DocBean> docBeans=new ArrayList<DocBean>();
        BufferedReader reader;
        DocBean docBean;
        ArrayList<String> docTokens = null;
        ArrayList<String> lineTokens=null;

        try {
            FileInputStream fis = new FileInputStream(fileName);
            List<InputStream> streams =
                    Arrays.asList(
                            new ByteArrayInputStream("<root>".getBytes()),
                            fis,
                            new ByteArrayInputStream("</root>".getBytes()));
            InputStream cntr =
                    new SequenceInputStream(Collections.enumeration(streams));
            DocumentBuilderFactory dbFactory
                    = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document document = dBuilder.parse(cntr);
            NodeList nodeList = document.getElementsByTagName("doc");
            Node node;
            String content;
            Element docElement ;
            for(int i=0;i<nodeList.getLength();i++)
            {
                node= nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    docElement= (Element) node;
                    docBean= new DocBean(Long.parseLong(docElement.getAttribute("id")),docElement.getAttribute("url"));
                    content= docElement.getTextContent();
                    reader= new BufferedReader(new StringReader(content));
                    String line=null;
                    docTokens= new ArrayList<String>();
                    while((line=reader.readLine())!=null)
                    {
                        lineTokens= VocabBuilder.tokenize(line);
                        if (docBean.getTitle()==null){
                            if (lineTokens!=null&& lineTokens.size()!=0)docBean.setTitle(lineTokens);
                        }
                        if (lineTokens!=null&& lineTokens.size()!=0){
                            docTokens.addAll(lineTokens);
                        }
                    }
                    reader.close();
                    docBean.setContent(docTokens);
                    docBeans.add(docBean);
                }
            }

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally {
            return docBeans;
        }
    }

    public static void main(String args[])
    {
        File file= new File("/home/vibhor/Documents/word2vec/extractedfull/processedwikicontent/AJ/wiki_16");
        ShortFileReader shortFileReader= new ShortFileReader();
        List<DocBean> docBeans=  shortFileReader.readDocBeansfromResource(file);
        for(DocBean docBean: docBeans)
        {
            System.out.println("docid: "+docBean.getTag());
            System.out.println("url: "+docBean.getUrl());
            System.out.println("title: "+docBean.getTitle());
            System.out.println("content: "+docBean.getContent());

        }
        System.out.println(docBeans.size());
    }


}
