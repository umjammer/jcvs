package com.ice.config.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import com.ice.config.ConfigureEditor;
import com.ice.config.ConfigureSpec;
import com.ice.pref.UserPrefs;
import com.ice.util.AWTUtilities;


public class ConfigColorEditor extends ConfigureEditor implements FocusListener, ActionListener {
    protected JTextField rField;
    protected JTextField gField;
    protected JTextField bField;
    protected JColorButton color;

    public ConfigColorEditor() {
        super("RGB Color");
    }

    public void edit(UserPrefs prefs, ConfigureSpec spec) {
        super.edit(prefs, spec);

        Color color = prefs.getColor(spec.getPropertyName(), null);

        if (color != null) {
            this.rField.setText(Integer.toString(color.getRed()));
            this.gField.setText(Integer.toString(color.getGreen()));
            this.bField.setText(Integer.toString(color.getBlue()));
        } else {
            this.rField.setText("0");
            this.gField.setText("0");
            this.bField.setText("0");
        }
    }

    public void saveChanges(UserPrefs prefs, ConfigureSpec spec) {
        String propName = spec.getPropertyName();

        try {
            int r = Integer.parseInt(this.rField.getText());
            int g = Integer.parseInt(this.gField.getText());
            int b = Integer.parseInt(this.bField.getText());

            Color newVal = new Color(r, g, b);
            Color oldVal = prefs.getColor(propName, Color.black);

            if (!newVal.equals(oldVal)) {
                prefs.setColor(propName, newVal);
            }
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
        }
    }

    public void actionPerformed(ActionEvent event) {
        String cmdStr = event.getActionCommand();

        if (cmdStr.equals("COLORBUTTON")) {
            JComponent cb = (JComponent) event.getSource();
            Color c = cb.getBackground();
            this.rField.setText(Integer.toString(c.getRed()));
            this.gField.setText(Integer.toString(c.getGreen()));
            this.bField.setText(Integer.toString(c.getBlue()));
            this.color.setColor(c.getRed(), c.getGreen(), c.getBlue());
        }
    }

    public void requestInitialFocus() {
        this.rField.requestFocus();
        this.rField.selectAll();
    }

    private void computeColor() {
        try {
            int red = Integer.parseInt(this.rField.getText());
            int green = Integer.parseInt(this.gField.getText());
            int blue = Integer.parseInt(this.bField.getText());

            if (red < 0 || red > 255) {
                this.rField.setText("0");
                throw new NumberFormatException("red value '" + red + "' is out of range");
            }
            if (green < 0 || green > 255) {
                this.gField.setText("0");
                throw new NumberFormatException("green value '" + green + "' is out of range");
            }
            if (blue < 0 || blue > 255) {
                this.bField.setText("0");
                throw new NumberFormatException("blue value '" + blue + "' is out of range");
            }

            this.color.setColor(red, green, blue);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "one of the color fields is valid, " + ex.getMessage(), "Invalid Number", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void focusGained(FocusEvent event) {
        this.computeColor();
        ((JTextField) event.getComponent()).selectAll();
    }

    public void focusLost(FocusEvent event) {
        this.computeColor();
    }

    protected JPanel createEditPanel() {
        JPanel result = new JPanel();
        result.setLayout(new GridBagLayout());
        result.setBorder(new EmptyBorder(5, 3, 3, 3));

        int col = 0;
        int row = 0;

        JLabel lbl = new JLabel("Red");
        lbl.setBorder(new EmptyBorder(1, 3, 1, 3));
        AWTUtilities.constrain(result, lbl, GridBagConstraints.NONE, GridBagConstraints.WEST, col++, row, 1, 1, 0.0, 0.0);

        this.rField = new JTextField("0");
        this.rField.addFocusListener(this);
        AWTUtilities.constrain(result, this.rField, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, col++, row++, 1, 1, 1.0, 0.0);

        col = 0;
        lbl = new JLabel("Green");
        lbl.setBorder(new EmptyBorder(1, 3, 1, 3));
        AWTUtilities.constrain(result, lbl, GridBagConstraints.NONE, GridBagConstraints.WEST, col++, row, 1, 1, 0.0, 0.0);

        this.gField = new JTextField("0");
        this.gField.addFocusListener(this);
        AWTUtilities.constrain(result, this.gField, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, col++, row++, 1, 1, 1.0, 0.0);

        col = 0;
        lbl = new JLabel("Blue");
        lbl.setBorder(new EmptyBorder(1, 3, 1, 3));
        AWTUtilities.constrain(result, lbl, GridBagConstraints.NONE, GridBagConstraints.WEST, col++, row, 1, 1, 0.0, 0.0);

        this.bField = new JTextField("0") {
            public Component getNextFocusableComponent() {
                return rField;
            }
        };
        this.bField.addFocusListener(this);
        AWTUtilities.constrain(result, this.bField, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST, col++, row++, 1, 1, 1.0, 0.0);

        this.color = this.new JColorButton(Color.red);
        AWTUtilities.constrain(result, this.color, GridBagConstraints.BOTH, GridBagConstraints.CENTER, 2, 0, 1, 3, 1.0, 1.0);

        JPanel btnPan = new JPanel();
        btnPan.setLayout(new GridBagLayout());
        btnPan.setBorder(new CompoundBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED), "Color Table"), new EmptyBorder(3, 3, 3, 3)));

