-- ============================================================
-- INVENTORY MANAGEMENT SYSTEM - DATABASE SCHEMA & SAMPLE DATA
-- Technology: MySQL 8.x
-- ============================================================

CREATE DATABASE IF NOT EXISTS inventory_management
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE inventory_management;

-- ------------------------------------------------------------
-- 1. Table: admin
-- Stores administrative user credentials and profile details.
-- ------------------------------------------------------------
DROP TABLE IF EXISTS stock_transactions;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS suppliers;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS admin;

CREATE TABLE admin (
    admin_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- 2. Table: categories
-- Product categories for hierarchical organization.
-- ------------------------------------------------------------
CREATE TABLE categories (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- 3. Table: suppliers
-- Vendor records supplying items for warehouse stock.
-- ------------------------------------------------------------
CREATE TABLE suppliers (
    supplier_id INT AUTO_INCREMENT PRIMARY KEY,
    supplier_name VARCHAR(150) NOT NULL,
    contact_person VARCHAR(100),
    phone VARCHAR(30),
    email VARCHAR(100),
    address TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- 4. Table: products
-- Inventory items with SKU codes, stock levels, and pricing.
-- ------------------------------------------------------------
CREATE TABLE products (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    product_name VARCHAR(150) NOT NULL,
    sku_code VARCHAR(60) NOT NULL UNIQUE,
    barcode VARCHAR(100),
    category_id INT NOT NULL,
    description TEXT,
    purchase_price DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    selling_price DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    current_stock INT NOT NULL DEFAULT 0,
    min_stock_level INT NOT NULL DEFAULT 10,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_products_category
        FOREIGN KEY (category_id) REFERENCES categories (category_id)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE INDEX idx_products_sku ON products (sku_code);
CREATE INDEX idx_products_stock ON products (current_stock);

-- ------------------------------------------------------------
-- 5. Table: stock_transactions
-- Audit log of all Stock IN and Stock OUT operations.
-- ------------------------------------------------------------
CREATE TABLE stock_transactions (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    transaction_type ENUM('IN', 'OUT') NOT NULL,
    quantity INT NOT NULL,
    supplier_id INT NULL,
    reference_number VARCHAR(100),
    reason VARCHAR(255),
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_transactions_product
        FOREIGN KEY (product_id) REFERENCES products (product_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_transactions_supplier
        FOREIGN KEY (supplier_id) REFERENCES suppliers (supplier_id)
        ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB;

CREATE INDEX idx_transactions_date ON stock_transactions (transaction_date);

-- ============================================================
-- SAMPLE DATA INSERTIONS
-- ============================================================

-- Default Admin Account:
-- Username: admin
-- Password: admin123 (BCrypt hash verified)
INSERT INTO admin (username, password_hash, full_name, email)
VALUES ('admin', '$2a$10$Peotn0oBQb3yHuF.eDoeR.k79/Bd30iem66SL64aqMJjrxwZjfqbq', 'System Administrator', 'admin@warehouse.local');

-- Categories
INSERT INTO categories (category_name, description) VALUES
('Electronics', 'Electronic components, adapters, circuits, and sensors'),
('Hardware & Tools', 'Industrial tools, fasteners, bolts, and mechanical fittings'),
('Office Supplies', 'Packaging stationery, labels, barcode rolls, and papers'),
('Packaging Materials', 'Corrugated boxes, bubble wrap, packing tapes, and cushioning'),
('Safety Gear', 'PPE kits, industrial gloves, high-vis vests, and goggles');

-- Suppliers
INSERT INTO suppliers (supplier_name, contact_person, phone, email, address) VALUES
('Apex Global Components', 'John Davis', '+1-555-0192', 'orders@apexcomponents.com', '124 Industrial Pkwy, Chicago, IL'),
('Prime Hardware Corp', 'Sarah Jenkins', '+1-555-0144', 'contact@primehardware.com', '88 Assembly Blvd, Detroit, MI'),
('Direct Pack Supplies', 'Michael Lee', '+1-555-0178', 'sales@directpack.com', '502 Logistics Way, Dallas, TX'),
('ShieldSafe Industrial', 'Emily Watson', '+1-555-0163', 'info@shieldsafe.com', '77 Safety Ave, Atlanta, GA');

-- Products
-- Mix of: Normal stock, Low stock, and Out-of-stock items for demonstration!
INSERT INTO products (product_name, sku_code, barcode, category_id, description, purchase_price, selling_price, current_stock, min_stock_level) VALUES
('High-Torque Cordless Drill', 'SKU-DRL-001', '890123456781', 2, '18V Brushless heavy duty drill with 2 batteries', 45.00, 79.99, 45, 10),
('Precision Digital Caliper', 'SKU-CAL-002', '890123456782', 2, 'Stainless steel 6-inch 0.01mm resolution caliper', 18.50, 32.00, 8, 15), -- LOW STOCK
('M8 Stainless Steel Hex Bolts (100pk)', 'SKU-BLT-003', '890123456783', 2, 'Grade 304 marine stainless steel bolts', 7.20, 14.50, 150, 25),
('Thermal Barcode Label Rolls (500/roll)', 'SKU-LBL-004', '890123456784', 3, '4x6 inch direct thermal shipping labels', 5.00, 9.99, 120, 30),
('Heavy-Duty Corrugated Box 16x12x12', 'SKU-BOX-005', '890123456785', 4, '32 ECT single wall shipping cartons', 1.25, 2.75, 400, 100),
('HDMI 2.1 Ultra High Speed Cable 2m', 'SKU-HDM-006', '890123456786', 1, 'Braided 48Gbps 8K HDR video cable', 4.50, 11.99, 5, 20), -- LOW STOCK
('USB-C Industrial Docking Hub', 'SKU-HUB-007', '890123456787', 1, 'Multi-port aluminum USB-C hub with Power Delivery', 28.00, 54.00, 0, 10), -- OUT OF STOCK
('Industrial Nitrile Work Gloves (Box/100)', 'SKU-GLV-008', '890123456788', 5, 'Heavy-duty 6mil powder-free nitrile gloves', 8.50, 16.00, 60, 20),
('Heavy Duty Packing Tape 2-inch (6pk)', 'SKU-TPE-009', '890123456789', 4, 'Commercial grade acrylic parcel sealing tape', 6.00, 12.50, 0, 15), -- OUT OF STOCK
('ANSI Approved Safety Glasses', 'SKU-EYE-010', '890123456790', 5, 'Anti-scratch clear lens wrap-around protection', 2.80, 6.50, 85, 25);

-- Stock Transactions Sample History
INSERT INTO stock_transactions (product_id, transaction_type, quantity, supplier_id, reference_number, reason, transaction_date) VALUES
(1, 'IN', 50, 2, 'PO-2026-001', 'Initial purchase stock-in', DATE_SUB(NOW(), INTERVAL 5 DAY)),
(1, 'OUT', 5, NULL, 'SO-10023', 'Customer dispatch order #10023', DATE_SUB(NOW(), INTERVAL 4 DAY)),
(2, 'IN', 20, 2, 'PO-2026-002', 'Restock shipment', DATE_SUB(NOW(), INTERVAL 4 DAY)),
(2, 'OUT', 12, NULL, 'SO-10045', 'Internal equipment allotment', DATE_SUB(NOW(), INTERVAL 3 DAY)),
(3, 'IN', 200, 2, 'PO-2026-003', 'Bulk procurement', DATE_SUB(NOW(), INTERVAL 3 DAY)),
(3, 'OUT', 50, NULL, 'SO-10060', 'Assembly line supply', DATE_SUB(NOW(), INTERVAL 2 DAY)),
(7, 'OUT', 15, NULL, 'SO-10091', 'Wholesale order fulfilment (stock depleted)', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(8, 'IN', 100, 4, 'PO-2026-008', 'Quarterly PPE replenishment', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(8, 'OUT', 40, NULL, 'SO-10110', 'Distribution to Warehouse Staff', NOW());

