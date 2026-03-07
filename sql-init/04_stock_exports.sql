-- =============================================
-- Bảng phiếu xuất kho và chi tiết xuất kho
-- =============================================

CREATE TABLE IF NOT EXISTS stock_exports (
    export_id    BIGINT AUTO_INCREMENT PRIMARY KEY,
    export_code  VARCHAR(15)  NOT NULL UNIQUE,
    export_date  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    employee_id  BIGINT,
    reason       ENUM('SALE','CANCEL','TRANSFER','LOSS') NOT NULL DEFAULT 'LOSS',
    notes        TEXT,
    total_amount DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    status       ENUM('DONE','CANCELLED') NOT NULL DEFAULT 'DONE',
    created_at   DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_stock_exports_employee
        FOREIGN KEY (employee_id) REFERENCES employees(employee_id) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS stock_export_items (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    export_id   BIGINT NOT NULL,
    product_id  BIGINT NOT NULL,
    quantity    BIGINT NOT NULL,
    unit_price  DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    notes       TEXT,
    CONSTRAINT fk_stock_export_items_export
        FOREIGN KEY (export_id) REFERENCES stock_exports(export_id) ON DELETE CASCADE,
    CONSTRAINT fk_stock_export_items_product
        FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE RESTRICT
);
