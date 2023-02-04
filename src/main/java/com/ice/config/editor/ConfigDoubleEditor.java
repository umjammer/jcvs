package com.ice.config.editor;

import com.ice.config.ConfigureSpec;
import com.ice.pref.UserPrefs;


public class ConfigDoubleEditor extends ConfigNumberEditor {

    public ConfigDoubleEditor() {
        super("Double");
    }

    @Override
    public String getTypeTitle() {
        return "Double";
    }

    @Override
    public String formatNumber(UserPrefs prefs, ConfigureSpec spec) {
        double dbl = prefs.getDouble(spec.getPropertyName(), 0.0);

        return Double.toString(dbl);
    }

    @Override
    public boolean isChanged(UserPrefs prefs, ConfigureSpec spec, String numText) {
        double cur = Double.parseDouble(numText);
        double old = prefs.getDouble(spec.getPropertyName(), 0);
        return (cur != old);
    }

}
