USE sieuthiminiv2;

SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- ─── 1. CATEGORIES ──────────────────────────────────────────────────────────
INSERT IGNORE INTO categories (name, description) VALUES
    ('Đồ uống',                    'Nước ngọt, nước suối, trà, cà phê, sữa...'),
    ('Thực phẩm khô',              'Mì gói, bánh kẹo, gia vị, đồ ăn vặt'),
    ('Sữa & sản phẩm từ sữa',     'Sữa tươi, sữa chua, phô mai, sữa bột'),
    ('Đồ dùng hàng ngày',          'Xà phòng, giấy vệ sinh, bột giặt, nước rửa chén'),
    ('Thực phẩm chế biến sẵn',     'Chả, pate, xúc xích, cơm hộp'),
    ('Hóa mỹ phẩm và hóa chất',   'Mỹ phẩm, nước tẩy trang, nước lau sàn, nước xả vải'),
    ('Đồ gia dụng nhỏ',            'Pin, băng keo, bóng đèn...'),
    ('Đồ dùng học tập',            'Vở, bút, gôm, thước...');

-- ─── 2. SUPPLIERS ───────────────────────────────────────────────────────────
INSERT IGNORE INTO suppliers (supplier_code, name, address, phone, email, contact_person) VALUES
    ('NCC001', 'Công ty Vinamilk',       'Quận 7, TP.HCM',    '19001001',    'vinamilk@vn.com',   'Chị Lan'),
    ('NCC002', 'Unilever Việt Nam',      'Bình Dương',         '18001234',    'unilever@vn.com',   'Anh Minh'),
    ('NCC003', 'Acecook Việt Nam',       'Đồng Nai',           '02838212345', 'acecook@vn.com',    'Chị Hương'),
    ('NCC004', 'Masan Consumer',         'Quận 1, TP.HCM',    '19001002',    'masan@vn.com',      'Anh Hoàng'),
    ('NCC005', 'CP Foods Việt Nam',      'Bình Dương',         '19001003',    'cpfoods@vn.com',    'Chị Mai'),
    ('NCC006', 'Mondelez Kinh Đô',       'TP.HCM',             '02838256789', 'kinhdo@vn.com',     'Anh Danh'),
    ('NCC007', 'P&G Việt Nam',           'TP.HCM',             '18001567',    'pg@vn.com',         'Chị Cúc'),
    ('NCC008', 'Nestlé Việt Nam',        'Đồng Nai',           '19001004',    'nestle@vn.com',     'Chị Thoa');

