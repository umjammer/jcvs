package com.ice.config;

import java.util.HashMap;
import java.util.Map;

import com.ice.config.editor.ConfigBooleanEditor;
import com.ice.config.editor.ConfigChoiceEditor;
import com.ice.config.editor.ConfigColorEditor;
import com.ice.config.editor.ConfigDimensionEditor;
import com.ice.config.editor.ConfigDoubleEditor;
import com.ice.config.editor.ConfigFloatEditor;
import com.ice.config.editor.ConfigFontEditor;
import com.ice.config.editor.ConfigIntegerEditor;
import com.ice.config.editor.ConfigLongEditor;
import com.ice.config.editor.ConfigPointEditor;
import com.ice.config.editor.ConfigRectangleEditor;
import com.ice.config.editor.ConfigStringArrayEditor;
import com.ice.config.editor.ConfigStringEditor;
import com.ice.config.editor.ConfigTokenEditor;
import com.ice.config.editor.ConfigTupleTableEditor;
import com.ice.pref.UserPrefs;

public class DefaultConfigureEditorFactory implements ConfigureEditorFactory, ConfigureConstants {

    protected Map<String, ConfigureEditor> editors;
    protected UserPrefs editSpecs;

    public DefaultConfigureEditorFactory(UserPrefs specs) {
        this.editSpecs = specs;

        this.editors = new HashMap<>();

        this.editors.put(CFG_DEFAULT, this.createDefaultEditor(CFG_STRING));

        this.editors.put(CFG_STRING, this.createDefaultEditor(CFG_STRING));
    }

    @Override
    public ConfigureEditor createEditor(String propertyType) {
        ConfigureEditor result = this.editors.get(propertyType);

        if (result == null) {
            result = createDefaultEditor(propertyType);

            if (result != null) {
                this.editors.put(propertyType, result);
            }
        }

        return result;
    }

    public void addEditor(String propertyType, ConfigureEditor editor) {
        this.editors.put(propertyType, editor);
    }

    public void removeEditor(String propertyType) {
        this.editors.remove(propertyType);
    }

    private ConfigureEditor createDefaultEditor(String propertyType) {
        if (propertyType.equalsIgnoreCase(CFG_STRING)) return new ConfigStringEditor();
        else if (propertyType.equalsIgnoreCase(CFG_POINT)) return new ConfigPointEditor();
        else if (propertyType.equalsIgnoreCase(CFG_DIMENSION)) return new ConfigDimensionEditor();
        else if (propertyType.equalsIgnoreCase(CFG_RECTANGLE)) return new ConfigRectangleEditor();
        else if (propertyType.equalsIgnoreCase(CFG_BOOLEAN)) return new ConfigBooleanEditor();
        else if (propertyType.equalsIgnoreCase(CFG_INTEGER)) return new ConfigIntegerEditor();
        else if (propertyType.equalsIgnoreCase(CFG_LONG)) return new ConfigLongEditor();
        else if (propertyType.equalsIgnoreCase(CFG_FLOAT)) return new ConfigFloatEditor();
        else if (propertyType.equalsIgnoreCase(CFG_DOUBLE)) return new ConfigDoubleEditor();
        else if (propertyType.equalsIgnoreCase(CFG_COLOR)) return new ConfigColorEditor();
        else if (propertyType.equalsIgnoreCase(CFG_FONT)) return new ConfigFontEditor();
        else if (propertyType.equalsIgnoreCase(CFG_CHOICE)) return new ConfigChoiceEditor();
        else if (propertyType.equalsIgnoreCase(CFG_STRINGARRAY)) return new ConfigStringArrayEditor();
        else if (propertyType.equalsIgnoreCase(CFG_TOKENS)) return new ConfigTokenEditor();
        else if (propertyType.equalsIgnoreCase(CFG_TUPLETABLE)) return new ConfigTupleTableEditor();
        return null;
    }
}
