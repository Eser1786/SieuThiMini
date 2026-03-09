-- Add is_deleted column to tables for soft delete functionality
-- Run this after creating tables
USE sieuthiminiv2;

-- Add is_deleted to customers table
ALTER TABLE
    customers
ADD
    COLUMN isdeleted TINYINT(1) NOT NULL DEFAULT 0;

ALTER TABLE
    products
ADD
    COLUMN isdeleted TINYINT(1) NOT NULL DEFAULT 0;

ALTER TABLE
    employees
ADD
    COLUMN isdeleted TINYINT(1) NOT NULL DEFAULT 0;

ALTER TABLE
    discounts
ADD
    COLUMN isdeleted TINYINT(1) NOT NULL DEFAULT 0;

ALTER TABLE
    sales
ADD
    COLUMN isdeleted TINYINT(1) NOT NULL DEFAULT 0;