package com.inventory.service;

import java.io.ByteArrayInputStream;
import java.util.List;
import com.inventory.dto.InventoryReportItem;

public interface ExcelExportService {
    ByteArrayInputStream exportInventoryReport(List<InventoryReportItem> items);
}