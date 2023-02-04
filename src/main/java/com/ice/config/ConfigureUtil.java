package com.ice.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ice.pref.UserPrefs;

public class ConfigureUtil implements ConfigureConstants {

    public static List<ConfigureSpec> readConfigSpecification(UserPrefs specs) throws InvalidSpecificationException {
        String specSfx = ".spec";
        List<ConfigureSpec> result = new ArrayList<>();
        String propPfx = specs.getPropertyPrefix();

        for (Object key_ : specs.keySet()) {
            String key = (String) key_;

            if (!key.endsWith(specSfx)) continue;

            // Strip off the property prefix to prepare for getProperty().
            String keyBase = specs.getBaseName(key);

            // Get the property type using this key.
            String type = specs.getProperty(keyBase, null);

            // Strip off the ".spec" suffix.
            keyBase = keyBase.substring(0, (keyBase.length() - specSfx.length()));

            // Get the other property parameters using the various suffixes.
            String path = specs.getProperty(keyBase + ".path", null);
            String name = specs.getProperty(keyBase + ".name", null);
            String desc = specs.getProperty(keyBase + ".desc", null);
            String help = specs.getProperty(keyBase + ".help", null);

            String reason = "";
            boolean invalid = false;
            if (type == null) {
                invalid = true;
                reason = "the spec has no property type";
            } else if (path == null) {
                invalid = true;
                reason = "the spec has no config tree path";
            } else if (name == null) {
                invalid = true;
                reason = "the spec has no property name";
            } else if (type.equals("choice")) {
                List<String> sV = new ArrayList<>();
                for (int ci = 0; ci < 32; ++ci) {
                    String item = specs.getProperty((keyBase + ".choice." + ci), null);
                    if (item == null) break;
                    sV.add(item);
                }

                if (sV.size() < 2) {
                    invalid = true;
                    reason = "choice config has no choices (need 2 or more)";
                } else {
                    String[] choices = new String[sV.size()];
                    sV.addAll(Arrays.asList(choices));

                    ConfigureSpec spec = new ConfigureSpec(keyBase, type.trim(), path.trim(), name.trim(), ((desc == null) ? desc : desc.trim()), ((help == null) ? help : help.trim()), choices);

                    result.add(spec);
                }
            } else {
                ConfigureSpec spec = new ConfigureSpec(keyBase, type.trim(), path.trim(), name.trim(), ((desc == null) ? desc : desc.trim()), ((help == null) ? help : help.trim()), null);

                result.add(spec);
            }

            if (invalid) {
                throw new InvalidSpecificationException("invalid configuration specification for '" + keyBase + "', " + reason);
            }
        }

        return result;
    }
}
