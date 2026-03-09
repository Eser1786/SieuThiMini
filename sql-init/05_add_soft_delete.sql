-- Add is_deleted column to tables for soft delete functionality
-- Run this after creating tables
USE sieuthiminiv2;

-- Add is_deleted to customers table
ALTER TABLE customers
ADD COLUMN is_deleted TINYINT(1) NOT NULL DEFAULT 0;

-- Add is_deleted to products table
ALTER TABLE products
ADD COLUMN is_deleted TINYINT(1) NOT NULL DEFAULT 0;

-- Add is_deleted to employees table
ALTER TABLE employees
ADD COLUMN is_deleted TINYINT(1) NOT NULL DEFAULT 0;

-- Add is_deleted to discounts table
ALTER TABLE discounts
ADD COLUMN is_deleted TINYINT(1) NOT NULL DEFAULT 0;