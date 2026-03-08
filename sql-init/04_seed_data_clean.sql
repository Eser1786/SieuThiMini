USE sieuthiminiv2;

SET
    NAMES utf8mb4;

SET
    CHARACTER SET utf8mb4;

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
    ),
    (
        'Thực phẩm chế biến sẵn',
        'Chả, pate, xúc xích, cơm hộp, cơm nấm'
    ),
    (
        'Hóa mỹ phẩm và hóa chất',
        'Mỹ phẩm, nước tẩy trang, nước lau sàn, nước xả vải'
    ),
    (
        'Đồ gia dụng nhỏ',
        'Pin, băng keo,...'
    ),
    (
        'Đồ dùng học tập',
        'Vở, bút, gôm, thước,...'
    );

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
    ),
    (
        'NCC004',
        'Masan Consumer',
        'Quận 1, TP.HCM',
        '19001002',
        'masan@vn.com',
        'Anh Hoàng'
    ),
    (
        'NCC005',
        'CP Foods Việt Nam',
        'Bình Dương',
        '19001003',
        'cpfoods@vn.com',
        'Chị Mai'
    ),
    (
        'NCC006',
        'Mondelez Kinh Đô',
        'TP.HCM',
        '02838256789',
        'kinhdo@vn.com',
        'Anh Danh'
    ),
    (
        'NCC007',
        'P&G Việt Nam',
        'TP.HCM',
        '18001567',
        'pg@vn.com',
        'Chị Cúc'
    ),
    (
        'NCC008',
        'Nestlé Việt Nam',
        'Đồng Nai',
        '19001004',
        'nestle@vn.com',
        'Chị Thoa'
    );

INSERT
    IGNORE INTO products (
        product_code,
        name,
        image_path,
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
        '7 Up',
        'img\\7up.png',
        3,
        1,
        22000.00,
        32000.00,
        120,
        20,
        'Lon',
        'Việt Nam',
        '2026-02-01',
        '2026-05-01',
        'ACTIVE',
        1
    ),
    (
        'SP002',
        'Mì Hảo Hảo tôm chua cay',
        'img\\mitrontrung.png',
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
        'Fanta Việt Quất',
        'img\\Fanta Viet Quat.png',
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
        'Quận 4',
        320,
        980000.00,
        'SILVER',
        'ACTIVE'
    ),
    (
        'KH003',
        'Đoàn Văn Sáng',
        '0123456789',
        'Sanggay@gmail.com',
        'Thủ đức',
        150,
        1200000.00,
        'SILVER',
        'ACTIVE'
    ),
    (
        'KH004',
        'Gao đỏ',
        '0123456789',
        'Gaooooo@gmail.com',
        'Sao hỏa',
        1920,
        20000000.00,
        'DIAMOND',
        'ACTIVE'
    ),
    (
        'KH005',
        'Bát man',
        '0132456798',
        'noparents@gmail.com',
        'hang dơi',
        200,
        900000.00,
        'REGULAR',
        'ACTIVE'
    );

INSERT
    IGNORE INTO roles (role_name, description)
VALUES
    ('ADMIN', 'Quản trị toàn hệ thống'),
    ('MANAGER', 'Quản lý nhân viên, quản lý sản phẩm'),
    ('CASHIER', 'Thu ngân, tạo hóa đơn, thanh toán'),
    (
        'WAREHOUSE',
        'Nhân viên kho, nhập hàng, cập nhật tồn kho'
    ),
    ('SUPPORT', 'Xem thông tin khách hàng');

INSERT
    IGNORE INTO permissions (permission_name, description)
