package com.inventory.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * JavaBean representing a Stock Movement Transaction (Stock IN / Stock OUT).
 */
public class StockTransaction implements Serializable {
    private static final long serialVersionUID = 1L;

    private int transactionId;
    private int productId;
    private String productName;
    private String skuCode;
    private String transactionType; // 'IN' or 'OUT'
    private int quantity;
    private Integer supplierId;
    private String supplierName;
    private String referenceNumber;
    private String reason;
    private Timestamp transactionDate;

    public StockTransaction() {}

    public StockTransaction(int productId, String transactionType, int quantity,
                            Integer supplierId, String referenceNumber, String reason) {
        this.productId = productId;
        this.transactionType = transactionType;
        this.quantity = quantity;
        this.supplierId = supplierId;
        this.referenceNumber = referenceNumber;
        this.reason = reason;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Timestamp getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Timestamp transactionDate) {
        this.transactionDate = transactionDate;
    }
}

