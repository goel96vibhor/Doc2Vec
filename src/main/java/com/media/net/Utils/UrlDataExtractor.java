package com.media.net.Utils;


import com.autoopt.beans.XtractorBean;
import com.autoopt.beans.XtractorContentBean;
import com.autoopt.beans.XtractorResultBean;
import com.autoopt.utils.*;
import com.autoopt.xtractor.XtractorContentExtractor;
import com.google.gson.Gson;
import com.media.net.Beans.DocBean;
import com.media.net.Beans.UrlBean;
import com.media.net.PreprocessingEntities.VocabBuilder;
import com.media.net.Word2VecMain;
import org.apache.log4j.Logger;

import java.util.Scanner;

public class UrlDataExtractor
{
    private static Logger logger = Logger.getLogger(UrlDataExtractor.class);
    static {
        try {
            ApplicationProperties.loadProperties(UrlDataExtractor.class.getResourceAsStream("/application.properties"), true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    private static XtractorContentBean getXtractorContentBean(String url) throws Exception
    {
        XtractorResultBean xtractorResultBean = XtractorContentExtractor.getXtractorResultBean(url);
        XtractorContentBean xtractorContent = xtractorResultBean.getXtractorContentBean();
        if (xtractorContent == null) throw new Exception("xtractor content is null!");
        else if (xtractorContent.getStatus().equalsIgnoreCase("error"))  throw new Exception("xtractor returned status = error!");
        return xtractorContent;

    }

    private static String extractUsefulContentFromExtractor(XtractorContentBean xtractorContent, String useExtractorContent){
        if(useExtractorContent.equals("true")) {
            StringBuilder usefulContent = new StringBuilder();
            for (XtractorBean xtractorBean : xtractorContent.getxTractorContent()) {
                usefulContent.append(xtractorBean.getMainText());
                usefulContent.append(". ");
            }
            return Util.parseData((usefulContent.toString()));
        }else {
            return Util.parseData(xtractorContent.getSnacktoryContent());
        }
    }

    private static String extractUsefulContent(XtractorContentBean xtractorContent, String useExtractorContent) {
        try {
            String usefulContent = extractUsefulContentFromExtractor(xtractorContent, useExtractorContent);
            if(usefulContent != null) usefulContent = usefulContent.replaceAll("<[^<]*>", " ").replaceAll(" +", " ");
            if(usefulContent == null || usefulContent.length() < Integer.parseInt(ApplicationProperties.getProperty("MINIMUM_CONTENT_LENGTH"))){
                logger.info("Content from Xtractor is insufficient .....");
//                if(usefulContent == null) usefulContent = "";
//                if(Util.parseData(xtractorContent.getTitle()) != null) {
//                    String contentFromSearchEngine = Util.parseData(Util.getContentForKeyword(xtractorContent.getTitle()));
//                    if(contentFromSearchEngine != null){
//                        return usefulContent + ". " + contentFromSearchEngine;
//                    }
//                }
                throw new Exception("Useful content length very less");
            }
            usefulContent= cleanUsefulContent(usefulContent);
            return usefulContent;

        }catch (Exception e){
            logger.error("Exception in getting useful content , Reason :: " + e.getMessage(), e);
        }
        return null;

    }

   public static String cleanUsefulContent(String usefulContent)
   {
//       usefulContent=usefulContent.replaceAll("[^A-Za-z0-9 ]"," ");
       usefulContent=usefulContent.replaceAll("[^A-Za-z ]"," ");
       usefulContent=usefulContent.toLowerCase();
//       usefulContent=usefulContent.replace("0","zero ");
//       usefulContent=usefulContent.replace("1","one ");
//       usefulContent=usefulContent.replace("2","two ");
//       usefulContent=usefulContent.replace("3","three ");
//       usefulContent=usefulContent.replace("4","four ");
//       usefulContent=usefulContent.replace("5","five ");
//       usefulContent=usefulContent.replace("6","six ");
//       usefulContent=usefulContent.replace("7","seven ");
//       usefulContent=usefulContent.replace("8","eight ");
//       usefulContent=usefulContent.replace("9","nine ");
       return usefulContent;
   }

   public DocBean getDocBeanforUrl(String url, long tag) throws Exception
   {
       UrlBean urlBean= extractUrlData(url, 3);
       DocBean docBean= new DocBean(tag,url);
       if(urlBean.getTitle()!=null)
       {
           docBean.setTitle(VocabBuilder.tokenize(urlBean.getTitle()));
       }
       if(urlBean.getUsefulContent()!=null)
       {
           docBean.setContent(VocabBuilder.tokenize(urlBean.getUsefulContent()));
       }
       else throw new Exception("No useful content for url");
       return docBean;
   }

   public UrlBean extractUrlData(String url, int retryCount) throws Exception
   {
       UrlBean urlBean= new UrlBean(url);
       long start = System.currentTimeMillis();
       try {
           XtractorContentBean xtractorContent =  getXtractorContentBean(url);
           urlBean.setTitle(xtractorContent.getTitle() + " ~ " + Util.getTitleFromUrl(url));
           urlBean.setUsefulContent(extractUsefulContent(xtractorContent, "true"));
           if(Util.parseData(urlBean.getUsefulContent()) == null) throw new Exception("Useful content is null");
           try {
               if (xtractorContent.getMetaDescription() == null)   urlBean.setMetaContent("");
               else urlBean.setMetaContent(xtractorContent.getMetaDescription());
           }catch (Exception e){
               logger.error("Error in fetching Meta content : " + e.getMessage(), e);
               urlBean.setMetaContent("");
           }
       }catch (Exception e){
           if (retryCount == 0)    throw e;
           else return extractUrlData(url, retryCount-1);
       }
       return urlBean;
   }



   public static void main(String args[])
   {
       UrlDataExtractor urlDataExtractor= new UrlDataExtractor();
       String url;
       System.out.println(Integer.parseInt(ApplicationProperties.getProperty("MINIMUM_CONTENT_LENGTH")));
//       Scanner in= new Scanner(System.in);
//       while (true)
       {
           System.out.println("Enter url :");
           url= "http://www.naturallivingideas.com/weird-fruits-veggies-to-grow-at-home/";
           try {
               UrlBean urlBean= urlDataExtractor.extractUrlData(url,3);
               System.out.println(urlBean.getUrl());
               System.out.println(urlBean.getTitle());
               System.out.println(urlBean.getUsefulContent());
               System.out.println(VocabBuilder.tokenize(urlBean.getUsefulContent()));
           }
           catch (Exception ex)
           {
               ex.printStackTrace();
           }
       }
   }
}