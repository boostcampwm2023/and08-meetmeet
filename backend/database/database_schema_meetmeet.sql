-- 없을경우
CREATE DATABASE IF NOT EXISTS meetmeet ;

use meetmeet;

-- User 테이블 생성 카카오인지 아닌지 하는 테이블을 새로 놔야할듯?
CREATE TABLE user (
    id        INT AUTO_INCREMENT PRIMARY KEY,
    email     VARCHAR(255) unique not null,
    password  VARCHAR(255) not null,
    nickname  VARCHAR(64) unique not null,
    profile_id INT,
    oauth_provider_id INT,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP not null ,
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deletedAt TIMESTAMP null default NULL
);

CREATE TABLE oauth_provider (
    id INT AUTO_INCREMENT PRIMARY KEY,
    display_name varchar(16) not null,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP not null ,
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Calendar 테이블 생성, 필요없는 테이블 일 수도 있다.
CREATE TABLE calendar (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    group_id INT,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP not null ,
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deletedAt TIMESTAMP null default NULL
);

-- Event 테이블 생성
CREATE TABLE event (
    id INT AUTO_INCREMENT PRIMARY KEY,
    calendar_id INT not null comment '삭제할 수 도 있는 테이블',
    title VARCHAR(64) not null,
    startDate TIMESTAMP not null,
    endDate TIMESTAMP default CURRENT_TIMESTAMP not null,
    isJoinable TINYINT default 1,
    announcement VARCHAR(255),
    repeatPolicy_id INT,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP not null ,
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deletedAt TIMESTAMP null default NULL
);

-- EventMember 테이블 생성
CREATE TABLE event_member (
    id INT AUTO_INCREMENT PRIMARY KEY,
    event_id INT not null ,
    user_id INT not null ,
    detail_id INT not null ,
    authority_id INT not null ,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP not null,
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deletedAt TIMESTAMP null default NULL
);

-- Detail 테이블 생성
CREATE TABLE detail (
    id INT AUTO_INCREMENT PRIMARY KEY,
    isVisible TINYINT default 1,
    memo VARCHAR(64),
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP not null ,
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deletedAt TIMESTAMP null default NULL
);

-- Authority 테이블 생성
CREATE TABLE authority (
    id INT AUTO_INCREMENT PRIMARY KEY,
    display_name VARCHAR(255) not null comment 'admin, owner, member app Enum 구현',
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP not null ,
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deletedAt TIMESTAMP null default NULL
);

-- Feed 테이블 생성
CREATE TABLE feed (
    id INT AUTO_INCREMENT PRIMARY KEY,
    memo VARCHAR(64),
    author INT not null ,
    event_id INT not null ,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP not null ,
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deletedAt TIMESTAMP null default NULL
);

-- Comment 테이블 생성
CREATE TABLE comment (
    id INT AUTO_INCREMENT PRIMARY KEY,
    feed_id INT not null ,
    author INT not null ,
    memo VARCHAR(64) not null comment '메모가 없으면 댓글을 달 수 없는 것으로',
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP not null ,
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deletedAt TIMESTAMP null default NULL
);

-- Content 테이블 생성
CREATE TABLE content (
    id INT AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(8) not null comment 'video, img',
    mime_type VARCHAR(16) not null comment 'image/png, image/jpeg',
    thumbnail varchar(64),
    path VARCHAR(64) not null,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP not null ,
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deletedAt TIMESTAMP null default NULL
);

-- FeedContent 테이블 생성
CREATE TABLE feed_content (
    feed_id INT not null ,
    content_id INT not null,
    primary key (feed_id, content_id)
);

-- Invite 테이블 생성
CREATE TABLE invite (
    id INT AUTO_INCREMENT PRIMARY KEY,
    invite_type_id INT not null ,
    sender INT not null ,
    receiver INT not null ,
    event_id INT not null ,
    reqTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP not null comment 'createdAt',
    resTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment 'updatedAt',
    deletedAt TIMESTAMP null default NULL
);

-- Invite_type 테이블 생성
CREATE TABLE invite_type (
    id INT AUTO_INCREMENT PRIMARY KEY,
    display_name VARCHAR(16) not null comment 'event group',
    request_type VARCHAR(16) not null comment 'invite join',
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP not null ,
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deletedAt TIMESTAMP null default NULL
);

-- RepeatPolicy 테이블 생성
CREATE TABLE repeat_policy (
    id INT AUTO_INCREMENT PRIMARY KEY,
    startDate TIMESTAMP not null ,
    endDate TIMESTAMP DEFAULT '2038-01-18 00:00:00' not null,
    repeatDay INT,
    repeatWeek INT,
    repeatMonth INT,
    repeatYear INT,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP not null ,
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deletedAt TIMESTAMP null default NULL
);

-- Follow 테이블 생성
CREATE TABLE follow (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT not null ,
    follower_id INT not null ,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP not null ,
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deletedAt TIMESTAMP null default NULL
);

-- Group 테이블 생성(Group가 예약어라... 어떻게 할지)
CREATE TABLE `group` (
    id INT AUTO_INCREMENT PRIMARY KEY,
    display_name VARCHAR(255) not null ,
    profile INT,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP not null ,
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deletedAt TIMESTAMP null default NULL
);

-- UserGroup 테이블 생성
CREATE TABLE user_group (
    user_id INT not null comment '',
    group_id INT not null,
    authority_id INT not null,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP not null ,
    updatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deletedAt TIMESTAMP null default NULL,
    primary key (user_id, group_id) comment '속도를 고려하여 조합키로 primary 생성'
);


