-- 01_create_tables.sql
-- Tạo database (nếu cần, nhưng Docker đã tạo từ env)
CREATE DATABASE IF NOT EXISTS sieuthiminiv2 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE sieuthiminiv2;

-- categories
CREATE TABLE categories (
    category_id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(50) COLLATE utf8mb4_unicode_ci NOT NULL,
    description TEXT COLLATE utf8mb4_unicode_ci,
    PRIMARY KEY (category_id),
    UNIQUE KEY name (name),
    KEY idx_category_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- customers
CREATE TABLE customers (
    customer_id BIGINT NOT NULL AUTO_INCREMENT,
    customer_code VARCHAR(20) COLLATE utf8mb4_unicode_ci NOT NULL,
    full_name VARCHAR(255) COLLATE utf8mb4_unicode_ci NOT NULL,
    phone VARCHAR(20) COLLATE utf8mb4_unicode_ci NOT NULL,
    email VARCHAR(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    address TEXT COLLATE utf8mb4_unicode_ci,
    loyalty_points BIGINT DEFAULT '0',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    last_purchase DATETIME DEFAULT NULL,
    total_spent DECIMAL(15,2) DEFAULT '0.00',
    customer_type ENUM('REGULAR','SILVER','GOLD','DIAMOND') COLLATE utf8mb4_unicode_ci DEFAULT 'REGULAR',
    status ENUM('ACTIVE','INACTIVE','BLOCKED') COLLATE utf8mb4_unicode_ci DEFAULT 'ACTIVE',
    PRIMARY KEY (customer_id),
    UNIQUE KEY customer_code (customer_code),
    UNIQUE KEY phone (phone),
    KEY idx_phone (phone),
    KEY idx_customer_code (customer_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- discounts
CREATE TABLE discounts (
    discount_id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(150) COLLATE utf8mb4_unicode_ci NOT NULL,
    discount_type ENUM('PERCENT','FIXED','BUY_X_GET_Y') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PERCENT',
    value DECIMAL(12,2) NOT NULL,
    min_order_amount DECIMAL(15,2) NOT NULL DEFAULT '0.00',
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    description TEXT COLLATE utf8mb4_unicode_ci,
    status ENUM('ACTIVE','INACTIVE','EXPIRED') COLLATE utf8mb4_unicode_ci DEFAULT 'ACTIVE',
    is_auto_apply TINYINT(1) DEFAULT '0',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (discount_id),
    KEY idx_date_status (start_date, end_date, status),
    KEY idx_type_value (discount_type, value)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- employees
CREATE TABLE employees (
    employee_id BIGINT NOT NULL AUTO_INCREMENT,
    employee_code VARCHAR(20) COLLATE utf8mb4_unicode_ci NOT NULL,
    name VARCHAR(100) COLLATE utf8mb4_unicode_ci NOT NULL,
    user_name VARCHAR(100) COLLATE utf8mb4_unicode_ci NOT NULL,
    password_hash VARCHAR(255) COLLATE utf8mb4_unicode_ci NOT NULL,
    phone VARCHAR(20) COLLATE utf8mb4_unicode_ci NOT NULL,
    email VARCHAR(100) COLLATE utf8mb4_unicode_ci NOT NULL,
    hire_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    salary DECIMAL(12,0) DEFAULT NULL,
    role_id BIGINT DEFAULT NULL,
    PRIMARY KEY (employee_id),
    UNIQUE KEY employee_code (employee_code),
    UNIQUE KEY phone (phone),
    UNIQUE KEY email (email),
    KEY role_id (role_id),
    KEY idx_employee_code (employee_code),
    KEY idx_user_name (user_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- permissions
CREATE TABLE permissions (
    permission_id BIGINT NOT NULL AUTO_INCREMENT,
    permission_name VARCHAR(100) COLLATE utf8mb4_unicode_ci NOT NULL,
    description VARCHAR(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (permission_id),
    UNIQUE KEY permission_name (permission_name),
    KEY idx_permission_name (permission_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- products
CREATE TABLE products (
    product_id BIGINT NOT NULL AUTO_INCREMENT,
    product_code VARCHAR(20) COLLATE utf8mb4_unicode_ci NOT NULL,
    name VARCHAR(150) COLLATE utf8mb4_unicode_ci NOT NULL,
    description TEXT COLLATE utf8mb4_unicode_ci,
    category_id BIGINT NOT NULL,
    supplier_id BIGINT NOT NULL,
    cost_price DECIMAL(15,2) NOT NULL,
    selling_price DECIMAL(15,2) NOT NULL,
    total_quantity BIGINT NOT NULL DEFAULT '0',
    min_stock_level BIGINT DEFAULT '10',
    made_in VARCHAR(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    production_date DATE DEFAULT NULL,
    expire_date DATE DEFAULT NULL,
    position VARCHAR(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    unit VARCHAR(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    status ENUM('ACTIVE','INACTIVE','EXPIRED','OUT_OF_STOCK') COLLATE utf8mb4_unicode_ci DEFAULT 'ACTIVE',
    is_visible TINYINT(1) DEFAULT '1',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (product_id),
    UNIQUE KEY product_code (product_code),
    KEY idx_product_code (product_code),
    KEY idx_product_name (name),
    KEY idx_category_id (category_id),
    KEY idx_supplier_id (supplier_id),
    KEY idx_status (status),
    KEY idx_expire_date (expire_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- purchase_invoice_items
CREATE TABLE purchase_invoice_items (
    id BIGINT NOT NULL AUTO_INCREMENT,
    invoice_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity BIGINT NOT NULL,
    unit_price DECIMAL(15,2) NOT NULL,
    subtotal DECIMAL(15,2) NOT NULL,
    notes TEXT COLLATE utf8mb4_unicode_ci,
    PRIMARY KEY (id),
    KEY idx_invoice_id (invoice_id),
    KEY idx_product_id (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- purchase_invoices
CREATE TABLE purchase_invoices (
    invoice_id BIGINT NOT NULL AUTO_INCREMENT,
    invoice_code VARCHAR(15) COLLATE utf8mb4_unicode_ci NOT NULL,
    purchase_id BIGINT DEFAULT NULL,
    date_in DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    supplier_id BIGINT DEFAULT NULL,
    employee_id BIGINT DEFAULT NULL,
    subtotal DECIMAL(15,2) NOT NULL,
    discount_amount DECIMAL(15,2) DEFAULT '0.00',
    tax_amount DECIMAL(15,2) DEFAULT '0.00',
    total_amount DECIMAL(15,2) NOT NULL,
    payment_method ENUM('CASH','CARD','TRANSFER','DEBT') COLLATE utf8mb4_unicode_ci DEFAULT 'DEBT',
    payment_status ENUM('PENDING','PAID','CANCELLED') COLLATE utf8mb4_unicode_ci DEFAULT 'PENDING',
    status ENUM('RECEIVED','CANCELLED') COLLATE utf8mb4_unicode_ci DEFAULT 'RECEIVED',
    notes TEXT COLLATE utf8mb4_unicode_ci,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (invoice_id),
    UNIQUE KEY invoice_code (invoice_code),
    KEY employee_id (employee_id),
    KEY idx_invoice_code (invoice_code),
    KEY idx_invoice_date (date_in),
    KEY idx_payment_status (payment_status),
    KEY idx_supplier_id (supplier_id),
    KEY idx_purchase_id (purchase_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- purchases
CREATE TABLE purchases (
    purchase_id BIGINT NOT NULL AUTO_INCREMENT,
    purchase_code VARCHAR(15) COLLATE utf8mb4_unicode_ci NOT NULL,
    purchase_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    supplier_id BIGINT NOT NULL,
    employee_id BIGINT NOT NULL,
    subtotal DECIMAL(15,2) NOT NULL,
    discount_amount DECIMAL(15,2) DEFAULT '0.00',
    tax_amount DECIMAL(15,2) DEFAULT '0.00',
    total_amount DECIMAL(15,2) NOT NULL,
    status ENUM('DRAFT','PENDING','CONFIRM','CANCELLED') COLLATE utf8mb4_unicode_ci DEFAULT 'DRAFT',
    notes TEXT COLLATE utf8mb4_unicode_ci,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (purchase_id),
    UNIQUE KEY purchase_code (purchase_code),
    KEY employee_id (employee_id),
    KEY idx_purchase_code (purchase_code),
    KEY idx_purchase_date (purchase_date),
    KEY idx_purchase_status (status),
    KEY idx_supplier_id (supplier_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- role_permissions
CREATE TABLE role_permissions (
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    KEY permission_id (permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- roles
CREATE TABLE roles (
    role_id BIGINT NOT NULL AUTO_INCREMENT,
    role_name VARCHAR(100) COLLATE utf8mb4_unicode_ci NOT NULL,
    description VARCHAR(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    PRIMARY KEY (role_id),
    UNIQUE KEY role_name (role_name),
    KEY idx_role_name (role_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- sale_invoice_discounts
CREATE TABLE sale_invoice_discounts (
    id BIGINT NOT NULL AUTO_INCREMENT,
    discount_id BIGINT NOT NULL,
    invoice_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_discount_invoice (discount_id, invoice_id),
    KEY idx_invoice (invoice_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- sales
CREATE TABLE sales (
    sale_id BIGINT NOT NULL AUTO_INCREMENT,
    sale_code VARCHAR(15) COLLATE utf8mb4_unicode_ci NOT NULL,
    sale_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    customer_id BIGINT DEFAULT NULL,
    employee_id BIGINT NOT NULL,
    subtotal DECIMAL(15,2) NOT NULL,
    discount_amount DECIMAL(15,2) DEFAULT '0.00',
    status ENUM('COMPLETED','CANCELLED') COLLATE utf8mb4_unicode_ci DEFAULT 'COMPLETED',
    payment_method ENUM('CASH','CARD','TRANSFER') COLLATE utf8mb4_unicode_ci DEFAULT 'CASH',
    total_amount DECIMAL(15,2) NOT NULL,
    note TEXT COLLATE utf8mb4_unicode_ci,
    PRIMARY KEY (sale_id),
    UNIQUE KEY sale_code (sale_code),
    KEY customer_id (customer_id),
    KEY employee_id (employee_id),
    KEY idx_sale_code (sale_code),
    KEY idx_sale_date (sale_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- sales_invoice_items
CREATE TABLE sales_invoice_items (
    id BIGINT NOT NULL AUTO_INCREMENT,
    invoice_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity BIGINT NOT NULL,
    unit_price DECIMAL(15,2) NOT NULL,
    subtotal DECIMAL(15,2) NOT NULL,
    notes TEXT COLLATE utf8mb4_unicode_ci,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_invoice_id (invoice_id),
    KEY idx_product_id (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- sales_invoices
CREATE TABLE sales_invoices (
    invoice_id BIGINT NOT NULL AUTO_INCREMENT,
    invoice_code VARCHAR(15) COLLATE utf8mb4_unicode_ci NOT NULL,
    sale_id BIGINT DEFAULT NULL,
    customer_id BIGINT DEFAULT NULL,
    employee_id BIGINT DEFAULT NULL,
    subtotal DECIMAL(15,2) NOT NULL,
    discount_amount DECIMAL(15,2) DEFAULT '0.00',
    tax_amount DECIMAL(15,2) DEFAULT '0.00',
    total_amount DECIMAL(15,2) NOT NULL,
    payment_method ENUM('CASH','CARD','TRANSFER') COLLATE utf8mb4_unicode_ci DEFAULT 'CASH',
    status ENUM('COMPLETED','CANCELLED') COLLATE utf8mb4_unicode_ci DEFAULT 'COMPLETED',
    PRIMARY KEY (invoice_id),
    UNIQUE KEY invoice_code (invoice_code),
    KEY sale_id (sale_id),
    KEY customer_id (customer_id),
    KEY employee_id (employee_id),
    KEY idx_invoice_code (invoice_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- supplier_discounts
CREATE TABLE supplier_discounts (
    id BIGINT NOT NULL AUTO_INCREMENT,
    discount_id BIGINT NOT NULL,
    supplier_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_discount_supplier (discount_id, supplier_id),
    KEY idx_supplier (supplier_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- suppliers
CREATE TABLE suppliers (
    supplier_id BIGINT NOT NULL AUTO_INCREMENT,
    supplier_code VARCHAR(20) COLLATE utf8mb4_unicode_ci NOT NULL,
    name VARCHAR(150) COLLATE utf8mb4_unicode_ci NOT NULL,
    address VARCHAR(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    email VARCHAR(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    phone VARCHAR(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    contact_person VARCHAR(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (supplier_id),
    UNIQUE KEY supplier_code (supplier_code),
    KEY idx_supplier_code (supplier_code),
    KEY idx_supplier_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;