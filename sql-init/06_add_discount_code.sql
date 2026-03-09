-- 06_add_discount_code.sql
-- Add discount_code VARCHAR(10) column to discounts table

-- Step 1: Add column (nullable first so existing rows can be updated)
ALTER TABLE discounts
    ADD COLUMN discount_code VARCHAR(10) DEFAULT NULL AFTER discount_id;

-- Step 2: Set readable discount codes by position (id-based, safe for fresh containers)
UPDATE discounts SET discount_code = 'SALE10'   WHERE discount_id = 1;
UPDATE discounts SET discount_code = 'GIAM20K'  WHERE discount_id = 2;
UPDATE discounts SET discount_code = 'BIRTHDAY' WHERE discount_id = 3;
UPDATE discounts SET discount_code = 'VIP15'    WHERE discount_id = 4;
-- Fallback: auto-generate for any rows not matched above
UPDATE discounts SET discount_code = UPPER(SUBSTR(MD5(CONCAT('DC', discount_id)), 1, 6))
WHERE discount_code IS NULL;

-- Step 3: Enforce NOT NULL and UNIQUE
ALTER TABLE discounts
    MODIFY COLUMN discount_code VARCHAR(10) NOT NULL DEFAULT '';

ALTER TABLE discounts
    ADD UNIQUE KEY uq_discount_code (discount_code);
