package com.dinochiesa.edgecallouts.util;

import com.apigee.flow.message.MessageContext;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.Collections;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public final class CalloutUtil {
    private static final String variableReferencePatternString = "(.*?)\\{([^\\{\\} ]+?)\\}(.*?)";
    private static final Pattern variableReferencePattern = Pattern.compile(variableReferencePatternString);

    public static Map<String,String> genericizeMap(Map properties) {
        // convert an untyped Map to a generic map
        Map<String,String> m = new HashMap<String,String>();
        Iterator iterator =  properties.keySet().iterator();
        while(iterator.hasNext()){
            Object key = iterator.next();
            Object value = properties.get(key);
            if ((key instanceof String) && (value instanceof String)) {
                m.put((String) key, (String) value);
            }
        }
        return Collections.unmodifiableMap(m);
    }

    public static String getHeaderWithCommas(MessageContext msgCtxt, String headerName) {
        ArrayList list = msgCtxt.getVariable("request.header." + headerName + ".values");
        return StringUtils.join(list,",");
    }

    /**
     * Strips all leading and trailing characters from the given string.
     * Does NOT strip characters in the middle, and strips the leading and
     * trailing characters respectively.
     * e.g. "{abc}", "{", "}" returns "abc"
     * e.g. "aabccxyz", "ba", "z" returns "ccxy"
     *
     * @param toStrip  The String to remove characters from
     * @param start  The characters to remove from the start (in any order)
     * @param end The characters to remove from the end (in any order)
     * @return String with leading and trailing characters stripped
     */
    public static String stripStartAndEnd(String toStrip, String start, String end) {
        if(StringUtils.isBlank(toStrip)) {
            throw new IllegalArgumentException("toStrip must not be blank or null");
        }
        return StringUtils.stripEnd(StringUtils.stripStart(toStrip, start), end);
    }

    /**
     * Used to resolve strings potentially containing references to variables available in the Apigee context.
     * If the string contains a variable name surrounded with curly braces, it is interpreted
     * as a dynamic variable and the reference is replaced with the value obtained from the context.
     *
     * @param spec The string potentially containing variable names
     * @param msgCtxt The Apigee context object
     * @return The value with variable references resolved
     */
    public static String resolveVariableFromContext(String spec, MessageContext msgCtxt) {
        Matcher matcher = variableReferencePattern.matcher(spec);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "");
            sb.append(matcher.group(1));
            sb.append((String) msgCtxt.getVariable(matcher.group(2)));
            sb.append(matcher.group(3));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

}
