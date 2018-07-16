package com.media.net.Utils;

import com.media.net.Beans.APIResultBean;
import com.media.net.PreprocessingEntities.WordDetails;
import org.apache.log4j.Logger;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: vibhor
 * Date: 16/1/18
 * Time: 8:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class SimilarityAnalyzerEndPointXmlBuilder {

    private static final Logger logger = Logger.getLogger(SimilarityAnalyzerEndPointXmlBuilder.class);
    private static DecimalFormat df = new DecimalFormat("0.0000");

    public static String getXMLforWordPairSimlarity(APIResultBean apiResultBean) {

        StringBuilder buffer = new StringBuilder();
        logger.info("Trying to create xml..");
        buffer.append("<ROOT>");

        buffer.append("<Word>").append("<![CDATA[").append(apiResultBean.getWord1()).append("]]>").append("</Word>");
        buffer.append("<Word>").append("<![CDATA[").append(apiResultBean.getWord2()).append("]]>").append("</Word>");
        buffer.append("<similarity>").append(df.format(apiResultBean.getSimilarity())).append("</similarity>");


        buffer.append("<error><![CDATA[").append(apiResultBean.getError()).append("]]></error>");
        buffer.append("</ROOT>");
        StringBuilder xmlBuffer = new StringBuilder();
        xmlBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xmlBuffer.append("<RESULT>");
        xmlBuffer.append(buffer.toString()) ;
        xmlBuffer.append("</RESULT>");
        return xmlBuffer.toString();
    }

    public static String getXMLforSentencePairSimlarity(APIResultBean apiResultBean) {

        StringBuilder buffer = new StringBuilder();
        logger.info("Trying to create xml..");
        buffer.append("<ROOT>");

        buffer.append("<Sentence>").append("<![CDATA[").append(apiResultBean.getSentence1()).append("]]>").append("</Sentence>");
        buffer.append("<Sentence>").append("<![CDATA[").append(apiResultBean.getSentence2()).append("]]>").append("</Sentence>");
        buffer.append("<similarity>").append(df.format(apiResultBean.getSimilarity())).append("</similarity>");


        buffer.append("<error><![CDATA[").append(apiResultBean.getError()).append("]]></error>");
        buffer.append("</ROOT>");
        StringBuilder xmlBuffer = new StringBuilder();
        xmlBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xmlBuffer.append("<RESULT>");
        xmlBuffer.append(buffer.toString()) ;
        xmlBuffer.append("</RESULT>");
        return xmlBuffer.toString();
    }

    public static String getXMLforWordResultSet(APIResultBean apiResultBean) {

        StringBuilder buffer = new StringBuilder();
        logger.info("Trying to create xml..");
        buffer.append("<ROOT>");
        String terms;

        buffer.append("<Word>").append("<![CDATA[").append(apiResultBean.getWord1()).append("]]>").append("</Word>");
        buffer.append("<Content>").append("<![CDATA[").append(apiResultBean.getContent()).append("]]>").append("</Content>");
        buffer.append("<MaxWordFreq>"+ WordDetails.maxWordFreq+"</MaxWordFreq>");
        buffer.append("<TermFrequencies>");
        try {
            if(apiResultBean.getContent()!=null)
                terms = apiResultBean.getContent();
            else terms = apiResultBean.getWord1();
            HashMap<String, Integer> termList = WordDetails.getTermListforContent(terms);
            for(String term : termList.keySet())
            {
                buffer.append("<Word>");
                buffer.append("<Term>").append("<![CDATA[").append(term).append("]]>").append("</Term>");
                buffer.append("<Freq>"+termList.get(term)+"</Freq>") ;
                buffer.append("</Word>");
            }
        }
        catch (Exception ex)
        {
            logger.error(ex.getMessage());
        }
        buffer.append("</TermFrequencies>");

        if(apiResultBean.getVector()!=null)
        {
            buffer.append("<Vector>").append("<![CDATA[").append(Arrays.toString(apiResultBean.getVector())).append("]]>").append("</Vector>");
        }
        buffer.append("<ResultSets>");
        for(ResultSet resultSet : apiResultBean.getResultSets())
        {
            buffer.append("<ResultSet>");
            buffer.append("<Word>").append("<![CDATA[").append(resultSet.getEntity()).append("]]>").append("</Word>");
            buffer.append("<similarity>").append(df.format(resultSet.getSimilarityScore())).append("</similarity>");
            buffer.append("</ResultSet>");
        }
        buffer.append("</ResultSets>");
        buffer.append("<error><![CDATA[").append(apiResultBean.getError()).append("]]></error>");
        buffer.append("</ROOT>");
        StringBuilder xmlBuffer = new StringBuilder();
        xmlBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xmlBuffer.append("<RESULT>");
        xmlBuffer.append(buffer.toString()) ;
        xmlBuffer.append("</RESULT>");
        return xmlBuffer.toString();
    }

    public static String getXMLforRandomDisSimilar(APIResultBean apiResultBean, Integer posCount, Integer negCount) {
        StringBuilder buffer = new StringBuilder();
        logger.info("Trying to create xml..");
        buffer.append("<ROOT>");

        buffer.append("<Word>").append("<![CDATA[").append(apiResultBean.getWord1()).append("]]>").append("</Word>");
        buffer.append("<Word>").append("<![CDATA[").append(apiResultBean.getWord2()).append("]]>").append("</Word>");
        buffer.append("<similarity>").append(df.format(apiResultBean.getSimilarity())).append("</similarity>");
        buffer.append("<PositiveCount>").append(posCount).append("</PositiveCount>");
        buffer.append("<NegativeCount>").append(negCount).append("</NegativeCount>");

        buffer.append("<error><![CDATA[").append(apiResultBean.getError()).append("]]></error>");
        buffer.append("</ROOT>");
        StringBuilder xmlBuffer = new StringBuilder();
        xmlBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xmlBuffer.append("<RESULT>");
        xmlBuffer.append(buffer.toString()) ;
        xmlBuffer.append("</RESULT>");
        return xmlBuffer.toString();
    }

    public static String getXMLforUrlResultSet(APIResultBean apiResultBean) {

        StringBuilder buffer = new StringBuilder();
        logger.info("Trying to create xml..");
        buffer.append("<ROOT>");

        buffer.append("<Url>").append("<![CDATA[").append(apiResultBean.getUrl()).append("]]>").append("</Url>");
        buffer.append("<Content>").append("<![CDATA[").append(apiResultBean.getContent().toString().replaceAll(","," ")).append("]]>").append("</Content>");
        buffer.append("<Title>").append("<![CDATA[").append(apiResultBean.getTitle().toString().replaceAll(","," ")).append("]]>").append("</Title>");

        buffer.append("<ContentResultSets>");
        for(ResultSet resultSet : apiResultBean.getResultSets())
        {
            buffer.append("<ResultSet>");
            buffer.append("<Word>").append("<![CDATA[").append(resultSet.getEntity()).append("]]>").append("</Word>");
            buffer.append("<similarity>").append(df.format(resultSet.getSimilarityScore())).append("</similarity>");
            buffer.append("</ResultSet>");
        }
        buffer.append("</ContentResultSets>");

        buffer.append("<ContentResultSets>");
        for(ResultSet resultSet : apiResultBean.getTitleResultSets())
        {
            buffer.append("<ResultSet>");
            buffer.append("<Word>").append("<![CDATA[").append(resultSet.getEntity()).append("]]>").append("</Word>");
            buffer.append("<similarity>").append(df.format(resultSet.getSimilarityScore())).append("</similarity>");
            buffer.append("</ResultSet>");
        }
        buffer.append("</ContentResultSets>");
        buffer.append("<error><![CDATA[").append(apiResultBean.getError()).append("]]></error>");
        buffer.append("</ROOT>");
        StringBuilder xmlBuffer = new StringBuilder();
        xmlBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xmlBuffer.append("<RESULT>");
        xmlBuffer.append(buffer.toString()) ;
        xmlBuffer.append("</RESULT>");
        return xmlBuffer.toString();
    }

    public static String getXMLforAnalogy(APIResultBean apiResultBean) {

        StringBuilder buffer = new StringBuilder();
        logger.info("Trying to create xml..");
        buffer.append("<ROOT>");

        buffer.append("<PositiveWords>");
        buffer.append("<Word>").append("<![CDATA[").append(apiResultBean.getWord1()).append("]]>").append("</Word>");
        buffer.append("<Word>").append("<![CDATA[").append(apiResultBean.getWord2()).append("]]>").append("</Word>");
        buffer.append("</PositiveWords>");

        buffer.append("<NegativeWords>");
        buffer.append("<Word>").append("<![CDATA[").append(apiResultBean.getNegWord()).append("]]>").append("</Word>");
        buffer.append("</NegativeWords>");


        buffer.append("<ResultSets>");
        for(ResultSet resultSet : apiResultBean.getResultSets())
        {
            buffer.append("<ResultSet>");
            buffer.append("<Word>").append("<![CDATA[").append(resultSet.getEntity()).append("]]>").append("</Word>");
            buffer.append("<similarity>").append(df.format(resultSet.getSimilarityScore())).append("</similarity>");
            buffer.append("</ResultSet>");
        }
        buffer.append("</ResultSets>");
        buffer.append("<error><![CDATA[").append(apiResultBean.getError()).append("]]></error>");
        buffer.append("</ROOT>");
        StringBuilder xmlBuffer = new StringBuilder();
        xmlBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xmlBuffer.append("<RESULT>");
        xmlBuffer.append(buffer.toString()) ;
        xmlBuffer.append("</RESULT>");
        return xmlBuffer.toString();
    }

    public static String getXMLforError(APIResultBean apiResultBean) {
        logger.info("Trying to create xml..");

        StringBuilder xmlBuffer = new StringBuilder();
        xmlBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xmlBuffer.append("<RESULT>");
        xmlBuffer.append("<error><![CDATA[").append(apiResultBean.getError()).append("]]></error>");
        xmlBuffer.append("</RESULT>");
        return xmlBuffer.toString();
    }
}
