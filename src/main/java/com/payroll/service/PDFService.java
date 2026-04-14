package com.payroll.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.util.Map;

@Service
public class PDFService {

    public byte[] generateSalarySlip(Map<String, Object> data) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Fonts
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, Color.DARK_GRAY);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.WHITE);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 11, Color.BLACK);

            // Title
            Paragraph title = new Paragraph("PAYROLLPRO SALARY SLIP", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(40);
            document.add(title);

            // Info Table
            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.setSpacingAfter(20);

            addCell(infoTable, "Employee Name:", data.getOrDefault("name", "N/A").toString(), normalFont);
            addCell(infoTable, "Employee ID:", data.getOrDefault("id", "N/A").toString(), normalFont);
            addCell(infoTable, "Pay Month:", data.getOrDefault("month", "N/A").toString(), normalFont);
            addCell(infoTable, "Generated Date:", data.getOrDefault("date", "N/A").toString(), normalFont);

            document.add(infoTable);

            // Salary Details Table
            PdfPTable salaryTable = new PdfPTable(2);
            salaryTable.setWidthPercentage(100);
            salaryTable.setSpacingBefore(10);

            // Header Row
            PdfPCell h1 = new PdfPCell(new Phrase("Description", headerFont));
            h1.setBackgroundColor(Color.DARK_GRAY);
            h1.setPadding(8);
            salaryTable.addCell(h1);

            PdfPCell h2 = new PdfPCell(new Phrase("Amount (INR)", headerFont));
            h2.setBackgroundColor(Color.DARK_GRAY);
            h2.setPadding(8);
            salaryTable.addCell(h2);

            // Data Rows
            addSalaryRow(salaryTable, "Basic Salary", (Double) data.get("baseSalary"), normalFont);
            addSalaryRow(salaryTable, "Bonus / Incentives", (Double) data.get("bonus"), normalFont);
            addSalaryRow(salaryTable, "Income Tax Deductions", - (Double) data.get("taxAmount"), normalFont);
            
            // Total Row
            Font totalFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.BLACK);
            PdfPCell t1 = new PdfPCell(new Phrase("NET SALARY", totalFont));
            t1.setPadding(10);
            t1.setBackgroundColor(new Color(240, 240, 240));
            salaryTable.addCell(t1);

            PdfPCell t2 = new PdfPCell(new Phrase("₹ " + String.format("%.2f", (Double) data.get("finalSalary")), totalFont));
            t2.setPadding(10);
            t2.setBackgroundColor(new Color(240, 240, 240));
            salaryTable.addCell(t2);

            document.add(salaryTable);

            // Footer
            Paragraph footer = new Paragraph("\n\nThis is a computer generated salary slip and does not require a signature.", 
                    FontFactory.getFont(FontFactory.HELVETICA, 9, Color.GRAY));
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return out.toByteArray();
    }

    private void addCell(PdfPTable table, String label, String value, Font font) {
        PdfPCell c1 = new PdfPCell(new Phrase(label, font));
        c1.setBorder(Rectangle.NO_BORDER);
        c1.setPadding(5);
        table.addCell(c1);

        PdfPCell c2 = new PdfPCell(new Phrase(value, font));
        c2.setBorder(Rectangle.NO_BORDER);
        c2.setPadding(5);
        table.addCell(c2);
    }

    private void addSalaryRow(PdfPTable table, String desc, Double amount, Font font) {
        PdfPCell c1 = new PdfPCell(new Phrase(desc, font));
        c1.setPadding(8);
        table.addCell(c1);

        PdfPCell c2 = new PdfPCell(new Phrase("₹ " + String.format("%.2f", amount), font));
        c2.setPadding(8);
        table.addCell(c2);
    }
}
