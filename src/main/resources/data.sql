INSERT INTO authority (name, code, created_by, updated_by) values ('MASTER ADMIN', '0001', 'SYSTEM', 'SYSTEM');
INSERT INTO authority (name, code, created_by, updated_by) values ('ADMIN', '0002', 'SYSTEM', 'SYSTEM');
INSERT INTO authority (name, code, created_by, updated_by) values ('USER', '0003', 'SYSTEM', 'SYSTEM');

INSERT INTO admin (login_id, password, name, phone_number, email, authority_code, created_by, updated_by) values 
('ymkmoon43', '$2a$10$oZPWMiSQF4lduNY/X8q9ZuuE14kJhcfIeER7SR/Ou0iE6W1VUY9WW', '유명기', 
'01029320134', 'ymkmoon43@gmail.com', '0001', 'SYSTEM', 'SYSTEM');

INSERT INTO admin (login_id, password, name, phone_number, email, authority_code, created_by, updated_by) values 
('ymkmoon433', '$2a$10$oZPWMiSQF4lduNY/X8q9ZuuE14kJhcfIeER7SR/Ou0iE6W1VUY9WW', '유명기2', 
'11129320134', 'ymkmoon433@gmail.com', '0002', 'SYSTEM', 'SYSTEM');

INSERT INTO admin (login_id, password, name, phone_number, email, authority_code, created_by, updated_by) values 
('ymkmoon4333', '$2a$10$oZPWMiSQF4lduNY/X8q9ZuuE14kJhcfIeER7SR/Ou0iE6W1VUY9WW', '유명기3', 
'22229320134', 'ymkmoon4333@gmail.com', '0003', 'SYSTEM', 'SYSTEM');
