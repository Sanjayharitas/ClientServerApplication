DROP TABLE user_details IF EXISTS;
CREATE TABLE user_details (
   id INT NOT NULL,
   name VARCHAR(50) NOT NULL,
   hashkey VARCHAR NOT NULL,
   dob VARCHAR(30) NOT NULL
);

INSERT INTO user_details VALUES (1, 'admin', '7b6db39a706d9253d08a2bd1c99e8347f030a17dc9c4a331b129a94772fe6a7ada827a436cbcd91a496bf3d9a6fea78e8bb30025ad31c6e6367f630de7d94d5b', 'April1');
INSERT INTO user_details VALUES (2, 'user1', '34fe5be99abfe20969ec805ce9d36243fcbcd847f9a88b278947d7f2c7994a508e862d37410112146dcc1527be9e3053c2c3c0b15517ceaaff16afd46a2527c5', 'April2');
INSERT INTO user_details VALUES (3, 'user2', 'de8740961a7b6b61fd0d81cebf5f3439b2173c09ab037057ad73fdb4b2ca6336ce5971550dffcad4a11350c69be55e027a7e5f7216e79477b344f57b360ffa1c', 'April3');
INSERT INTO user_details VALUES (4, 'user3', 'eb6ca8f388f2280f9a91488cddec5c4630c6316dcf3ca397b9de7ca04165e8ab444e4d373b0de89b821fdb1c3cf44a1760a1c27982d322822ee796180280167b', 'April4');

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