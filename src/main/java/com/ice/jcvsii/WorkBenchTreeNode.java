package com.ice.jcvsii;

import java.util.Enumeration;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

public class WorkBenchTreeNode extends DefaultMutableTreeNode {

    public WorkBenchTreeNode(WorkBenchDefinition def) {
        super(def);
    }

    public String toString() {
        return this.getDefinition().getDisplayName();
    }

    public WorkBenchDefinition getDefinition() {
        return (WorkBenchDefinition) getUserObject();
    }

    @Override
    public boolean isLeaf() {
        return !this.getDefinition().isFolder();
    }

    public String getPathString() {
        TreeNode[] path = this.getPath();
        StringBuilder buf = new StringBuilder();

        for (int i = 0; i < path.length; ++i) {
            WorkBenchTreeNode node = (WorkBenchTreeNode) path[i];
            buf.append(node.getDefinition().getName());
            if (i < (path.length - 1)) buf.append(".");
        }

        return buf.toString();
    }

    public WorkBenchTreeNode[] getChildren() {
        WorkBenchTreeNode[] result = new WorkBenchTreeNode[this.getChildCount()];

        Enumeration<?> e = this.children();
        for (int i = 0; e.hasMoreElements(); ++i)
            result[i] = (WorkBenchTreeNode) e.nextElement();

        return result;
    }

    public WorkBenchDefinition[] getChildDefinitions() {
        int cnt = this.getChildCount();

        WorkBenchDefinition[] result = new WorkBenchDefinition[cnt];

        Enumeration<?> e = this.children();
        for (int i = 0; e.hasMoreElements(); ++i) {
            result[i] = ((WorkBenchTreeNode) e.nextElement()).getDefinition();
        }

        return result;
    }
}
