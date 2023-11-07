package parser.src;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Year;
import java.util.*;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import parser.src.dto.Actor;
import parser.src.dto.Film;
import parser.src.dto.Genre;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.DefaultHandler;

public class SAXParserTool extends DefaultHandler {

    private Map<String,Film> filmMap;
    private Map<UUID, Actor> actorMap;
    private Map<Film, List<Actor>> stars_in_movies_map;

    private String tempVal;
    private Film tempFilm;
    private Genre tempGenre;
    private Actor tempActor;
    private List<Actor> tempActorListForMovie;

    private PrintWriter outputWriter;
    private PrintWriter movieInconsistencyReport;
    private PrintWriter actorInconsistencyReport;

    private SAXParserFactory spf;
    private SAXParser sp;

    private int incMoviesCount;
    private int incActorsCount;

    private DataSource dataSource;

    public SAXParserTool() {
        spf = SAXParserFactory.newInstance();
        filmMap = new HashMap<>();
        actorMap = new HashMap<>();
        stars_in_movies_map = new HashMap<>();

        try {
            sp = spf.newSAXParser();
            movieInconsistencyReport = new PrintWriter("src/parser/out/movie_inconsistency.txt", StandardCharsets.UTF_8);
            actorInconsistencyReport = new PrintWriter("src/parser/out/actor_inconsistency.txt", StandardCharsets.UTF_8);
        } catch (ParserConfigurationException | SAXException | IOException e) {
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
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
            Connection conn = dataSource.getConnection();
            //insetFilmsToDb(conn);
            insertActorsToDb(conn);
            //insetStarsInMoviesDb(conn);
        } catch (NamingException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void insetStarsInMoviesDb(Connection c) {

    }

    private void insertActorsToDb(Connection c) {
        int batchSize = 0;
        String actorEntry = "INSERT INTO stars VALUES (?, ?, ?);";

        try (PreparedStatement insertStatement = c.prepareStatement(actorEntry)){
            for (Map.Entry<UUID, Actor> entry : actorMap.entrySet()) {
                UUID actorId = entry.getKey();
                Actor actorValue = entry.getValue();

                if(batchSize == 500) {
                    int[] result = insertStatement.executeBatch();
                    System.out.println(Arrays.toString(result));
                    batchSize = 0;
                    insertStatement.clearBatch();
                }

                insertStatement.setString(1, actorId.toString().substring(0,9));
                insertStatement.setString(2, actorValue.getName());
                insertStatement.setString(3, String.valueOf(actorValue.getBirthYear().getValue()));

                batchSize++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("inserted into actors table");
    }

    private void insetFilmsToDb(Connection c) {
    }

    private void parseMovies() {
        try {
            sp.reset();
            sp.parse("src/parser/stanford-movies/mains243.xml", this);
        } catch (SAXException | IOException se) {
            se.printStackTrace();
        }
        System.out.println("mains243.xml parsed");
    }

    private void parseActors() {
        try {
            sp.reset();
            sp.parse("src/parser/stanford-movies/actors63.xml", this);
        } catch (SAXException | IOException se) {
            se.printStackTrace();
        }
        System.out.println("actors63.xml parsed");
    }

    private void parseMovieCast() {
        try {
            sp.reset();
            sp.parse("src/parser/stanford-movies/casts124.xml", this);
        } catch (SAXException | IOException se) {
            se.printStackTrace();
        }
        System.out.println("casts124.xml parsed");
    }

    /**
     * Iterate through the list and print
     * the contents
     */
    private void printMovieData() {
        try {
            outputWriter = new PrintWriter("src/parser/out/movies.txt", StandardCharsets.UTF_8);

            outputWriter.println("No of Films read '" + filmMap.size() + "'.");
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

    /**
     * Iterate through the list and print
     * the contents
     */
    private void printActorData() {
        try {
            outputWriter = new PrintWriter("src/parser/out/actors.txt", StandardCharsets.UTF_8);

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
            outputWriter = new PrintWriter("src/parser/out/stars_in_movies.txt", StandardCharsets.UTF_8);

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

    //Event Handlers
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        //reset
        tempVal = "";

        if (qName.equalsIgnoreCase("film")) {
            tempFilm = new Film();
        } else if (qName.equalsIgnoreCase("cat")) {
            tempGenre = new Genre();
        }
        else if (qName.equalsIgnoreCase("actor")) {
            tempActor = new Actor();
        } else if (qName.equalsIgnoreCase("f")) {
            tempFilm = new Film();
        }
        else if (qName.equalsIgnoreCase("a")) {
            tempActor = new Actor();
        }
        else if (qName.equalsIgnoreCase("filmc")) {
            tempActorListForMovie = new ArrayList<>();
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal = new String(ch, start, length);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        //Movie parsing
        if (qName.equalsIgnoreCase("fid")) {
            tempFilm.setId(tempVal);
        }
        else if (qName.equalsIgnoreCase("t")) {
            tempFilm.setTitle(tempVal);
        }
        else if (qName.equalsIgnoreCase("year")) {
            try {
                tempFilm.setYear(Year.parse(tempVal));
            } catch (Exception e) {
                //continue
            }
        }
        else if (qName.equalsIgnoreCase("dirn")) {
            tempFilm.setDirector(tempVal);
        }
        else if (qName.equalsIgnoreCase("cat")) {
            tempGenre.setName(tempVal);
            tempFilm.addGenre(tempGenre);
        }
        else if (qName.equalsIgnoreCase("film")) {      //Movie enter
            if (filmMap.containsKey(tempFilm.getId())) {
                movieInconsistencyReport.println("DUPLICATE   " + tempFilm.toString());
                incMoviesCount++;
            }
            else if (tempFilm.hasError()) {
                movieInconsistencyReport.println("NULL ITEMS  " + tempFilm.toString());
                incMoviesCount++;
            }
            else if (tempFilm.getGenres().isEmpty()) {
                movieInconsistencyReport.println("GENRE ERROR " + tempFilm.toString());
                incMoviesCount++;
            }
            else {
                filmMap.put(tempFilm.getId(), tempFilm);
            }
        }   // Actor parsing
        else if (qName.equalsIgnoreCase("stagename")) {
            tempActor.setName(tempVal);
        }
        else if (qName.equalsIgnoreCase("dob")) {
            try {
                tempActor.setBirthYear(Year.parse(tempVal));
            } catch (Exception e) {
                //continue
            }
        }
        else if (qName.equalsIgnoreCase("actor")) {     //actor adding to list
            if (getActorByName(tempActor.getName()) != null) {
                actorInconsistencyReport.println("DUPLICATE     " + tempActor.toString());
                incActorsCount++;
            }
            else if (!tempActor.isNameValid()) {
                actorInconsistencyReport.println("INVALID ACTOR " + tempActor.toString());
                incActorsCount++;
            }
            else {
                actorMap.put(tempActor.getId(), tempActor);
            }
        }
        else if (qName.equalsIgnoreCase("f")) {
            tempFilm.setId(tempVal);
        }
        else if (qName.equalsIgnoreCase("a")) {
            tempActor.setName(tempVal);
        }
        else if (qName.equalsIgnoreCase("m")) {
            if (filmMap.containsKey(tempFilm.getId()) && getActorByName(tempActor.getName()) != null){
                var actor = getActorByName(tempActor.getName());
                tempActorListForMovie.add(actor);
            }
            else if (getActorByName(tempActor.getName()) == null) {
                actorInconsistencyReport.println("ACTOR DNE    " + tempActor.toString());
                incActorsCount++;
            }
        }
        else if (qName.equalsIgnoreCase("filmc")) {
            if (!filmMap.containsKey(tempFilm.getId())) {
                movieInconsistencyReport.println("MOVIE DNE " + tempFilm.toString());
                incMoviesCount++;
            }
            stars_in_movies_map.put(filmMap.get(tempFilm.getId()), tempActorListForMovie);
        }
    }

    public Actor getActorByName(String name) {
        for (Map.Entry<UUID, Actor> entry : actorMap.entrySet()) {
            if (entry.getValue().getName().equals(name)) {
                return entry.getValue();
            }
        }
        return null; // Return null if the value is not found
    }

    public static void main(String[] args) {
        SAXParserTool spe = new SAXParserTool();
        spe.runParser();
    }

}
