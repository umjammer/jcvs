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

import java.io.PrintStream;
import java.util.ArrayList;


/**
 * Implements a List subclass that handles CVSResonseItems
 * from CVSRequest objects.
 *
 * @author Timothy Gerard Endres, <a href="mailto:time@ice.com">time@ice.com</a>.
 * @version $Revision: 2.1 $
 * @see CVSClient
 * @see CVSProject
 * @see CVSRequest
 * @see CVSResponse
 * @see CVSResponseItem
 */

public class
CVSRespItemList extends ArrayList {
    static public final String RCS_ID = "$Id: CVSRespItemVector.java,v 2.1 1997/04/19 05:12:09 time Exp $";
    static public final String RCS_REV = "$Revision: 2.1 $";

    public CVSRespItemList() {
        super();
    }

    public CVSRespItemList(int initCap) {
        super(initCap);
    }

    public CVSResponseItem
    itemAt(int index) {
        return (CVSResponseItem) this.get(index);
    }

    public void
    appendItem(CVSResponseItem item) {
        this.add(item);
    }

    public void
    printResponseItemList(PrintStream out, String prefix) {
        for (int i = 0; i < this.size(); ++i) {
            CVSResponseItem item = this.itemAt(i);

            out.print(prefix + "ITEM ");
            out.print("type '" + item.getType() + "' ");
            out.println("");
        }
    }

}