VALUES
    ('DASHBOARD_VIEW', 'Xem trang tổng quan'),
    (
        'REPORT_VIEW',
        'Xem báo cáo doanh thu, tồn kho, bán hàng'
    ),
    ('REPORT_EXPORT', 'Xuất báo cáo ra pdf, excel'),
    ('CATEGORY_VIEW', 'Xem danh mục sản phẩm'),
    ('CATEGORY_ADD', 'Thêm danh mục'),
    ('SUPPLIER_VIEW', 'Xem các nhà cung cấp'),
    ('SUPPLIER_ADD', 'Thêm nhà cung cấp'),
    ('PRODUCT_VIEW', 'Xem sản phẩm'),
    ('PRODUCT_ADD', 'Thêm sản phẩm'),
    ('CUSTOMER_VIEW', 'Xem khách hàng'),
    ('CUSTOMER_ADD', 'Thêm khách hàng'),
    ('EMPLOYEE_VIEW', 'Xem nhân viên'),
    ('EMPLOYEE_ADD', 'Thêm khách hàng'),
    ('EMPLOYEE_ROLE', 'Phân quyền cho nhân viên'),
    ('SALE_VIEW', 'Xem đơn hàng'),
    ('SALE_CREATE', 'Tạo đơn hàng'),
    ('PURCHASE_VIEW', 'Xem nhập hàng'),
    ('PURCHASE_CREATE', 'Tạo đơn nhập'),
    ('DISCOUNT_VIEW', 'Xem khuyến mãi'),
    ('DISCOUNT_ADD', 'Tạo khuyến mãi');

INSERT
    IGNORE INTO role_permissions (role_id, permission_id)
VALUES
    (1, 1),
    (1, 2),
    (1, 3),
    (1, 4),
    (1, 5),
    (1, 6),
    (1, 7),
    (1, 8),
    (1, 9),
    (1, 10),
    (1, 11),
    (1, 12),
    (1, 13),
    (1, 14),
    (1, 15),
    (1, 16),
    (1, 17),
    (1, 18),
    (1, 19),
    (1, 20),
    (2, 1),
    (2, 2),
    (2, 3),
    (2, 4),
    (2, 5),
    (2, 6),
    (2, 7),
    (2, 8),
    (2, 9),
    (2, 10),
    (2, 11),
    (2, 12),
    (2, 13),
    (2, 15),
    (2, 16),
    (2, 17),
    (2, 18),
    (2, 19),
    (2, 20),
    (3, 1),
    (3, 2),
    (3, 8),
    (3, 10),
    (3, 11),
    (3, 15),
    (3, 16),
    (3, 19),
    (3, 20),
    (4, 1),
    (4, 2),
    (4, 4),
    (4, 6),
    (4, 8),
    (4, 9),
    (4, 17),
    (4, 18),
    (5, 1),
    (5, 2),
    (5, 8),
    (5, 10),
    (5, 11),
    (5, 15);

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
        'Admin',
        'admin',
        '123456',
        '0123456789',
        'admin@sieuthi.com',
        '2025-01-15',
        12000000,
        1
    ),
    (
        'NV002',
        'Lê Đỗ Thái Anh',
        'eser',
        '123456',
        '0906649246',
        'thaianu2006@gmail.com',
        '2025-06-01',
        6500000,
        2
    ),
    (
        'NV003',
        'Nhan Thị Ngọc Trân',
        'ToRan',
        '123456',
        '0345435108',
        'ngoc@sieuthi.com',
        '2025-09-10',
        7500000,
        3
    ),
    (
        'NV004',
        'Nguyễn Thái Thảo',
        'MeoLanhManh',
        '123456',
        '0123456789',
        'NhuyenThao@gmail.com',
        '2025-10-01',
        5000000,
        4
    ),
    (
        'NV005',
        'Nguyễn Hoàng Sang',
        'KhungLong',
        '123456',
        '0123456789',
        'Loptruongkho11@gmail.com',
        '2025-10-15',
        7500000,
        5
    ),
    (
        'NV006',
        'Diệp Phương Duy',
        'PhDuy',
        '123456',
        '0123456789',
        'phduy@sieuthi.com',
        '2025-11-01',
        8000000,
        2
    ),
    (
        'NV007',
        'Lý Nguyễn',
        'LyNguyen',
        '123456',
        '0123456789',
        'lynguyen@sieuthi.com',
        '2025-12-01',
        7500000,
        3
    );

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

INSERT
    IGNORE INTO discount_products (discount_id, product_id)
VALUES
    (2, 1);

INSERT
    IGNORE INTO sale_invoice_discounts (discount_id, invoice_id)
VALUES
    (1, 1);

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

INSERT
    IGNORE INTO supplier_discounts (discount_id, supplier_id)
VALUES
    (2, 1);