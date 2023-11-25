USE moviedb;

DROP PROCEDURE IF EXISTS set_genre;
DELIMITER //
CREATE PROCEDURE set_genre (IN genre VARCHAR(32), IN movieId VARCHAR(10))
    BEGIN
        DECLARE genreId VARCHAR(32);
        IF NOT EXISTS (SELECT * FROM genres g WHERE g.name = genre) THEN INSERT INTO genres (name) VALUES(genre);
            END IF;
        SET genreId = (SELECT id FROM genres WHERE name = genre);
        INSERT INTO genres_in_movies (genreId, movieId) VALUES(genreId, movieId);
    END
    //
DELIMITER ;

DROP PROCEDURE IF EXISTS set_star;
DELIMITER //
CREATE PROCEDURE set_star (IN star_name VARCHAR(100), IN movieId VARCHAR(10))
    BEGIN
		DECLARE starId INTEGER;
		DECLARE starIdString VARCHAR(10);
		IF NOT EXISTS (SELECT * FROM stars WHERE name = star_name) THEN
			SET starId = CAST((SELECT SUBSTRING(MAX(id), 3, 10) FROM stars WHERE id REGEXP '^nm[0-9]') AS DECIMAL) + 1;
			SET starIdString = CONCAT("nm", starId);
			INSERT INTO stars (id, name) VALUES(starIdString, star_name);
		ELSE
			SET starIdString = (SELECT id FROM stars WHERE name = star_name);
		END IF;
		INSERT INTO stars_in_movies (starId, movieID) VALUES(starIdString, movieId);
    END
    //
DELIMITER ;

DROP PROCEDURE IF EXISTS add_movie;
DELIMITER //
CREATE PROCEDURE add_movie (IN title VARCHAR(100), IN director VARCHAR(100), IN year VARCHAR(4),
                            IN genre VARCHAR(32), IN star_name VARCHAR(100))
BEGIN
    DECLARE genreId INTEGER;
    DECLARE movieId INTEGER;
    DECLARE starId INTEGER;
    DECLARE intYear INTEGER;
    DECLARE movieIdString VARCHAR(10);
    DECLARE starIdString VARCHAR(10);
    DECLARE returnString VARCHAR(50);
    SET intYear = CAST(year AS DECIMAL);

    IF NOT EXISTS (SELECT id FROM movies m WHERE m.title = title AND m.year = intYear AND m.director = director) THEN
        SET movieId = CAST((SELECT SUBSTRING(MAX(id), 3, 9) FROM movies WHERE id REGEXP '^tt[0-9]') AS DECIMAL) + 1;
        SET movieIdString = CONCAT("tt0", movieId);
        INSERT INTO movies (id, title, year, director, price) VALUES(movieIdString, title, year, director, ROUND(RAND() * 5 + 5, 2));

        CALL set_genre(genre, movieIdString);
        SET genreId = (SELECT id FROM genres WHERE name = genre);

        CALL set_star(star_name, movieIdString);
        SET starId = CAST((SELECT SUBSTRING(MAX(id), 3, 10) FROM stars WHERE id LIKE 'nm%') AS DECIMAL);
        SET starIdString = CONCAT("nm", starId);

        INSERT INTO ratings (movieId, rating, numVotes) VALUES (movieIdString, 0, 0);

        SET returnString = CONCAT_WS(", ", movieIdString, starIdString, CAST(genreId AS CHAR));

        SELECT (returnString) AS answer;
    ELSE
        SELECT ("no update") AS answer;
    END IF;
END
//
DELIMITER ;

DROP PROCEDURE IF EXISTS get_table_metadata;
CREATE PROCEDURE get_table_metadata ()
    SELECT TABLE_NAME, COLUMN_NAME, DATA_TYPE
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE table_schema = 'moviedb'
    ORDER BY table_name ASC;

DROP PROCEDURE IF EXISTS genre_query;
CREATE PROCEDURE genre_query (IN genreMovieId VARCHAR(10))
    SELECT g.name, g.id
    FROM genres g, genres_in_movies gim
    WHERE g.id = gim.genreID AND gim.movieId = genreMovieId
    ORDER BY g.name
    LIMIT 3;

DROP PROCEDURE IF EXISTS stars_query;
CREATE PROCEDURE stars_query (IN starMovieId VARCHAR(10))
    SELECT S.id, S.name
    FROM stars S, stars_in_movies SiM
    WHERE S.id = SiM.starId AND SiM.movieId = starMovieId
    LIMIT 3;

DROP PROCEDURE IF EXISTS movie_title_id_query;
CREATE PROCEDURE  movie_title_id_query(IN titleName VARCHAR(100))
    SELECT title, id
    FROM movies
    WHERE MATCH (title) AGAINST (titleName IN BOOLEAN MODE)
    LIMIT 10;