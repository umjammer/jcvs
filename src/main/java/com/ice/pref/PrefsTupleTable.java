/*
 ** User Preferences Package.
 ** Copyright (c) 1999 by Timothy Gerard Endres
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

package com.ice.pref;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;


public
class PrefsTupleTable
        extends Hashtable {
    private List keyOrder = new ArrayList<>();

    public PrefsTupleTable() {
        super();
    }

    public boolean
    equals(PrefsTupleTable that) {
        if (this.size() != that.size())
            return false;

        for (int i = 0; i < this.keyOrder.size(); ++i) {
            String key =
                    (String) this.keyOrder.get(i);

            PrefsTuple thisTup = this.getTuple(key);
            PrefsTuple thatTup = that.getTuple(key);

            if (thisTup == null || thatTup == null)
                return false;

            if (!thisTup.equals(thatTup))
                return false;
        }

        return true;
    }

    public List
    getKeyOrder() {
        return this.keyOrder;
    }

    public PrefsTuple
    getTuple(String key) {
        Object o = this.get(key);
        return (PrefsTuple) this.get(key);
    }

    public PrefsTuple
    getTupleAt(int idx) {
        if (idx < 0 || idx >= this.keyOrder.size())
            return null;

        return (PrefsTuple)
                this.get(this.keyOrder.get(idx));
    }

    public PrefsTuple
    setTupleAt(PrefsTuple tup, int idx) {
        String key = (String) this.keyOrder.get(idx);

        PrefsTuple remTup = (PrefsTuple) this.remove(key);

        this.keyOrder.set(idx, tup.getKey());

        this.put(tup.getKey(), tup);

        return remTup;
    }

    public void
    removeTuple(PrefsTuple tup) {
        this.keyOrder.remove(tup);
        this.remove(tup);
    }

    public void
    removeTupleAt(int idx) {
        if (idx < 0 || idx >= this.keyOrder.size())
            return;

        this.remove(this.keyOrder.get(idx));
        this.keyOrder.remove(idx);
    }

    public void
    insertTupleAt(PrefsTuple tup, int idx) {
        if (idx < 0 || idx >= this.keyOrder.size())
            return;

        this.put(tup.getKey(), tup);
        this.keyOrder.set(idx, tup.getKey());
    }

    public void
    appendTuple(PrefsTuple tup) {
        this.put(tup.getKey(), tup);
        this.keyOrder.add(tup.getKey());
    }

    public void
    putTuple(PrefsTuple tuple) {
        if (this.get(tuple.getKey()) == null)
            this.keyOrder.add(tuple.getKey());
        this.put(tuple.getKey(), tuple);
    }

    public int
    getMaximumTupleLength() {
        int max = 0;

        Enumeration e = this.elements();
        for (; e.hasMoreElements(); ) {
            PrefsTuple tup = (PrefsTuple) e.nextElement();
            if (tup.length() > max)
                max = tup.length();
        }

        return max;
    }

    public String
    toSting() {
        return "[PrefsTupleTable [size=" + this.size() + ","
                + super.toString() + "]";
    }

}

