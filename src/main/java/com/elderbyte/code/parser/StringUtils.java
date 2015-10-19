package com.elderbyte.code.parser;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 *
 */
public final class StringUtils {

    public static List<String> splitKeep(String input, Iterable<String> delims)
    {
        String regex = "";

        for(String delim : delims){
            regex += "((?<="+Pattern.quote(delim)+")|(?="+Pattern.quote(delim)+"))" + "|";
        }
        regex = regex.substring(0, regex.length()-1);

        return Arrays.asList(Pattern.compile(regex).split(input));
    }


}
