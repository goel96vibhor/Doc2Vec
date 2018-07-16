package com.media.net;

import com.media.net.Beans.APIResultBean;
import com.media.net.Beans.DocBean;
import com.media.net.NeuralNet.Doc2VecTrainer;
import com.media.net.NeuralNet.Doc2VecWorker;
import com.media.net.PreprocessingEntities.WordDetails;
import com.media.net.Utils.ApplicationProperties;
import com.media.net.Utils.ResultSet;
import com.media.net.Utils.SimilarityAnalyzerEndPointXmlBuilder;
import com.media.net.Utils.UrlDataExtractor;
import org.apache.log4j.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: vibhor
 * Date: 16/1/18
 * Time: 4:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class WordSimilarityAnalyzerEndPoint extends HttpServlet {

    private static final Logger logger = Logger.getLogger(WordSimilarityAnalyzerEndPoint.class);
    private static DecimalFormat df = new DecimalFormat("0.0000");
    private static Random generator= new Random();
    public static  Doc2VecTrainer networkTrainer;
    public static  UrlDataExtractor urlDataExtractor= new UrlDataExtractor();
    public static  Doc2VecWorker doc2VecWorker;
    @Override
    public void init(ServletConfig config) throws ServletException {

        try {
            ApplicationProperties.loadProperties(WordSimilarityAnalyzerEndPoint.class.getResourceAsStream("/application.properties"), true);

            WordDetails.deserializeWordDetails(ApplicationProperties.getProperty("WORD_DETAILS_FILE"));
            long count=0;
            for(int i=0;i<WordDetails.index2Word.size();i++)
            {
                count+=WordDetails.vocabulary.get(WordDetails.index2Word.get(i));
            }
            logger.info("Index2word size :"+WordDetails.index2Word.size());
            logger.info("Sum of word count of all words in training data considered for vocabulary:" + count);
            networkTrainer = Doc2VecTrainer.deserializeNetwork(WordDetails.huffmanNodeMap,
                    WordDetails.index2Word,
                    WordDetails.word2index);

            doc2VecWorker= new Doc2VecWorker(networkTrainer);
            if(WordDetails.maxWordFreq <=0)throw new Exception("Word frequencies not loaded from word details.");

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            throw new ServletException(ex.getMessage());
        }
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        long start = System.currentTimeMillis();
        try {
            String task;
            String url;
            String title;
            String sentence1;
            String sentence2;
            Double similarity;

            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/xml; charset=utf-8");
            PrintWriter out = response.getWriter();
            String word1,word2,negword;
            if(request.getParameter("task")!=null){
                task = (request.getParameter("task"));
            }else {
                return;
            }

            word1 = request.getParameter("word1");
            if(word1 !=null)word1 = word1.trim().toLowerCase();
            word2 = request.getParameter("word2");
            if(word2 !=null)word2 = word2.trim().toLowerCase();
            negword = request.getParameter("negword");
            if(negword !=null)negword = negword.trim().toLowerCase();
            sentence1 = request.getParameter("sentence");
            if(sentence1 !=null)sentence1 = sentence1.trim().toLowerCase();
            sentence2 = request.getParameter("sentence2");
            if(sentence2 !=null)sentence2 = sentence2.trim().toLowerCase();
            url=  request.getParameter("url");

            logger.info("Got request for task :"+task);
            ArrayList<ResultSet> resultSets= new ArrayList<ResultSet>();
            APIResultBean apiResultBean = new APIResultBean();
            if(task.trim().toLowerCase().equals("wordpairsim"))
            {
                apiResultBean.setWord1(word1);
                apiResultBean.setWord2(word2);
                logger.info("Calculating word pair similarity for words:'"+word1+"', '"+word2+"'");
                if(word1== null || word2 == null)
                {
                    apiResultBean.setError("None of the words should be null for word-pair similarity");
                }
                try {
                    similarity = networkTrainer.wordSimilarity(word1, word2);
                    apiResultBean.setSimilarity(similarity);
                    logger.info("Similarity obtained:"+similarity);
                }
                catch (Exception ex)
                {
                    logger.error(ex.getMessage());
                    apiResultBean.setError(ex.getMessage());
                }

                out.print(SimilarityAnalyzerEndPointXmlBuilder.getXMLforWordPairSimlarity(apiResultBean));
            }
            else if(task.trim().toLowerCase().equals("sentpairsim"))
            {
                apiResultBean.setSentence1(sentence1);
                apiResultBean.setSentence2(sentence2);
                logger.info("Calculating sentence pair similarity for sentence:'"+sentence1+"', '"+sentence2+"'");
                if(sentence1== null || sentence2 == null)
                {
                    apiResultBean.setError("None of the sentences should be null for sentence-pair similarity");
                }
                try {
                    similarity = networkTrainer.sentencePairSimilarity(sentence1, sentence2);
                    apiResultBean.setSimilarity(similarity);
                    logger.info("Similarity obtained:"+similarity);
                }
                catch (Exception ex)
                {
                    logger.error(ex.getMessage());
                    apiResultBean.setError(ex.getMessage());
                }

                out.print(SimilarityAnalyzerEndPointXmlBuilder.getXMLforSentencePairSimlarity(apiResultBean));
            }
            else if(task.trim().toLowerCase().equals("simword"))
            {
                apiResultBean.setWord1(word1);
                if(word1== null)
                {
                    apiResultBean.setError("Word should not be null for getting most similar word");
                }
                try {
                    apiResultBean.setResultSets(networkTrainer.mostSimilartoWord(word1));
                    apiResultBean.setVector(networkTrainer.getTVforWord(word1));
                }
                catch (Exception ex)
                {
                    apiResultBean.setError(ex.getMessage());
                }
                out.print(SimilarityAnalyzerEndPointXmlBuilder.getXMLforWordResultSet(apiResultBean));
            }
            else if(task.trim().toLowerCase().equals("simsent"))
            {
                apiResultBean.setContent(sentence1);
                if(sentence1== null)
                {
                    apiResultBean.setError("Content should not be null for getting most similar word");
                }
                try {
                    apiResultBean.setResultSets(networkTrainer.probabWordforSentence(sentence1));
                    apiResultBean.setVector(networkTrainer.getTfWeightedTVforSentence(sentence1));
                }
                catch (Exception ex)
                {
                    apiResultBean.setError(ex.getMessage());
                }
                out.print(SimilarityAnalyzerEndPointXmlBuilder.getXMLforWordResultSet(apiResultBean));
            }
            else if(task.trim().toLowerCase().equals("randdissim"))
            {
                String min1=null, min2=null;double minScore=1,score;
                int posCount=0,negCount=0;
                for(int i=0;i<WordDetails.index2Word.size();i++)
                {
                    for(int j=0;j<100;j++)
                    {
                        int k= (int)(Math.random()*WordDetails.index2Word.size());
                        score= networkTrainer.wordSimilarity(WordDetails.index2Word.get(i),WordDetails.index2Word.get(k));
                        if(score<0)negCount++;
                        else posCount++;
                        if(score<minScore)
                        {
                            minScore=score;
                            min1=WordDetails.index2Word.get(i);
                            min2=WordDetails.index2Word.get(k);
                        }
                    }

                }
                apiResultBean.setWord1(min1);
                apiResultBean.setWord2(min2);
                apiResultBean.setSimilarity(minScore);
                out.print(SimilarityAnalyzerEndPointXmlBuilder.getXMLforRandomDisSimilar(apiResultBean, posCount, negCount));
//                System.out.println(posCount+" "+negCount);

            }
            else if(task.trim().toLowerCase().equals("urltopic"))
            {
                double alpha=0.1;
                double minAlpha= 0.00001;
                if(url== null)
                {
                    apiResultBean.setError("Url should not be null for getting topic");
                }
                DocBean docBean= urlDataExtractor.getDocBeanforUrl(url,-1);
                apiResultBean.setUrl(url);
                apiResultBean.setEntityId(docBean.getTag());
                apiResultBean.setContent(docBean.getContent().toString().replaceAll(","," "));
                apiResultBean.setTitle(docBean.getTitle().toString().replaceAll(","," "));
                double []vector= doc2VecWorker.inferDocument(docBean,alpha,minAlpha,20,networkTrainer);
                try {
                    apiResultBean.setResultSets(networkTrainer.mostSimilarDocumentToVector(vector,10));
                }
                catch (Exception ex)
                {
                    apiResultBean.setError(ex.getMessage());
                }
                alpha=0.1;
                DocBean titleDocBean= new DocBean(-1l,url);
                titleDocBean.setContent(docBean.getTitle());
                titleDocBean.setTitle(docBean.getTitle());
                vector= doc2VecWorker.inferDocument(titleDocBean,alpha,minAlpha,20,networkTrainer);
                try {
                    apiResultBean.setTitleResultSets(networkTrainer.mostSimilarDocumentToVector(vector, 10));
                }
                catch (Exception ex)
                {
                    apiResultBean.setError(ex.getMessage());
                }
                out.print(SimilarityAnalyzerEndPointXmlBuilder.getXMLforUrlResultSet(apiResultBean));
            }
            else if(task.trim().toLowerCase().equals("urlword"))
            {
                double alpha=0.1;
                double minAlpha= 0.00001;
                if(url== null)
                {
                    apiResultBean.setError("Url should not be null for getting topic");
                }
                DocBean docBean= urlDataExtractor.getDocBeanforUrl(url,-1);
                apiResultBean.setUrl(url);
                apiResultBean.setEntityId(docBean.getTag());
                apiResultBean.setContent(docBean.getContent().toString().replaceAll(","," "));
                apiResultBean.setTitle(docBean.getTitle().toString().replaceAll(","," "));
                double []vector= doc2VecWorker.inferDocument(docBean,alpha,minAlpha,20,networkTrainer);
                try {
                    apiResultBean.setResultSets(networkTrainer.mostSimilartoVector(vector,10));
                }
                catch (Exception ex)
                {
                    apiResultBean.setError(ex.getMessage());
                }
                alpha=0.1;
                DocBean titleDocBean= new DocBean(-1l,url);
                titleDocBean.setContent(docBean.getTitle());
                titleDocBean.setTitle(docBean.getTitle());
                vector= doc2VecWorker.inferDocument(titleDocBean,alpha,minAlpha,20,networkTrainer);
                try {
                    apiResultBean.setTitleResultSets(networkTrainer.mostSimilartoVector(vector,10));
                }
                catch (Exception ex)
                {
                    apiResultBean.setError(ex.getMessage());
                }
                out.print(SimilarityAnalyzerEndPointXmlBuilder.getXMLforUrlResultSet(apiResultBean));
            }
            else if(task.trim().toLowerCase().equals("analogy"))
            {
                if(word1 == null || word2==null || negword ==null)
                {
                    apiResultBean.setError("Both positive words and negative words should not be null.");
                }
                apiResultBean.setWord1(word1);
                apiResultBean.setWord1(word2);
                apiResultBean.setNegWord(negword);
                String [] positive ={word1,word2};
                String [] negative={negword};
                try {
                    apiResultBean.setResultSets(networkTrainer.empiricallySimilar(new ArrayList<String>(Arrays.asList(positive)),new ArrayList<String>(Arrays.asList(negative))));
                }
                catch (Exception ex)
                {
                    apiResultBean.setError(ex.getMessage());
                }
                out.print(SimilarityAnalyzerEndPointXmlBuilder.getXMLforAnalogy(apiResultBean));
            }
            else
            {
                apiResultBean.setError("Task passed is not valid.");
                out.print(SimilarityAnalyzerEndPointXmlBuilder.getXMLforError(apiResultBean));
            }


        } catch (Exception e) {
            logger.error("Error in entity processing :: " + e.getMessage());
        }
        finally {
//            StatsReporter.updateTimer("Total_Process_Time" , System.currentTimeMillis() - start);
        }
    }


}
