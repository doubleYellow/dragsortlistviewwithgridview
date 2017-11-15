package com.adouble.dragsortlistviewwithgridview.util;

/**
 * Created by hh on 2016/7/28.
 */

public class StringUtil {
    public static String addString(String... text) {
        StringBuilder sb = new StringBuilder();
        for (String item : text) {
            sb.append(item);
        }
        return sb.toString();
    }

    public static String addString(int year, String y, int month, String m, int day, String d, String s) {
        StringBuffer sb = new StringBuffer();
        sb.append(year);
        sb.append(y);
        if (month <10 ){
            sb.append(0);
        }
        sb.append(month);
        sb.append(m);
        if (day < 10){
            sb.append(0);
        }
        sb.append(day);
        sb.append(d);
        sb.append(s);
        return sb.toString();
    }
}
