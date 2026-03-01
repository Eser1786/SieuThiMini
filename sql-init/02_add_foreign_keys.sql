-- 02_add_foreign_keys.sql
USE sieuthiminiv2;

-- products
ALTER TABLE
    products
ADD
    CONSTRAINT fk_products_category FOREIGN KEY (category_id) REFERENCES categories(category_id) ON DELETE RESTRICT,
ADD
    CONSTRAINT fk_products_supplier FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id) ON DELETE RESTRICT;

-- purchase_invoice_items
ALTER TABLE
    purchase_invoice_items
ADD
    CONSTRAINT fk_purchase_invoice_items_invoice FOREIGN KEY (invoice_id) REFERENCES purchase_invoices(invoice_id) ON DELETE CASCADE,
ADD
    CONSTRAINT fk_purchase_invoice_items_product FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE RESTRICT;

-- purchase_invoices
ALTER TABLE
    purchase_invoices
ADD
    CONSTRAINT fk_purchase_invoices_purchase FOREIGN KEY (purchase_id) REFERENCES purchases(purchase_id) ON DELETE
SET
    NULL,
ADD
    CONSTRAINT fk_purchase_invoices_supplier FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id) ON DELETE RESTRICT,
ADD
    CONSTRAINT fk_purchase_invoices_employee FOREIGN KEY (employee_id) REFERENCES employees(employee_id) ON DELETE RESTRICT;

-- purchases
ALTER TABLE
    purchases
ADD
    CONSTRAINT fk_purchases_supplier FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id) ON DELETE RESTRICT,
ADD
    CONSTRAINT fk_purchases_employee FOREIGN KEY (employee_id) REFERENCES employees(employee_id) ON DELETE RESTRICT;

-- role_permissions
ALTER TABLE
    role_permissions
ADD
    CONSTRAINT fk_role_permissions_role FOREIGN KEY (role_id) REFERENCES roles(role_id) ON DELETE CASCADE,
ADD
    CONSTRAINT fk_role_permissions_permission FOREIGN KEY (permission_id) REFERENCES permissions(permission_id) ON DELETE CASCADE;

-- sale_invoice_discounts
ALTER TABLE
    sale_invoice_discounts
ADD
    CONSTRAINT fk_sale_invoice_discounts_discount FOREIGN KEY (discount_id) REFERENCES discounts(discount_id) ON DELETE CASCADE,
ADD
    CONSTRAINT fk_sale_invoice_discounts_invoice FOREIGN KEY (invoice_id) REFERENCES sales_invoices(invoice_id) ON DELETE CASCADE;

-- sales
ALTER TABLE
    sales
ADD
    CONSTRAINT fk_sales_customer FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE
SET
    NULL,
ADD
    CONSTRAINT fk_sales_employee FOREIGN KEY (employee_id) REFERENCES employees(employee_id) ON DELETE RESTRICT;

-- sales_invoice_items
ALTER TABLE
    sales_invoice_items
ADD
    CONSTRAINT fk_sales_invoice_items_invoice FOREIGN KEY (invoice_id) REFERENCES sales_invoices(invoice_id) ON DELETE CASCADE,
ADD
    CONSTRAINT fk_sales_invoice_items_product FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE RESTRICT;

-- sales_invoices
ALTER TABLE
    sales_invoices
ADD
    CONSTRAINT fk_sales_invoices_sale FOREIGN KEY (sale_id) REFERENCES sales(sale_id) ON DELETE
SET
    NULL,
ADD
    CONSTRAINT fk_sales_invoices_customer FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE
SET
    NULL,
ADD
    CONSTRAINT fk_sales_invoices_employee FOREIGN KEY (employee_id) REFERENCES employees(employee_id) ON DELETE RESTRICT;

-- supplier_discounts
ALTER TABLE
    supplier_discounts
ADD
    CONSTRAINT fk_supplier_discounts_discount FOREIGN KEY (discount_id) REFERENCES discounts(discount_id) ON DELETE CASCADE,
ADD
    CONSTRAINT fk_supplier_discounts_supplier FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id) ON DELETE CASCADE;

-- employees
ALTER TABLE
    employees
ADD
    CONSTRAINT fk_employees_role FOREIGN KEY (role_id) REFERENCES roles(role_id) ON DELETE
SET
    NULL;