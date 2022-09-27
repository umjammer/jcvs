package com.ice.config;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.tree.DefaultTreeModel;


public
class ConfigureTreeModel
        extends DefaultTreeModel {
    public ConfigureTreeModel() {
        super(new ConfigureTreeNode("Prefs"));
    }

    public ConfigureTreeNode
    addPath(String path, ConfigureSpec spec) {
        StringTokenizer tokenizer =
                new StringTokenizer(path, ".", false);

        ConfigureTreeNode node, next;
        node = (ConfigureTreeNode) getRoot();

        for (; tokenizer.hasMoreTokens(); ) {
            String name = tokenizer.nextToken();
            next = node.getChild(name);

            if (next == null) {
                next = new ConfigureTreeNode(name, spec);
                node.add(next);
            }

            node = next;
        }

        node.setPropertySpec(spec);

        return node;
    }

    public ConfigureTreeNode
    getPathNode(String path) {
        StringTokenizer tokenizer =
                new StringTokenizer(path, ".", false);

        ConfigureTreeNode node, next;
        node = (ConfigureTreeNode) getRoot();

        for (; node != null && tokenizer.hasMoreTokens(); ) {
            node = node.getChild(tokenizer.nextToken());
        }

        return node;
    }

    public List
    getAllPaths() {
        List List = new ArrayList<>();
        getAllPaths("", List, (ConfigureTreeNode) getRoot());
        return List;
    }

    private void
    getAllPaths(String path, List List, ConfigureTreeNode node) {
        ConfigureTreeNode child;
        int count = node.getChildCount();

        for (int i = 0; i < count; i++) {
            String next = null;
            child = (ConfigureTreeNode) node.getChildAt(i);
            String name = child.getName();

            if (path.length() == 0)
                next = name;
            else
                next = path + "." + name;

            List.add(next);

            getAllPaths(next, List, child);
        }
    }
}
