package com.example.blueskywarehouse.Entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;


@Entity
public class AmazonRefund {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "order_date")
    private LocalDate orderDate;

    @Column(name = "refund_date")
    private LocalDate refundDate;

    @Column(name = "refund_amount")
    private BigDecimal refundAmount;

    @Column(name = "invoice_issued")
    private Boolean invoiceIssued;

    @Column(name = "invoice_date")
    private LocalDate invoiceDate;

    @Column(name = "refund_reason")
    private String refundReason;

    @Column(name = "returned_item")
    private String returnedItem;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "remark")
    private String remark;

    @Column(name = "credit_note_number")
    private String creditNoteNumber;
    @Version
    private Integer version;

    // ----------------------------------------

    public AmazonRefund() {
    }

    public AmazonRefund(Long id, String customerName, LocalDate orderDate, LocalDate refundDate, BigDecimal refundAmount, Boolean invoiceIssued, LocalDate invoiceDate, String refundReason, String returnedItem, Integer quantity, String remark, String creditNoteNumber) {
        this.id = id;
        this.customerName = customerName;
        this.orderDate = orderDate;
        this.refundDate = refundDate;
        this.refundAmount = refundAmount;
        this.invoiceIssued = invoiceIssued;
        this.invoiceDate = invoiceDate;
        this.refundReason = refundReason;
        this.returnedItem = returnedItem;
        this.quantity = quantity;
        this.remark = remark;
        this.creditNoteNumber = creditNoteNumber;
    }

    public AmazonRefund(Long id, String customerName, LocalDate orderDate, LocalDate refundDate, BigDecimal refundAmount, Boolean invoiceIssued, LocalDate invoiceDate, String refundReason, String returnedItem, Integer quantity, String remark, String creditNoteNumber, Integer version) {
        this.id = id;
        this.customerName = customerName;
        this.orderDate = orderDate;
        this.refundDate = refundDate;
        this.refundAmount = refundAmount;
        this.invoiceIssued = invoiceIssued;
        this.invoiceDate = invoiceDate;
        this.refundReason = refundReason;
        this.returnedItem = returnedItem;
        this.quantity = quantity;
        this.remark = remark;
        this.creditNoteNumber = creditNoteNumber;
        this.version = version;
    }



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public LocalDate getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDate orderDate) { this.orderDate = orderDate; }

    public LocalDate getRefundDate() { return refundDate; }
    public void setRefundDate(LocalDate refundDate) { this.refundDate = refundDate; }

    public BigDecimal getRefundAmount() { return refundAmount; }
    public void setRefundAmount(BigDecimal refundAmount) { this.refundAmount = refundAmount; }

    public Boolean getInvoiceIssued() { return invoiceIssued; }
    public void setInvoiceIssued(Boolean invoiceIssued) { this.invoiceIssued = invoiceIssued; }

    public LocalDate getInvoiceDate() { return invoiceDate; }
    public void setInvoiceDate(LocalDate invoiceDate) { this.invoiceDate = invoiceDate; }

    public String getRefundReason() { return refundReason; }
    public void setRefundReason(String refundReason) { this.refundReason = refundReason; }

    public String getReturnedItem() { return returnedItem; }
    public void setReturnedItem(String returnedItem) { this.returnedItem = returnedItem; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public String getCreditNoteNumber() { return creditNoteNumber; }
    public void setCreditNoteNumber(String creditNoteNumber) { this.creditNoteNumber = creditNoteNumber; }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
