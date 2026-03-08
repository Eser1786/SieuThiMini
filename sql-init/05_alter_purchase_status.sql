-- Thêm PENDING vào ENUM status của bảng purchase_invoices
-- Chạy file này nếu DB đã tồn tại trước khi có trạng thái PENDING.
ALTER TABLE purchase_invoices
    MODIFY COLUMN `status` ENUM('PENDING', 'RECEIVED', 'CANCELLED')
        COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDING';
