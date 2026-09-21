package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(new Color(50, 50, 50));
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
        JLabel lblShow = new JLabel("Show");
        lblShow.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        JComboBox<String> cmbEntries = new JComboBox<>(new String[]{"10", "25", "50", "100"});
        cmbEntries.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        JLabel lblEntries = new JLabel("entries");
        lblEntries.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        showPanel.add(lblShow);
        showPanel.add(cmbEntries);
        showPanel.add(lblEntries);
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
            b.setPreferredSize(new Dimension(50, 25));
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
                    JOptionPane.showMessageDialog(this, "CSV file exported successfully!", "CSV Export", JOptionPane.INFORMATION_MESSAGE);
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
                    JOptionPane.showMessageDialog(this, "Excel file exported successfully!", "Excel Export", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error exporting to Excel: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPdf.addActionListener(e -> {
            try {
                boolean complete = table.print(JTable.PrintMode.FIT_WIDTH, null, null);
                if (complete) {
                    JOptionPane.showMessageDialog(this, "Document sent to printer/PDF successfully!", "PDF Export", JOptionPane.INFORMATION_MESSAGE);
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
        JLabel lblSearch = new JLabel("Search:");
        lblSearch.setFont(new Font("Segoe UI", Font.PLAIN, 12));

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
        
        // Updated default data na may mga pamilyar na gamot sa Pilipinas
        String[][] defaultData = {
            {"Biogesic 500mg", "Tablet", "₱4.00", "₱6.00", "150", "1", "Unilab", "15-Aug-2028", ""},
            {"Neozep Forte", "Tablet", "₱5.50", "₱7.75", "120", "2", "Unilab", "20-Nov-2027", ""},
            {"Alaxan FR", "Capsule", "₱8.00", "₱11.00", "90", "3", "Unilab", "10-Jan-2027", ""},
            {"Decolgen Fort", "Tablet", "₱5.00", "₱7.00", "100", "4", "Pascual Lab", "05-Dec-2027", ""},
            {"Lagundi 600mg", "Tablet", "₱6.00", "₱8.50", "80", "5", "Pascual Lab", "12-Mar-2028", ""},
            {"Ascorbic Acid (Ceelin) 100ml", "Syrup", "₱85.00", "₱110.00", "45", "6", "Unilab", "30-Jun-2027", ""},
            {"Mefenamic Acid 500mg", "Capsule", "₱4.50", "₱6.50", "200", "7", "Ritemed", "18-Sep-2028", ""}
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
        
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                
                if (col == 8 && row >= 0) {
                    Rectangle cellRect = table.getCellRect(row, col, false);
                    int clickX = e.getX() - cellRect.x;
                    
                    if (clickX < cellRect.width / 2) {
                        JOptionPane.showMessageDialog(table, "Edit medicine at row: " + (row + 1));
                    } else {
                        int confirm = JOptionPane.showConfirmDialog(table, "Are you sure you want to delete this medicine?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                        if (confirm == JOptionPane.YES_OPTION) {
                            ((DefaultTableModel) table.getModel()).removeRow(row);
                            if (row < SharedData.medicineList.size()) {
                                SharedData.medicineList.remove(row);
                            }
                        }
                    }
                }
            }
        });

        table.setRowHeight(36);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(248, 249, 250));
        table.getTableHeader().setForeground(new Color(50, 50, 50));

        JScrollPane scrollPane = new JScrollPane(table);
        centerContainer.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomBar = new JPanel(new BorderLayout());
        bottomBar.setOpaque(false);

        JLabel lblShowing = new JLabel("Showing 1 to " + table.getRowCount() + " of entries");
        lblShowing.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblShowing.setForeground(new Color(100, 100, 100));
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