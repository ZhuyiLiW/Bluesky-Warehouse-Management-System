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

    // 获取所有日志
    @PreAuthorize("hasRole('1') or hasRole('2') or hasRole('3')")
    @GetMapping
    public List<WorkLog> getAll(@RequestParam(required = false) String s) {
        // 如果有传递 content 参数，进行模糊查询，否则获取所有日志
        return workLogService.searchWorkLogsByContent(s);
    }

    // 插入新的工作日志
    @PreAuthorize("hasRole('1') or hasRole('2') or hasRole('3')")
    @PostMapping("/insertWorklog")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    public ApiResponse<?> insertWorkLog(@RequestParam String customerName,
                             @RequestParam LocalDateTime operationDate,
                             @RequestParam int itemId,
                             @RequestParam int itemsCount,
                             @RequestParam int status,
                             @RequestParam String binCode) {
        // 调用 Service 层方法插入数据

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
            @RequestParam(required = false) Integer itemId) {

        boolean hasCustomer = customerName != null && !customerName.trim().isEmpty();
        boolean hasItem = itemId != null;

        if (!hasCustomer && !hasItem) {
            return workLogService.getWorklogByPeriode(startDate, endDate);
        } else if (!hasCustomer) {
            return workLogService.getWorklogByPeriode(startDate, endDate, itemId);
        } else if (!hasItem) {
            return workLogService.getWorklogByPeriode(startDate, endDate, customerName);
        } else {
            return workLogService.getWorklogByPeriode(startDate, endDate, customerName, itemId);
        }
    }
    @PreAuthorize("hasRole('1') or hasRole('2') or hasRole('3')")
    @PostMapping("/getCustomerRecord")
    public ResponseEntity<InputStreamResource> exportExcel(
            // 接收请求参数 startDate，格式为 yyyy-MM-dd，并将其自动转换为 Java 的 Date 类型
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
            // 接收请求参数 endDate，同样格式化并转换
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate
    ) throws IOException {
        // 调用业务层的导出方法，生成 Excel 数据并以流的形式返回
        ByteArrayInputStream in = workLogService.exportToExcel(startDate, endDate);

        // 设置响应头，告知客户端下载的文件名为 customer_records.xlsx
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=customer_records.xlsx");

        // 构建 HTTP 响应对象，设置：
        // - 状态码为 200 OK
        // - 内容类型为 Excel 文件类型
        // - 响应体为 InputStreamResource（包装了 Excel 文件流）
        return ResponseEntity
                .ok()  // HTTP 200
                .headers(headers)  // 添加响应头
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in));  // 设置文件内容
    }

}
