CREATE DATABASE IF NOT EXISTS moviedb;
USE moviedb;

DROP TABLE IF EXISTS ratings;
DROP TABLE IF EXISTS sales;
DROP TABLE IF EXISTS customers;
DROP TABLE IF EXISTS creditcards;
DROP TABLE IF EXISTS genres_in_movies;
DROP TABLE IF EXISTS genres;
DROP TABLE IF EXISTS stars_in_movies;
DROP TABLE IF EXISTS stars;
DROP TABLE IF EXISTS movies;


CREATE TABLE movies (
    id varchar(10) PRIMARY KEY,
    title varchar(100) NOT NULL,
    year integer NOT NULL,
    director varchar(100) NOT NULL
);

CREATE TABLE stars (
    id varchar(10) PRIMARY KEY,
    name varchar(100) NOT NULL,
    birthYear integer
);

CREATE TABLE stars_in_movies (
    starId varchar(10),
    movieID varchar(10),
    FOREIGN KEY (starId) REFERENCES stars(id) ON DELETE CASCADE,
    FOREIGN KEY (movieID) REFERENCES movies(id) ON DELETE CASCADE
);

CREATE TABLE genres (
    id integer AUTO_INCREMENT PRIMARY KEY,
    name varchar(32) NOT NULL
);

CREATE TABLE genres_in_movies (
    genreId integer,
    movieID varchar(10),
    FOREIGN KEY (genreId) REFERENCES genres(id) ON DELETE CASCADE,
    FOREIGN KEY (movieID) REFERENCES movies(id) ON DELETE CASCADE
);

CREATE TABLE creditcards (
    id varchar(20) PRIMARY KEY,
    firstName varchar(50) NOT NULL,
    lastName varchar(50) NOT NULL,
    expiration date NOT NULL
);

CREATE TABLE customers (
    id integer AUTO_INCREMENT PRIMARY KEY,
    firstName varchar(50),
    lastName varchar(50),
    ccId varchar(20),
    address varchar(200),
    email varchar(50),
    password varchar(20),
    FOREIGN KEY (ccID) REFERENCES creditcards(id) ON DELETE CASCADE
);

CREATE TABLE sales (
    id integer AUTO_INCREMENT PRIMARY KEY,
    customerID integer,
    movieID varchar(10),
    saleDate date NOT NULL,
    FOREIGN KEY (customerID) REFERENCES customers(id) ON DELETE CASCADE,
    FOREIGN KEY (movieID) REFERENCES movies(id) ON DELETE CASCADE
);

CREATE TABLE ratings (
    movieID varchar(10),
    rating float NOT NULL,
    numVotes integer NOT NULL,
    FOREIGN KEY (movieID) REFERENCES movies(id) ON DELETE CASCADE
);

CREATE TABLE employees
(
    email    varchar(50) PRIMARY KEY,
    password varchar(128) NOT NULL,
    fullname varchar(100)
);
