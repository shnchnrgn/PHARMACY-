package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

public class MedicinePanel extends JPanel {

    private JTable table;
    private JTextField txtSearch;

    public MedicinePanel() {
        setLayout(new BorderLayout(0, 15));
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("Medicine List");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(new Color(40, 40, 40));
        add(lblTitle, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new BorderLayout(0, 10));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 224, 230)),
            new EmptyBorder(15, 15, 15, 15)
        ));

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);

        JPanel showPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        showPanel.setOpaque(false);
        showPanel.add(new JLabel("Show"));
        showPanel.add(new JComboBox<>(new String[]{"10", "25", "50", "100"}));
        showPanel.add(new JLabel("entries"));
        topBar.add(showPanel, BorderLayout.WEST);

        JPanel rightTopPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        rightTopPanel.setOpaque(false);
        
        JButton btnCopy = new JButton("Copy");
        JButton btnCsv = new JButton("CSV");
        JButton btnExcel = new JButton("Excel");
        JButton btnPdf = new JButton("PDF");
        JButton btnPrint = new JButton("Print");
        
        for (JButton b : new JButton[]{btnCopy, btnCsv, btnExcel, btnPdf, btnPrint}) {
            b.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            b.setFocusPainted(false);
            b.setBackground(new Color(248, 249, 250));
            b.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
            b.setPreferredSize(new Dimension(55, 25));
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
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setSelectedFile(new File("medicine_list.csv"));
                if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
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
                    JOptionPane.showMessageDialog(this, "CSV file exported successfully to: " + file.getAbsolutePath(), "CSV Export", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error exporting to CSV: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnExcel.addActionListener(e -> {
            try {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setSelectedFile(new File("medicine_list.xls"));
                if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
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
                    JOptionPane.showMessageDialog(this, "Excel file exported successfully! You can open it in Microsoft Excel.", "Excel Export", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error exporting to Excel: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPdf.addActionListener(e -> {
            try {
                boolean complete = table.print(JTable.PrintMode.FIT_WIDTH, null, null);
                if (complete) {
                    JOptionPane.showMessageDialog(this, "PDF/Document generated and sent to printer/PDF writer successfully!", "PDF Export", JOptionPane.INFORMATION_MESSAGE);
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
        rightTopPanel.add(btnCopy);
        rightTopPanel.add(btnCsv);
        rightTopPanel.add(btnExcel);
        rightTopPanel.add(btnPdf);
        rightTopPanel.add(btnPrint);
        rightTopPanel.add(Box.createHorizontalStrut(10));
        rightTopPanel.add(new JLabel("Search:"));
        rightTopPanel.add(txtSearch);

        topBar.add(rightTopPanel, BorderLayout.EAST);
        contentPanel.add(topBar, BorderLayout.NORTH);

        JPanel centerContainer = new JPanel(new BorderLayout(0, 10));
        centerContainer.setOpaque(false);

        String[] columns = {
            "Medicine Name ↕", 
            "Medicine Category ↕", 
            "Buy Price ↕", 
            "Sell Price ↕", 
            "Quantity ↕", 
            "Rack No ↕", 
            "Company Name ↕", 
            "Expire Date ↕", 
            "Action ↕"
        };
        
        SharedData.medicineTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 8;
            }
        };
        
        String[][] defaultData = {
            {"Biogesic 500mg", "Tablet", "₱4.50", "₱6.00", "150", "1", "Unilab", "2028-10-15", ""},
            {"Neozep Forte", "Tablet", "₱6.00", "₱7.50", "80", "2", "Unilab", "2027-06-20", ""},
            {"Alaxan FR", "Capsule", "₱8.00", "₱10.00", "45", "3", "Unilab", "2027-09-10", ""},
            {"Solmux 500mg", "Capsule", "₱9.00", "₱12.00", "60", "4", "Pascual Laboratories", "2028-01-12", ""},
            {"Kremil-S", "Tablet", "₱7.00", "₱9.00", "90", "5", "Unilab", "2027-11-30", ""},
            {"Decolgen Forte", "Tablet", "₱5.50", "₱7.00", "110", "6", "Pascual Laboratories", "2027-05-18", ""}
        };
        
        if (SharedData.medicineList.isEmpty()) {
            for (String[] row : defaultData) {
                SharedData.medicineList.add(row);
                SharedData.medicineTableModel.addRow(row);
            }
        } else {
            for (String[] row : SharedData.medicineList) {
                SharedData.medicineTableModel.addRow(row);
            }
        }

        SharedData.medicineTableModel.addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int col = e.getColumn();
                if (row >= 0 && row < SharedData.medicineList.size() && col != 8) {
                    String updatedVal = (String) SharedData.medicineTableModel.getValueAt(row, col);
                    String[] rowData = SharedData.medicineList.get(row);
                    rowData[col] = updatedVal;
                }
            }
        });

        table = new JTable(SharedData.medicineTableModel);
        table.getColumnModel().getColumn(8).setCellRenderer(new ActionButtonRenderer());
        table.getColumnModel().getColumn(8).setCellEditor(new ActionButtonEditor(new JCheckBox(), table));
        table.setRowHeight(38);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        JScrollPane scrollPane = new JScrollPane(table);
        centerContainer.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomBar = new JPanel(new BorderLayout());
        bottomBar.setOpaque(false);

        JLabel lblShowing = new JLabel("Showing 1 to " + table.getRowCount() + " of " + table.getRowCount() + " entries");
        lblShowing.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        bottomBar.add(lblShowing, BorderLayout.WEST);

        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 0));
        paginationPanel.setOpaque(false);
        
        JButton btnPrev = new JButton("Previous");
        JButton btn1 = new JButton("1");
        JButton btn2 = new JButton("2");
        JButton btn3 = new JButton("3");
        JButton btnNext = new JButton("Next");
        
        for (JButton b : new JButton[]{btnPrev, btn1, btn2, btn3, btnNext}) {
            b.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            b.setFocusPainted(false);
            b.setBackground(Color.WHITE);
            b.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
            b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
        btn1.setBackground(new Color(240, 240, 240));

        paginationPanel.add(btnPrev);
        paginationPanel.add(btn1);
        paginationPanel.add(btn2);
        paginationPanel.add(btn3);
        paginationPanel.add(btnNext);

        bottomBar.add(paginationPanel, BorderLayout.EAST);
        centerContainer.add(bottomBar, BorderLayout.SOUTH);

        contentPanel.add(centerContainer, BorderLayout.CENTER);
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
            btnEdit.setPreferredSize(new Dimension(32, 26));

            btnDelete = new JButton("🗑");
            btnDelete.setBackground(new Color(217, 83, 79));
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
            setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
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

            btnEdit = new JButton("✏");
            btnEdit.setBackground(new Color(51, 122, 183));
            btnEdit.setForeground(Color.WHITE);
            btnEdit.setFocusPainted(false);
            btnEdit.setBorderPainted(false);
            btnEdit.setFont(new Font("Segoe UI", Font.BOLD, 11));
            btnEdit.setPreferredSize(new Dimension(32, 26));

            btnDelete = new JButton("🗑");
            btnDelete.setBackground(new Color(217, 83, 79));
            btnDelete.setForeground(Color.WHITE);
            btnDelete.setFocusPainted(false);
            btnDelete.setBorderPainted(false);
            btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 11));
            btnDelete.setPreferredSize(new Dimension(32, 26));

            btnEdit.addActionListener(e -> {
                fireEditingStopped();
                JOptionPane.showMessageDialog(table, "Double-click any cell in row " + (currentRow + 1) + " to edit information directly.");
            });

            btnDelete.addActionListener(e -> {
                fireEditingStopped();
                int confirm = JOptionPane.showConfirmDialog(table, "Are you sure you want to delete this medicine?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    ((DefaultTableModel) table.getModel()).removeRow(currentRow);
                    SharedData.medicineList.remove(currentRow);
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