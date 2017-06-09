package com.media.net.Utils;

/**
 * Created by IntelliJ IDEA.
 * User: jigar.p
 * Date: Jul 15, 2010
 * Time: 12:51:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class Util {
    public static String parseData(String str) {
        if (str == null || str.trim().length() == 0 || str.trim().equalsIgnoreCase("null")) {
            return null;
        } else {
            return str.trim();
        }
    }

    public static String getTitleFromUrl(String url){
        if (url.startsWith("/"))    url = url.substring(1);
        if (url.charAt(url.length() - 1) != '/')    url = url + "/";
        if (url.startsWith("http://"))  url = url.replace("http://" , "");
        if (url.startsWith("https://"))  url = url.replace("https://" , "");
        if (url.indexOf("/") == url.length()-1)   return "";
        url = url.substring(url.indexOf("/") + 1);
        String[] split = url.toLowerCase().split("[^\\w']+");
        StringBuilder builder = new StringBuilder();
        for (String s : split)  builder.append(s).append(" ");
        return builder.toString();
    }

}
