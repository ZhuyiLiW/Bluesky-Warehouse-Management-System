package com.example.blueskywarehouse.Entity;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Timestamp;  // 导入 Timestamp 类型

@Entity
public class WorkLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "operation_date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Timestamp operationDate;  // 修改为 Timestamp

    @Column(name = "item_id")
    private int itemId;

    @Column(name = "items_count")
    private int itemsCount;

    @Column(name = "status")
    private int status;

    @Column(name = "bin_code")
    private String bin_code;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "item_name")
    private String itemName;
    @Version
    private Integer version;

    // -------------------- 构造方法 --------------------

    public WorkLog() {
    }

    public WorkLog(Long id, String customerName, Timestamp operationDate, int itemId, int itemsCount, int status, String bin_code, String remarks, String itemName, Integer version) {
        this.id = id;
        this.customerName = customerName;
        this.operationDate = operationDate;
        this.itemId = itemId;
        this.itemsCount = itemsCount;
        this.status = status;
        this.bin_code = bin_code;
        this.remarks = remarks;
        this.itemName = itemName;
        this.version = version;
    }

    public WorkLog(int itemId, int itemsCount, String bin_code) {
        this.itemId = itemId;
        this.itemsCount = itemsCount;
        this.bin_code = bin_code;
    }

    public WorkLog(int itemId, int itemsCount, int status, String bin_code) {
        this.itemId = itemId;
        this.itemsCount = itemsCount;
        this.status = status;
        this.bin_code = bin_code;
    }

    public WorkLog(String customerName, Timestamp operationDate, int itemId, int itemsCount, int status) {
        this.customerName = customerName;
        this.operationDate = operationDate;
        this.itemId = itemId;
        this.itemsCount = itemsCount;
        this.status = status;
    }

    public WorkLog(String customerName, Timestamp operationDate, int itemId, int itemsCount, int status, String bin_code) {
        this.customerName = customerName;
        this.operationDate = operationDate;
        this.itemId = itemId;
        this.itemsCount = itemsCount;
        this.status = status;
        this.bin_code = bin_code;
    }


    public WorkLog(Long id, String customerName, int itemsCount, int status, String bin_code, String remarks, String itemName, int itemId,Timestamp operationDate) {
        this.id = id;
        this.customerName = customerName;
        this.itemsCount = itemsCount;
        this.status = status;
        this.bin_code = bin_code;
        this.remarks = remarks;
        this.itemName = itemName;
        this.itemId = itemId;
        this.operationDate=operationDate;
    }

    // -------------------- Getter 和 Setter --------------------

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Timestamp getOperationDate() {
        return operationDate;
    }

    public void setOperationDate(Timestamp operationDate) {
        this.operationDate = operationDate;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getItemsCount() {
        return itemsCount;
    }

    public void setItemsCount(int itemsCount) {
        this.itemsCount = itemsCount;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getBin_code() {
        return bin_code;
    }

    public void setBin_code(String bin_code) {
        this.bin_code = bin_code;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
