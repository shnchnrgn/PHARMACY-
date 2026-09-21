package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;
import java.awt.*;

public class MedicineCategoryPanel extends JPanel {

    private JTable catTable;
    private DefaultTableModel tableModel;

    public MedicineCategoryPanel() {
        setLayout(new BorderLayout(0, 15));
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("Medicine Category Management");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(40, 40, 40));
        add(lblTitle, BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(15, 0));
        content.setOpaque(false);

        JPanel leftForm = new JPanel(new GridLayout(3, 2, 5, 10));
        leftForm.setBackground(Color.WHITE);
        leftForm.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 224, 230)),
            new EmptyBorder(15, 15, 15, 15)
        ));
        leftForm.setPreferredSize(new Dimension(320, 180));

        JTextField txtCategoryName = new JTextField();
        JTextField txtDescription = new JTextField();

        leftForm.add(new JLabel("Category Name:"));
        leftForm.add(txtCategoryName);
        leftForm.add(new JLabel("Description:"));
        leftForm.add(txtDescription);
        
        JButton btnAddCat = new JButton("Add Category");
        btnAddCat.setBackground(new Color(39, 174, 96));
        btnAddCat.setForeground(Color.WHITE);
        btnAddCat.setFocusPainted(false);
        leftForm.add(new JLabel(""));
        leftForm.add(btnAddCat);

        content.add(leftForm, BorderLayout.WEST);

        JPanel rightTable = new JPanel(new BorderLayout());
        rightTable.setBackground(Color.WHITE);
        rightTable.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 224, 230)),
            new EmptyBorder(15, 15, 15, 15)
        ));

        String[] cols = {"Category ID", "Category Name", "Description", "Action"};
        Object[][] data = {
            {"C01", "Tablet", "Oral solid dosage form", ""},
            {"C02", "Syrup", "Liquid oral preparation", ""},
            {"C03", "Capsule", "Enclosed in a gelatin shell", ""},
            {"C04", "Drop", "Liquid medication drops", ""}
        };

        tableModel = new DefaultTableModel(data, cols);
        catTable = new JTable(tableModel);
        
        catTable.getColumnModel().getColumn(3).setCellRenderer(new ActionButtonRenderer());
        catTable.getColumnModel().getColumn(3).setCellEditor(new ActionButtonEditor(new JCheckBox(), catTable));
        catTable.setRowHeight(35);

        rightTable.add(new JScrollPane(catTable), BorderLayout.CENTER);

        content.add(rightTable, BorderLayout.CENTER);
        add(content, BorderLayout.CENTER);

        btnAddCat.addActionListener(e -> {
            String catName = txtCategoryName.getText().trim();
            String desc = txtDescription.getText().trim();
            if (!catName.isEmpty()) {
                String id = "C0" + (catTable.getRowCount() + 1);
                tableModel.addRow(new Object[]{id, catName, desc, ""});
                txtCategoryName.setText("");
                txtDescription.setText("");
                JOptionPane.showMessageDialog(this, "Category added successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Please enter Category Name.");
            }
        });
    }

    class ActionButtonRenderer extends JPanel implements TableCellRenderer {
        private JButton btnEdit, btnDelete;

        public ActionButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 4, 4));
            setOpaque(true);
            
            btnEdit = new JButton("...");
            btnEdit.setBackground(new Color(41, 128, 185));
            btnEdit.setForeground(Color.WHITE);
            btnEdit.setFocusPainted(false);
            btnEdit.setBorderPainted(false);
            btnEdit.setFont(new Font("Segoe UI", Font.BOLD, 11));
            btnEdit.setPreferredSize(new Dimension(32, 26));

            btnDelete = new JButton("...");
            btnDelete.setBackground(new Color(203, 67, 53));
            btnDelete.setForeground(Color.WHITE);
            btnDelete.setFocusPainted(false);
            btnDelete.setBorderPainted(false);
            btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 11));
            btnDelete.setPreferredSize(new Dimension(32, 26));

            add(btnEdit);
            add(btnDelete);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setBackground(table.getSelectionBackground());
            } else {
                setBackground(Color.WHITE);
            }
            return this;
        }
    }

    class ActionButtonEditor extends AbstractCellEditor implements TableCellEditor {
        private JPanel panel;
        private JButton btnEdit, btnDelete;
        private JTable table;
        private int currentRow;

        public ActionButtonEditor(JCheckBox checkBox, JTable table) {
            this.table = table;
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 4));
            panel.setOpaque(true);

            btnEdit = new JButton("...");
            btnEdit.setBackground(new Color(41, 128, 185));
            btnEdit.setForeground(Color.WHITE);
            btnEdit.setFocusPainted(false);
            btnEdit.setBorderPainted(false);
            btnEdit.setFont(new Font("Segoe UI", Font.BOLD, 11));
            btnEdit.setPreferredSize(new Dimension(32, 26));

            btnDelete = new JButton("...");
            btnDelete.setBackground(new Color(203, 67, 53));
            btnDelete.setForeground(Color.WHITE);
            btnDelete.setFocusPainted(false);
            btnDelete.setBorderPainted(false);
            btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 11));
            btnDelete.setPreferredSize(new Dimension(32, 26));

            btnEdit.addActionListener(e -> {
                fireEditingStopped();
                JOptionPane.showMessageDialog(table, "Edit category at row: " + (currentRow + 1));
            });

            btnDelete.addActionListener(e -> {
                fireEditingStopped();
                int confirm = JOptionPane.showConfirmDialog(table, "Are you sure you want to delete this category?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    ((DefaultTableModel) table.getModel()).removeRow(currentRow);
                }
            });

            panel.add(btnEdit);
            panel.add(btnDelete);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            currentRow = row;
            panel.setBackground(table.getSelectionBackground());
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "";
        }
    }
}