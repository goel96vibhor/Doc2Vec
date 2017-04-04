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


}