-- ─── 3. PRODUCTS (12 sản phẩm đa dạng) ─────────────────────────────────────
-- Columns: product_code, name, image_path, category_id, supplier_id,
--          cost_price, selling_price, total_quantity, min_stock_level,
--          unit, made_in, production_date, expire_date, status, is_visible
INSERT IGNORE INTO products (
    product_code, name, image_path,
    category_id, supplier_id,
    cost_price, selling_price, total_quantity, min_stock_level,
    unit, made_in, production_date, expire_date, STATUS, is_visible
) VALUES
    ('SP001', '7 Up',                      'img/products/7up.png',               1, 1,  22000.00,  32000.00, 120, 20, 'Lon',   'Việt Nam', '2026-02-01', '2026-08-01', 'ACTIVE', 1),
    ('SP002', 'Mì Hảo Hảo tôm chua cay',  'img/products/mitrontrung.png',       2, 3,   3500.00,   5000.00, 400, 50, 'Gói',   'Việt Nam', '2026-01-15', '2026-07-15', 'ACTIVE', 1),
    ('SP003', 'Fanta Việt Quất',           'img/products/Fanta Viet Quat.png',   1, 1,   4000.00,   7000.00, 250, 30, 'Chai',  'Việt Nam', '2026-02-10', '2027-02-10', 'ACTIVE', 1),
    ('SP004', 'Sữa Vinamilk Tươi Tiệt Trùng', 'img/products/sua.png',                     3, 1,  12000.00,  18000.00, 200, 30, 'Hộp',   'Việt Nam', '2026-03-01', '2026-09-01', 'ACTIVE', 1),
    ('SP005', 'Pepsi lon 330ml',           'img/products/pepsi.png',                         1, 1,   8000.00,  12000.00, 180, 30, 'Lon',   'Việt Nam', '2026-02-20', '2026-08-20', 'ACTIVE', 1),
    ('SP006', 'Snack Oishi tôm',           'img/products/khanluaf.png',                         2, 6,   4500.00,   7500.00, 300, 40, 'Gói',   'Việt Nam', '2026-01-10', '2026-10-10', 'ACTIVE', 1),
    ('SP007', 'Nước rửa chén Sunlight',    'img/products/nuocruatay.png',                         4, 2,  18000.00,  25000.00,  80, 10, 'Chai',  'Việt Nam', '2025-12-01', '2027-12-01', 'ACTIVE', 1),
    ('SP008', 'Bột giặt OMO 3kg',         'img/products/no-product.png',                         4, 2,  95000.00, 135000.00,  50, 10, 'Túi',   'Việt Nam', '2025-11-01', '2027-11-01', 'ACTIVE', 1),
    ('SP009', 'Xúc xích CP',              'img/products/xucxichchebien.png',                         5, 5,  22000.00,  30000.00, 100, 15, 'Gói',   'Việt Nam', '2026-03-01', '2026-04-15', 'ACTIVE', 1),
    ('SP010', 'Dầu gội Clear bạc hà',     'img/products/daugoiclear.png',                         6, 7,  45000.00,  65000.00,  60, 10, 'Chai',  'Việt Nam', '2025-10-01', '2027-10-01', 'ACTIVE', 1),
    ('SP011', 'Pin AA Energizer (vỉ 2)',   'img/products/no-product.png',                         7, 8,  20000.00,  30000.00, 150, 20, 'Vỉ',    'Nhật Bản', '2025-01-01', '2030-01-01', 'ACTIVE', 1),
    ('SP012', 'Vở học sinh 200 trang',     'img/products/no-product.png',                         8, 4,   7000.00,  11000.00, 500, 50, 'Quyển', 'Việt Nam', '2025-06-01', '2028-06-01', 'ACTIVE', 1);

-- ─── 4. CUSTOMERS (7 khách hàng) ────────────────────────────────────────────
INSERT IGNORE INTO customers (customer_code, full_name, phone, email, address, loyalty_points, total_spent, customer_type, STATUS) VALUES
    ('KH001', 'Nguyễn Văn An',   '0987654321', 'an.nguyen@gmail.com',   'Quận 1, TP.HCM',  1250,  4500000.00, 'GOLD',    'ACTIVE'),
    ('KH002', 'Trần Thị Bình',   '0909123456', 'binhbong@gmail.com',                    'Quận 4, TP.HCM',   320,   980000.00, 'SILVER',  'ACTIVE'),
    ('KH003', 'Đoàn Văn Sáng',   '0123456789', 'sang@gmail.com',        'Thủ Đức, TP.HCM',  150,  1200000.00, 'SILVER',  'ACTIVE'),
    ('KH004', 'Gao Đỏ',          '0987654000', 'gao@gmail.com',         'Sao Hỏa',         1920, 20000000.00, 'DIAMOND', 'ACTIVE'),
    ('KH005', 'Bát Man',         '0132456798', 'noparents@gmail.com',   'Hang Dơi',         200,   900000.00, 'REGULAR', 'ACTIVE'),
    ('KH006', 'Lê Văn Cường',    '0369852411', 'cuong.le@gmail.com',    'Quận 7, TP.HCM',    80,   350000.00, 'REGULAR', 'ACTIVE'),
    ('KH007', 'Phạm Thị Diệu',   '0356789012', 'dieu.pham@gmail.com',  'Bình Thạnh, TP.HCM', 0,        0.00, 'REGULAR', 'ACTIVE');

