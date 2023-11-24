DROP TABLE IF EXISTS user_roles;
DROP TABLE IF EXISTS actions;
DROP TABLE IF EXISTS access_control_list;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS user_details;
DROP TABLE IF EXISTS printer_details;

CREATE TABLE user_details (
   id INT NOT NULL PRIMARY KEY,
   name VARCHAR(50) NOT NULL,
   hashkey VARCHAR NOT NULL,
   dob VARCHAR(30) NOT NULL
);

INSERT INTO user_details VALUES (1, 'Alice', '$2a$10$T9MT3/ObYwLjvovd3BNEYe4IcWVorw02ppn2H/lCYXBCM3j9y3F46', 'April1');
INSERT INTO user_details VALUES (2, 'Bob', '$2a$10$T9MT3/ObYwLjvovd3BNEYe4IcWVorw02ppn2H/lCYXBCM3j9y3F46', 'April2');
INSERT INTO user_details VALUES (3, 'Cecilia', '$2a$10$T9MT3/ObYwLjvovd3BNEYe4IcWVorw02ppn2H/lCYXBCM3j9y3F46', 'April3');
INSERT INTO user_details VALUES (4, 'David', '$2a$10$T9MT3/ObYwLjvovd3BNEYe4IcWVorw02ppn2H/lCYXBCM3j9y3F46', 'April4');
INSERT INTO user_details VALUES (5, 'Erica', '$2a$10$T9MT3/ObYwLjvovd3BNEYe4IcWVorw02ppn2H/lCYXBCM3j9y3F46', 'April4');
INSERT INTO user_details VALUES (6, 'Fred', '$2a$10$T9MT3/ObYwLjvovd3BNEYe4IcWVorw02ppn2H/lCYXBCM3j9y3F46', 'April4');
INSERT INTO user_details VALUES (7, 'George', '$2a$10$T9MT3/ObYwLjvovd3BNEYe4IcWVorw02ppn2H/lCYXBCM3j9y3F46', 'April4');

CREATE TABLE IF NOT EXISTS roles (
    role_id INT NOT NULL PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL
);
INSERT INTO roles (role_id, role_name) VALUES
    (1, 'admin'),
    (2, 'superuser'),
    (3, 'poweruser'),
    (4, 'user');

CREATE TABLE user_roles (
    user_id INT NOT NULL,
    role_id INT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES user_details(id),
    FOREIGN KEY (role_id) REFERENCES roles(role_id)
);

INSERT INTO user_roles (user_id, role_id) VALUES
    (1, 1),
    (2, 2),
    (3, 3),
    (4, 4),
    (5, 4),
    (6, 4),
    (7, 4);

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

CREATE TABLE IF NOT EXISTS actions (
    action_id INT NOT NULL,
    actions VARCHAR(50) NOT NULL
);

INSERT INTO actions (action_id, actions) VALUES
    (1,'print'),
    (2,'print_queue'),
    (3,'top_queue'),
    (4,'start'),
    (5,'stop'),
    (6,'restart'),
    (7,'status'),
    (8,'read_config'),
    (9,'set_config'),
    (10,'register_user'),
    (11,'delete_user'),
    (12,'role_management'),
    (13,'get_user'),
    (14,'get_users_list'),
    (15,'get_roles_list');



CREATE TABLE access_control_list (
    role_id INT NOT NULL,
    action_id INT NOT NULL,
    PRIMARY KEY (action_id, role_id),
    FOREIGN KEY (role_id) REFERENCES roles(role_id)
);

INSERT INTO access_control_list (role_id, action_id) VALUES
    (1,1),(1,2),(1,3),(1,4),(1,5),(1,6),(1,7),(1,8),(1,9),(1,10),(1,11)
    ,(1,12),(1,13),(1,14),(1,15),(2,4),(2,5),(2,6),(2,7),
    (2,8),(2,9),(3,1),(3,2),(3,3),(3,6),(4,1),(4,2);





