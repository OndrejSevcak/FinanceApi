CREATE TABLE IF NOT EXISTS account (
    acc_key BIGINT AUTO_INCREMENT PRIMARY KEY,
    balance DECIMAL(19,2) DEFAULT 0,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    currency_code VARCHAR(10) DEFAULT 'EUR',
    crypto_flag BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS account_type (
    acc_type_key VARCHAR(3) PRIMARY KEY,
    type_name VARCHAR(100) UNIQUE NOT NULL,
    description TEXT
);


INSERT INTO account_type (acc_type_key, type_name, description)
SELECT 'CUR', 'Currency account', 'A fiat currency account.'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM account_type WHERE acc_type_key = 'CUR');

INSERT INTO account_type (acc_type_key, type_name, description)
SELECT 'CRY', 'Crypto account', 'A cryptocurrency account.'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM account_type WHERE acc_type_key = 'CRY');

CREATE TABLE IF NOT EXISTS users (
    user_key BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(100),
    levell INT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS user_account (
    user_key BIGINT,
    acc_type_key VARCHAR(3),
    acc_key BIGINT,
    PRIMARY KEY (user_key, acc_type_key, acc_key),
    FOREIGN KEY (user_key) REFERENCES users(user_key),
    FOREIGN KEY (acc_key) REFERENCES account(acc_key),
    FOREIGN KEY (acc_type_key) REFERENCES account_type(acc_type_key)
);