package com.media.net.DataPreparation;

import com.media.net.Utils.ApplicationProperties;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by vibhor.go on 01/18/17.
 */

public class FileSplitter extends FileIO
{
    public final static int maxLineWords= Integer.parseInt(ApplicationProperties.getProperty("MAX_LINE_WORDS"));
    public final static long maxFileWords=Integer.parseInt(ApplicationProperties.getProperty("MAX_FILE_WORDS"));
    public final static int charBufferSize=Integer.parseInt(ApplicationProperties.getProperty("CHAR_BUFFER_SIZE"));

    public static void splitFile(String fileName, String writeDirPath)
    {
        List<String> sentences = null;
        int fileCount=0;
        int fileWordCount=0;
        int lineWordCount=0;
        try {
            Reader bufferedReader=getReader(FileSplitter.class,fileName);
            File directory= new File(writeDirPath);
            if(!directory.exists())directory.mkdirs();
            if(directory.listFiles()!=null)fileCount=directory.listFiles().length;
            BufferedWriter bufferedWriter= new BufferedWriter(getFileWriter(writeDirPath,fileCount));
            sentences= new ArrayList<String>();
            String line=null;
            char[] charBuffer = new char[charBufferSize];
            StringBuilder lineBuilder= new StringBuilder("");
            StringBuilder wordBuilder= new StringBuilder("");
            for(int len; (len = bufferedReader.read(charBuffer)) > 0;) {

//                for(int i=0;i<charBufferSize;i++)System.out.print(charBuffer[i]);
//                System.out.println(len);
//                System.out.println();

                 for(int i=0;i<charBufferSize;i++)
                 {

                     if(!String.valueOf(charBuffer[i]).matches("."))
                     {

                         if(fileWordCount+lineWordCount>maxFileWords)
                         {
                             fileCount++;
                             System.out.println("wriiten "+fileWordCount+" words in file "+fileCount);
                             fileWordCount=0;

                             bufferedWriter.close();

                             bufferedWriter= new BufferedWriter(getFileWriter(writeDirPath,fileCount));
                         }

                         bufferedWriter.write(lineBuilder.toString());
                         bufferedWriter.newLine();
//                         System.out.println(lineBuilder.toString());
//                         System.out.println("yes");
                         lineBuilder= new StringBuilder("");
                         fileWordCount+=lineWordCount;
                         lineWordCount=0;
                     }
                     else if(Character.isWhitespace(charBuffer[i]))
                     {

                         wordBuilder.append(charBuffer[i]);
                         //System.out.println(wordBuilder);
                         lineBuilder.append(wordBuilder);
                         lineWordCount++;
                         if(lineWordCount==maxLineWords)
                         {
                             if(fileWordCount+lineWordCount>maxFileWords)
                             {
                                 fileCount++;
                                 System.out.println("wriiten "+fileWordCount+" words in file "+fileCount);
                                 fileWordCount=0;

                                 bufferedWriter.close();

                                 bufferedWriter= new BufferedWriter(getFileWriter(writeDirPath,fileCount));
                             }

                             bufferedWriter.write(lineBuilder.toString());
                             bufferedWriter.newLine();
//                             System.out.println(lineBuilder.toString());
//                             System.out.println("yes");
                             lineBuilder= new StringBuilder("");
                             fileWordCount+=lineWordCount;
                             lineWordCount=0;

                         }

                         wordBuilder= new StringBuilder("");
                     }
                     else {
                         //System.out.print(charBuffer[i]);
                         wordBuilder.append(charBuffer[i]);
                     }
                 }
            }
            fileWordCount+=lineWordCount;
            lineBuilder.append(wordBuilder);
            bufferedWriter.write(lineBuilder.toString());
            bufferedWriter.newLine();
            bufferedWriter.close();
            fileCount++;
            System.out.println("wriiten "+fileWordCount+" words in file "+fileCount);

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public static FileWriter getFileWriter(String writeDirPath, int fileCount) throws IOException
    {

        String fileName= writeDirPath+File.separator+ "file_"+String.valueOf(fileCount)+".txt";
        File file= new File(fileName);
        if(!file.exists())file.createNewFile();
        FileWriter fileWriter= new FileWriter(file);
        return fileWriter;
    }
}