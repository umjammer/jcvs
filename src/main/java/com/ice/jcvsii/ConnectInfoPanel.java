/*
 ** Java CVS client application package.
 ** Copyright (c) 1997 by Timothy Gerard Endres
 **
 ** This program is free software.
 **
 ** You may redistribute it and/or modify it under the terms of the GNU
 ** General Public License as published by the Free Software Foundation.
 ** Version 2 of the license should be included with this distribution in
 ** the file LICENSE, as well as License.html. If the license is not
 ** included	with this distribution, you may find a copy at the FSF web
 ** site at 'www.gnu.org' or 'www.fsf.org', or you may write to the
 ** Free Software Foundation, 675 Mass Ave, Cambridge, MA 02139 USA.
 **
 ** THIS SOFTWARE IS PROVIDED AS-IS WITHOUT WARRANTY OF ANY KIND,
 ** NOT EVEN THE IMPLIED WARRANTY OF MERCHANTABILITY. THE AUTHOR
 ** OF THIS SOFTWARE, ASSUMES _NO_ RESPONSIBILITY FOR ANY
 ** CONSEQUENCE RESULTING FROM THE USE, MODIFICATION, OR
 ** REDISTRIBUTION OF THIS SOFTWARE.
 **
 */


package com.ice.jcvsii;

import java.awt.Color;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.ice.cvsc.CVSRequest;
import com.ice.pref.UserPrefs;
import com.ice.util.AWTUtilities;


