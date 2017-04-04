package com.media.net.DataPreparation;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.util.List;
import java.util.zip.GZIPInputStream;

/**
 * Created by vibhor.go on 01/18/17.
 */

public class FileIO
{
    public static InputStream getResourceAsStream(String fileName) throws IOException
    {
        InputStream stream = new FileInputStream(fileName);
        if (stream == null) {
            throw new IOException("resource \"" + fileName +  " not found.");
        }
        return fileTypeStream(stream, fileName);
    }

    public static InputStream fileTypeStream(InputStream inputStream, String fileName) throws IOException
    {
        if (inputStream == null)
            throw new FileNotFoundException("InputStream is null for " + fileName);

        String extension= FilenameUtils.getExtension(fileName).toLowerCase();
        if(extension.equals("gz"))
                return new GZIPInputStream(inputStream);
        if(extension.equals("bz2"))
                return new BZip2CompressorInputStream(inputStream);
        return inputStream;

    }

    public static BufferedReader getReader(Class<?> clazz, String fileName) throws IOException
    {
        try
        {
            final Reader reader = new UnicodeReader(getResourceAsStream(fileName),"utf-8");
            return new BufferedReader(reader);
        }
        finally {

        }
    }

}