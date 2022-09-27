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


/**
 * The CVSEntryList class subclasses List to specifically
 * handle CVSEntry ocjects. This subclass adds several convenience
 * methods for adding and retrieving CVSEntry objects quickly.
 *
 * @author Timothy Gerard Endres, <a href="mailto:time@ice.com">time@ice.com</a>.
 * @version $Revision: 2.2 $
 * @see CVSClient
 * @see CVSProject
 */

public class
CVSEntryList extends ArrayList {
    static public final String RCS_ID = "$Id: CVSEntryVector.java,v 2.2 1999/04/01 17:48:22 time Exp $";
    static public final String RCS_REV = "$Revision: 2.2 $";

    static public boolean traceLocate = false;
    static public boolean traceLocatePath = false;

    /**
     * Indicates if this entry List is 'dirty'. If this
     * is true, then an entry was removed from this List.
     * Since that entry can not indicate a dirty condition
     * because it is gone, we must record that state in the
     * List itself. We also pick out the added entry case
     * in the event that the adder forgot to dirty the newly
     * added entry.
     */
    private boolean isDirty;


    public CVSEntryList() {
        super();
        this.isDirty = false;
    }

    public CVSEntryList(int initCap) {
        super(initCap);
        this.isDirty = false;
    }

    // UNDONE - finalize should removeAllEntries!!!

    public void
    removeAllEntries() {
        // Since we can contain other CVSEntryList, we
        // need to recurse on those to be sure all is freed!
        //
        for (int i = 0; i < this.size(); ++i) {
            CVSEntry entry = this.entryAt(i);
            if (entry.isDirectory()) {
                entry.removeAllEntries();
            }
        }

        this.clear();
    }

    public CVSEntry
    entryAt(int index) {
        return (CVSEntry) this.get(index);
    }

    public CVSEntry
    getEntryAt(int index) {
        return (CVSEntry) this.get(index);
    }

    public void
    appendEntry(CVSEntry entry) {
        this.add(entry);
        this.isDirty = true;
    }

    private boolean
    removeEntry(CVSEntry entry) {
        boolean result;

        result = this.remove(entry);
        if (result) {
            this.isDirty = true;
        }

        return result;
    }

    private boolean
    removeEntry(String entryName) {
        for (int i = 0; i < this.size(); ++i) {
            CVSEntry entry = this.entryAt(i);

            if (entryName.equals(entry.getName())) {
                this.remove(i);
                this.isDirty = true;
                return true;
            }
        }

        return false;
    }

    /**
     * Check to see if any entries in this List are dirty.
     *
     * @return If any entry is dirty, returns true, else false.
     */

    public boolean
    isDirty() {
        if (this.isDirty)
            return true;

        for (int i = 0; i < this.size(); ++i) {
            CVSEntry entry = (CVSEntry) this.get(i);
            if (entry.isDirty())
                return true;
        }

        return false;
    }

    /**
     * Check to see if any entries in this List are dirty.
     *
     * @return If any entry is dirty, returns true, else false.
     */

    public void
    setDirty(boolean dirty) {
        this.isDirty = dirty;

        for (int i = 0; i < this.size(); ++i) {
            CVSEntry entry = (CVSEntry) this.get(i);
            entry.setDirty(dirty);
        }
    }

    /**
     * Locate an entry in this entry List with the given name.
     *
     * @param name The entry's name (without any path).
     * @return The entry corresponding to name, or null if not found.
     */

    public CVSEntry
    locateEntry(String name) {
        CVSTracer.traceIf(CVSEntryList.traceLocate,
                "===== CVSEntryVector.locateEntry: "
                        + "name '" + name + "' =====");

        for (int i = 0; i < this.size(); ++i) {
            CVSEntry entry = (CVSEntry) this.get(i);

            CVSTracer.traceIf(CVSEntryList.traceLocate,
                    "CVSEntryVector.locateEntry: ENTRY '"
                            + entry.getFullName()
                            + "' isDir '" + entry.isDirectory() + "'");

            if (name.equals(entry.getName())) {
                CVSTracer.traceIf(CVSEntryList.traceLocate,
                        "CVSEntryVector.locateEntry: '"
                                + entry.getFullName() + "' FOUND.");
                return entry;
            }
        }

        CVSTracer.traceIf(CVSEntryList.traceLocate,
                "CVSEntryVector.locateEntry: '" + name + "' NOT FOUND.");

        return null;
    }

}
