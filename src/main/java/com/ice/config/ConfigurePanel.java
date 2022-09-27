package com.ice.config;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import com.ice.pref.UserPrefs;


public
class ConfigurePanel
        extends JPanel
        implements ConfigureConstants, TreeSelectionListener {
    protected JTree tree = null;
    protected JLabel title = null;
    protected JPanel editorPanel = null;
    protected JSplitPane splitter = null;

    protected UserPrefs specs;
    protected List<ConfigureSpec> specList;

    protected UserPrefs prefs;
    protected UserPrefs origPrefs;

    protected ConfigureTreeModel model = null;
    protected ConfigureEditor currEditor = null;
    protected ConfigureTreeNode currSelection = null;
    protected Properties template = new Properties();

    protected ConfigureEditorFactory factory = null;


    public ConfigurePanel(UserPrefs cfgPrefs, UserPrefs specs) {
        this(cfgPrefs, specs,
                new DefaultConfigureEditorFactory(specs));
    }

    public ConfigurePanel
            (UserPrefs cfgPrefs, UserPrefs specs,
             ConfigureEditorFactory factory) {
        this.origPrefs = cfgPrefs;

        this.prefs =
                cfgPrefs.createWorkingCopy
                        ("Configuration Working Copy");

        this.specs = specs;

        this.setLayout(new BorderLayout());
        this.setBorder(new EmptyBorder(5, 5, 5, 5));

        this.model = new ConfigureTreeModel();

        try {
            this.specList =
                    ConfigureUtil.readConfigSpecification(specs);
        } catch (InvalidSpecificationException ex) {
            // REVIEW
            // UNDONE
            // Should we not throw this?
            ex.printStackTrace();
            this.specList = new ArrayList<>();
        }

        this.establishConfigTree();

        this.tree = new ConfigureTree(model);
        this.tree.addTreeSelectionListener(this);

        JScrollPane treeScroller = new JScrollPane(this.tree);

        JPanel pan = new JPanel();
        pan.setLayout(new BorderLayout());
        pan.setPreferredSize(new Dimension(125, 225));
        pan.add(BorderLayout.CENTER, treeScroller);

        this.editorPanel = new EditorPanel();
        this.editorPanel.setLayout(new BorderLayout());
        this.editorPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());

        this.title = new JLabel("Properties", JLabel.LEFT);
        this.title.setPreferredSize(new Dimension(30, 30));
        this.title.setBackground(new Color(224, 224, 255));
        this.title.setForeground(Color.black);
        this.title.setOpaque(true);
        this.title.setFont
                (new Font(this.getFont().getName(), Font.BOLD, 14));
        this.title.setBorder
                (new CompoundBorder
                        (new LineBorder(Color.black),
                                new EmptyBorder(5, 5, 5, 5)));

        this.splitter =
                new JSplitPane
                        (JSplitPane.HORIZONTAL_SPLIT,
                                true, pan, contentPanel);

        this.splitter.setDividerSize(5);

        contentPanel.add(BorderLayout.NORTH, this.title);

        contentPanel.add(BorderLayout.CENTER, this.editorPanel);

        this.add(BorderLayout.CENTER, this.splitter);

        this.factory = factory;
    }

    public void
    setDividerLocation(double divPct) {
        this.splitter.setDividerLocation(divPct);
    }

    public ConfigureEditorFactory
    getEditorFactory() {
        return this.factory;
    }

    public void
    setEditorFactory(ConfigureEditorFactory factory) {
        this.factory = factory;
    }

    public void
    commit() {
        for (ConfigureSpec spec : this.specList) {

            ConfigureEditor editor =
                    this.factory.createEditor(spec.getPropertyType());

            if (editor != null) {
                editor.commit(spec, this.prefs, this.origPrefs);
            } else {
                // UNDONE report this!!!
            }
        }
    }

    private void
    establishConfigTree() {
        for (ConfigureSpec spec : this.specList) {
            String path = spec.getPropertyPath();

            this.model.addPath(path, spec);
        }
    }

    public void
    saveCurrentEdit() {
        if (this.currSelection != null && this.currEditor != null) {
            this.currEditor.saveChanges
                    (this.prefs, this.currSelection.getConfigureSpec());
        }
    }

    public void
    valueChanged(TreeSelectionEvent event) {
        Object obj = tree.getLastSelectedPathComponent();

        if (obj == this.currSelection)
            return;

        this.saveCurrentEdit();

        synchronized (this.editorPanel.getTreeLock()) {
            this.editorPanel.removeAll();
            title.setText("");

            if (obj != null) {
                ConfigureTreeNode node =
                        this.currSelection =
                                (ConfigureTreeNode) obj;

                if (node.isLeaf()) {
                    ConfigureSpec spec = node.getConfigureSpec();

                    if (spec != null) {
                        this.currEditor =
                                this.factory.createEditor
                                        (node.getConfigureSpec().getPropertyType());

                        if (this.currEditor == null)
                            this.currEditor =
                                    this.factory.createEditor(CFG_DEFAULT);

                        StringBuffer sb = new StringBuffer();

                        sb.append(spec.getName());

                        if (this.currEditor != null) {
                            if (this.currEditor.isModified
                                    (spec, this.prefs, this.origPrefs)) {
                                sb.append(" *");
                            }
                        } else {
                            sb.append(" (NO EDITOR)");
                        }

                        title.setText(sb.toString());

                        if (this.currEditor != null) {
                            this.currEditor.edit(this.prefs, spec);

                            this.editorPanel.add
                                    (BorderLayout.CENTER, this.currEditor);

                            this.editorPanel.revalidate();

                            this.currEditor.requestInitialFocus();
                        }
                    }
                } else {
                    this.currEditor = null;
                    this.currSelection = null;
                    title.setText(node.getName());
                }
            }
        }

        this.editorPanel.repaint(250);
    }

    public void
    addEditor(String type, ConfigureEditor editor) {
        if (this.factory instanceof DefaultConfigureEditorFactory) {
            ((DefaultConfigureEditorFactory) this.factory).addEditor
                    (type, editor);
        } else {
            (new Throwable
                    ("can not add editor, factory is not class "
                            + "DefaultConfigureEditorFactory")).
                    printStackTrace();
        }
    }

    public String
    treePath(TreePath treePath) {
        ConfigureTreeNode node;
        Object[] list = treePath.getPath();
        StringBuffer path = new StringBuffer();

        for (int i = 1; i < list.length; i++) {
            node = (ConfigureTreeNode) list[i];
            if (i > 1)
                path.append(".");
            path.append(node.getName());
        }

        return path.toString();
    }

    public void
    editProperty(String propName) {
        String[] propNames = {propName};
        this.editProperties(propNames);
    }

    public void
    editProperties(String[] propNames) {
        int numSpecs = this.specList.size();

        List<String> pathV = new ArrayList<>();

        for (int i = propNames.length - 1; i >= 0; --i) {
            String propName = propNames[i];

            for (ConfigureSpec spec : this.specList) {

                if (spec.getPropertyName().equals(propName)) {
                    pathV.add(spec.getPropertyPath());
                    break;
                }
            }
        }

        if (pathV.size() > 0) {
            this.editPaths(pathV.toArray(new String[0]));
        }
    }

    public void
    editPath(String path) {
        String[] paths = {path};
        this.editPaths(paths);
    }

    public void
    editPaths(String[] paths) {
        for (int i = paths.length - 1; i >= 0; --i) {
            ConfigureTreeNode node =
                    this.model.getPathNode(paths[i]);

            if (node != null) {
                TreePath tPath = new TreePath(node.getPath());
                this.tree.expandPath(tPath);
                if (i == 0)
                    this.tree.setSelectionPath(tPath);
            }
        }
    }


    /**
     * This panel is used by the editor panel so that we can tell
     * the scroll pane we are inside to track out width with the
     * viewport. This essentially eliminates horizontal scrolling
     * which is quite ugly in this context.
     */
    private
    class EditorPanel
            extends JPanel
            implements Scrollable {
        public Dimension
        getPreferredScrollableViewportSize() {
            return this.getPreferredSize();
        }

        public int
        getScrollableBlockIncrement
                (Rectangle visibleRect, int orientation, int direction) {
            if (orientation == SwingConstants.VERTICAL)
                return visibleRect.height - 10;
            else
                return visibleRect.width - 10;
        }

        public boolean
        getScrollableTracksViewportHeight() {
            return false;
        }

        public boolean
        getScrollableTracksViewportWidth() {
            return true;
        }

        public int
        getScrollableUnitIncrement
                (Rectangle visibleRect, int orientation, int direction) {
            if (orientation == SwingConstants.VERTICAL) {
                int unit = visibleRect.height / 10;
                return (unit == 0 ? 1 : (unit > 20 ? 20 : unit));
            } else {
                int unit = visibleRect.width / 10;
                return (unit == 0 ? 1 : (unit > 20 ? 20 : unit));
            }
        }
    }
}
