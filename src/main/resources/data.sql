CREATE USER IF NOT EXISTS write_user PASSWORD 'write_password';
CREATE USER IF NOT EXISTS read_user PASSWORD 'read_password';
GRANT ALL ON SCHEMA PUBLIC TO write_user;
GRANT SELECT ON SCHEMA PUBLIC TO read_user;

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
('admin_uuid1', 'ymkmoon43', '$2a$10$tuLXB3HaKF9B6IkKQLkER.uBZkbP9qkcgIqUpXoXGrYDy1Ac3GiE2', '유명기', 
'01029320134', 'ymkmoon43@gmail.com', 'authority_group_uuid1', 'ACTIVE', 'SYSTEM', 'SYSTEM');

INSERT INTO admin (id, login_id, password, name, phone_number, email, authority_group_id, approval_status, created_by, updated_by) values 
('admin_uuid2', 'ymkmoon1', '$2a$10$tuLXB3HaKF9B6IkKQLkER.uBZkbP9qkcgIqUpXoXGrYDy1Ac3GiE2', '승인대기', 
'11129320134', 'ymkmoon433@gmail.com', 'authority_group_uuid1', 'PENDING', 'SYSTEM', 'SYSTEM');

INSERT INTO admin (id, login_id, password, name, phone_number, email, authority_group_id, approval_status, created_by, updated_by) values 
('admin_uuid3', 'ymkmoon2', '$2a$10$tuLXB3HaKF9B6IkKQLkER.uBZkbP9qkcgIqUpXoXGrYDy1Ac3GiE2', '반려', 
'22229320134', 'ymkmoon4333@gmail.com', 'authority_group_uuid2', 'REJECTED', 'SYSTEM', 'SYSTEM');

INSERT INTO admin (id, login_id, password, name, phone_number, email, authority_group_id, approval_status, created_by, updated_by) values 
('admin_uuid4', 'ymkmoon3', '$2a$10$tuLXB3HaKF9B6IkKQLkER.uBZkbP9qkcgIqUpXoXGrYDy1Ac3GiE2', '일시정지', 
'33329320134', 'ymkmoon43333@gmail.com', 'authority_group_uuid3', 'SUSPENDED', 'SUSPENDED', 'SYSTEM');

INSERT INTO admin (id, login_id, password, name, phone_number, email, authority_group_id, approval_status, created_by, updated_by) values 
('admin_uuid5', 'ymkmoon4', '$2a$10$tuLXB3HaKF9B6IkKQLkER.uBZkbP9qkcgIqUpXoXGrYDy1Ac3GiE2', '탈퇴', 
'44429320134', 'ymkmoon433333@gmail.com', 'authority_group_uuid4', 'WITHDRAWN', 'SUSPENDED', 'SYSTEM');

INSERT INTO admin (id, login_id, password, name, phone_number, email, authority_group_id, approval_status, created_by, updated_by, is_active) values 
('admin_uuid6', 'ymkmoon5', '$2a$10$tuLXB3HaKF9B6IkKQLkER.uBZkbP9qkcgIqUpXoXGrYDy1Ac3GiE2', '비정상적', 
'55529320134', 'ymkmoon4333333@gmail.com', 'authority_group_uuid4', 'ACTIVE', 'SYSTEM', 'SYSTEM', 'F');

INSERT INTO admin (id, login_id, password, name, phone_number, email, authority_group_id, approval_status, created_by, updated_by, is_active) values 
('admin_uuid7', 'ymkmoon6', '$2a$10$tuLXB3HaKF9B6IkKQLkER.uBZkbP9qkcgIqUpXoXGrYDy1Ac3GiE2', '비정상적_권한없', 
'66629320134', 'ymkmoon43333333@gmail.com', null, 'ACTIVE', 'SYSTEM', 'SYSTEM', 'T');

INSERT INTO admin (id, login_id, password, name, phone_number, email, authority_group_id, approval_status, created_by, updated_by, is_active) values 
('admin_uuid8', 'ymkmoon7', '$2a$10$tuLXB3HaKF9B6IkKQLkER.uBZkbP9qkcgIqUpXoXGrYDy1Ac3GiE2', '비정상적_권한없', 
'00629320134', '7@gmail.com', null, 'PENDING', 'SYSTEM', 'SYSTEM', 'T');

INSERT INTO admin (id, login_id, password, name, phone_number, email, authority_group_id, approval_status, created_by, updated_by, is_active) values 
('admin_uuid9', 'ymkmoon8', '$2a$10$tuLXB3HaKF9B6IkKQLkER.uBZkbP9qkcgIqUpXoXGrYDy1Ac3GiE2', '비정상적_권한없', 
'00529320134', '8@gmail.com', null, 'PENDING', 'SYSTEM', 'SYSTEM', 'T');

INSERT INTO admin (id, login_id, password, name, phone_number, email, authority_group_id, approval_status, created_by, updated_by, is_active) values 
('admin_uuid10', 'ymkmoon9', '$2a$10$tuLXB3HaKF9B6IkKQLkER.uBZkbP9qkcgIqUpXoXGrYDy1Ac3GiE2', '비정상적_권한없', 
'00429320134', '9@gmail.com', null, 'PENDING', 'SYSTEM', 'SYSTEM', 'T');

INSERT INTO admin (id, login_id, password, name, phone_number, email, authority_group_id, approval_status, created_by, updated_by, is_active) values 
('admin_uuid11', 'ymkmoon10', '$2a$10$tuLXB3HaKF9B6IkKQLkER.uBZkbP9qkcgIqUpXoXGrYDy1Ac3GiE2', '비정상적_권한없', 
'00329320134', '10@gmail.com', null, 'PENDING', 'SYSTEM', 'SYSTEM', 'T');

INSERT INTO admin (id, login_id, password, name, phone_number, email, authority_group_id, approval_status, created_by, updated_by, is_active) values 
('admin_uuid12', 'ymkmoon11', '$2a$10$tuLXB3HaKF9B6IkKQLkER.uBZkbP9qkcgIqUpXoXGrYDy1Ac3GiE2', '비정상적_권한없', 
'00229320134', '11@gmail.com', null, 'PENDING', 'SYSTEM', 'SYSTEM', 'T');

INSERT INTO admin (id, login_id, password, name, phone_number, email, authority_group_id, approval_status, created_by, updated_by, is_active) values 
('admin_uuid13', 'ymkmoon12', '$2a$10$tuLXB3HaKF9B6IkKQLkER.uBZkbP9qkcgIqUpXoXGrYDy1Ac3GiE2', '비정상적_권한없', 
'00129320134', '12@gmail.com', null, 'PENDING', 'SYSTEM', 'SYSTEM', 'T');
