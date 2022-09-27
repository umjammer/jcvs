package com.ice.config.editor;

import java.util.ArrayList;
import java.util.List;

import com.ice.config.ConfigureSpec;
import com.ice.pref.UserPrefs;


public
class ConfigStringArrayEditor
        extends ConfigArrayEditor {

    public ConfigStringArrayEditor() {
        super("String Array");
    }

    public boolean
    isTupleTable(ConfigureSpec spec) {
        return false;
    }

    public boolean
    isStringArray(ConfigureSpec spec) {
        return true;
    }

    public void
    edit(UserPrefs prefs, ConfigureSpec spec) {
        super.edit(prefs, spec);

        List v = prefs.getStringList(spec.getPropertyName(), null);

        if (v != null) {
            this.model.setData(v);
        } else {
            this.model.setData(new ArrayList<>());
        }

        this.table.sizeColumnsToFit(-1);
        this.table.repaint(100);
    }

    public void
    saveChanges(UserPrefs prefs, ConfigureSpec spec) {
        this.table.clearSelection();
        List<String> vStrs = this.model.getData();
        prefs.setStringArray(spec.getPropertyName(), vStrs.toArray(new String[0]));
    }

    public void
    commitChanges(ConfigureSpec spec, UserPrefs prefs, UserPrefs orig) {
        String propName = spec.getPropertyName();
        String[] strs = prefs.getStringArray(propName, null);
        orig.removeStringArray(propName);
        if (strs != null) {
            orig.setStringArray(propName, strs);
        }
    }

}