-- Patch: đảm bảo KH002 có email (INSERT IGNORE không update nếu row đã tồn tại)
UPDATE customers SET email = 'binhbong@gmail.com' WHERE customer_code = 'KH002' AND (email IS NULL OR email = '');

-- ─── 5. ROLES ────────────────────────────────────────────────────────────────
INSERT IGNORE INTO roles (role_name, description) VALUES
    ('ADMIN',     'Quản trị toàn hệ thống'),
    ('MANAGER',   'Quản lý nhân viên, quản lý sản phẩm'),
    ('CASHIER',   'Thu ngân, tạo hóa đơn, thanh toán'),
    ('WAREHOUSE', 'Nhân viên kho, nhập hàng, cập nhật tồn kho'),
    ('SUPPORT',   'Xem thông tin khách hàng');

-- ─── 6. PERMISSIONS ─────────────────────────────────────────────────────────
INSERT IGNORE INTO permissions (permission_name, description) VALUES
    ('DASHBOARD_VIEW',  'Xem trang tổng quan'),
    ('REPORT_VIEW',     'Xem báo cáo doanh thu, tồn kho, bán hàng'),
    ('REPORT_EXPORT',   'Xuất báo cáo ra pdf, excel'),
    ('CATEGORY_VIEW',   'Xem danh mục sản phẩm'),
    ('CATEGORY_ADD',    'Thêm danh mục'),
    ('SUPPLIER_VIEW',   'Xem các nhà cung cấp'),
    ('SUPPLIER_ADD',    'Thêm nhà cung cấp'),
    ('PRODUCT_VIEW',    'Xem sản phẩm'),
    ('PRODUCT_ADD',     'Thêm sản phẩm'),
    ('CUSTOMER_VIEW',   'Xem khách hàng'),
    ('CUSTOMER_ADD',    'Thêm khách hàng'),
    ('EMPLOYEE_VIEW',   'Xem nhân viên'),
    ('EMPLOYEE_ADD',    'Thêm nhân viên'),
    ('EMPLOYEE_ROLE',   'Phân quyền cho nhân viên'),
    ('SALE_VIEW',       'Xem đơn hàng'),
    ('SALE_CREATE',     'Tạo đơn hàng'),
    ('PURCHASE_VIEW',   'Xem nhập hàng'),
    ('PURCHASE_CREATE', 'Tạo đơn nhập'),
    ('DISCOUNT_VIEW',   'Xem khuyến mãi'),
    ('DISCOUNT_ADD',    'Tạo khuyến mãi');

-- ─── 7. ROLE_PERMISSIONS ─────────────────────────────────────────────────────
-- ADMIN (1): all
INSERT IGNORE INTO role_permissions (role_id, permission_id) VALUES
    (1,1),(1,2),(1,3),(1,4),(1,5),(1,6),(1,7),(1,8),(1,9),(1,10),
    (1,11),(1,12),(1,13),(1,14),(1,15),(1,16),(1,17),(1,18),(1,19),(1,20);
-- MANAGER (2): all except delete/role
INSERT IGNORE INTO role_permissions (role_id, permission_id) VALUES
    (2,1),(2,2),(2,3),(2,4),(2,5),(2,6),(2,7),(2,8),(2,9),(2,10),
    (2,11),(2,12),(2,13),(2,15),(2,16),(2,17),(2,18),(2,19),(2,20);
-- CASHIER (3): sale, product view, customer, discount
INSERT IGNORE INTO role_permissions (role_id, permission_id) VALUES
    (3,1),(3,2),(3,8),(3,10),(3,11),(3,15),(3,16),(3,19),(3,20);
