CREATE TABLE Employee (
    id_employee VARCHAR(10) PRIMARY KEY,
    password_hash VARCHAR(255) NOT NULL,
    empl_surname VARCHAR(50) NOT NULL,
    empl_name VARCHAR(50) NOT NULL,
    empl_patronymic VARCHAR(50),
    empl_role VARCHAR(10) NOT NULL CHECK (empl_role IN ('CASHIER', 'MANAGER')),
    salary DECIMAL(13,4) NOT NULL CHECK (salary >= 0),
    date_of_birth DATE NOT NULL CHECK (date_of_birth <= CURRENT_DATE - INTERVAL '18 years'),
    date_of_start DATE NOT NULL,
    phone_number VARCHAR(13) NOT NULL,
    city VARCHAR(50) NOT NULL,
    street VARCHAR(50) NOT NULL,
    zip_code VARCHAR(9) NOT NULL
);

CREATE TABLE Category (
    category_number SERIAL PRIMARY KEY,
    category_name VARCHAR(50) NOT NULL
);

CREATE TABLE Product (
    id_product SERIAL PRIMARY KEY,
    category_number INT NOT NULL,
    product_name VARCHAR(50) NOT NULL,
    manufacturer VARCHAR(50) NOT NULL,
    characteristics VARCHAR(100) NOT NULL,
    FOREIGN KEY (category_number) REFERENCES Category(category_number)
     ON UPDATE CASCADE ON DELETE RESTRICT
);

CREATE TABLE Store_Product (
    UPC VARCHAR(12) PRIMARY KEY,
    UPC_prom VARCHAR(12),
    id_product INT NOT NULL,
    selling_price DECIMAL(13,4) NOT NULL CHECK (selling_price >= 0),
    products_number INT NOT NULL CHECK (products_number >= 0),
    promotional_product BOOLEAN NOT NULL,
    FOREIGN KEY (id_product) REFERENCES Product(id_product)
       ON UPDATE CASCADE ON DELETE RESTRICT,
    FOREIGN KEY (UPC_prom) REFERENCES Store_Product(UPC)
       ON UPDATE CASCADE ON DELETE SET NULL
);

CREATE TABLE Customer_Card (
    card_number VARCHAR(13) PRIMARY KEY,
    cust_surname VARCHAR(50) NOT NULL,
    cust_name VARCHAR(50) NOT NULL,
    cust_patronymic VARCHAR(50),
    phone_number VARCHAR(13) NOT NULL,
    city VARCHAR(50),
    street VARCHAR(50),
    zip_code VARCHAR(9),
    percent INT NOT NULL CHECK (percent >= 0 AND percent <= 100)
);

CREATE TABLE Check_Table (
     check_number VARCHAR(10) PRIMARY KEY,
     id_employee VARCHAR(10) NOT NULL,
     card_number VARCHAR(13),
     print_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
     sum_total DECIMAL(13,4) NOT NULL CHECK (sum_total >= 0),
     vat DECIMAL(13,4) NOT NULL CHECK (vat >= 0),
     FOREIGN KEY (id_employee) REFERENCES Employee(id_employee)
         ON UPDATE CASCADE ON DELETE RESTRICT,
     FOREIGN KEY (card_number) REFERENCES Customer_Card(card_number)
         ON UPDATE CASCADE ON DELETE RESTRICT
);

CREATE TABLE Sale (
    UPC VARCHAR(12),
    check_number VARCHAR(10),
    product_number INT NOT NULL CHECK (product_number > 0),
    selling_price DECIMAL(13,4) NOT NULL CHECK (selling_price >= 0),
    PRIMARY KEY (UPC, check_number),
    FOREIGN KEY (UPC) REFERENCES Store_Product(UPC)
      ON UPDATE CASCADE ON DELETE RESTRICT,
    FOREIGN KEY (check_number) REFERENCES Check_Table(check_number)
      ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE INDEX idx_employee_surname ON Employee(empl_surname);
CREATE INDEX idx_employee_role ON Employee(empl_role);
CREATE INDEX idx_product_name ON Product(product_name);
CREATE INDEX idx_product_category ON Product(category_number);
CREATE INDEX idx_store_product_promotional ON Store_Product(promotional_product);
CREATE INDEX idx_store_product_product ON Store_Product(id_product);
CREATE INDEX idx_check_date ON Check_Table(print_date);
CREATE INDEX idx_check_employee ON Check_Table(id_employee);
CREATE INDEX idx_check_card ON Check_Table(card_number);
CREATE INDEX idx_customer_surname ON Customer_Card(cust_surname);
CREATE INDEX idx_sale_check ON Sale(check_number);