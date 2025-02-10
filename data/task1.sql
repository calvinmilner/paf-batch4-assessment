-- Write your Task 1 answers in this file

create database bedandbreakfast;

use bedandbreakfast;

create table users(
    email varchar(128),
    name varchar(128),
    constraint pk_email_id primary key (email)
); 

create table bookings(
    booking_id char(8),
    listing_id varchar(20),
    duration int,
    email varchar(128),
    constraint pk_booking_id primary key (booking_id),
    constraint fk_email_id foreign key (email) references users(email)
);

create table reviews(
    id int auto_increment,
    date date,
    listing_id varchar(20),
    reviewer_name varchar(64),
    comments text,
    constraint pk_reviews_id primary key (id)
);

LOAD DATA INFILE 'C:/ProgramData/MySQL/MySQL Server 8.4/Uploads/users.csv' INTO TABLE users
FIELDS TERMINATED BY ',' ENCLOSED BY '"'
LINES TERMINATED BY '\r\n'
IGNORE 1 ROWS;