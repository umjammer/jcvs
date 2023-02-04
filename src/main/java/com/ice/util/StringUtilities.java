/*
 ** Tim Endres' utilities package.
 ** Copyright (c) 1997 by Tim Endres
 **
 ** This program is free software.
 **
 ** You may redistribute it and/or modify it under the terms of the GNU
 ** General Public License as published by the Free Software Foundation.
 ** Version 2 of the license should be included with this distribution in
 ** the file LICENSE, as well as License.html. If the license is not
 ** included	with this distribution, you may find a copy at the FSF web
 ** site at 'www.gnu.org' or 'www.fsf.org', or you may write to the
 ** Free Software Foundation, 675 Mass Ave, Cambridge, MA 02139 USA.
 **
 ** THIS SOFTWARE IS PROVIDED AS-IS WITHOUT WARRANTY OF ANY KIND,
 ** NOT EVEN THE IMPLIED WARRANTY OF MERCHANTABILITY. THE AUTHOR
 ** OF THIS SOFTWARE, ASSUMES _NO_ RESPONSIBILITY FOR ANY
 ** CONSEQUENCE RESULTING FROM THE USE, MODIFICATION, OR
 ** REDISTRIBUTION OF THIS SOFTWARE.
 **
 */

package com.ice.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class StringUtilities {

    /**
     * Split a string into a string array containing the substrings
     * between the delimiters.
     * <p>
     * NOTE This method WILL <strong>NOT</strong> return an empty
     * token at the end of the array that is returned, if the string
     * ends with the delimiter. If you wish to have a property string
     * array that ends with the delimiter return an empty string at
     * the end of the array, use <code>ListString()</code>.
     */
    static public String[] splitString(String splitStr, String delim) {
        int i, count;
        String[] result;
        StringTokenizer toker;

        toker = new StringTokenizer(splitStr, delim);

        count = toker.countTokens();

        result = new String[count];

        for (i = 0; i < count; ++i) {
            try {
                result[i] = toker.nextToken();
            } catch (NoSuchElementException ex) {
                result = null;
                break;
            }
        }

        return result;
    }

    /**
     * Split a string into a string List containing the substrings
     * between the delimiters.
     * <p>
     * NOTE This method WILL return an empty
     * token at the end of the array that is returned, if the string
     * ends with the delimiter.
     */
    static public List<String> ListString(String splitStr, String delim) {
        boolean tokeWasDelim = false;
        int i, count;
        StringTokenizer toker;

        List<String> result = new ArrayList<>();

        toker = new StringTokenizer(splitStr, delim, true);
        count = toker.countTokens();

        for (i = 0; i < count; ++i) {
            String toke;

            try {
                toke = toker.nextToken();
            } catch (NoSuchElementException ex) {
                break;
            }

            if (toke.equals(delim)) {
                if (tokeWasDelim) result.add("");
                tokeWasDelim = true;
            } else {
                result.add(toke);
                tokeWasDelim = false;
            }
        }

        if (tokeWasDelim) result.add("");

        return result;
    }

    public static String join(String[] strings, String sep) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; strings != null && i < strings.length; ++i) {
            if (i > 0) result.append(sep);
            result.append(strings[i]);
        }

        return result.toString();
    }

    public static String[] argumentSubstitution(String[] args, Map<String, String> vars) {
        StringBuilder argBuf = new StringBuilder();

        String[] result = new String[args.length];

        for (int aIdx = 0; aIdx < args.length; ++aIdx) {
            String argStr = args[aIdx];

            int index = argStr.indexOf('$');

            if (index < 0) {
                result[aIdx] = argStr;
            } else {
                result[aIdx] = StringUtilities.stringSubstitution(argStr, vars);
            }
        }

        return result;
    }

    public static String stringSubstitution(String argStr, Map<String, String> vars) {
        StringBuilder argBuf = new StringBuilder();

        for (int cIdx = 0; cIdx < argStr.length(); ) {
            char ch = argStr.charAt(cIdx);

            switch (ch) {
            case '$':
                StringBuilder nameBuf = new StringBuilder();
                for (++cIdx; cIdx < argStr.length(); ++cIdx) {
                    ch = argStr.charAt(cIdx);
                    if (ch == '_' || Character.isLetterOrDigit(ch)) nameBuf.append(ch);
                    else break;
                }

                if (!nameBuf.isEmpty()) {
                    String value = vars.get(nameBuf.toString());

                    if (value != null) {
                        argBuf.append(value);
                    }
                }
                break;

            default:
                argBuf.append(ch);
                ++cIdx;
                break;
            }
        }

        return argBuf.toString();
    }

    public static String[] parseArgumentString(String argStr) {
        List<String> list = StringUtilities.parseArgumentList(argStr);
        return list.toArray(new String[0]);
    }

    public static List<String> parseArgumentList(String argStr) {
        List<String> result = new ArrayList<>();
        StringBuilder argBuf = new StringBuilder();

        boolean backSlash = false;
        boolean matchSglQuote = false;
        boolean matchDblQuote = false;

        for (int cIdx = 0; cIdx < argStr.length(); ++cIdx) {
            char ch = argStr.charAt(cIdx);

            switch (ch) {
            //
            // W H I T E S P A C E
            //
            case ' ':
            case '\t':
            case '\n':
            case '\r':
                if (backSlash) {
                    argBuf.append(ch);
                    backSlash = false;
                } else if (matchSglQuote || matchDblQuote) {
                    argBuf.append(ch);
                } else if (!argBuf.isEmpty()) {
                    result.add(argBuf.toString());
                    argBuf.setLength(0);
                }
                break;

            case '\\':
                if (backSlash) {
                    argBuf.append("\\");
                }
                backSlash = !backSlash;
                break;

            case '\'':
                if (backSlash) {
                    argBuf.append("'");
                    backSlash = false;
                } else if (matchSglQuote) {
                    result.add(argBuf.toString());
                    argBuf.setLength(0);
                    matchSglQuote = false;
                } else if (!matchDblQuote) {
                    matchSglQuote = true;
                }
                break;

            case '"':
                if (backSlash) {
                    argBuf.append("\"");
                    backSlash = false;
                } else if (matchDblQuote) {
                    result.add(argBuf.toString());
                    argBuf.setLength(0);
                    matchDblQuote = false;
                } else if (!matchSglQuote) {
                    matchDblQuote = true;
                }
                break;

            default:
                if (backSlash) {
                    switch (ch) {
                    case 'b':
                        argBuf.append('\b');
                        break;
                    case 'f':
                        argBuf.append('\f');
                        break;
                    case 'n':
                        argBuf.append('\n');
                        break;
                    case 'r':
                        argBuf.append('\r');
                        break;
                    case 't':
                        argBuf.append('\t');
                        break;

                    default:
                        char ch2 = argStr.charAt(cIdx + 1);
                        char ch3 = argStr.charAt(cIdx + 2);
                        if ((ch >= '0' && ch <= '7') && (ch2 >= '0' && ch2 <= '7') && (ch3 >= '0' && ch3 <= '7')) {
                            int octal = (((ch - '0') * 64) + ((ch2 - '0') * 8) + (ch3 - '0'));
                            argBuf.append((char) octal);
                            cIdx += 2;
                        } else if (ch == '0') {
                            argBuf.append('\0');
                        } else {
                            argBuf.append(ch);
                        }
                        break;
                    }
                } else {
                    argBuf.append(ch);
                }

                backSlash = false;
                break;
            }
        }

        if (!argBuf.isEmpty()) {
            result.add(argBuf.toString());
        }

        return result;
    }
}
