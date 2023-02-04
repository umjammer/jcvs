package com.ice.config.editor;

import com.ice.config.ConfigureSpec;
import com.ice.pref.UserPrefs;


public class ConfigIntegerEditor extends ConfigNumberEditor {

    public ConfigIntegerEditor() {
        super("Integer");
    }

    @Override
    public String getTypeTitle() {
        return "Integer";
    }

    @Override
    public String formatNumber(UserPrefs prefs, ConfigureSpec spec) {
        int num = prefs.getInteger(spec.getPropertyName(), 0);

        return Integer.toString(num);
    }

    @Override
    public boolean isChanged(UserPrefs prefs, ConfigureSpec spec, String numText) {
        int cur = Integer.parseInt(numText);
        int old = prefs.getInteger(spec.getPropertyName(), 0);
        return (cur != old);
    }
}
