package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class MedicineCategoryPanel extends JPanel {

    private JTable categoryTable;
    private DefaultTableModel categoryModel;
    private JTextField txtCategoryName;
    private JTextArea txtDescription;

    public MedicineCategoryPanel() {
        setLayout(new BorderLayout(0, 15));
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("Medicine Category Management");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(new Color(50, 50, 50));
        add(lblTitle, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        contentPanel.setOpaque(false);

        JPanel leftFormPanel = new JPanel();
        leftFormPanel.setLayout(null);
        leftFormPanel.setBackground(Color.WHITE);
        leftFormPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 224, 230)),
            new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel lblCatName = new JLabel("Category Name:");
        lblCatName.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblCatName.setBounds(20, 30, 120, 25);
        leftFormPanel.add(lblCatName);

        txtCategoryName = new JTextField();
        txtCategoryName.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtCategoryName.setBounds(20, 60, 300, 30);
        leftFormPanel.add(txtCategoryName);

        JLabel lblDesc = new JLabel("Description:");
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblDesc.setBounds(20, 110, 120, 25);
        leftFormPanel.add(lblDesc);

        txtDescription = new JTextArea();
        txtDescription.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtDescription.setLineWrap(true);
        JScrollPane descScroll = new JScrollPane(txtDescription);
        descScroll.setBounds(20, 140, 300, 90);
        leftFormPanel.add(descScroll);

        JButton btnAddCategory = new JButton("Add Category");
        btnAddCategory.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAddCategory.setBackground(new Color(40, 167, 69));
        btnAddCategory.setForeground(Color.WHITE);
        btnAddCategory.setFocusPainted(false);
        btnAddCategory.setBounds(20, 250, 300, 45);
        leftFormPanel.add(btnAddCategory);

        JPanel rightTablePanel = new JPanel(new BorderLayout());
        rightTablePanel.setBackground(Color.WHITE);
        rightTablePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 224, 230)),
            new EmptyBorder(15, 15, 15, 15)
        ));

        String[] columns = {"Category ID", "Category Name", "Description", "Action"};
        categoryModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3;
            }
        };

        categoryModel.addRow(new Object[]{"C01", "Tablet", "Oral solid dosage form", ""});
        categoryModel.addRow(new Object[]{"C02", "Syrup", "Liquid oral preparation", ""});
        categoryModel.addRow(new Object[]{"C03", "Capsule", "Enclosed in a gelatin shell", ""});
        categoryModel.addRow(new Object[]{"C04", "Drop", "Liquid medication drops", ""});

        categoryTable = new JTable(categoryModel);
        categoryTable.setRowHeight(35);
        categoryTable.setShowVerticalLines(false);
        categoryTable.setShowHorizontalLines(true);
        categoryTable.setGridColor(new Color(235, 238, 242));
        categoryTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        categoryTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        categoryTable.getTableHeader().setBackground(new Color(248, 249, 250));
        categoryTable.getTableHeader().setForeground(new Color(50, 50, 50));
        categoryTable.getColumnModel().getColumn(3).setCellRenderer(new ActionButtonRenderer());

        JScrollPane tableScroll = new JScrollPane(categoryTable);
        tableScroll.getViewport().setBackground(Color.WHITE);
        rightTablePanel.add(tableScroll, BorderLayout.CENTER);

        contentPanel.add(leftFormPanel);
        contentPanel.add(rightTablePanel);
        add(contentPanel, BorderLayout.CENTER);
    }

    class ActionButtonRenderer extends JPanel implements TableCellRenderer {
        private JButton btnEdit, btnDelete;

        public ActionButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 4, 4));
            setOpaque(true);
            
            btnEdit = new JButton("✏");
            btnEdit.setBackground(new Color(51, 122, 183));
            btnEdit.setForeground(Color.WHITE);
            btnEdit.setFocusPainted(false);
            btnEdit.setBorderPainted(false);
            btnEdit.setFont(new Font("Segoe UI", Font.BOLD, 11));
            btnEdit.setPreferredSize(new Dimension(30, 24));

            btnDelete = new JButton("🗑");
            btnDelete.setBackground(new Color(217, 83, 79));
            btnDelete.setForeground(Color.WHITE);
            btnDelete.setFocusPainted(false);
            btnDelete.setBorderPainted(false);
            btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 11));
            btnDelete.setPreferredSize(new Dimension(30, 24));

            add(btnEdit);
            add(btnDelete);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            return this;
        }
    }
}