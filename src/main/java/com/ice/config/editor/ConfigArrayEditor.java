package com.ice.config.editor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;

import com.ice.config.ConfigureEditor;
import com.ice.config.ConfigureSpec;

public abstract class ConfigArrayEditor extends ConfigureEditor implements FocusListener {

    protected JButton insertButton;
    protected JButton appendButton;
    protected JButton deleteButton;
    protected JTable table;
    protected SAETableModel model;

    public ConfigArrayEditor(String typeName) {
        super(typeName);
    }

    @Override
    public void requestInitialFocus() {
    }

    @Override
    public void focusGained(FocusEvent event) {
    }

    @Override
    public void focusLost(FocusEvent event) {
    }

    @Override
    public boolean isTupleTable(ConfigureSpec spec) {
        return false;
    }

    // REVIEW Is this a reasonable assumption?
    @Override
    public boolean isStringArray(ConfigureSpec spec) {
        return true;
    }

    @Override
    protected JPanel createEditPanel() {
        JPanel result = new JPanel();
        result.setLayout(new BorderLayout());
        result.setBorder(new CompoundBorder(new EmptyBorder(7, 7, 7, 7), new CompoundBorder(new EtchedBorder(EtchedBorder.RAISED), new EmptyBorder(2, 2, 2, 2))));

        result.setPreferredSize(new Dimension(150, 225));

        this.insertButton = new JButton("Insert");

        this.model = new SAETableModel();
        this.table = new JTable(this.model) {
            @Override
            public Component getNextFocusableComponent() {
                return insertButton;
            }
        };

        this.table.setIntercellSpacing(new Dimension(3, 3));

        JScrollPane scroller = new JScrollPane(this.table);

        result.add("Center", scroller);

        JPanel ctlPan = new JPanel();
        ctlPan.setLayout(new GridLayout(1, 3, 5, 5));

        result.add("South", ctlPan);

        this.insertButton.addActionListener(new ActionAdapter() {
            @Override
            public void actionPerformed(ActionEvent e) {
                insertElement();
            }
        });
        ctlPan.add(this.insertButton);

        this.appendButton = new JButton("Append");
        this.appendButton.addActionListener(new ActionAdapter() {
            @Override
            public void actionPerformed(ActionEvent e) {
                appendElement();
            }
        });
        ctlPan.add(this.appendButton);

        this.deleteButton = new JButton("Delete");
        this.deleteButton.addActionListener(new ActionAdapter() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteElement();
            }
        });
        ctlPan.add(this.deleteButton);

        this.descOffset = 5;

        return result;
    }

    public void insertElement() {
        int row = this.table.getSelectedRow();
        this.model.insertElement("New String", row);
        this.table.setRowSelectionInterval(row, row);
        this.table.repaint(250);
    }

    public void appendElement() {
        this.model.appendElement("New String");
        int row = this.model.getRowCount() - 1;
        this.table.setRowSelectionInterval(row, row);
        this.table.repaint(250);
    }

    public void deleteElement() {
        this.table.removeEditor();
        int row = this.table.getSelectedRow();
        if (row >= 0 && row < this.model.getRowCount()) {
            this.model.deleteElement(row);
            if (row >= this.model.getRowCount()) row = this.model.getRowCount() - 1;
            this.table.setRowSelectionInterval(row, row);
            this.table.repaint(250);
        }
    }

    public static class SAETableModel extends AbstractTableModel {

        private String[] columnNames = {"Value"};
        private Class<?>[] columnTypes = {String.class};

        private List<Object> data;

        public SAETableModel() {
            this.data = null;
        }

        public List<Object> getData() {
            return this.data;
        }

        public void setData(List<Object> data) {
            this.data = data;

            this.fireTableChanged(new TableModelEvent(this));
        }

        public void insertElement(Object val, int row) {
            this.data.add(row, val);
            this.fireTableRowsInserted(row, row);
        }

        public void appendElement(Object val) {
            this.data.add(val);
            this.fireTableRowsInserted(this.data.size(), this.data.size());
        }

        public void deleteElement(int row) {
            this.data.remove(row);
            this.fireTableRowsDeleted(row, row);
        }

        //
        // I N T E R F A C E    TableModel
        //

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

        @Override
        public Class<?> getColumnClass(int column) {
            return columnTypes[column];
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public int getRowCount() {
            if (this.data == null) return 0;

            return data.size();
        }

        @Override
        public Object getValueAt(int aRow, int aColumn) {
            return this.data.get(aRow);
        }

        @Override
        public void setValueAt(Object value, int row, int column) {
            this.data.set(row, value);
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return true;
        }
    }

    private static class ActionAdapter implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent event) {
        }
    }
}
