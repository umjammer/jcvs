package com.ice.config.editor;

import com.ice.config.ConfigureSpec;
import com.ice.pref.UserPrefs;


public class ConfigLongEditor extends ConfigNumberEditor {

    public ConfigLongEditor() {
        super("Long");
    }

    @Override
    public String getTypeTitle() {
        return "Long";
    }

    @Override
    public String formatNumber(UserPrefs prefs, ConfigureSpec spec) {
        long num = prefs.getLong(spec.getPropertyName(), 0);

        return Long.toString(num);
    }

    @Override
    public boolean isChanged(UserPrefs prefs, ConfigureSpec spec, String numText) {
        long cur = Long.parseLong(numText);
        long old = prefs.getLong(spec.getPropertyName(), 0);
        return (cur != old);
    }
}
