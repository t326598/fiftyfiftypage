CREATE TABLE `plan` (
	`no`	BIGINT	NOT NULL PRIMARY KEY AUTO_INCREMENT COMMENT '일정번호',
	`file_no`	BIGINT	NULL,
	`crt`	VARCHAR(100)	NOT NULL	COMMENT '일정 카테고리',
	`title`	VARCHAR(100)	NOT NULL	COMMENT '일정 제목',
	`content`	VARCHAR(1000)	NOT NULL	COMMENT '일정 상세내용',
	`start_at`	TIMESTAMP	NOT NULL	COMMENT '시작일자',
	`end_at`	TIMESTAMP	NULL	COMMENT '종료일자',
	`created_at`	TIMESTAMP	NOT NULL	COMMENT '등록일자',
	`updated_at`	TIMESTAMP	NULL	COMMENT '수정일자'
);

CREATE TABLE `Notice` (
	`no`	BIGINT	NOT NULL PRIMARY KEY AUTO_INCREMENT	COMMENT '공지사항번호',
	`file_no`	BIGINT	NULL	COMMENT '공지사항 이미지',
	`title`	VARCHAR(100)	NOT NULL	COMMENT '제목',
	`content`	VARCHAR(100)	NOT NULL	COMMENT '내용',
	`created_at`	TIMESTAMP	NOT NULL	COMMENT '등록일자',
	`updated_at`	TIMESTAMP	NOT NULL	COMMENT '수정일자'
);


CREATE TABLE fifty_fifty_music_char (
    no INT PRIMARY KEY COMMENT '차트 번호 (1~10 고정)',
    video_id VARCHAR(50) NOT NULL COMMENT '유튜브 영상 ID',
    title VARCHAR(255) NOT NULL COMMENT '영상 제목',
    thumbnail_url VARCHAR(255) NOT NULL COMMENT '썸네일 이미지 URL',
    video_url VARCHAR(255) NOT NULL COMMENT '유튜브 영상 URL',
    view_count BIGINT NOT NULL COMMENT '현재 조회수',
    published_at DATETIME NOT NULL COMMENT '영상 게시일',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '마지막 업데이트 시각'
);

CREATE TABLE `files` (
	`no`	INT	NOT NULL PRIMARY KEY AUTO_INCREMENT	COMMENT '사진번호',
	`crt`	VARCHAR(100)	NULL	COMMENT '멤버 카테고리',
	`path`	VARCHAR(100)	NOT NULL	COMMENT '파일경로',
	`name`	VARCHAR(100)	NOT NULL	COMMENT '파일이름',
	`size`	INT	NOT NULL	COMMENT '파일용량',
	`true_day`	VARCHAR(100)	NOT NULL	COMMENT '사진 찍힌일자',
	`created_at`	TIMESTAMP	NOT NULL	COMMENT '등록일자'
);

CREATE TABLE `Profile` (
	`no`	INT	NOT NULL PRIMARY KEY AUTO_INCREMENT	COMMENT '프로필번호',
	`file_no`	BIGINT NULL	COMMENT '프로필 파일',
	`content`	VARCHAR(1000)	NOT NULL	COMMENT '프로필 내용',
  `sub_content` VARCHAR(255) NOT NULL COMMENT'프로필 서브내용',
	`title`	VARCHAR(100)	NOT NULL	COMMENT '프로필 제목'
);

DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (  
    no          BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT "PK",
    id          VARCHAR(100) NOT NULL COMMENT "UK",
    username    VARCHAR(100) NOT NULL COMMENT "아이디",
    password    VARCHAR(100) NOT NULL COMMENT "비밀번호",
    name        VARCHAR(100) NULL COMMENT "이름",
    email       VARCHAR(100) NULL COMMENT "이메일",
    created_at  TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT "등록일자",
    updated_at  TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP COMMENT "수정일자",
    enabled     BOOLEAN NULL DEFAULT TRUE COMMENT "활성화여부"
) COMMENT '회원';

CREATE TABLE `user_auth` (
    no          BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT "PK",
    user_no     BIGINT NOT NULL COMMENT "아이디",
    auth        VARCHAR(100) NOT NULL COMMENT "권한"
) COMMENT "회원권한";


CREATE TABLE monthly_background (
  no INT AUTO_INCREMENT PRIMARY KEY,
  month INT NOT NULL UNIQUE, -- 1~12월
  file_no BIGINT NOT NULL
);

DROP TABLE IF EXISTS `plan`;
DROP TABLE IF EXISTS `files`;
DROP TABLE IF EXISTS `Profile`;
DROP TABLE IF EXISTS `fifty_fifty_music_char`;
DROP TABLE IF EXISTS `Notice`;
DROP TABLE IF EXISTS `monthly_background`;
  SELECT m.*, f.name
    FROM monthly_background m JOIN files f ON m.file_no = f.no

INSERT INTO plan (file_no, crt, title, content, start_at, end_at, created_at)
VALUES
(NULL, '1', 'BTS 콘서트', '서울 고척돔에서 열리는 콘서트', '2025-05-22 19:00:00', '2025-05-22 21:30:00', NOW()),

(NULL, '2', '아이유 팬미팅', '팬들과 함께하는 소규모 팬미팅', '2025-05-25 14:00:00', '2025-05-25 16:00:00', NOW()),

(NULL, '3', '신곡 홍보 인터뷰', '라디오 출연 및 인터뷰', '2025-05-23 10:00:00', '2025-05-23 11:00:00', NOW()),

(NULL, '1', 'New Jeans 콘서트', '올림픽공원 KSPO 돔', '2025-05-28 18:00:00', '2025-05-28 20:30:00', NOW()),

(NULL, '2', '예능 방송 출연', '유퀴즈 온 더 블럭 출연', '2025-05-29 17:00:00', '2025-05-29 18:30:00', NOW());


INSERT INTO notice (title, content, created_at)
VALUES 
  ('서비스 점검 안내', '시스템 점검으로 인해 5월 25일(토) 00:00 ~ 04:00 동안 서비스 이용이 제한됩니다.', NOW()),
  ('신규 기능 출시', '새로운 일정 공유 기능이 추가되었습니다. 자세한 내용은 공지사항을 참고해주세요.', NOW()),
  ('이용약관 변경 안내', '2025년 6월 1일부터 새로운 이용약관이 적용됩니다. 변경된 내용은 홈페이지 하단에서 확인 가능합니다.', NOW());




ALTER TABLE fifty_fifty_music_char
ADD COLUMN chart_date DATE GENERATED ALWAYS AS (DATE(`date`)) STORED;

ALTER TABLE fifty_fifty_music_char
ADD UNIQUE KEY uq_platform_song_date (platform, song_title, DATE(date));


  SELECT p.*, f.name
        FROM Profile p JOIN  Files f ON p.file_no = f.no
        WHERE f.crt = '7'