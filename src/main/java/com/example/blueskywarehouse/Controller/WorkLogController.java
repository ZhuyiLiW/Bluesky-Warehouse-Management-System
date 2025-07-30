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

    // Alle Logs abrufen
    @PreAuthorize("hasRole('1') or hasRole('2') or hasRole('3')")
    @GetMapping
    public List<WorkLog> getAll(@RequestParam(required = false) String s) {
        //Wenn ein 'content'-Parameter übergeben wird, eine unscharfe Suche durchführen, andernfalls alle Protokolle abrufen.
        return workLogService.searchWorkLogsByContent(s);
    }

    // Einen neuen Arbeitslog einfüge
    @PreAuthorize("hasRole('1') or hasRole('2') or hasRole('3')")
    @PostMapping("/insertWorklog")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    public ApiResponse<?> insertWorkLog(@RequestParam String customerName,
                                        @RequestParam LocalDateTime operationDate,
                                        @RequestParam int itemId,
                                        @RequestParam int itemsCount,
                                        @RequestParam int status,
                                        @RequestParam String binCode) {
        // Eine Methode der Service-Schicht aufrufen, um Daten einzufügen

        return workLogService.insertNewWorklog(customerName, operationDate, itemId, itemsCount, status, binCode);
    }

    @PreAuthorize("hasRole('1') or hasRole('2') or hasRole('3')")
    @PostMapping("invalidLogId")
    public ApiResponse<?> invalidWorklog(int worklogId){

        return workLogService.invalidWorklog( worklogId);
    }
    @PreAuthorize("hasRole('1') or hasRole('2') or hasRole('3')")
    @PostMapping("getWorklogByDate")
    public ApiResponse<?> getWorklogByDate(String date){
        return workLogService.getWorklogByDate( date);
    }
    @PreAuthorize("hasRole('1') or hasRole('2') or hasRole('3')")
    @PostMapping("/getWorklogByPeriode")
    public ApiResponse<?> getWorklogByPeriode(
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) Integer itemId,
            @RequestParam(defaultValue = "0") int page,   // ✅ 仅在无过滤条件时使用
            @RequestParam(defaultValue = "10") int size   // ✅ 每页10条
    ) {

        boolean hasCustomer = customerName != null && !customerName.trim().isEmpty();
        boolean hasItem = itemId != null;

        if (!hasCustomer && !hasItem) {
            // ✅ 无过滤条件 → 走分页查询
            return workLogService.getWorklogByPeriode(startDate, endDate, page, size);
        } else if (!hasCustomer) {
            // ✅ 只有 itemId → 走原来的方法
            return workLogService.getWorklogByPeriode(startDate, endDate, itemId);
        } else if (!hasItem) {
            // ✅ 只有 customerName → 走原来的方法
            return workLogService.getWorklogByPeriode(startDate, endDate, customerName);
        } else {
            // ✅ 两个过滤条件都有 → 走原来的方法
            return workLogService.getWorklogByPeriode(startDate, endDate, customerName, itemId);
        }
    }

    @PreAuthorize("hasRole('1') or hasRole('2') or hasRole('3')")
    @PostMapping("/getCustomerRecord")
    public ResponseEntity<InputStreamResource> exportExcel(
            // Den Anfrageparameter startDate im Format yyyy-MM-dd empfangen und automatisch in den Java-Typ Date umwandeln.
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
            //Den Anfrageparameter endDate ebenfalls im Format yyyy-MM-dd empfangen und in den Java-Typ Date umwandeln.
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate
    ) throws IOException {
        // Die Exportmethode der Business-Logik aufrufen, um Excel-Daten zu erzeugen und diese als Stream zurückzugeben.
        ByteArrayInputStream in = workLogService.exportToExcel(startDate, endDate);

        // Den Response-Header setzen, um dem Client den Dateinamen customer_records.xlsx für den Download mitzuteilen.
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=customer_records.xlsx");

        // Aufbau des HTTP-Antwortobjekts mit folgenden Einstellungen:
        // - Statuscode: 200 OK
        // - Content-Type: Excel-Dateityp
        // - Antwortinhalt: InputStreamResource (eingepackter Excel-Dateistream)

        return ResponseEntity
                .ok()  // HTTP 200
                .headers(headers)  // Response-Header hinzufügen
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in));  // Dateiinhalt festlegen
    }

}

