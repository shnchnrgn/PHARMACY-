package ui;

import db.MedicineDAO;
import models.Medicine;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
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
            CustomDialog.showMessage(this, "Table data copied to clipboard successfully!", "Copy Success", false);
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
                    CustomDialog.showMessage(this, "CSV file exported successfully!", "CSV Export", false);
                }
            } catch (Exception ex) {
                CustomDialog.showMessage(this, "Error exporting to CSV: " + ex.getMessage(), "Error", true);
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
                    CustomDialog.showMessage(this, "Excel file exported successfully!", "Excel Export", false);
                }
            } catch (Exception ex) {
                CustomDialog.showMessage(this, "Error exporting to Excel: " + ex.getMessage(), "Error", true);
            }
        });

        btnPdf.addActionListener(e -> {
            try {
                FileDialog fileDialog = new FileDialog((Frame) SwingUtilities.getWindowAncestor(this), "Save PDF", FileDialog.SAVE);
                fileDialog.setFile("medicine_list.pdf");
                fileDialog.setVisible(true);
                
                String fileName = fileDialog.getFile();
                String directory = fileDialog.getDirectory();
                
                if (fileName != null) {
                    boolean complete = table.print(JTable.PrintMode.FIT_WIDTH, null, null);
                    if (complete) {
                        CustomDialog.showMessage(this, "Document saved to PDF successfully!", "PDF Export", false);
                    }
                }
            } catch (Exception ex) {
                CustomDialog.showMessage(this, "Error generating PDF: " + ex.getMessage(), "PDF Error", true);
            }
        });

        btnPrint.addActionListener(e -> {
            try {
                FileDialog fileDialog = new FileDialog((Frame) SwingUtilities.getWindowAncestor(this), "Print Document", FileDialog.SAVE);
                fileDialog.setFile("medicine_print.pdf");
                fileDialog.setVisible(true);
                
                String fileName = fileDialog.getFile();
                String directory = fileDialog.getDirectory();
                
                if (fileName != null) {
                    boolean complete = table.print();
                    if (complete) {
                        CustomDialog.showMessage(this, "Printing completed successfully.", "Print Success", false);
                    }
                }
            } catch (Exception ex) {
                CustomDialog.showMessage(this, "Printing failed: " + ex.getMessage(), "Print Error", true);
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

        table.setLayout(new FlowLayout(FlowLayout.CENTER,0,1));
        table.setRowHeight(32);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(235, 238, 242));
        table.getColumnModel().getColumn(8).setCellRenderer(new ActionButtonRenderer());
        table.getColumnModel().getColumn(8).setCellEditor(new ActionButtonEditor(new JCheckBox()));

        table.getColumnModel().getColumn(0).setPreferredWidth(50);   
        table.getColumnModel().getColumn(1).setPreferredWidth(160);
        table.getColumnModel().getColumn(2).setPreferredWidth(120);
        table.getColumnModel().getColumn(3).setPreferredWidth(90); 
        table.getColumnModel().getColumn(4).setPreferredWidth(90);  
        table.getColumnModel().getColumn(5).setPreferredWidth(70); 
        table.getColumnModel().getColumn(6).setPreferredWidth(150);
        table.getColumnModel().getColumn(7).setPreferredWidth(100); 
        table.getColumnModel().getColumn(8).setPreferredWidth(180);

        CenterTableCellRenderer centerRenderer = new CenterTableCellRenderer();
        for (int i = 0; i < 8; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
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

        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                loadTableData((DefaultTableModel) table.getModel());
            }
        });
    }

    public void loadTableData(DefaultTableModel model) {
        model.setRowCount(0);
        List<Medicine> list = MedicineDAO.getAllMedicines();
        for (Medicine m : list) {
            model.addRow(new Object[]{
                m.getId(),
                m.getName(),
                m.getMedicineCategory(), 
                "₱" + m.getBuyPrice(),
                "₱" + m.getSellPrice(),
                m.getStock(),
                m.getCompanyName(),
                m.getExpiryDate(),
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
            setLayout(new FlowLayout(FlowLayout.CENTER, 1, 2));
            setOpaque(true);
            
            btnEdit = new JButton("Edit");
            btnEdit.setBackground(new Color(51, 122, 183));
            btnEdit.setForeground(Color.WHITE); 
            btnEdit.setOpaque(true);
            btnEdit.setFocusPainted(false);
            btnEdit.setFont(new Font("Segoe UI", Font.BOLD, 11));
            btnEdit.setPreferredSize(new Dimension(70, 26)); 

            btnDelete = new JButton("Delete");
            btnDelete.setBackground(new Color(220, 53, 69));
            btnDelete.setForeground(Color.WHITE); 
            btnDelete.setOpaque(true);
            btnDelete.setFocusPainted(false);
            btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 11));
            btnDelete.setPreferredSize(new Dimension(70, 26)); 

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
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 1, 2));
            panel.setOpaque(true);

            btnEdit = new JButton("Edit");
            btnEdit.setBackground(new Color(51, 122, 183));
            btnEdit.setForeground(Color.WHITE);
            btnEdit.setOpaque(true);
            btnEdit.setFocusPainted(false);
            btnEdit.setFont(new Font("Segoe UI", Font.BOLD, 11));
            btnEdit.setPreferredSize(new Dimension(70, 26));

            btnDelete = new JButton("Delete");
            btnDelete.setBackground(new Color(220, 53, 69));
            btnDelete.setForeground(Color.WHITE);
            btnDelete.setOpaque(true);
            btnDelete.setFocusPainted(false);
            btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 11));
            btnDelete.setPreferredSize(new Dimension(70, 26));

            btnEdit.addActionListener(e -> {
                fireEditingStopped();
                int medicineId = Integer.parseInt(table.getValueAt(currentRow, 0).toString());
                CustomDialog.showMessage(table, "Edit medicine ID: " + medicineId, "Information", false);
            });

            btnDelete.addActionListener(e -> {
                fireEditingStopped();
                int medicineId = Integer.parseInt(table.getValueAt(currentRow, 0).toString());
                boolean confirmed = CustomDialog.showConfirm(table, "Are you sure you want to delete this medicine? Click \"Yes\" to delete.", "Confirm Delete");
                if (confirmed) {
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

    class CenterTableCellRenderer extends DefaultTableCellRenderer {
        public CenterTableCellRenderer() {
            setHorizontalAlignment(JLabel.CENTER);
        }
    }

    private static class CustomDialog {
        private static boolean confirmResult = false;

        public static void showMessage(Component parent, String message, String title, boolean isWarning) {
            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(parent), title, true);
            dialog.setSize(450, 180);
            dialog.setLocationRelativeTo(parent);
            dialog.setLayout(new BorderLayout());

            JPanel topHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
            topHeader.setBackground(new Color(248, 249, 250));
            topHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 225, 230)));
            JLabel lblTitle = new JLabel(title);
            lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
            lblTitle.setForeground(isWarning ? new Color(217, 119, 6) : new Color(50, 60, 70));
            topHeader.add(lblTitle);
            dialog.add(topHeader, BorderLayout.NORTH);

            JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
            centerPanel.setBackground(Color.WHITE);
            JLabel lblMsg = new JLabel(message);
            lblMsg.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            lblMsg.setForeground(new Color(70, 75, 80));
            centerPanel.add(lblMsg);
            dialog.add(centerPanel, BorderLayout.CENTER);

            JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
            bottomPanel.setBackground(new Color(248, 249, 250));
            bottomPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 225, 230)));

            JButton btnOk = new JButton("OK");
            btnOk.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btnOk.setBackground(new Color(13, 148, 136));
            btnOk.setForeground(Color.WHITE);
            btnOk.setFocusPainted(false);
            btnOk.setBorder(BorderFactory.createEmptyBorder(6, 18, 6, 18));
            btnOk.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnOk.addActionListener(e -> dialog.dispose());

            bottomPanel.add(btnOk);
            dialog.add(bottomPanel, BorderLayout.SOUTH);
            dialog.setVisible(true);
        }

        public static boolean showConfirm(Component parent, String message, String title) {
            confirmResult = false;
            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(parent), title, true);
            dialog.setSize(450, 180);
            dialog.setLocationRelativeTo(parent);
            dialog.setLayout(new BorderLayout());

            JPanel topHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
            topHeader.setBackground(new Color(248, 249, 250));
            topHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 225, 230)));
            JLabel lblTitle = new JLabel(title);
            lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
            lblTitle.setForeground(new Color(217, 119, 6));
            topHeader.add(lblTitle);
            dialog.add(topHeader, BorderLayout.NORTH);

            JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
            centerPanel.setBackground(Color.WHITE);
            JLabel lblMsg = new JLabel(message);
            lblMsg.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            lblMsg.setForeground(new Color(70, 75, 80));
            centerPanel.add(lblMsg);
            dialog.add(centerPanel, BorderLayout.CENTER);

            JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
            bottomPanel.setBackground(new Color(248, 249, 250));
            bottomPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 225, 230)));

            JButton btnYes = new JButton("✓ Yes");
            btnYes.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btnYes.setBackground(new Color(13, 148, 136));
            btnYes.setForeground(Color.WHITE);
            btnYes.setFocusPainted(false);
            btnYes.setBorder(BorderFactory.createEmptyBorder(6, 18, 6, 18));
            btnYes.setCursor(new Cursor(Cursor.HAND_CURSOR));

            JButton btnNo = new JButton("✕ No");
            btnNo.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btnNo.setBackground(new Color(220, 53, 69));
            btnNo.setForeground(Color.WHITE);
            btnNo.setFocusPainted(false);
            btnNo.setBorder(BorderFactory.createEmptyBorder(6, 18, 6, 18));
            btnNo.setCursor(new Cursor(Cursor.HAND_CURSOR));

            btnYes.addActionListener(e -> {
                confirmResult = true;
                dialog.dispose();
            });

            btnNo.addActionListener(e -> {
                confirmResult = false;
                dialog.dispose();
            });

            bottomPanel.add(btnYes);
            bottomPanel.add(btnNo);
            dialog.add(bottomPanel, BorderLayout.SOUTH);
            dialog.setVisible(true);

            return confirmResult;
        }
    }
}