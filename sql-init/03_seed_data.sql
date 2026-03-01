USE sieuthiminiv2;

-- 1. categories
INSERT
    IGNORE INTO categories (name, description)
VALUES
    (
        'Đồ uống',
        'Nước ngọt, nước suối, trà, cà phê, sữa...'
    ),
    (
        'Thực phẩm khô',
        'Mì gói, bánh kẹo, gia vị, đồ ăn vặt'
    ),
    (
        'Sữa & sản phẩm từ sữa',
        'Sữa tươi, sữa chua, phô mai, sữa bột'
    ),
    (
        'Đồ dùng hàng ngày',
        'Xà phòng, giấy vệ sinh, bột giặt, nước rửa chén'
    );

-- 2. suppliers
INSERT
    IGNORE INTO suppliers (
        supplier_code,
        name,
        address,
        phone,
        email,
        contact_person
    )
VALUES
    (
        'NCC001',
        'Công ty Vinamilk',
        'Quận 7, TP.HCM',
        '19001001',
        'vinamilk@vn.com',
        'Chị Lan'
    ),
    (
        'NCC002',
        'Unilever Việt Nam',
        'Bình Dương',
        '18001234',
        'unilever@vn.com',
        'Anh Minh'
    ),
    (
        'NCC003',
        'Acecook Việt Nam',
        'Đồng Nai',
        '02838212345',
        'acecook@vn.com',
        'Chị Hương'
    );

-- 3. products
INSERT
    IGNORE INTO products (
        product_code,
        name,
        category_id,
        supplier_id,
        cost_price,
        selling_price,
        total_quantity,
        min_stock_level,
        unit,
        made_in,
        production_date,
        expire_date,
        STATUS,
        is_visible
    )
VALUES
    (
        'SP001',
        'Sữa tươi Vinamilk 1L',
        3,
        1,
        22000.00,
        32000.00,
        120,
        20,
        'Hộp',
        'Việt Nam',
        '2026-02-01',
        '2026-05-01',
        'ACTIVE',
        1
    ),
    (
        'SP002',
        'Mì Hảo Hảo tôm chua cay',
        2,
        3,
        3500.00,
        5000.00,
        400,
        50,
        'Gói',
        'Việt Nam',
        '2026-01-15',
        '2026-07-15',
        'ACTIVE',
        1
    ),
    (
        'SP003',
        'Nước suối Lavie 500ml',
        1,
        1,
        4000.00,
        7000.00,
        250,
        30,
        'Chai',
        'Việt Nam',
        '2026-02-10',
        '2027-02-10',
        'ACTIVE',
        1
    );

-- 4. customers
INSERT
    IGNORE INTO customers (
        customer_code,
        full_name,
        phone,
        email,
        address,
        loyalty_points,
        total_spent,
        customer_type,
        STATUS
    )
VALUES
    (
        'KH001',
        'Nguyễn Văn An',
        '0987654321',
        'an.nguyen@gmail.com',
        'Quận 1, TP.HCM',
        1250,
        4500000.00,
        'GOLD',
        'ACTIVE'
    ),
    (
        'KH002',
        'Trần Thị Bình',
        '0909123456',
        NULL,
        NULL,
        320,
        980000.00,
        'SILVER',
        'ACTIVE'
    );

-- 5. roles
INSERT
    IGNORE INTO roles (role_name, description)
VALUES
    ('ADMIN', 'Quản trị toàn hệ thống'),
    ('CASHIER', 'Thu ngân bán hàng'),
    ('WAREHOUSE', 'Nhân viên kho');

-- 6. permissions
INSERT
    IGNORE INTO permissions (permission_name, description)
VALUES
    ('PRODUCT_VIEW', 'Xem danh sách sản phẩm'),
    ('PRODUCT_ADD', 'Thêm sản phẩm'),
    ('INVOICE_CREATE', 'Tạo hóa đơn bán'),
    ('REPORT_SALES', 'Xem báo cáo doanh thu');

-- 7. role_permissions
INSERT
    IGNORE INTO role_permissions (role_id, permission_id)
VALUES
    (1, 1),
(1, 2),
(1, 3),
(1, 4),
    (2, 1),
(2, 3),
    (3, 1);

-- 8. employees (sau khi roles có dữ liệu)
INSERT
    IGNORE INTO employees (
        employee_code,
        name,
        user_name,
        password_hash,
        phone,
        email,
        hire_date,
        salary,
        role_id
    )
