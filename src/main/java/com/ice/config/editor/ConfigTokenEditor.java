package com.ice.config.editor;

import java.util.ArrayList;
import java.util.List;

import com.ice.config.ConfigureSpec;
import com.ice.pref.UserPrefs;


public class ConfigTokenEditor extends ConfigArrayEditor {

    public ConfigTokenEditor() {
        super("Tokens");
    }

    public void edit(UserPrefs prefs, ConfigureSpec spec) {
        super.edit(prefs, spec);

        String[] tokes = prefs.getTokens(spec.getPropertyName(), null);

        if (tokes != null) {
            List v = new ArrayList<>();
            for (int i = 0; i < tokes.length; ++i)
                v.add(tokes[i]);
            this.model.setData(v);
        } else {
            this.model.setData(new ArrayList<>());
        }

        this.table.sizeColumnsToFit(-1);
        this.table.repaint(100);
    }

    public void saveChanges(UserPrefs prefs, ConfigureSpec spec) {
        List<String> vTokes = this.model.getData();
        prefs.setTokens(spec.getPropertyName(), vTokes.toArray(new String[0]));
    }
}