-- WAREHOUSE (4): product, purchase, category, supplier
INSERT IGNORE INTO role_permissions (role_id, permission_id) VALUES
    (4,1),(4,2),(4,4),(4,6),(4,8),(4,9),(4,17),(4,18);
-- SUPPORT (5): view only
INSERT IGNORE INTO role_permissions (role_id, permission_id) VALUES
    (5,1),(5,2),(5,8),(5,10),(5,11),(5,15);

-- ─── 8. EMPLOYEES (7 nhân viên) ─────────────────────────────────────────────
-- NOTE: phone và email phải unique
INSERT IGNORE INTO employees (employee_code, name, user_name, password_hash, phone, email, hire_date, salary, role_id) VALUES
    ('NV001', 'Admin',                'admin',       '123456', '0123456789', 'admin@sieuthi.com',    '2025-01-15', 12000000, 1),
    ('NV002', 'Lê Đỗ Thái Anh',      'eser',        '123456', '0906649246', 'thaianu2006@gmail.com','2025-06-01',  6500000, 2),
    ('NV003', 'Nhan Thị Ngọc Trân',  'ToRan',       '123456', '0345435108', 'ngoc@sieuthi.com',     '2025-09-10',  7500000, 3),
    ('NV004', 'Nguyễn Thái Thảo',    'MeoLanhManh', '123456', '0971234561', 'nguyenthao@sieuthi.com','2025-10-01', 5000000, 4),
    ('NV005', 'Nguyễn Hoàng Sang',   'KhungLong',   '123456', '0971234562', 'hoangsang@sieuthi.com','2025-10-15',  7500000, 5),
    ('NV006', 'Diệp Phương Duy',     'PhDuy',       '123456', '0971234563', 'phduy@sieuthi.com',    '2025-11-01',  8000000, 2),
    ('NV007', 'Lý Nguyễn',           'LyNguyen',    '123456', '0971234564', 'lynguyen@sieuthi.com', '2025-12-01',  7500000, 3);

-- ─── 9. DISCOUNTS (4 mã - discount_code sẽ được cập nhật trong 06) ──────────
-- NOTE: discount_code column chưa tồn tại ở bước này (thêm sau bởi 06)
INSERT IGNORE INTO discounts (name, discount_type, value, min_order_amount, start_date, end_date, STATUS) VALUES
    ('Giảm 10% đơn trên 100k',        'PERCENT', 10.00, 100000.00, '2026-01-01', '2026-12-31', 'ACTIVE'),
    ('Giảm 20.000đ đơn từ 50k',       'FIXED',   20000.00, 50000.00, '2026-01-01', '2026-12-31', 'ACTIVE'),
    ('Ưu đãi sinh nhật giảm 50%',     'PERCENT', 50.00,      0.00, '2026-01-01', '2026-12-31', 'ACTIVE'),
    ('Giảm 15% VIP đơn trên 500k',    'PERCENT', 15.00, 500000.00, '2026-01-01', '2026-06-30', 'INACTIVE');

