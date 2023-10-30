DROP TABLE user_details IF EXISTS;
CREATE TABLE user_details (
   id INT NOT NULL,
   name VARCHAR(50) NOT NULL,
   hashkey VARCHAR NOT NULL,
   active BOOLEAN
);

INSERT INTO user_details VALUES (1, 'admin', 'd25f3bda0a134a4458308c015dfd80399570fb0b0f91f0cb77447674682ea73bbb917aa30d10721960569b901343bc277d1c6e1e65cfdc3661e140f11580cc03', TRUE);
INSERT INTO user_details VALUES (2, 'user1', '59223819e40bf9486dd5771a606108a0f275994f584fe925b059596f47a3fa897948eda731dd34047cd234e97d9601689db745fb29beb633910610e9b592bc7f', TRUE);
INSERT INTO user_details VALUES (3, 'user2', '04b5d567289bd3501d3fc06969d44e55708fd2145d8214e724a53e5d833fbdf543be3f4eb74c501145ddb0626b1ec776fef71b07a152d557987a3a7ec9613f87', TRUE);
INSERT INTO user_details VALUES (4, 'user3', 'fbd782dd00ce1be94a2a65e139d368a00ad3f1fdd7f508413102117a761c47c0c374e3b8ef47561f5bc456fc6f0cb6ff61afc1f330a1efe2c3ab78203b047a9b', TRUE);

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