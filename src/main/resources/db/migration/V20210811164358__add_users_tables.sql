create table onlineshop.users (id SERIAL PRIMARY KEY, name VARCHAR(200) NOT NULL, password VARCHAR(20) NOT NULL);
create table onlineshop.tokens (user_id INT NOT NULL, token VARCHAR(200) NOT NULL);
ALTER TABLE onlineshop.tokens ADD PRIMARY KEY (user_id, token)