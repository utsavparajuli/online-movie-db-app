CREATE DATABASE IF NOT EXISTS moviedb;
USE moviedb;

DROP TABLE IF EXISTS  Ratings;
DROP TABLE IF EXISTS Sales;
DROP TABLE IF EXISTS Customers;
DROP TABLE IF EXISTS Creditcards;
DROP TABLE IF EXISTS Genres_in_movies;
DROP TABLE IF EXISTS Genres;
DROP TABLE IF EXISTS Stars_in_movies;
DROP TABLE IF EXISTS Stars;
DROP TABLE IF EXISTS Movies;


CREATE TABLE Movies (
    id varchar(10) PRIMARY KEY,
    title varchar(100) NOT NULL,
    year integer NOT NULL,
    director varchar(100) NOT NULL
);

CREATE TABLE Stars (
    id varchar(10) PRIMARY KEY,
    name varchar(100) NOT NULL,
    birthYear integer
);

CREATE TABLE Stars_in_movies (
    starId varchar(10),
    movieID varchar(10),
    FOREIGN KEY (starId) REFERENCES Stars(id) ON DELETE CASCADE,
    FOREIGN KEY (movieID) REFERENCES Movies(id) ON DELETE CASCADE
);

CREATE TABLE Genres (
    id integer AUTO_INCREMENT PRIMARY KEY,
    name varchar(32) NOT NULL
);

CREATE TABLE Genres_in_movies (
    genreId integer,
    movieID varchar(10),
    FOREIGN KEY (genreId) REFERENCES Genres(id) ON DELETE CASCADE,
    FOREIGN KEY (movieID) REFERENCES Movies(id) ON DELETE CASCADE
);

CREATE TABLE Creditcards (
    id varchar(20) PRIMARY KEY,
    firstName varchar(50) NOT NULL,
    lastName varchar(50) NOT NULL,
    expiration date NOT NULL
);

CREATE TABLE Customers (
    id integer AUTO_INCREMENT PRIMARY KEY,
    firstName varchar(50),
    lastName varchar(50),
    ccId varchar(20),
    address varchar(200),
    email varchar(50),
    password varchar(20),
    FOREIGN KEY (ccID) REFERENCES Creditcards(id) ON DELETE CASCADE
);

CREATE TABLE Sales (
    id integer AUTO_INCREMENT PRIMARY KEY,
    customerID integer,
    movieID varchar(10),
    saleDate date NOT NULL,
    FOREIGN KEY (customerID) REFERENCES Customers(id) ON DELETE CASCADE,
    FOREIGN KEY (movieID) REFERENCES Movies(id) ON DELETE CASCADE
);

CREATE TABLE Ratings (
    movieID varchar(10),
    rating float NOT NULL,
    numVotes integer NOT NULL,
    FOREIGN KEY (movieID) REFERENCES Movies(id) ON DELETE CASCADE
);
