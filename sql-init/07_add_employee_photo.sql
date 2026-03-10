-- Add photo_path column to employees table for storing avatar file path
USE sieuthiminiv2;

ALTER TABLE employees
ADD COLUMN photo_path VARCHAR(255) DEFAULT NULL;
