INSERT INTO authority_level (level_code, description, created_by, updated_by) VALUES ('SUPER_ADMIN', '최고 관리자', 'SYSTEM', 'SYSTEM');
INSERT INTO authority_level (level_code, description, created_by, updated_by) VALUES ('MID_ADMIN', '중간 관리자', 'SYSTEM', 'SYSTEM');
INSERT INTO authority_level (level_code, description, created_by, updated_by) VALUES ('USER', '일반 사용자', 'SYSTEM', 'SYSTEM');


INSERT INTO authority_group (id, level_code, name, description, created_by, updated_by) VALUES ('authority_group_uuid1', 'SUPER_ADMIN', '최고관리자 그룹', '모든 메뉴 접근 가능', 'SYSTEM', 'SYSTEM');
INSERT INTO authority_group (id, level_code, name, description, created_by, updated_by) VALUES ('authority_group_uuid2', 'MID_ADMIN', '중간관리자A', '메뉴1, 메뉴2 접근 가능', 'SYSTEM', 'SYSTEM');
INSERT INTO authority_group (id, level_code, name, description, created_by, updated_by) VALUES ('authority_group_uuid3', 'MID_ADMIN', '중간관리자B', '메뉴3, 메뉴4 접근 가능', 'SYSTEM', 'SYSTEM');
INSERT INTO authority_group (id, level_code, name, description, created_by, updated_by) VALUES ('authority_group_uuid4', 'USER', '일반사용자 그룹', '기본 메뉴만 접근 가능', 'SYSTEM', 'SYSTEM');

INSERT INTO menu (id, menu_name, path, sort_order, created_by, updated_by) VALUES ('menu_uuid1', '대시보드', '/dashboard', 1, 'SYSTEM', 'SYSTEM');
INSERT INTO menu (id, menu_name, path, sort_order, created_by, updated_by) VALUES ('menu_uuid2', '회원관리', '/members', 2, 'SYSTEM', 'SYSTEM');
INSERT INTO menu (id, menu_name, path, sort_order, created_by, updated_by) VALUES ('menu_uuid3', '권한관리', '/authorities', 3, 'SYSTEM', 'SYSTEM');
INSERT INTO menu (id, menu_name, path, sort_order, created_by, updated_by) VALUES ('menu_uuid4', '메뉴관리', '/menus', 4, 'SYSTEM', 'SYSTEM');


-- 최고관리자: 모든 메뉴
INSERT INTO authority_group_menu (id, group_id, menu_id, created_by, updated_by) VALUES ('authority_group_menu_uuid1', 'authority_group_uuid1', 'menu_uuid1', 'SYSTEM', 'SYSTEM');
INSERT INTO authority_group_menu (id, group_id, menu_id, created_by, updated_by) VALUES ('authority_group_menu_uuid2', 'authority_group_uuid1', 'menu_uuid2', 'SYSTEM', 'SYSTEM');
INSERT INTO authority_group_menu (id, group_id, menu_id, created_by, updated_by) VALUES ('authority_group_menu_uuid3', 'authority_group_uuid1', 'menu_uuid3', 'SYSTEM', 'SYSTEM');
INSERT INTO authority_group_menu (id, group_id, menu_id, created_by, updated_by) VALUES ('authority_group_menu_uuid4', 'authority_group_uuid1', 'menu_uuid4', 'SYSTEM', 'SYSTEM');

-- 중간관리자A: 대시보드 + 회원관리
INSERT INTO authority_group_menu (id, group_id, menu_id, created_by, updated_by) VALUES ('authority_group_menu_uuid5', 'authority_group_uuid2', 'menu_uuid1', 'SYSTEM', 'SYSTEM');
INSERT INTO authority_group_menu (id, group_id, menu_id, created_by, updated_by) VALUES ('authority_group_menu_uuid6', 'authority_group_uuid2', 'menu_uuid2', 'SYSTEM', 'SYSTEM');

-- 중간관리자B: 권한관리 + 메뉴관리
INSERT INTO authority_group_menu (id, group_id, menu_id, created_by, updated_by) VALUES ('authority_group_menu_uuid7', 'authority_group_uuid3', 'menu_uuid3', 'SYSTEM', 'SYSTEM');
INSERT INTO authority_group_menu (id, group_id, menu_id, created_by, updated_by) VALUES ('authority_group_menu_uuid8', 'authority_group_uuid3', 'menu_uuid4', 'SYSTEM', 'SYSTEM');

-- 일반사용자: 대시보드만
INSERT INTO authority_group_menu (id, group_id, menu_id, created_by, updated_by) VALUES ('authority_group_menu_uuid9', 'authority_group_uuid4', 'menu_uuid1', 'SYSTEM', 'SYSTEM');


INSERT INTO admin (id, login_id, password, name, phone_number, email, authority_group_id, approval_status, created_by, updated_by) values 
('admin_uuid1', 'ymkmoon43', '$2a$10$oZPWMiSQF4lduNY/X8q9ZuuE14kJhcfIeER7SR/Ou0iE6W1VUY9WW', '유명기', 
'01029320134', 'ymkmoon43@gmail.com', 'authority_group_uuid1', 'ACTIVE', 'SYSTEM', 'SYSTEM');

INSERT INTO admin (id, login_id, password, name, phone_number, email, authority_group_id, approval_status, created_by, updated_by) values 
('admin_uuid2', 'ymkmoon433', '$2a$10$oZPWMiSQF4lduNY/X8q9ZuuE14kJhcfIeER7SR/Ou0iE6W1VUY9WW', '유명기2', 
'11129320134', 'ymkmoon433@gmail.com', 'authority_group_uuid2', 'PENDING', 'SYSTEM', 'SYSTEM');

INSERT INTO admin (id, login_id, password, name, phone_number, email, authority_group_id, approval_status, created_by, updated_by) values 
('admin_uuid3', 'ymkmoon4333', '$2a$10$oZPWMiSQF4lduNY/X8q9ZuuE14kJhcfIeER7SR/Ou0iE6W1VUY9WW', '유명기3', 
'22229320134', 'ymkmoon4333@gmail.com', 'authority_group_uuid3', 'REJECTED', 'SYSTEM', 'SYSTEM');

INSERT INTO admin (id, login_id, password, name, phone_number, email, authority_group_id, approval_status, created_by, updated_by) values 
('admin_uuid4', 'ymkmoon43333', '$2a$10$oZPWMiSQF4lduNY/X8q9ZuuE14kJhcfIeER7SR/Ou0iE6W1VUY9WW', '유명기4', 
'33329320134', 'ymkmoon43333@gmail.com', 'authority_group_uuid3', 'SUSPENDED', 'SUSPENDED', 'SYSTEM');

INSERT INTO admin (id, login_id, password, name, phone_number, email, authority_group_id, approval_status, created_by, updated_by) values 
('admin_uuid6', 'ymkmoon433333', '$2a$10$oZPWMiSQF4lduNY/X8q9ZuuE14kJhcfIeER7SR/Ou0iE6W1VUY9WW', '유명기4', 
'44429320134', 'ymkmoon433333@gmail.com', 'authority_group_uuid3', 'WITHDRAWN', 'SUSPENDED', 'SYSTEM');