public
class ConnectInfoPanel
        extends JPanel
        implements ItemListener, ActionListener {
    private JTextField moduleText;
    private JTextField hostNameText;
    private JTextField repositoryText;
    private JTextField argumentsText;
    private JTextField exportDirText;

    private JRadioButton rshRadio;
    private JRadioButton inetdRadio;
    private JCheckBox passwordCheck;

    private JLabel userNameLbl;
    private JTextField userNameText;
    private JLabel passwordLbl;
    private JPasswordField passwordText;


    public ConnectInfoPanel(String operation) {
        super();
        this.establishContents(operation);
    }

    public void
    loadPreferences(String panName) {
        UserPrefs prefs = Config.getPreferences();

        this.setUserName
                (prefs.getProperty
                        (panName + "." + Config.INFOPAN_USER_NAME, ""));
        this.setServer
                (prefs.getProperty
                        (panName + "." + Config.INFOPAN_SERVER_NAME, ""));
        this.setModule
                (prefs.getProperty
                        (panName + "." + Config.INFOPAN_MODULE_NAME, ""));
        this.setRepository
                (prefs.getProperty
                        (panName + "." + Config.INFOPAN_REPOS_NAME, ""));
        this.setExportDirectory
                (prefs.getProperty
                        (panName + "." + Config.INFOPAN_EXPDIR_NAME, ""));
        this.setArguments
                (prefs.getProperty
                        (panName + "." + Config.INFOPAN_ARGS_NAME, ""));
    }

    public void
    savePreferences(String panName) {
        UserPrefs prefs = Config.getPreferences();

        prefs.setProperty
                (panName + "." + Config.INFOPAN_USER_NAME,
                        this.getUserName());
        prefs.setProperty
                (panName + "." + Config.INFOPAN_SERVER_NAME,
                        this.getServer());
        prefs.setProperty
                (panName + "." + Config.INFOPAN_MODULE_NAME,
                        this.getModule());
        prefs.setProperty
                (panName + "." + Config.INFOPAN_REPOS_NAME,
                        this.getRepository());
        prefs.setProperty
                (panName + "." + Config.INFOPAN_EXPDIR_NAME,
                        this.getExportDirectory());
        prefs.setProperty
                (panName + "." + Config.INFOPAN_ARGS_NAME,
                        this.getArguments());
    }

    public void
    setServerMode(boolean state) {
        this.inetdRadio.setSelected(!state);
        this.rshRadio.setSelected(state);
    }

    public void
    setPServerMode(boolean state) {
        this.rshRadio.setSelected(!state);
        this.inetdRadio.setSelected(state);
    }

    public void
    setUsePassword(boolean state) {
        this.passwordCheck.setSelected(state);
    }

    public boolean
    isInetdSelected() {
        return this.inetdRadio.isSelected();
    }

    public boolean
    isPasswordSelected() {
        return this.passwordCheck.isSelected();
    }

    public String
    getUserName() {
        return this.userNameText.getText();
    }

    public void
    setUserName(String name) {
        this.userNameText.setText(name);
    }

    public String
    getPassword() {
        return new String(this.passwordText.getPassword());
    }

    public String
    getModule() {
        return (this.moduleText == null
                ? "" : this.moduleText.getText());
    }

    public void
    setModule(String name) {
        if (this.moduleText != null)
            this.moduleText.setText(name);
    }

    public String
    getServer() {
        return this.hostNameText.getText();
    }

    public void
    setServer(String name) {
        this.hostNameText.setText(name);
    }

    public String
    getRepository() {
        if (repositoryText == null)
            return "";

        String repositorty = this.repositoryText.getText();

        if (repositorty.endsWith("/"))
            repositorty =
                    repositorty.substring(0, repositorty.length() - 1);

        return repositorty;
    }

    public void
    setRepository(String name) {
        if (repositoryText != null)
            this.repositoryText.setText(name);
    }

    public String
    getArguments() {
        return (this.argumentsText == null
                ? "" : this.argumentsText.getText());
    }

    public void
    setArguments(String args) {
        if (argumentsText != null)
            this.argumentsText.setText(args);
    }

    public String
    getExportDirectory() {
        return (this.exportDirText == null
                ? "" : this.exportDirText.getText());
    }

    public void
    setExportDirectory(String dir) {
        if (exportDirText != null)
            this.exportDirText.setText(dir);
    }

    public String
    getImportDirectory() {
        return (this.exportDirText == null
                ? "" : this.exportDirText.getText());
    }

    public void
    setImportDirectory(String dir) {
        if (exportDirText != null)
            this.exportDirText.setText(dir);
    }

    public String
    getLocalDirectory() {
        return (this.exportDirText == null
                ? "" : this.exportDirText.getText());
    }

    public void
    setLocalDirectory(String dir) {
        if (exportDirText != null)
            this.exportDirText.setText(dir);
    }

    public void
    requestInitialFocus() {
        this.userNameText.requestFocus();
    }

    public void
    actionPerformed(ActionEvent evt) {
        if (evt.getSource() == this.userNameText) {
            this.passwordText.requestFocus();
        }
    }

    public void
    itemStateChanged(ItemEvent event) {
        boolean relay = false;
        Object item = event.getItemSelectable();

        if (item == this.inetdRadio) {
            if (this.inetdRadio.isSelected()) {
                this.passwordCheck.setEnabled(true);
                this.passwordCheck.setSelected(true);
                this.userNameLbl.setEnabled(true);
                this.userNameText.setEnabled(true);
                this.passwordText.setEnabled(true);
                this.userNameText.requestFocus();
            }

            relay = true;
        } else if (item == this.rshRadio) {
            if (this.rshRadio.isSelected()) {
                this.passwordCheck.setSelected(false);
                this.passwordCheck.setEnabled(false);
                this.passwordText.setEnabled(false);
                this.userNameLbl.setEnabled(true);
                this.userNameText.setEnabled(true);
                this.userNameText.requestFocus();
            }

            relay = true;
        } else if (item == this.passwordCheck) {
            if (this.passwordCheck.isSelected()) {
                this.userNameLbl.setEnabled(true);
                this.userNameText.setEnabled(true);
                this.passwordText.setEnabled(true);
                this.userNameText.requestFocus();
            } else {
                this.userNameLbl.setEnabled(false);
                this.userNameText.setEnabled(false);
                this.passwordText.setEnabled(false);
            }

            relay = true;
        }

        if (relay) {
            this.invalidate();
            this.validate();
            this.repaint();
        }
    }

    private void
    establishContents(String operation) {
        JLabel lbl;
        int row = 0;

        this.setLayout(new GridBagLayout());

        ResourceMgr rmgr = ResourceMgr.getInstance();

        // ============== INPUT FIELDS PANEL ================

        JPanel fldPan = new JPanel();
        fldPan.setLayout(new GridBagLayout());

        // ------------------- Module -------------------
        if (!operation.equals("test")) {
            lbl = this.new MyLabel
                    (rmgr.getUIString("name.for.cvsmodule"));
            AWTUtilities.constrain(
                    fldPan, lbl,
                    GridBagConstraints.NONE,
                    GridBagConstraints.WEST,
                    0, row, 1, 1, 0.0, 0.0);

            this.moduleText = new JTextField();
            AWTUtilities.constrain(
                    fldPan, this.moduleText,
                    GridBagConstraints.HORIZONTAL,
                    GridBagConstraints.CENTER,
                    1, row++, 1, 1, 1.0, 0.0);
        }

        // ------------------- Server -------------------
        lbl = this.new MyLabel
                (rmgr.getUIString("name.for.cvsserver"));
        AWTUtilities.constrain(
                fldPan, lbl,
                GridBagConstraints.NONE,
                GridBagConstraints.WEST,
                0, row, 1, 1, 0.0, 0.0);

        this.hostNameText = new JTextField();
        AWTUtilities.constrain(
                fldPan, this.hostNameText,
                GridBagConstraints.HORIZONTAL,
                GridBagConstraints.CENTER,
                1, row++, 1, 1, 1.0, 0.0);

        // ------------------- Repository -------------------
        lbl = this.new MyLabel
                (rmgr.getUIString("name.for.cvsrepos"));
        AWTUtilities.constrain(
                fldPan, lbl,
                GridBagConstraints.NONE,
                GridBagConstraints.WEST,
                0, row, 1, 1, 0.0, 0.0);

        this.repositoryText = new JTextField();
        AWTUtilities.constrain(
                fldPan, this.repositoryText,
                GridBagConstraints.HORIZONTAL,
                GridBagConstraints.CENTER,
                1, row++, 1, 1, 1.0, 0.0);

        // ------------------- Export/Import/Checkout Directory -------------------
        if (operation.equals("export")
                || operation.equals("import")
                || operation.equals("checkout")) {
            if (operation.equals("export"))
                lbl = this.new MyLabel
                        (rmgr.getUIString("name.for.exportdir"));
            else if (operation.equals("import"))
                lbl = this.new MyLabel
                        (rmgr.getUIString("name.for.importdir"));
            else if (operation.equals("checkout"))
                lbl = this.new MyLabel
                        (rmgr.getUIString("name.for.checkoutdir"));

            AWTUtilities.constrain(
                    fldPan, lbl,
                    GridBagConstraints.NONE,
                    GridBagConstraints.WEST,
                    0, row, 1, 1, 0.0, 0.0);

            this.exportDirText = new JTextField();
            AWTUtilities.constrain(
                    fldPan, this.exportDirText,
                    GridBagConstraints.HORIZONTAL,
                    GridBagConstraints.CENTER,
                    1, row++, 1, 1, 1.0, 0.0);
        }

        // ------------------- Arguments -------------------
        if (operation.equals("export")
                || operation.equals("checkout")
                || operation.equals("import")) {
            lbl = this.new MyLabel
                    (rmgr.getUIString("name.for.arguments"));
            AWTUtilities.constrain(
                    fldPan, lbl,
                    GridBagConstraints.NONE,
                    GridBagConstraints.WEST,
                    0, row, 1, 1, 0.0, 0.0);

            this.argumentsText = new JTextField();
            AWTUtilities.constrain(
                    fldPan, this.argumentsText,
                    GridBagConstraints.HORIZONTAL,
                    GridBagConstraints.CENTER,
                    1, row++, 1, 1, 1.0, 0.0);
        }


        // ============== SERVER DEFINES DIALOG BUTTON ================

        JButton defBtn =
                new JButton(rmgr.getUIString("name.for.servers.button"));
        defBtn.addActionListener(
                new ActionListener() {
                    public void
                    actionPerformed(ActionEvent evt) {
                        ServersDialog dlg =
                                new ServersDialog
                                        ((Frame) getTopLevelAncestor(),
                                                Config.getPreferences(),
                                                ConnectInfoPanel.this);
                        dlg.show();

                        ServerDef def = dlg.getServerDefinition();

                        if (def != null) {
                            if (moduleText != null)
                                moduleText.setText(def.getModule());

                            if (hostNameText != null)
                                hostNameText.setText(def.getHostName());

                            if (userNameText != null)
                                userNameText.setText(def.getUserName());

                            if (repositoryText != null)
                                repositoryText.setText(def.getRepository());

                            if (def.getConnectMethod() == CVSRequest.METHOD_RSH) {
                                passwordCheck.setSelected(false);
                                rshRadio.setSelected(true);
                            } else {
                                inetdRadio.setSelected(true);
                                passwordCheck.setSelected(def.isPServer());

                                if (def.isPServer())
                                    passwordText.requestFocus();
                                else if (moduleText != null)
                                    moduleText.requestFocus();
                                else
                                    repositoryText.requestFocus();
                            }
                        }
                    }
                }
        );

        // ============== USER LOGIN INFO PANEL ================

        JPanel namePan = new JPanel();
        namePan.setLayout(new GridBagLayout());

        row = 0;

        // server method
        this.rshRadio =
                new JRadioButton
                        (rmgr.getUIString("name.for.connect.method.server"));
        this.rshRadio.addItemListener(this);
        AWTUtilities.constrain(
                namePan, this.rshRadio,
                GridBagConstraints.NONE,
                GridBagConstraints.WEST,
                0, row, 1, 1, 0.0, 0.0);

        this.userNameLbl = this.new MyLabel
                (rmgr.getUIString("name.for.user.name"));
        this.userNameLbl.setForeground(Color.black);
        AWTUtilities.constrain(
                namePan, this.userNameLbl,
                GridBagConstraints.NONE,
                GridBagConstraints.EAST,
                1, row, 1, 1, 0.0, 0.0);

        this.userNameText = new JTextField();
        this.userNameText.addActionListener(this);

        AWTUtilities.constrain(
                namePan, this.userNameText,
                GridBagConstraints.HORIZONTAL,
                GridBagConstraints.WEST,
                2, row++, 1, 1, 1.0, 0.0);

        // pserver method
        this.inetdRadio =
                new JRadioButton
                        (rmgr.getUIString("name.for.connect.method.pserver"));
        this.inetdRadio.addItemListener(this);
        AWTUtilities.constrain(
                namePan, this.inetdRadio,
                GridBagConstraints.NONE,
                GridBagConstraints.WEST,
                0, row, 1, 1, 0.0, 0.0);

        // Password Checkbox
        this.passwordCheck =
                new JCheckBox(rmgr.getUIString("name.for.user.pass"));
        this.passwordCheck.addItemListener(this);
        AWTUtilities.constrain(
                namePan, this.passwordCheck,
                GridBagConstraints.NONE,
                GridBagConstraints.EAST,
                1, row, 1, 1, 0.0, 0.0);

        this.passwordText = new JPasswordField();
        this.passwordText.setEchoChar('*');
        AWTUtilities.constrain(
                namePan, this.passwordText,
                GridBagConstraints.HORIZONTAL,
                GridBagConstraints.WEST,
                2, row++, 1, 1, 1.0, 0.0);


        ButtonGroup btnGrp = new ButtonGroup();
        btnGrp.add(this.rshRadio);
        btnGrp.add(this.inetdRadio);

        row = 0;

        JPanel topPan = new JPanel();
        topPan.setLayout(new GridBagLayout());

        defBtn.setMargin(new Insets(4, 8, 4, 8));
        AWTUtilities.constrain(
                topPan, defBtn,
                GridBagConstraints.NONE,
                GridBagConstraints.CENTER,
                0, row, 1, 1, 0.0, 0.0,
                new Insets(4, 10, 4, 15));

        AWTUtilities.constrain(
                topPan, new JSeparator(SwingConstants.VERTICAL),
                GridBagConstraints.VERTICAL,
                GridBagConstraints.CENTER,
                1, row, 1, 1, 0.0, 1.0,
                new Insets(0, 0, 0, 10));

        AWTUtilities.constrain(
                topPan, namePan,
                GridBagConstraints.HORIZONTAL,
                GridBagConstraints.CENTER,
                2, row++, 1, 1, 0.7, 0.0);

        row = 0;

        AWTUtilities.constrain(
                this, topPan,
                GridBagConstraints.HORIZONTAL,
                GridBagConstraints.CENTER,
                0, row++, 1, 1, 1.0, 0.0);

        JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);

        AWTUtilities.constrain(
                this, sep,
                GridBagConstraints.HORIZONTAL,
                GridBagConstraints.CENTER,
                0, row++, 1, 1, 1.0, 0.0,
                new Insets(3, 0, 5, 0));

        AWTUtilities.constrain(
                this, fldPan,
                GridBagConstraints.HORIZONTAL,
                GridBagConstraints.WEST,
                0, row++, 1, 1, 1.0, 0.0);
    }

    private
    class MyLabel
            extends JLabel {
        public MyLabel(String text) {
            super(text);
            this.setBorder(new EmptyBorder(0, 3, 0, 5));
        }
    }

}

