CREATE TABLE onlineshop.roles (name VARCHAR(20) PRIMARY KEY);
ALTER TABLE onlineshop.users ADD role_name VARCHAR(20);
ALTER TABLE onlineshop.users ADD CONSTRAINT userrole_fk FOREIGN KEY (role_name)
	REFERENCES onlineshop.roles (name);
INSERT INTO onlineshop.roles(name) VALUES('ADMIN'), ('GUEST');