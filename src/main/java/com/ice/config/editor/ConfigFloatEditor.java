package com.ice.config.editor;

import com.ice.config.ConfigureSpec;
import com.ice.pref.UserPrefs;


public class ConfigFloatEditor extends ConfigNumberEditor {

    public ConfigFloatEditor() {
        super("Double");
    }

    @Override
    public String getTypeTitle() {
        return "Float";
    }

    @Override
    public String formatNumber(UserPrefs prefs, ConfigureSpec spec) {
        float num = prefs.getFloat(spec.getPropertyName(), 0.0F);

        return Float.toString(num);
    }

    @Override
    public boolean isChanged(UserPrefs prefs, ConfigureSpec spec, String numText) {
        float cur = Float.parseFloat(numText);
        float old = prefs.getFloat(spec.getPropertyName(), 0);
        return (cur != old);
    }
}
