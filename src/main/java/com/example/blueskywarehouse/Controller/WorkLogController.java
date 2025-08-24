package com.example.blueskywarehouse.Controller;

import com.example.blueskywarehouse.Entity.WorkLog;
import com.example.blueskywarehouse.Response.ApiResponse;
import com.example.blueskywarehouse.Service.WorkLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/worklogs")
public class WorkLogController {

    @Autowired
    private WorkLogService workLogService;

    // Alle Arbeitslogs abrufen (optional nach Inhalt filtern)
    @PreAuthorize("hasRole('1') or hasRole('2') or hasRole('3')")
    @GetMapping
    public List<WorkLog> getAll(@RequestParam(required = false) String s) {
        // Wenn ein 'content'-Parameter übergeben wird, eine unscharfe Suche durchführen, andernfalls alle Protokolle abrufen.
        return workLogService.searchWorkLogsByContent(s);
    }

    // Neuen Arbeitslog einfügen
    @PreAuthorize("hasRole('1') or hasRole('2') or hasRole('3')")
    @PostMapping("/insertWorklog")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    public ApiResponse<?> insertWorkLog(@RequestParam String customerName,
                                        @RequestParam LocalDateTime operationDate,
                                        @RequestParam int itemId,
                                        @RequestParam int itemsCount,
                                        @RequestParam int status,
                                        @RequestParam String binCode) {
        return workLogService.insertNewWorklog(customerName, operationDate, itemId, itemsCount, status, binCode);
    }

    // Arbeitslog ungültig machen (Rollback durchführen)
    @PreAuthorize("hasRole('1') or hasRole('2') or hasRole('3')")
    @PutMapping("/invalidLogId")
    public ApiResponse<?> invalidWorklog(int worklogId) {
        return workLogService.invalidWorklog(worklogId);
    }

    // Arbeitslogs nach Datum abrufen
    @PreAuthorize("hasRole('1') or hasRole('2') or hasRole('3')")
    @GetMapping("/getWorklogByDate")
    public ApiResponse<?> getWorklogByDate(String date) {
        return workLogService.getWorklogByDate(date);
    }

    // Arbeitslogs nach Zeitraum / Kunde / Artikel-ID abrufen (mit Paginierung)
    @PreAuthorize("hasRole('1') or hasRole('2') or hasRole('3')")
    @GetMapping("/getWorklogByPeriode")
    public ApiResponse<?> getWorklogByPeriode(
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) Integer itemId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        boolean hasCustomer = customerName != null && !customerName.trim().isEmpty();
        boolean hasItem = itemId != null;

        if (!hasCustomer && !hasItem) {
            return workLogService.getWorklogByPeriode(startDate, endDate, page, size);
        } else if (!hasCustomer) {
            return workLogService.getWorklogByPeriode(startDate, endDate, itemId);
        } else if (!hasItem) {
            return workLogService.getWorklogByPeriode(startDate, endDate, customerName);
        } else {
            return workLogService.getWorklogByPeriode(startDate, endDate, customerName, itemId);
        }
    }

    // Kundendaten als Excel exportieren
    @PreAuthorize("hasRole('1') or hasRole('2') or hasRole('3')")
    @GetMapping("/getCustomerRecord")
    public ResponseEntity<InputStreamResource> exportExcel(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate
    ) throws IOException {
        ByteArrayInputStream in = workLogService.exportToExcel(startDate, endDate);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=customer_records.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in));
    }
}
