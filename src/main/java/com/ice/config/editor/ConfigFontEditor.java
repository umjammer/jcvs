package com.ice.config.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.ice.config.ConfigureEditor;
import com.ice.config.ConfigureSpec;
import com.ice.pref.UserPrefs;
import com.ice.util.AWTUtilities;


public class ConfigFontEditor extends ConfigureEditor
        implements FocusListener, ItemListener, ChangeListener {

    protected JComboBox<String> fontName;
    protected JTextField sizeField;
    protected JCheckBox boldCheck;
    protected JCheckBox italicCheck;
    protected JLabel exLabel;

    public ConfigFontEditor() {
        super("Text Font");
    }

    protected Font getConfiguredFont() throws NumberFormatException {
        String name = (String) this.fontName.getSelectedItem();
        int size = Integer.parseInt(this.sizeField.getText());
        int style = Font.PLAIN;
        if (this.boldCheck.isSelected()) style |= Font.BOLD;
        if (this.italicCheck.isSelected()) style |= Font.ITALIC;
        return new Font(name, style, size);
    }

    @Override
    public void edit(UserPrefs prefs, ConfigureSpec spec) {
        super.edit(prefs, spec);

        Font font = prefs.getFont(spec.getPropertyName(), null);

        if (font != null) {
            this.fontName.setSelectedItem(font.getName());
            this.sizeField.setText(Integer.toString(font.getSize()));
            this.boldCheck.setSelected(font.isBold());
            this.italicCheck.setSelected(font.isItalic());
        } else {
            this.fontName.setSelectedItem("Monospaced");
            this.sizeField.setText("12");
            this.boldCheck.setSelected(false);
            this.italicCheck.setSelected(false);
        }
    }

    @Override
    public void saveChanges(UserPrefs prefs, ConfigureSpec spec) {
        String propName = spec.getPropertyName();

        try {
            Font newVal = this.getConfiguredFont();

            Font oldVal = prefs.getFont(propName, new Font("Serif", Font.PLAIN, 12));

            if (!newVal.equals(oldVal)) {
                prefs.setFont(propName, newVal);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "the font size field is valid, " + ex.getMessage(), "Invalid Size", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void requestInitialFocus() {
        this.sizeField.requestFocus();
        this.sizeField.selectAll();
    }

    private void showConfiguredFont() {
        try {
            Font f = this.getConfiguredFont();
            this.exLabel.setFont(f);
            this.exLabel.repaint(250);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "the font size field is valid, " + ex.getMessage(), "Invalid Size", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void stateChanged(ChangeEvent event) {
        this.showConfiguredFont();
    }

    @Override
    public void itemStateChanged(ItemEvent event) {
        this.showConfiguredFont();
    }

    @Override
    public void focusGained(FocusEvent event) {
        this.showConfiguredFont();

        Component comp = event.getComponent();
        if (comp instanceof JTextField) ((JTextField) comp).selectAll();
    }

    @Override
    public void focusLost(FocusEvent event) {
        this.showConfiguredFont();
    }

    @Override
    protected JPanel createEditPanel() {
        JPanel result = new JPanel();
        result.setLayout(new GridBagLayout());
        result.setBorder(new EmptyBorder(5, 3, 3, 3));

        int col = 0;
        int row = 0;

        JLabel lbl = new JLabel("Font");
        lbl.setBorder(new EmptyBorder(1, 3, 1, 3));
        AWTUtilities.constrain(result, lbl, GridBagConstraints.NONE, GridBagConstraints.WEST, col++, row, 1, 1, 0.0, 0.0);

        this.fontName = new JComboBox<>(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());

        this.fontName.addItemListener(this);
        this.fontName.addFocusListener(this);
        AWTUtilities.constrain(result, this.fontName, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, col++, row++, 1, 1, 1.0, 0.0);

        col = 0;
        lbl = new JLabel("Size");
        lbl.setBorder(new EmptyBorder(1, 3, 1, 3));
        AWTUtilities.constrain(result, lbl, GridBagConstraints.NONE, GridBagConstraints.WEST, col++, row, 1, 1, 0.0, 0.0);

        this.sizeField = new JTextField("0");
        this.sizeField.addFocusListener(this);
        AWTUtilities.constrain(result, this.sizeField, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, col++, row++, 1, 1, 1.0, 0.0);

        col = 0;

        JPanel chkPan = new JPanel();
        chkPan.setLayout(new GridBagLayout());
        chkPan.setBorder(new EmptyBorder(5, 5, 5, 5));
        AWTUtilities.constrain(result, chkPan, GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER, 0, row++, 2, 1, 1.0, 0.0);

        this.boldCheck = new JCheckBox("Bold");
        this.boldCheck.addFocusListener(this);
        this.boldCheck.addChangeListener(this);
        this.boldCheck.setHorizontalAlignment(SwingConstants.CENTER);

        this.italicCheck = new JCheckBox("Italic") {
            @Override
            public Component getNextFocusableComponent() {
                return fontName;
            }
        };
        this.italicCheck.addFocusListener(this);
        this.italicCheck.addChangeListener(this);
        this.italicCheck.setHorizontalAlignment(SwingConstants.CENTER);

        AWTUtilities.constrain(chkPan, this.boldCheck, GridBagConstraints.NONE, GridBagConstraints.CENTER, 0, 0, 1, 1, 0.5, 0.0);

        AWTUtilities.constrain(chkPan, this.italicCheck, GridBagConstraints.NONE, GridBagConstraints.CENTER, 1, 0, 1, 1, 0.5, 0.0);

        JPanel exPan = new JPanel();
        exPan.setLayout(new BorderLayout());
        exPan.setBorder(new CompoundBorder(new EmptyBorder(5, 5, 5, 5), new CompoundBorder(new EtchedBorder(EtchedBorder.RAISED), new EmptyBorder(5, 10, 5, 10))));

        this.exLabel = new JLabel("Sample");
        this.exLabel.setForeground(Color.black);
        this.exLabel.setHorizontalAlignment(SwingConstants.CENTER);

        exPan.add("Center", this.exLabel);
        AWTUtilities.constrain(result, exPan, GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER, 0, row++, 2, 1, 1.0, 0.0);

        return result;
    }

}
