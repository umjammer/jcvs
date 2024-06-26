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

import java.util.NoSuchElementException;
import java.util.StringTokenizer;


public class HTTPUtilities {

    //
    // HTTP/1.0 200 OK
    //
    static public String[] parseStatusLine(String statusLine) {
        StringTokenizer toker = new StringTokenizer(statusLine, " \t");

        int count = toker.countTokens();
        String[] result = new String[count];

        for (int i = 0; i < count; ++i) {
            try {
                result[i] = toker.nextToken();
            } catch (NoSuchElementException ex) {
                break;
            }
        }

        return result;
    }

    static public String getResultCode(String statusLine) {
        String[] tokes = HTTPUtilities.parseStatusLine(statusLine);

        if (tokes.length > 1) return tokes[1];
        else return null;
    }
}
