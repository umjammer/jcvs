/*
 ** Copyright (c) 1998 by Timothy Gerard Endres
 ** <mailto:time@ice.com>  <http://www.ice.com>
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

package com.ice.jcvsii;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import javax.swing.tree.DefaultMutableTreeNode;

import com.ice.cvsc.CVSCUtilities;
import com.ice.cvsc.CVSEntry;
import com.ice.cvsc.CVSEntryList;
import com.ice.cvsc.CVSTimestamp;


public
class EntryNode
        extends DefaultMutableTreeNode
        implements CVSEntry.ChildEventListener {
    private static String tsFormatStr = null;
    private static SimpleDateFormat timeStampFormat = null;

    protected boolean hasLoaded;
    protected CVSEntry entry;

    protected String tsCache;


    public static void
    setTimestampFormat(String fmtStr) {
        EntryNode.tsFormatStr = fmtStr;
        if (EntryNode.tsFormatStr == null) {
            EntryNode.timeStampFormat = null;
        } else {
            SimpleDateFormat format =
                    new SimpleDateFormat(fmtStr, Locale.US); // UNDONE
            format.setTimeZone(TimeZone.getDefault());
            EntryNode.timeStampFormat = format;
        }
    }

    public EntryNode(CVSEntry entry) {
        this.entry = entry;
        this.tsCache = null;
        this.hasLoaded = false;
        if (this.entry != null) {
            this.entry.addChildEventListener(this);
        }
    }

    /**
     * Returns the string to be used to display this leaf in the JTree.
     */
    public String
    toString() {
        return "[EntryNode " +
                " hasLoaded=" + hasLoaded +
                " entry=" + entry +
                "]";
    }

    /**
     * Returns the local File that this node's CVSEntry represents.
     * The File uses the root node's local path combined with the
     * CVSEntry's getFullName() to build the file's path.
     */
    public File
    getLocalFile() {
        EntryRootNode root = (EntryRootNode) this.getRoot();

        return new File
                (CVSCUtilities.exportPath(root.getLocalRootPath()),
                        CVSCUtilities.exportPath(this.entry.getFullPathName()));
    }

    /**
     * Returns this node's CVSEntry.
     */
    public String
    getEntryVersion() {
        return this.entry.getVersion();
    }

    /**
     * Resets the cached display strings so they will be recomputed.
     */
    public void
    resetDisplayCaches() {
        this.tsCache = null;
        if (this.hasLoaded) {
            Enumeration e = this.children();
            for (; e.hasMoreElements(); ) {
                EntryNode node = (EntryNode) e.nextElement();
                node.resetDisplayCaches();
            }
        }
    }

    /**
     * Returns the date the receiver was last modified.
     */
    public String
    getEntryTimestamp() {
        if (this.tsCache == null) {
            CVSTimestamp ts = this.entry.getCVSTime();
            if (ts != null) {
                this.tsCache =
                        EntryNode.timeStampFormat.format(ts);
            }

            if (this.tsCache == null) {
                this.tsCache = this.entry.getTimestamp();
            }
        }

        return (this.tsCache == null ? "" : this.tsCache);
    }

    /**
     * Returns this node's CVSEntry.
     */
    public CVSEntry
    getEntry() {
        return this.entry;
    }

    /**
     * Returns true if the receiver represents a leaf, that is it is
     * isn't a directory.
     */
    public boolean
    isLeaf() {
        return !this.entry.isDirectory();
    }

    /**
     * Returns true if the receiver represents a leaf, that is it is
     * isn't a directory.
     */
    public boolean
    hasLoadedChildren() {
        return this.hasLoaded;
    }

    /**
     * return the number of children for this folder node. The first
     * time this method is called we load up all of the folders
     * under the store's defaultFolder
     */

    public int
    getChildCount() {
        if (!this.hasLoaded) {
            this.loadChildren();
        }

        return super.getChildCount();
    }

    /**
     * Creates the children of the receiver.
     */
    protected void
    loadChildren() {
        if (!this.isLeaf()) {
            CVSEntryList entries = this.entry.getEntryList();

            entries = this.sortEntryList(entries);

            for (int i = 0, sz = entries.size(); i < sz; ++i) {
                this.insert
                        (new EntryNode(entries.entryAt(i)), i);
            }
        }

        this.hasLoaded = true;
    }

    private CVSEntryList
    sortEntryList(CVSEntryList entries) {
        List v = null;
        List dirV = new ArrayList<>();
        List fileV = new ArrayList<>();

        for (int i = 0, sz = entries.size(); i < sz; ++i) {
            CVSEntry entry = entries.entryAt(i);
            String entryName = entry.getName();

            boolean inserted = false;
            v = (entry.isDirectory() ? dirV : fileV);

            for (int j = 0, jsz = v.size(); j < jsz; ++j) {
                CVSEntry jEntry = (CVSEntry) v.get(j);

                if (entryName.compareTo(jEntry.getName()) < 0) {
                    v.set(j, entry);
                    inserted = true;
                    break;
                }
            }

            if (!inserted) {
                v.add(entry);
            }
        }

        CVSEntryList result = new CVSEntryList();

        v = fileV;
        for (int i = 0, sz = v.size(); i < sz; ++i) {
            CVSEntry entry = (CVSEntry) v.get(i);
            result.appendEntry(entry);
        }

        v = dirV;
        for (int i = 0, sz = v.size(); i < sz; ++i) {
            CVSEntry entry = (CVSEntry) v.get(i);
            result.appendEntry(entry);
        }

        return result;
    }

    public void
    cvsEntryAddedChild(CVSEntry.ChildEvent event) {
        int idx = event.getChildIndex();

        EntryRootNode rootNode = (EntryRootNode) this.getRoot();

        EntryTreeModel model =
                (EntryTreeModel) rootNode.getEntryTree().getModel();

        CVSEntry entry = event.getChildEntry();

        EntryNode chNode = null;

        // NOTE
        // If we have not loaded yet, we must be careful. Otherwise,
        // we will do an insert here, but not have the other children
        // AND if we insert, then we will get a duplicate when the
        // loadchildren method loads the children. So, we check...
        //
        if (!this.hasLoaded) {
            this.loadChildren();
            chNode = (EntryNode) this.getChildAt(idx);
        } else {
            chNode = new EntryNode(entry);
            this.insert(chNode, idx);
        }

        model.fireEntryNodeInserted(this, idx, chNode);
    }

    public void
    cvsEntryRemovedChild(CVSEntry.ChildEvent event) {
        int idx = event.getChildIndex();

        EntryRootNode rootNode = (EntryRootNode) this.getRoot();

        EntryTreeModel model =
                (EntryTreeModel) rootNode.getEntryTree().getModel();

        if (idx == -1) {
            this.removeAllChildren();
            model.fireStructureChanged(this);
        } else {
            // NOTE
            // If we have not loaded yet, we must be careful. We should
            // just load the children, since the remove has already
            // occurred there.
            //
            EntryNode remNode = null;

            if (!this.hasLoaded) {
                // NOTE
                // Because of the nature of our dynamic loading
                // we do not have this node, as it NEVER EXISTED!
                // The CVSEntry was removed, so the loadChildren()
                // we are about to call will reflect the correct
                // children and require no EntryNode remove.
                //
                remNode = null;
                this.loadChildren();
            } else {
                remNode = (EntryNode) this.getChildAt(idx);
                this.remove(idx);
            }

            if (remNode != null) {
                model.fireEntryNodeRemoved(this, idx, remNode);
            }
        }
    }

}

