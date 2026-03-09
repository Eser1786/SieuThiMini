-- Add isdeleted column to tables for soft delete functionality
-- Run this after creating tables
USE sieuthiminiv2;

-- Add isdeleted to customers table
ALTER TABLE
    customers
ADD
    COLUMN isdeleted TINYINT(1) DEFAULT 0 NOT NULL
AFTER
    STATUS;

-- Add isdeleted to products table
ALTER TABLE
    products
ADD
    COLUMN isdeleted TINYINT(1) DEFAULT 0 NOT NULL
AFTER
    is_visible;

-- Add isdeleted to employees table
ALTER TABLE
    employees
ADD
    COLUMN isdeleted TINYINT(1) DEFAULT 0 NOT NULL
AFTER
    STATUS;

-- Add isdeleted to discounts table
ALTER TABLE
    discounts
ADD
    COLUMN isdeleted TINYINT(1) DEFAULT 0 NOT NULL
AFTER
    is_auto_apply;