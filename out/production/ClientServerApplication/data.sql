DROP TABLE user_details IF EXISTS;
CREATE TABLE user_details (
   id INT NOT NULL,
   name VARCHAR(50) NOT NULL,
   hashkey VARCHAR NOT NULL,
   active BOOLEAN
);

INSERT INTO user_details VALUES (1, 'admin', 'password', TRUE);
INSERT INTO user_details VALUES (2, 'user1', 'password', TRUE);
INSERT INTO user_details VALUES (3, 'user2', 'password', TRUE);
INSERT INTO user_details VALUES (4, 'user3', 'password', TRUE);

DROP TABLE printer_details IF EXISTS;
CREATE TABLE printer_details (
   id INT NOT NULL,
   name VARCHAR(50) NOT NULL,
   amount_of_papers INT,
   ink_available BOOLEAN
);

INSERT INTO printer_details VALUES (1, 'printer1', 500, TRUE);
INSERT INTO printer_details VALUES (2, 'printer2', 500, TRUE);
INSERT INTO printer_details VALUES (3, 'printer3', 500, TRUE);
INSERT INTO printer_details VALUES (4, 'printer4', 500, TRUE);