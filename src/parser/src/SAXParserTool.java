package parser.src;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.Year;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

//    List<DirectorFilms> directorFilms;
    private List<Film> filmsList;
    private List<Actor> actorList;
    private Map<Film, List<Actor>> stars_in_movies_map;


    private String tempVal;

    //to maintain context
    private Film tempFilm;
    private Genre tempGenre;
    private Actor tempActor;

    private PrintWriter outputWriter;
    private PrintWriter movieInconsistencyReport;
    private PrintWriter actorInconsistencyReport;

    private SAXParserFactory spf;
    private SAXParser sp;

    private int incMoviesCount;
    private int incActorsCount;

    public SAXParserTool() {
        spf = SAXParserFactory.newInstance();
        filmsList = new ArrayList<>();
        actorList = new ArrayList<>();
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
        movieInconsistencyReport.close();
        actorInconsistencyReport.close();
        System.out.println("Inconsistent movies = " + incMoviesCount);
        System.out.println("Inconsistent actors = " + incActorsCount);

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

            outputWriter.println("No of Films read '" + filmsList.size() + "'.");
            System.out.println("Movies stored = " + filmsList.size());
            for (Film film : filmsList) {
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

            outputWriter.println("No of Actors read '" + actorList.size() + "'.");
            System.out.println("Actors stored = " + actorList.size());


            for (Actor actor : actorList) {
                outputWriter.println(actor.toString());
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
            if (filmsList.contains(tempFilm)) {
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
                filmsList.add(tempFilm);
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
            if (actorList.contains(tempActor)) {
                actorInconsistencyReport.println("DUPLICATE     " + tempActor.toString());
                incActorsCount++;
            }
            else if (!tempActor.isNameValid()) {
                actorInconsistencyReport.println("INVALID ACTOR " + tempActor.toString());
                incActorsCount++;
            }
            else {
                actorList.add(tempActor);
            }
        }
        else if (qName.equalsIgnoreCase("f")) {
            tempFilm.setId(tempVal);
        }
        else if (qName.equalsIgnoreCase("a")) {
            tempActor.setName(tempVal);
        }
        else if (qName.equalsIgnoreCase("m")) {
            if (filmsList.contains(tempFilm) && actorList.contains(tempActor)) {
//                stars_in_movies_map.put()
            }
        }
    }


    public static void main(String[] args) {
        SAXParserTool spe = new SAXParserTool();
        spe.runParser();
    }

}
