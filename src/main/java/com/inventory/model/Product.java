package com.inventory.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * JavaBean representing an Inventory Product.
 */
public class Product implements Serializable {
    private static final long serialVersionUID = 1L;

    private int productId;
    private String productName;
    private String skuCode;
    private String barcode;
    private int categoryId;
    private String categoryName;
    private String description;
    private double purchasePrice;
    private double sellingPrice;
    private int currentStock;
    private int minStockLevel;
    private Timestamp createdAt;

    public Product() {}

    public Product(int productId, String productName, String skuCode, String barcode,
                   int categoryId, String description, double purchasePrice,
                   double sellingPrice, int currentStock, int minStockLevel) {
        this.productId = productId;
        this.productName = productName;
        this.skuCode = skuCode;
        this.barcode = barcode;
        this.categoryId = categoryId;
        this.description = description;
        this.purchasePrice = purchasePrice;
        this.sellingPrice = sellingPrice;
        this.currentStock = currentStock;
        this.minStockLevel = minStockLevel;
    }

    /**
     * Calculates the stock alert status based on warehouse rules:
     * - Stock = 0 -> OUT_OF_STOCK
     * - Stock <= minStockLevel -> LOW_STOCK
     * - Stock > minStockLevel -> NORMAL
     */
    public String getAlertStatus() {
        if (currentStock <= 0) {
            return "OUT_OF_STOCK";
        } else if (currentStock <= minStockLevel) {
            return "LOW_STOCK";
        }
        return "NORMAL";
    }

    /**
     * Calculates total inventory valuation for this product.
     */
    public double getInventoryValue() {
        return currentStock * purchasePrice;
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

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public double getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public int getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(int currentStock) {
        this.currentStock = currentStock;
    }

    public int getMinStockLevel() {
        return minStockLevel;
    }

    public void setMinStockLevel(int minStockLevel) {
        this.minStockLevel = minStockLevel;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}