        AWTUtilities.constrain(result, btnPan, GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER, 0, row, 3, 1, 1.0, 0.0, new Insets(5, 5, 5, 5));

        row = col = 0;

        JColorButton cb;

        cb = this.new JColorButton(Color.black);
        cb.addActionListener(this);
        AWTUtilities.constrain(btnPan, cb, GridBagConstraints.NONE, GridBagConstraints.CENTER, col++, row, 1, 1, 0.0, 0.0);
        cb = this.new JColorButton(Color.darkGray);
        cb.addActionListener(this);
        AWTUtilities.constrain(btnPan, cb, GridBagConstraints.NONE, GridBagConstraints.CENTER, col++, row, 1, 1, 0.0, 0.0);
        cb = this.new JColorButton(new Color((Color.darkGray.getRed() + Color.gray.getRed()) / 2, (Color.darkGray.getGreen() + Color.gray.getGreen()) / 2, (Color.darkGray.getBlue() + Color.gray.getBlue()) / 2));
        cb.addActionListener(this);
        AWTUtilities.constrain(btnPan, cb, GridBagConstraints.NONE, GridBagConstraints.CENTER, col++, row, 1, 1, 0.0, 0.0);
        cb = this.new JColorButton(Color.gray);
        cb.addActionListener(this);
        AWTUtilities.constrain(btnPan, cb, GridBagConstraints.NONE, GridBagConstraints.CENTER, col++, row, 1, 1, 0.0, 0.0);
        cb = this.new JColorButton(new Color((Color.lightGray.getRed() + Color.gray.getRed()) / 2, (Color.lightGray.getGreen() + Color.gray.getGreen()) / 2, (Color.lightGray.getBlue() + Color.gray.getBlue()) / 2));
        cb.addActionListener(this);
        AWTUtilities.constrain(btnPan, cb, GridBagConstraints.NONE, GridBagConstraints.CENTER, col++, row, 1, 1, 0.0, 0.0);
        cb = this.new JColorButton(Color.lightGray);
        cb.addActionListener(this);
        AWTUtilities.constrain(btnPan, cb, GridBagConstraints.NONE, GridBagConstraints.CENTER, col++, row, 1, 1, 0.0, 0.0);
        cb = this.new JColorButton(new Color((Color.lightGray.getRed() + Color.white.getRed()) / 2, (Color.lightGray.getGreen() + Color.white.getGreen()) / 2, (Color.lightGray.getBlue() + Color.white.getBlue()) / 2));
        cb.addActionListener(this);
        AWTUtilities.constrain(btnPan, cb, GridBagConstraints.NONE, GridBagConstraints.CENTER, col++, row, 1, 1, 0.0, 0.0);
        cb = this.new JColorButton(Color.white);
        cb.addActionListener(this);
        AWTUtilities.constrain(btnPan, cb, GridBagConstraints.NONE, GridBagConstraints.CENTER, col++, row, 1, 1, 0.0, 0.0);

