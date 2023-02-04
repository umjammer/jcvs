/*
 ** Java cvs client library package.
 ** Copyright (c) 1997 by Timothy Gerard Endres
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

package com.ice.cvsc;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 * Implements a List subclass that handles CVS Arguments used
 * in CVSRequest objects.
 *
 * @author Timothy Gerard Endres, <a href="mailto:time@ice.com">time@ice.com</a>.
 * @version $Revision: 2.2 $
 * @see CVSClient
 * @see CVSProject
 */
public class CVSArgumentList extends ArrayList<String> {

    static public final String RCS_ID = "$Id: CVSArgumentVector.java,v 2.2 1998/07/05 00:02:19 time Exp $";
    static public final String RCS_REV = "$Revision: 2.2 $";

    public CVSArgumentList() {
        super();
    }

    public CVSArgumentList(int initCap) {
        super(initCap);
    }

    public String argumentAt(int index) {
        return this.get(index);
    }

    public void appendArgument(String argument) {
        this.add(argument);
    }

    public void appendArguments(List<String> args) {
        this.addAll(args);
    }

    public boolean containsArgument(String argument) {

        for (int i = 0; i < this.size(); ++i) {
            String argStr = this.get(i);

            if (argStr.equals(argument)) return true;

            if (argStr.startsWith("-")) {
                ++i; // skip this argument's parameter
            }
        }

        return false;
    }

    public boolean containsString(String string) {
        for (String str : this) {
            if (str.equals(string)) return true;
        }

        return false;
    }

    public static CVSArgumentList parseArgumentString(String argStr) {
        String token;
        String newDelim = null;
        boolean matchQuote = false;

        CVSArgumentList result = new CVSArgumentList();

        StringTokenizer toker = new StringTokenizer(argStr, " '\"", true);

        while (toker.hasMoreTokens()) {
            try {
                token = (newDelim == null ? toker.nextToken() : toker.nextToken(newDelim));

                newDelim = null;
            } catch (NoSuchElementException ex) {
                break;
            }

            switch (token) {
            case " ":
                continue;
            case "'":
                if (matchQuote) {
                    newDelim = " '\"";
                    matchQuote = false;
                } else {
                    newDelim = "'";
                    matchQuote = true;
                }
                break;
            case "\"":
                if (matchQuote) {
                    newDelim = " '\"";
                    matchQuote = false;
                } else {
                    newDelim = "\"";
                    matchQuote = true;
                }
                break;
            default:
                result.add(token);
                break;
            }
        }

        return result;
    }
}
