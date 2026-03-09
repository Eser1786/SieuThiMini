-- 06_add_discount_code.sql
-- Add discount_code VARCHAR(10) column to discounts table

-- Step 1: Add column (nullable first so existing rows can be updated)
ALTER TABLE discounts
    ADD COLUMN discount_code VARCHAR(10) DEFAULT NULL AFTER discount_id;

-- Step 2: Set readable discount codes by name
UPDATE discounts SET discount_code = 'SALE10'   WHERE name = 'Giảm 10% đơn trên 100k';
UPDATE discounts SET discount_code = 'GIAM20K'  WHERE name = 'Giảm 20.000đ đơn từ 50k';
UPDATE discounts SET discount_code = 'BIRTHDAY' WHERE name = 'Ưu đãi sinh nhật giảm 50%';
UPDATE discounts SET discount_code = 'VIP15'    WHERE name = 'Giảm 15% VIP đơn trên 500k';
-- Fallback: auto-generate for any rows not matched above
UPDATE discounts SET discount_code = UPPER(SUBSTR(MD5(CONCAT('DC', discount_id)), 1, 6))
WHERE discount_code IS NULL;

-- Step 3: Enforce NOT NULL and UNIQUE
ALTER TABLE discounts
    MODIFY COLUMN discount_code VARCHAR(10) NOT NULL DEFAULT '';

ALTER TABLE discounts
    ADD UNIQUE KEY uq_discount_code (discount_code);
