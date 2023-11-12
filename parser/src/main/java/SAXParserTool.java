import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Year;
import java.util.*;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import dto.Actor;
import dto.Film;
import dto.Genre;
import utilities.Utility;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.DefaultHandler;

public class SAXParserTool extends DefaultHandler {

    private final Map<String, Film> filmMap;
    private final Map<String, Actor> actorMap;
    private final Map<Film, List<Actor>> stars_in_movies_map;
    private final Map<String, Integer> genreIdMap;
    private final List<Genre> newGenresList;

    private String tempVal;
    private Film tempFilmObj;
    private Genre tempGenreObj;
    private Actor tempActorObj;
    private List<Actor> tempActorListForMovie;
    private int lastGenreId = 0;

    private PrintWriter outputWriter;
    private PrintWriter movieInconsistencyReport;
    private PrintWriter actorInconsistencyReport;

    private Connection conn;
    private SAXParser sp;

    private int incMoviesCount;
    private int incActorsCount;

    public SAXParserTool() {
        filmMap = new HashMap<>();
        actorMap = new HashMap<>();
        stars_in_movies_map = new HashMap<>();
        genreIdMap = new HashMap<>();
        newGenresList = new ArrayList<>();

        SAXParserFactory spf = SAXParserFactory.newInstance();

        try {
            sp = spf.newSAXParser();

            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:" + Parameters.dbtype + ":///" +
                            Parameters.dbname + "?allowPublicKeyRetrieval=true&autoReconnect=true&useSSL=false",
                    Parameters.username, Parameters.password);
            loadExistingGenresFromDatabase(conn);

            movieInconsistencyReport = new PrintWriter("movie_inconsistency.txt", StandardCharsets.UTF_8);
            actorInconsistencyReport = new PrintWriter("actor_inconsistency.txt", StandardCharsets.UTF_8);
        } catch (ParserConfigurationException | SAXException | IOException | ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void runParser() {
        parseMovies();
        parseActors();
        parseMovieCast();
        printMovieData();
        printActorData();
        printCastInfo();
        updateDatabase();
        movieInconsistencyReport.close();
        actorInconsistencyReport.close();
        System.out.println("Inconsistent movies = " + incMoviesCount);
        System.out.println("Inconsistent actors = " + incActorsCount);
    }

    private void updateDatabase() {
        try {
            insetFilmsToDb(conn);
            insertActorsToDb(conn);
            insetStarsInMoviesToDb(conn);
            insertNewGenresToDb(conn);
            insertGenresInMoviesToDb(conn);
            insertRatingsInMoviesToDb(conn);
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadExistingGenresFromDatabase(Connection c){
        System.out.println("Reading existing genres from table");
        try {
            String query = "SELECT * FROM genres;";
            PreparedStatement getGenresStatement = c.prepareStatement(query);
            var rs = getGenresStatement.executeQuery();
            while (rs.next()) {
                genreIdMap.put(rs.getString("name"), Integer.parseInt(rs.getString("id")));
                if(Integer.parseInt(rs.getString("id")) > lastGenreId) {
                    lastGenreId = Integer.parseInt(rs.getString("id"));
                }
            }
            getGenresStatement.close();
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insetFilmsToDb(Connection c) {
        int batchSize = 0;
        String filmEntry = "INSERT INTO movies VALUES (?, ?, ?, ?, ?);";

        try (PreparedStatement filmInsertStatement = c.prepareStatement(filmEntry)){
            for (Map.Entry<String, Film> entry : filmMap.entrySet()) {
                Film filmValue = entry.getValue();
                if(batchSize == 500) {
                    filmInsertStatement.executeBatch();
                    batchSize = 0;
                    filmInsertStatement.clearBatch();
                }
                filmInsertStatement.setString(1, filmValue.getId());
                filmInsertStatement.setString(2, filmValue.getTitle());
                filmInsertStatement.setInt(3, filmValue.getYear().getValue());
                filmInsertStatement.setString(4, filmValue.getDirector());
                filmInsertStatement.setFloat(5, Utility.getPriceForMovies());
                filmInsertStatement.addBatch();
                batchSize++;
            }
            filmInsertStatement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Data inserted into movies table");
    }

    private void insertActorsToDb(Connection c) {
        int batchSize = 0;
        String actorEntry = "INSERT INTO stars VALUES (?, ?, ?);";

        try (PreparedStatement insertStatement = c.prepareStatement(actorEntry)){
            for (Map.Entry<String, Actor> entry : actorMap.entrySet()) {
                Actor actorValue = entry.getValue();
                if(batchSize == 500) {
                    insertStatement.executeBatch();
                    batchSize = 0;
                    insertStatement.clearBatch();
                }
                insertStatement.setString(1, actorValue.getId().toString().substring(0,8));
                insertStatement.setString(2, actorValue.getName());
                insertStatement.setInt(3, actorValue.getBirthYear().getValue());
                insertStatement.addBatch();
                batchSize++;
            }
            insertStatement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Data inserted into actors table");
    }

    private void insetStarsInMoviesToDb(Connection c) {
        int batchSize = 0;
        String star_in_movie_query = "INSERT INTO stars_in_movies VALUES (?, ?);";

        try (PreparedStatement starMovieInsert = c.prepareStatement(star_in_movie_query)){
            for (Map.Entry<Film, List<Actor>> entry : stars_in_movies_map.entrySet()) {
                for (Actor a : entry.getValue()) {
                    if(batchSize == 500) {
                        starMovieInsert.executeBatch();
                        batchSize = 0;
                        starMovieInsert.clearBatch();
                    }
                    starMovieInsert.setString(1, a.getId().toString().substring(0,8));
                    starMovieInsert.setString(2, entry.getKey().getId());
                    starMovieInsert.addBatch();
                    batchSize++;
                }
            }
            starMovieInsert.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Data inserted into stars_in_movies table");
    }

    private void insertNewGenresToDb(Connection c) {
        int batchSize = 0;
        String genreEntry = "INSERT INTO genres VALUES (?, ?);";

        try (PreparedStatement insertStatement = c.prepareStatement(genreEntry)){
            for (Genre g: newGenresList) {
                if(batchSize == 500) {
                    insertStatement.executeBatch();
                    batchSize = 0;
                    insertStatement.clearBatch();
                }
                insertStatement.setInt(1, g.getId());
                insertStatement.setString(2, g.getName());
                insertStatement.addBatch();
                batchSize++;
            }
            insertStatement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Data inserted into genres table");
    }

    private void insertGenresInMoviesToDb(Connection c) {
        int batchSize = 0;
        String genre_in_movie_query = "INSERT INTO genres_in_movies VALUES (?, ?);";

        try (PreparedStatement genreMovieInsertStm = c.prepareStatement(genre_in_movie_query)){
            for (Map.Entry<String, Film> entry : filmMap.entrySet()) {
                for (Genre g : entry.getValue().getGenres()) {
                    if(batchSize == 500) {
                        genreMovieInsertStm.executeBatch();
                        batchSize = 0;
                        genreMovieInsertStm.clearBatch();
                    }
                    genreMovieInsertStm.setInt(1, g.getId());
                    genreMovieInsertStm.setString(2, entry.getValue().getId());
                    genreMovieInsertStm.addBatch();
                    batchSize++;
                }
            }
            genreMovieInsertStm.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Data inserted into genres_in_movies table");
    }

    private void insertRatingsInMoviesToDb(Connection c) {
        int batchSize = 0;
        String ratings_in_movie_query = "INSERT INTO ratings VALUES (?, ?, ?);";

        try (PreparedStatement ratingMovieInsertStm = c.prepareStatement(ratings_in_movie_query)){
            for (Map.Entry<String, Film> entry : filmMap.entrySet()) {
                if(batchSize == 500) {
                    ratingMovieInsertStm.executeBatch();
                    batchSize = 0;
                    ratingMovieInsertStm.clearBatch();
                }
                ratingMovieInsertStm.setString(1, entry.getValue().getId());
                ratingMovieInsertStm.setFloat(2, 0.0F);
                ratingMovieInsertStm.setInt(3, 0);
                ratingMovieInsertStm.addBatch();
                batchSize++;
            }
            ratingMovieInsertStm.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Data inserted into ratings table");
    }

    private void parseMovies() {
        try {
            sp.reset();
            sp.parse("stanford-movies/mains243.xml", this);
        } catch (SAXException | IOException se) {
            se.printStackTrace();
        }
        System.out.println("mains243.xml parsed");
    }

    private void parseActors() {
        try {
            sp.reset();
            sp.parse("stanford-movies/actors63.xml", this);
        } catch (SAXException | IOException se) {
            se.printStackTrace();
        }
        System.out.println("actors63.xml parsed");
    }

    private void parseMovieCast() {
        try {
            sp.reset();
            sp.parse("stanford-movies/casts124.xml", this);
        } catch (SAXException | IOException se) {
            se.printStackTrace();
        }
        System.out.println("casts124.xml parsed");
    }

    private void printMovieData() {
        try {
            outputWriter = new PrintWriter("movies.txt", StandardCharsets.UTF_8);
            outputWriter.println("No. of Films read '" + filmMap.size() + "'.");
            System.out.println("Movies stored = " + filmMap.size());
            for (Film film : filmMap.values()) {
                outputWriter.println(film.toString());
            }
            outputWriter.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void printActorData() {
        try {
            outputWriter = new PrintWriter("actors.txt", StandardCharsets.UTF_8);
            outputWriter.println("No of Actors read '" + actorMap.size() + "'.");
            System.out.println("Actors stored = " + actorMap.size());

            for (Actor actor : actorMap.values()) {
                outputWriter.println(actor.toString());
            }
            outputWriter.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void printCastInfo() {
        try {
            outputWriter = new PrintWriter("stars_in_movies.txt", StandardCharsets.UTF_8);
            outputWriter.println("No of Movies-Actors map read '" + stars_in_movies_map.size() + "'.");
            System.out.println("Movie-Actors stored = " + stars_in_movies_map.size());

            for (Map.Entry<Film, List<Actor>> entry : stars_in_movies_map.entrySet()) {
                outputWriter.println(entry.getKey() + "\n" + entry.getValue() + "\n");
            }
            outputWriter.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Event Handler for XML Parsing
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        tempVal = "";
        if (qName.equalsIgnoreCase("film")) {
            tempFilmObj = new Film();
        } else if (qName.equalsIgnoreCase("cat")) {
            tempGenreObj = new Genre();
        }
        else if (qName.equalsIgnoreCase("actor")) {
            tempActorObj = new Actor();
        } else if (qName.equalsIgnoreCase("f")) {
            tempFilmObj = new Film();
        }
        else if (qName.equalsIgnoreCase("a")) {
            tempActorObj = new Actor();
        }
        else if (qName.equalsIgnoreCase("filmc")) {
            tempActorListForMovie = new ArrayList<>();
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("fid")) {            //Movie parsing - film id
            tempFilmObj.setId(tempVal);
        }
        else if (qName.equalsIgnoreCase("t")) {         //Movie parsing - title
            tempFilmObj.setTitle(tempVal);
        }
        else if (qName.equalsIgnoreCase("year")) {      //Movie parsing - year released
            try {
                tempFilmObj.setYear(Year.parse(tempVal));
            } catch (Exception e) {
                //continue
            }
        }
        else if (qName.equalsIgnoreCase("dirn")) {      //Movie parsing - director name
            tempFilmObj.setDirector(tempVal);
        }
        else if (qName.equalsIgnoreCase("cat")) {       //Movie parsing - genre name
            var genreName = Utility.getFullGenre(tempVal);
            if(genreName != null) {
                tempGenreObj.setName(genreName);
                if(genreIdMap.containsKey(genreName)) {
                    tempGenreObj.setId(genreIdMap.get(genreName));
                }
                else {
                    ++lastGenreId;
                    genreIdMap.put(genreName, lastGenreId);
                    tempGenreObj.setId(lastGenreId);
                    newGenresList.add(tempGenreObj);
                }
                tempFilmObj.addGenre(tempGenreObj);
            }
        }
        else if (qName.equalsIgnoreCase("film")) {      //Movie parsing - Add to map after error checks
            if (filmMap.containsKey(tempFilmObj.getId())) {
                movieInconsistencyReport.println("DUPLICATE   " + tempFilmObj.toString());
                incMoviesCount++;
            }
            else if (tempFilmObj.hasError()) {
                movieInconsistencyReport.println("NULL ITEMS  " + tempFilmObj.toString());
                incMoviesCount++;
            }
            else if (tempFilmObj.getGenres().isEmpty()) {
                movieInconsistencyReport.println("GENRE ERROR " + tempFilmObj.toString());
                incMoviesCount++;
            }
            else {
                filmMap.put(tempFilmObj.getId(), tempFilmObj);
            }
        }
        else if (qName.equalsIgnoreCase("stagename")) {     //Actor parsing - Full Name
            tempActorObj.setName(tempVal);
        }
        else if (qName.equalsIgnoreCase("dob")) {           //Actor parsing - DOB
            try {
                tempActorObj.setBirthYear(Year.parse(tempVal));
            } catch (Exception e) {
                //continue
            }
        }
        else if (qName.equalsIgnoreCase("actor")) {     //Actor parsing - Add to map after error checks
            if (actorMap.containsKey(tempActorObj.getName())) {
                actorInconsistencyReport.println("DUPLICATE     " + tempActorObj.toString());
                incActorsCount++;
            }
            else if (!tempActorObj.isNameValid()) {
                actorInconsistencyReport.println("INVALID ACTOR " + tempActorObj.toString());
                incActorsCount++;
            }
            else {
                actorMap.put(tempActorObj.getName(), tempActorObj);
            }
        }
        else if (qName.equalsIgnoreCase("f")) {         //Cast parsing - Film ID
            tempFilmObj.setId(tempVal);
        }
        else if (qName.equalsIgnoreCase("a")) {         //Cast parsing - Actor name
            tempActorObj.setName(tempVal);
        }
        else if (qName.equalsIgnoreCase("m")) {         //Cast parsing - Add actors associated to movie
            if (filmMap.containsKey(tempFilmObj.getId()) && actorMap.containsKey(tempActorObj.getName())){
                tempActorListForMovie.add(actorMap.get(tempActorObj.getName()));
            }
            else if (!actorMap.containsKey(tempActorObj.getName())) {
                actorInconsistencyReport.println("ACTOR DNE    " + tempActorObj.toString());
                incActorsCount++;
            }
        }
        else if (qName.equalsIgnoreCase("filmc")) {     //Cast parsing - Add film with actors to map
            if (!filmMap.containsKey(tempFilmObj.getId())) {
                movieInconsistencyReport.println("MOVIE DNE " + tempFilmObj.toString());
                incMoviesCount++;
            }
            stars_in_movies_map.put(filmMap.get(tempFilmObj.getId()), tempActorListForMovie);
        }
    }

    public static void main(String[] args) {
        SAXParserTool spe = new SAXParserTool();
        spe.runParser();
    }

    static class QueryWorker implements Runnable {
        Random random;
        String connection;
        String query;

        QueryWorker(int i) {
            random = new Random(i);
            connection = "New connection " + i;
            System.out.println(connection);
            query = "SELECT * FROM Foo WHERE id = " + i;
        }

        @Override
        public void run() {
            System.out.printf("Executing query: %s%n", query);
            try {
                Thread.sleep(random.nextInt(5000));
            } catch (InterruptedException e) { e.printStackTrace();}
        }
    }


}
