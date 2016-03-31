package com.elderbyte.ffmpeg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.StringTokenizer;
import java.util.Vector;

/**
 *
 */
public class ShellParser {

    private static final Logger logger = LoggerFactory.getLogger(ShellParser.class);


    /*
     *  Code below bases on ANT command parser
     *
     *
     *  Licensed to the Apache Software Foundation (ASF) under one or more
     *  contributor license agreements.  See the NOTICE file distributed with
     *  this work for additional information regarding copyright ownership.
     *  The ASF licenses this file to You under the Apache License, Version 2.0
     *  (the "License"); you may not use this file except in compliance with
     *  the License.  You may obtain a copy of the License at
     *
     *      http://www.apache.org/licenses/LICENSE-2.0
     *
     *  Unless required by applicable law or agreed to in writing, software
     *  distributed under the License is distributed on an "AS IS" BASIS,
     *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     *  See the License for the specific language governing permissions and
     *  limitations under the License.
     *
     */
    /**
     * Parses a command into tokens.
     *
     * The command line is generally split by whitespace,
     * simple Quotes are supported and cause the quoted part
     * to not be broken down.
     *
     * @param command
     * @return
     */
    public static String[] parseCommand(String command, boolean keepQuotes) {
        if (command == null || command.length() == 0) {
            //no command? no string
            return new String[0];
        }
        // parse with a simple finite state machine

        final int normal = 0;
        final int inQuote = 1;
        final int inDoubleQuote = 2;
        int state = normal;
        StringTokenizer tok = new StringTokenizer(command, "\"\' ", true);
        Vector v = new Vector();
        StringBuffer current = new StringBuffer();
        boolean lastTokenHasBeenQuoted = false;

        while (tok.hasMoreTokens()) {
            String nextTok = tok.nextToken();
            switch (state) {
                case inQuote:
                    if ("\'".equals(nextTok)) {
                        lastTokenHasBeenQuoted = true;
                        state = normal;
                        if(keepQuotes) current.append(nextTok);
                    } else {
                        current.append(nextTok);
                    }
                    break;
                case inDoubleQuote:
                    if ("\"".equals(nextTok)) {
                        lastTokenHasBeenQuoted = true;
                        state = normal;
                        if(keepQuotes) current.append(nextTok);
                    } else {
                        current.append(nextTok);
                    }
                    break;
                default:
                    if ("\'".equals(nextTok)) {
                        state = inQuote;
                        current.append(nextTok);
                    } else if ("\"".equals(nextTok)) {
                        state = inDoubleQuote;
                        current.append(nextTok);
                    } else if (" ".equals(nextTok)) {
                        if (lastTokenHasBeenQuoted || current.length() != 0) {
                            v.addElement(current.toString());
                            current = new StringBuffer();
                        }
                    } else {
                        current.append(nextTok);
                    }
                    lastTokenHasBeenQuoted = false;
                    break;
            }
        }
        if (lastTokenHasBeenQuoted || current.length() != 0) {
            v.addElement(current.toString());
        }
        if (state == inQuote || state == inDoubleQuote) {
            logger.warn("Parser: unbalanced quotes in " + command);
        }
        String[] args = new String[v.size()];
        v.copyInto(args);
        return args;
    }


    public static String toCommandLine(Iterable<String> str){
        StringBuilder sb = new StringBuilder();
        for (String string : str) {
            sb.append(string + " ");
        }
        return sb.toString();
    }

    public static String toCommandLine(String[] str){
        StringBuilder sb = new StringBuilder();
        for (String string : str) {
            sb.append(string + " ");
        }
        return sb.toString();
    }

}
