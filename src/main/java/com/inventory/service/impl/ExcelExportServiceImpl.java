package com.inventory.service.impl;

import com.inventory.dto.InventoryReportItem;
import com.inventory.service.ExcelExportService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ExcelExportServiceImpl implements ExcelExportService {

    @Override
    public ByteArrayInputStream exportInventoryReport(List<InventoryReportItem> items) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Inventory Report");

            String[] headers = {"SKU", "Product Name", "Category", "Stock Quantity", "Cost Price", "Stock Value"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            int rowIdx = 1;
            for (InventoryReportItem item : items) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(item.getSku());
                row.createCell(1).setCellValue(item.getProductName());
                row.createCell(2).setCellValue(item.getCategory());
                row.createCell(3).setCellValue(item.getStockQuantity());
                row.createCell(4).setCellValue(item.getCostPrice().doubleValue());
                row.createCell(5).setCellValue(item.getStockValue().doubleValue());
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException("Failed to generate Excel report", e);
        }
    }
}