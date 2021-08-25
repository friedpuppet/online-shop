ALTER TABLE onlineshop.tokens ADD COLUMN valid_until TIMESTAMP NOT NULL;
ALTER TABLE onlineshop.users ALTER COLUMN password TYPE VARCHAR(40);
ALTER TABLE onlineshop.users RENAME COLUMN password TO password_hash;
ALTER TABLE onlineshop.users ADD COLUMN salt VARCHAR(36) NOT NULL;
