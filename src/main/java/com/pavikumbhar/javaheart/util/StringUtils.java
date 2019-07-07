package com.pavikumbhar.javaheart.util;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author pavikumbhar
 *
 */

@Slf4j
public class StringUtils extends org.apache.commons.lang3.StringUtils {
    
    private static Pattern pattern = Pattern.compile("<(span)?\\sstyle.*?style>|(span)?\\sstyle=.*?>", Pattern.DOTALL);
    private static Pattern pattern2 = Pattern.compile("(<[^>]+>)", Pattern.DOTALL);
    private static Pattern patterncomma = Pattern.compile("(&[^;]+;)", Pattern.DOTALL);
    
    /**
     * Determining multiple or single objects at once is empty.
     *
     * @param objects
     * @return returns true if there is an element of Blank
     * @author zhou-baicheng
     */
    public static boolean isBlank(Object... objects) {
        Boolean result = false;
        for (Object object : objects) {
            if (null == object || "".equals(object.toString().trim()) || "null".equals(object.toString().trim())) {
                result = true;
                break;
            }
        }
        return result;
    }
    
    public static String getRandom(int length) {
        String val = "";
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            // output letters or numbers
            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
            // string
            if ("char".equalsIgnoreCase(charOrNum)) {
                // Get uppercase or lowercase letters
                int choice = random.nextInt(2) % 2 == 0 ? 65 : 97;
                val += (char) (choice + random.nextInt(26));
            } else if ("num".equalsIgnoreCase(charOrNum)) { // 数字
                val += String.valueOf(random.nextInt(10));
            }
        }
        return val.toLowerCase();
    }
    
    /**
     * Determining multiple or single objects at once is not empty.
     *
     * @param objects
     * @return returns true if there is an element that is not Blank
     * @author zhou-baicheng
     */
    public static boolean isNotBlank(Object... objects) {
        return !isBlank(objects);
    }
    
    public static boolean isBlank(String... objects) {
        Object[] object = objects;
        return isBlank(object);
    }
    
    public static boolean isNotBlank(String... objects) {
        Object[] object = objects;
        return !isBlank(object);
    }
    
    public static boolean isBlank(String str) {
        Object object = str;
        return isBlank(object);
    }
    
    public static boolean isNotBlank(String str) {
        Object object = str;
        return !isBlank(object);
    }
    
    /**
     * Judge a string that exists in the array
     *
     * @param baseStr
     * @param strings
     * @return
     */
    public static int indexOf(String baseStr, String[] strings) {
        
        if (null == baseStr || baseStr.length() == 0 || null == strings) {
            return 0;
        }
        
        int i = 0;
        for (String string : strings) {
            boolean result = baseStr.equals(string);
            i = result ? ++i : i;
        }
        return i;
    }
    
    public static String trimToEmpty(Object str) {
        return (isBlank(str) ? "" : str.toString().trim());
    }
    
    /**
     * Convert Map to get request parameter type, such as {"name"=20,"age"=30} after conversion,
     * change to name=20&age=30
     *
     * @param map
     * @return
     */
    public static String mapToGet(Map<? extends Object, ? extends Object> map) {
        String result = "";
        if (map == null || map.size() == 0) {
            return result;
        }
        Set<? extends Object> keys = map.keySet();
        for (Object key : keys) {
            result += ((String) key + "=" + (String) map.get(key) + "&");
        }
        
        return isBlank(result) ? result : result.substring(0, result.length() - 1);
    }
    
    /**
     * Convert a string of parameter strings into a map such as "?a=3&b=4" and convert to Map{a=3,
     * b=4}
     *
     * @param args
     * @return
     */
    public static Map<String, ? extends Object> getToMap(String args) {
        if (isBlank(args)) {
            return null;
        }
        args = args.trim();
        //If it is? At the beginning, remove it?
        if (args.startsWith("?")) {
            args = args.substring(1, args.length());
        }
        String[] argsArray = args.split("&");
        
        Map<String, Object> result = new HashMap<>();
        for (String ag : argsArray) {
            if (!isBlank(ag) && ag.indexOf("=") > 0) {
                
                String[] keyValue = ag.split("=");
                // If the value or key value contains "=", the first "=" is the main, such as name = 0 = 3 after conversion, {"name": "0=3"},
                //if the demand is not met , please do not modify, solve it yourself.
                
                String key = keyValue[0];
                String value = "";
                for (int i = 1; i < keyValue.length; i++) {
                    value += keyValue[i] + "=";
                }
                value = value.length() > 0 ? value.substring(0, value.length() - 1) : value;
                result.put(key, value);
                
            }
        }
        
        return result;
    }
    
    /**
     * Convert to Unicode
     *
     * @param str
     * @return
     */
    public static String toUnicode(String str) {
        String as[] = new String[str.length()];
        String s1 = "";
        for (int i = 0; i < str.length(); i++) {
            int v = str.charAt(i);
            if (v >= 19968 && v <= 171941) {
                as[i] = Integer.toHexString(str.charAt(i) & 0xffff);
                s1 = s1 + "\\u" + as[i];
            } else {
                s1 = s1 + str.charAt(i);
            }
        }
        return s1;
    }
    
    /**
     * Consolidated data
     *
     * @param v
     * @return
     */
    public static String merge(Object... v) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < v.length; i++) {
            sb.append(v[i]);
        }
        return sb.toString();
    }
    
    /**
     * string to urlcode
     *
     * @param value
     * @return
     */
    public static String strToUrlcode(String value) {
        try {
            value = java.net.URLEncoder.encode(value, "utf-8");
            return value;
        } catch (UnsupportedEncodingException e) {
            log.error("The string converted to URLCode failed, value: {}", value, e);
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * urlcode to string
     *
     * @param value
     * @return
     */
    public static String urlcodeToStr(String value) {
        try {
            value = java.net.URLDecoder.decode(value, "utf-8");
            return value;
        } catch (UnsupportedEncodingException e) {
            log.error("URLCode converted to string failed; value: {}", value, e);
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Determine if the string contains Chinese characters
     *
     * @param txt
     * @return
     */
    public static Boolean containsCN(String txt) {
        if (isBlank(txt)) {
            return false;
        }
        for (int i = 0; i < txt.length(); i++) {
            
            String bb = txt.substring(i, i + 1);
            
            boolean cc = Pattern.matches("[\u4E00-\u9FA5]", bb);
            if (cc) {
                return cc;
            }
        }
        return false;
    }
    
    /**
     * Remove the HTML code
     *
     * @param news
     * @return
     */
    public static String removeHtml(String news) {
        String s = news.replaceAll("amp;", "").replaceAll("<", "<").replaceAll(">", ">");
        
        Matcher matcher = pattern.matcher(s);
        String str = matcher.replaceAll("");
        
        Matcher matcher2 = pattern2.matcher(str);
        String strhttp = matcher2.replaceAll(" ");
        
        String regEx = "(((http|https|ftp)(\\s)*((\\:)|：))(\\s)*(//|//)(\\s)*)?"
                + "([\\sa-zA-Z0-9(\\.|．)(\\s)*\\-]+((\\:)|(:)[\\sa-zA-Z0-9(\\.|．)&%\\$\\-]+)*@(\\s)*)?" + "("
                + "(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])" + "(\\.|．)(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)"
                + "(\\.|．)(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)"
                + "(\\.|．)(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])"
                + "|([\\sa-zA-Z0-9\\-]+(\\.|．)(\\s)*)*[\\sa-zA-Z0-9\\-]+(\\.|．)(\\s)*[\\sa-zA-Z]*" + ")" + "((\\s)*(\\:)|(：)(\\s)*[0-9]+)?"
                + "(/(\\s)*[^/][\\sa-zA-Z0-9\\.\\,\\?\\'\\\\/\\+&%\\$\\=~_\\-@]*)*";
        Pattern p1 = Pattern.compile(regEx, Pattern.DOTALL);
        Matcher matchhttp = p1.matcher(strhttp);
        String strnew = matchhttp.replaceAll("").replaceAll("(if[\\s]*\\(|else|elseif[\\s]*\\().*?;", " ");
        
        Matcher matchercomma = patterncomma.matcher(strnew);
        String strout = matchercomma.replaceAll(" ");
        String answer = strout.replaceAll("[\\pP‘’“”]", " ").replaceAll("\r", " ").replaceAll("\n", " ").replaceAll("\\s", " ").replaceAll("　", "");
        
        return answer;
    }
    
    /**
     * Remove the empty data of the array
     *
     * @param array
     * @return
     */
    public static List<String> array2Empty(String[] array) {
        List<String> list = new ArrayList<>();
        for (String string : array) {
            if (StringUtils.isNotBlank(string)) {
                list.add(string);
            }
        }
        return list;
    }
    
    /**
     * Convert an array to a set
     *
     * @param array
     * @return
     */
    public static Set<?> array2Set(Object[] array) {
        Set<Object> set = new TreeSet<>();
        for (Object id : array) {
            if (null != id) {
                set.add(id);
            }
        }
        return set;
    }
    
    /**
     * serializable toString
     *
     * @param serializable
     * @return
     */
    public static String toString(Serializable serializable) {
        if (null == serializable) {
            return null;
        }
        try {
            return (String) serializable;
        } catch (Exception e) {
            return serializable.toString();
        }
    }
    
    /**
     * If Object is null then return "" empty string, otherwise return (String) Object
     */
    public static String checkNull(Object o) {
        String result = "";
        if (o != null) {
            result = o.toString();
        }
        return result;
    }
    
}