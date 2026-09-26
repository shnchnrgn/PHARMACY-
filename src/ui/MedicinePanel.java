package ui;

import db.MedicineDAO;
import models.Medicine;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;

public class MedicinePanel extends JPanel {

    private JTable table;
    private JTextField txtSearch;

    public MedicinePanel() {
        setLayout(new BorderLayout(0, 15));
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("Medicine List");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(60, 65, 70));
        add(lblTitle, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new BorderLayout(0, 10));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(210, 215, 220)),
            new EmptyBorder(15, 15, 15, 15)
        ));

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);

        JPanel rightTopPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightTopPanel.setOpaque(false);
        
        JButton btnCopy = new JButton("Copy");
        JButton btnCsv = new JButton("CSV");
        JButton btnExcel = new JButton("Excel");
        JButton btnPdf = new JButton("PDF");
        JButton btnPrint = new JButton("Print");
        
        for (JButton b : new JButton[]{btnCopy, btnCsv, btnExcel, btnPdf, btnPrint}) {
            b.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            b.setFocusPainted(false);
            b.setBackground(new Color(240, 242, 245));
            b.setForeground(new Color(70, 75, 80));
            b.setBorder(BorderFactory.createLineBorder(new Color(190, 195, 200)));
            b.setPreferredSize(new Dimension(55, 26));
            b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        btnCopy.addActionListener(e -> {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < table.getRowCount(); i++) {
                for (int j = 0; j < table.getColumnCount() - 1; j++) {
                    sb.append(table.getValueAt(i, j)).append("\t");
                }
                sb.append("\n");
            }
            StringSelection selection = new StringSelection(sb.toString());
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
            JOptionPane.showMessageDialog(this, "Table data copied to clipboard successfully!", "Copy Success", JOptionPane.INFORMATION_MESSAGE);
        });

        btnCsv.addActionListener(e -> {
            try {
                FileDialog fileDialog = new FileDialog((Frame) SwingUtilities.getWindowAncestor(this), "Save CSV", FileDialog.SAVE);
                fileDialog.setFile("medicine_list.csv");
                fileDialog.setVisible(true);
                
                String fileName = fileDialog.getFile();
                String directory = fileDialog.getDirectory();
                
                if (fileName != null) {
                    File file = new File(directory, fileName);
                    PrintWriter pw = new PrintWriter(new FileWriter(file));
                    for (int i = 0; i < table.getColumnCount() - 1; i++) {
                        pw.print(table.getColumnName(i).replace(" ↕", "") + (i == table.getColumnCount() - 2 ? "" : ","));
                    }
                    pw.println();
                    for (int i = 0; i < table.getRowCount(); i++) {
                        for (int j = 0; j < table.getColumnCount() - 1; j++) {
                            pw.print(table.getValueAt(i, j) + (j == table.getColumnCount() - 2 ? "" : ","));
                        }
                        pw.println();
                    }
                    pw.close();
                    JOptionPane.showMessageDialog(this, "CSV file exported successfully!", "CSV Export", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error exporting to CSV: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnExcel.addActionListener(e -> {
            try {
                FileDialog fileDialog = new FileDialog((Frame) SwingUtilities.getWindowAncestor(this), "Save Excel", FileDialog.SAVE);
                fileDialog.setFile("medicine_list.xls");
                fileDialog.setVisible(true);
                
                String fileName = fileDialog.getFile();
                String directory = fileDialog.getDirectory();
                
                if (fileName != null) {
                    File file = new File(directory, fileName);
                    PrintWriter pw = new PrintWriter(new FileWriter(file));
                    pw.println("<html xmlns:o=\"urn:schemas-microsoft-com:office:office\" xmlns:x=\"urn:schemas-microsoft-com:office:excel\" xmlns=\"http://www.w3.org/TR/REC-html40\">");
                    pw.println("<head><meta charset='UTF-8'></head><body>");
                    pw.println("<table border='1'><tr>");
                    for (int i = 0; i < table.getColumnCount() - 1; i++) {
                        pw.println("<th>" + table.getColumnName(i).replace(" ↕", "") + "</th>");
                    }
                    pw.println("</tr>");
                    for (int i = 0; i < table.getRowCount(); i++) {
                        pw.println("<tr>");
                        for (int j = 0; j < table.getColumnCount() - 1; j++) {
                            pw.println("<td>" + table.getValueAt(i, j) + "</td>");
                        }
                        pw.println("</tr>");
                    }
                    pw.println("</table></body></html>");
                    pw.close();
                    JOptionPane.showMessageDialog(this, "Excel file exported successfully!", "Excel Export", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error exporting to Excel: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPdf.addActionListener(e -> {
            try {
                FileDialog fileDialog = new FileDialog((Frame) SwingUtilities.getWindowAncestor(this), "Save PDF / Print", FileDialog.SAVE);
                fileDialog.setFile("medicine_list.pdf");
                fileDialog.setVisible(true);
                
                String fileName = fileDialog.getFile();
                String directory = fileDialog.getDirectory();
                
                if (fileName != null) {
                    boolean complete = table.print(JTable.PrintMode.FIT_WIDTH, null, null);
                    if (complete) {
                        JOptionPane.showMessageDialog(this, "Document sent to printer/PDF successfully!", "PDF Export", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error generating PDF: " + ex.getMessage(), "PDF Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPrint.addActionListener(e -> {
            try {
                boolean complete = table.print();
                if (complete) {
                    JOptionPane.showMessageDialog(this, "Printing completed successfully.", "Print Success", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Printing failed: " + ex.getMessage(), "Print Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        txtSearch = new JTextField(15);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 205, 210)),
            BorderFactory.createEmptyBorder(4, 6, 4, 6)
        ));
        
        JLabel lblSearch = new JLabel("Search:");
        lblSearch.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSearch.setForeground(new Color(90, 95, 100));

        rightTopPanel.add(btnCopy);
        rightTopPanel.add(btnCsv);
        rightTopPanel.add(btnExcel);
        rightTopPanel.add(btnPdf);
        rightTopPanel.add(btnPrint);
        rightTopPanel.add(Box.createHorizontalStrut(10));
        rightTopPanel.add(lblSearch);
        rightTopPanel.add(txtSearch);

        topBar.add(rightTopPanel, BorderLayout.EAST);
        contentPanel.add(topBar, BorderLayout.NORTH);

        JPanel centerContainer = new JPanel(new BorderLayout(0, 10));
        centerContainer.setOpaque(false);

        String[] columns = {
            "ID",
            "Medicine Name ↕", 
            "Medicine Category ↕", 
            "Buy Price ↕", 
            "Sell Price ↕", 
            "Quantity ↕", 
            "Company Name ↕", 
            "Expire Date ↕", 
            "Action ↕"
        };
        
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 8;
            }
        };

        table = new JTable(tableModel);
        loadTableData(tableModel);

        table.setRowHeight(32);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(235, 238, 242));
        table.getColumnModel().getColumn(8).setCellRenderer(new ActionButtonRenderer());
        table.getColumnModel().getColumn(8).setCellEditor(new ActionButtonEditor(new JCheckBox()));
        
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(248, 249, 250));
        table.getTableHeader().setForeground(new Color(80, 85, 90));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 224, 230)));
        centerContainer.add(scrollPane, BorderLayout.CENTER);

        contentPanel.add(centerContainer, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.CENTER);
    }

    public void loadTableData(DefaultTableModel model) {
        model.setRowCount(0);
        List<Medicine> list = MedicineDAO.getAllMedicines();
        for (Medicine m : list) {
            model.addRow(new Object[]{
                m.getId(),
                m.getName(),
                "Tablet", 
                "₱" + m.getPrice(),
                "₱" + m.getPrice(),
                m.getStock(),
                "Unilab",
                "2027-01-01",
                ""
            });
        }
    }

    @Override
    public void addNotify() {
        super.addNotify();
        if (table != null) {
            loadTableData((DefaultTableModel) table.getModel());
        }
    }

    class ActionButtonRenderer extends JPanel implements TableCellRenderer {
        private JButton btnEdit, btnDelete;

        public ActionButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 4, 4));
            setOpaque(true);
            
            btnEdit = new JButton("...");
            btnEdit.setBackground(new Color(51, 122, 183));
            btnEdit.setForeground(Color.WHITE);
            btnEdit.setOpaque(true);
            btnEdit.setContentAreaFilled(false);
            btnEdit.setFocusPainted(false);
            btnEdit.setBorderPainted(false);
            btnEdit.setFont(new Font("Segoe UI", Font.BOLD, 11));
            btnEdit.setPreferredSize(new Dimension(30, 24));

            btnDelete = new JButton("...");
            btnDelete.setBackground(new Color(220, 53, 69));
            btnDelete.setForeground(Color.WHITE);
            btnDelete.setOpaque(true);
            btnDelete.setContentAreaFilled(false);
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

    class ActionButtonEditor extends DefaultCellEditor {
        private JPanel panel;
        private JButton btnEdit, btnDelete;
        private int currentRow;

        public ActionButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 4));
            panel.setOpaque(true);

            btnEdit = new JButton("...");
            btnEdit.setBackground(new Color(51, 122, 183));
            btnEdit.setForeground(Color.WHITE);
            btnEdit.setOpaque(true);
            btnEdit.setContentAreaFilled(false);
            btnEdit.setFocusPainted(false);
            btnEdit.setBorderPainted(false);
            btnEdit.setFont(new Font("Segoe UI", Font.BOLD, 11));
            btnEdit.setPreferredSize(new Dimension(30, 24));

            btnDelete = new JButton("...");
            btnDelete.setBackground(new Color(220, 53, 69));
            btnDelete.setForeground(Color.WHITE);
            btnDelete.setOpaque(true);
            btnDelete.setContentAreaFilled(false);
            btnDelete.setFocusPainted(false);
            btnDelete.setBorderPainted(false);
            btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 11));
            btnDelete.setPreferredSize(new Dimension(30, 24));

            btnEdit.addActionListener(e -> {
                fireEditingStopped();
                int medicineId = Integer.parseInt(table.getValueAt(currentRow, 0).toString());
                JOptionPane.showMessageDialog(table, "Edit medicine ID: " + medicineId);
            });

            btnDelete.addActionListener(e -> {
                fireEditingStopped();
                int medicineId = Integer.parseInt(table.getValueAt(currentRow, 0).toString());
                int confirm = JOptionPane.showConfirmDialog(table, "Are you sure you want to delete this medicine?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    MedicineDAO.deleteMedicine(medicineId);
                    loadTableData((DefaultTableModel) table.getModel());
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