VALUES
    (
        'NV001',
        'Nguyễn Thị Thu Hương',
        'admin',
        'hashed_pass_admin',
        '0912345678',
        'admin@sieuthi.com',
        '2025-01-15',
        12000000,
        1
    ),
    (
        'NV002',
        'Trần Văn Hải',
        'thu_ngan1',
        'hashed_pass_cashier',
        '0987654321',
        'hai@sieuthi.com',
        '2025-06-01',
        6500000,
        2
    ),
    (
        'NV003',
        'Lê Thị Ngọc Lan',
        'kho_vien',
        'hashed_pass_warehouse',
        '0978123456',
        'ngoc@sieuthi.com',
        '2025-09-10',
        7500000,
        3
    );

-- 9. sales
INSERT
    IGNORE INTO sales (
        sale_code,
        sale_date,
        customer_id,
        employee_id,
        subtotal,
        discount_amount,
        total_amount,
        payment_method,
        STATUS
    )
VALUES
    (
        'S001',
        '2026-03-01 09:30:00',
        1,
        2,
        95000.00,
        5000.00,
        90000.00,
        'CASH',
        'COMPLETED'
    ),
    (
        'S002',
        '2026-03-01 14:15:00',
        2,
        2,
        65000.00,
        0.00,
        65000.00,
        'TRANSFER',
        'COMPLETED'
    );

-- 10. sales_invoices
INSERT
    IGNORE INTO sales_invoices (
        invoice_code,
        sale_id,
        customer_id,
        employee_id,
        subtotal,
        discount_amount,
        tax_amount,
        total_amount,
        payment_method,
        STATUS
    )
VALUES
    (
        'HD20260301-001',
        1,
        1,
        2,
        95000.00,
        5000.00,
        0.00,
        90000.00,
        'CASH',
        'COMPLETED'
    ),
    (
        'HD20260301-002',
        2,
        2,
        2,
        65000.00,
        0.00,
        0.00,
        65000.00,
        'TRANSFER',
        'COMPLETED'
    );

-- 11. sales_invoice_items
INSERT
    IGNORE INTO sales_invoice_items (
        invoice_id,
        product_id,
        quantity,
        unit_price,
        subtotal
    )
VALUES
    (1, 1, 2, 32000.00, 64000.00),
    (1, 2, 5, 5000.00, 25000.00),
    (1, 3, 1, 7000.00, 7000.00),
    (2, 4, 3, 22000.00, 66000.00);

-- 12. discounts
INSERT
    IGNORE INTO discounts (
        name,
        discount_type,
        value,
        min_order_amount,
        start_date,
        end_date,
        STATUS
    )
VALUES
    (
        'Giảm 10% toàn hóa đơn trên 500k',
        'PERCENT',
        10.00,
        500000.00,
        '2026-03-01',
        '2026-03-31',
        'ACTIVE'
    ),
    (
        'Giảm 20k cho sản phẩm sữa Vinamilk',
        'FIXED',
        20000.00,
        0.00,
        '2026-03-01',
        '2026-03-15',
        'ACTIVE'
    );

-- 13. discount_products
INSERT
    IGNORE INTO discount_products (discount_id, product_id)
VALUES
    (2, 1);

-- 14. sale_invoice_discounts
INSERT
    IGNORE INTO sale_invoice_discounts (discount_id, invoice_id)
VALUES
    (1, 1);

-- 15. purchases
INSERT
    IGNORE INTO purchases (
        purchase_code,
        purchase_date,
        supplier_id,
        employee_id,
        subtotal,
        discount_amount,
        tax_amount,
        total_amount,
        STATUS
    )
VALUES
    (
        'PN20260301-001',
        '2026-03-01 08:00:00',
        1,
        3,
        4500000.00,
        0.00,
        0.00,
        4500000.00,
        'RECEIVED'
    );

-- 16. purchase_invoices
INSERT
    IGNORE INTO purchase_invoices (
        invoice_code,
        purchase_id,
        date_in,
        supplier_id,
        employee_id,
        subtotal,
        discount_amount,
        tax_amount,
        total_amount,
        payment_method,
        STATUS
    )
VALUES
    (
        'HN20260301-001',
        1,
        '2026-03-01 10:00:00',
        1,
        3,
        4500000.00,
        0.00,
        0.00,
        4500000.00,
        'TRANSFER',
        'PAID'
    );

-- 17. purchase_invoice_items
INSERT
    IGNORE INTO purchase_invoice_items (
        invoice_id,
        product_id,
        quantity,
        unit_price,
        subtotal
    )
VALUES
    (1, 1, 100, 22000.00, 2200000.00),
    (1, 2, 200, 3500.00, 700000.00);

-- 18. supplier_discounts
INSERT
    IGNORE INTO supplier_discounts (discount_id, supplier_id)
VALUES
    (2, 1);