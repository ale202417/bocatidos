CREATE TABLE partners (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    first_name VARCHAR(80) NOT NULL,
    last_name VARCHAR(80) NOT NULL,
    phone VARCHAR(30),
    email VARCHAR(120),
    status VARCHAR(20) NOT NULL,
    notes VARCHAR(500)
);

CREATE TABLE customers (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    full_name VARCHAR(140) NOT NULL,
    phone VARCHAR(30),
    email VARCHAR(120),
    address VARCHAR(255),
    notes VARCHAR(500)
);

CREATE TABLE expense_categories (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255)
);

CREATE TABLE production_workers (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    full_name VARCHAR(140) NOT NULL,
    default_hourly_rate DECIMAL(10, 2) NOT NULL,
    active BIT NOT NULL DEFAULT b'1'
);

CREATE TABLE sale_orders (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    order_number VARCHAR(30) NOT NULL,
    order_date DATE NOT NULL,
    partner_id BIGINT NOT NULL,
    customer_id BIGINT,
    payment_status VARCHAR(20) NOT NULL,
    subtotal DECIMAL(12, 2) NOT NULL,
    discount_amount DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    total_amount DECIMAL(12, 2) NOT NULL,
    amount_paid DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    notes VARCHAR(500),
    CONSTRAINT uq_sale_orders_order_number UNIQUE (order_number),
    CONSTRAINT fk_sale_orders_partner FOREIGN KEY (partner_id) REFERENCES partners (id),
    CONSTRAINT fk_sale_orders_customer FOREIGN KEY (customer_id) REFERENCES customers (id)
);

CREATE TABLE sale_order_items (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    sale_order_id BIGINT NOT NULL,
    item_name VARCHAR(140) NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(12, 2) NOT NULL,
    line_total DECIMAL(12, 2) NOT NULL,
    notes VARCHAR(255),
    CONSTRAINT fk_sale_order_items_order FOREIGN KEY (sale_order_id) REFERENCES sale_orders (id)
);

CREATE TABLE payment_records (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    sale_order_id BIGINT NOT NULL,
    payment_date DATE NOT NULL,
    amount DECIMAL(12, 2) NOT NULL,
    payment_method VARCHAR(30) NOT NULL,
    notes VARCHAR(255),
    CONSTRAINT fk_payment_records_order FOREIGN KEY (sale_order_id) REFERENCES sale_orders (id)
);

CREATE TABLE expenses (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    expense_date DATE NOT NULL,
    category_id BIGINT NOT NULL,
    partner_id BIGINT,
    type VARCHAR(30) NOT NULL,
    amount DECIMAL(12, 2) NOT NULL,
    description VARCHAR(255) NOT NULL,
    notes VARCHAR(500),
    CONSTRAINT fk_expenses_category FOREIGN KEY (category_id) REFERENCES expense_categories (id),
    CONSTRAINT fk_expenses_partner FOREIGN KEY (partner_id) REFERENCES partners (id)
);

CREATE TABLE production_sessions (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    production_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    units_produced INT NOT NULL,
    hourly_labor_rate DECIMAL(10, 2) NOT NULL,
    other_cost DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    notes VARCHAR(500)
);

CREATE TABLE production_session_workers (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    session_id BIGINT NOT NULL,
    worker_id BIGINT NOT NULL,
    hours_worked DECIMAL(6, 2) NOT NULL,
    hourly_rate DECIMAL(10, 2) NOT NULL,
    labor_cost DECIMAL(12, 2) NOT NULL,
    CONSTRAINT fk_session_workers_session FOREIGN KEY (session_id) REFERENCES production_sessions (id),
    CONSTRAINT fk_session_workers_worker FOREIGN KEY (worker_id) REFERENCES production_workers (id)
);

INSERT INTO expense_categories (name, description) VALUES
    ('Ingredients', 'Core food ingredients and fillings'),
    ('Packaging', 'Boxes, bags, labels, and wrappers'),
    ('Transport', 'Delivery and supply run costs'),
    ('Utilities', 'Gas, water, and other operating utilities');
