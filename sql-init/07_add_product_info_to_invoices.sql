-- Migration: Add product_code and product_name columns to invoice_items tables
-- Purpose: Preserve product information in historical invoices even if product is soft-deleted

-- Add columns to purchase_invoice_items
ALTER TABLE purchase_invoice_items 
ADD COLUMN product_code VARCHAR(20) COLLATE utf8mb4_unicode_ci AFTER product_id,
ADD COLUMN product_name VARCHAR(255) COLLATE utf8mb4_unicode_ci AFTER product_code;

-- Add columns to sales_invoice_items
ALTER TABLE sales_invoice_items 
ADD COLUMN product_code VARCHAR(20) COLLATE utf8mb4_unicode_ci AFTER product_id,
ADD COLUMN product_name VARCHAR(255) COLLATE utf8mb4_unicode_ci AFTER product_code;

-- Backfill existing data from products table
UPDATE purchase_invoice_items pii
JOIN products p ON pii.product_id = p.product_id
SET pii.product_code = p.product_code, pii.product_name = p.name
WHERE pii.product_code IS NULL;

UPDATE sales_invoice_items sii
JOIN products p ON sii.product_id = p.product_id
SET sii.product_code = p.product_code, sii.product_name = p.name
WHERE sii.product_code IS NULL;
