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

CREATE TABLE IF NOT EXISTS transaction_type (
    tx_type_key VARCHAR(30) PRIMARY KEY,
    description VARCHAR(255)
);

/*
INSERT INTO transaction_type (tx_type_key, description) VALUES
('FIAT_DEPOSIT', 'Fiat deposit'),
('FIAT_WITHDRAWAL', 'Fiat withdrawal'),
('CRYPTO_DEPOSIT', 'Crypto deposit'),
('CRYPTO_WITHDRAWAL', 'Crypto withdrawal'),
('INTERNAL_TRANSFER', 'Transfer between own accounts'),
('USER_TRANSFER', 'Transfer to another user'),
('CONVERSION', 'Currency/asset conversion'),
('TRADE_FILL', 'Order fill / trade'),
('FEE', 'Fee charged'),
('REFUND', 'Refund or reversal'),
('STAKING_REWARD', 'Staking/reward'),
('ADJUSTMENT', 'Manual/system adjustment')
;
*/

CREATE TABLE IF NOT EXISTS transactions (
    tx_key BIGINT AUTO_INCREMENT PRIMARY KEY,
    tx_type_key VARCHAR(30) NOT NULL,
    from_acc_key BIGINT,
    to_acc_key BIGINT,
    amount DECIMAL(36,18) NOT NULL,
    from_currency_code VARCHAR(10) NOT NULL,
    to_currency_code VARCHAR(10) NOT NULL,
    fee_amount DECIMAL(36,18) DEFAULT 0,
    fee_currency VARCHAR(10) DEFAULT NULL,
    exchange_rate DECIMAL(36,18),
    idempotency_key VARCHAR(255) DEFAULT NULL,
    tx_hash VARCHAR(255) DEFAULT NULL,
    chain VARCHAR(50) DEFAULT NULL,
    confirmations INT DEFAULT NULL,
    confirmations_required INT DEFAULT 0,
    status VARCHAR(20) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    reference VARCHAR(255) DEFAULT NULL,
    memo TEXT DEFAULT NULL,
    FOREIGN KEY (from_acc_key) REFERENCES account(acc_key),
    FOREIGN KEY (to_acc_key) REFERENCES account(acc_key),
    FOREIGN KEY (tx_type_key) REFERENCES transaction_type(tx_type_key)
);