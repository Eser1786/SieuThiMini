-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: sieuthiminiv2
-- ------------------------------------------------------
-- Server version	8.0.44

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `categories`
--

DROP TABLE IF EXISTS `categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categories` (
  `category_id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`category_id`),
  UNIQUE KEY `name` (`name`),
  KEY `idx_category_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categories`
--

LOCK TABLES `categories` WRITE;
/*!40000 ALTER TABLE `categories` DISABLE KEYS */;
INSERT INTO `categories` VALUES (1,'Đồ uống','Nước ngọt, nước suối, trà...'),(2,'Thực phẩm khô','Mì gói, bánh kẹo, gia vị'),(3,'Sữa & sản phẩm từ sữa','Sữa tươi, sữa chua, phô mai');
/*!40000 ALTER TABLE `categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customers`
--

DROP TABLE IF EXISTS `customers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customers` (
  `customer_id` bigint NOT NULL AUTO_INCREMENT,
  `customer_code` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `full_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `address` text COLLATE utf8mb4_unicode_ci,
  `loyalty_points` bigint DEFAULT '0',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `last_purchase` datetime DEFAULT NULL,
  `total_spent` decimal(15,2) DEFAULT '0.00',
  `customer_type` enum('REGULAR','SILVER','GOLD','DIAMOND') COLLATE utf8mb4_unicode_ci DEFAULT 'REGULAR',
  `status` enum('ACTIVE','INACTIVE','BLOCKED') COLLATE utf8mb4_unicode_ci DEFAULT 'ACTIVE',
  PRIMARY KEY (`customer_id`),
  UNIQUE KEY `customer_code` (`customer_code`),
  UNIQUE KEY `phone` (`phone`),
  KEY `idx_phone` (`phone`),
  KEY `idx_customer_code` (`customer_code`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customers`
--

LOCK TABLES `customers` WRITE;
/*!40000 ALTER TABLE `customers` DISABLE KEYS */;
INSERT INTO `customers` VALUES (1,'KH001','Nguyễn Văn A','0987654321','vana@gmail.com','Q.1, TP.HCM',500,'2026-03-01 01:39:43',NULL,2500000.00,'GOLD','ACTIVE'),(2,'KH002','Trần Thị B','0909123456',NULL,NULL,120,'2026-03-01 01:39:43',NULL,800000.00,'SILVER','ACTIVE');
/*!40000 ALTER TABLE `customers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `discount_products`
--

DROP TABLE IF EXISTS `discount_products`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `discount_products` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `discount_id` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  `priority` tinyint DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_discount_product` (`discount_id`,`product_id`),
  KEY `idx_product` (`product_id`),
  CONSTRAINT `discount_products_ibfk_1` FOREIGN KEY (`discount_id`) REFERENCES `discounts` (`discount_id`) ON DELETE CASCADE,
  CONSTRAINT `discount_products_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `discount_products`
--

LOCK TABLES `discount_products` WRITE;
/*!40000 ALTER TABLE `discount_products` DISABLE KEYS */;
/*!40000 ALTER TABLE `discount_products` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `discounts`
--

DROP TABLE IF EXISTS `discounts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `discounts` (
  `discount_id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `discount_type` enum('PERCENT','FIXED','BUY_X_GET_Y') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PERCENT',
  `value` decimal(12,2) NOT NULL,
  `min_order_amount` decimal(15,2) NOT NULL DEFAULT '0.00',
  `start_date` date NOT NULL,
  `end_date` date NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `status` enum('ACTIVE','INACTIVE','EXPIRED') COLLATE utf8mb4_unicode_ci DEFAULT 'ACTIVE',
  `is_auto_apply` tinyint(1) DEFAULT '0',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`discount_id`),
  KEY `idx_date_status` (`start_date`,`end_date`,`status`),
  KEY `idx_type_value` (`discount_type`,`value`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `discounts`
--

LOCK TABLES `discounts` WRITE;
/*!40000 ALTER TABLE `discounts` DISABLE KEYS */;
/*!40000 ALTER TABLE `discounts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `employees`
--

DROP TABLE IF EXISTS `employees`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `employees` (
  `employee_id` bigint NOT NULL AUTO_INCREMENT,
  `employee_code` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `user_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password_hash` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `hire_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `salary` decimal(12,0) DEFAULT NULL,
  `role_id` bigint DEFAULT NULL,
  PRIMARY KEY (`employee_id`),
  UNIQUE KEY `employee_code` (`employee_code`),
  UNIQUE KEY `phone` (`phone`),
  UNIQUE KEY `email` (`email`),
  KEY `role_id` (`role_id`),
  KEY `idx_employee_code` (`employee_code`),
  KEY `idx_user_name` (`user_name`),
  CONSTRAINT `employees_ibfk_1` FOREIGN KEY (`role_id`) REFERENCES `roles` (`role_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `employees`
--

LOCK TABLES `employees` WRITE;
/*!40000 ALTER TABLE `employees` DISABLE KEYS */;
/*!40000 ALTER TABLE `employees` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `permissions`
--

DROP TABLE IF EXISTS `permissions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `permissions` (
  `permission_id` bigint NOT NULL AUTO_INCREMENT,
  `permission_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`permission_id`),
  UNIQUE KEY `permission_name` (`permission_name`),
  KEY `idx_permission_name` (`permission_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `permissions`
--

LOCK TABLES `permissions` WRITE;
/*!40000 ALTER TABLE `permissions` DISABLE KEYS */;
/*!40000 ALTER TABLE `permissions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `products`
--

DROP TABLE IF EXISTS `products`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `products` (
  `product_id` bigint NOT NULL AUTO_INCREMENT,
  `product_code` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `category_id` bigint NOT NULL,
  `supplier_id` bigint NOT NULL,
  `cost_price` decimal(15,2) NOT NULL,
  `selling_price` decimal(15,2) NOT NULL,
  `total_quantity` bigint NOT NULL DEFAULT '0',
  `min_stock_level` bigint DEFAULT '10',
  `made_in` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `production_date` date DEFAULT NULL,
  `expire_date` date DEFAULT NULL,
  `position` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `unit` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` enum('ACTIVE','INACTIVE','EXPIRED','OUT_OF_STOCK') COLLATE utf8mb4_unicode_ci DEFAULT 'ACTIVE',
  `is_visible` tinyint(1) DEFAULT '1',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`product_id`),
  UNIQUE KEY `product_code` (`product_code`),
  KEY `idx_product_code` (`product_code`),
  KEY `idx_product_name` (`name`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_supplier_id` (`supplier_id`),
  KEY `idx_status` (`status`),
  KEY `idx_expire_date` (`expire_date`),
  CONSTRAINT `products_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `categories` (`category_id`) ON DELETE RESTRICT,
  CONSTRAINT `products_ibfk_2` FOREIGN KEY (`supplier_id`) REFERENCES `suppliers` (`supplier_id`) ON DELETE RESTRICT,
  CONSTRAINT `products_chk_1` CHECK ((`selling_price` >= `cost_price`))
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `products`
--

LOCK TABLES `products` WRITE;
/*!40000 ALTER TABLE `products` DISABLE KEYS */;
INSERT INTO `products` VALUES (1,'SP001','Sữa tươi Vinamilk 1L',NULL,3,1,25000.00,32000.00,100,10,NULL,NULL,NULL,NULL,'hộp','ACTIVE',1,'2026-03-01 01:39:43','2026-03-01 01:39:43'),(2,'SP002','Mì Hảo Hảo tôm chua cay',NULL,2,2,3500.00,5000.00,500,10,NULL,NULL,NULL,NULL,'gói','ACTIVE',1,'2026-03-01 01:39:43','2026-03-01 01:39:43'),(3,'SP003','Nước suối Lavie 500ml',NULL,1,1,4000.00,7000.00,200,10,NULL,NULL,NULL,NULL,'chai','ACTIVE',1,'2026-03-01 01:39:43','2026-03-01 01:39:43');
/*!40000 ALTER TABLE `products` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `purchase_invoice_items`
--

DROP TABLE IF EXISTS `purchase_invoice_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `purchase_invoice_items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `invoice_id` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  `quantity` bigint NOT NULL,
  `unit_price` decimal(15,2) NOT NULL,
  `subtotal` decimal(15,2) NOT NULL,
  `notes` text COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`id`),
  KEY `idx_invoice_id` (`invoice_id`),
  KEY `idx_product_id` (`product_id`),
  CONSTRAINT `purchase_invoice_items_ibfk_1` FOREIGN KEY (`invoice_id`) REFERENCES `purchase_invoices` (`invoice_id`) ON DELETE CASCADE,
  CONSTRAINT `purchase_invoice_items_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`) ON DELETE RESTRICT,
  CONSTRAINT `purchase_invoice_items_chk_1` CHECK ((`quantity` > 0))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `purchase_invoice_items`
--

LOCK TABLES `purchase_invoice_items` WRITE;
/*!40000 ALTER TABLE `purchase_invoice_items` DISABLE KEYS */;
/*!40000 ALTER TABLE `purchase_invoice_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `purchase_invoices`
--

DROP TABLE IF EXISTS `purchase_invoices`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `purchase_invoices` (
  `invoice_id` bigint NOT NULL AUTO_INCREMENT,
  `invoice_code` varchar(15) COLLATE utf8mb4_unicode_ci NOT NULL,
  `purchase_id` bigint DEFAULT NULL,
  `date_in` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `supplier_id` bigint DEFAULT NULL,
  `employee_id` bigint DEFAULT NULL,
  `subtotal` decimal(15,2) NOT NULL,
  `discount_amount` decimal(15,2) DEFAULT '0.00',
  `tax_amount` decimal(15,2) DEFAULT '0.00',
  `total_amount` decimal(15,2) NOT NULL,
  `payment_method` enum('CASH','CARD','TRANSFER','DEBT') COLLATE utf8mb4_unicode_ci DEFAULT 'DEBT',
  `payment_status` enum('PENDING','PAID','CANCELLED') COLLATE utf8mb4_unicode_ci DEFAULT 'PENDING',
  `status` enum('RECEIVED','CANCELLED') COLLATE utf8mb4_unicode_ci DEFAULT 'RECEIVED',
  `notes` text COLLATE utf8mb4_unicode_ci,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`invoice_id`),
  UNIQUE KEY `invoice_code` (`invoice_code`),
  KEY `employee_id` (`employee_id`),
  KEY `idx_invoice_code` (`invoice_code`),
  KEY `idx_invoice_date` (`date_in`),
  KEY `idx_payment_status` (`payment_status`),
  KEY `idx_supplier_id` (`supplier_id`),
  KEY `idx_purchase_id` (`purchase_id`),
  CONSTRAINT `purchase_invoices_ibfk_1` FOREIGN KEY (`purchase_id`) REFERENCES `purchases` (`purchase_id`) ON DELETE SET NULL,
  CONSTRAINT `purchase_invoices_ibfk_2` FOREIGN KEY (`supplier_id`) REFERENCES `suppliers` (`supplier_id`) ON DELETE RESTRICT,
  CONSTRAINT `purchase_invoices_ibfk_3` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`employee_id`) ON DELETE RESTRICT,
  CONSTRAINT `purchase_invoices_chk_1` CHECK ((`total_amount` >= 0))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `purchase_invoices`
--

LOCK TABLES `purchase_invoices` WRITE;
/*!40000 ALTER TABLE `purchase_invoices` DISABLE KEYS */;
/*!40000 ALTER TABLE `purchase_invoices` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `purchases`
--

DROP TABLE IF EXISTS `purchases`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `purchases` (
  `purchase_id` bigint NOT NULL AUTO_INCREMENT,
  `purchase_code` varchar(15) COLLATE utf8mb4_unicode_ci NOT NULL,
  `purchase_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `supplier_id` bigint NOT NULL,
  `employee_id` bigint NOT NULL,
  `subtotal` decimal(15,2) NOT NULL,
  `discount_amount` decimal(15,2) DEFAULT '0.00',
  `tax_amount` decimal(15,2) DEFAULT '0.00',
  `total_amount` decimal(15,2) NOT NULL,
  `status` enum('DRAFT','PENDING','CONFIRM','CANCELLED') COLLATE utf8mb4_unicode_ci DEFAULT 'DRAFT',
  `notes` text COLLATE utf8mb4_unicode_ci,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`purchase_id`),
  UNIQUE KEY `purchase_code` (`purchase_code`),
  KEY `employee_id` (`employee_id`),
  KEY `idx_purchase_code` (`purchase_code`),
  KEY `idx_purchase_date` (`purchase_date`),
  KEY `idx_purchase_status` (`status`),
  KEY `idx_supplier_id` (`supplier_id`),
  CONSTRAINT `purchases_ibfk_1` FOREIGN KEY (`supplier_id`) REFERENCES `suppliers` (`supplier_id`) ON DELETE RESTRICT,
  CONSTRAINT `purchases_ibfk_2` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`employee_id`) ON DELETE RESTRICT,
  CONSTRAINT `purchases_chk_1` CHECK ((`total_amount` >= 0))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `purchases`
--

LOCK TABLES `purchases` WRITE;
/*!40000 ALTER TABLE `purchases` DISABLE KEYS */;
/*!40000 ALTER TABLE `purchases` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role_permissions`
--

DROP TABLE IF EXISTS `role_permissions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role_permissions` (
  `role_id` bigint NOT NULL,
  `permission_id` bigint NOT NULL,
  PRIMARY KEY (`role_id`,`permission_id`),
  KEY `permission_id` (`permission_id`),
  CONSTRAINT `role_permissions_ibfk_1` FOREIGN KEY (`role_id`) REFERENCES `roles` (`role_id`) ON DELETE CASCADE,
  CONSTRAINT `role_permissions_ibfk_2` FOREIGN KEY (`permission_id`) REFERENCES `permissions` (`permission_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role_permissions`
--

LOCK TABLES `role_permissions` WRITE;
/*!40000 ALTER TABLE `role_permissions` DISABLE KEYS */;
/*!40000 ALTER TABLE `role_permissions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
  `role_id` bigint NOT NULL AUTO_INCREMENT,
  `role_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`role_id`),
  UNIQUE KEY `role_name` (`role_name`),
  KEY `idx_role_name` (`role_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sale_invoice_discounts`
--

DROP TABLE IF EXISTS `sale_invoice_discounts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sale_invoice_discounts` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `discount_id` bigint NOT NULL,
  `invoice_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_discount_invoice` (`discount_id`,`invoice_id`),
  KEY `idx_invoice` (`invoice_id`),
  CONSTRAINT `sale_invoice_discounts_ibfk_1` FOREIGN KEY (`discount_id`) REFERENCES `discounts` (`discount_id`) ON DELETE CASCADE,
  CONSTRAINT `sale_invoice_discounts_ibfk_2` FOREIGN KEY (`invoice_id`) REFERENCES `sales_invoices` (`invoice_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sale_invoice_discounts`
--

LOCK TABLES `sale_invoice_discounts` WRITE;
/*!40000 ALTER TABLE `sale_invoice_discounts` DISABLE KEYS */;
/*!40000 ALTER TABLE `sale_invoice_discounts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sales`
--

DROP TABLE IF EXISTS `sales`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sales` (
  `sale_id` bigint NOT NULL AUTO_INCREMENT,
  `sale_code` varchar(15) COLLATE utf8mb4_unicode_ci NOT NULL,
  `sale_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `customer_id` bigint DEFAULT NULL,
  `employee_id` bigint NOT NULL,
  `subtotal` decimal(15,2) NOT NULL,
  `discount_amount` decimal(15,2) DEFAULT '0.00',
  `status` enum('COMPLETED','CANCELLED') COLLATE utf8mb4_unicode_ci DEFAULT 'COMPLETED',
  `payment_method` enum('CASH','CARD','TRANSFER') COLLATE utf8mb4_unicode_ci DEFAULT 'CASH',
  `total_amount` decimal(15,2) NOT NULL,
  `note` text COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`sale_id`),
  UNIQUE KEY `sale_code` (`sale_code`),
  KEY `customer_id` (`customer_id`),
  KEY `employee_id` (`employee_id`),
  KEY `idx_sale_code` (`sale_code`),
  KEY `idx_sale_date` (`sale_date`),
  CONSTRAINT `sales_ibfk_1` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`) ON DELETE SET NULL,
  CONSTRAINT `sales_ibfk_2` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`employee_id`) ON DELETE RESTRICT,
  CONSTRAINT `sales_chk_1` CHECK ((`total_amount` >= 0))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sales`
--

LOCK TABLES `sales` WRITE;
/*!40000 ALTER TABLE `sales` DISABLE KEYS */;
/*!40000 ALTER TABLE `sales` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sales_invoice_items`
--

DROP TABLE IF EXISTS `sales_invoice_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sales_invoice_items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `invoice_id` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  `quantity` bigint NOT NULL,
  `unit_price` decimal(15,2) NOT NULL,
  `subtotal` decimal(15,2) NOT NULL,
  `notes` text COLLATE utf8mb4_unicode_ci,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_invoice_id` (`invoice_id`),
  KEY `idx_product_id` (`product_id`),
  CONSTRAINT `sales_invoice_items_ibfk_1` FOREIGN KEY (`invoice_id`) REFERENCES `sales_invoices` (`invoice_id`) ON DELETE CASCADE,
  CONSTRAINT `sales_invoice_items_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`) ON DELETE RESTRICT,
  CONSTRAINT `sales_invoice_items_chk_1` CHECK ((`quantity` > 0))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sales_invoice_items`
--

LOCK TABLES `sales_invoice_items` WRITE;
/*!40000 ALTER TABLE `sales_invoice_items` DISABLE KEYS */;
/*!40000 ALTER TABLE `sales_invoice_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sales_invoices`
--

DROP TABLE IF EXISTS `sales_invoices`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sales_invoices` (
  `invoice_id` bigint NOT NULL AUTO_INCREMENT,
  `invoice_code` varchar(15) COLLATE utf8mb4_unicode_ci NOT NULL,
  `sale_id` bigint DEFAULT NULL,
  `customer_id` bigint DEFAULT NULL,
  `employee_id` bigint DEFAULT NULL,
  `subtotal` decimal(15,2) NOT NULL,
  `discount_amount` decimal(15,2) DEFAULT '0.00',
  `tax_amount` decimal(15,2) DEFAULT '0.00',
  `total_amount` decimal(15,2) NOT NULL,
  `payment_method` enum('CASH','CARD','TRANSFER') COLLATE utf8mb4_unicode_ci DEFAULT 'CASH',
  `status` enum('COMPLETED','CANCELLED') COLLATE utf8mb4_unicode_ci DEFAULT 'COMPLETED',
  PRIMARY KEY (`invoice_id`),
  UNIQUE KEY `invoice_code` (`invoice_code`),
  KEY `sale_id` (`sale_id`),
  KEY `customer_id` (`customer_id`),
  KEY `employee_id` (`employee_id`),
  KEY `idx_invoice_code` (`invoice_code`),
  CONSTRAINT `sales_invoices_ibfk_1` FOREIGN KEY (`sale_id`) REFERENCES `sales` (`sale_id`) ON DELETE SET NULL,
  CONSTRAINT `sales_invoices_ibfk_2` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`) ON DELETE SET NULL,
  CONSTRAINT `sales_invoices_ibfk_3` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`employee_id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sales_invoices`
--

LOCK TABLES `sales_invoices` WRITE;
/*!40000 ALTER TABLE `sales_invoices` DISABLE KEYS */;
/*!40000 ALTER TABLE `sales_invoices` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `supplier_discounts`
--

DROP TABLE IF EXISTS `supplier_discounts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `supplier_discounts` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `discount_id` bigint NOT NULL,
  `supplier_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_discount_supplier` (`discount_id`,`supplier_id`),
  KEY `idx_supplier` (`supplier_id`),
  CONSTRAINT `supplier_discounts_ibfk_1` FOREIGN KEY (`discount_id`) REFERENCES `discounts` (`discount_id`) ON DELETE CASCADE,
  CONSTRAINT `supplier_discounts_ibfk_2` FOREIGN KEY (`supplier_id`) REFERENCES `suppliers` (`supplier_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `supplier_discounts`
--

LOCK TABLES `supplier_discounts` WRITE;
/*!40000 ALTER TABLE `supplier_discounts` DISABLE KEYS */;
/*!40000 ALTER TABLE `supplier_discounts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `suppliers`
--

DROP TABLE IF EXISTS `suppliers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `suppliers` (
  `supplier_id` bigint NOT NULL AUTO_INCREMENT,
  `supplier_code` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `address` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `email` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `contact_person` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`supplier_id`),
  UNIQUE KEY `supplier_code` (`supplier_code`),
  KEY `idx_supplier_code` (`supplier_code`),
  KEY `idx_supplier_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `suppliers`
--

LOCK TABLES `suppliers` WRITE;
/*!40000 ALTER TABLE `suppliers` DISABLE KEYS */;
INSERT INTO `suppliers` VALUES (1,'NCC001','Vinamilk','Quận 7, TP.HCM','vinamilk@vn.com','19001001',NULL,'2026-03-01 01:39:43','2026-03-01 01:39:43'),(2,'NCC002','Unilever','Bình Dương','unilever@vn.com','18001002',NULL,'2026-03-01 01:39:43','2026-03-01 01:39:43');
/*!40000 ALTER TABLE `suppliers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'sieuthiminiv2'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-03-01  1:52:33
