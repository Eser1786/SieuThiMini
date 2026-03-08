CREATE TABLE discount_products (
    discount_id BIGINT,
    product_id BIGINT,
    PRIMARY KEY (discount_id, product_id),
    CONSTRAINT fk_discount_products_discount FOREIGN KEY (discount_id) REFERENCES discounts(discount_id) ON DELETE CASCADE,
    CONSTRAINT fk_discount_products_product FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE
);