-- ─── 10. SALES (10 đơn hàng đa trạng thái) ──────────────────────────────────
-- sale_code, sale_date, customer_id, employee_id, subtotal, discount_amount, STATUS, payment_method, total_amount, total_quantity, note
INSERT IGNORE INTO sales (sale_code, sale_date, customer_id, employee_id, subtotal, discount_amount, STATUS, payment_method, total_amount, total_quantity, note) VALUES
    ('S001', '2026-03-01 09:30:00', 1, 2,  96000.00,  0.00, 'COMPLETED',  'CASH',     96000.00, 3,  'SP001|7 Up|32000|1;SP002|Mì Hảo Hảo|5000|5;SP003|Fanta|7000|2'),
    ('S002', '2026-03-01 14:15:00', 2, 2,  65000.00,  0.00, 'COMPLETED',  'TRANSFER', 65000.00, 5,  'SP002|Mì Hảo Hảo|5000|5;SP011|Pin AA|30000|1;SP012|Vở|11000|0'),
    ('S003', '2026-03-03 10:00:00', 3, 3,  96000.00,  0.00, 'COMPLETED',  'CASH',     96000.00, 4,  'SP004|Sữa Vinamilk|18000|2;SP009|Xúc xích|30000|2'),
    ('S004', '2026-03-05 11:30:00', 4, 3, 270000.00, 27000.00, 'COMPLETED', 'CARD',  243000.00, 3,  'SP008|Bột giặt OMO|135000|1;SP010|Dầu gội|65000|1;SP007|Sunlight|25000|1'),
    ('S005', '2026-03-06 09:00:00', 5, 4,  37500.00,  0.00, 'CANCELLED',  'CASH',     37500.00, 5,  'SP006|Snack Oishi|7500|5'),
    ('S006', '2026-03-07 14:00:00', 1, 2, 174000.00, 20000.00, 'COMPLETED', 'TRANSFER', 154000.00, 7, 'SP001|7 Up|32000|2;SP005|Pepsi|12000|3;SP002|Mì Hảo Hảo|5000|2'),
    ('S007', '2026-03-08 10:30:00', 6, 3,  53000.00,  0.00, 'CONFIRMED',  'CASH',     53000.00, 3,  'SP003|Fanta|7000|1;SP012|Vở|11000|2;SP011|Pin AA|30000|1'),
    ('S008', '2026-03-08 15:00:00', 2, 2, 120000.00,  0.00, 'PENDING',    'CASH',    120000.00, 8,  'SP002|Mì Hảo Hảo|5000|8;SP006|Snack|7500|4;SP012|Vở|11000|2'),
    ('S009', '2026-03-09 09:00:00', NULL, 2, 84000.00, 0.00, 'PENDING',   'CASH',     84000.00, 5,  'SP005|Pepsi|12000|3;SP009|Xúc xích|30000|1;SP011|Pin AA|30000|1'),
    ('S010', '2026-03-09 11:00:00', 7, 3,  65000.00,  0.00, 'SHIPPING',   'TRANSFER', 65000.00, 4,  'SP004|Sữa Vinamilk|18000|1;SP003|Fanta|7000|2;SP006|Snack|7500|3');

-- ─── 11. PURCHASES & PURCHASE INVOICES (dữ liệu nhập kho) ───────────────────
INSERT IGNORE INTO purchases (purchase_code, purchase_date, supplier_id, employee_id, subtotal, discount_amount, tax_amount, total_amount, STATUS) VALUES
    ('PN20260301-001', '2026-03-01 08:00:00', 1, 4, 4560000.00, 0.00, 0.00, 4560000.00, 'CONFIRM'),
    ('PN20260305-002', '2026-03-05 08:30:00', 3, 4, 1400000.00, 0.00, 0.00, 1400000.00, 'CONFIRM');

INSERT IGNORE INTO purchase_invoices (invoice_code, purchase_id, date_in, supplier_id, employee_id, subtotal, discount_amount, tax_amount, total_amount, payment_method, STATUS) VALUES
    ('HN20260301-001', 1, '2026-03-01 10:00:00', 1, 4, 4560000.00, 0.00, 0.00, 4560000.00, 'TRANSFER', 'RECEIVED'),
    ('HN20260305-002', 2, '2026-03-05 10:30:00', 3, 4, 1400000.00, 0.00, 0.00, 1400000.00, 'TRANSFER', 'RECEIVED');

INSERT IGNORE INTO purchase_invoice_items (invoice_id, product_id, quantity, unit_price, subtotal) VALUES
    (1, 1, 100, 22000.00, 2200000.00),
    (1, 4, 120, 12000.00, 1440000.00),
    (1, 5,  90,  8000.00,  720000.00),
    (2, 2, 200,  3500.00,  700000.00),
    (2, 6, 150,  4500.00,  675000.00);
