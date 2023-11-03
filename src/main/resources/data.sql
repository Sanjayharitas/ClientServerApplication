DROP TABLE user_details IF EXISTS;
CREATE TABLE user_details (
   id INT NOT NULL,
   name VARCHAR(50) NOT NULL,
   hashkey VARCHAR NOT NULL,
   dob VARCHAR(30) NOT NULL
);

INSERT INTO user_details VALUES (1, 'admin', '$2a$10$T9MT3/ObYwLjvovd3BNEYe4IcWVorw02ppn2H/lCYXBCM3j9y3F46', 'April1');
INSERT INTO user_details VALUES (2, 'user1', '$2a$10$T9MT3/ObYwLjvovd3BNEYe4IcWVorw02ppn2H/lCYXBCM3j9y3F46', 'April2');
INSERT INTO user_details VALUES (3, 'user2', '$2a$10$T9MT3/ObYwLjvovd3BNEYe4IcWVorw02ppn2H/lCYXBCM3j9y3F46', 'April3');
INSERT INTO user_details VALUES (4, 'user3', '$2a$10$T9MT3/ObYwLjvovd3BNEYe4IcWVorw02ppn2H/lCYXBCM3j9y3F46', 'April4');

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