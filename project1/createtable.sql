CREATE DATABASE IF NOT EXISTS 'moviedb';

DROP TABLE IF EXISTS 'Movies';
CREATE TABLE 'Movies' (
    'id' varchar(10),
    'title' varchar(100) NOT NULL,
    'year' integer NOT NULL,
    'director' varchar(100) NOT NULL,
    PRIMARY KEY ('id')
);

DROP TABLE IF EXISTS 'Stars';
CREATE TABLE 'Stars' (
    'id' varchar(10),
    'name' varchar(100) NOT NULL,
    'birthYear' integer,
    PRIMARY KEY ('id')
);

DROP TABLE IF EXISTS 'Stars_in_movies';
CREATE TABLE 'Stars_in_movies' (
    'starId' varchar(10),
    'movieID' varchar(10),
    FOREIGN KEY ('starId') REFERENCES Stars('id') ON DELETE CASCADE,
    FOREIGN KEY ('movieID') REFERENCES Movies('id') ON DELETE CASCADE
);

DROP TABLE IF EXISTS 'Genres';
CREATE TABLE 'Genres' (
    'id' integer AUTO_INCREMENT,
    'name' varchar(32) NOT NULL,
    PRIMARY KEY ('id')
);

DROP TABLE IF EXISTS 'Genres_in_movies';
CREATE TABLE 'Genres_in_movies' (
    'genreId' integer,
    'movieID' varchar(10),
    FOREIGN KEY ('genreId') REFERENCES Genres('id') ON DELETE CASCADE,
    FOREIGN KEY ('movieID') REFERENCES Movies('id') ON DELETE CASCADE
);


DROP TABLE IF EXISTS 'Creditcards';
CREATE TABLE 'Creditcards' (
    'id' varchar(20),
    'firstName' varchar(50) NOT NULL,
    'lastName' varchar(50) NOT NULL,
    'expiration' date NOT NULL,
    PRIMARY KEY ('id')
);

DROP TABLE IF EXISTS 'Customers';
CREATE TABLE 'Customers' (
    'id' integer AUTO_INCREMENT,
    'firstName' varchar(50),
    'lastName'varchar(50),
    'ccId'varchar(20) AUTO_INCREMENT,
    'address' varchar(200),
    'email' varchar(50),
    'password' varchar(20),
    PRIMARY KEY ('id'),
    FOREIGN KEY ('ccID') REFERENCES Creditcards('id') ON DELETE CASCADE
);

DROP TABLE IF EXISTS 'Sales';
CREATE TABLE 'Sales' (
    'id' integer AUTO_INCREMENT,
    'customerID' integer AUTO_INCREMENT,
    'moveID' varchar(10) AUTO_INCREMENT,
    'saleDate' date NOT NULL,
    PRIMARY KEY ('id'),
    FOREIGN KEY ('customerID') REFERENCES Customers('id') ON DELETE CASCADE,
    FOREIGN KEY ('movieID') REFERENCES Movies('id') ON DELETE CASCADE
);

DROP TABLE IF EXISTS 'Ratings';
CREATE TABLE 'Ratings' (
    'movieID' varchar(10),
    'rating' float NOT NULL,
    'numVotes' integer NOT NULL,
    FOREIGN KEY ('movieID') REFERENCES Movies('id') ON DELETE CASCADE
);
