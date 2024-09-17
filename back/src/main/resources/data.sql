INSERT INTO TEACHERS (first_name, last_name)
VALUES ('Margot', 'DELAHAYE'),
       ('Dhalsim', 'INDIA');

INSERT INTO USERS (first_name, last_name, admin, email, password)
VALUES ('Admin', 'Admin', true, 'yoga@studio.com', '$2a$10$.Hsa/ZjUVaHqi0tp9xieMeewrnZxrZ5pQRzddUXE/WjDu2ZThe6Iq');

INSERT INTO USERS (first_name, last_name, admin, email, password)
VALUES ('Alice', 'WONDERLAND', false, 'alice@test.com', '$2a$10$8nOCM7k7wogqHF85.GSxtugT9dJ8wY9EymTp4.g1k37rd2NcK5Ib.');

INSERT INTO USERS (first_name, last_name, admin, email, password)
VALUES ('Bob', 'LE BRICOLEUR', false, 'bob@test.com', '$2a$10$8nOCM7k7wogqHF85.GSxtugT9dJ8wY9EymTp4.g1k37rd2NcK5Ib.');

INSERT INTO SESSIONS (name, description, date, teacher_id)
VALUES ("Ceci n'est pas une location", 'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed non risus. Suspendisse lectus tortor, dignissim sit amet, adipiscing nec, ultricies sed, dolor. Cras elementum ultrices diam. Maecenas ligula massa, varius a, semper congue, euismod non, mi. Proin porttitor, orci nec nonummy molestie, enim est eleifend mi, non fermentum diam nisl sit amet erat.', DATE('2024-09-03'), 1);

INSERT INTO SESSIONS (name, description, date, teacher_id)
VALUES ("Session n°2", 'Duis semper. Duis arcu massa, scelerisque vitae, consequat in, pretium a, enim. Pellentesque congue.', DATE('2024-10-04'), 1);