        ++row;
        col = 0;

        cb = this.new JColorButton(Color.red);
        cb.addActionListener(this);
        AWTUtilities.constrain(btnPan, cb, GridBagConstraints.NONE, GridBagConstraints.CENTER, col++, row, 1, 1, 0.0, 0.0);
        cb = this.new JColorButton(Color.blue);
        cb.addActionListener(this);
        AWTUtilities.constrain(btnPan, cb, GridBagConstraints.NONE, GridBagConstraints.CENTER, col++, row, 1, 1, 0.0, 0.0);
        cb = this.new JColorButton(Color.green);
        cb.addActionListener(this);
        AWTUtilities.constrain(btnPan, cb, GridBagConstraints.NONE, GridBagConstraints.CENTER, col++, row, 1, 1, 0.0, 0.0);
        cb = this.new JColorButton(Color.cyan);
        cb.addActionListener(this);
        AWTUtilities.constrain(btnPan, cb, GridBagConstraints.NONE, GridBagConstraints.CENTER, col++, row, 1, 1, 0.0, 0.0);
        cb = this.new JColorButton(Color.magenta);
        cb.addActionListener(this);
        AWTUtilities.constrain(btnPan, cb, GridBagConstraints.NONE, GridBagConstraints.CENTER, col++, row, 1, 1, 0.0, 0.0);
        cb = this.new JColorButton(Color.pink);
        cb.addActionListener(this);
        AWTUtilities.constrain(btnPan, cb, GridBagConstraints.NONE, GridBagConstraints.CENTER, col++, row, 1, 1, 0.0, 0.0);
        cb = this.new JColorButton(Color.orange);
        cb.addActionListener(this);
        AWTUtilities.constrain(btnPan, cb, GridBagConstraints.NONE, GridBagConstraints.CENTER, col++, row, 1, 1, 0.0, 0.0);
        cb = this.new JColorButton(Color.yellow);
        cb.addActionListener(this);
        AWTUtilities.constrain(btnPan, cb, GridBagConstraints.NONE, GridBagConstraints.CENTER, col++, row, 1, 1, 0.0, 0.0);

        return result;
    }

    private class JColorButton extends JPanel {
        private int red;
        private int green;
        private int blue;
        private JButton color;
        private List listeners;

        public JColorButton(Color c) {
            this(c.getRed(), c.getGreen(), c.getBlue());
        }

        public JColorButton(int r, int g, int b) {
            this.red = r;
            this.green = g;
            this.blue = b;

            this.listeners = new ArrayList<>();

            this.setLayout(new BorderLayout());
            this.setBorder(new CompoundBorder(new EmptyBorder(3, 3, 3, 3), new CompoundBorder(new BevelBorder(EtchedBorder.LOWERED), new EmptyBorder(1, 1, 1, 1))));

            this.color = new JButton("");
            this.color.setActionCommand("COLORBUTTON");
            this.color.setBackground(new Color(r, g, b));
            this.add("Center", this.color);

            this.setMinimumSize(new Dimension(36, 36));
            this.setMaximumSize(new Dimension(36, 36));
            this.setPreferredSize(new Dimension(36, 36));
        }

        public void setColor(Color c) {
            this.setColor(c.getRed(), c.getGreen(), c.getBlue());
        }

        public void setColor(int r, int g, int b) {
            this.red = r;
            this.green = g;
            this.blue = b;

            this.color.setBackground(new Color(r, g, b));
        }

        public int getRed() {
            Color c = this.color.getBackground();
            return c.getRed();
        }

        public int getGreen() {
            Color c = this.color.getBackground();
            return c.getGreen();
        }

        public int getBlue() {
            Color c = this.color.getBackground();
            return c.getBlue();
        }

        public synchronized void setActionCommand(String cmd) {
            this.color.setActionCommand(cmd);
        }

        public synchronized void addActionListener(ActionListener listener) {
            this.color.addActionListener(listener);
        }

        public synchronized void removeActionListener(ActionListener listener) {
            this.color.removeActionListener(listener);
        }
    }

}

